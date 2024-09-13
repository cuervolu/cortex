package com.cortex.backend.handler;


import static com.cortex.backend.handler.BusinessErrorCodes.ACCOUNT_DISABLED;
import static com.cortex.backend.handler.BusinessErrorCodes.ACCOUNT_LOCKED;
import static com.cortex.backend.handler.BusinessErrorCodes.BAD_CREDENTIALS;
import static com.cortex.backend.handler.BusinessErrorCodes.INCORRECT_CURRENT_PASSWORD;
import static com.cortex.backend.handler.BusinessErrorCodes.NEW_PASSWORD_DOES_NOT_MATCH;
import static com.cortex.backend.handler.BusinessErrorCodes.USER_ALREADY_EXISTS;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.cortex.backend.exception.IncorrectCurrentPasswordException;
import com.cortex.backend.exception.NewPasswordDoesNotMatchException;
import com.cortex.backend.exception.OperationNotPermittedException;
import com.resend.core.exception.ResendException;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(LockedException.class)
  public ResponseEntity<ExceptionResponse> handleException(LockedException exp) {
    return ResponseEntity.status(UNAUTHORIZED)
        .body(
            ExceptionResponse.builder()
                .businessErrorCode(ACCOUNT_LOCKED.getCode())
                .businessErrorDescription(ACCOUNT_LOCKED.getDescription())
                .error(exp.getMessage())
                .build());
  }

  @ExceptionHandler(DisabledException.class)
  public ResponseEntity<ExceptionResponse> handleException(DisabledException exp) {
    return ResponseEntity.status(UNAUTHORIZED)
        .body(
            ExceptionResponse.builder()
                .businessErrorCode(ACCOUNT_DISABLED.getCode())
                .businessErrorDescription(ACCOUNT_DISABLED.getDescription())
                .error(exp.getMessage())
                .build());
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ExceptionResponse> handleException(BadCredentialsException exp) {
    return ResponseEntity.status(UNAUTHORIZED)
        .body(
            ExceptionResponse.builder()
                .businessErrorCode(BAD_CREDENTIALS.getCode())
                .businessErrorDescription(BAD_CREDENTIALS.getDescription())
                .error(exp.getMessage())
                .build());
  }

  @ExceptionHandler(ResendException.class)
  public ResponseEntity<ExceptionResponse> handleException(ResendException exp) {
    return ResponseEntity.status(INTERNAL_SERVER_ERROR)
        .body(ExceptionResponse.builder().error(exp.getMessage()).build());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ExceptionResponse> handleException(MethodArgumentNotValidException exp) {
    Set<String> errors = new HashSet<>();
    exp.getBindingResult()
        .getAllErrors()
        .forEach(
            error -> {
              var errorMessage = error.getDefaultMessage();
              errors.add(errorMessage);
            });
    return ResponseEntity.status(BAD_REQUEST)
        .body(ExceptionResponse.builder().validationErrors(errors).build());
  }

  @ExceptionHandler(OperationNotPermittedException.class)
  public ResponseEntity<ExceptionResponse> handleException(OperationNotPermittedException exp) {
    return ResponseEntity.status(BAD_REQUEST)
        .body(ExceptionResponse.builder().error(exp.getMessage()).build());
  }


  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ExceptionResponse> handleException(DataIntegrityViolationException exp) {
    return ResponseEntity.status(CONFLICT)
        .body(
            ExceptionResponse.builder()
                .businessErrorCode(USER_ALREADY_EXISTS.getCode())
                .businessErrorDescription(USER_ALREADY_EXISTS.getDescription())
                .error(exp.getMessage())
                .build());
  }

  @ExceptionHandler(IncorrectCurrentPasswordException.class)
  public ResponseEntity<ExceptionResponse> handleException(IncorrectCurrentPasswordException exp) {
    return ResponseEntity.status(INCORRECT_CURRENT_PASSWORD.getHttpStatus())
        .body(
            ExceptionResponse.builder()
                .businessErrorCode(INCORRECT_CURRENT_PASSWORD.getCode())
                .businessErrorDescription(INCORRECT_CURRENT_PASSWORD.getDescription())
                .error(exp.getMessage())
                .build());
  }
  
  @ExceptionHandler(NewPasswordDoesNotMatchException.class)
  public ResponseEntity<ExceptionResponse> handleException(NewPasswordDoesNotMatchException exp) {
    return ResponseEntity.status(NEW_PASSWORD_DOES_NOT_MATCH.getHttpStatus())
        .body(
            ExceptionResponse.builder()
                .businessErrorCode(NEW_PASSWORD_DOES_NOT_MATCH.getCode())
                .businessErrorDescription(NEW_PASSWORD_DOES_NOT_MATCH.getDescription())
                .error(exp.getMessage())
                .build());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ExceptionResponse> handleException(Exception exp) {

    log.error("Internal error", exp);

    return ResponseEntity.status(INTERNAL_SERVER_ERROR)
        .body(
            ExceptionResponse.builder()
                .businessErrorDescription("Internal error, please contact support")
                .error(exp.getMessage())
                .build());
  }
}
