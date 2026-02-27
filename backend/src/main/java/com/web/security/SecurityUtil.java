package com.web.security;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.web.enums.Role;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

public class SecurityUtil {
    public static Long getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails cud) || cud.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return cud.getUserId();
    }

    public static String getRoles (){
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities()
                .stream()
                .map(a -> a.getAuthority().replace("ROLE_",""))
                .collect(Collectors.joining(","));

    }

    public static boolean isAdmin() {
        return getRoles().contains(Role.ADMIN.value);
    }

    public static boolean isStaff() {
        return getRoles().contains(Role.STAFF.value);
    }

}
