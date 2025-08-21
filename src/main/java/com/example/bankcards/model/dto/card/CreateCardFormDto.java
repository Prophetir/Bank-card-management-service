package com.example.bankcards.model.dto.card;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateCardFormDto {

    private String fullName;

    private String birthday;

    private String phoneNumber;

    private String email;
}
