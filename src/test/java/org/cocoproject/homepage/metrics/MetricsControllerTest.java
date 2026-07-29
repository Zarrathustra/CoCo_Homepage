package org.cocoproject.homepage.metrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class MetricsControllerTest {
    @Test
    void internalMetricsAreHiddenWhenAdminTokenIsNotConfigured() {
        MetricsProperties properties = new MetricsProperties();
        MetricsController controller = new MetricsController(
                null,
                null,
                null,
                properties,
                Clock.systemUTC());

        assertThatThrownBy(() -> controller.internalMetrics(null, null, null))
                .isInstanceOfSatisfying(
                        ResponseStatusException.class,
                        exception -> assertThat(exception.getStatusCode())
                                .isEqualTo(HttpStatus.NOT_FOUND));
    }
}
