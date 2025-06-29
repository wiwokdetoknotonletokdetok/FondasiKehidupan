package org.gaung.wiwokdetok.fondasikehidupan.security.aspect;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.gaung.wiwokdetok.fondasikehidupan.security.JwtUtil;
import org.gaung.wiwokdetok.fondasikehidupan.security.annotation.AllowedRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Aspect
@Component
public class AllowedRolesAspect {

    @Autowired
    private JwtUtil JwtUtil;

    @Before("@annotation(org.gaung.wiwokdetok.fondasikehidupan.security.annotation.AllowedRoles)")
    public void checkAllowedRoles(JoinPoint joinPoint) {
        String token = getToken();

        Claims payload;

        try {
            payload = JwtUtil.decodeToken(token);
        } catch (ExpiredJwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token expired");
        } catch (JwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        String roleFromToken = JwtUtil.getRole(payload);

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        AllowedRoles allowedRoles = method.getAnnotation(AllowedRoles.class);
        List<String> allowed = Arrays.asList(allowedRoles.value());

        if (!allowed.contains(roleFromToken)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }
    }

    private static String getToken() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        HttpServletRequest request = attrs.getRequest();
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid token");
        }

        token = token.substring(7);
        return token;
    }
}
