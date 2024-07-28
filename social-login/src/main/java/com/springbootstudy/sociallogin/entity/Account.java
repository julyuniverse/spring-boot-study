package com.springbootstudy.sociallogin.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int accountId;
    private String userIdentifier;
    private String email;
    private String firstName;
    private String lastName;

    @Builder
    public Account(String userIdentifier, String email, String firstName, String lastName) {
        this.userIdentifier = userIdentifier;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
