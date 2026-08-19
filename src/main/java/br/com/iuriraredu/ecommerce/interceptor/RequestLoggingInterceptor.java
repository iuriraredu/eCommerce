package br.com.iuriraredu.ecommerce.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle (HttpServletRequest request, HttpServletResponse response, Object handler){
        log.info("------ New Request ------");
        log.info("Method: {}", request.getMethod());
        log.info("URI: {}", request.getRequestURI());
        log.info("User-Agent: {}", request.getHeader("User-Agent"));
        log.info("Authorization: {}", maskAuthorizationHeader(request.getHeader("Authorization")));
        log.info("Content-Type: {}", request.getHeader("Content-Type"));
        log.info("-------------------------");
        return true;
    }

    private String maskAuthorizationHeader(String authHeader) {
        if (authHeader == null) {
            return "não informado";
        }
        return "presente (oculto)";
    }
}

