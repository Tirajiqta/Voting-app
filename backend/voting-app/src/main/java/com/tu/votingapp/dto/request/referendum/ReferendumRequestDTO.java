package com.tu.votingapp.dto.request.referendum;

import com.tu.votingapp.enums.ReferendumStatus;
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
        groups = {ValidationGroups.Create.class, ValidationGroups.Update.class}
)
public class ReferendumRequestDTO {

    @Null(message = "id must be null when creating", groups = ValidationGroups.Create.class)
    @NotNull(message = "id is required when updating", groups = ValidationGroups.Update.class)
    private Long id;

    @NotBlank(message = "title is required", groups = ValidationGroups.Create.class)
    @Size(min = 3, max = 200,
            message = "title must be between 3 and 200 characters",
            groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String title;

    @Size(max = 2000,
            message = "description can be at most 2000 characters",
            groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String description;

    @NotBlank(message = "question is required", groups = ValidationGroups.Create.class)
    @Size(min = 5, max = 1000,
            message = "question must be between 5 and 1000 characters",
            groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String question;

    @NotNull(message = "startDate is required", groups = ValidationGroups.Create.class)
    @FutureOrPresent(message = "startDate must be today or in the future",
            groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private LocalDate startDate;

    @NotNull(message = "endDate is required", groups = ValidationGroups.Create.class)
    @Future(message = "endDate must be in the future",
            groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private LocalDate endDate;

    @NotNull(message = "status is required", groups = ValidationGroups.Create.class)
    private ReferendumStatus status;

    @NotBlank(message = "appVersion is required")
    private String appVersion;
}
