package com.tu.votingapp.services.impl;

import com.tu.votingapp.dto.general.PermissionDTO;
import com.tu.votingapp.dto.general.RoleDTO;
import com.tu.votingapp.dto.general.UserDTO;
import com.tu.votingapp.dto.request.DocumentRequestDTO;
import com.tu.votingapp.dto.response.DocumentResponseDTO;
import com.tu.votingapp.dto.response.UserProfileDetailsDTO;
import com.tu.votingapp.entities.*;
import com.tu.votingapp.repositories.interfaces.*;
import com.tu.votingapp.security.TokenProvider;
import com.tu.votingapp.services.interfaces.UserService;
import com.tu.votingapp.utils.mappers.UserMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final RoleRepository roleRepository;
    private final LocationRepository locationRepository; // <-- Add if Location needs fetching
    private final PermissionRepository permissionRepository;
    private final TokenProvider tokenProvider;
    private final UserMapper userMapper;
    private final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());

    @Override
    public String login(String egn, String documentNumber) {
        // 1. Find user by EGN
        UserEntity user = userRepository.findByEgn(egn)
                .orElseThrow(() -> {
                    logger.warning("Login failed: User not found for EGN=" + egn);
                    return new RuntimeException("Invalid credentials"); // Or AuthenticationException
                });

        // 2. Get the associated document *safely*
        DocumentEntity doc = user.getDocument(); // Fetch via JPA relationship

        // 3. Check if document exists and if the number matches
        if (doc == null) {
            logger.warning(() -> "Login failed for EGN=" + egn + ". User exists but has no associated document record.");
            throw new RuntimeException("Invalid credentials - Document not found"); // Or AuthenticationException
        }

        if (!doc.getNumber().equals(documentNumber)) {
            logger.warning(() -> "Login failed for EGN=" + egn + ". Document number mismatch.");
            throw new RuntimeException("Invalid credentials - Document mismatch"); // Or AuthenticationException
        }

        String token = tokenProvider.createToken(user.getId(), user.getRoles());
        logger.info(() -> "Login successful for EGN=" + egn);
        return token;
    }

    @Override
    @Transactional
    public DocumentResponseDTO addDocument(Long userId, DocumentRequestDTO req) {
        logger.info(() -> "Adding document for userId=" + userId);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        DocumentEntity doc = new DocumentEntity();
        doc.setNumber(req.getNumber());
        doc.setValidFrom(java.sql.Date.valueOf(req.getValidFrom()));
        doc.setValidTo(java.sql.Date.valueOf(req.getValidTo()));
        doc.setIssuer(req.getIssuer());
        doc.setGender(req.getGender());
        doc.setDateOfBirth(java.sql.Date.valueOf(req.getDateOfBirth()));
        doc.setPermanentAddress(req.getPermanentAddress());
        doc.setUser(user);
        DocumentEntity saved = documentRepository.save(doc);
        logger.info(() -> "Document added id=" + saved.getId() + " for userId=" + userId);
        return new DocumentResponseDTO(
                saved.getId(), saved.getNumber(),
                saved.getValidFrom().toLocalDate(),
                saved.getValidTo().toLocalDate(),
                saved.getIssuer(), saved.getGender(),
                saved.getDateOfBirth().toLocalDate(),
                saved.getPermanentAddress(), userId
        );
    }

    @Override
    public DocumentResponseDTO getDocument(Long userId) {
        logger.info(() -> "Fetching document for userId=" + userId);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        DocumentEntity doc = user.getDocument();
        DocumentResponseDTO dto = new DocumentResponseDTO(
                doc.getId(), doc.getNumber(),
                doc.getValidFrom().toLocalDate(),
                doc.getValidTo().toLocalDate(),
                doc.getIssuer(), doc.getGender(),
                doc.getDateOfBirth().toLocalDate(),
                doc.getPermanentAddress(), userId
        );
        logger.fine(() -> "Fetched document id=" + dto.getId());
        return dto;
    }

    @Override
    @Transactional
    public RoleDTO addRoleToUser(Long userId, Long roleId) {
        logger.info(() -> "Adding role " + roleId + " to user " + userId);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        RoleEntity role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleId));
        user.getRoles().add(role);
        userRepository.save(user);
        RoleDTO dto = new RoleDTO(role.getId(), role.getName(),
                role.getPermissions().stream()
                        .map(p -> new PermissionDTO(p.getId(), p.getName()))
                        .collect(Collectors.toList()));
        logger.info(() -> "Added role id=" + roleId + " to userId=" + userId);
        return dto;
    }

    @Override
    @Transactional
    public List<RoleDTO> updateUserRoles(Long userId, List<Long> roleIds) {
        logger.info(() -> "Updating roles for userId=" + userId);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        List<RoleEntity> roles = roleRepository.findAllById(roleIds);
        user.setRoles(roles);
        userRepository.save(user);
        List<RoleDTO> dtos = roles.stream()
                .map(r -> new RoleDTO(r.getId(), r.getName(),
                        r.getPermissions().stream()
                                .map(p -> new PermissionDTO(p.getId(), p.getName()))
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
        logger.info(() -> "Updated roles for userId=" + userId + ", count=" + dtos.size());
        return dtos;
    }

    @Override
    @Transactional
    public void removeRoleFromUser(Long userId, Long roleId) {
        logger.info(() -> "Removing role " + roleId + " from user " + userId);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        user.getRoles().removeIf(r -> r.getId().equals(roleId));
        userRepository.save(user);
        logger.info(() -> "Removed role id=" + roleId + " from userId=" + userId);
    }

    @Override
    @Transactional
    public PermissionDTO addPermissionToRole(Long roleId, Long permissionId) {
        logger.info(() -> "Adding permission " + permissionId + " to role " + roleId);
        RoleEntity role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleId));
        PermissionEntity perm = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found: " + permissionId));
        role.getPermissions().add(perm);
        roleRepository.save(role);
        logger.info(() -> "Added permission id=" + permissionId + " to roleId=" + roleId);
        return new PermissionDTO(perm.getId(), perm.getName());
    }

    @Override
    @Transactional
    public List<PermissionDTO> updateRolePermissions(Long roleId, List<Long> permissionIds) {
        logger.info(() -> "Updating permissions for roleId=" + roleId);
        RoleEntity role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleId));
        List<PermissionEntity> perms = permissionRepository.findAllById(permissionIds);
        role.setPermissions(perms);
        roleRepository.save(role);
        List<PermissionDTO> dtos = perms.stream()
                .map(p -> new PermissionDTO(p.getId(), p.getName()))
                .collect(Collectors.toList());
        logger.info(() -> "Updated permissions for roleId=" + roleId + ", count=" + dtos.size());
        return dtos;
    }

    @Override
    @Transactional
    public void removePermissionFromRole(Long roleId, Long permissionId) {
        logger.info(() -> "Removing permission " + permissionId + " from role " + roleId);
        RoleEntity role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleId));
        role.getPermissions().removeIf(p -> p.getId().equals(permissionId));
        roleRepository.save(role);
        logger.info(() -> "Removed permission id=" + permissionId + " from roleId=" + roleId);
    }

    @Override
    public UserDTO getById(Long id) {
        // This implementation should already exist, ensure it fetches and maps correctly
        logger.info(() -> "Fetching user profile id=" + id);
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + id)); // Use a specific exception
        UserDTO dto = userMapper.toDto(user);
        // Ensure password hash is NEVER returned from any get method
        dto.setPassword(null);
        logger.fine(() -> "Fetched user id=" + dto.getId() + ", email='" + dto.getEmail() + "'");
        return dto;
    }


    @Override
    @Transactional // Ensure atomicity
    public UserDTO create(UserDTO userDTO) { // Implement the create method from BaseService
        logger.info(() -> "Attempting to create user with EGN: " + userDTO.getEgn());

        // --- Basic Validation ---
        if (userDTO.getEgn() != null && userRepository.existsByEgn(userDTO.getEgn())) {
            logger.warning("User creation failed: EGN already exists - " + userDTO.getEgn());
            // Consider a more specific exception if available
            throw new DataIntegrityViolationException("User with EGN " + userDTO.getEgn() + " already exists.");
        }

        // --- Password Check ---
        if (userDTO.getPassword() == null || userDTO.getPassword().isBlank()) {
            logger.warning("User creation failed: Password cannot be empty for EGN " + userDTO.getEgn());
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        // --- Map DTO to Entity ---
        UserEntity userEntity = userMapper.toEntity(userDTO); // Use your mapper

        // --- IMPORTANT: Hash the password ---
        userEntity.setPassword(String.valueOf(userDTO.getPassword().hashCode()));

        // --- Handle Related Entities (Example: Location) ---
        if (userDTO.getLocationId() != null && userDTO.getLocationId().getId() != null) {
            LocationEntity location = locationRepository.findById(userDTO.getLocationId().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Location ID provided: " + userDTO.getLocationId().getId()));
            userEntity.setRegionId(location);
        } else {
            // Handle cases where location is required or can be null
            userEntity.setRegionId(null); // Or throw error if required
            logger.warning("Location ID not provided or invalid for user EGN: " + userDTO.getEgn());
        }

        // --- Handle Roles (Example: Assign a default 'USER' role) ---
        // You might want different logic here based on registration requirements
        RoleEntity defaultRole = roleRepository.findByName("ROLE_VOTER") // Assuming you have a standard USER role
                .orElseThrow(() -> new RuntimeException("Default ROLE_VOTER not found in database!"));
        userEntity.setRoles(Collections.singletonList(defaultRole));
        // Or fetch roles based on IDs if provided in DTO (careful with security)
        // if (userDTO.getRoles() != null && !userDTO.getRoles().isEmpty()) {
        //     List<Long> roleIds = userDTO.getRoles().stream().map(RoleDTO::getId).collect(Collectors.toList());
        //     List<RoleEntity> roles = roleRepository.findAllById(roleIds);
        //     userEntity.setRoles(new HashSet<>(roles));
        // }


        // --- Handle Document (Document is usually added *after* user creation) ---
        // You might want to create a user *without* a document initially,
        // and use the addDocument endpoint later.
        // If the document is required *at creation* and passed in the DTO,
        // you'd need to map and set it here, ensuring it's also saved (cascade or separate save).
        if (userDTO.getDocument() == null) {
            // Throw error if document is mandatory according to business logic/DB constraint
            logger.warning("User creation failed: Document data is required for EGN " + userDTO.getEgn());
            throw new IllegalArgumentException("Document data is required.");
        } else {
            // Map DocumentDTO to DocumentEntity
            // Assuming UserMapper or a separate DocumentMapper handles this nested mapping
            // If using ModelMapper directly, you might map it separately:
            // DocumentEntity documentEntity = modelMapper.map(userDTO.getDocument(), DocumentEntity.class);

            // Get the document entity potentially mapped by userMapper.toEntity
            DocumentEntity documentEntity = new DocumentEntity();
            documentEntity.setNumber(userDTO.getDocument().getNumber());
            // Convert LocalDate from DTO to java.sql.Date for Entity
            documentEntity.setValidFrom(java.sql.Date.valueOf(String.valueOf(userDTO.getDocument().getValidFrom())));
            documentEntity.setValidTo(java.sql.Date.valueOf(String.valueOf(userDTO.getDocument().getValidTo())));
            documentEntity.setIssuer(userDTO.getDocument().getIssuer());
            documentEntity.setGender(userDTO.getDocument().getGender());
            documentEntity.setDateOfBirth(java.sql.Date.valueOf(String.valueOf(userDTO.getDocument().getDateOfBirth())));
            documentEntity.setPermanentAddress(userDTO.getDocument().getPermanentAddress());
            // ID should be null as it's a new entity

            // --- Link the DocumentEntity TO the UserEntity ---
            userEntity.setDocument(documentEntity); // Associate the newly created Document with the User

            // --- Link the UserEntity TO the DocumentEntity (Bidirectional) ---
            documentEntity.setUser(userEntity);

            logger.fine(() -> "New Document entity created and linked for user EGN: " + userDTO.getEgn());

            // No need to explicitly save documentRepository.save(documentEntity); IF using CascadeType.ALL/PERSIST
        }

        userEntity.setId(null); // Ensure ID is null for JPA/Hibernate to generate one

        // --- Save the User ---
        try {
            UserEntity savedEntity = userRepository.save(userEntity);
            logger.info(() -> "User created successfully with ID: " + savedEntity.getId());

            // --- Map saved entity back to DTO for response ---
            UserDTO createdDto = userMapper.toDto(savedEntity);
            createdDto.setPassword(null); // NEVER return the password/hash

            return createdDto;
        } catch (DataIntegrityViolationException e) {
            logger.severe("Data integrity violation during user save: " + e.getMessage());
            // Rethrow a more specific exception or handle it
            throw new RuntimeException("Could not save user due to data constraint.", e);
        } catch (Exception e) {
            logger.severe("Unexpected error during user save: " + e.getMessage());
            throw new RuntimeException("An unexpected error occurred while creating the user.", e);
        }
    }

    // --- Implementation for update (from BaseService) ---
    @Override
    @Transactional
    public UserDTO update(Long id, UserDTO userDTO) {
        logger.info(() -> "Attempting to update user with ID: " + id);

        // Find existing user
        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id)); // Use a proper ResourceNotFoundException

        // --- Update fields (selectively) ---
        // Use the mapper but be careful about what it overwrites
        // It's often safer to update fields manually or use a dedicated update DTO

        // Example of selective update:
        if (userDTO.getName() != null) {
            existingUser.setName(userDTO.getName());
        }
        if (userDTO.getEmail() != null) {
            existingUser.setEmail(userDTO.getEmail());
        }
        if (userDTO.getPhone() != null) {
            existingUser.setPhone(userDTO.getPhone());
        }
        if (userDTO.getCurrentAddress() != null) {
            existingUser.setCurrentAddress(userDTO.getCurrentAddress());
        }
        // EGN usually shouldn't be updatable
        // Password should ONLY be updated via a separate "change password" endpoint/logic
        // Roles are updated via specific methods (updateUserRoles)
        // Document is updated via specific method (addDocument)

        // Handle Location update
        if (userDTO.getLocationId() != null && userDTO.getLocationId().getId() != null) {
            // Check if location is actually changing to avoid unnecessary DB lookup
            if (!existingUser.getRegionId().getId().equals(userDTO.getLocationId().getId())) {
                LocationEntity location = locationRepository.findById(userDTO.getLocationId().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid Location ID provided for update: " + userDTO.getLocationId().getId()));
                existingUser.setRegionId(location);
            }
        } else {
            existingUser.setRegionId(null); // Allow setting location to null if needed
        }


        // --- Save the updated user ---
        UserEntity updatedEntity = userRepository.save(existingUser);
        logger.info(() -> "User updated successfully with ID: " + updatedEntity.getId());

        // --- Map back to DTO ---
        UserDTO updatedDto = userMapper.toDto(updatedEntity);
        updatedDto.setPassword(null); // Ensure password is not returned

        return updatedDto;
    }

    // --- Implement other BaseService methods (findAll, delete) ---
    @Override
    public List<UserDTO> findAllUsers() {
        logger.info("Fetching all users");
        List<UserEntity> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toDto) // Map each entity
                .peek(dto -> dto.setPassword(null)) // Ensure password is null
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public void delete(Long id) {
        logger.info(() -> "Attempting to delete user with ID: " + id);
        if (!userRepository.existsById(id)) {
            logger.warning("User deletion failed: User not found with ID: " + id);
            throw new RuntimeException("User not found with ID: " + id); // Or specific exception
        }
        // Handle related data if necessary (e.g., orphan removal or explicit deletion)
        // Depending on Cascade settings, deleting the user might delete related documents/votes etc. Be careful.
        try {
            userRepository.deleteById(id);
            logger.info(() -> "User deleted successfully with ID: " + id);
        } catch (DataIntegrityViolationException e) {
            logger.severe("User deletion failed due to data integrity violation (e.g., related records exist): " + e.getMessage());
            throw new RuntimeException("Cannot delete user. Related data may exist.", e); // Provide clearer message
        } catch (Exception e) {
            logger.severe("Unexpected error during user deletion: " + e.getMessage());
            throw new RuntimeException("An unexpected error occurred while deleting the user.", e);
        }
    }

    @Override
    public List<UserDTO> findAll() {
        return List.of();
    }

    @Override
    public UserDTO findById(Long aLong) {
        return null;
    }

    @Override
    @Transactional // Add if lazy loading might occur for the document relationship
    public UserProfileDetailsDTO getUserProfileDetailsById(Long userId) {
        logger.info(() -> "Service: Fetching full profile details for userId=" + userId);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    // Log clearly if a user ID that should exist (from principal) isn't found
                    logger.log(Level.SEVERE, "Authenticated user ID not found in database: " + userId);
                    return new EntityNotFoundException("User not found: " + userId);
                });

        // Map user data using existing logic/mapper
        UserDTO userDto = userMapper.toDto(user);
        userDto.setPassword(null); // Ensure password hash is not included

        // Map document data (if exists)
        DocumentResponseDTO docDto = null;
        DocumentEntity doc = user.getDocument(); // Assumes UserEntity has a mapped relationship to DocumentEntity

        if (doc != null) {
            logger.fine(() -> "Found document id=" + doc.getId() + " for userId=" + userId);
            try {
                // Perform null checks on dates before conversion
                java.time.LocalDate validFromDate = (doc.getValidFrom() != null) ? doc.getValidFrom().toLocalDate() : null;
                java.time.LocalDate validToDate = (doc.getValidTo() != null) ? doc.getValidTo().toLocalDate() : null;
                java.time.LocalDate dateOfBirth = (doc.getDateOfBirth() != null) ? doc.getDateOfBirth().toLocalDate() : null;

                docDto = new DocumentResponseDTO(
                        doc.getId(),
                        doc.getNumber(),
                        validFromDate,
                        validToDate,
                        doc.getIssuer(),
                        doc.getGender(),
                        dateOfBirth,
                        doc.getPermanentAddress(),
                        userId
                );
            } catch (Exception e) {
                // Log potential errors during date conversion or mapping
                logger.log(Level.WARNING, "Error mapping DocumentEntity to DTO for userId=" + userId + ", docId=" + doc.getId(), e);
                // Decide if you want to return null docDto or throw an error
            }
        } else {
            logger.fine(() -> "No document found for userId=" + userId + " while fetching full details.");
        }

        // Combine into the new DTO
        return new UserProfileDetailsDTO(userDto, docDto);
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ((com.tu.votingapp.security.UserPrincipal) auth.getPrincipal()).getId();
    }
}
