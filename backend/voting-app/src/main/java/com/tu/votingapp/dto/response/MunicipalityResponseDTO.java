package com.tu.votingapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MunicipalityResponseDTO {
    private Long id;
    private String name;
    private Long population;
    private RegionResponseDTO region;
}

