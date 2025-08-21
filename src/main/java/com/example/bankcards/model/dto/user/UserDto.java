package com.example.bankcards.model.dto.user;

import com.example.bankcards.util.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {

    private UUID id;

    private String name;

    private String email;

    private String password;

    private UserRole userRole;
}
