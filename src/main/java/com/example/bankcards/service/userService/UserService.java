package com.example.bankcards.service.userService;

import com.example.bankcards.model.dto.response.TransactionResponse;
import com.example.bankcards.model.dto.user.RegisterUserDto;

import java.util.UUID;

public interface UserService {

    RegisterUserDto getUser(UUID id);

    TransactionResponse deleteUser(UUID id);

    TransactionResponse softDeleteUser(UUID id);
}
