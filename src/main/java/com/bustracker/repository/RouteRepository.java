package com.bustracker.repository;

import com.bustracker.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Route} entities.
 */
@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    Optional<Route> findByRouteNumber(String routeNumber);

    List<Route> findByActiveTrue();

    boolean existsByRouteNumber(String routeNumber);

    List<Route> findByOriginContainingIgnoreCaseOrDestinationContainingIgnoreCase(
            String origin, String destination);
}
