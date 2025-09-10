package com.example.bankcards.model.dto.registerUser;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRegisterUserDto {

    private String email;

    private String password;
}
