package com.jun.smartlineup.user.service;

import com.jun.smartlineup.exception.NoExistUserException;
import com.jun.smartlineup.user.domain.Role;
import com.jun.smartlineup.user.domain.User;
import com.jun.smartlineup.user.dto.OAuth2UserImpl;
import com.jun.smartlineup.user.dto.OAuthAttributes;
import com.jun.smartlineup.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final DefaultOAuth2UserService oAuth2UserService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(
                registrationId,
                userNameAttributeName,
                oAuth2User.getAttributes()
        );

        User user = upsert(attributes);
        return new OAuth2UserImpl(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().name())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey()
        );
    }

    private User upsert(OAuthAttributes attributes) {
        User user = userRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture(), entity.getRole()))
                .orElseGet(() -> User.builder()
                        .name(attributes.getName())
                        .email(attributes.getEmail())
                        .picture(attributes.getPicture())
                        .role(Role.USER)
                        .build()
                );

        userRepository.save(user);
        return user;
    }

    public User convertUser(OAuth2User oAuth2User) {
        return userRepository.findByEmail(oAuth2User.getAttribute("email"))
                .orElseThrow(NoExistUserException::new);
    }
}
