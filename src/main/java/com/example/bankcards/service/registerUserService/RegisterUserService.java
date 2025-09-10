package com.example.bankcards.service.registerUserService;

import com.example.bankcards.model.dto.response.TransactionResponse;
import com.example.bankcards.model.dto.registerUser.RegisterUserDto;
import com.example.bankcards.model.dto.registerUser.UpdateRegisterUserDto;

import java.util.UUID;

public interface RegisterUserService {

    RegisterUserDto getRegisterUser(UUID id);

    TransactionResponse updateRegisterUser(UUID id, UpdateRegisterUserDto updateUserDto);

    TransactionResponse deleteRegisterUser(UUID id);

    TransactionResponse softDeleteRegisterUser(UUID id);
}
