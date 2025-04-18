package com.tu.votingapp.dto.request.elections;

import com.tu.votingapp.enums.ElectionStatus;
import com.tu.votingapp.enums.ElectionType;
import com.tu.votingapp.validation.ValidationGroups;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@AssertTrue(
        message = "endDate must be after startDate",
        groups = { ValidationGroups.Create.class, ValidationGroups.Update.class }
)
public class ElectionsRequestDTO {

    @Null(message = "id must be null when creating", groups = ValidationGroups.Create.class)
    @NotNull(message = "id is required when updating", groups = ValidationGroups.Update.class)
    private Long id;

    @NotBlank(message = "electionName is required", groups = ValidationGroups.Create.class)
    @Size(
            min = 3, max = 100,
            message = "electionName must be between 3 and 100 characters",
            groups = { ValidationGroups.Create.class, ValidationGroups.Update.class }
    )
    private String electionName;

    @Size(
            max = 1000,
            message = "description can be at most 1000 characters",
            groups = { ValidationGroups.Create.class, ValidationGroups.Update.class }
    )
    private String description;

    @NotNull(message = "startDate is required", groups = ValidationGroups.Create.class)
    @FutureOrPresent(
            message = "startDate must be today or in the future",
            groups = { ValidationGroups.Create.class, ValidationGroups.Update.class }
    )
    private LocalDate startDate;

    @NotNull(message = "endDate is required", groups = ValidationGroups.Create.class)
    @Future(
            message = "endDate must be in the future",
            groups = { ValidationGroups.Create.class, ValidationGroups.Update.class }
    )
    private LocalDate endDate;

    @NotNull(message = "electionType is required", groups = ValidationGroups.Create.class)
    private ElectionType electionType;

    @NotNull(message = "status is required", groups = ValidationGroups.Create.class)
    private ElectionStatus status;

    @NotBlank(message = "appVersion is required")
    private String appVersion;
}
