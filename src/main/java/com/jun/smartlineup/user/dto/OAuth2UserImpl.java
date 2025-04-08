package com.jun.smartlineup.user.dto;

import com.jun.smartlineup.user.domain.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

public class OAuth2UserImpl extends DefaultOAuth2User {
    @Getter
    private User user;
    public OAuth2UserImpl(Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes, String nameAttributeKey, User user) {
        super(authorities, attributes, nameAttributeKey);
        this.user = user;
    }
}
