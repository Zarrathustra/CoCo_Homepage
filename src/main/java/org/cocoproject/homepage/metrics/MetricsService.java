package org.cocoproject.homepage.metrics;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Set;

import org.cocoproject.homepage.metrics.MetricsDatabase.InternalMetricsReport;
import org.cocoproject.homepage.metrics.MetricsDatabase.ReleaseStatistics;
import org.cocoproject.homepage.metrics.ReleaseCatalog.ReleaseInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsService.class);
    private static final Set<String> TRACKED_VIEWS = Set.of(
            "home",
            "cryosieve",
            "cryopros",
            "ctfa",
            "changelog",
            "install",
            "install-v0-1-0");

    private final MetricsDatabase database;
    private final MetricsProperties.Metrics properties;
    private final Clock clock;
    private final ZoneId zoneId;

    public MetricsService(
            MetricsDatabase database,
            MetricsProperties properties,
            Clock metricsClock) {
        this.database = database;
        this.properties = properties.getMetrics();
        this.clock = metricsClock;
        this.zoneId = ZoneId.of(this.properties.getTimeZone());
    }

    public boolean isTrackedView(String view) {
        return view != null && TRACKED_VIEWS.contains(view);
    }

    public void recordView(String view, String visitorHash) {
        Instant now = clock.instant();
        try {
            database.recordUniqueView(
                    visitorHash,
                    view,
                    now,
                    LocalDate.ofInstant(now, zoneId));
        } catch (MetricsStorageException exception) {
            LOGGER.error("Unable to record view metric for {}", view, exception);
        }
    }

    public void recordDownload(ReleaseInfo release, String visitorHash) {
        Instant now = clock.instant();
        try {
            database.recordDownload(
                    visitorHash,
                    release,
                    now,
                    LocalDate.ofInstant(now, zoneId),
                    properties.getDownloadDedupeWindow());
        } catch (MetricsStorageException exception) {
            LOGGER.error("Unable to record download metric for {}", release.version(), exception);
        }
    }

    public ReleaseStatistics releaseStatistics(String releaseVersion) {
        return database.releaseStatistics(releaseVersion);
    }

    public InternalMetricsReport internalReport(LocalDate from, LocalDate to) {
        return database.internalReport(from, to);
    }

    @Scheduled(fixedDelayString = "#{@metricsProperties.metrics.cleanupInterval.toMillis()}")
    public void cleanUpDeduplicationData() {
        try {
            database.cleanup(
                    clock.instant(),
                    properties.getDownloadDedupeRetention(),
                    properties.getViewDedupeRetention());
        } catch (MetricsStorageException exception) {
            LOGGER.error("Unable to clean up metrics deduplication data", exception);
        }
    }
}
