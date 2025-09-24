package com.example.bankcards.model.dto.registerUser;

import com.example.bankcards.util.UserRole;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserDto {

    private UUID id;

    private String name;

    private String email;

    private String password;

    private UserRole userRole;
}
