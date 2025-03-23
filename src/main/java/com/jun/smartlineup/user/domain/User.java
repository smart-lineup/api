package com.jun.smartlineup.user.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

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
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column
    private String picture;

    @Enumerated(EnumType.STRING)
    private Role role;

    // hash fix length is 60
    @Column(length = 60)
    private String password;

    @Column(nullable = false, name = "is_OAuth_login")
    @Builder.Default
    private Boolean isOAuthLogin = true;

    @Builder.Default
    private boolean isVerified = false;

    private String verificationToken;

    @Builder.Default
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @LastModifiedDate
    private LocalDateTime updatedAt = LocalDateTime.now();

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

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }
}
