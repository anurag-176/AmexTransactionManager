package com.amex.UserService.controller;

import com.amex.UserService.dto.UserRequestDTO;
import com.amex.UserService.dto.UserResponseDTO;
import com.amex.UserService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping
    public UserResponseDTO create(@RequestBody UserRequestDTO dto) {
        return service.createUser(dto);
    }

    @GetMapping("/{id}")
    public UserResponseDTO get(@PathVariable Long id) {
        return service.getUser(id);
    }

    @GetMapping
    public List<UserResponseDTO> getAll() {
        return service.getAllUsers();
    }

    @PutMapping("/{id}")
    public UserResponseDTO update(@PathVariable Long id, @RequestBody UserRequestDTO dto) {
        return service.updateUser(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.softDeleteUser(id);
    }
}