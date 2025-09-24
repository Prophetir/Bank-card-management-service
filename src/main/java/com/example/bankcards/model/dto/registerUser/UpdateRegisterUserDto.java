package com.example.bankcards.model.dto.registerUser;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateRegisterUserDto {

    private String name;

    private String email;

    private String phone;

    private String password;
}
