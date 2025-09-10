package com.example.bankcards.controller.registerUserController;

import com.example.bankcards.model.dto.response.TransactionResponse;
import com.example.bankcards.model.dto.registerUser.RegisterUserDto;
import com.example.bankcards.model.dto.registerUser.UpdateRegisterUserDto;

public interface RegisterUserController {

    RegisterUserDto getRegisterUser(String tokenHeader);

    TransactionResponse updateRegisterUser(String tokenHeader, UpdateRegisterUserDto updateUserDto);

    TransactionResponse deleteRegisterUser(String tokenHeader);

    TransactionResponse softDeleteRegisterUser(String tokenHeader);
}
