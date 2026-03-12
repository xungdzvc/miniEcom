package com.web.security;

import com.web.entity.UserEntity;
import com.web.repository.UserRepository;
import com.web.service.impl.CustomUserDetailsService;
import com.web.util.Utils;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.AntPathMatcher;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (isBypassToken(request)) {
            filterChain.doFilter(request, response);
            return;
        }
    
        String authHeader = request.getHeader("Authorization");
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            if(jwtUtils.isAccessToken(token)){
                Long userId = jwtUtils.getUserId(token);
                UserEntity user = userRepository.findById(userId)
                        .orElseThrow();

                CustomUserDetails userDetails = new CustomUserDetails(user);
                UsernamePasswordAuthenticationToken authentication
                        = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            

        } catch (ExpiredJwtException e) {
            Utils.handleException(response, "token hết hạn");
            return;

        } catch (Exception e) {
            Utils.handleException(response, "Xác thực không thành công");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isBypassToken(@NonNull HttpServletRequest request) {
        final List<Pair<String, String>> bypassTokens = Arrays.asList(
                Pair.of("/api/auth/logout", "POST"),
                Pair.of("/api/auth/google-login", "POST"),
                Pair.of("/api/auth/login", "POST"),
                Pair.of("/api/auth/register", "POST"),
                Pair.of("/api/auth/fresh-token", "POST"),
                Pair.of("/api/products", "GET"),
                Pair.of("/api/products/reviews/*/list", "GET"),
                Pair.of("/api/categories", "GET"),
                Pair.of("/api/search", "GET"),
                Pair.of("/uploads/products/**", "GET"),
                Pair.of("/api/products/slug/**", "PUT"),
                Pair.of("/api/callback", "POST")

        );

        String path = request.getServletPath();
        String method = request.getMethod();

        for (Pair<String, String> bypass : bypassTokens) {
            if (pathMatcher.match(bypass.getFirst(), path)
                    && method.equalsIgnoreCase(bypass.getSecond())) {
                return true;
            }
        }
        return false;
    }

}
