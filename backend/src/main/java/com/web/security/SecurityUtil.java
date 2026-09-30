package com.web.security;
  
import java.util.stream.Collectors;

import com.web.enums.Role; 
import org.springframework.security.core.Authentication; 
import org.springframework.security.core.context.SecurityContextHolder; 

public class SecurityUtil {
    public static Long getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails cud) || cud.getUserId() == null) {
            return null;
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
        return getRoles().contains(Role.ADMIN.roleName());
    }

    public static boolean isStaff() {
        return getRoles().contains(Role.STAFF.roleName());
    }

}
