package com.tu.votingapp.dto.response.elections;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElectionResultsDTO {
    private Long electionId;
    private List<CandidateResultDTO> candidateResults;
    private List<PartyResultDTO> partyResults;
}
