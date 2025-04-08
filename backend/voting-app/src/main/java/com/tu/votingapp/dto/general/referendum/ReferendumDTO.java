package com.tu.votingapp.dto.general.referendum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReferendumDTO {
    private Long id;
    private String title;
    private String description;
    private String question;
    private Date startDate;
    private Date endDate;
    // Representing the enum value as a String (e.g., "OPEN", "CLOSED")
    private String status;
    // List of nested referendum option DTOs
    private List<ReferendumOptionDTO> options;
    // Reference to the ID of the user who created this referendum
    private Long createdById;
}
