package com.example.bankcards.model.entity;

import com.example.bankcards.util.DocumentType;
import com.example.bankcards.util.GenderType;
import com.example.bankcards.util.encryption.AesGcmEncryptor;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@Table(name = "users_profiles")
@NoArgsConstructor
@AllArgsConstructor
public class ProfileEntity {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(mappedBy = "userProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private RegisterUserEntity relatedUser;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CardEntity> cards;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "gender", nullable = false)
    private GenderType gender;

    @Column(name = "birthday", nullable = false)
    private String birthday;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "document_type", nullable = false)
    private DocumentType documentType;

    @Column(name = "password_number", nullable = false)
    private String passportNumber;

    @Convert(converter = AesGcmEncryptor.class)
    @Column(name = "password_series", nullable = false)
    private String passportSeries;

    @Column(name = "issue_by", nullable = false)
    private String issueBy;

    @Convert(converter = AesGcmEncryptor.class)
    @Column(name = "address", nullable = false)
    private String address;

    @Convert(converter = AesGcmEncryptor.class)
    @Column(name = "tax_id", nullable = false)
    private String taxId;

    @Column(name = "citizenship", nullable = false)
    private String citizenship;

    @Convert(converter = AesGcmEncryptor.class)
    @Column(name = "employment_info", nullable = false)
    private String employmentInfo;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "registration_date", nullable = false)
    private LocalDate registrationDate;

    @Column(name = "soft-delete", nullable = false)
    private boolean softDelete = false;
}
