package com.tu.votingapp.dto.response;

import com.tu.votingapp.dto.general.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Adds getters, setters, equals, hashCode, toString
@NoArgsConstructor // Adds a no-argument constructor
@AllArgsConstructor // Adds a constructor with all arguments
public class UserProfileDetailsDTO {

    private UserDTO user; // Holds the basic user profile data

    private DocumentResponseDTO document; // Holds the user's document data (can be null)
}