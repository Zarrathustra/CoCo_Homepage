package org.cocoproject.homepage.metrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.cocoproject.homepage.metrics.MetricsDatabase.InternalMetricsReport;
import org.cocoproject.homepage.metrics.ReleaseCatalog.ReleaseInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MetricsDatabaseTest {
    @TempDir
    Path tempDirectory;

    @Test
    void initializesSchemaAndPersistsReleaseCountsAcrossRestarts() throws Exception {
        Path databasePath = tempDirectory.resolve("nested/coco-metrics.db");
        TestDatabase first = database(databasePath, "v0.2.0", "coco-0.2.6.tar.gz",
                "v0.3.0", "coco-0.3.0.tar.gz");
        Instant now = Instant.parse("2026-07-29T02:00:00Z");

        assertThat(Files.isRegularFile(databasePath)).isTrue();
        assertThat(pragma(databasePath, "user_version")).isEqualTo("1");
        assertThat(pragma(databasePath, "journal_mode")).isEqualToIgnoringCase("wal");
        assertThat(first.metrics.releaseStatistics("v0.2.0").releaseDownloads()).isZero();

        assertThat(first.metrics.recordDownload(
                "visitor-a",
                first.catalog.findByVersion("v0.2.0").orElseThrow(),
                now,
                LocalDate.parse("2026-07-29"),
                Duration.ofSeconds(30))).isTrue();
        assertThat(first.metrics.recordDownload(
                "visitor-a",
                first.catalog.findByVersion("v0.2.0").orElseThrow(),
                now.plusSeconds(29),
                LocalDate.parse("2026-07-29"),
                Duration.ofSeconds(30))).isFalse();
        assertThat(first.metrics.recordDownload(
                "visitor-a",
                first.catalog.findByVersion("v0.2.0").orElseThrow(),
                now.plusSeconds(30),
                LocalDate.parse("2026-07-29"),
                Duration.ofSeconds(30))).isTrue();

        TestDatabase restarted = database(databasePath, "v0.2.0", "coco-0.2.6.tar.gz",
                "v0.3.0", "coco-0.3.0.tar.gz");
        assertThat(restarted.metrics.releaseStatistics("v0.2.0").releaseDownloads()).isEqualTo(2);

        ReleaseInfo secondRelease = restarted.catalog.findByVersion("v0.3.0").orElseThrow();
        assertThat(restarted.metrics.recordDownload(
                "visitor-b",
                secondRelease,
                now,
                LocalDate.parse("2026-07-29"),
                Duration.ofSeconds(30))).isTrue();
        assertThat(restarted.metrics.releaseStatistics("v0.3.0"))
                .satisfies(statistics -> {
                    assertThat(statistics.releaseDownloads()).isEqualTo(1);
                    assertThat(statistics.allReleaseDownloads()).isEqualTo(3);
                });
    }

    @Test
    void countsUniqueVisitorsPerViewAndDate() {
        TestDatabase testDatabase = database(
                tempDirectory.resolve("views.db"),
                "v0.2.0", "coco-0.2.6.tar.gz");
        Instant firstDay = Instant.parse("2026-07-29T02:00:00Z");
        LocalDate july29 = LocalDate.parse("2026-07-29");
        LocalDate july30 = LocalDate.parse("2026-07-30");

        assertThat(testDatabase.metrics.recordUniqueView("visitor-a", "home", firstDay, july29)).isTrue();
        assertThat(testDatabase.metrics.recordUniqueView("visitor-a", "home", firstDay, july29)).isFalse();
        assertThat(testDatabase.metrics.recordUniqueView("visitor-a", "install", firstDay, july29)).isTrue();
        assertThat(testDatabase.metrics.recordUniqueView("visitor-b", "home", firstDay, july29)).isTrue();
        assertThat(testDatabase.metrics.recordUniqueView(
                "visitor-a", "home", firstDay.plus(Duration.ofDays(1)), july30)).isTrue();

        InternalMetricsReport report = testDatabase.metrics.internalReport(july29, july30);
        assertThat(report.dailyUniqueVisitors())
                .extracting(metric -> metric.date() + ":" + metric.view() + ":" + metric.uniqueVisitors())
                .containsExactly(
                        "2026-07-29:home:2",
                        "2026-07-29:install:1",
                        "2026-07-30:home:1");
    }

    @Test
    void cleanupRemovesOnlyExpiredDeduplicationRows() throws Exception {
        Path databasePath = tempDirectory.resolve("cleanup.db");
        TestDatabase testDatabase = database(databasePath, "v0.2.0", "coco-0.2.6.tar.gz");
        ReleaseInfo release = testDatabase.catalog.findByVersion("v0.2.0").orElseThrow();
        Instant now = Instant.parse("2026-07-29T12:00:00Z");

        testDatabase.metrics.recordDownload(
                "old-download", release, now.minus(Duration.ofHours(25)),
                LocalDate.parse("2026-07-28"), Duration.ofSeconds(30));
        testDatabase.metrics.recordDownload(
                "new-download", release, now,
                LocalDate.parse("2026-07-29"), Duration.ofSeconds(30));
        testDatabase.metrics.recordUniqueView(
                "old-view", "home", now.minus(Duration.ofHours(49)), LocalDate.parse("2026-07-27"));
        testDatabase.metrics.recordUniqueView(
                "new-view", "home", now, LocalDate.parse("2026-07-29"));

        testDatabase.metrics.cleanup(now, Duration.ofHours(24), Duration.ofHours(48));

        assertThat(rowCount(databasePath, "download_dedupe")).isEqualTo(1);
        assertThat(rowCount(databasePath, "daily_unique_dedupe")).isEqualTo(1);
        assertThat(testDatabase.metrics.releaseStatistics("v0.2.0").releaseDownloads()).isEqualTo(2);
        assertThat(testDatabase.metrics.internalReport(
                LocalDate.parse("2026-07-27"), LocalDate.parse("2026-07-29")).dailyUniqueVisitors())
                .hasSize(2);
    }

    @Test
    void concurrentDuplicateDownloadClaimsIncrementOnlyOnce() throws Exception {
        TestDatabase testDatabase = database(
                tempDirectory.resolve("concurrent.db"),
                "v0.2.0", "coco-0.2.6.tar.gz");
        ReleaseInfo release = testDatabase.catalog.findByVersion("v0.2.0").orElseThrow();
        Instant now = Instant.parse("2026-07-29T12:00:00Z");
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(6);
        try {
            List<Callable<Boolean>> tasks = new ArrayList<>();
            for (int index = 0; index < 6; index++) {
                tasks.add(() -> {
                    start.await();
                    return testDatabase.metrics.recordDownload(
                            "same-visitor",
                            release,
                            now,
                            LocalDate.parse("2026-07-29"),
                            Duration.ofSeconds(30));
                });
            }
            List<Future<Boolean>> futures = tasks.stream().map(executor::submit).toList();
            start.countDown();

            assertThat(futures)
                    .extracting(future -> future.get())
                    .containsExactlyInAnyOrder(true, false, false, false, false, false);
            assertThat(testDatabase.metrics.releaseStatistics("v0.2.0").releaseDownloads()).isEqualTo(1);
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void refusesDatabaseCreatedByANewerSchema() throws Exception {
        Path databasePath = tempDirectory.resolve("future.db");
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
                Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA user_version=2");
        }

        MetricsProperties properties = properties(databasePath, "v0.2.0", "coco-0.2.6.tar.gz");
        MetricsDatabase database = new MetricsDatabase(properties, new ReleaseCatalog(properties));
        assertThatThrownBy(database::initialize)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Unable to initialize");
    }

    @Test
    void failsInitializationWhenDatabaseParentCannotBeCreated() throws Exception {
        Path blockingFile = tempDirectory.resolve("not-a-directory");
        Files.writeString(blockingFile, "blocking file");
        Path databasePath = blockingFile.resolve("metrics.db");
        MetricsProperties properties = properties(databasePath, "v0.2.0", "coco-0.2.6.tar.gz");
        MetricsDatabase database = new MetricsDatabase(properties, new ReleaseCatalog(properties));

        assertThatThrownBy(database::initialize)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(databasePath.toString());
    }

    private TestDatabase database(Path path, String... releases) {
        MetricsProperties properties = properties(path, releases);
        ReleaseCatalog catalog = new ReleaseCatalog(properties);
        MetricsDatabase metrics = new MetricsDatabase(properties, catalog);
        metrics.initialize();
        return new TestDatabase(metrics, catalog);
    }

    private static MetricsProperties properties(Path path, String... releases) {
        MetricsProperties properties = new MetricsProperties();
        properties.getMetrics().setDatabase(path);
        List<MetricsProperties.Release> configured = new ArrayList<>();
        for (int index = 0; index < releases.length; index += 2) {
            MetricsProperties.Release release = new MetricsProperties.Release();
            release.setVersion(releases[index]);
            release.setFileName(releases[index + 1]);
            configured.add(release);
        }
        properties.setReleases(configured);
        return properties;
    }

    private static String pragma(Path path, String pragma) throws Exception {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + path);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("PRAGMA " + pragma)) {
            resultSet.next();
            return resultSet.getString(1);
        }
    }

    private static long rowCount(Path path, String table) throws Exception {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + path);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM " + table)) {
            resultSet.next();
            return resultSet.getLong(1);
        }
    }

    private record TestDatabase(MetricsDatabase metrics, ReleaseCatalog catalog) {
    }
}
