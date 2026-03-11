/*
 Navicat Premium Data Transfer

 Source Server         : dockerMySql
 Source Server Type    : MySQL
 Source Server Version : 80044 (8.0.44)
 Source Host           : localhost:3306
 Source Schema         : web

 Target Server Type    : MySQL
 Target Server Version : 80044 (8.0.44)
 File Encoding         : 65001

 Date: 11/03/2026 04:28:09
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for bank_accounts
-- ----------------------------
DROP TABLE IF EXISTS `bank_accounts`;
CREATE TABLE `bank_accounts`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `bank_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `bank_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `account_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `account_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` bigint NOT NULL,
  `active` tinyint(1) NULL DEFAULT 1,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_bank_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_bank_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of bank_accounts
-- ----------------------------
INSERT INTO `bank_accounts` VALUES (1, 'MB', 'MBBANK', '120220032015', 'PHAM VAN XUNG', 7, 1, '2025-12-31 11:05:37');

-- ----------------------------
-- Table structure for cart_items
-- ----------------------------
DROP TABLE IF EXISTS `cart_items`;
CREATE TABLE `cart_items`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cart_id` bigint NOT NULL,
  `product_id` bigint NULL DEFAULT NULL,
  `quantity` int NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `cart_id`(`cart_id` ASC) USING BTREE,
  INDEX `product_id`(`product_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 40 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of cart_items
-- ----------------------------

-- ----------------------------
-- Table structure for carts
-- ----------------------------
DROP TABLE IF EXISTS `carts`;
CREATE TABLE `carts`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `carts_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of carts
-- ----------------------------
INSERT INTO `carts` VALUES (7, 7);
INSERT INTO `carts` VALUES (8, 8);
INSERT INTO `carts` VALUES (9, 11);
INSERT INTO `carts` VALUES (10, 13);
INSERT INTO `carts` VALUES (11, 14);
INSERT INTO `carts` VALUES (12, 15);
INSERT INTO `carts` VALUES (13, 16);
INSERT INTO `carts` VALUES (14, 17);

-- ----------------------------
-- Table structure for categories
-- ----------------------------
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of categories
-- ----------------------------
INSERT INTO `categories` VALUES (1, 'Resource Mod', '2025-12-12 22:04:30', '2026-03-05 04:19:59');
INSERT INTO `categories` VALUES (2, 'Resrouce Games', '2025-12-12 22:03:33', '2025-12-12 22:03:33');
INSERT INTO `categories` VALUES (8, 'Resource Tool', '2025-12-22 16:14:37', '2026-03-05 04:20:07');
INSERT INTO `categories` VALUES (10, 'Resource Web', '2026-03-05 05:42:41', '2026-03-05 05:42:41');

-- ----------------------------
-- Table structure for coupon_code
-- ----------------------------
DROP TABLE IF EXISTS `coupon_code`;
CREATE TABLE `coupon_code`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `coupon_code` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `discount` tinyint NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of coupon_code
-- ----------------------------
INSERT INTO `coupon_code` VALUES (1, 'XUNG', 20, NULL, '2026-03-08 12:32:57');

-- ----------------------------
-- Table structure for order_items
-- ----------------------------
DROP TABLE IF EXISTS `order_items`;
CREATE TABLE `order_items`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NULL DEFAULT NULL,
  `product_id` int NULL DEFAULT NULL,
  `quantity` int NULL DEFAULT NULL,
  `price` decimal(10, 0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `product_id`(`product_id` ASC) USING BTREE,
  INDEX `fk_orderdetail_order`(`order_id` ASC) USING BTREE,
  CONSTRAINT `fk_orderdetail_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 221 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of order_items
-- ----------------------------

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` bigint NULL DEFAULT NULL,
  `order_date` timestamp NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `total` decimal(10, 0) NULL DEFAULT NULL,
  `payment_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `expires_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_order_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_orders_status`(`payment_method` ASC) USING BTREE,
  CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 147 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of orders
-- ----------------------------

-- ----------------------------
-- Table structure for payment_transactions
-- ----------------------------
DROP TABLE IF EXISTS `payment_transactions`;
CREATE TABLE `payment_transactions`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `payment_type` enum('BANK','CARD') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `payment_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `payment_ref` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `amount` bigint NOT NULL,
  `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'VND',
  `transaction_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `card_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `card_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `card_serial` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `bank_account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `pay_status` enum('PENDING','SUCCESS','FAILED','WRONG_AMOUNT') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'PENDING',
  `match_type` enum('ORDER','TOPUP') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `match_ref` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `order_id` bigint NULL DEFAULT NULL,
  `user_id` bigint NULL DEFAULT NULL,
  `matched_at` datetime NULL DEFAULT NULL,
  `occurred_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uq_payment_ref`(`payment_type` ASC, `payment_ref` ASC) USING BTREE,
  INDEX `idx_pay_status`(`pay_status` ASC) USING BTREE,
  INDEX `idx_order`(`order_id` ASC) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of payment_transactions
-- ----------------------------
INSERT INTO `payment_transactions` VALUES (25, 'BANK', 'MBBank', 'FT252655114108159', 1111, NULL, 'MB 0788084165 HD2026144- Ma GD ACSP/ MK009303', NULL, NULL, NULL, '0788084165', 'SUCCESS', 'ORDER', 'HD2026144', 144, NULL, NULL, '2026-03-04 09:02:20', '2026-03-04 16:02:19');

-- ----------------------------
-- Table structure for product_detail
-- ----------------------------
DROP TABLE IF EXISTS `product_detail`;
CREATE TABLE `product_detail`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint NOT NULL,
  `quantity` int NULL DEFAULT NULL,
  `download_url` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `youtube_url` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `demo_url` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `view_count` int NULL DEFAULT 0,
  `sales_count` int NULL DEFAULT 0,
  `discount` int NULL DEFAULT 0,
  `technology` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `install_tutorial` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `pin` tinyint NULL DEFAULT 0,
  `share_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `product_id`(`product_id` ASC) USING BTREE,
  CONSTRAINT `fk_product_details_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 50 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_detail
-- ----------------------------
INSERT INTO `product_detail` VALUES (39, 31, 999, 'https://drive.google.com/file/d/1TqSBYp4O3hi9G8_uJ1jVT5x92YlDMP8D/view?usp=sharing', '', '', 5, NULL, 100, NULL, NULL, 0, NULL);
INSERT INTO `product_detail` VALUES (40, 32, 999, 'https://drive.google.com/file/d/1d75J6TGL4SJUda7LaE884sUJfdeixAai/view?usp=sharing', '', '', 3, NULL, 0, NULL, NULL, 0, 'xunglord');
INSERT INTO `product_detail` VALUES (41, 33, 999, 'https://drive.google.com/file/d/1Z-C8Io29RrzxjcTkT7myLrhFMJAX2Sjb/view?usp=sharing', '', '', 1, NULL, 0, NULL, NULL, 0, 'xunglord');
INSERT INTO `product_detail` VALUES (42, 34, 999, 'https://drive.google.com/file/d/17eUKxGvbBhwtjHArhx1XIYH2X9nhfHwg/view?usp=sharing', '', '', 0, NULL, 100, NULL, NULL, 0, NULL);
INSERT INTO `product_detail` VALUES (43, 35, 999, 'https://drive.google.com/file/d/1XxlILhBTyF-1uRK2NQ7BKAVRYuJ6qhmv/view?usp=sharing', '', '', 6, NULL, 100, NULL, NULL, 0, NULL);
INSERT INTO `product_detail` VALUES (44, 36, 999, 'https://drive.google.com/file/d/1LWd2MNjxlIS_RGVN1bWTM8BFeA9KiWQt/view?usp=sharing', '', '', 3, NULL, 0, NULL, NULL, 0, 'xunglord');
INSERT INTO `product_detail` VALUES (45, 37, 999, 'https://drive.google.com/file/d/1IlK-7-KAxIr9HX_-J5gcxi0KO8_CnHkG/view?usp=drive_link', '', '', 2, NULL, 0, NULL, NULL, 0, NULL);
INSERT INTO `product_detail` VALUES (46, 38, 999, 'https://drive.google.com/file/d/1h6psMnP9xRqI-P0dmBG6tblvENuyP1CB/view?usp=sharing', '', '', 1, NULL, 0, NULL, NULL, 0, NULL);
INSERT INTO `product_detail` VALUES (47, 39, 999, 'https://drive.google.com/file/d/1Xo1FZEuzIXaupldQ9dXmEqbRyM2vcZ_D/view?usp=sharing', '', '', 1, NULL, 0, NULL, NULL, 0, NULL);
INSERT INTO `product_detail` VALUES (48, 40, 999, 'https://drive.google.com/file/d/1fHqMH32JwIGrlNjcjCFZYPmLmHzg-QDI/view?usp=sharing', '', '', 4, NULL, 70, NULL, NULL, 1, NULL);
INSERT INTO `product_detail` VALUES (49, 41, 1, 'https://drive.google.com/file/d/19Wjmprn7CjYrknXFenYLqRMvMPXY7OKh/view?usp=sharing', '', '', 4, NULL, 0, NULL, NULL, 0, NULL);

-- ----------------------------
-- Table structure for product_images
-- ----------------------------
DROP TABLE IF EXISTS `product_images`;
CREATE TABLE `product_images`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint NOT NULL,
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `sort_order` int NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `product_id`(`product_id` ASC) USING BTREE,
  CONSTRAINT `product_images_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 258 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_images
-- ----------------------------
INSERT INTO `product_images` VALUES (154, 32, '/uploads/products/6c9e344e-9c78-47d1-8565-9a1bb06aca74.png', 0);
INSERT INTO `product_images` VALUES (155, 33, '/uploads/products/c74bf3ef-7e5d-4937-b30c-f18a2f21771d.png', 0);
INSERT INTO `product_images` VALUES (156, 33, '/uploads/products/a1fe5d43-d33f-49d1-8586-44e91cbb39ec.png', 0);
INSERT INTO `product_images` VALUES (157, 33, '/uploads/products/3375172f-c319-4efb-a47f-6f6c280b3165.png', 0);
INSERT INTO `product_images` VALUES (158, 33, '/uploads/products/123b1402-9285-4fc6-a148-2fed67c6aaa9.png', 0);
INSERT INTO `product_images` VALUES (159, 33, '/uploads/products/b1b8d90c-1a1f-421b-89d1-274281b60ca8.png', 0);
INSERT INTO `product_images` VALUES (191, 36, '/uploads/products/9b3a1fd7-7a53-4e80-8d44-43b706496647.png', 0);
INSERT INTO `product_images` VALUES (192, 36, '/uploads/products/201a5b4c-ed31-4376-a8e8-49cb786c3702.png', 0);
INSERT INTO `product_images` VALUES (193, 36, '/uploads/products/151e1ecc-0d0f-42dc-ba30-3d9cbca0b2c0.png', 0);
INSERT INTO `product_images` VALUES (207, 38, '/uploads/products/893d6c7c-aca4-4482-9f54-52dea09c7e5c.png', 0);
INSERT INTO `product_images` VALUES (208, 38, '/uploads/products/9edaaa7b-5463-49f3-9a1e-9ae9469f467b.png', 0);
INSERT INTO `product_images` VALUES (209, 38, '/uploads/products/053228db-cd46-4fc6-bac6-1d7d42debcff.png', 0);
INSERT INTO `product_images` VALUES (210, 38, '/uploads/products/0dace09c-a1fc-43d3-8820-a0573bcb230d.png', 0);
INSERT INTO `product_images` VALUES (211, 38, '/uploads/products/90d42300-2435-4d0b-aa65-ab1ca5f44037.png', 0);
INSERT INTO `product_images` VALUES (212, 38, '/uploads/products/6217038d-7d4b-4b5f-82ad-bc83a657c6bc.png', 0);
INSERT INTO `product_images` VALUES (217, 37, '/uploads/products/58c88530-bc62-446b-850e-1c213a5fa727.png', 0);
INSERT INTO `product_images` VALUES (218, 37, '/uploads/products/95e543b6-b3f8-4137-bd73-23a5cb702c99.png', 0);
INSERT INTO `product_images` VALUES (219, 37, '/uploads/products/52910f9b-5c71-4d91-b430-95025b8784f7.png', 0);
INSERT INTO `product_images` VALUES (220, 37, '/uploads/products/06337e04-49ab-4647-8d98-3ac603d1216a.png', 0);
INSERT INTO `product_images` VALUES (221, 37, '/uploads/products/58a59229-46a9-4743-bba0-94968435045d.png', 0);
INSERT INTO `product_images` VALUES (222, 37, '/uploads/products/0dfa12f9-8edb-43fc-909d-7427149b3518.png', 0);
INSERT INTO `product_images` VALUES (223, 39, '/uploads/products/dd5c315a-5664-4ae7-af02-8b7298091b17.png', 0);
INSERT INTO `product_images` VALUES (224, 39, '/uploads/products/7ba38cb4-cd7e-43e7-a11d-e7e2fc5e3fdc.png', 0);
INSERT INTO `product_images` VALUES (225, 39, '/uploads/products/adfe8f3c-322b-47c5-9345-9b36be44511b.png', 0);
INSERT INTO `product_images` VALUES (226, 39, '/uploads/products/c683e672-317d-4b93-941d-425faacff1ec.png', 0);
INSERT INTO `product_images` VALUES (227, 31, '/uploads/products/c215c29e-f8ba-4d67-ad12-572de0b5ae37.png', 0);
INSERT INTO `product_images` VALUES (228, 31, '/uploads/products/55b2f72e-93d3-451e-a310-608ea5c4cdb6.png', 0);
INSERT INTO `product_images` VALUES (229, 31, '/uploads/products/80bada88-4d15-4d90-9423-e41bf5437ee7.png', 0);
INSERT INTO `product_images` VALUES (230, 31, '/uploads/products/38235c4d-1c6e-46c8-a870-2af49636801a.png', 0);
INSERT INTO `product_images` VALUES (231, 31, '/uploads/products/49ab3670-7cda-45b6-9b23-728112143a0f.png', 0);
INSERT INTO `product_images` VALUES (232, 31, '/uploads/products/df6a7527-6e94-44b2-9aad-87066e39a9ae.png', 0);
INSERT INTO `product_images` VALUES (233, 31, '/uploads/products/47065a42-6e04-4678-98fe-d9c69f4c8ec4.png', 0);
INSERT INTO `product_images` VALUES (234, 34, '/uploads/products/1cbf6a59-a3f1-448d-9d42-219b8b3fe1a9.png', 0);
INSERT INTO `product_images` VALUES (235, 34, '/uploads/products/7e4ead12-84da-4a03-99a9-99dfc3687744.png', 0);
INSERT INTO `product_images` VALUES (236, 34, '/uploads/products/cc818a52-8b17-4980-b17c-a96db5069a86.png', 0);
INSERT INTO `product_images` VALUES (237, 34, '/uploads/products/18b99c80-d7d6-428e-9414-9e2de10ffba8.png', 0);
INSERT INTO `product_images` VALUES (238, 34, '/uploads/products/0b3bd11a-64e0-4985-a1fd-c03030386e7c.png', 0);
INSERT INTO `product_images` VALUES (239, 34, '/uploads/products/50782483-1ab2-4aaa-9550-2957a4b4c560.png', 0);
INSERT INTO `product_images` VALUES (240, 34, '/uploads/products/b26824d6-519e-439d-ab3b-b6f0fc41076d.png', 0);
INSERT INTO `product_images` VALUES (241, 34, '/uploads/products/d39b23cb-6000-44bf-80a1-bf5a300c85fb.png', 0);
INSERT INTO `product_images` VALUES (242, 34, '/uploads/products/5067a192-5924-4b4f-8ad8-04240b55159d.png', 0);
INSERT INTO `product_images` VALUES (243, 35, '/uploads/products/abcfb036-fbe9-45f5-a84e-559543af260c.png', 0);
INSERT INTO `product_images` VALUES (244, 35, '/uploads/products/5f8911f4-03a5-46b3-9bbf-febdad993706.png', 0);
INSERT INTO `product_images` VALUES (245, 35, '/uploads/products/9bac04c3-be0b-49e7-8a79-ff5e45588c10.png', 0);
INSERT INTO `product_images` VALUES (246, 35, '/uploads/products/69eaf30c-49bf-48ef-bcb1-541f124512dd.png', 0);
INSERT INTO `product_images` VALUES (247, 35, '/uploads/products/da28a745-7732-4873-8ad1-01a7c49dca2e.png', 0);
INSERT INTO `product_images` VALUES (248, 35, '/uploads/products/c01b404b-1ad7-48d4-8f9f-38b1bafa074d.png', 0);
INSERT INTO `product_images` VALUES (249, 35, '/uploads/products/0c2a0c87-f0c5-4ae5-8c3b-66c16dcd5b08.png', 0);
INSERT INTO `product_images` VALUES (250, 35, '/uploads/products/5d13d490-1180-4dfb-9e42-88586967a9a4.png', 0);
INSERT INTO `product_images` VALUES (251, 35, '/uploads/products/f71d0d01-2329-4c3f-8d15-c823cbffab0a.png', 0);
INSERT INTO `product_images` VALUES (252, 35, '/uploads/products/4e3b4b4c-6c3e-41e9-8442-4fc7383124ac.png', 0);
INSERT INTO `product_images` VALUES (253, 35, '/uploads/products/a74f8445-4490-43e9-9f3f-937d24051748.png', 0);
INSERT INTO `product_images` VALUES (254, 40, '/uploads/products/05046afa-2b00-45fa-9486-4a2e3bc6c346.png', 0);
INSERT INTO `product_images` VALUES (255, 40, '/uploads/products/d2239230-ac6d-4898-ac84-37c1afaa893b.png', 0);
INSERT INTO `product_images` VALUES (256, 40, '/uploads/products/5785b8c2-3d60-4da6-82f8-a8349ab2f0ac.png', 0);
INSERT INTO `product_images` VALUES (257, 41, '/uploads/products/8df732ff-a687-4b00-bb11-4a5d923647a0.png', 0);

-- ----------------------------
-- Table structure for products
-- ----------------------------
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `price` decimal(10, 2) NULL DEFAULT NULL,
  `thumbnail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_id` bigint NULL DEFAULT NULL,
  `status` tinyint NULL DEFAULT 1,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `category_id` int NULL DEFAULT NULL,
  `slug` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_products_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 42 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of products
-- ----------------------------
INSERT INTO `products` VALUES (31, 'Source Ngọc Rồng Tobi (Base Rose) - Đame Gốc', 5000000.00, '/uploads/products/9fc8425f-6742-4758-bcdf-347dc6be1780.png', '- Src Ngọc Rồng Rose từng online 8k người \n- Đame Gốc', 7, 1, '2026-03-05 04:15:20', '2026-03-06 18:24:02', 2, 'source-ngoc-rong-tobi-base-rose-dame-goc');
INSERT INTO `products` VALUES (32, 'Mod Local Long - Int ', 0.00, '/uploads/products/8827dac9-60dd-4d23-8ee4-f72b98ef6151.png', '1. Hỗ trợ đăng nhập source Int \n2. Hỗ trợ đăng nhập source long cơ bản\n3. Hỗ trợ đăng nhập source full long\n', 7, 1, '2026-03-05 04:28:23', '2026-03-05 04:28:23', 1, 'mod-local-long-int');
INSERT INTO `products` VALUES (33, 'Source Web Ngọc Rồng Đại Chiến', 0.00, '/uploads/products/1448eedf-7d90-4341-b469-b78bd664537f.png', '- Source web nhiều tính năng, auto nạp\n- Giao diện khá đẹp ', 7, 1, '2026-03-05 05:46:22', '2026-03-05 05:46:22', 10, 'source-web-ngoc-rong-dai-chien');
INSERT INTO `products` VALUES (34, 'Source Ngọc Rồng Donus (Emti) - Nhiều chức năng', 3000000.00, '/uploads/products/67924fd3-31c9-4fed-89c9-8e05afd65dff.png', '- Source Ngọc Rồng Donus (Emti) - Nhiều chức năng\n- Đame gốc - Trung\n- Đã từng bán giá 3m ', 7, 1, '2026-03-05 06:42:50', '2026-03-06 18:24:34', 2, 'source-ngoc-rong-donus-emti-nhieu-chuc-nang');
INSERT INTO `products` VALUES (35, 'Source Ngọc Rồng Chip - Nhiều chức năng, có biến hình, phân thân', 1000000.00, '/uploads/products/9d2a9498-0bdf-4b60-89cc-4a264d5fe177.png', '-Source Ngọc Rồng Chip \n- Nhiều chức năng, có biến hình, phân thân\n\n', 7, 1, '2026-03-05 14:46:52', '2026-03-06 18:24:59', 10, 'source-ngoc-rong-chip-nhieu-chuc-nang-co-bien-hinh-phan-than');
INSERT INTO `products` VALUES (36, 'Source Web Bùi kim trường ', 0.00, '/uploads/products/02a8f2dc-47e8-423d-b950-336abd94afaf.png', '- Source Web Bùi kim trường \n- Giao diện khá đẹp', 7, 1, '2026-03-05 14:49:04', '2026-03-05 14:49:04', 10, 'source-web-bui-kim-truong');
INSERT INTO `products` VALUES (37, 'Source Web Ngọc Rồng Hoa Sen', 0.00, '/uploads/products/ccc67bd3-90b3-44af-ac50-cfc2f6ab956f.png', '- Khá đẹp mắt\n- Có auto bank, auto card\n', 7, 1, '2026-03-05 17:48:13', '2026-03-06 18:17:41', 10, 'source-web-ngoc-rong-hoa-sen');
INSERT INTO `products` VALUES (38, 'Source Web Ngọc Rồng Hồi Ức', 0.00, '/uploads/products/8cf87f81-a03f-4468-99b4-2dfb285d2784.png', '- Source Web Ngọc Rồng Hồi Ức\n- Giao diện ổn, auto bank, autocard', 10, 1, '2026-03-06 18:00:57', '2026-03-06 18:00:57', 10, 'source-web-ngoc-rong-hoi-uc');
INSERT INTO `products` VALUES (39, 'Source Web Ngọc Rồng Tương Lai', 0.00, '/uploads/products/8f0cfb00-d5c7-4b81-b5fb-9e9b090e8a47.png', '-Source Web Ngọc Rồng Tương Lai\n-Giao diện đẹp , auto bank, auto nạp', 7, 1, '2026-03-06 18:05:28', '2026-03-06 18:18:05', 10, 'source-web-ngoc-rong-tuong-lai');
INSERT INTO `products` VALUES (40, 'Client Unity - Fake Hồi Sinh Ngọc Rồng - HTL', 1000000.00, '/uploads/products/8edfd730-8bf7-4349-9c87-3d357bee0bc6.png', '-Client Unity - Fake Hồi Sinh Ngọc Rồng - HTL\n-Client Mượt\n-Hỗ trợ server Int', 7, 1, '2026-03-07 07:04:49', '2026-03-07 07:04:49', 1, 'client-unity-fake-hoi-sinh-ngoc-rong-htl');
INSERT INTO `products` VALUES (41, 'Bộ công cụ cài đặt môi trường', 0.00, '/uploads/products/c46a0bd7-c011-427f-8545-81f246e88569.png', '- Trong đây chứa hầu hết thứ bạn cần trước khi cài đặt 1 server nro\n', 7, 1, '2026-03-08 04:12:15', '2026-03-08 04:12:15', 8, 'bo-cong-cu-cai-dat-moi-truong');

-- ----------------------------
-- Table structure for refresh_tokens
-- ----------------------------
DROP TABLE IF EXISTS `refresh_tokens`;
CREATE TABLE `refresh_tokens`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `jti` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `user_id` int NULL DEFAULT NULL,
  `expired_at` timestamp NULL DEFAULT NULL,
  `revoked` tinyint NULL DEFAULT NULL,
  `replaced_by_jti` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `revoked_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of refresh_tokens
-- ----------------------------
INSERT INTO `refresh_tokens` VALUES (3, '74efa1c8-b549-46f9-8292-1dee2718dd03', 7, '2026-03-16 17:51:43', 1, '74efa1c8-b549-46f9-8292-1dee2718dd03', '2026-03-09 17:51:43', '2026-03-11 01:28:50');
INSERT INTO `refresh_tokens` VALUES (4, '74efa1c8-b549-46f9-8292-1dee2718dd03', 7, '2026-03-18 01:28:50', 0, NULL, '2026-03-11 01:28:50', NULL);
INSERT INTO `refresh_tokens` VALUES (5, '1405b7fb-52a0-4d25-b845-fca60a0f6f34', 7, '2026-03-18 01:30:14', 0, NULL, '2026-03-11 01:30:14', NULL);

-- ----------------------------
-- Table structure for reviews
-- ----------------------------
DROP TABLE IF EXISTS `reviews`;
CREATE TABLE `reviews`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `rating` int NULL DEFAULT NULL,
  `comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `product_id`(`product_id` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `reviews_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `reviews_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of reviews
-- ----------------------------

-- ----------------------------
-- Table structure for roles
-- ----------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of roles
-- ----------------------------
INSERT INTO `roles` VALUES (1, 'ROLE_USER');
INSERT INTO `roles` VALUES (2, 'ROLE_ADMIN');
INSERT INTO `roles` VALUES (3, 'ROLE_STAFF');

-- ----------------------------
-- Table structure for system_bank_account
-- ----------------------------
DROP TABLE IF EXISTS `system_bank_account`;
CREATE TABLE `system_bank_account`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `bank_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `bank_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `account_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `account_name` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `priority` tinyint(1) NOT NULL DEFAULT 1,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_default` tinyint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_bank_account
-- ----------------------------
INSERT INTO `system_bank_account` VALUES (1, 'MB', 'MBBANK', '120220032015', 'PHAM VAN XUNG', 1, '2025-12-31 11:22:18', '2026-01-01 15:37:23', 1);

-- ----------------------------
-- Table structure for topup_intent
-- ----------------------------
DROP TABLE IF EXISTS `topup_intent`;
CREATE TABLE `topup_intent`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NULL DEFAULT NULL,
  `status` char(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `amount` int NULL DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `expired_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_topup_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of topup_intent
-- ----------------------------

-- ----------------------------
-- Table structure for user_roles
-- ----------------------------
DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE `user_roles`  (
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  INDEX `FKh8ciramu9cc9q3qcqiv4ue8a6`(`role_id` ASC) USING BTREE,
  INDEX `FKhfh9dx7w3ubf1co1vdev94g3f`(`user_id` ASC) USING BTREE,
  CONSTRAINT `FKh8ciramu9cc9q3qcqiv4ue8a6` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKhfh9dx7w3ubf1co1vdev94g3f` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_roles
-- ----------------------------
INSERT INTO `user_roles` VALUES (7, 1);
INSERT INTO `user_roles` VALUES (7, 2);
INSERT INTO `user_roles` VALUES (9, 1);
INSERT INTO `user_roles` VALUES (13, 1);
INSERT INTO `user_roles` VALUES (14, 1);
INSERT INTO `user_roles` VALUES (15, 1);
INSERT INTO `user_roles` VALUES (16, 1);
INSERT INTO `user_roles` VALUES (8, 1);
INSERT INTO `user_roles` VALUES (8, 3);
INSERT INTO `user_roles` VALUES (10, 1);
INSERT INTO `user_roles` VALUES (10, 3);
INSERT INTO `user_roles` VALUES (11, 1);
INSERT INTO `user_roles` VALUES (11, 3);
INSERT INTO `user_roles` VALUES (17, 1);

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `fullname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `google_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `auth_provider` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `total_vnd` int NULL DEFAULT 0,
  `vnd` int NULL DEFAULT 0,
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `phonenumber` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `created_at` datetime NULL DEFAULT NULL,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_active` tinyint(1) NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `unique_email`(`email` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (7, 'Phạm Văn Xuggg', 'phmaxung@gmail.com', 'xungdeptry', '$2a$10$MJJx52H4KyyxJQuENa6O.uTpypBYf/VjSy18mnD3X4r951VDzVjMK', '114507758618292431304', 'LOCAL', 0, 0, NULL, '0388001659', '2025-11-26 11:26:53', '2026-03-04 01:44:11', 1);
INSERT INTO `users` VALUES (8, NULL, 'phmaxung@mail.com', 'admin', '$2a$10$nxTKXLmZQ5XM3dVLPAZpN.HHnrS3k/GU29V.aZ1E5T8EPfls8E93S', NULL, 'LOCAL', 0, 0, NULL, NULL, '2025-12-12 09:38:20', '2026-01-27 14:53:43', 1);
INSERT INTO `users` VALUES (9, 'Phạm Văn Xung', 'phmaxu1ng@gmail.com', 'xung', '$2a$10$AqjswPBJKujl9wHOHfc5IeohK0.QfNqeCmQ0U4o7suWeN7AZ4nJyK', NULL, 'LOCAL', 0, 0, '', '0388001659', '2025-12-25 01:42:22', '2026-01-27 14:53:43', 1);
INSERT INTO `users` VALUES (10, NULL, 'phma2xung@gmail.com', 'xung1', '$2a$10$o3thfmSXRZSTxdP1XyxnpuRkCHToPT0X3njn3fNLQgcXWYSR9ViOW', NULL, 'LOCAL', 0, 0, '', NULL, '2025-12-25 01:45:34', '2026-01-27 14:53:43', 1);
INSERT INTO `users` VALUES (11, 'Phạm Văn Xung', 'phmaxung1@gmail.com', 'user', '$2a$10$tewuM3dKejmJ8iRNbx8OvuK3mt02A1gf2wXbCbRvOhkf8Wtr9NChC', NULL, 'LOCAL', 0, 100000, '', '0338095081', '2026-01-27 09:33:42', '2026-01-27 14:53:43', 1);
INSERT INTO `users` VALUES (13, 'deptryyy xung', 'xungdzsc1@gmail.com', NULL, NULL, '117093469507690278039', 'GOOGLE', 0, 0, NULL, NULL, '2026-01-28 14:07:49', NULL, 1);
INSERT INTO `users` VALUES (14, 'future ngocrong', 'ngocrongfuture@gmail.com', NULL, NULL, '110377588679953984571', 'GOOGLE', 0, 0, NULL, NULL, '2026-01-28 18:58:21', NULL, 1);
INSERT INTO `users` VALUES (15, 'Văn Xung Phạm', 'phmaxung@gmaissl.com', NULL, NULL, '114386723144243639066', 'GOOGLE', 0, 0, NULL, NULL, '2026-02-02 10:41:37', '2026-03-01 22:21:36', 1);
INSERT INTO `users` VALUES (16, NULL, 'xungcute@deptry.com', 'xung12', '$2a$10$2eP.HNXysC.493H/I8ico.0kwxK1Nm22LJfCX5pFi87T.f01a6zK6', NULL, NULL, 0, 0, '', NULL, '2026-03-02 05:04:54', NULL, 1);
INSERT INTO `users` VALUES (17, NULL, 'phmaxung1231231@gmail.com', 'xungdeptry11111', '$2a$10$diz7krIt9scDmuHTgrHljez2dOuUevcV.P2p9XI6ThkoRLiJI3W/W', NULL, NULL, 0, 0, '', NULL, '2026-03-08 23:30:47', NULL, 1);

SET FOREIGN_KEY_CHECKS = 1;
