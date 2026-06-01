package com.bustracker.mapper;

import com.bustracker.dto.request.BusStopRequest;
import com.bustracker.dto.response.BusStopResponse;
import com.bustracker.entity.BusStop;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class that converts between {@link BusStop} entities and
 * their corresponding DTOs.
 *
 * <p>Follows SRP — this class is solely responsible for mapping
 * between the persistence layer (entities) and the presentation
 * layer (DTOs).</p>
 */
public final class BusStopMapper {

    /** Private constructor to prevent instantiation. */
    private BusStopMapper() {
        throw new UnsupportedOperationException("Mapper class cannot be instantiated");
    }

    /**
     * Converts a request DTO to a JPA entity.
     *
     * @param request the incoming request DTO
     * @return a new BusStop entity (without ID)
     */
    public static BusStop toEntity(BusStopRequest request) {
        BusStop entity = new BusStop();
        entity.setName(request.getName());
        entity.setLatitude(request.getLatitude());
        entity.setLongitude(request.getLongitude());
        return entity;
    }

    /**
     * Updates an existing JPA entity with values from a request DTO.
     *
     * @param entity  the existing entity to update
     * @param request the request DTO containing new values
     */
    public static void updateEntity(BusStop entity, BusStopRequest request) {
        entity.setName(request.getName());
        entity.setLatitude(request.getLatitude());
        entity.setLongitude(request.getLongitude());
    }

    /**
     * Converts a JPA entity to a response DTO.
     *
     * @param entity the JPA entity from the database
     * @return a response DTO safe to send to the client
     */
    public static BusStopResponse toResponse(BusStop entity) {
        return new BusStopResponse(
                entity.getId(),
                entity.getName(),
                entity.getLatitude(),
                entity.getLongitude()
        );
    }

    /**
     * Converts a list of JPA entities to a list of response DTOs.
     *
     * @param entities the list of JPA entities
     * @return list of response DTOs
     */
    public static List<BusStopResponse> toResponseList(List<BusStop> entities) {
        return entities.stream()
                .map(BusStopMapper::toResponse)
                .collect(Collectors.toList());
    }
}
