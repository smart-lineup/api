package com.jun.smartlineup.attendee.service;

import com.jun.smartlineup.attendee.domain.Attendee;
import com.jun.smartlineup.attendee.dto.AttendeeAddRequestDto;
import com.jun.smartlineup.attendee.repository.AttendeeRepository;
import com.jun.smartlineup.line.domain.Line;
import com.jun.smartlineup.line.repository.LineRepository;
import com.jun.smartlineup.queue.service.QueueService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AttendeeService {
    private final AttendeeRepository attendeeRepository;
    private final LineRepository lineRepository;
    private final QueueService queueService;

    public void add(AttendeeAddRequestDto dto) {
        Optional<Line> optionalLine = lineRepository.findByUuid(dto.getUuid());
        Line line = optionalLine.orElseThrow(() -> new RuntimeException("No exist uuid: " + dto.getUuid()));
        Attendee attendee = Attendee.fromDto(line.getUser(), dto);
        attendeeRepository.save(attendee);

        queueService.addFromAttendee(line, attendee);
    }
}
