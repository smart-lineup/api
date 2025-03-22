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
    public void reorder(CustomUserDetails userDetails, QueueChangeRequestDto dto) {
        User user = UserUtil.ConvertUser(userRepository, userDetails);

//
//        Long oldOrderNo = queueRepository.findOrderNoByQueueId(queueId);
//        String sql;
//        if (newOrderNo < oldOrderNo) {
//            // Moving up
//            sql = "UPDATE queue " +
//                    "SET order_no = CASE " +
//                    "WHEN queue_id = :queueId THEN :newOrderNo " +
//                    "WHEN order_no >= :newOrderNo AND order_no < :oldOrderNo THEN order_no + 1 " +
//                    "ELSE order_no END " +
//                    "WHERE line_id = :lineId " +
//                    "AND (queue_id = :queueId OR (order_no >= :newOrderNo AND order_no < :oldOrderNo))";
//        } else {
//            // Moving down
//            sql = "UPDATE queue " +
//                    "SET order_no = CASE " +
//                    "WHEN queue_id = :queueId THEN :newOrderNo " +
//                    "WHEN order_no > :oldOrderNo AND order_no <= :newOrderNo THEN order_no - 1 " +
//                    "ELSE order_no END " +
//                    "WHERE line_id = :lineId " +
//                    "AND (queue_id = :queueId OR (order_no > :oldOrderNo AND order_no <= :newOrderNo))";
//        }
    }

}
