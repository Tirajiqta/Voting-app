package com.tu.votingapp.dto.general.referendum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReferendumOptionDTO {
    private Long id;
    private String optionText;
    private int voteCount;
    // Reference to the associated referendum's ID
    private Long referendumId;
}
