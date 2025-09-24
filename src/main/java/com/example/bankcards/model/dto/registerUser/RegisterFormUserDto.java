package com.example.bankcards.model.dto.registerUser;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RegisterFormUserDto {

    private String name;

    private String phone;

    private String email;

    private String password;
}
