package com.tu.votingapp.dto.request.elections;

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
public class CandidateRequestDTO {

    @Null(message = "id must be null when creating", groups = ValidationGroups.Create.class)
    @NotNull(message = "id is required when updating", groups = ValidationGroups.Update.class)
    private Long id;

    @NotNull(message = "electionId is required")
    private Long electionId;

    @NotBlank(message = "name is required")
    @Size(
            min = 2, max = 100,
            message = "name must be between 2 and 100 characters"
    )
    private String name;

    @Size(
            max = 1000,
            message = "bio can be at most 1000 characters"
    )
    private String bio;

    private String imageUri;

    @Size(
            max = 50,
            message = "position can be at most 50 characters"
    )
    private String position;

    @NotBlank(message = "appVersion is required")
    private String appVersion;
}
