package com.jun.smartlineup.attendee.dto;

public record AttendeePositionResponseDto(
        boolean isQueuePositionVisibleToAttendee,
        int position,
        int totalWaiting
) {}
