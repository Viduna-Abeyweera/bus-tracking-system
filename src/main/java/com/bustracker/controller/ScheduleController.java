package com.bustracker.controller;

import com.bustracker.dto.request.ScheduleRequest;
import com.bustracker.dto.response.ScheduleResponse;
import com.bustracker.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for schedule management.
 */
@RestController
@RequestMapping("/api/schedules")
@Tag(name = "Schedules", description = "Endpoints for managing bus departure schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/route/{routeId}")
    @Operation(summary = "Get schedules by route", description = "Get all active schedules for a route")
    public ResponseEntity<List<ScheduleResponse>> getSchedulesByRoute(@PathVariable Long routeId) {
        return ResponseEntity.ok(scheduleService.getSchedulesByRoute(routeId));
    }

    @GetMapping("/route/{routeId}/day/{dayOfWeek}")
    @Operation(summary = "Get schedules by route and day",
            description = "Get schedules for a specific route and day (1=Monday, 7=Sunday)")
    public ResponseEntity<List<ScheduleResponse>> getSchedulesByRouteAndDay(
            @PathVariable Long routeId, @PathVariable Integer dayOfWeek) {
        return ResponseEntity.ok(scheduleService.getSchedulesByRouteAndDay(routeId, dayOfWeek));
    }

    @GetMapping("/bus/{busId}")
    @Operation(summary = "Get schedules by bus")
    public ResponseEntity<List<ScheduleResponse>> getSchedulesByBus(@PathVariable Long busId) {
        return ResponseEntity.ok(scheduleService.getSchedulesByBus(busId));
    }

    @PostMapping
    @Operation(summary = "Create schedule", description = "Add a new departure schedule (ADMIN only)")
    public ResponseEntity<ScheduleResponse> createSchedule(
            @Valid @RequestBody ScheduleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduleService.createSchedule(request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete schedule", description = "Remove a schedule (ADMIN only)")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }
}
