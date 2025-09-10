package com.example.bankcards.model.dto.registerUser;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterFormUserDto {

    private String name;

    private String email;

    private String password;
}
