package com.tu.votingapp.dto.response.elections;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElectionResponseDTO {
    private Long id;
    private String electionName;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String electionType; // enum.name()
    private String status;       // enum.name()
    private Long createdById;
    private List<CandidateResponseDTO> candidates;
    private List<PartyResponseDTO> parties;
}
