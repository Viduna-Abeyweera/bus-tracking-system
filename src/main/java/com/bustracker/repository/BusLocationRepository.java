package com.bustracker.repository;

import com.bustracker.entity.BusLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusLocationRepository extends JpaRepository<BusLocation, Long> {

    // Spring auto-generates this query from the method name!
    // It means: SELECT * FROM bus_locations WHERE bus_id = ?
    List<BusLocation> findByBusId(String busId);
}