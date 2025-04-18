package com.tu.votingapp.services.interfaces;

import com.tu.votingapp.dto.general.PermissionDTO;
import com.tu.votingapp.dto.general.RoleDTO;
import com.tu.votingapp.dto.general.UserDTO;
import com.tu.votingapp.dto.request.DocumentRequestDTO;
import com.tu.votingapp.dto.response.DocumentResponseDTO;
import com.tu.votingapp.services.BaseService;

import java.util.List;

public interface UserService extends BaseService<UserDTO, Long> {

    /**
     * Authenticate user and return a JWT token.
     */
    String login(String egn, String documentNumber);

    /**
     * Retrieve user profile by ID.
     */
    UserDTO getById(Long id);

    /**
     * Update user profile.
     */
    UserDTO update(Long id, UserDTO userDTO);

    /**
     * Add or update a document for the user.
     */
    DocumentResponseDTO addDocument(Long userId, DocumentRequestDTO request);

    /**
     * Get the document associated with the user.
     */
    DocumentResponseDTO getDocument(Long userId);

    /**
     * Assign a role to a user.
     */
    RoleDTO addRoleToUser(Long userId, Long roleId);

    /**
     * Replace all roles for a user.
     */
    List<RoleDTO> updateUserRoles(Long userId, List<Long> roleIds);

    /**
     * Remove a role from a user.
     */
    void removeRoleFromUser(Long userId, Long roleId);

    /**
     * Assign a permission to a role.
     */
    PermissionDTO addPermissionToRole(Long roleId, Long permissionId);

    /**
     * Replace all permissions for a role.
     */
    List<PermissionDTO> updateRolePermissions(Long roleId, List<Long> permissionIds);

    /**
     * Remove a permission from a role.
     */
    void removePermissionFromRole(Long roleId, Long permissionId);
}