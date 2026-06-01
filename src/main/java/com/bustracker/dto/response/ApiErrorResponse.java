package com.bustracker.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standardized error response DTO returned by the API for all error cases.
 *
 * <p>This ensures the frontend always receives a consistent error shape,
 * regardless of whether the error is a validation failure, a 404, or
 * an internal server error.</p>
 *
 * <p>Example JSON output:</p>
 * <pre>
 * {
 *   "timestamp": "2026-06-01T12:00:00",
 *   "status": 404,
 *   "error": "Not Found",
 *   "message": "BusStop not found with id: '99'",
 *   "path": "/api/bus-stops/99",
 *   "details": null
 * }
 * </pre>
 */
public class ApiErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private List<String> details;

    // ===== CONSTRUCTORS =====

    public ApiErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiErrorResponse(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public ApiErrorResponse(int status, String error, String message,
                            String path, List<String> details) {
        this(status, error, message, path);
        this.details = details;
    }

    // ===== GETTERS AND SETTERS =====

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }
}
