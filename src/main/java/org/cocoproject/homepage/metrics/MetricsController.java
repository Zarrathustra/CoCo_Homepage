package org.cocoproject.homepage.metrics;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.cocoproject.homepage.metrics.MetricsDatabase.InternalMetricsReport;
import org.cocoproject.homepage.metrics.MetricsDatabase.ReleaseStatistics;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class MetricsController {
    private static final long MAX_REPORT_DAYS = 366;

    private final MetricsService metricsService;
    private final ReleaseCatalog releaseCatalog;
    private final VisitorIdentityService visitorIdentityService;
    private final MetricsProperties.Metrics properties;
    private final Clock clock;

    public MetricsController(
            MetricsService metricsService,
            ReleaseCatalog releaseCatalog,
            VisitorIdentityService visitorIdentityService,
            MetricsProperties properties,
            Clock metricsClock) {
        this.metricsService = metricsService;
        this.releaseCatalog = releaseCatalog;
        this.visitorIdentityService = visitorIdentityService;
        this.properties = properties.getMetrics();
        this.clock = metricsClock;
    }

    @PostMapping("/metrics/views")
    public ResponseEntity<Void> recordView(
            @RequestBody ViewMetricRequest metric,
            HttpServletRequest request,
            HttpServletResponse response) {
        if (metric == null || !metricsService.isTrackedView(metric.view())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown CoCo page view");
        }

        String visitorHash = visitorIdentityService.visitorHash(request, response);
        metricsService.recordView(metric.view(), visitorHash);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats/releases/{version}")
    public ResponseEntity<ReleaseStatsResponse> releaseStats(@PathVariable String version) {
        if (releaseCatalog.findByVersion(version).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown CoCo release");
        }

        ReleaseStatistics statistics = metricsService.releaseStatistics(version);
        ReleaseStatsResponse response = new ReleaseStatsResponse(
                version,
                statistics.releaseDownloads(),
                statistics.allReleaseDownloads());
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(properties.getPublicCacheDuration()).cachePublic())
                .body(response);
    }

    @GetMapping("/internal/metrics")
    public ResponseEntity<InternalMetricsReport> internalMetrics(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        authorize(authorization);

        LocalDate toDate = parseDate(to, LocalDate.now(clock), "to");
        LocalDate fromDate = parseDate(from, toDate.minusDays(29), "from");
        long reportDays = ChronoUnit.DAYS.between(fromDate, toDate) + 1;
        if (reportDays < 1 || reportDays > MAX_REPORT_DAYS) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Metrics date range must contain between 1 and 366 days");
        }

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(metricsService.internalReport(fromDate, toDate));
    }

    private void authorize(String authorization) {
        String adminToken = properties.getAdminToken();
        if (!StringUtils.hasText(adminToken)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        String expected = "Bearer " + adminToken;
        byte[] expectedBytes = expected.getBytes(StandardCharsets.UTF_8);
        byte[] actualBytes = authorization == null
                ? new byte[0]
                : authorization.getBytes(StandardCharsets.UTF_8);
        if (!MessageDigest.isEqual(expectedBytes, actualBytes)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    private static LocalDate parseDate(String value, LocalDate defaultValue, String parameterName) {
        if (!StringUtils.hasText(value)) {
            return defaultValue;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid " + parameterName + " date",
                    exception);
        }
    }

    public record ViewMetricRequest(String view) {
    }

    public record ReleaseStatsResponse(
            String releaseVersion,
            long releaseDownloads,
            long allReleaseDownloads) {
    }
}
