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
}
