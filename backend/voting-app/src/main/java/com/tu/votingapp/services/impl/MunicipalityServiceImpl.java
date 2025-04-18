package com.tu.votingapp.services.impl;

import com.tu.votingapp.dto.response.MunicipalityResponseDTO;
import com.tu.votingapp.dto.response.RegionResponseDTO;
import com.tu.votingapp.entities.MunicipalityEntity;
import com.tu.votingapp.entities.RegionEntity;
import com.tu.votingapp.repositories.interfaces.MunicipalityRepository;
import com.tu.votingapp.repositories.interfaces.RegionRepository;
import com.tu.votingapp.services.interfaces.MunicipalityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MunicipalityServiceImpl implements MunicipalityService {
    private final MunicipalityRepository municipalityRepository;
    private final RegionRepository regionRepository;

    @Override
    public List<MunicipalityResponseDTO> listMunicipalities() {
        return municipalityRepository.findAll().stream().map(e -> {
            RegionEntity r = e.getRegion();
            RegionResponseDTO rdto = new RegionResponseDTO(r.getId(), r.getName(), r.getPopulation());
            return new MunicipalityResponseDTO(e.getId(), e.getName(), e.getPopulation(), rdto);
        }).collect(Collectors.toList());
    }

    @Override
    public MunicipalityResponseDTO getMunicipalityById(Long id) {
        MunicipalityEntity e = municipalityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Municipality not found"));
        RegionEntity r = e.getRegion();
        RegionResponseDTO rdto = new RegionResponseDTO(r.getId(), r.getName(), r.getPopulation());
        return new MunicipalityResponseDTO(e.getId(), e.getName(), e.getPopulation(), rdto);
    }
}
