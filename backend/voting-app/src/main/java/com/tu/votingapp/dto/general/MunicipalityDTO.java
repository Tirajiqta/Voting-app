package com.tu.votingapp.dto.general;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MunicipalityDTO {
    private Long id;
    private String name;
    private Long population;
    private RegionDTO region;

}
