package com.tu.votingapp.services.impl;

import com.tu.votingapp.dto.general.PermissionDTO;
import com.tu.votingapp.dto.general.RoleDTO;
import com.tu.votingapp.dto.general.UserDTO;
import com.tu.votingapp.dto.request.DocumentRequestDTO;
import com.tu.votingapp.dto.response.DocumentResponseDTO;
import com.tu.votingapp.entities.DocumentEntity;
import com.tu.votingapp.entities.PermissionEntity;
import com.tu.votingapp.entities.RoleEntity;
import com.tu.votingapp.entities.UserEntity;
import com.tu.votingapp.repositories.interfaces.DocumentRepository;
import com.tu.votingapp.repositories.interfaces.PermissionRepository;
import com.tu.votingapp.repositories.interfaces.RoleRepository;
import com.tu.votingapp.repositories.interfaces.UserRepository;
import com.tu.votingapp.security.TokenProvider;
import com.tu.votingapp.services.interfaces.UserService;
import com.tu.votingapp.utils.mappers.UserMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final TokenProvider tokenProvider;

    @Override
    public String login(String egn, String documentNumber) {
        UserEntity user = userRepository.findByEgn(egn)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        DocumentEntity doc = documentRepository.findById(user.getDocument().getId())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if (!doc.getNumber().equals(documentNumber)) {
            return null;
        }
        return tokenProvider.createToken(user.getId(), user.getRoles());
    }



    @Override
    @Transactional
    public DocumentResponseDTO addDocument(Long userId, DocumentRequestDTO req) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
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
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        DocumentEntity doc = user.getDocument();
        return new DocumentResponseDTO(
                doc.getId(), doc.getNumber(),
                doc.getValidFrom().toLocalDate(),
                doc.getValidTo().toLocalDate(),
                doc.getIssuer(), doc.getGender(),
                doc.getDateOfBirth().toLocalDate(),
                doc.getPermanentAddress(), userId
        );
    }

    @Override
    @Transactional
    public RoleDTO addRoleToUser(Long userId, Long roleId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        RoleEntity role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.getRoles().add(role);
        userRepository.save(user);
        return new RoleDTO(role.getId(), role.getName(),
                role.getPermissions().stream()
                        .map(p -> new PermissionDTO(p.getId(), p.getName()))
                        .collect(Collectors.toList()));
    }

    @Override
    @Transactional
    public List<RoleDTO> updateUserRoles(Long userId, List<Long> roleIds) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<RoleEntity> roles = roleRepository.findAllById(roleIds);
        user.setRoles(roles);
        userRepository.save(user);
        return roles.stream()
                .map(r -> new RoleDTO(r.getId(), r.getName(),
                        r.getPermissions().stream()
                                .map(p -> new PermissionDTO(p.getId(), p.getName()))
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeRoleFromUser(Long userId, Long roleId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.getRoles().removeIf(r -> r.getId().equals(roleId));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public PermissionDTO addPermissionToRole(Long roleId, Long permissionId) {
        RoleEntity role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        PermissionEntity perm = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found"));
        role.getPermissions().add(perm);
        roleRepository.save(role);
        return new PermissionDTO(perm.getId(), perm.getName());
    }

    @Override
    @Transactional
    public List<PermissionDTO> updateRolePermissions(Long roleId, List<Long> permissionIds) {
        RoleEntity role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        List<PermissionEntity> perms = permissionRepository.findAllById(permissionIds);
        role.setPermissions(perms);
        roleRepository.save(role);
        return perms.stream()
                .map(p -> new PermissionDTO(p.getId(), p.getName()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removePermissionFromRole(Long roleId, Long permissionId) {
        RoleEntity role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        role.getPermissions().removeIf(p -> p.getId().equals(permissionId));
        roleRepository.save(role);
    }


    @Override
    public UserDTO getById(Long id) {
        return null;
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
    public UserDTO create(UserDTO dto) {
        return null;
    }

    @Override
    public UserDTO update(Long id, UserDTO userDTO) {
        return null;
    }

    @Override
    public void delete(Long aLong) {

    }
}
