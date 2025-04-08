package com.tu.votingapp.dto.general;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentDTO {
    private Long id;
    private String number;
    private Date validFrom;
    private Date validTo;
    private String issuer;
    private Integer gender;
    private Date dateOfBirth;
    private String permanentAddress;
    private UserDTO user;
}
