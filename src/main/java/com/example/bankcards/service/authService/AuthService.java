package com.example.bankcards.service.authService;

import com.example.bankcards.model.dto.jwt.JwtTokenDto;
import com.example.bankcards.model.dto.registerUser.LoginRegisterUserDto;
import com.example.bankcards.model.dto.registerUser.RegisterFormUserDto;

public interface AuthService {

    JwtTokenDto register(RegisterFormUserDto registerDto);

    JwtTokenDto login(LoginRegisterUserDto loginRegisterUserDto);
}
