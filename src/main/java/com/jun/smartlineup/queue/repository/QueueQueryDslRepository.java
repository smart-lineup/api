package com.jun.smartlineup.queue.repository;

import com.jun.smartlineup.queue.domain.Queue;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QueueQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public void reorderMoveUp() {
//        Queue
//        Long oldOrderNo = queryFactory.select(qQueue.orderNo).from(qQueue)
//                .where(qQueue.queueId.eq(queueId).and(qQueue.lineId.eq(lineId))).fetchOne();
//        if (oldOrderNo == null || newOrderNo >= oldOrderNo) return;
//
//        Long lineUserId = queryFactory.select(qLine.userId).from(qLine)
//                .where(qLine.lineId.eq(lineId)).fetchOne();
//        if (!user.getId().equals(lineUserId)) return;
//
//        queryFactory.update(qQueue)
//                .set(qQueue.orderNo, qQueue.orderNo.add(1))
//                .where(qQueue.lineId.eq(lineId)
//                        .and(qQueue.orderNo.goe(newOrderNo))
//                        .and(qQueue.orderNo.lt(oldOrderNo)))
//                .execute();
//
//        queryFactory.update(qQueue)
//                .set(qQueue.orderNo, newOrderNo)
//                .where(qQueue.queueId.eq(queueId).and(qQueue.lineId.eq(lineId)))
//                .execute();
    }

}
