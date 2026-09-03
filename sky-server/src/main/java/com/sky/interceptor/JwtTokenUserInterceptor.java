package com.sky.interceptor;

import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.properties.JwtProperties;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtProperties jwtProperties;
    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler){
        if (!(handler instanceof HandlerMethod)){
            return true;
        }
        String token = request.getHeader(
                jwtProperties.getUserTokenName()
        );
        try {
            log.info("用户端JWT校验：{} ",token);
            Claims claims = JwtUtil.parseJWT(
                    jwtProperties.getUserSecretKey(), token
            );
            Long userId = Long.valueOf(
                    claims.get(
                            JwtClaimsConstant.USER_ID
                    ).toString()
            );
            BaseContext.setCurrentId(userId);
            log.info("当前用户ID：{}", userId);
            return true;
        } catch (Exception ex){
            log.error(
                    MessageConstant.USER_NOT_LOGIN,
                    ex
            );
            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );
            return false;
        }
    }

}
