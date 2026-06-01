package com.bustracker.service;

/**
 * Service interface for scheduled cleanup operations.
 *
 * <p>Segregated from other service interfaces following the
 * <strong>Interface Segregation Principle (ISP)</strong>. Cleanup
 * is a cross-cutting infrastructure concern, not business logic.</p>
 */
public interface CleanupService {

    /**
     * Removes stale bus location records that are older than the
     * configured retention period.
     */
    void cleanupOldLocations();
}