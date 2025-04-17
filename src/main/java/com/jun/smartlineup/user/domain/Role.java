package com.jun.smartlineup.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum Role {
    FREE("FREE", "Basic user"),
    PREMIUM("PREMIUM", "Pay user"),
    BETA("BETA", "Beta Tester");

    private final String key;
    private final String title;

    public static List<Role> getAll() {
        return Arrays.stream(Role.values()).toList();
    }

    public static Role fromString(String s) {
        if (s.equals(Role.PREMIUM.key)) {
            return Role.PREMIUM;
        }
        if (s.equals(Role.BETA.key)) {
            return Role.BETA;
        }
        return Role.FREE;
    }

    public boolean isPaid() {
        return this.key.equals("PREMIUM") || this.key.equals("BETA");
    }
}
