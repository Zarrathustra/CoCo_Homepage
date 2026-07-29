package org.cocoproject.homepage.metrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PostConstruct;

import org.cocoproject.homepage.metrics.ReleaseCatalog.ReleaseInfo;
import org.springframework.stereotype.Component;

@Component
public class MetricsDatabase {
    private static final int SCHEMA_VERSION = 1;
    private static final int BUSY_TIMEOUT_MILLIS = 5_000;

    private final Path databasePath;
    private final String jdbcUrl;
    private final ReleaseCatalog releaseCatalog;

    public MetricsDatabase(MetricsProperties properties, ReleaseCatalog releaseCatalog) {
        this.databasePath = properties.getMetrics().getDatabase().toAbsolutePath().normalize();
        this.jdbcUrl = "jdbc:sqlite:" + databasePath;
        this.releaseCatalog = releaseCatalog;
    }

    @PostConstruct
    public void initialize() {
        try {
            Path parent = databasePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            try (Connection connection = openConnection()) {
                enableWriteAheadLogging(connection);
                migrate(connection);
                seedReleaseCatalog(connection);
            }
        } catch (IOException | SQLException exception) {
            throw new IllegalStateException(
                    "Unable to initialize the CoCo metrics database at " + databasePath,
                    exception);
        }
    }

    public boolean recordDownload(
            String visitorHash,
            ReleaseInfo release,
            Instant now,
            LocalDate metricDate,
            Duration dedupeWindow) {
        String claimSql = """
                INSERT INTO download_dedupe(visitor_hash, release_version, last_counted_at)
                VALUES (?, ?, ?)
                ON CONFLICT(visitor_hash, release_version) DO UPDATE SET
                    last_counted_at = excluded.last_counted_at
                WHERE download_dedupe.last_counted_at <= ?
                """;
        String totalSql = """
                INSERT INTO release_download_totals(
                    release_version, file_name, download_count, updated_at)
                VALUES (?, ?, 1, ?)
                ON CONFLICT(release_version) DO UPDATE SET
                    file_name = excluded.file_name,
                    download_count = release_download_totals.download_count + 1,
                    updated_at = excluded.updated_at
                """;
        String dailySql = """
                INSERT INTO release_download_daily(metric_date, release_version, download_count)
                VALUES (?, ?, 1)
                ON CONFLICT(metric_date, release_version) DO UPDATE SET
                    download_count = release_download_daily.download_count + 1
                """;

        try (Connection connection = openConnection()) {
            connection.setAutoCommit(false);
            try {
                long timestamp = now.toEpochMilli();
                int claimed;
                try (PreparedStatement statement = connection.prepareStatement(claimSql)) {
                    statement.setString(1, visitorHash);
                    statement.setString(2, release.version());
                    statement.setLong(3, timestamp);
                    statement.setLong(4, now.minus(dedupeWindow).toEpochMilli());
                    claimed = statement.executeUpdate();
                }

                if (claimed == 0) {
                    connection.rollback();
                    return false;
                }

                try (PreparedStatement statement = connection.prepareStatement(totalSql)) {
                    statement.setString(1, release.version());
                    statement.setString(2, release.fileName());
                    statement.setLong(3, timestamp);
                    statement.executeUpdate();
                }

                try (PreparedStatement statement = connection.prepareStatement(dailySql)) {
                    statement.setString(1, metricDate.toString());
                    statement.setString(2, release.version());
                    statement.executeUpdate();
                }

                connection.commit();
                return true;
            } catch (SQLException exception) {
                rollback(connection, exception);
                throw exception;
            }
        } catch (SQLException exception) {
            throw new MetricsStorageException("Unable to record a CoCo download", exception);
        }
    }

    public boolean recordUniqueView(
            String visitorHash,
            String view,
            Instant now,
            LocalDate metricDate) {
        String claimSql = """
                INSERT OR IGNORE INTO daily_unique_dedupe(
                    metric_date, view_name, visitor_hash, created_at)
                VALUES (?, ?, ?, ?)
                """;
        String totalSql = """
                INSERT INTO daily_unique_counts(metric_date, view_name, unique_count)
                VALUES (?, ?, 1)
                ON CONFLICT(metric_date, view_name) DO UPDATE SET
                    unique_count = daily_unique_counts.unique_count + 1
                """;

        try (Connection connection = openConnection()) {
            connection.setAutoCommit(false);
            try {
                int claimed;
                try (PreparedStatement statement = connection.prepareStatement(claimSql)) {
                    statement.setString(1, metricDate.toString());
                    statement.setString(2, view);
                    statement.setString(3, visitorHash);
                    statement.setLong(4, now.toEpochMilli());
                    claimed = statement.executeUpdate();
                }

                if (claimed == 0) {
                    connection.rollback();
                    return false;
                }

                try (PreparedStatement statement = connection.prepareStatement(totalSql)) {
                    statement.setString(1, metricDate.toString());
                    statement.setString(2, view);
                    statement.executeUpdate();
                }

                connection.commit();
                return true;
            } catch (SQLException exception) {
                rollback(connection, exception);
                throw exception;
            }
        } catch (SQLException exception) {
            throw new MetricsStorageException("Unable to record a CoCo page view", exception);
        }
    }

    public ReleaseStatistics releaseStatistics(String releaseVersion) {
        String sql = """
                SELECT release.download_count,
                       (SELECT COALESCE(SUM(download_count), 0)
                          FROM release_download_totals) AS all_downloads
                  FROM release_download_totals AS release
                 WHERE release.release_version = ?
                """;

        try (Connection connection = openConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, releaseVersion);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new MetricsStorageException(
                            "No metrics row exists for configured release " + releaseVersion,
                            null);
                }
                return new ReleaseStatistics(
                        resultSet.getLong("download_count"),
                        resultSet.getLong("all_downloads"));
            }
        } catch (SQLException exception) {
            throw new MetricsStorageException("Unable to read CoCo release statistics", exception);
        }
    }

    public InternalMetricsReport internalReport(LocalDate from, LocalDate to) {
        List<DailyViewMetric> dailyUniqueVisitors = queryDailyUniqueVisitors(from, to);
        List<DailyDownloadMetric> dailyDownloads = queryDailyDownloads(from, to);
        List<ReleaseTotalMetric> releaseTotals = queryReleaseTotals();
        long allReleaseDownloads = releaseTotals.stream()
                .mapToLong(ReleaseTotalMetric::downloads)
                .sum();
        return new InternalMetricsReport(
                from,
                to,
                dailyUniqueVisitors,
                dailyDownloads,
                releaseTotals,
                allReleaseDownloads);
    }

    public void cleanup(
            Instant now,
            Duration downloadDedupeRetention,
            Duration viewDedupeRetention) {
        String downloadSql = "DELETE FROM download_dedupe WHERE last_counted_at < ?";
        String viewSql = "DELETE FROM daily_unique_dedupe WHERE created_at < ?";

        try (Connection connection = openConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement statement = connection.prepareStatement(downloadSql)) {
                    statement.setLong(1, now.minus(downloadDedupeRetention).toEpochMilli());
                    statement.executeUpdate();
                }
                try (PreparedStatement statement = connection.prepareStatement(viewSql)) {
                    statement.setLong(1, now.minus(viewDedupeRetention).toEpochMilli());
                    statement.executeUpdate();
                }
                connection.commit();
            } catch (SQLException exception) {
                rollback(connection, exception);
                throw exception;
            }
        } catch (SQLException exception) {
            throw new MetricsStorageException("Unable to clean up CoCo metrics deduplication data", exception);
        }
    }

    private List<DailyViewMetric> queryDailyUniqueVisitors(LocalDate from, LocalDate to) {
        String sql = """
                SELECT metric_date, view_name, unique_count
                  FROM daily_unique_counts
                 WHERE metric_date BETWEEN ? AND ?
                 ORDER BY metric_date, view_name
                """;
        List<DailyViewMetric> metrics = new ArrayList<>();
        try (Connection connection = openConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, from.toString());
            statement.setString(2, to.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    metrics.add(new DailyViewMetric(
                            LocalDate.parse(resultSet.getString("metric_date")),
                            resultSet.getString("view_name"),
                            resultSet.getLong("unique_count")));
                }
            }
        } catch (SQLException exception) {
            throw new MetricsStorageException("Unable to read daily CoCo unique visitors", exception);
        }
        return List.copyOf(metrics);
    }

    private List<DailyDownloadMetric> queryDailyDownloads(LocalDate from, LocalDate to) {
        String sql = """
                SELECT metric_date, release_version, download_count
                  FROM release_download_daily
                 WHERE metric_date BETWEEN ? AND ?
                 ORDER BY metric_date, release_version
                """;
        List<DailyDownloadMetric> metrics = new ArrayList<>();
        try (Connection connection = openConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, from.toString());
            statement.setString(2, to.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    metrics.add(new DailyDownloadMetric(
                            LocalDate.parse(resultSet.getString("metric_date")),
                            resultSet.getString("release_version"),
                            resultSet.getLong("download_count")));
                }
            }
        } catch (SQLException exception) {
            throw new MetricsStorageException("Unable to read daily CoCo downloads", exception);
        }
        return List.copyOf(metrics);
    }

    private List<ReleaseTotalMetric> queryReleaseTotals() {
        String sql = """
                SELECT release_version, file_name, download_count, updated_at
                  FROM release_download_totals
                 ORDER BY release_version
                """;
        List<ReleaseTotalMetric> metrics = new ArrayList<>();
        try (Connection connection = openConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                metrics.add(new ReleaseTotalMetric(
                        resultSet.getString("release_version"),
                        resultSet.getString("file_name"),
                        resultSet.getLong("download_count"),
                        Instant.ofEpochMilli(resultSet.getLong("updated_at"))));
            }
        } catch (SQLException exception) {
            throw new MetricsStorageException("Unable to read CoCo release totals", exception);
        }
        return List.copyOf(metrics);
    }

    private Connection openConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(jdbcUrl);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA busy_timeout=" + BUSY_TIMEOUT_MILLIS);
            statement.execute("PRAGMA foreign_keys=ON");
        } catch (SQLException exception) {
            connection.close();
            throw exception;
        }
        return connection;
    }

    private void enableWriteAheadLogging(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("PRAGMA journal_mode=WAL")) {
            if (!resultSet.next() || !"wal".equalsIgnoreCase(resultSet.getString(1))) {
                throw new SQLException("SQLite did not enable WAL journal mode");
            }
        }
    }

    private void migrate(Connection connection) throws SQLException {
        int version;
        try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("PRAGMA user_version")) {
            version = resultSet.next() ? resultSet.getInt(1) : 0;
        }

        if (version > SCHEMA_VERSION) {
            throw new SQLException(
                    "Metrics database schema " + version
                            + " is newer than supported schema " + SCHEMA_VERSION);
        }
        if (version == SCHEMA_VERSION) {
            return;
        }

        connection.setAutoCommit(false);
        try (Statement statement = connection.createStatement()) {
            if (version == 0) {
                createSchemaV1(statement);
                statement.execute("PRAGMA user_version=" + SCHEMA_VERSION);
            }
            connection.commit();
        } catch (SQLException exception) {
            rollback(connection, exception);
            throw exception;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private void createSchemaV1(Statement statement) throws SQLException {
        statement.execute("""
                CREATE TABLE release_download_totals (
                    release_version TEXT PRIMARY KEY,
                    file_name TEXT NOT NULL,
                    download_count INTEGER NOT NULL DEFAULT 0 CHECK(download_count >= 0),
                    updated_at INTEGER NOT NULL
                )
                """);
        statement.execute("""
                CREATE TABLE release_download_daily (
                    metric_date TEXT NOT NULL,
                    release_version TEXT NOT NULL,
                    download_count INTEGER NOT NULL DEFAULT 0 CHECK(download_count >= 0),
                    PRIMARY KEY(metric_date, release_version),
                    FOREIGN KEY(release_version)
                        REFERENCES release_download_totals(release_version)
                )
                """);
        statement.execute("""
                CREATE TABLE download_dedupe (
                    visitor_hash TEXT NOT NULL,
                    release_version TEXT NOT NULL,
                    last_counted_at INTEGER NOT NULL,
                    PRIMARY KEY(visitor_hash, release_version),
                    FOREIGN KEY(release_version)
                        REFERENCES release_download_totals(release_version)
                )
                """);
        statement.execute("""
                CREATE INDEX download_dedupe_last_counted_idx
                    ON download_dedupe(last_counted_at)
                """);
        statement.execute("""
                CREATE TABLE daily_unique_counts (
                    metric_date TEXT NOT NULL,
                    view_name TEXT NOT NULL,
                    unique_count INTEGER NOT NULL DEFAULT 0 CHECK(unique_count >= 0),
                    PRIMARY KEY(metric_date, view_name)
                )
                """);
        statement.execute("""
                CREATE TABLE daily_unique_dedupe (
                    metric_date TEXT NOT NULL,
                    view_name TEXT NOT NULL,
                    visitor_hash TEXT NOT NULL,
                    created_at INTEGER NOT NULL,
                    PRIMARY KEY(metric_date, view_name, visitor_hash)
                )
                """);
        statement.execute("""
                CREATE INDEX daily_unique_dedupe_created_idx
                    ON daily_unique_dedupe(created_at)
                """);
    }

    private void seedReleaseCatalog(Connection connection) throws SQLException {
        String sql = """
                INSERT INTO release_download_totals(
                    release_version, file_name, download_count, updated_at)
                VALUES (?, ?, 0, ?)
                ON CONFLICT(release_version) DO UPDATE SET
                    file_name = excluded.file_name,
                    updated_at = CASE
                        WHEN release_download_totals.file_name <> excluded.file_name
                        THEN excluded.updated_at
                        ELSE release_download_totals.updated_at
                    END
                """;
        connection.setAutoCommit(false);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            long now = Instant.now().toEpochMilli();
            for (ReleaseInfo release : releaseCatalog.releases()) {
                statement.setString(1, release.version());
                statement.setString(2, release.fileName());
                statement.setLong(3, now);
                statement.addBatch();
            }
            statement.executeBatch();
            connection.commit();
        } catch (SQLException exception) {
            rollback(connection, exception);
            throw exception;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private static void rollback(Connection connection, SQLException original) {
        try {
            connection.rollback();
        } catch (SQLException rollbackFailure) {
            original.addSuppressed(rollbackFailure);
        }
    }

    public record ReleaseStatistics(long releaseDownloads, long allReleaseDownloads) {
    }

    public record DailyViewMetric(LocalDate date, String view, long uniqueVisitors) {
    }

    public record DailyDownloadMetric(LocalDate date, String releaseVersion, long downloads) {
    }

    public record ReleaseTotalMetric(
            String releaseVersion,
            String fileName,
            long downloads,
            Instant updatedAt) {
    }

    public record InternalMetricsReport(
            LocalDate from,
            LocalDate to,
            List<DailyViewMetric> dailyUniqueVisitors,
            List<DailyDownloadMetric> dailyDownloads,
            List<ReleaseTotalMetric> releaseTotals,
            long allReleaseDownloads) {
    }
}
