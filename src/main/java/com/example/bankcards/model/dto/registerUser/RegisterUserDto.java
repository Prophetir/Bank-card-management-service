package com.example.bankcards.model.dto.registerUser;

import com.example.bankcards.util.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class RegisterUserDto {

    private UUID id;

    private String name;

    private String email;

    private String password;

    private UserRole userRole;
}
