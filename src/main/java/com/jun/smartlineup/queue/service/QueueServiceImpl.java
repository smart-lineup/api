package com.jun.smartlineup.queue.service;

import com.jun.smartlineup.attendee.dao.FindPositionDao;
import com.jun.smartlineup.attendee.dao.QueueDao;
import com.jun.smartlineup.attendee.domain.Attendee;
import com.jun.smartlineup.attendee.repository.AttendeeRepository;
import com.jun.smartlineup.attendee.util.AttendeeUtil;
import com.jun.smartlineup.line.domain.Line;
import com.jun.smartlineup.line.repository.LineRepository;
import com.jun.smartlineup.queue.domain.Queue;
import com.jun.smartlineup.queue.domain.QueueStatus;
import com.jun.smartlineup.queue.dto.QueueAddRequestDto;
import com.jun.smartlineup.queue.dto.QueueAttendeeChangeRequestDto;
import com.jun.smartlineup.queue.dto.QueueBatchAddRequestDto;
import com.jun.smartlineup.queue.dto.QueueReorderRequestDto;
import com.jun.smartlineup.queue.repository.QueueRepository;
import com.jun.smartlineup.queue.util.QueueUtil;
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

        return QueueUtil.orderQueueList(allQueues);
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

        List<QueueDao> daoList = queueRepository.findQueueWithAttendeeByLine(line);
        List<Attendee> attendees = getAttendeeWithoutDuplication(dto, daoList, user);

        Map<String, QueueDao> queueMap = daoList.stream()
                .collect(Collectors.toMap(
                        a -> a.attendee().getName() + ":" + a.attendee().getPhone(),
                        a -> a,
                        (existing, dao) -> existing
                ));

        int size = 50;
        for (int i = 0; i < attendees.size(); i += size) {
            List<Attendee> originChunk = attendees.subList(i, Math.min(i + size, attendees.size()));
            List<Attendee> attendeeChunk = originChunk.stream()
                    .filter(a -> !queueMap.containsKey(a.getName() + ":" + a.getPhone()))
                    .toList();

            // Save new attendees
            attendeeRepository.saveAllAndFlush(attendeeChunk);

            Map<String, Attendee> insertedAttendeeMap = attendeeChunk.stream().collect(Collectors.toMap(
                    a -> a.getName() + ":" + a.getPhone(),
                    a -> a
            ));

            List<Attendee> combineAttendee = originChunk.stream()
                    .map(a -> {
                        if (!queueMap.containsKey(a.getName() + ":" + a.getPhone())) {
                            return insertedAttendeeMap.get(a.getName() + ":" + a.getPhone());
                        }
                        return queueMap.get(a.getName() + ":" + a.getPhone()).attendee();
                    })
                    .toList();

            Queue lastQueueBeforeChunk = queueRepository.findFirstByLineAndDeletedAtIsNullOrderByIdDesc(line)
                    .orElse(null);

            List<Queue> queueChunk = buildQueuesWithLinks(combineAttendee, line, lastQueueBeforeChunk);

            if (lastQueueBeforeChunk != null) {
                queueRepository.save(lastQueueBeforeChunk);
            }

            queueRepository.saveAllAndFlush(queueChunk);
            em.flush();
            em.clear();
        }
    }

    private List<Attendee> getAttendeeWithoutDuplication(QueueBatchAddRequestDto dto, List<QueueDao> list, User user) {
        Set<String> phoneSet = list.stream()
                .filter(dao -> dao.queue().getStatus() == QueueStatus.WAITING)
                .map(dao -> dao.attendee().getPhone())
                .collect(Collectors.toSet());

        return dto.getAttendees().stream()
                .filter(a -> !phoneSet.contains(a.getPhone()))
                .map(a -> a.toEntity(user))
                .toList();
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

    public void addQueueByUser(CustomUserDetails userDetails, QueueAddRequestDto dto) {
        User user = UserUtil.ConvertUser(userRepository, userDetails);
        Optional<Line> optionalLine = lineRepository.getLineByIdAndUserAndDeleteAtIsNull(dto.getLineId(), user);
        Line line = optionalLine.orElseThrow(() -> new RuntimeException("Not Exist Line::addQueueByUser::user=" + user.getEmail() + ", lineId=" + dto.getLineId()));

        List<FindPositionDao> list = queueRepository.findAllByLine_IdForAttendee(line.getId());
        AttendeeUtil.validBeforeAdd(dto.getAttendee().getPhone(), list);

        Attendee attendee = attendeeRepository.findByNameAndPhone(dto.getAttendee().getName(), dto.getAttendee().getPhone())
                .orElse(dto.getAttendee().toEntity(user));
        attendeeRepository.save(attendee);

        QueueUtil.addQueue(queueRepository, line, attendee);
    }

}
