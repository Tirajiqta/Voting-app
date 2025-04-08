package com.tu.votingapp.dto.general;

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
    private String password;
    private String currentAddress;
    private LocationDTO locationId;
    private String egn;
    private DocumentDTO document;
    private List<RoleDTO> roles;
}
