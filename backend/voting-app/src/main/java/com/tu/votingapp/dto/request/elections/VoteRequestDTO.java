package com.tu.votingapp.dto.request.elections;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteRequestDTO {

    @NotNull(message = "electionId is required")
    private Long electionId;

    /** Exactly one of candidateId or partyId must be set */
    private Long candidateId;
    private Long partyId;

    @NotBlank(message = "appVersion is required")
    private String appVersion;

    @AssertTrue(
            message = "You must vote for either a candidate or a party, but not both"
    )
    private boolean isValidChoice() {
        return (candidateId != null) ^ (partyId != null);
    }
}