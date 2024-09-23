package com.cortex.backend.handler;


import static com.cortex.backend.common.BusinessErrorCodes.ACCESS_DENIED;
import static com.cortex.backend.common.BusinessErrorCodes.ACCOUNT_DISABLED;
import static com.cortex.backend.common.BusinessErrorCodes.ACCOUNT_LOCKED;
import static com.cortex.backend.common.BusinessErrorCodes.BAD_CREDENTIALS;
import static com.cortex.backend.common.BusinessErrorCodes.EMAIL_SENDING_FAILED;
import static com.cortex.backend.common.BusinessErrorCodes.EXPIRED_TOKEN;
import static com.cortex.backend.common.BusinessErrorCodes.FILE_SIZE_EXCEEDED;
import static com.cortex.backend.common.BusinessErrorCodes.INCORRECT_CURRENT_PASSWORD;
import static com.cortex.backend.common.BusinessErrorCodes.INVALID_FILE_TYPE;
import static com.cortex.backend.common.BusinessErrorCodes.INVALID_TOKEN;
import static com.cortex.backend.common.BusinessErrorCodes.INVALID_URI;
import static com.cortex.backend.common.BusinessErrorCodes.NEW_PASSWORD_DOES_NOT_MATCH;
import static com.cortex.backend.common.BusinessErrorCodes.USER_ALREADY_EXISTS;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.cortex.backend.common.exception.EmailSendingException;
import com.cortex.backend.common.exception.ExpiredTokenException;
import com.cortex.backend.common.exception.FileSizeExceededException;
import com.cortex.backend.common.exception.IncorrectCurrentPasswordException;
import com.cortex.backend.common.exception.InvalidFileTypeException;
import com.cortex.backend.common.exception.InvalidTokenException;
import com.cortex.backend.common.exception.InvalidURIException;
import com.cortex.backend.common.exception.NewPasswordDoesNotMatchException;
import com.cortex.backend.common.exception.OperationNotPermittedException;
import com.resend.core.exception.ResendException;
import io.jsonwebtoken.ExpiredJwtException;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
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

  @ExceptionHandler(InvalidTokenException.class)
  public ResponseEntity<ExceptionResponse> handleException(InvalidTokenException exp) {
    return ResponseEntity.status(INVALID_TOKEN.getHttpStatus())
        .body(
            ExceptionResponse.builder()
                .businessErrorCode(INVALID_TOKEN.getCode())
                .businessErrorDescription(INVALID_TOKEN.getDescription())
                .error(exp.getMessage())
                .build());
  }

  @ExceptionHandler(EmailSendingException.class)
  public ResponseEntity<ExceptionResponse> handleEmailSendingException(EmailSendingException ex) {
    ExceptionResponse response = ExceptionResponse.builder()
        .businessErrorCode(EMAIL_SENDING_FAILED.getCode())
        .businessErrorDescription(EMAIL_SENDING_FAILED.getDescription())
        .error(ex.getMessage())
        .build();
    return ResponseEntity.status(EMAIL_SENDING_FAILED.getHttpStatus()).body(response);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ExceptionResponse> handleAccessDeniedException(AccessDeniedException ex) {
    log.error("Access denied error: ", ex);
    return ResponseEntity
        .status(ACCESS_DENIED.getHttpStatus())
        .body(ExceptionResponse.builder()
            .businessErrorCode(ACCESS_DENIED.getCode())
            .businessErrorDescription(ACCESS_DENIED.getDescription())
            .error(ex.getMessage())
            .build());
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ExceptionResponse> handleAuthenticationException(
      AuthenticationException ex) {
    log.error("Authentication error: ", ex);
    return ResponseEntity
        .status(FORBIDDEN)
        .body(ExceptionResponse.builder()
            .businessErrorCode(BAD_CREDENTIALS.getCode())
            .businessErrorDescription(BAD_CREDENTIALS.getDescription())
            .error(ex.getMessage())
            .build());
  }


  @ExceptionHandler(FileSizeExceededException.class)
  public ResponseEntity<ExceptionResponse> handleFileSizeExceededException(
      FileSizeExceededException exp) {
    return ResponseEntity.status(FILE_SIZE_EXCEEDED.getHttpStatus())
        .body(
            ExceptionResponse.builder()
                .businessErrorCode(FILE_SIZE_EXCEEDED.getCode())
                .businessErrorDescription(FILE_SIZE_EXCEEDED.getDescription())
                .error(exp.getMessage())
                .build());
  }

  @ExceptionHandler(InvalidFileTypeException.class)
  public ResponseEntity<ExceptionResponse> handleInvalidFileTypeException(
      InvalidFileTypeException exp) {
    return ResponseEntity.status(INVALID_FILE_TYPE.getHttpStatus())
        .body(
            ExceptionResponse.builder()
                .businessErrorCode(INVALID_FILE_TYPE.getCode())
                .businessErrorDescription(INVALID_FILE_TYPE.getDescription())
                .error(exp.getMessage())
                .build());
  }

  @ExceptionHandler(InvalidURIException.class)
  public ResponseEntity<ExceptionResponse> handleInvalidURLException(
      InvalidURIException exp) {
    return ResponseEntity.status(INVALID_URI.getHttpStatus())
        .body(
            ExceptionResponse.builder()
                .businessErrorCode(INVALID_URI.getCode())
                .businessErrorDescription(INVALID_URI.getDescription())
                .error(exp.getMessage())
                .build());
  }


  @ExceptionHandler(ExpiredTokenException.class)
  public ResponseEntity<ExceptionResponse> handleExpiredJwtToken(
      ExpiredTokenException exp) {
    return ResponseEntity.status(EXPIRED_TOKEN.getHttpStatus())
        .body(
            ExceptionResponse.builder()
                .businessErrorCode(EXPIRED_TOKEN.getCode())
                .businessErrorDescription(EXPIRED_TOKEN.getDescription())
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
