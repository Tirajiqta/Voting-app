package com.tu.votingapp.services.impl;

import com.tu.votingapp.dto.response.RegionResponseDTO;
import com.tu.votingapp.entities.RegionEntity;
import com.tu.votingapp.repositories.interfaces.RegionRepository;
import com.tu.votingapp.services.interfaces.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegionServiceImpl implements RegionService {
    private final RegionRepository regionRepository;
    private final Logger logger = Logger.getLogger(RegionServiceImpl.class.getName());

    @Override
    public List<RegionResponseDTO> listRegions() {
        logger.info("Listing all regions");
        List<RegionResponseDTO> list = regionRepository.findAll()
                .stream()
                .map(e -> new RegionResponseDTO(e.getId(), e.getName(), e.getPopulation()))
                .collect(Collectors.toList());
        logger.fine(() -> "Retrieved " + list.size() + " regions");
        return list;
    }

    @Override
    public RegionResponseDTO getRegionById(Long id) {
        logger.info(() -> "Fetching region id=" + id);
        RegionEntity e = regionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Region not found: " + id));
        RegionResponseDTO dto = new RegionResponseDTO(e.getId(), e.getName(), e.getPopulation());
        logger.fine(() -> "Fetched region: " + dto.getName());
        return dto;
    }
}