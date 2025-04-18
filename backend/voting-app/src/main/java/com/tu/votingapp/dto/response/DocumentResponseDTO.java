package com.tu.votingapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponseDTO {
    private Long id;
    private String number;
    private LocalDate validFrom;
    private LocalDate validTo;
    private String issuer;
    private Integer gender;
    private LocalDate dateOfBirth;
    private String permanentAddress;
    private Long userId;
}
