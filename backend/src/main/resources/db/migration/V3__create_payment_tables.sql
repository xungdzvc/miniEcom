-- V3: New normalized payment tables from the current local schema.
-- These are additive and do not replace/remove historical payment_transactions rows.

CREATE TABLE `order_payments`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `amount` decimal(38, 2) NULL DEFAULT NULL,
  `payment_method` enum('ORDER_BANKING','TOPUP','WALLET') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status` enum('FAILED','PENDING','SUCCESS','UNMATCH','WRONG_AMOUNT') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `transfer_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `order_id` bigint NOT NULL,
  `transaction_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK3s9vxneb3dk3plhpv9s213so0`(`order_id` ASC) USING BTREE,
  INDEX `FKa4xpy44xpfhuomnjhhh4j6uao`(`transaction_id` ASC) USING BTREE,
  CONSTRAINT `FK3s9vxneb3dk3plhpv9s213so0` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKa4xpy44xpfhuomnjhhh4j6uao` FOREIGN KEY (`transaction_id`) REFERENCES `payment_transactions` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;;

CREATE TABLE `topup_payments`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `amount` decimal(38, 2) NULL DEFAULT NULL,
  `card_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `card_serial` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `card_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `payment_method` enum('ORDER_BANKING','TOPUP','WALLET') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status` enum('FAILED','PENDING','SUCCESS','UNMATCH','WRONG_AMOUNT') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `transaction_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK2nwjc9gl0rchbo17xu8d20ve1`(`transaction_id` ASC) USING BTREE,
  INDEX `FKbb4tbxuj0d7e64dh20qqlr1ml`(`user_id` ASC) USING BTREE,
  CONSTRAINT `FK2nwjc9gl0rchbo17xu8d20ve1` FOREIGN KEY (`transaction_id`) REFERENCES `payment_transactions` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKbb4tbxuj0d7e64dh20qqlr1ml` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;;

