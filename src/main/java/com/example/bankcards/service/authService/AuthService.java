package com.example.bankcards.service.authService;

import com.example.bankcards.model.dto.jwt.JwtTokenDto;
import com.example.bankcards.model.dto.user.LoginUserDto;
import com.example.bankcards.model.dto.user.RegisterUserDto;

public interface AuthService {

    JwtTokenDto register(RegisterUserDto registerDto);

    JwtTokenDto login(LoginUserDto loginUserDto);
}
