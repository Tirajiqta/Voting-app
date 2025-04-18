package com.tu.votingapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationRegionResponseDTO {
    private Long id;
    private String name;
    private LocationResponseDTO location;
}

