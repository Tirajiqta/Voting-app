package com.tu.votingapp.dto.request.referendum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReferendumVoteRequestDTO {

    @NotNull(message = "referendumId is required")
    private Long referendumId;

    @NotNull(message = "optionId is required")
    private Long optionId;

    @NotBlank(message = "appVersion is required")
    private String appVersion;
}
