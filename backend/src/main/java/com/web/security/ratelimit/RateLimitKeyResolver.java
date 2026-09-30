package com.web.security.ratelimit;

import com.web.enums.RateLimitScope;
import com.web.util.Utils;
import jakarta.servlet.http.HttpServletRequest;
import jdk.jshell.execution.Util;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class RateLimitKeyResolver {
    public String resolve(HttpServletRequest request, RateLimitScope scope){
        String ip = Utils.getClientIp(request);

        Authentication authentication  = SecurityContextHolder.getContext().getAuthentication();
        boolean authenticated = isAuthenticated();
        String username = authenticated ? authentication.getName() :null;
        return switch (scope){
            case IP->
                "ip:" + ip;
            case USER ->
                authenticated ? "user:" +username : "uknow";
            case USER_OR_IP ->
                 authenticated ? "user:" +username : "ip:" + ip;
            case USER_AND_IP ->
                authenticated ? "user:" + username + "ip:"+ip : "uknow:"+"ip:"+ip;

        };

    }

    public boolean isAuthenticated(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken);
    }
}
