package com.tu.votingapp.services.interfaces;

import com.tu.votingapp.dto.response.MunicipalityResponseDTO;

import java.util.List;

public interface MunicipalityService {
    /**
     * List all municipalities.
     */
    List<MunicipalityResponseDTO> listMunicipalities();

    /**
     * Get a specific municipality by ID.
     */
    MunicipalityResponseDTO getMunicipalityById(Long id);
}
