package com.springbootstudy.sociallogin.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DynamicInsert // insert 시 null인 필드 제외
@DynamicUpdate // update 시 null인 필드 제외
public class Account extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int accountId;
    private String userIdentifier;
    private String email;
    private String firstName;
    private String lastName;
    @Column(columnDefinition = "varchar(10) default 'ROLE_USER'")
    private String authority;
    private String password;
    @Column(columnDefinition = "boolean default true")
    private Boolean isActive = true;

    @Builder
    public Account(String userIdentifier, String email, String firstName, String lastName, String authority, String password, Boolean isActive) {
        this.userIdentifier = userIdentifier;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.authority = authority;
        this.password = password;
        this.isActive = isActive;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updateFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void updateLastName(String lastName) {
        this.lastName = lastName;
    }
}
