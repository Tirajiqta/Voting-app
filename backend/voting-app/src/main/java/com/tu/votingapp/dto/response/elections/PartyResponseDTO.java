package com.tu.votingapp.dto.response.elections;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartyResponseDTO {
    private Long id;
    private String name;
    private String abbreviation;
    private String logoUrl;
    private String leaderName;
    private List<CandidateResponseDTO> candidates;
}
