package com.tu.votingapp.services.impl;

import com.tu.votingapp.dto.response.LocationResponseDTO;
import com.tu.votingapp.dto.response.MunicipalityResponseDTO;
import com.tu.votingapp.dto.response.RegionResponseDTO;
import com.tu.votingapp.entities.LocationEntity;
import com.tu.votingapp.entities.MunicipalityEntity;
import com.tu.votingapp.entities.RegionEntity;
import com.tu.votingapp.repositories.interfaces.LocationRepository;
import com.tu.votingapp.repositories.interfaces.MunicipalityRepository;
import com.tu.votingapp.services.interfaces.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;
    private final MunicipalityRepository municipalityRepository;

    @Override
    public List<LocationResponseDTO> listLocations() {
        return locationRepository.findAll().stream().map(e -> {
            MunicipalityEntity m = e.getMunicipality();
            RegionEntity r = m.getRegion();
            RegionResponseDTO rdto = new RegionResponseDTO(r.getId(), r.getName(), r.getPopulation());
            MunicipalityResponseDTO mdto = new MunicipalityResponseDTO(m.getId(), m.getName(), m.getPopulation(), rdto);
            return new LocationResponseDTO(e.getId(), e.getName(), mdto);
        }).collect(Collectors.toList());
    }

    @Override
    public LocationResponseDTO getLocationById(Long id) {
        LocationEntity e = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found"));
        MunicipalityEntity m = e.getMunicipality();
        RegionEntity r = m.getRegion();
        RegionResponseDTO rdto = new RegionResponseDTO(r.getId(), r.getName(), r.getPopulation());
        MunicipalityResponseDTO mdto = new MunicipalityResponseDTO(m.getId(), m.getName(), m.getPopulation(), rdto);
        return new LocationResponseDTO(e.getId(), e.getName(), mdto);
    }
}
