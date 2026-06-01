package com.bustracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested resource is not found in the database.
 *
 * <p>This exception is automatically mapped to HTTP 404 (Not Found) by the
 * {@link GlobalExceptionHandler}. It replaces unsafe patterns like
 * {@code repository.findById(id).orElse(null)} with a clean, exception-based
 * approach that enforces proper error handling.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *     BusStop stop = busStopRepository.findById(stopId)
 *         .orElseThrow(() -> new ResourceNotFoundException("BusStop", "id", stopId));
 * </pre>
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    /**
     * Constructs a new ResourceNotFoundException.
     *
     * @param resourceName the name of the resource (e.g., "BusStop")
     * @param fieldName    the field used for lookup (e.g., "id")
     * @param fieldValue   the value that was not found
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}
