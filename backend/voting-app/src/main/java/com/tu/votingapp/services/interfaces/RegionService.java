package com.tu.votingapp.services.interfaces;

import com.tu.votingapp.dto.response.RegionResponseDTO;

import java.util.List;

public interface RegionService {
    /**
     * List all regions.
     */
    List<RegionResponseDTO> listRegions();

    /**
     * Get a specific region by ID.
     */
    RegionResponseDTO getRegionById(Long id);
}
