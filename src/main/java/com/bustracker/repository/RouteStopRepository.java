package com.bustracker.repository;

import com.bustracker.entity.RouteStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link RouteStop} entities.
 */
@Repository
public interface RouteStopRepository extends JpaRepository<RouteStop, Long> {

    List<RouteStop> findByRouteIdOrderBySequenceOrderAsc(Long routeId);

    List<RouteStop> findByBusStopId(Long busStopId);
}
