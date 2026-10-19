package org.cocoproject.homepage.metrics;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import jakarta.servlet.http.Cookie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@TestExecutionListeners(
        listeners = DependencyInjectionTestExecutionListener.class,
        inheritListeners = false,
        mergeMode = TestExecutionListeners.MergeMode.REPLACE_DEFAULTS)
class MetricsWebIntegrationTest {
    private static final String RELEASE_VERSION = "v0.2.0";
    private static final String RELEASE_FILE = "coco-test-release.tar.gz";
    private static final String DOWNLOAD_PATH = "/downloads/" + RELEASE_FILE;
    private static final String MISSING_VERSION = "v0.1.9";
    private static final String MISSING_FILE = "missing-release.tar.gz";
    private static final String UNREGISTERED_FILE = "unregistered-release.tar.gz";
    private static final String ADMIN_TOKEN = "test-metrics-admin-token";
    private static final ZoneId METRICS_ZONE = ZoneId.of("Asia/Shanghai");
    private static final byte[] RELEASE_CONTENT = createReleaseContent();
    private static final byte[] UNREGISTERED_CONTENT = "unregistered test artifact"
            .getBytes(StandardCharsets.UTF_8);
    private static final List<String> EMODELG_FILES = List.of(
            "emodelg-so3-2.2.0-20261011.tar.gz",
            "emodelg-models-2.2.0-20261011.tar.gz");
    private static final Path TEST_ROOT = createTestFixtures();
    private static final Path DOWNLOAD_DIRECTORY = TEST_ROOT.resolve("downloads");
    private static final Path DATABASE_PATH = TEST_ROOT.resolve("coco-metrics.db");

    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void configureTestApplication(DynamicPropertyRegistry registry) {
        registry.add("coco.download.directory", DOWNLOAD_DIRECTORY::toString);
        registry.add("coco.metrics.database", DATABASE_PATH::toString);
        registry.add("coco.metrics.admin-token", () -> ADMIN_TOKEN);
        registry.add("coco.releases[0].version", () -> RELEASE_VERSION);
        registry.add("coco.releases[0].file-name", () -> RELEASE_FILE);
        registry.add("coco.releases[1].version", () -> MISSING_VERSION);
        registry.add("coco.releases[1].file-name", () -> MISSING_FILE);
    }

    @BeforeEach
    void resetMetrics() throws SQLException {
        try (Connection connection = openConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM release_download_daily");
            statement.executeUpdate("DELETE FROM download_dedupe");
            statement.executeUpdate("DELETE FROM daily_unique_dedupe");
            statement.executeUpdate("DELETE FROM daily_unique_counts");
            statement.executeUpdate("UPDATE release_download_totals SET download_count = 0");
        }
    }

    @Test
    void normalDownloadReturnsOriginalContentAndRecordsOneDownload() throws Exception {
        MvcResult result = mockMvc.perform(get(DOWNLOAD_PATH))
                .andExpect(status().isOk())
                .andExpect(content().bytes(RELEASE_CONTENT))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, allOf(
                        startsWith("coco_vid="),
                        containsString("Path=/"),
                        containsString("Max-Age=31536000"),
                        containsString("HttpOnly"),
                        containsString("SameSite=Lax"))))
                .andReturn();

        assertNotNull(visitorCookie(result));
        assertEquals(1, downloadCount(RELEASE_VERSION));
    }

    @Test
    void sameCookieIsDeduplicatedWithinThirtySeconds() throws Exception {
        Cookie visitor = visitorCookie("download-dedupe-visitor-0001");

        mockMvc.perform(get(DOWNLOAD_PATH).cookie(visitor))
                .andExpect(status().isOk());
        mockMvc.perform(get(DOWNLOAD_PATH).cookie(visitor))
                .andExpect(status().isOk());

        assertEquals(1, downloadCount(RELEASE_VERSION));
    }

    @Test
    void concurrentDownloadsWithSameCookieCountOnce() throws Exception {
        Cookie visitor = visitorCookie("concurrent-download-visitor-0001");
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);

        try {
            List<Future<Integer>> responses = List.of(
                    executor.submit(() -> concurrentDownload(visitor, ready, start)),
                    executor.submit(() -> concurrentDownload(visitor, ready, start)));

            assertTrue(ready.await(5, TimeUnit.SECONDS), "Concurrent requests did not become ready");
            start.countDown();
            for (Future<Integer> response : responses) {
                assertEquals(200, response.get(10, TimeUnit.SECONDS));
            }
        } finally {
            start.countDown();
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
        }

        assertEquals(1, downloadCount(RELEASE_VERSION));
    }

    @Test
    void initialByteRangesAreCounted() throws Exception {
        mockMvc.perform(get(DOWNLOAD_PATH)
                        .header(HttpHeaders.RANGE, "bytes=0-")
                        .cookie(visitorCookie("initial-open-range-visitor-0001")))
                .andExpect(status().isPartialContent())
                .andExpect(content().bytes(RELEASE_CONTENT));

        mockMvc.perform(get(DOWNLOAD_PATH)
                        .header(HttpHeaders.RANGE, "bytes=0-99")
                        .cookie(visitorCookie("initial-fixed-range-visitor-0002")))
                .andExpect(status().isPartialContent())
                .andExpect(content().bytes(firstBytes(RELEASE_CONTENT, 100)));

        assertEquals(2, downloadCount(RELEASE_VERSION));
    }

    @Test
    void headNonzeroRangeAndMultiRangeRequestsAreNotCounted() throws Exception {
        mockMvc.perform(request(HttpMethod.HEAD, DOWNLOAD_PATH)
                        .cookie(visitorCookie("head-request-visitor-0001")))
                .andExpect(status().isOk());

        mockMvc.perform(get(DOWNLOAD_PATH)
                        .header(HttpHeaders.RANGE, "bytes=100-")
                        .cookie(visitorCookie("nonzero-range-visitor-0002")))
                .andExpect(status().isPartialContent());

        mockMvc.perform(get(DOWNLOAD_PATH)
                        .header(HttpHeaders.RANGE, "bytes=0-9,20-29")
                        .cookie(visitorCookie("multi-range-visitor-0003")))
                .andExpect(status().isPartialContent());

        assertEquals(0, downloadCount(RELEASE_VERSION));
    }

    @Test
    void unregisteredAndMissingArtifactsAreNotCounted() throws Exception {
        mockMvc.perform(get("/downloads/" + UNREGISTERED_FILE)
                        .cookie(visitorCookie("unregistered-file-visitor-0001")))
                .andExpect(status().isOk())
                .andExpect(content().bytes(UNREGISTERED_CONTENT));

        mockMvc.perform(get("/downloads/" + MISSING_FILE)
                        .cookie(visitorCookie("missing-file-visitor-0002")))
                .andExpect(status().isNotFound());

        assertEquals(0, downloadCount(RELEASE_VERSION));
        assertEquals(0, downloadCount(MISSING_VERSION));
    }

    @Test
    void emodelgBundlesSupportDownloadHeadAndRangesThroughBothPaths() throws Exception {
        for (String fileName : EMODELG_FILES) {
            byte[] expected = Files.readAllBytes(DOWNLOAD_DIRECTORY.resolve("emodelg").resolve(fileName));
            for (String prefix : List.of("/emodelg/", "/downloads/emodelg/")) {
                String path = prefix + fileName;
                mockMvc.perform(get(path))
                        .andExpect(status().isOk())
                        .andExpect(content().bytes(expected))
                        .andExpect(header().doesNotExist(HttpHeaders.SET_COOKIE));

                mockMvc.perform(request(HttpMethod.HEAD, path))
                        .andExpect(status().isOk())
                        .andExpect(header().longValue(HttpHeaders.CONTENT_LENGTH, expected.length))
                        .andExpect(header().string(HttpHeaders.ACCEPT_RANGES, "bytes"))
                        .andExpect(content().bytes(new byte[0]));

                mockMvc.perform(get(path).header(HttpHeaders.RANGE, "bytes=8-15"))
                        .andExpect(status().isPartialContent())
                        .andExpect(header().string(HttpHeaders.CONTENT_RANGE, "bytes 8-15/" + expected.length))
                        .andExpect(content().bytes(Arrays.copyOfRange(expected, 8, 16)));
            }
        }
        mockMvc.perform(get("/api/stats/releases/" + RELEASE_VERSION))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.allReleaseDownloads").value(0));
    }

    @Test
    void emodelgAliasRejectsMissingFilesAndPathsOutsideItsDirectory() throws Exception {
        for (String path : List.of(
                "/emodelg/missing.tar.gz",
                "/downloads/emodelg/missing.tar.gz",
                "/emodelg/" + RELEASE_FILE,
                "/emodelg/../" + RELEASE_FILE,
                "/emodelg/%2e%2e/" + RELEASE_FILE,
                "/emodelg/%2e%2e%2f" + RELEASE_FILE)) {
            mockMvc.perform(get(URI.create(path)))
                    .andExpect(status().isNotFound());
        }
        assertEquals(0, downloadCount(RELEASE_VERSION));
    }

    @Test
    void allDocumentedViewsAreAccepted() throws Exception {
        Cookie visitor = visitorCookie("all-views-visitor-0001");
        LocalDate today = LocalDate.now(METRICS_ZONE);
        for (String view : List.of(
                "home",
                "cryosieve",
                "cryopros",
                "ctfa",
                "changelog",
                "changelogs",
                "changelog-v0-2-0",
                "install",
                "install-v0-3-2",
                "install-v0-2-0",
                "install-v0-1-0")) {
            mockMvc.perform(post("/api/metrics/views")
                            .cookie(visitor)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"view\":\"" + view + "\"}"))
                    .andExpect(status().isNoContent());
            assertEquals(1, uniqueViewCount(today, view));
        }
    }

    @Test
    void viewRequestIsDeduplicatedForSameCookieAndDay() throws Exception {
        MvcResult firstRequest = mockMvc.perform(post("/api/metrics/views")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"view\":\"home\"}"))
                .andExpect(status().isNoContent())
                .andReturn();
        Cookie visitor = visitorCookie(firstRequest);

        mockMvc.perform(post("/api/metrics/views")
                        .cookie(visitor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"view\":\"home\"}"))
                .andExpect(status().isNoContent());

        LocalDate today = LocalDate.now(METRICS_ZONE);
        assertEquals(1, uniqueViewCount(today, "home"));
    }

    @Test
    void invalidViewIsRejected() throws Exception {
        mockMvc.perform(post("/api/metrics/views")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"view\":\"unknown\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void visitorCookieHasLongLivedPrivateAttributesAndSecureFollowsRequest() throws Exception {
        mockMvc.perform(post("/api/metrics/views")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"view\":\"home\"}"))
                .andExpect(status().isNoContent())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, allOf(
                        startsWith("coco_vid="),
                        containsString("Path=/"),
                        containsString("Max-Age=31536000"),
                        containsString("HttpOnly"),
                        containsString("SameSite=Lax"),
                        not(containsString("; Secure")))));

        mockMvc.perform(post("/api/metrics/views")
                        .secure(true)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"view\":\"install\"}"))
                .andExpect(status().isNoContent())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, allOf(
                        startsWith("coco_vid="),
                        containsString("Max-Age=31536000"),
                        containsString("HttpOnly"),
                        containsString("SameSite=Lax"),
                        containsString("Secure"))));
    }

    @Test
    void publicReleaseStatsExposeAggregateFieldsAndSixtySecondCache() throws Exception {
        mockMvc.perform(get(DOWNLOAD_PATH)
                        .cookie(visitorCookie("public-stats-download-visitor-0001")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/stats/releases/{version}", RELEASE_VERSION))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CACHE_CONTROL, allOf(
                        containsString("max-age=60"),
                        containsString("public"))))
                .andExpect(jsonPath("$.releaseVersion").value(RELEASE_VERSION))
                .andExpect(jsonPath("$.releaseDownloads").value(1))
                .andExpect(jsonPath("$.allReleaseDownloads").value(1));

        mockMvc.perform(get("/api/stats/releases/{version}", "v-does-not-exist"))
                .andExpect(status().isNotFound());
    }

    @Test
    void internalMetricsRequireTokenAndSupportDefaultAndExplicitDates() throws Exception {
        mockMvc.perform(get("/api/internal/metrics")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer wrong-token"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/internal/metrics")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken()))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CACHE_CONTROL, containsString("no-store")))
                .andExpect(jsonPath("$.from").isString())
                .andExpect(jsonPath("$.to").isString())
                .andExpect(jsonPath("$.dailyUniqueVisitors").isArray())
                .andExpect(jsonPath("$.dailyDownloads").isArray())
                .andExpect(jsonPath("$.releaseTotals", hasSize(2)))
                .andExpect(jsonPath("$.allReleaseDownloads").value(0));

        mockMvc.perform(get("/api/internal/metrics")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .queryParam("from", "2026-07-01")
                        .queryParam("to", "2026-07-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.from").value("2026-07-01"))
                .andExpect(jsonPath("$.to").value("2026-07-31"));
    }

    @Test
    void internalMetricsRejectRangesLongerThanThreeHundredSixtySixDays() throws Exception {
        mockMvc.perform(get("/api/internal/metrics")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .queryParam("from", "2024-01-01")
                        .queryParam("to", "2025-01-01"))
                .andExpect(status().isBadRequest());
    }

    private int concurrentDownload(
            Cookie visitor,
            CountDownLatch ready,
            CountDownLatch start) throws Exception {
        ready.countDown();
        assertTrue(start.await(5, TimeUnit.SECONDS), "Concurrent requests were not released");
        return mockMvc.perform(get(DOWNLOAD_PATH).cookie(visitor))
                .andReturn()
                .getResponse()
                .getStatus();
    }

    private static Cookie visitorCookie(MvcResult result) {
        String setCookie = result.getResponse().getHeader(HttpHeaders.SET_COOKIE);
        assertNotNull(setCookie, "Expected the response to set a visitor cookie");
        int valueStart = setCookie.indexOf('=') + 1;
        int valueEnd = setCookie.indexOf(';', valueStart);
        String value = setCookie.substring(valueStart, valueEnd < 0 ? setCookie.length() : valueEnd);
        return visitorCookie(value);
    }

    private static Cookie visitorCookie(String value) {
        return new Cookie("coco_vid", value);
    }

    private static String bearerToken() {
        return "Bearer " + ADMIN_TOKEN;
    }

    private long downloadCount(String version) throws SQLException {
        try (Connection connection = openConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT download_count FROM release_download_totals WHERE release_version = ?")) {
            statement.setString(1, version);
            try (ResultSet resultSet = statement.executeQuery()) {
                assertTrue(resultSet.next(), "Expected seeded release metrics for " + version);
                return resultSet.getLong(1);
            }
        }
    }

    private long uniqueViewCount(LocalDate date, String view) throws SQLException {
        try (Connection connection = openConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT unique_count FROM daily_unique_counts WHERE metric_date = ? AND view_name = ?")) {
            statement.setString(1, date.toString());
            statement.setString(2, view);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getLong(1) : 0;
            }
        }
    }

    private static Connection openConnection() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_PATH);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA busy_timeout=5000");
            statement.execute("PRAGMA foreign_keys=ON");
        }
        return connection;
    }

    private static Path createTestFixtures() {
        try {
            Path root = Files.createTempDirectory("coco-metrics-web-test-");
            Path downloads = Files.createDirectories(root.resolve("downloads"));
            Files.write(downloads.resolve(RELEASE_FILE), RELEASE_CONTENT);
            Files.write(downloads.resolve(UNREGISTERED_FILE), UNREGISTERED_CONTENT);
            Path emodelg = Files.createDirectories(downloads.resolve("emodelg"));
            for (String fileName : EMODELG_FILES) {
                Files.write(emodelg.resolve(fileName), ("test bundle: " + fileName).getBytes(StandardCharsets.UTF_8));
            }
            return root;
        } catch (IOException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static byte[] createReleaseContent() {
        byte[] content = new byte[256];
        for (int index = 0; index < content.length; index++) {
            content[index] = (byte) index;
        }
        return content;
    }

    private static byte[] firstBytes(byte[] source, int length) {
        byte[] result = new byte[length];
        System.arraycopy(source, 0, result, 0, length);
        return result;
    }
}
