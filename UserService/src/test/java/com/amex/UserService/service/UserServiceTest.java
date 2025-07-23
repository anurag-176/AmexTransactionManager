package com.amex.UserService.service;


import com.amex.UserService.dto.UserRequestDTO;
import com.amex.UserService.dto.UserResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureMockMvc
public class UserServiceTest {

    @Autowired
    private UserService service;


    @Test
    void addUser() {
        UserRequestDTO request = new UserRequestDTO();
        request.setName("Anurag");
        request.setEmail("anurag@example.com");

        // Act
        UserResponseDTO saved = service.createUser(request);

        assertNotNull(saved);
        assertNotNull(saved.getId());

    }
}
