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
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/position")
@RequiredArgsConstructor
public class PositionController {
    private final RegionService regionService;
    private final MunicipalityService municipalityService;
    private final LocationService locationService;
    private final LocationRegionService locationRegionService;
    private final Logger logger = Logger.getLogger(PositionController.class.getName());

    // Regions
    @GetMapping("/regions")
    public ResponseEntity<List<RegionResponseDTO>> listRegions() {
        logger.info("Listing all regions");
        List<RegionResponseDTO> list = regionService.listRegions();
        logger.fine(() -> "Retrieved " + list.size() + " regions");
        return ResponseEntity.ok(list);
    }

    @GetMapping("/regions/{id}")
    public ResponseEntity<RegionResponseDTO> getRegion(@PathVariable Long id) {
        logger.info(() -> "Fetching region id=" + id);
        RegionResponseDTO dto = regionService.getRegionById(id);
        logger.fine(() -> "Fetched region: " + dto.getName());
        return ResponseEntity.ok(dto);
    }

    // Municipalities
    @GetMapping("/municipalities")
    public ResponseEntity<List<MunicipalityResponseDTO>> listMunicipalities() {
        logger.info("Listing all municipalities");
        List<MunicipalityResponseDTO> list = municipalityService.listMunicipalities();
        logger.fine(() -> "Retrieved " + list.size() + " municipalities");
        return ResponseEntity.ok(list);
    }

    @GetMapping("/municipalities/{id}")
    public ResponseEntity<MunicipalityResponseDTO> getMunicipality(@PathVariable Long id) {
        logger.info(() -> "Fetching municipality id=" + id);
        MunicipalityResponseDTO dto = municipalityService.getMunicipalityById(id);
        logger.fine(() -> "Fetched municipality: " + dto.getName());
        return ResponseEntity.ok(dto);
    }

    // Locations
    @GetMapping("/locations")
    public ResponseEntity<List<LocationResponseDTO>> listLocations() {
        logger.info("Listing all locations");
        List<LocationResponseDTO> list = locationService.listLocations();
        logger.fine(() -> "Retrieved " + list.size() + " locations");
        return ResponseEntity.ok(list);
    }

    @GetMapping("/locations/{id}")
    public ResponseEntity<LocationResponseDTO> getLocation(@PathVariable Long id) {
        logger.info(() -> "Fetching location id=" + id);
        LocationResponseDTO dto = locationService.getLocationById(id);
        logger.fine(() -> "Fetched location: " + dto.getName());
        return ResponseEntity.ok(dto);
    }

    // Location-Region mappings
    @GetMapping("/location-regions")
    public ResponseEntity<List<LocationRegionResponseDTO>> listLocationRegions() {
        logger.info("Listing all location-region mappings");
        List<LocationRegionResponseDTO> list = locationRegionService.listLocationRegions();
        logger.fine(() -> "Retrieved " + list.size() + " location-region entries");
        return ResponseEntity.ok(list);
    }

    @GetMapping("/location-regions/{id}")
    public ResponseEntity<LocationRegionResponseDTO> getLocationRegion(@PathVariable Long id) {
        logger.info(() -> "Fetching location-region id=" + id);
        LocationRegionResponseDTO dto = locationRegionService.getLocationRegionById(id);
        logger.fine(() -> "Fetched location-region: " + dto.getName());
        return ResponseEntity.ok(dto);
    }
}