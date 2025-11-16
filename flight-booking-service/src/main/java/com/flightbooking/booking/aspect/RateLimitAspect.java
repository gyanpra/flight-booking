package com.flightbooking.booking.aspect;

import com.flightbooking.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitAspect {
    
    private final RedissonClient redissonClient;
    
    @Around("@annotation(com.flightbooking.booking.aspect.UserRateLimit)")
    public Object aroundUserRateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        String userId = extractUserId(args);
        
        if (userId != null) {
            String rateLimitKey = "rate-limit:user:" + userId;
            RRateLimiter rateLimiter = redissonClient.getRateLimiter(rateLimitKey);
            rateLimiter.trySetRate(RateType.OVERALL, 10, 1, RateIntervalUnit.MINUTES);
            
            if (!rateLimiter.tryAcquire()) {
                throw new BusinessException("Rate limit exceeded. Please try again later.");
            }
        }
        
        return joinPoint.proceed();
    }
    
    private String extractUserId(Object[] args) {
        for (Object arg : args) {
            if (arg != null && arg.getClass().getSimpleName().contains("Request")) {
                try {
                    var method = arg.getClass().getMethod("getUserId");
                    Object result = method.invoke(arg);
                    return result != null ? result.toString() : null;
                } catch (Exception e) {
                    log.debug("Could not extract userId from request", e);
                }
            }
        }
        return null;
    }
}
