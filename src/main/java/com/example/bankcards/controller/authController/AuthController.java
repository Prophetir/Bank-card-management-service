package com.example.bankcards.controller.authController;

import com.example.bankcards.model.dto.jwt.JwtTokenDto;
import com.example.bankcards.model.dto.user.LoginUserDto;
import com.example.bankcards.model.dto.user.RegisterUserDto;

public interface AuthController {

    JwtTokenDto registrationUser(RegisterUserDto registerDto);

    JwtTokenDto loginUser(LoginUserDto loginDto);
}
