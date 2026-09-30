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
import lombok.extern.slf4j.Slf4j;
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
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
@Slf4j
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
        System.out.println("Request to : "+request.getServletPath());
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
            if (jwtUtils.isAccessToken(token)) {
                Long userId = jwtUtils.getUserId(token);

                UserEntity user = userRepository.findWithRolesById(userId)
                        .orElseThrow();

                CustomUserDetails userDetails = new CustomUserDetails(user);

                UsernamePasswordAuthenticationToken authentication
                        = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (ExpiredJwtException e) {
            Utils.handleException(response, "token hết hạn ");
            log.error("Token hết hạn ",e);
            return;

        } catch (Exception e) { 
            Utils.handleException(response, "Xác thực không thành công");
            log.error("Xác thực không thành công ",e);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isBypassToken(@NonNull HttpServletRequest request) {
        String path = request.getServletPath(); 
        for (String bypass : SecurityConstants.PUBLIC_URLS) {
            if (pathMatcher.match(bypass, path)) {
                return true;
            }
        }
        return false;
    }

}
