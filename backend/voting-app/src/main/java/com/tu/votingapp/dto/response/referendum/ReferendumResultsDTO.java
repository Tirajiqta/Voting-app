package com.tu.votingapp.dto.response.referendum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReferendumResultsDTO {
    private Long referendumId;
    private List<OptionResultDTO> optionResults;
}
