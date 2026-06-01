package com.bustracker.mapper;

import com.bustracker.dto.request.BusLocationRequest;
import com.bustracker.dto.response.BusLocationResponse;
import com.bustracker.entity.BusLocation;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class that converts between {@link BusLocation} entities and
 * their corresponding DTOs.
 *
 * <p>This class follows the Single Responsibility Principle (SRP): its only
 * concern is data transformation between layers. It does not perform any
 * business logic or database operations.</p>
 *
 * <p>All methods are static since the mapper holds no state and does not
 * require dependency injection.</p>
 */
public final class BusLocationMapper {

    /** Private constructor to prevent instantiation. */
    private BusLocationMapper() {
        throw new UnsupportedOperationException("Mapper class cannot be instantiated");
    }

    /**
     * Converts a request DTO to a JPA entity.
     * Note: timestamp is NOT set here — it is set by the service layer.
     *
     * @param request the incoming request DTO
     * @return a new BusLocation entity (without ID or timestamp)
     */
    public static BusLocation toEntity(BusLocationRequest request) {
        BusLocation entity = new BusLocation();
        entity.setBusId(request.getBusId());
        entity.setLatitude(request.getLatitude());
        entity.setLongitude(request.getLongitude());
        return entity;
    }

    /**
     * Converts a JPA entity to a response DTO.
     *
     * @param entity the JPA entity from the database
     * @return a response DTO safe to send to the client
     */
    public static BusLocationResponse toResponse(BusLocation entity) {
        return new BusLocationResponse(
                entity.getId(),
                entity.getBusId(),
                entity.getLatitude(),
                entity.getLongitude(),
                entity.getTimestamp()
        );
    }

    /**
     * Converts a list of JPA entities to a list of response DTOs.
     *
     * @param entities the list of JPA entities
     * @return list of response DTOs
     */
    public static List<BusLocationResponse> toResponseList(List<BusLocation> entities) {
        return entities.stream()
                .map(BusLocationMapper::toResponse)
                .collect(Collectors.toList());
    }
}
