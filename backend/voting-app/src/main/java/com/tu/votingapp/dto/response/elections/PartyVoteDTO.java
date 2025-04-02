package com.tu.votingapp.dto.response.elections;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartyVoteDTO {
    private Long id;
    // Reference to the election's ID
    private Long electionId;
    // Reference to the party's ID
    private Long partyId;
    private int voteCount;
}
