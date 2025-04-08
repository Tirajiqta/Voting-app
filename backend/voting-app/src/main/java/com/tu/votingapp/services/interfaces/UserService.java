package com.tu.votingapp.services.interfaces;

import com.tu.votingapp.dto.general.UserDTO;
import com.tu.votingapp.services.BaseService;

public interface UserService extends BaseService<UserDTO, Long> {
    String login(String egn, String documentIdentifier);

    /**
     * Retrieves a user by its ID.
     */
    UserDTO getById(Long id);

    /**
     * Updates an existing user with the given ID and data.
     */
    UserDTO update(Long id, UserDTO userDTO);
}
