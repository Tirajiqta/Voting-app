package com.tu.votingapp.services.interfaces;

import com.tu.votingapp.dto.response.LocationResponseDTO;

import java.util.List;

public interface LocationService {
    /**
     * List all locations.
     */
    List<LocationResponseDTO> listLocations();

    /**
     * Get a specific location by ID.
     */
    LocationResponseDTO getLocationById(Long id);
}
