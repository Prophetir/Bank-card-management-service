package com.example.bankcards.controller.registerUserController.registerUserControllerImpl;

import com.example.bankcards.controller.registerUserController.RegisterUserController;
import com.example.bankcards.model.dto.response.TransactionResponse;
import com.example.bankcards.model.dto.registerUser.RegisterUserDto;
import com.example.bankcards.model.dto.registerUser.UpdateRegisterUserDto;
import com.example.bankcards.security.JwtTokenService;
import com.example.bankcards.service.registerUserService.RegisterUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class RegisterUserControllerImpl implements RegisterUserController {

    private final JwtTokenService tokenService;

    private final RegisterUserService regUserService;

    @GetMapping("/get")
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public RegisterUserDto getRegisterUser(@RequestHeader("Authorization") String tokenHeader) {
        return regUserService.getRegisterUser(UUID.fromString(tokenService.decoderToken(tokenHeader).getSubject()));
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public TransactionResponse updateRegisterUser(@RequestHeader("Authorization") String tokenHeader, UpdateRegisterUserDto updateUserDto) {
        return regUserService.updateRegisterUser(
                UUID.fromString(tokenService.decoderToken(tokenHeader).getSubject()), updateUserDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public TransactionResponse deleteRegisterUser(@RequestHeader("Authorization") String tokenHeader) {
        return regUserService.deleteRegisterUser(UUID.fromString(tokenService.decoderToken(tokenHeader).getSubject()));
    }

    @PostMapping("/soft-delete")
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public TransactionResponse softDeleteRegisterUser(@RequestHeader("Authorization") String tokenHeader) {
        return regUserService.softDeleteRegisterUser(UUID.fromString(tokenService.decoderToken(tokenHeader).getSubject()));
    }
}
