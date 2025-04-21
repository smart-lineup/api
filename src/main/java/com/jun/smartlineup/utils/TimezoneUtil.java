package com.jun.smartlineup.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimezoneUtil {
    public static LocalDateTime convertToUserTimeZoneLocalDateTime(String timeZone, LocalDateTime localDateTime) {
        // UTC로부터 타임존을 고려하여 ZonedDateTime 변환
        ZonedDateTime utcZonedDateTime = localDateTime.atZone(ZoneId.of("UTC"));
        ZonedDateTime userZonedDateTime = utcZonedDateTime.withZoneSameInstant(ZoneId.of(timeZone));

        return userZonedDateTime.toLocalDateTime();
    }
}
