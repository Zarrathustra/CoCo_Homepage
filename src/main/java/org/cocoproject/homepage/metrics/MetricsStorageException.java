package org.cocoproject.homepage.metrics;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class MetricsStorageException extends RuntimeException {
    public MetricsStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
