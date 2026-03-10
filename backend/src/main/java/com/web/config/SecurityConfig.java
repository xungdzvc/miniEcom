package com.web.config;

import com.web.entity.UserEntity;
import com.web.enums.Role;
import com.web.exception.MyException;
import com.web.repository.UserRepository;
import com.web.security.CustomUserDetails;
import com.web.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    @Autowired
    private UserRepository userRepository;

    private final UserDetailsService UserDetailsService;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            UserEntity user = userRepository.findByUsername(username);
            if (user == null) {
                throw new MyException(username + " not exists");
            }
            return new CustomUserDetails(user);
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOrigins(List.of("http://localhost:4200", "http://127.0.0.1:4200"));
            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            config.setAllowedHeaders(List.of("*"));
            config.setAllowCredentials(true);

            return config;
        }))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/login", "/api/webhook", "/api/auth/register",
                        "/api/products", "/api/auth/logout", "/api/auth/fresh-token", "/files/**",
                        "/api/products/**", "/api/products/slug/**", "/api/webhook", "/api/auth/google-login",
                        "/api/cart/coupon/**",
                        "/api/products/category/**",
                        "/api/products/*/reviews",
                        "/api/products/reviews/*/list",
                        "/api/search",
                        "/api/callback",
                        "/api/categories",
                        "/uploads/products/**",
                        "/error").permitAll()
                .requestMatchers("/api/admin/elastic/sync").hasAnyRole(Role.ADMIN.roleName())
                .requestMatchers("/api/admin/*/staff").hasAnyRole(Role.ADMIN.roleName())
                .requestMatchers("/api/admin/roles").hasAnyRole(Role.ADMIN.roleName())
                .requestMatchers("/api/admin/users/**").hasAnyRole(Role.ADMIN.roleName())
                .requestMatchers("/api/admin/coupons/**").hasAnyRole(Role.ADMIN.roleName())
                .requestMatchers("/api/admin/products").hasAnyRole(Role.ADMIN.roleName(), Role.STAFF.roleName())
                .requestMatchers("/api/admin/products/**").hasAnyRole(Role.ADMIN.roleName(), Role.STAFF.roleName())
                .requestMatchers("/api/admin/products/change-status/**").hasAnyRole(Role.ADMIN.roleName(), Role.STAFF.roleName())
                .requestMatchers("/api/topup").hasAnyRole(Role.ADMIN.roleName(), Role.STAFF.roleName(), Role.USER.roleName())
                .requestMatchers("/api/order/checkout").hasAnyRole(Role.ADMIN.roleName(), Role.STAFF.roleName(), Role.USER.roleName())
                .requestMatchers("/api/order").hasAnyRole(Role.ADMIN.roleName(), Role.STAFF.roleName(), Role.USER.roleName())
                .requestMatchers("/api/order/status/**").hasAnyRole(Role.ADMIN.roleName(), Role.STAFF.roleName(), Role.USER.roleName())
                .requestMatchers("/api/order/**").hasAnyRole(Role.ADMIN.roleName(), Role.STAFF.roleName(), Role.USER.roleName())
                .requestMatchers("/api/admin/categories").hasAnyRole(Role.ADMIN.roleName(), Role.STAFF.roleName())
                .requestMatchers("/api/auth/fresh-token").hasAnyRole(Role.ADMIN.roleName(), Role.STAFF.roleName(), Role.USER.roleName())
                .requestMatchers("/api/auth/me").hasAnyRole(Role.USER.roleName(), Role.ADMIN.roleName(), Role.STAFF.roleName())
                .requestMatchers("/api/users/topup-history").hasAnyRole(Role.USER.roleName(), Role.ADMIN.roleName(), Role.STAFF.roleName())
                .requestMatchers("/api/cart/update-qty").hasAnyRole(Role.USER.roleName(), Role.ADMIN.roleName(), Role.STAFF.roleName())
                .requestMatchers("/api/cart").hasAnyRole(Role.USER.roleName(), Role.ADMIN.roleName(), Role.STAFF.roleName())
                .requestMatchers("/api/cart/add").hasAnyRole(Role.USER.roleName(), Role.ADMIN.roleName(), Role.STAFF.roleName())
                .requestMatchers("/api/products/reviews/can-rate/**").hasAnyRole(Role.USER.roleName(), Role.ADMIN.roleName(), Role.STAFF.roleName())
                .requestMatchers("/api/charging").hasAnyRole(Role.USER.roleName(), Role.ADMIN.roleName(), Role.STAFF.roleName())
                .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
