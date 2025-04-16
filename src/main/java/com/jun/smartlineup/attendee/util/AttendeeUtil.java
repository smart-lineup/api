package com.jun.smartlineup.attendee.util;

import com.jun.smartlineup.attendee.dao.FindPositionDao;
import com.jun.smartlineup.common.exception.DuplicationAttendeeException;
import com.jun.smartlineup.queue.domain.QueueStatus;

import java.util.List;
import java.util.Optional;

public class AttendeeUtil {
    public static void validBeforeAdd(String phone, List<FindPositionDao> list) {
        Optional<FindPositionDao> duplication = list.stream().filter(q -> {
            if (q.status() == QueueStatus.ENTERED) {
                return false;
            }
            return q.phone().equals(phone);
        }).findAny();
        if (duplication.isPresent()) {
            throw new DuplicationAttendeeException();
        }
    }
}
