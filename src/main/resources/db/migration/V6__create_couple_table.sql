CREATE TABLE `couple` (
  `couple_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `couple_status` enum('CONNECTED','DISCONNECTED_GRACE_PERIOD') DEFAULT NULL,
  `member_a_id` bigint DEFAULT NULL,
  `member_b_id` bigint DEFAULT NULL,
  `disconnected_at` timestamp NULL DEFAULT NULL,
  `delete_scheduled_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`couple_id`),
  UNIQUE KEY `uq_couple_pair` (`member_a_id`, `member_b_id`),
  KEY `FKtlmckme2msnqymntc9u6x2rhh` (`member_b_id`),
  CONSTRAINT `chk_member_order` CHECK (`member_a_id` < `member_b_id`),
  CONSTRAINT `FKlqrofs4461uq26x1ttmchk270` FOREIGN KEY (`member_a_id`) REFERENCES `member` (`member_id`),
  CONSTRAINT `FKtlmckme2msnqymntc9u6x2rhh` FOREIGN KEY (`member_b_id`) REFERENCES `member` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
