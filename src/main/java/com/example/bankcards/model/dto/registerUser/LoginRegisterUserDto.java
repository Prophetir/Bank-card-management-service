package com.example.bankcards.model.dto.registerUser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginRegisterUserDto {

    private String email;

    private String password;
}
