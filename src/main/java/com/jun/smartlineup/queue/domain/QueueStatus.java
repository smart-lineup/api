package com.jun.smartlineup.queue.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QueueStatus {
    WAITING("waiting", "waiting", "기다리는 사람"),
    ENTERED("entered", "entered", "입장한 사람");

    private final String key;
    private final String status;
    private final String korean;
}


