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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final TokenProvider tokenProvider;
    private final UserMapper userMapper;
    private final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());

    @Override
    public String login(String egn, String documentNumber) {
        logger.info(() -> "Login attempt for EGN=" + egn);
        UserEntity user = userRepository.findByEgn(egn)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        DocumentEntity doc = documentRepository.findById(user.getDocument().getId())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if (!doc.getNumber().equals(documentNumber)) {
            logger.warning(() -> "Login failed for EGN=" + egn);
            return null;
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
        logger.info(() -> "Fetching user profile id=" + id);
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        UserDTO dto = userMapper.toDto(user);
        logger.fine(() -> "Fetched user id=" + dto.getId() + ", email='" + dto.getEmail() + "'");
        return dto;
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
