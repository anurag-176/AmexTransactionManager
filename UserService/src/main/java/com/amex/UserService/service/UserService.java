package com.amex.UserService.service;

import com.amex.UserService.dto.UserRequestDTO;
import com.amex.UserService.dto.UserResponseDTO;
import com.amex.UserService.exception.ResourceNotFoundException;
import com.amex.UserService.model.User;
import com.amex.UserService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    public UserResponseDTO createUser(UserRequestDTO dto) {
        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
        return toResponseDTO(repository.save(user));
    }

    public UserResponseDTO getUser(Long id) {
        User user = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return toResponseDTO(user);
    }

    public List<UserResponseDTO> getAllUsers() {
        return repository.findAllByDeletedFalse()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public UserResponseDTO updateUser(Long id, UserRequestDTO dto) {
        User user = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        return toResponseDTO(repository.save(user));
    }

    public void softDeleteUser(Long id) {
        User user = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        user.setDeleted(true);
        repository.save(user);
    }

    private UserResponseDTO toResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .deleted(user.isDeleted())
                .build();
    }
}