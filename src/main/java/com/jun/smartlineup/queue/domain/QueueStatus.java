package com.jun.smartlineup.queue.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum QueueStatus {
    WAITING("WAITING", "waiting", "기다리는 사람"),
    ENTERED("ENTERED", "entered", "입장한 사람");

    private final String key;
    private final String status;
    private final String korean;

    public static QueueStatus convertStatus(String key) {
        Optional<QueueStatus> first = Arrays.stream(QueueStatus.values()).filter(s -> s.key.equals(key)).findFirst();
        return first.orElseThrow(() -> new RuntimeException("No exsit key::" + key));
    }
}


