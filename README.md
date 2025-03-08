# smart-lineup

CREATE TABLE `users` (
`user_id` BIGINT NOT NULL AUTO_INCREMENT,
`name` VARCHAR(255) NULL,
`email` VARCHAR(255) NULL,
`picture` VARCHAR(255) NULL,
`role` ENUM('USER', 'ADMIN') NULL,
`created_date` TIMESTAMP NULL,
`updated_date` TIMESTAMP NULL,
PRIMARY KEY (`user_id`)
);

CREATE TABLE `line` (
`line_id` BIGINT NOT NULL AUTO_INCREMENT,
`user_id` BIGINT NOT NULL,
`name` VARCHAR(100) NOT NULL,
`created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
`updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (`line_id`),
CONSTRAINT `FK_users_TO_line_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
);

CREATE TABLE `attendee` (
`attendee_id` BIGINT NOT NULL AUTO_INCREMENT,
`user_id` BIGINT NOT NULL,
`phone` VARCHAR(20) NULL,
`info` JSON NULL,
`created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
`updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (`attendee_id`),
CONSTRAINT `FK_users_TO_attendee_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
);

CREATE TABLE `queue` (
`queue_id` BIGINT NOT NULL AUTO_INCREMENT,
`attendee_id` BIGINT NOT NULL,
`line_id` BIGINT NOT NULL,
`user_id` BIGINT NOT NULL,
`previous_id` BIGINT NULL,
`next_id` BIGINT NULL,
`status` VARCHAR(20) NOT NULL CHECK (`status` IN ('PENDING', 'APPROVED', 'REJECTED', 'COMPLETED')),
`created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
`updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (`queue_id`),
CONSTRAINT `FK_attendee_TO_queue_1` FOREIGN KEY (`attendee_id`) REFERENCES `attendee` (`attendee_id`),
CONSTRAINT `FK_line_TO_queue_1` FOREIGN KEY (`line_id`) REFERENCES `line` (`line_id`),
CONSTRAINT `FK_users_TO_queue_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
CONSTRAINT `FK_previous_TO_queue` FOREIGN KEY (`previous_id`) REFERENCES `queue` (`queue_id`),
CONSTRAINT `FK_next_TO_queue` FOREIGN KEY (`next_id`) REFERENCES `queue` (`queue_id`)
);