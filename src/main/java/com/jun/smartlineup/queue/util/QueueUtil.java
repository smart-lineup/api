package com.jun.smartlineup.queue.util;

import com.jun.smartlineup.attendee.domain.Attendee;
import com.jun.smartlineup.line.domain.Line;
import com.jun.smartlineup.queue.domain.Queue;
import com.jun.smartlineup.queue.repository.QueueRepository;

import java.util.*;
import java.util.stream.Collectors;

public class QueueUtil {
    public static void addQueue(QueueRepository queueRepository, Line line, Attendee attendee) {
        Optional<Queue> optionalQueue = queueRepository.findFirstByLineAndDeletedAtIsNullOrderByIdDesc(line);
        Queue queue = Queue.createQueue(line, attendee);
        if (optionalQueue.isPresent()) {
            Queue previous = optionalQueue.get();
            queue.setPrevious(previous);
            previous.setNext(queue);
        }
        queueRepository.save(queue);
    }

    public static List<Queue> orderQueueList(List<Queue> allQueues) {
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
}
