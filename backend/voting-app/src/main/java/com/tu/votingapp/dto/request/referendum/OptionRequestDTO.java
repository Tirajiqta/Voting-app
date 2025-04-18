package com.tu.votingapp.dto.request.referendum;

import com.tu.votingapp.validation.ValidationGroups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionRequestDTO {

    @Null(message = "id must be null when creating", groups = ValidationGroups.Create.class)
    @NotNull(message = "id is required when updating", groups = ValidationGroups.Update.class)
    private Long id;

    @NotNull(message = "referendumId is required")
    private Long referendumId;

    @NotBlank(message = "optionText is required", groups = ValidationGroups.Create.class)
    @Size(min = 1, max = 200,
            message = "optionText must be between 1 and 200 characters",
            groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String optionText;

    @NotBlank(message = "appVersion is required")
    private String appVersion;
}
