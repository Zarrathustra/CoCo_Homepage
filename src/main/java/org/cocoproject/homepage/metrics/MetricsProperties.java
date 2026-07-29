package org.cocoproject.homepage.metrics;

import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "coco")
public class MetricsProperties {
    private Download download = new Download();
    private Metrics metrics = new Metrics();
    private List<Release> releases = new ArrayList<>();

    public Download getDownload() {
        return download;
    }

    public void setDownload(Download download) {
        this.download = download;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public void setMetrics(Metrics metrics) {
        this.metrics = metrics;
    }

    public List<Release> getReleases() {
        return releases;
    }

    public void setReleases(List<Release> releases) {
        this.releases = releases;
    }

    public static class Download {
        private Path directory = Path.of("./downloads");

        public Path getDirectory() {
            return directory;
        }

        public void setDirectory(Path directory) {
            this.directory = directory;
        }
    }

    public static class Metrics {
        private Path database = Path.of("./data/coco-metrics.db");
        private String timeZone = "Asia/Shanghai";
        private String adminToken = "";
        private Duration downloadDedupeWindow = Duration.ofSeconds(30);
        private Duration downloadDedupeRetention = Duration.ofHours(24);
        private Duration viewDedupeRetention = Duration.ofHours(48);
        private Duration cleanupInterval = Duration.ofHours(1);
        private Duration publicCacheDuration = Duration.ofSeconds(60);

        public Path getDatabase() {
            return database;
        }

        public void setDatabase(Path database) {
            this.database = database;
        }

        public String getTimeZone() {
            return timeZone;
        }

        public void setTimeZone(String timeZone) {
            this.timeZone = timeZone;
        }

        public String getAdminToken() {
            return adminToken;
        }

        public void setAdminToken(String adminToken) {
            this.adminToken = adminToken;
        }

        public Duration getDownloadDedupeWindow() {
            return downloadDedupeWindow;
        }

        public void setDownloadDedupeWindow(Duration downloadDedupeWindow) {
            this.downloadDedupeWindow = downloadDedupeWindow;
        }

        public Duration getDownloadDedupeRetention() {
            return downloadDedupeRetention;
        }

        public void setDownloadDedupeRetention(Duration downloadDedupeRetention) {
            this.downloadDedupeRetention = downloadDedupeRetention;
        }

        public Duration getViewDedupeRetention() {
            return viewDedupeRetention;
        }

        public void setViewDedupeRetention(Duration viewDedupeRetention) {
            this.viewDedupeRetention = viewDedupeRetention;
        }

        public Duration getCleanupInterval() {
            return cleanupInterval;
        }

        public void setCleanupInterval(Duration cleanupInterval) {
            this.cleanupInterval = cleanupInterval;
        }

        public Duration getPublicCacheDuration() {
            return publicCacheDuration;
        }

        public void setPublicCacheDuration(Duration publicCacheDuration) {
            this.publicCacheDuration = publicCacheDuration;
        }
    }

    public static class Release {
        private String version;
        private String fileName;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
    }
}
