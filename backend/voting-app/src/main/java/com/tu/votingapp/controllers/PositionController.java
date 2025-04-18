package com.tu.votingapp.controllers;

import com.tu.votingapp.dto.response.LocationRegionResponseDTO;
import com.tu.votingapp.dto.response.LocationResponseDTO;
import com.tu.votingapp.dto.response.MunicipalityResponseDTO;
import com.tu.votingapp.dto.response.RegionResponseDTO;
import com.tu.votingapp.services.interfaces.LocationRegionService;
import com.tu.votingapp.services.interfaces.LocationService;
import com.tu.votingapp.services.interfaces.MunicipalityService;
import com.tu.votingapp.services.interfaces.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/position")
@RequiredArgsConstructor
public class PositionController {
    private final RegionService regionService;
    private final MunicipalityService municipalityService;
    private final LocationService locationService;
    private final LocationRegionService locationRegionService;

    // Regions
    @GetMapping("/regions")
    public ResponseEntity<List<RegionResponseDTO>> listRegions() {
        return ResponseEntity.ok(regionService.listRegions());
    }

    @GetMapping("/regions/{id}")
    public ResponseEntity<RegionResponseDTO> getRegion(@PathVariable Long id) {
        return ResponseEntity.ok(regionService.getRegionById(id));
    }

    // Municipalities
    @GetMapping("/municipalities")
    public ResponseEntity<List<MunicipalityResponseDTO>> listMunicipalities() {
        return ResponseEntity.ok(municipalityService.listMunicipalities());
    }

    @GetMapping("/municipalities/{id}")
    public ResponseEntity<MunicipalityResponseDTO> getMunicipality(@PathVariable Long id) {
        return ResponseEntity.ok(municipalityService.getMunicipalityById(id));
    }

    // Locations
    @GetMapping("/locations")
    public ResponseEntity<List<LocationResponseDTO>> listLocations() {
        return ResponseEntity.ok(locationService.listLocations());
    }

    @GetMapping("/locations/{id}")
    public ResponseEntity<LocationResponseDTO> getLocation(@PathVariable Long id) {
        return ResponseEntity.ok(locationService.getLocationById(id));
    }

    // Location-Region mappings
    @GetMapping("/location-regions")
    public ResponseEntity<List<LocationRegionResponseDTO>> listLocationRegions() {
        return ResponseEntity.ok(locationRegionService.listLocationRegions());
    }

    @GetMapping("/location-regions/{id}")
    public ResponseEntity<LocationRegionResponseDTO> getLocationRegion(@PathVariable Long id) {
        return ResponseEntity.ok(locationRegionService.getLocationRegionById(id));
    }
}

