-- 1. users 테이블 생성 (다른 테이블의 참조 대상이므로 먼저 생성)
CREATE TABLE
`users` (
`user_id` BIGINT AUTO_INCREMENT NOT NULL,
`name` VARCHAR(255) NULL,
`email` VARCHAR(255) NULL,
`picture` VARCHAR(255) NULL,
`role` ENUM ('USER', 'ADMIN') NULL,
`password` VARCHAR(60) NULL,
`is_OAuth_login` BOOLEAN NULL,
`is_verified` BOOLEAN NULL,
`verification_token` VARCHAR(255) NULL,
`created_date` TIMESTAMP NULL,
`updated_date` TIMESTAMP NULL,
CONSTRAINT `PK_USERS` PRIMARY KEY (`user_id`)
);

-- 2. line 테이블 생성 (users 참조)
CREATE TABLE
`line` (
`line_id` BIGINT AUTO_INCREMENT NOT NULL,
`user_id` BIGINT NOT NULL,
`name` VARCHAR(100) NOT NULL,
`created_at` TIMESTAMP NOT NULL,
`updated_at` TIMESTAMP NOT NULL,
CONSTRAINT `PK_LINE` PRIMARY KEY (`line_id`),
CONSTRAINT `FK_users_TO_line_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
);

-- 3. attendee 테이블 생성 (users 참조)
CREATE TABLE
`attendee` (
`attendee_id` BIGINT AUTO_INCREMENT NOT NULL,
`user_id` BIGINT NOT NULL,
`phone` VARCHAR(20) NULL,
`info` JSON NULL,
`created_at` TIMESTAMP NOT NULL,
`updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
CONSTRAINT `PK_ATTENDEE` PRIMARY KEY (`attendee_id`),
CONSTRAINT `FK_users_TO_attendee_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
);

-- 4. queue 테이블 생성 (attendee, line, users 참조)
CREATE TABLE
`queue` (
`queue_id` BIGINT AUTO_INCREMENT NOT NULL,
`attendee_id` BIGINT NOT NULL,
`line_id` BIGINT NOT NULL,
`user_id` BIGINT NOT NULL,
`previous_id` BIGINT NULL,
`next_id` BIGINT NULL,
`status` VARCHAR(20) NOT NULL,
`created_at` TIMESTAMP NOT NULL,
`updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
CONSTRAINT `PK_QUEUE` PRIMARY KEY (`queue_id`),
CONSTRAINT `FK_attendee_TO_queue_1` FOREIGN KEY (`attendee_id`) REFERENCES `attendee` (`attendee_id`),
CONSTRAINT `FK_line_TO_queue_1` FOREIGN KEY (`line_id`) REFERENCES `line` (`line_id`),
CONSTRAINT `FK_users_TO_queue_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
CONSTRAINT `FK_queue_TO_previous` FOREIGN KEY (`previous_id`) REFERENCES `queue` (`queue_id`),
CONSTRAINT `FK_queue_TO_next` FOREIGN KEY (`next_id`) REFERENCES `queue` (`queue_id`)
);