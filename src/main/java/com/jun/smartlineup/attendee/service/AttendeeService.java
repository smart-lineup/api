package com.jun.smartlineup.attendee.service;

import com.jun.smartlineup.attendee.dao.FindPositionDao;
import com.jun.smartlineup.attendee.domain.Attendee;
import com.jun.smartlineup.attendee.dto.AttendeeAddRequestDto;
import com.jun.smartlineup.attendee.dto.AttendeeDeleteRequestDto;
import com.jun.smartlineup.attendee.dto.AttendeePositionResponseDto;
import com.jun.smartlineup.attendee.repository.AttendeeRepository;
import com.jun.smartlineup.attendee.util.AttendeeUtil;
import com.jun.smartlineup.common.exception.ImpossibleRequestException;
import com.jun.smartlineup.line.domain.Line;
import com.jun.smartlineup.line.repository.LineRepository;
import com.jun.smartlineup.queue.domain.Queue;
import com.jun.smartlineup.queue.domain.QueueStatus;
import com.jun.smartlineup.queue.repository.QueueRepository;
import com.jun.smartlineup.queue.util.QueueUtil;
import com.jun.smartlineup.user.domain.Role;
import com.jun.smartlineup.user.domain.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendeeService {
    private final AttendeeRepository attendeeRepository;
    private final LineRepository lineRepository;
    private final QueueRepository queueRepository;

    @Transactional
    public void add(AttendeeAddRequestDto dto) {
        Optional<Line> optionalLine = lineRepository.findByUuidAndDeleteAtIsNull(dto.getUuid());
        Line line = optionalLine.orElseThrow(() -> new RuntimeException("No exist uuid: " + dto.getUuid()));
        User user = line.getUser();

        if (!attendeeCanJoin(dto.getUuid())) {
            throw new ImpossibleRequestException("add", user);
        }
        List<FindPositionDao> list = queueRepository.findAllByLine_IdForAttendee(line.getId());
        AttendeeUtil.validBeforeAdd(dto.getPhone(), list);

        Attendee attendee = attendeeRepository.findByNameAndPhone(dto.getName(), dto.getPhone())
                .orElse(Attendee.fromDto(line.getUser(), dto));
        attendeeRepository.save(attendee);
        QueueUtil.addQueue(queueRepository, line, attendee);
    }

    public boolean attendeeCanJoin(String uuid) {
        Optional<Line> byUuidAndDeleteAtIsNull = lineRepository.findByUuidAndDeleteAtIsNull(uuid);
        Line line = byUuidAndDeleteAtIsNull.orElseThrow(IllegalArgumentException::new);
        User user = line.getUser();
        Role role = user.getRole();
        if (role.isPremium()) {
            return true;
        }

        List<Queue> list = queueRepository.findAllByUserAndLine_Id(user, line.getId());
        return list.size() < 20;
    }

    public AttendeePositionResponseDto findPosition(String uuid, String phone) {
        Optional<Line> byUuidAndDeleteAtIsNull = lineRepository.findByUuidAndDeleteAtIsNull(uuid);
        Line line = byUuidAndDeleteAtIsNull.orElseThrow(IllegalArgumentException::new);
        if (!line.getIsQueuePositionVisibleToAttendee()) {
            return new AttendeePositionResponseDto(false, 0, 0);
        }

        List<FindPositionDao> allQueues = queueRepository.findAllByLine_IdForAttendee(line.getId());
        if (allQueues.isEmpty()) {
            return new AttendeePositionResponseDto(true, 0, 0);
        }

        List<FindPositionDao> queueList = orderQueueList(allQueues);

        int index = 0;
        for (int i = 0; i < queueList.size(); i++) {
            if (phone.equals(queueList.get(i).phone())) {
                index = i + 1;
                break;
            }
        }

        return new AttendeePositionResponseDto(true, index, queueList.size());
    }

    private List<FindPositionDao> orderQueueList(List<FindPositionDao> allQueues) {
        Map<Long, FindPositionDao> queueMap = allQueues.stream()
                .collect(Collectors.toMap(FindPositionDao::id, queue -> queue));

        // 첫 번째 항목 찾기
        FindPositionDao firstQueue = allQueues.stream()
                .filter(queue -> queue.prevId() == null)
                .findFirst()
                .orElse(null);

        if (firstQueue == null) {
            return Collections.emptyList();
        }

        // 링크를 따라가며 순서 재구성
        List<FindPositionDao> orderedQueue = new ArrayList<>();
        FindPositionDao current = firstQueue;
        while (current != null) {
            if (current.status() == QueueStatus.WAITING) {
                orderedQueue.add(current);
            }
            Long nextId = current.nextId();
            current = nextId != null ? queueMap.get(nextId) : null;
        }

        return orderedQueue;
    }

    @Transactional
    public void deleteAttendee(AttendeeDeleteRequestDto dto) {
        List<String> list = queueRepository.deleteAndReturnPhones(dto.uuid(), dto.phone());
        System.out.println(list);
    }
}