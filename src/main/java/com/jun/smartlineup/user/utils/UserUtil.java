package com.jun.smartlineup.user.utils;

import com.jun.smartlineup.exception.NoExistUserException;
import com.jun.smartlineup.user.domain.User;
import com.jun.smartlineup.user.dto.CustomUserDetails;
import com.jun.smartlineup.user.repository.UserRepository;

import java.util.Optional;

public class UserUtil {
    public static User ConvertUser(UserRepository userRepository, CustomUserDetails userDetails) {
        Optional<User> OptionalUser = userRepository.findByEmail(userDetails.getUsername());
        return OptionalUser.orElseThrow(NoExistUserException::new);
    }
}
