package com.jun.smartlineup.config;

import com.jun.smartlineup.utils.TimezoneUtil;
import com.jun.smartlineup.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDateTime;

@ControllerAdvice
@RequiredArgsConstructor
public class TimeZoneAdvice {

//    private final UserService userService;

    @ModelAttribute
    public void convertTimeZone(Model model) {
        String timeZone = getUserTimeZoneFromRequest();

        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime convertedTime = TimezoneUtil.convertToUserTimeZoneLocalDateTime(timeZone, localDateTime);

        model.addAttribute("convertedTime", convertedTime);
    }

    private String getUserTimeZoneFromRequest() {
        return "Asia/Seoul";
    }
}
