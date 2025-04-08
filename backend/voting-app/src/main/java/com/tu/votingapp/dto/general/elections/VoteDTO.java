package com.tu.votingapp.dto.general.elections;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteDTO {
    private Long id;
    private Long userId;
    // Represent the associated election by its ID
    private Long electionId;
    private Date voteTimestamp;
}