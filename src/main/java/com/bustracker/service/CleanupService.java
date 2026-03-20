package com.bustracker.service;

import com.bustracker.repository.BusLocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CleanupService {

    private static final Logger logger = LoggerFactory.getLogger(CleanupService.class);

    private final BusLocationRepository repository;

    public CleanupService(BusLocationRepository repository) {
        this.repository = repository;
    }

    // Run every 30 minutes (1800000 milliseconds)
    @Scheduled(fixedRate = 1800000)
    @Transactional
    public void cleanupOldLocations() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        repository.deleteByTimestampBefore(oneHourAgo);
        logger.info("Cleanup: Deleted bus locations older than {}", oneHourAgo);
    }
}