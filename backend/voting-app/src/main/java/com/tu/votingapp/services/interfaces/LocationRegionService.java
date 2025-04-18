package com.tu.votingapp.services.interfaces;

import com.tu.votingapp.dto.response.LocationRegionResponseDTO;

import java.util.List;

public interface LocationRegionService {
    /**
     * List all location-region mappings.
     */
    List<LocationRegionResponseDTO> listLocationRegions();

    /**
     * Get a specific location-region mapping by ID.
     */
    LocationRegionResponseDTO getLocationRegionById(Long id);
}
