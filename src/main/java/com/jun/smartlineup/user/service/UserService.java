package com.jun.smartlineup.user.service;

import com.jun.smartlineup.config.auth.JwtTokenProvider;
import com.jun.smartlineup.config.email.EmailService;
import com.jun.smartlineup.exception.EmailAlreadyExistException;
import com.jun.smartlineup.exception.NoExistUserException;
import com.jun.smartlineup.exception.NotVerifyUserException;
import com.jun.smartlineup.user.domain.Role;
import com.jun.smartlineup.user.domain.User;
import com.jun.smartlineup.user.dto.*;
import com.jun.smartlineup.user.repository.UserRepository;
import com.jun.smartlineup.user.utils.UserUtil;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final DefaultOAuth2UserService oAuth2UserService;
    private final EmailService emailService;
    private final JwtTokenProvider jwtTokenProvider;

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

        User user = Oauth2Upsert(attributes);
        return new OAuth2UserImpl(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().name())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey()
        );
    }

    private User Oauth2Upsert(OAuthAttributes attributes) {
        User user = userRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture(), entity.getRole()))
                .orElseGet(() -> User.builder()
                        .name(attributes.getName())
                        .email(attributes.getEmail())
                        .picture(attributes.getPicture())
                        .role(Role.USER)
                        .isOAuthLogin(true)
                        .isVerified(true)
                        .uuid(UUID.randomUUID().toString())
                        .build()
                );

        userRepository.save(user);
        return user;
    }

    public User convertUser(OAuth2User oAuth2User) {
        return userRepository.findByEmail(oAuth2User.getAttribute("email"))
                .orElseThrow(NoExistUserException::new);
    }

    public void signup(SignupRequestDto signupDto) throws MessagingException {
        if (userRepository.findByEmail(signupDto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistException();
        }

        String verificationToken = UUID.randomUUID().toString();
        User user = User.builder()
                .email(signupDto.getEmail())
                .password(encodePassword(signupDto.getPassword()))
                .name(signupDto.getName())
                .isOAuthLogin(false)
                .role(Role.USER)
                .isVerified(false)
                .verificationToken(verificationToken)
                .uuid(UUID.randomUUID().toString())
                .build();

        userRepository.save(user);
        emailService.sendVerificationEmail(user.getEmail(), verificationToken);
    }

    public void verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("No exist token"));

        if (user.isVerified()) {
            return;
        }

        user.SuccessVerified();
    }

    public ResponseCookie login(LoginRequestDto loginRequestDto) {
        Optional<User> userOptional = userRepository.findByEmail(loginRequestDto.getEmail());
        User user = userOptional.orElseThrow(NoExistUserException::new);

        if (!new BCryptPasswordEncoder().matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new NoExistUserException();
        }
        if (!user.isVerified()) {
            throw new NotVerifyUserException();
        }

        return jwtTokenProvider.getJwtCookie(user);
    }

    private String encodePassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

    public User findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.orElseThrow(NoExistUserException::new);
    }

    public UserUuidResponseDto uuid(CustomUserDetails userDetails) {
        User user = UserUtil.ConvertUser(userRepository, userDetails);
        return new UserUuidResponseDto(user.getUuid());
    }
}
