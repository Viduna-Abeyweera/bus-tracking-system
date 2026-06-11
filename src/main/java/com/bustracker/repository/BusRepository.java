package com.bustracker.repository;

import com.bustracker.entity.Bus;
import com.bustracker.enums.BusStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Bus} entities.
 */
@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {

    Optional<Bus> findByRegistrationNumber(String registrationNumber);

    List<Bus> findByRouteId(Long routeId);

    List<Bus> findByStatus(BusStatus status);

    List<Bus> findByDriverId(Long driverId);

    boolean existsByRegistrationNumber(String registrationNumber);
}
