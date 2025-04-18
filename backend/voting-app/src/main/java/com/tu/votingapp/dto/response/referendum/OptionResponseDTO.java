package com.tu.votingapp.dto.response.referendum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionResponseDTO {
    private Long id;
    private String optionText;
    private int voteCount;
    private Long referendumId;
}
