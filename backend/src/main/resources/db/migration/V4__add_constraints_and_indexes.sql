-- V4: Add indexes and referential integrity after data/type migrations.
-- Preflight verifies the current production data has no orphan rows.

-- cart_items
ALTER TABLE `cart_items`
  ADD CONSTRAINT `FK1re40cjegsfvw58xrkdp6bac6`
    FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
    ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `FKpcttvuq4mxppo8sxggjtn5i2c`
    FOREIGN KEY (`cart_id`) REFERENCES `carts` (`id`)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

-- categories hierarchy
ALTER TABLE `categories`
  ADD INDEX `FKsaok720gsu4u2wrgbk10b5n8d` (`parent_id` ASC),
  ADD CONSTRAINT `FKsaok720gsu4u2wrgbk10b5n8d`
    FOREIGN KEY (`parent_id`) REFERENCES `categories` (`id`)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

-- order_items.
-- Preserve the production behavior for order deletion (CASCADE) for rollback compatibility.
ALTER TABLE `order_items`
  ADD CONSTRAINT `fk_orderdetail_order`
    FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
    ON DELETE CASCADE ON UPDATE RESTRICT,
  ADD CONSTRAINT `FKocimc7dtr037rh4ls4l95nlfi`
    FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

-- products
ALTER TABLE `products`
  ADD INDEX `idx_product_status` (`status` ASC),
  ADD INDEX `idx_product_category_id` (`category_id` ASC),
  ADD INDEX `idx_product_slug` (`slug` ASC),
  ADD CONSTRAINT `FKog2rp4qthbtt2lfyhfo32lsw9`
    FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

-- refresh_tokens
ALTER TABLE `refresh_tokens`
  ADD INDEX `FK1lih5y2npsf8u5o3vhdb9y0os` (`user_id` ASC),
  ADD CONSTRAINT `FK1lih5y2npsf8u5o3vhdb9y0os`
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

-- topup_intent
ALTER TABLE `topup_intent`
  ADD CONSTRAINT `FKry3f3p74fc937ffw3ir85ly3f`
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
    ON DELETE RESTRICT ON UPDATE RESTRICT;
