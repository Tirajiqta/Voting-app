package com.tu.votingapp.dto.response.referendum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReferendumVoteResponseDTO {
    private Long id;
    private Long userId;
    private Long referendumId;
    private Long optionId;
    private LocalDateTime voteTimestamp;
}
