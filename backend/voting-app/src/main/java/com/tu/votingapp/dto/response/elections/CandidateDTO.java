package com.tu.votingapp.dto.response.elections;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandidateDTO {
    private Long id;
    private String name;
    private String bio;
    // Reference to the associated election’s ID
    private Long electionId;
    private int votesCount;
    private String imageUri;
    private String position;
}
