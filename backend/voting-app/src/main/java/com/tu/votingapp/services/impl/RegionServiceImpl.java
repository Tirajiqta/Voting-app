package com.tu.votingapp.services.impl;

import com.tu.votingapp.dto.response.RegionResponseDTO;
import com.tu.votingapp.entities.RegionEntity;
import com.tu.votingapp.repositories.interfaces.RegionRepository;
import com.tu.votingapp.services.interfaces.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegionServiceImpl implements RegionService {
    private final RegionRepository regionRepository;

    @Override
    public List<RegionResponseDTO> listRegions() {
        return regionRepository.findAll().stream()
                .map(e -> new RegionResponseDTO(e.getId(), e.getName(), e.getPopulation()))
                .collect(Collectors.toList());
    }

    @Override
    public RegionResponseDTO getRegionById(Long id) {
        RegionEntity e = regionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Region not found"));
        return new RegionResponseDTO(e.getId(), e.getName(), e.getPopulation());
    }
}
