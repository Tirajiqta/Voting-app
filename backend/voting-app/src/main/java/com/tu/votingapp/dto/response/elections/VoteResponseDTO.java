package com.tu.votingapp.dto.response.elections;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteResponseDTO {
    private Long id;
    private Long userId;
    private Long electionId;
    private Long candidateId;    // nullable if a party vote
    private Long partyId;        // nullable if a candidate vote
    private LocalDateTime voteTimestamp;
}
