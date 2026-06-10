package com.bustracker.service.impl;

import com.bustracker.dto.request.ScheduleRequest;
import com.bustracker.dto.response.ScheduleResponse;
import com.bustracker.entity.Bus;
import com.bustracker.entity.Route;
import com.bustracker.entity.Schedule;
import com.bustracker.enums.BusStatus;
import com.bustracker.exception.ResourceNotFoundException;
import com.bustracker.repository.BusRepository;
import com.bustracker.repository.RouteRepository;
import com.bustracker.repository.ScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ScheduleServiceImpl}.
 *
 * <p>Tests schedule creation, retrieval by route/day/bus,
 * deletion, and error handling for missing entities.</p>
 */
@ExtendWith(MockitoExtension.class)
class ScheduleServiceImplTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private BusRepository busRepository;

    @Mock
    private RouteRepository routeRepository;

    @InjectMocks
    private ScheduleServiceImpl scheduleService;

    private Route testRoute;
    private Bus testBus;
    private Schedule testSchedule;

    @BeforeEach
    void setUp() {
        testRoute = new Route();
        testRoute.setId(1L);
        testRoute.setRouteNumber("1");
        testRoute.setName("Colombo Fort → Kandy");

        testBus = new Bus("NB-1234", testRoute, 50);
        testBus.setId(1L);
        testBus.setStatus(BusStatus.ACTIVE);

        testSchedule = new Schedule(testBus, testRoute, LocalTime.of(6, 30), 1);
        testSchedule.setId(1L);
        testSchedule.setActive(true);
    }

    @Test
    @DisplayName("Should create schedule successfully")
    void createSchedule_Success() {
        ScheduleRequest request = new ScheduleRequest();
        request.setBusId(1L);
        request.setRouteId(1L);
        request.setDepartureTime("06:30");
        request.setDayOfWeek(1); // Monday

        when(busRepository.findById(1L)).thenReturn(Optional.of(testBus));
        when(routeRepository.findById(1L)).thenReturn(Optional.of(testRoute));
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(testSchedule);

        ScheduleResponse response = scheduleService.createSchedule(request);

        assertNotNull(response);
        assertEquals("06:30", response.getDepartureTime());
        assertEquals("NB-1234", response.getBusRegistrationNumber());
        assertEquals("1", response.getRouteNumber());
        verify(scheduleRepository).save(any(Schedule.class));
    }

    @Test
    @DisplayName("Should throw exception when bus not found for schedule")
    void createSchedule_BusNotFound() {
        ScheduleRequest request = new ScheduleRequest();
        request.setBusId(999L);
        request.setRouteId(1L);
        request.setDepartureTime("06:30");
        request.setDayOfWeek(1);

        when(busRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> scheduleService.createSchedule(request));
    }

    @Test
    @DisplayName("Should throw exception when route not found for schedule")
    void createSchedule_RouteNotFound() {
        ScheduleRequest request = new ScheduleRequest();
        request.setBusId(1L);
        request.setRouteId(999L);
        request.setDepartureTime("06:30");
        request.setDayOfWeek(1);

        when(busRepository.findById(1L)).thenReturn(Optional.of(testBus));
        when(routeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> scheduleService.createSchedule(request));
    }

    @Test
    @DisplayName("Should get schedules by route ID")
    void getSchedulesByRoute() {
        Schedule schedule2 = new Schedule(testBus, testRoute, LocalTime.of(8, 0), 1);
        schedule2.setId(2L);
        schedule2.setActive(true);

        when(scheduleRepository.findByRouteIdAndActiveTrue(1L))
                .thenReturn(Arrays.asList(testSchedule, schedule2));

        List<ScheduleResponse> responses = scheduleService.getSchedulesByRoute(1L);

        assertEquals(2, responses.size());
    }

    @Test
    @DisplayName("Should get schedules by route and day of week")
    void getSchedulesByRouteAndDay() {
        when(scheduleRepository.findByRouteIdAndDayOfWeekAndActiveTrue(1L, 1))
                .thenReturn(List.of(testSchedule));

        List<ScheduleResponse> responses = scheduleService.getSchedulesByRouteAndDay(1L, 1);

        assertEquals(1, responses.size());
        assertEquals("06:30", responses.get(0).getDepartureTime());
    }

    @Test
    @DisplayName("Should return empty list when no schedules found")
    void getSchedulesByRouteAndDay_Empty() {
        when(scheduleRepository.findByRouteIdAndDayOfWeekAndActiveTrue(1L, 7))
                .thenReturn(List.of());

        List<ScheduleResponse> responses = scheduleService.getSchedulesByRouteAndDay(1L, 7);

        assertTrue(responses.isEmpty());
    }

    @Test
    @DisplayName("Should get schedules by bus ID")
    void getSchedulesByBus() {
        when(scheduleRepository.findByBusId(1L)).thenReturn(List.of(testSchedule));

        List<ScheduleResponse> responses = scheduleService.getSchedulesByBus(1L);

        assertEquals(1, responses.size());
        assertEquals("NB-1234", responses.get(0).getBusRegistrationNumber());
    }

    @Test
    @DisplayName("Should delete schedule by ID")
    void deleteSchedule_Success() {
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(testSchedule));

        scheduleService.deleteSchedule(1L);

        verify(scheduleRepository).delete(testSchedule);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent schedule")
    void deleteSchedule_NotFound() {
        when(scheduleRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> scheduleService.deleteSchedule(999L));
    }
}
