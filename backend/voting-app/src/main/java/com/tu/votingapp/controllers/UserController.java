package com.tu.votingapp.controllers;

import com.tu.votingapp.dto.general.UserDTO;
import com.tu.votingapp.dto.request.DocumentRequestDTO;
import com.tu.votingapp.dto.request.LoginRequest;
import com.tu.votingapp.dto.response.DocumentResponseDTO;
import com.tu.votingapp.dto.response.LoginResponse;
import com.tu.votingapp.security.UserPrincipal;
import com.tu.votingapp.services.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.logging.Logger;

/**
 * Controller for user authentication and profile/document management.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final Logger logger = Logger.getLogger(UserController.class.getName());

    /**
     * Authenticate user and return JWT/opaque token.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest loginRequest) {
        logger.info(() -> "Login attempt for EGN=" + loginRequest.getEgn());
        String token = userService.login(
                loginRequest.getEgn(), loginRequest.getDocumentNumber());
        if (token == null) {
            logger.warning(() -> "Login failed for EGN=" + loginRequest.getEgn());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        logger.info(() -> "Login successful for EGN=" + loginRequest.getEgn());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    /**
     * Retrieve current user's profile.
     */
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getProfile(
            @AuthenticationPrincipal UserPrincipal principal) {
        Long userId = principal.getId();
        logger.info(() -> "Fetching profile for userId=" + userId);
        UserDTO dto = userService.getById(userId);
        logger.fine(() -> "Profile retrieved for userId=" + userId + ", email=" + dto.getEmail());
        return ResponseEntity.ok(dto);
    }

    /**
     * Update current user's profile.
     */
    @PutMapping("/profile")
    public ResponseEntity<UserDTO> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UserDTO userDTO) {
        Long userId = principal.getId();
        logger.info(() -> "Updating profile for userId=" + userId);
        userDTO.setId(userId);
        UserDTO updated = userService.update(userId, userDTO);
        if (updated == null) {
            logger.warning(() -> "Update failed for userId=" + userId);
            return ResponseEntity.notFound().build();
        }
        logger.info(() -> "Profile updated for userId=" + userId);
        return ResponseEntity.ok(updated);
    }

    /**
     * Add or update document for current user.
     */
    @PostMapping("/profile/document")
    public ResponseEntity<DocumentResponseDTO> addDocument(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody DocumentRequestDTO request) {
        Long userId = principal.getId();
        logger.info(() -> "Adding document for userId=" + userId);
        DocumentResponseDTO response = userService.addDocument(userId, request);
        logger.info(() -> "Document added id=" + response.getId() + " for userId=" + userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieve document for current user.
     */
    @GetMapping("/profile/document")
    public ResponseEntity<DocumentResponseDTO> getDocument(
            @AuthenticationPrincipal UserPrincipal principal) {
        Long userId = principal.getId();
        logger.info(() -> "Fetching document for userId=" + userId);
        DocumentResponseDTO response = userService.getDocument(userId);
        if (response == null) {
            logger.fine(() -> "No document found for userId=" + userId);
            return ResponseEntity.notFound().build();
        }
        logger.fine(() -> "Document retrieved id=" + response.getId() + " for userId=" + userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping // Maps to POST /api/users
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        logger.info(() -> "Received request to create user with EGN: " + userDTO.getEgn());

        // Basic security check: Don't allow setting ID or roles via this endpoint typically
        if (userDTO.getId() != null) {
            logger.warning("Attempt to create user with pre-set ID rejected. EGN: " + userDTO.getEgn());
            // Consider returning BadRequest or just ignoring the ID
            userDTO.setId(null);
        }
        // Consider clearing roles if they shouldn't be set during public registration
        // userDTO.setRoles(null);


        try {
            UserDTO createdUser = userService.create(userDTO);

            // Build the URI for the 'Location' header (good REST practice)
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest() // Gets base path /api/users
                    .path("/{id}")        // Appends /{id}
                    .buildAndExpand(createdUser.getId()) // Replaces {id} with the actual new ID
                    .toUri();

            logger.info(() -> "User created successfully. ID: " + createdUser.getId() + ", Location: " + location);

            // Return 201 Created status, Location header, and the created user DTO (without password)
            return ResponseEntity.created(location).body(createdUser);

        } catch (IllegalArgumentException e) {
            // Catch exceptions thrown by the service (like duplicate user)
            logger.warning("User creation failed: " + e.getMessage());
            // Return 400 Bad Request or 409 Conflict depending on the error
            return ResponseEntity.badRequest().body(null); // Or return an ErrorResponseDTO
        } catch (Exception e) {
            // Catch unexpected errors
            logger.severe("Unexpected error during user creation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Or ErrorResponseDTO
        }
    }
}
