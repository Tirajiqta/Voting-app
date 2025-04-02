package com.tu.votingapp.dto.response.elections;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ElectionDTO {
    private Long id;
    private String electionName;
    private String description;
    private Date startDate;
    private Date endDate;
    // Representing enum values as Strings; you can adjust if needed
    private String electionType;
    private String status;
    // Nested list of candidates
    private List<CandidateDTO> candidates;
    // Reference to the creator's user ID
    private Long createdById;
}
