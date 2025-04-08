package com.tu.votingapp.controllers;

import com.tu.votingapp.dto.general.UserDTO;
import com.tu.votingapp.dto.request.LoginRequest;
import com.tu.votingapp.dto.response.LoginResponse;
import com.tu.votingapp.services.interfaces.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/users")
public class UserController {
    private final UserService userService;

    // Inject the UserService via constructor injection
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        // Delegate authentication to the service layer
        String token = userService.login(loginRequest.getEgn(), loginRequest.getDocumentIdentifier());
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        return ResponseEntity.ok(new LoginResponse(token));
    }

    // Retrieve a user's profile
    @GetMapping("/profile/{id}")
    public ResponseEntity<UserDTO> getProfile(@PathVariable Long id) {
        UserDTO userDTO = userService.getById(id);
        if (userDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userDTO);
    }

    // Update a user's profile
    @PutMapping("/profile/{id}")
    public ResponseEntity<UserDTO> updateProfile(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        UserDTO updatedUser = userService.update(id, userDTO);
        if (updatedUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedUser);
    }
}
