package com.bustracker.repository;

import com.bustracker.entity.BusLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusLocationRepository extends JpaRepository<BusLocation, Long> {

    List<BusLocation> findByBusId(String busId);

    // Custom query: Get the latest location for each bus
    @Query("SELECT b FROM BusLocation b WHERE b.timestamp = " +
            "(SELECT MAX(b2.timestamp) FROM BusLocation b2 WHERE b2.busId = b.busId)")
    List<BusLocation> findLatestLocationForEachBus();
}