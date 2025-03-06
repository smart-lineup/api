package com.jun.smartlineup.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER("ROLE_USER", "Basic user"),
    VIP("ROLE_VIP", "Pay user");

    private final String key;
    private final String title;

    public static List<Role> getAll() {
        return Arrays.stream(Role.values()).toList();
    }
}
