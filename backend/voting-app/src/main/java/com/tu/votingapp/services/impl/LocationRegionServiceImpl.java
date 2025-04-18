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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationRegionServiceImpl implements LocationRegionService {
    private final LocationRegionRepository locationRegionRepository;
    private final LocationRepository locationRepository;

    @Override
    public List<LocationRegionResponseDTO> listLocationRegions() {
        return locationRegionRepository.findAll().stream().map(e -> {
            LocationEntity loc = locationRepository.findById(e.getLocation().getId())
                    .orElseThrow(() -> new RuntimeException("Location not found"));
            MunicipalityEntity m = loc.getMunicipality();
            RegionEntity r = m.getRegion();
            RegionResponseDTO rdto = new RegionResponseDTO(r.getId(), r.getName(), r.getPopulation());
            MunicipalityResponseDTO mdto = new MunicipalityResponseDTO(m.getId(), m.getName(), m.getPopulation(), rdto);
            LocationResponseDTO ldto = new LocationResponseDTO(loc.getId(), loc.getName(), mdto);
            return new LocationRegionResponseDTO(e.getId(), e.getName(), ldto);
        }).collect(Collectors.toList());
    }

    @Override
    public LocationRegionResponseDTO getLocationRegionById(Long id) {
        LocationRegionEntity e = locationRegionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("LocationRegion not found"));
        LocationEntity loc = locationRepository.findById(e.getLocation().getId())
                .orElseThrow(() -> new RuntimeException("Location not found"));
        MunicipalityEntity m = loc.getMunicipality();
        RegionEntity r = m.getRegion();
        RegionResponseDTO rdto = new RegionResponseDTO(r.getId(), r.getName(), r.getPopulation());
        MunicipalityResponseDTO mdto = new MunicipalityResponseDTO(m.getId(), m.getName(), m.getPopulation(), rdto);
        LocationResponseDTO ldto = new LocationResponseDTO(loc.getId(), loc.getName(), mdto);
        return new LocationRegionResponseDTO(e.getId(), e.getName(), ldto);
    }
}

