package com.jun.smartlineup.user.utils;

import com.jun.smartlineup.common.exception.NoExistUserException;
import com.jun.smartlineup.user.domain.User;
import com.jun.smartlineup.user.dto.CustomUserDetails;
import com.jun.smartlineup.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class UserUtil {
    public static User ConvertUser(UserRepository userRepository, CustomUserDetails userDetails) {
        Optional<User> OptionalUser = userRepository.findByEmail(userDetails.getUsername());
        return OptionalUser.orElseThrow(NoExistUserException::new);
    }

    public static CustomUserDetails getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (CustomUserDetails) authentication.getPrincipal();
    }
}
