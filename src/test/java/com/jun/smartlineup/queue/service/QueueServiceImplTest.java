package com.jun.smartlineup.queue.service;

import com.jun.smartlineup.queue.repository.QueueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class QueueServiceImplTest {

    @Autowired
    private QueueRepository queueRepository;

    private QueueService queueService;

    @BeforeEach
    void setUp() {
        queueService = new QueueServiceImpl(queueRepository);
    }

    @Test
    @DisplayName("test1")
    void test() {

    }
}