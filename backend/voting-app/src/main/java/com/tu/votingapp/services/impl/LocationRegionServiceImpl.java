package com.tu.votingapp.services.impl;

import com.tu.votingapp.dto.response.LocationRegionResponseDTO;
import com.tu.votingapp.dto.response.LocationResponseDTO;
import com.tu.votingapp.dto.response.MunicipalityResponseDTO;
import com.tu.votingapp.dto.response.RegionResponseDTO;
import com.tu.votingapp.entities.LocationEntity;
import com.tu.votingapp.entities.LocationRegionEntity;
import com.tu.votingapp.entities.MunicipalityEntity;
import com.tu.votingapp.entities.RegionEntity;
import com.tu.votingapp.repositories.interfaces.LocationRegionRepository;
import com.tu.votingapp.repositories.interfaces.LocationRepository;
import com.tu.votingapp.services.interfaces.LocationRegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationRegionServiceImpl implements LocationRegionService {
    private final LocationRegionRepository locationRegionRepository;
    private final LocationRepository locationRepository;
    private final Logger logger = Logger.getLogger(LocationRegionServiceImpl.class.getName());

    @Override
    public List<LocationRegionResponseDTO> listLocationRegions() {
        logger.info("Listing all location-region mappings");
        List<LocationRegionResponseDTO> list = locationRegionRepository.findAll()
                .stream()
                .map(e -> {
                    LocationEntity loc = locationRepository.findById(e.getLocation().getId())
                            .orElseThrow(() -> new RuntimeException("Location not found: " + e.getLocation().getId()));
                    MunicipalityEntity m = loc.getMunicipality();
                    RegionEntity r = m.getRegion();
                    RegionResponseDTO rdto = new RegionResponseDTO(r.getId(), r.getName(), r.getPopulation());
                    MunicipalityResponseDTO mdto = new MunicipalityResponseDTO(m.getId(), m.getName(), m.getPopulation(), rdto);
                    LocationResponseDTO ldto = new LocationResponseDTO(loc.getId(), loc.getName(), mdto);
                    return new LocationRegionResponseDTO(e.getId(), e.getName(), ldto);
                })
                .collect(Collectors.toList());
        logger.fine(() -> "Retrieved " + list.size() + " location-region mappings");
        return list;
    }

    @Override
    public LocationRegionResponseDTO getLocationRegionById(Long id) {
        logger.info(() -> "Fetching location-region id=" + id);
        LocationRegionEntity e = locationRegionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("LocationRegion not found: " + id));
        LocationEntity loc = locationRepository.findById(e.getLocation().getId())
                .orElseThrow(() -> new RuntimeException("Location not found: " + e.getLocation().getId()));
        MunicipalityEntity m = loc.getMunicipality();
        RegionEntity r = m.getRegion();
        RegionResponseDTO rdto = new RegionResponseDTO(r.getId(), r.getName(), r.getPopulation());
        MunicipalityResponseDTO mdto = new MunicipalityResponseDTO(m.getId(), m.getName(), m.getPopulation(), rdto);
        LocationResponseDTO ldto = new LocationResponseDTO(loc.getId(), loc.getName(), mdto);
        LocationRegionResponseDTO dto = new LocationRegionResponseDTO(e.getId(), e.getName(), ldto);
        logger.fine(() -> "Fetched location-region: " + dto.getName());
        return dto;
    }
}
