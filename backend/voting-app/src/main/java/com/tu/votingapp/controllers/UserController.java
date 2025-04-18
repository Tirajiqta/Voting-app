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

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest loginRequest) {
        String token = userService.login(
                loginRequest.getEgn(), loginRequest.getDocumentNumber());
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getProfile(
            @AuthenticationPrincipal UserPrincipal principal) {
        UserDTO dto = userService.getById(principal.getId());
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserDTO> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UserDTO userDTO) {
        userDTO.setId(principal.getId());
        UserDTO updated = userService.update(principal.getId(), userDTO);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/profile/document")
    public ResponseEntity<DocumentResponseDTO> addDocument(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody DocumentRequestDTO request) {
        DocumentResponseDTO response = userService.addDocument(
                principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/profile/document")
    public ResponseEntity<DocumentResponseDTO> getDocument(
            @AuthenticationPrincipal UserPrincipal principal) {
        DocumentResponseDTO response = userService.getDocument(principal.getId());
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }
}

