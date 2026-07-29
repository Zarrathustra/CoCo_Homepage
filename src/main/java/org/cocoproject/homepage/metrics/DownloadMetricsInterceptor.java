package org.cocoproject.homepage.metrics;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.cocoproject.homepage.metrics.ReleaseCatalog.ReleaseInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class DownloadMetricsInterceptor implements HandlerInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadMetricsInterceptor.class);
    private static final String DOWNLOAD_PREFIX = "/downloads/";
    private static final Pattern INITIAL_SINGLE_RANGE = Pattern.compile("(?i)^bytes\\s*=\\s*0-\\d*$");

    private final Path downloadDirectory;
    private final ReleaseCatalog releaseCatalog;
    private final VisitorIdentityService visitorIdentityService;
    private final MetricsService metricsService;

    public DownloadMetricsInterceptor(
            MetricsProperties properties,
            ReleaseCatalog releaseCatalog,
            VisitorIdentityService visitorIdentityService,
            MetricsService metricsService) {
        this.downloadDirectory = properties.getDownload().getDirectory().toAbsolutePath().normalize();
        this.releaseCatalog = releaseCatalog;
        this.visitorIdentityService = visitorIdentityService;
        this.metricsService = metricsService;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) {
        if (!"GET".equals(request.getMethod()) || !isInitialRequest(request.getHeader(HttpHeaders.RANGE))) {
            return true;
        }

        String requestPath = pathWithinApplication(request);
        if (!requestPath.startsWith(DOWNLOAD_PREFIX)) {
            return true;
        }

        String fileName = requestPath.substring(DOWNLOAD_PREFIX.length());
        ReleaseInfo release = releaseCatalog.findByFileName(fileName).orElse(null);
        if (release == null || !releaseFileExists(release)) {
            return true;
        }

        try {
            String visitorHash = visitorIdentityService.visitorHash(request, response);
            metricsService.recordDownload(release, visitorHash);
        } catch (RuntimeException exception) {
            LOGGER.error("Unable to prepare download metrics for {}", release.version(), exception);
        }
        return true;
    }

    private boolean releaseFileExists(ReleaseInfo release) {
        Path releasePath = downloadDirectory.resolve(release.fileName()).normalize();
        return releasePath.startsWith(downloadDirectory) && Files.isRegularFile(releasePath);
    }

    private static boolean isInitialRequest(String rangeHeader) {
        return rangeHeader == null || INITIAL_SINGLE_RANGE.matcher(rangeHeader.trim()).matches();
    }

    private static String pathWithinApplication(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (!contextPath.isEmpty() && requestUri.startsWith(contextPath)) {
            return requestUri.substring(contextPath.length());
        }
        return requestUri;
    }
}
