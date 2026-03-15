/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.security;

/**
 *
 * @author ZZ
 */
public class SecurityConstants {

    public static final String[] PUBLIC_URLS
            = {
                "/api/auth/login",
                "/api/auth/register",
                "/api/auth/logout",
                "/api/auth/fresh-token",
                "/api/auth/google-login",
                "/api/products",
                "/api/products/**",
                "/api/products/slug/**",
                "/api/products/category/**",
                "/api/products/*/reviews",
                "/api/products/reviews/*/list",
                "/api/products/reviews/can-rate/**",
                "/uploads/products/**",
                "/api/webhook",
                "/files/**",
                "/api/cart/coupon/**",
                "/api/search",
                "/api/callback",
                "/api/categories"
            };

    public static final String[] ADMIN_URLS = {
        "/api/admin/elastic/sync",
        "/api/admin/*/staff",
        "/api/admin/roles",
        "/api/admin/users/**",
        "/api/admin/coupons/**",
        "/api/admin/products"
    };

    public static final String[] ADMIN_STAFF_URLS = {
        "/api/admin/products/**",
        "/api/admin/products/change-status/**",
        "/api/admin/categories"
    };

    public static final String[] ADMIN_USER_STAFF_URLS = {
        "/api/topup",
        "/api/order/checkout",
        "/api/order",
        "/api/order/status/**",
        "/api/order/**",
        "/api/auth/me",
        "/api/users/topup-history",
        "/api/cart/update-qty",
        "/api/cart/**",
        "/api/cart",
        "/api/charging"};
}
