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
    private Long electionId;
    // ID of the candidate being voted for. Can be null if voting only for a party.
    private Long candidateId;
    // ID of the party being voted for. Can be null if voting for an independent candidate.
    private Long partyId;
    private Date voteTimestamp;
}