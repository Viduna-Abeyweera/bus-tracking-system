package com.bustracker.service.impl;

import com.bustracker.repository.BusLocationRepository;
import com.bustracker.service.CleanupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Implementation of {@link CleanupService} for scheduled database maintenance.
 *
 * <p>This service runs as a background job to remove stale bus location
 * records, preventing unbounded table growth. It follows the
 * <strong>Single Responsibility Principle</strong>: its only concern
 * is data lifecycle management.</p>
 *
 * <p>The {@code @Scheduled} annotation is placed on the implementation
 * (not the interface) because scheduling is an implementation detail —
 * the interface only defines <em>what</em> to do, not <em>when</em>.</p>
 */
@Service
public class CleanupServiceImpl implements CleanupService {

    private static final Logger logger = LoggerFactory.getLogger(CleanupServiceImpl.class);

    /** Retention period in hours — locations older than this are deleted. */
    private static final int RETENTION_HOURS = 1;

    private final BusLocationRepository repository;

    public CleanupServiceImpl(BusLocationRepository repository) {
        this.repository = repository;
    }

    /**
     * Removes bus locations older than the retention period.
     * Runs every 30 minutes (1,800,000 milliseconds).
     */
    @Override
    @Scheduled(fixedRate = 1800000)
    @Transactional
    public void cleanupOldLocations() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(RETENTION_HOURS);
        repository.deleteByTimestampBefore(cutoff);
        logger.info("Cleanup: Deleted bus locations older than {}", cutoff);
    }
}
