package com.tu.votingapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String currentAddress;
    private Long locationId;
    private String egn;
    private DocumentDTO document;
    private List<RoleDTO> roles;
}
