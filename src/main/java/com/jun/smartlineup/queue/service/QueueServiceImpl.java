package com.jun.smartlineup.queue.service;

import com.jun.smartlineup.attendee.domain.Attendee;
import com.jun.smartlineup.exception.NoExistUserException;
import com.jun.smartlineup.line.domain.Line;
import com.jun.smartlineup.line.repository.LineRepository;
import com.jun.smartlineup.line.service.LineService;
import com.jun.smartlineup.queue.domain.Queue;
import com.jun.smartlineup.queue.dto.QueueChangeRequestDto;
import com.jun.smartlineup.queue.repository.QueueRepository;
import com.jun.smartlineup.user.domain.User;
import com.jun.smartlineup.user.dto.CustomUserDetails;
import com.jun.smartlineup.user.repository.UserRepository;
import com.jun.smartlineup.user.utils.UserUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class QueueServiceImpl implements QueueService {
    private final QueueRepository queueRepository;
    private final UserRepository userRepository;
    private final LineService lineService;

    @Override
    public void save(Queue queue) {
        queueRepository.save(queue);
    }

    @Override
    public List<Queue> findQueues(CustomUserDetails userDetails, Long lineId) {
        User user = UserUtil.ConvertUser(userRepository, userDetails);
        return queueRepository.findByUserAndLine_Id(user, lineId);
    }

    @Override
    public void addFromAttendee(Line line, Attendee attendee) {
        Optional<Queue> optionalQueue = queueRepository.findFirstByLineOrderByIdDesc(line);
        long order = 1;
        if (optionalQueue.isPresent()) {
            Queue previous = optionalQueue.get();
            order = previous.getOrderNo() + 1;
        }
        Queue queue = Queue.createQueue(line, order, attendee);
        queueRepository.save(queue);
    }

    @Override
    public void changeOrder(CustomUserDetails userDetails, QueueChangeRequestDto dto) {
        User user = UserUtil.ConvertUser(userRepository, userDetails);

    }

}
