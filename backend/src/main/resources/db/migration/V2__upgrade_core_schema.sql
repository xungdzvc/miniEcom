-- V2: Upgrade the existing production schema toward the current local schema.
-- Data-preserving policy:
--   * widening is allowed;
--   * shrinking VARCHAR/TEXT conversions are guarded by deploy/preflight.sql;
--   * old rows are backfilled before NOT NULL/default-sensitive columns are finalized;
--   * existing semantic defaults are preserved when they are safer than local DDL.

-- USERS: wallet + optimistic locking
ALTER TABLE `users`
  ADD COLUMN `current_balance` DECIMAL(19,4) NOT NULL DEFAULT 0.0000,
  ADD COLUMN `lifetime_deposit` DECIMAL(19,4) NOT NULL DEFAULT 0.0000,
  ADD COLUMN `version` BIGINT NULL DEFAULT NULL;

-- BANK ACCOUNTS: widen identifiers and add audit column.
ALTER TABLE `bank_accounts`
  MODIFY COLUMN `bank_code` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  MODIFY COLUMN `bank_name` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  MODIFY COLUMN `account_number` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  MODIFY COLUMN `account_name` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  ADD COLUMN `updated_at` DATETIME(6) NULL;
UPDATE `bank_accounts`
SET `updated_at` = COALESCE(`created_at`, CURRENT_TIMESTAMP(6))
WHERE `updated_at` IS NULL;
ALTER TABLE `bank_accounts`
  MODIFY COLUMN `updated_at` DATETIME(6) NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);

-- CATEGORIES: hierarchical category support.
ALTER TABLE `categories`
  ADD COLUMN `status` BIT(1) NULL DEFAULT NULL,
  ADD COLUMN `parent_id` BIGINT NULL DEFAULT NULL;

-- COUPON: widen identifiers and discount.
ALTER TABLE `coupon_code`
  MODIFY COLUMN `id` BIGINT NOT NULL AUTO_INCREMENT,
  MODIFY COLUMN `coupon_code` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  MODIFY COLUMN `discount` INT NULL DEFAULT NULL;

-- ORDER ID is referenced by order_items: temporarily remove the old FK.
ALTER TABLE `order_items`
  DROP FOREIGN KEY `fk_orderdetail_order`;

-- ORDERS: BIGINT IDs, decimal money, timestamps, optimistic locking.
ALTER TABLE `orders`
  MODIFY COLUMN `id` BIGINT NOT NULL AUTO_INCREMENT,
  MODIFY COLUMN `total` DECIMAL(38,2) NULL DEFAULT NULL,
  ADD COLUMN `created_at` DATETIME(6) NULL,
  ADD COLUMN `updated_at` DATETIME(6) NULL,
  ADD COLUMN `version` BIGINT NULL DEFAULT NULL;

UPDATE `orders`
SET
  `created_at` = COALESCE(`created_at`, `order_date`, CURRENT_TIMESTAMP(6)),
  `updated_at` = COALESCE(`updated_at`, `order_date`, CURRENT_TIMESTAMP(6));

ALTER TABLE `orders`
  MODIFY COLUMN `created_at` DATETIME(6) NULL DEFAULT CURRENT_TIMESTAMP(6),
  MODIFY COLUMN `updated_at` DATETIME(6) NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);

-- ORDER ITEMS: align PK/FKs with BIGINT and preserve monetary decimals.
ALTER TABLE `order_items`
  MODIFY COLUMN `id` BIGINT NOT NULL AUTO_INCREMENT,
  MODIFY COLUMN `order_id` BIGINT NOT NULL,
  MODIFY COLUMN `product_id` BIGINT NOT NULL,
  MODIFY COLUMN `price` DECIMAL(38,2) NULL DEFAULT NULL;

-- PAYMENT TRANSACTIONS.
-- transaction_content is shrunk only because current entity-generated local DDL expects VARCHAR(255).
-- deploy/preflight.sql MUST be clean before this migration is applied.
ALTER TABLE `payment_transactions`
  MODIFY COLUMN `payment_name` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  MODIFY COLUMN `amount` DECIMAL(38,2) NULL DEFAULT NULL,
  MODIFY COLUMN `currency` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'VND',
  MODIFY COLUMN `transaction_content` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  MODIFY COLUMN `card_type` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  MODIFY COLUMN `card_code` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  MODIFY COLUMN `card_serial` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  MODIFY COLUMN `bank_account` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  MODIFY COLUMN `match_ref` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  ADD COLUMN `updated_at` DATETIME(6) NULL;

UPDATE `payment_transactions`
SET `updated_at` = COALESCE(`created_at`, CURRENT_TIMESTAMP(6))
WHERE `updated_at` IS NULL;

ALTER TABLE `payment_transactions`
  MODIFY COLUMN `updated_at` DATETIME(6) NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);

-- PRODUCTS: the production dump currently fits VARCHAR(255); preflight re-checks live data.
ALTER TABLE `products`
  MODIFY COLUMN `price` DECIMAL(38,2) NULL DEFAULT NULL,
  MODIFY COLUMN `description` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  MODIFY COLUMN `status` BIT(1) NULL DEFAULT NULL,
  MODIFY COLUMN `category_id` BIGINT NOT NULL,
  MODIFY COLUMN `slug` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  ADD COLUMN `version` BIGINT NULL DEFAULT NULL;

-- PRODUCT DETAIL: align generated schema. Preflight protects all VARCHAR(255) conversions.
ALTER TABLE `product_detail`
  MODIFY COLUMN `download_url` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  MODIFY COLUMN `youtube_url` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  MODIFY COLUMN `demo_url` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  MODIFY COLUMN `technology` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  MODIFY COLUMN `install_tutorial` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  MODIFY COLUMN `pin` BIT(1) NULL DEFAULT NULL,
  ADD COLUMN `version` BIGINT NULL DEFAULT NULL;

ALTER TABLE `product_images`
  MODIFY COLUMN `image_url` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL;

-- REFRESH TOKENS.
ALTER TABLE `refresh_tokens`
  MODIFY COLUMN `id` BIGINT NOT NULL AUTO_INCREMENT,
  MODIFY COLUMN `user_id` BIGINT NULL DEFAULT NULL,
  MODIFY COLUMN `revoked` BIT(1) NULL DEFAULT NULL,
  ADD COLUMN `updated_at` DATETIME(6) NULL;

UPDATE `refresh_tokens`
SET `updated_at` = COALESCE(`revoked_at`, `created_at`, CURRENT_TIMESTAMP(6))
WHERE `updated_at` IS NULL;

ALTER TABLE `refresh_tokens`
  MODIFY COLUMN `updated_at` DATETIME(6) NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);

-- REVIEWS.
ALTER TABLE `reviews`
  MODIFY COLUMN `comment` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  ADD COLUMN `updated_at` DATETIME(6) NULL;

UPDATE `reviews`
SET `updated_at` = COALESCE(`created_at`, CURRENT_TIMESTAMP(6))
WHERE `updated_at` IS NULL;

ALTER TABLE `reviews`
  MODIFY COLUMN `updated_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);

-- SYSTEM BANK ACCOUNT: widen display fields; retain safer required fields.
ALTER TABLE `system_bank_account`
  MODIFY COLUMN `bank_code` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  MODIFY COLUMN `bank_name` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  MODIFY COLUMN `account_number` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  MODIFY COLUMN `account_name` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  MODIFY COLUMN `is_default` BIT(1) NULL DEFAULT NULL;

-- TOPUP INTENT.
ALTER TABLE `topup_intent`
  MODIFY COLUMN `id` BIGINT NOT NULL AUTO_INCREMENT,
  MODIFY COLUMN `user_id` BIGINT NOT NULL,
  MODIFY COLUMN `amount` DECIMAL(38,2) NULL DEFAULT NULL,
  ADD COLUMN `updated_at` DATETIME(6) NULL,
  ADD COLUMN `payment_status` ENUM('FAILED','PENDING','SUCCESS','UNMATCH','WRONG_AMOUNT')
    CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL;

UPDATE `topup_intent`
SET `updated_at` = COALESCE(`created_at`, CURRENT_TIMESTAMP(6))
WHERE `updated_at` IS NULL;

ALTER TABLE `topup_intent`
  MODIFY COLUMN `updated_at` DATETIME(6) NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);
