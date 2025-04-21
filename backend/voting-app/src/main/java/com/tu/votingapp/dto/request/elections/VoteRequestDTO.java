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

    @NotNull
    private String userId;

    @NotNull(message = "electionId is required")
    private Long electionId;

    /** Exactly one of candidateId or partyId must be set */
    private Long candidateId;
    private Long partyId;
    private String encryptedVote;

    @NotBlank(message = "appVersion is required")
    private String appVersion;


}