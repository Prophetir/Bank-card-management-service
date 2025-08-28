package com.example.bankcards.model.dto.card;

import com.example.bankcards.util.DocumentType;
import com.example.bankcards.util.GenderType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class CreateCardFormDto {

    private String fullName;

    private String email;

    private String password;

    private GenderType gender;

    private String birthday;

    private String phoneNumber;

    private DocumentType documentType;

    private String passportNumber;

    private String passportSeries;

    private String issueBy;

    private String address;

    private String taxId;

    private String citizenship;

    private String employmentInfo;
}
