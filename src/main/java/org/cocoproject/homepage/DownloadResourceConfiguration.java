package org.cocoproject.homepage;

import java.nio.file.Path;

import org.cocoproject.homepage.metrics.DownloadMetricsInterceptor;
import org.cocoproject.homepage.metrics.MetricsProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
public class DownloadResourceConfiguration implements WebMvcConfigurer {
    private final Path downloadDirectory;
    private final DownloadMetricsInterceptor downloadMetricsInterceptor;

    public DownloadResourceConfiguration(
            MetricsProperties properties,
            DownloadMetricsInterceptor downloadMetricsInterceptor) {
        this.downloadDirectory = properties.getDownload().getDirectory().toAbsolutePath().normalize();
        this.downloadMetricsInterceptor = downloadMetricsInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(downloadMetricsInterceptor)
                .addPathPatterns("/downloads/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        addDownloadLocation(registry, "/downloads/**", downloadDirectory);
        // CoCo's resource catalogs use these aliases for externally hosted bundles.
        addDownloadLocation(registry, "/emodelg/**", downloadDirectory.resolve("emodelg"));
        addDownloadLocation(registry, "/cocofold/**", downloadDirectory.resolve("cocofold"));
    }

    private static void addDownloadLocation(
            ResourceHandlerRegistry registry, String pattern, Path directory) {
        String resourceLocation = directory.toUri().toString();
        if (!resourceLocation.endsWith("/")) {
            resourceLocation += "/";
        }

        registry.addResourceHandler(pattern)
                .addResourceLocations(resourceLocation)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
    }
}
