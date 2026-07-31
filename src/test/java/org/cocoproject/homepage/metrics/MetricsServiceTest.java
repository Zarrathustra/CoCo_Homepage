package org.cocoproject.homepage.metrics;

import static org.assertj.core.api.Assertions.assertThatCode;

import java.sql.SQLException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.cocoproject.homepage.metrics.ReleaseCatalog.ReleaseInfo;
import org.junit.jupiter.api.Test;

class MetricsServiceTest {
    @Test
    void runtimeWriteFailuresDoNotEscapeViewOrDownloadRecording() {
        MetricsProperties properties = new MetricsProperties();
        MetricsProperties.Release configuredRelease = new MetricsProperties.Release();
        configuredRelease.setVersion("v0.2.0");
        configuredRelease.setFileName("coco-0.2.0.tar.gz");
        properties.setReleases(List.of(configuredRelease));
        MetricsDatabase database = new FailingMetricsDatabase(
                properties,
                new ReleaseCatalog(properties));
        Clock clock = Clock.fixed(
                Instant.parse("2026-07-29T02:00:00Z"),
                ZoneId.of("Asia/Shanghai"));
        MetricsService service = new MetricsService(database, properties, clock);

        assertThatCode(() -> service.recordView("home", "visitor-hash"))
                .doesNotThrowAnyException();
        assertThatCode(() -> service.recordDownload(
                new ReleaseInfo("v0.2.0", "coco-0.2.0.tar.gz"),
                "visitor-hash"))
                .doesNotThrowAnyException();
    }

    private static final class FailingMetricsDatabase extends MetricsDatabase {
        private FailingMetricsDatabase(
                MetricsProperties properties,
                ReleaseCatalog releaseCatalog) {
            super(properties, releaseCatalog);
        }

        @Override
        public boolean recordUniqueView(
                String visitorHash,
                String view,
                Instant now,
                LocalDate metricDate) {
            throw failure();
        }

        @Override
        public boolean recordDownload(
                String visitorHash,
                ReleaseInfo release,
                Instant now,
                LocalDate metricDate,
                Duration dedupeWindow) {
            throw failure();
        }

        private MetricsStorageException failure() {
            return new MetricsStorageException(
                    "database unavailable",
                    new SQLException("test failure"));
        }
    }
}
