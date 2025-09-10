package com.example.bankcards.controller.authController.authControllerImpl;

import com.example.bankcards.controller.authController.AuthController;
import com.example.bankcards.model.dto.jwt.JwtTokenDto;
import com.example.bankcards.model.dto.registerUser.LoginRegisterUserDto;
import com.example.bankcards.model.dto.registerUser.RegisterFormUserDto;
import com.example.bankcards.service.authService.AuthService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthControllerImpl implements AuthController {

    @Resource
    private AuthService service;

    @PostMapping("/reg")
    @Override
    public JwtTokenDto registrationUser(@RequestBody RegisterFormUserDto registerDto) {
        return service.register(registerDto);
    }

    @PostMapping("/log")
    @Override
    public JwtTokenDto loginUser(@RequestBody LoginRegisterUserDto loginDto) {
        return service.login(loginDto);
    }
}
