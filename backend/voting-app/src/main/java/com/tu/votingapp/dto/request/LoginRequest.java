package com.tu.votingapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "EGN is required")
    private String egn;

    @NotBlank(message = "Document number is required")
    private String documentNumber;
}
