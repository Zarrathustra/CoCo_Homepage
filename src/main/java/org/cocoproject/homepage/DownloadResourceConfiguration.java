package org.cocoproject.homepage;

import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
public class DownloadResourceConfiguration implements WebMvcConfigurer {
    private final Path downloadDirectory;

    public DownloadResourceConfiguration(
            @Value("${coco.download.directory}") String downloadDirectory) {
        this.downloadDirectory = Path.of(downloadDirectory).toAbsolutePath().normalize();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String resourceLocation = downloadDirectory.toUri().toString();
        if (!resourceLocation.endsWith("/")) {
            resourceLocation += "/";
        }

        registry.addResourceHandler("/downloads/**")
                .addResourceLocations(resourceLocation)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
    }
}
