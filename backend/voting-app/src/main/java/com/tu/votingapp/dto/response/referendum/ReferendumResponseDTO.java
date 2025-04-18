package com.tu.votingapp.dto.response.referendum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReferendumResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String question;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private Long createdById;
    private List<OptionResponseDTO> options;
}
