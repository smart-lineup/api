package com.jun.smartlineup.queue.service;

import com.jun.smartlineup.attendee.domain.Attendee;
import com.jun.smartlineup.line.domain.Line;
import com.jun.smartlineup.queue.domain.Queue;
import com.jun.smartlineup.queue.domain.QueueStatus;
import com.jun.smartlineup.queue.dto.QueueReorderRequestDto;
import com.jun.smartlineup.queue.repository.QueueRepository;
import com.jun.smartlineup.user.domain.User;
import com.jun.smartlineup.user.dto.CustomUserDetails;
import com.jun.smartlineup.user.repository.UserRepository;
import com.jun.smartlineup.user.utils.UserUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QueueServiceImpl implements QueueService {
    private final QueueRepository queueRepository;
    private final UserRepository userRepository;

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
        Optional<Queue> optionalQueue = queueRepository.findFirstByLineOrderByIdDesc(line);
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

        Optional<Queue> optionalMoveQueue = queueRepository.findById(dto.getMovedQueueId());
        Queue move = optionalMoveQueue.orElseThrow(() -> new RuntimeException("Not exist Queue::" + dto.getMovedQueueId()));

        Optional<Queue> optionalTargetQueue = queueRepository.findById(dto.getTargetQueueId());
        Queue target = optionalTargetQueue.orElseThrow(() -> new RuntimeException("Not exist Queue::" + dto.getTargetQueueId()));

        if (!move.getLine().getId().equals(dto.getLineId())) {
            throw new RuntimeException("Wrong request::Not match Line::request Line::" + dto.getLineId() + ", find Line::" + move.getLine().getId());
        }
        if (!move.getLine().getUser().equals(user)) {
            throw new RuntimeException("Wrong request::Not match User::request User::" + user + ", Line User::" + move.getLine().getUser());
        }

        if (dto.getMovedQueueId() < dto.getTargetQueueId()) {
            //1
            move.getNext().setPrevious(move.getPrevious());
            if (move.getPrevious() != null) {
                move.getPrevious().setNext(move.getNext());
            }

            Queue TargetNext = target.getNext();
            if (target.getNext() != null) {
                TargetNext.setPrevious(move);
            }
            target.setNext(move);

            move.setPrevious(target);
            move.setNext(TargetNext);
            return;
        }
        target.getPrevious().setNext(move);
        move.getNext().setPrevious(move.getPrevious());

        Queue originTargetPrevious = target.getPrevious();

        target.setPrevious(move);
        move.getPrevious().setNext(move.getNext());

        move.setPrevious(originTargetPrevious);
        move.setNext(target);
    }

    @Override
    public void changeStatus(CustomUserDetails userDetails, Long queueId, String status) {
        User user = UserUtil.ConvertUser(userRepository, userDetails);

        Optional<Queue> optionalQueue = queueRepository.findById(queueId);
        Queue queue = optionalQueue.orElseThrow(() -> new RuntimeException("change status error::queue id:" + queueId));

        if (!queue.getLine().getUser().equals(user)) {
            throw new RuntimeException("Not match User::change status::try user=" + user.getEmail() + ", origin user=" + queue.getLine().getUser());
        }

        QueueStatus queueStatus = QueueStatus.convertStatus(status);
        queue.setStatus(queueStatus);
    }

}
