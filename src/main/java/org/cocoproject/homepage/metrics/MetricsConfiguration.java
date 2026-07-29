package org.cocoproject.homepage.metrics;

import java.time.Clock;
import java.time.ZoneId;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfiguration {
    @Bean
    Clock metricsClock(MetricsProperties properties) {
        ZoneId zoneId = ZoneId.of(properties.getMetrics().getTimeZone());
        return Clock.system(zoneId);
    }
}
