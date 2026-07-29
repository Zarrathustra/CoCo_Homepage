package org.cocoproject.homepage.metrics;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ReleaseCatalog {
    private final Map<String, ReleaseInfo> byVersion;
    private final Map<String, ReleaseInfo> byFileName;

    public ReleaseCatalog(MetricsProperties properties) {
        Map<String, ReleaseInfo> versions = new LinkedHashMap<>();
        Map<String, ReleaseInfo> files = new LinkedHashMap<>();

        for (MetricsProperties.Release configuredRelease : properties.getReleases()) {
            String version = configuredRelease.getVersion();
            String fileName = configuredRelease.getFileName();
            validate(version, fileName);

            ReleaseInfo release = new ReleaseInfo(version, fileName);
            if (versions.putIfAbsent(version, release) != null) {
                throw new IllegalStateException("Duplicate CoCo release version: " + version);
            }
            if (files.putIfAbsent(fileName, release) != null) {
                throw new IllegalStateException("Duplicate CoCo release file name: " + fileName);
            }
        }

        if (versions.isEmpty()) {
            throw new IllegalStateException("At least one CoCo release must be configured");
        }

        this.byVersion = Map.copyOf(versions);
        this.byFileName = Map.copyOf(files);
    }

    private static void validate(String version, String fileName) {
        if (!StringUtils.hasText(version)) {
            throw new IllegalStateException("A CoCo release version cannot be blank");
        }
        if (!StringUtils.hasText(fileName)
                || fileName.equals(".")
                || fileName.equals("..")
                || fileName.contains("/")
                || fileName.contains("\\")) {
            throw new IllegalStateException("A CoCo release file name must be a single path segment");
        }
    }

    public Optional<ReleaseInfo> findByVersion(String version) {
        return Optional.ofNullable(byVersion.get(version));
    }

    public Optional<ReleaseInfo> findByFileName(String fileName) {
        return Optional.ofNullable(byFileName.get(fileName));
    }

    public Collection<ReleaseInfo> releases() {
        return byVersion.values();
    }

    public record ReleaseInfo(String version, String fileName) {
    }
}
