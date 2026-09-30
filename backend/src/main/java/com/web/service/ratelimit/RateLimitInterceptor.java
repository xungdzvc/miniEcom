package com.web.service.ratelimit;

import com.web.exception.TooManyRequestsException;
import com.web.security.ratelimit.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {
    private final RateLimitService rateLimitService;
    private final RateLimitProperties rateLimitProperties;
    private final RateLimitKeyResolver keyResolver;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,Object handle){
        if(!rateLimitProperties.isEnabled()){
            return true;
        }

        String glocalPolicy = keyResolver.isAuthenticated() ? "global-authenticated" : "global-anonymous";
        endforce(glocalPolicy,request,response);

        if(handle instanceof HandlerMethod handlerMethod){
            RateLimited annotation = handlerMethod.getMethodAnnotation(RateLimited.class);

            if(annotation == null){
                annotation = handlerMethod.getBeanType().getAnnotation(RateLimited.class);
            }
            if(annotation != null){
                endforce(annotation.value(),request,response);
            }
        }
        return true;

    }

    private void endforce(String policyName, HttpServletRequest request, HttpServletResponse response){
        RateLimitProperties.Policy policy = rateLimitProperties.getPolicies().get(policyName);

        if(policy == null){
            throw new IllegalArgumentException("Rate limit policy not found "+policyName);
        }

        String identity = keyResolver.resolve(request,policy.getScope());
        String key =  "rl:"+policyName+ ":" + identity;
        RateLimitResult result = rateLimitService.consume(key,policy.getLimit(),policy.getWindow());
        response.setHeader("X-rateLimit-Remaining",String.valueOf(result.remaining()));

        if(!result.allowed()){
            throw new TooManyRequestsException(policy.getMessage(), result.retryAfterSeconds());
        }

    }
}
