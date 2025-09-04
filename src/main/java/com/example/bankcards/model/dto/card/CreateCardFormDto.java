package com.example.bankcards.model.dto.card;

import lombok.*;

@Getter
@Setter
@Data
public class CreateCardFormDto {

    private String fullName;

    private String gender;

    private String birthday;

    private String address;

    private String documentType;

    private String passportNumber;

    private String passportSeries;

    private String citizenship;
}
