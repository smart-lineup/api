package com.jun.smartlineup.queue.service;

import com.jun.smartlineup.attendee.domain.Attendee;
import com.jun.smartlineup.attendee.repository.AttendeeRepository;
import com.jun.smartlineup.line.domain.Line;
import com.jun.smartlineup.line.repository.LineRepository;
import com.jun.smartlineup.queue.domain.Queue;
import com.jun.smartlineup.queue.domain.QueueStatus;
import com.jun.smartlineup.queue.dto.QueueAttendeeChangeRequestDto;
import com.jun.smartlineup.queue.dto.QueueBatchAddRequestDto;
import com.jun.smartlineup.queue.dto.QueueReorderRequestDto;
import com.jun.smartlineup.queue.repository.QueueRepository;
import com.jun.smartlineup.user.domain.User;
import com.jun.smartlineup.user.dto.CustomUserDetails;
import com.jun.smartlineup.user.repository.UserRepository;
import com.jun.smartlineup.user.utils.UserUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QueueServiceImpl implements QueueService {
    private final QueueRepository queueRepository;
    private final UserRepository userRepository;
    private final LineRepository lineRepository;
    private final AttendeeRepository attendeeRepository;

    @PersistenceContext
    EntityManager em;

    @Override
    public void save(Queue queue) {
        queueRepository.save(queue);
    }

    @Override
    public List<Queue> findQueues(CustomUserDetails userDetails, Long lineId) {
        User user = UserUtil.ConvertUser(userRepository, userDetails);
        List<Queue> allQueues = queueRepository.findAllByUserAndLine_Id(user, lineId);
        if (allQueues.isEmpty()) {
            return Collections.emptyList();
        }

        // ID를 키로 하여 맵으로 변환 (빠른 조회를 위해)
        Map<Long, Queue> queueMap = allQueues.stream()
                .collect(Collectors.toMap(Queue::getId, queue -> queue));

        // 첫 번째 항목 찾기
        Queue firstQueue = allQueues.stream()
                .filter(queue -> queue.getPrevious() == null)
                .findFirst()
                .orElse(null);

        if (firstQueue == null) {
            return Collections.emptyList();
        }

        // 링크를 따라가며 순서 재구성
        List<Queue> orderedQueue = new ArrayList<>();
        Queue current = firstQueue;
        while (current != null) {
            orderedQueue.add(current);
            Long nextId = current.getNext() != null ? current.getNext().getId() : null;
            current = nextId != null ? queueMap.get(nextId) : null;
        }

        return orderedQueue;
    }

    @Override
    public void addFromAttendee(Line line, Attendee attendee) {
        Optional<Queue> optionalQueue = queueRepository.findFirstByLineAndDeletedAtIsNullOrderByIdDesc(line);
        Queue queue = Queue.createQueue(line, attendee);
        if (optionalQueue.isPresent()) {
            Queue previous = optionalQueue.get();
            queue.setPrevious(previous);
            previous.setNext(queue);
        }
        queueRepository.save(queue);
    }

    @Override
    public void reorder(CustomUserDetails userDetails, QueueReorderRequestDto dto) {
        if (dto.getMovedQueueId().equals(dto.getTargetQueueId())) {
            return;
        }

        User user = UserUtil.ConvertUser(userRepository, userDetails);

        Optional<Queue> optionalMoveQueue = queueRepository.findByUserAndQueue_Id(user, dto.getMovedQueueId());
        Queue move = optionalMoveQueue.orElseThrow(() -> new RuntimeException("Not exist Queue::" + dto.getMovedQueueId()));

        Optional<Queue> optionalTargetQueue = queueRepository.findByUserAndQueue_Id(user, dto.getTargetQueueId());
        Queue target = optionalTargetQueue.orElseThrow(() -> new RuntimeException("Not exist Queue::" + dto.getTargetQueueId()));

        if (!move.getLine().getId().equals(dto.getLineId())) {
            throw new RuntimeException("Wrong request::Not match Line::request Line::" + dto.getLineId() + ", find Line::" + move.getLine().getId());
        }
        if (!move.getLine().getUser().equals(user)) {
            throw new RuntimeException("Wrong request::Not match User::request User::" + user + ", Line User::" + move.getLine().getUser());
        }

        ChangeLikedListByReorder(dto, move, target);
    }

    private void ChangeLikedListByReorder(QueueReorderRequestDto dto, Queue move, Queue target) {
        if (move.getPrevious() != null) {
            move.getPrevious().setNext(move.getNext());
        }
        if (move.getNext() != null) {
            move.getNext().setPrevious(move.getPrevious());
        }

        if ("up".equals(dto.getDirection())) {
            Queue targetPrevious = target.getPrevious();
            move.setPrevious(targetPrevious);
            move.setNext(target);
            if (targetPrevious != null) {
                targetPrevious.setNext(move);
            }
            target.setPrevious(move);
        } else {
            Queue targetNext = target.getNext();
            move.setPrevious(target);
            move.setNext(targetNext);
            target.setNext(move);
            if (targetNext != null) {
                targetNext.setPrevious(move);
            }
        }
    }

    @Override
    public void changeStatus(CustomUserDetails userDetails, Long queueId, String status) {
        User user = UserUtil.ConvertUser(userRepository, userDetails);

        Optional<Queue> optionalQueue = queueRepository.findByUserAndQueue_Id(user, queueId);
        Queue queue = optionalQueue.orElseThrow(() -> new RuntimeException("change status error::queue id:" + queueId));

        if (!queue.getLine().getUser().equals(user)) {
            throw new RuntimeException("Not match User::change status::try user=" + user.getEmail() + ", origin user=" + queue.getLine().getUser());
        }

        QueueStatus queueStatus = QueueStatus.convertStatus(status);
        queue.setStatus(queueStatus);
    }

    @Override
    public void delete(CustomUserDetails userDetails, Long queueId) {
        User user = UserUtil.ConvertUser(userRepository, userDetails);
        Optional<Queue> optionalQueue = queueRepository.findByUserAndQueue_Id(user, queueId);
        Queue queue = optionalQueue.orElseThrow(() -> new RuntimeException("Not match user and queue::user=" + user.getEmail() + ", queue=" + queueId));

        queue.setDeletedAt(LocalDateTime.now());
        Queue previous = queue.getPrevious();
        Queue next = queue.getNext();
        if (previous != null) {
            previous.setNext(next);
        }
        if (next != null) {
            next.setPrevious(previous);
        }
    }

    @Override
    public void attendeeInfoChange(CustomUserDetails userDetails, Long queueId, QueueAttendeeChangeRequestDto dto) {
        User user = UserUtil.ConvertUser(userRepository, userDetails);
        Optional<Queue> optionalQueue = queueRepository.findByUserAndQueue_Id(user, queueId);
        Queue queue = optionalQueue.orElseThrow(() -> new RuntimeException("Not match User and Queue::attendeeChange::user=" + user.getEmail() + ", queue=" + queueId));

        Attendee attendee = queue.getAttendee();
        attendee.changeInfo(dto.getName(), dto.getPhone(), dto.getInfo());
    }

    // Todo. need to refactoring
    @Transactional
    public void batchAdd(CustomUserDetails userDetails, QueueBatchAddRequestDto dto) {
        User user = UserUtil.ConvertUser(userRepository, userDetails);
        Optional<Line> optionalLine = lineRepository.getLineByIdAndUserAndDeleteAtIsNull(dto.getLineId(), user);
        Line line = optionalLine.orElseThrow(() -> new RuntimeException("Not Exist Line::batchAdd::user=" + user.getEmail() + ", lineId=" + dto.getLineId()));

        List<Attendee> attendees = dto.getAttendees().stream()
                .map(a -> a.toEntity(user))
                .toList();

        int size = 50;
        for (int i = 0; i < attendees.size(); i += size) {
            List<Attendee> attendeeChunk = attendees.subList(i, Math.min(i + size, attendees.size()));
            attendeeRepository.saveAllAndFlush(attendeeChunk);

            Queue lastQueueBeforeChunk = queueRepository
                    .findFirstByLineAndDeletedAtIsNullOrderByIdDesc(line)
                    .orElse(null);

            List<Queue> queueChunk = buildQueuesWithLinks(attendeeChunk, line, lastQueueBeforeChunk);

            if (lastQueueBeforeChunk != null) {
                queueRepository.save(lastQueueBeforeChunk);
            }

            queueRepository.saveAllAndFlush(queueChunk);
            em.flush();
            em.clear();
        }
    }

    private List<Queue> buildQueuesWithLinks(List<Attendee> attendees, Line line, Queue previous) {
        List<Queue> queueList = new ArrayList<>();
        for (Attendee attendee : attendees) {
            Queue queue = Queue.createQueue(line, attendee);
            if (previous != null) {
                queue.setPrevious(previous);
                previous.setNext(queue);
            }
            previous = queue;
            queueList.add(queue);
        }
        return queueList;
    }

}
