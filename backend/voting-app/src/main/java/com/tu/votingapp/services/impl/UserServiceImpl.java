package com.tu.votingapp.services.impl;

import com.tu.votingapp.dto.general.UserDTO;
import com.tu.votingapp.entities.UserEntity;
import com.tu.votingapp.repositories.interfaces.UserRepository;
import com.tu.votingapp.services.interfaces.UserService;
import com.tu.votingapp.utils.mappers.UserMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends BaseServiceImpl<UserEntity, UserDTO, Long> implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    // Constructor injection
    public UserServiceImpl(UserRepository userRepository, UserMapper mapper) {
        this.userRepository = userRepository;
        this.userMapper = mapper;
    }

    @Override
    protected UserEntity toEntity(UserDTO dto) {
        return userMapper.toEntity(dto);
    }

    @Override
    protected UserDTO toDto(UserEntity entity) {
        return userMapper.toDto(entity);
    }

    @Override
    public String login(String egn, String documentIdentifier) {
        // Implement login logic here
        return "";
    }

    @Override
    public UserDTO getById(Long id) {
        // For example, return user or null if not found
        UserEntity entity = userRepository.findById(id).orElse(null);
        return userMapper.toDto(entity);
    }

    @Override
    protected JpaRepository<UserEntity, Long> getRepository() {
        return userRepository;
    }



    @Override
    public UserDTO update(Long id, UserDTO userDTO) {
        // Implement update logic here; for example:
        if (!userRepository.existsById(id)) {
            return null;
        }
        // Additional logic for mapping and updating fields can be added here
        userRepository.save(userMapper.toEntity(userDTO));
        return userDTO;
    }
}
