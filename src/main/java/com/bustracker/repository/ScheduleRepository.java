package com.bustracker.repository;

import com.bustracker.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link Schedule} entities.
 */
@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByRouteIdAndDayOfWeekAndActiveTrue(Long routeId, Integer dayOfWeek);

    List<Schedule> findByBusId(Long busId);

    List<Schedule> findByRouteIdAndActiveTrue(Long routeId);
}
