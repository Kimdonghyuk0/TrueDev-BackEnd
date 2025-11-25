package com.kdh.truedev.exception;

import com.kdh.truedev.base.dto.response.ApiResponse;
import com.kdh.truedev.springSecurity.exception.InvalidRefreshTokenException;
import com.kdh.truedev.springSecurity.exception.InvalidTokenException;
import com.kdh.truedev.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 예외 매핑 (검증 400 / 기타 500)
@RestControllerAdvice
public class ApiErrorHandler {

    @ExceptionHandler({ MethodArgumentNotValidException.class })
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("invalid_request"));
    }

    @ExceptionHandler(UserService.UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(UserService.UnauthorizedException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("unauthorized"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleServerError(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("internal_server_error"));
    }

    @ExceptionHandler(UserService.InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidCredentials(UserService.InvalidCredentialsException ex) {
        // 로그인 실패 → 401 Unauthorized
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ex.getMessage())); // "invalid_credentials"
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("invalid_credentials"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        // currentPassword_unauthorized, password_duplicated, 그 외 등등 전부 400
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler({InvalidRefreshTokenException.class, InvalidTokenException.class})
    public ResponseEntity<ApiResponse<Void>> handleInvalidTokens(RuntimeException ex) {
        // 잘못되었거나 만료된 토큰 → 401
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ex.getMessage()));
    }
}
