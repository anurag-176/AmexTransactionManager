package com.amex.AccountService.controller;

import com.amex.AccountService.dto.AccountRequestDTO;
import com.amex.AccountService.dto.AccountResponseDTO;
import com.amex.AccountService.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService service;

    @PostMapping
    public AccountResponseDTO create(@RequestBody AccountRequestDTO dto) {
        return service.createAccount(dto);
    }

    @GetMapping("/{id}")
    public AccountResponseDTO get(@PathVariable Long id) {
        return service.getAccount(id);
    }

    @GetMapping
    public List<AccountResponseDTO> getAll() {
        return service.getAllAccounts();
    }

    @PutMapping("/{id}")
    public AccountResponseDTO update(@PathVariable Long id, @RequestBody AccountRequestDTO dto) {
        return service.updateAccount(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.softDeleteAccount(id);
    }
}
