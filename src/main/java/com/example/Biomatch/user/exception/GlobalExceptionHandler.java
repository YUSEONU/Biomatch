package com.example.Biomatch.user.exception;

import com.example.Biomatch.user.responseDTO.ErrorResponse;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // 유효성 검사 실패 - 400 에러
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage(), ex);

        // 필드별로 메시지를 그룹화하여 처리
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> {
                    // 필수 입력값에 대한 처리
                    if (fieldError.getDefaultMessage().contains("필수 입력 값")) {
                        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
                    }
                    // 필드 값이 빈 값이 아닐 때만 @Pattern 오류 처리
                    else if (fieldError.getField().equals("email") && !fieldError.getRejectedValue().toString().isEmpty()) {
                        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
                    } else if (fieldError.getField().equals("password") && !fieldError.getRejectedValue().toString().isEmpty()) {
                        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
                    }
                    return null; // 그 외는 필터링
                })
                .filter(Objects::nonNull) // null 값 제거
                .collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Validation failed", errors));

    }
    // 일반적인 IllegalArgumentException - 400 에러
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("IllegalArgumentException: {}", ex.getMessage());
        List<String> errors = List.of(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Invalid request", errors));
    }

    // 인증 관련 문제 - 401 에러
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(IllegalStateException ex) {
        log.error("AuthenticationException: {}", ex.getMessage());
        List<String> errors = List.of(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Authentication failed", errors));
    }

    // 기타 예상치 못한 오류 - 500 에러
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        List<String> errors = List.of("An unexpected error occurred. Please contact support.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal server error", errors));
    }
}
