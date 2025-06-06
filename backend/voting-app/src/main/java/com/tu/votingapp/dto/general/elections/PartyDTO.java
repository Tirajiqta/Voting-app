package com.tu.votingapp.dto.general.elections;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartyDTO {
    private Long id;
    private String name;
    private String abbreviation;
    private Long electionId;
    private String logoUrl;
    private String leaderName;
    // Nested list of candidates associated with the party
    private List<CandidateDTO> candidates;

}
