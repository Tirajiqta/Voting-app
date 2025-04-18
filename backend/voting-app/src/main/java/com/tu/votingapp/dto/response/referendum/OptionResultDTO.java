package com.tu.votingapp.dto.response.referendum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionResultDTO {
    private Long optionId;
    private String optionText;
    private int voteCount;
}
