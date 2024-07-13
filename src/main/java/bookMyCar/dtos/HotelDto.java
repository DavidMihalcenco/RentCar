package bookMyCar.dtos;

import bookMyCar.entities.Agency;

import java.io.Serializable;

/**
 * A DTO for the {@link Agency} entity
 */
public record HotelDto(Long id, String name, String location, String phoneNumber,
                       String ownerEmail) implements Serializable {
}