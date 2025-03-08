package com.jun.smartlineup.user.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
@Builder
@AllArgsConstructor
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column
    private String picture;

    @Enumerated(EnumType.STRING)
    private Role role;

    /**
     * It's for sing up directly from user
     */
    private String password;

    @Column(nullable = false)
    private Boolean isOAuthLogin = true;

    @Builder.Default
    private boolean isVerified = false;

    private String verificationToken;

    public User(String name, String email, String picture, Role role) {
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.role = role;
    }

    public User update(String name, String picture, Role role) {
        this.name = name;
        this.picture = picture;
        this.role = role;

        return this;
    }

    public User updateRole(Role role) {
        this.role = role;

        return this;
    }

    public void SuccessVerified() {
        this.isVerified = true;
    }
}
