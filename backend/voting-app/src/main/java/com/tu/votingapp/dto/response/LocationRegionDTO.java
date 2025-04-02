package com.tu.votingapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationRegionDTO {
    private Long id;
    private String name;
    private LocationDTO location;
}
