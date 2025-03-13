package com.jun.smartlineup.user.dto;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;

public class CustomUserDetails implements UserDetails {
    private final String email;
    @Getter
    private final String name;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(String email, String name, Collection<? extends GrantedAuthority> authorities) {
        this.email = email;
        this.name = name;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return ""; // 비밀번호는 여기서 필요하지 않다면 빈 문자열로 처리하거나 실제 값을 넣습니다.
    }

    @Override
    public String getUsername() {
        return email; // username은 이메일로 사용
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
