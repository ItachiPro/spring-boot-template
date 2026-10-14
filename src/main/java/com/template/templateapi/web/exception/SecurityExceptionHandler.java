package com.template.templateapi.web.exception;

import com.template.templateapi.web.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class SecurityExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    public SecurityExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        var apiResponse = ApiResponse.error(
                authException.getMessage(),
                null,
                HttpStatus.UNAUTHORIZED
        );

        writeResponse(response, apiResponse);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        var apiResponse = ApiResponse.error(
                accessDeniedException.getMessage(),
                null,
                HttpStatus.FORBIDDEN);

        writeResponse(response, apiResponse);
    }

    private void writeResponse(
            HttpServletResponse response,
            org.springframework.http.ResponseEntity<?> apiResponse
    ) throws IOException {

        response.setStatus(apiResponse.getStatusCode().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(
                objectMapper.writeValueAsString(apiResponse.getBody())
        );
    }
}
