package com.example.bankcards.controller.authController;

import com.example.bankcards.model.dto.jwt.JwtTokenDto;
import com.example.bankcards.model.dto.registerUser.LoginRegisterUserDto;
import com.example.bankcards.model.dto.registerUser.RegisterFormUserDto;

public interface AuthController {

    JwtTokenDto registrationUser(RegisterFormUserDto registerDto);

    JwtTokenDto loginUser(LoginRegisterUserDto loginDto);
}
