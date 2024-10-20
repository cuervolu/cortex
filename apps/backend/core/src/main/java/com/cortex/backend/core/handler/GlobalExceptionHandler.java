package com.cortex.backend.core.handler;


import static com.cortex.backend.core.common.BusinessErrorCodes.ACCESS_DENIED;
import static com.cortex.backend.core.common.BusinessErrorCodes.ACCOUNT_DISABLED;
import static com.cortex.backend.core.common.BusinessErrorCodes.ACCOUNT_LOCKED;
import static com.cortex.backend.core.common.BusinessErrorCodes.BAD_CREDENTIALS;
import static com.cortex.backend.core.common.BusinessErrorCodes.CODE_EXECUTION_FAILED;
import static com.cortex.backend.core.common.BusinessErrorCodes.CONTAINER_EXECUTION_FAILED;
import static com.cortex.backend.core.common.BusinessErrorCodes.CONTENT_CHANGED;
import static com.cortex.backend.core.common.BusinessErrorCodes.EMAIL_SENDING_FAILED;
import static com.cortex.backend.core.common.BusinessErrorCodes.EXERCISE_CREATE_FAILED;
import static com.cortex.backend.core.common.BusinessErrorCodes.EXERCISE_READ_FAILED;
import static com.cortex.backend.core.common.BusinessErrorCodes.EXPIRED_TOKEN;
import static com.cortex.backend.core.common.BusinessErrorCodes.FILE_SIZE_EXCEEDED;
import static com.cortex.backend.core.common.BusinessErrorCodes.GITHUB_SYNC_FAILED;
import static com.cortex.backend.core.common.BusinessErrorCodes.HASH_GENERATION_FAILED;
import static com.cortex.backend.core.common.BusinessErrorCodes.INCORRECT_CURRENT_PASSWORD;
import static com.cortex.backend.core.common.BusinessErrorCodes.INVALID_FILE_TYPE;
import static com.cortex.backend.core.common.BusinessErrorCodes.INVALID_TOKEN;
import static com.cortex.backend.core.common.BusinessErrorCodes.INVALID_URI;
import static com.cortex.backend.core.common.BusinessErrorCodes.NEW_PASSWORD_DOES_NOT_MATCH;
import static com.cortex.backend.core.common.BusinessErrorCodes.NO_APPROVED_MENTORSHIP_REQUEST;
import static com.cortex.backend.core.common.BusinessErrorCodes.RESULT_NOT_AVAILABLE;
import static com.cortex.backend.core.common.BusinessErrorCodes.UNSUPPORTED_LANGUAGE;
import static com.cortex.backend.core.common.BusinessErrorCodes.USER_ALREADY_EXISTS;
import static com.cortex.backend.core.common.BusinessErrorCodes.USER_NOT_PART_OF_MENTORSHIP;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.cortex.backend.core.common.BusinessErrorCodes;
import com.cortex.backend.core.common.exception.CodeExecutionException;
import com.cortex.backend.core.common.exception.ContainerExecutionException;
import com.cortex.backend.core.common.exception.ContentChangedException;
import com.cortex.backend.core.common.exception.EmailSendingException;
import com.cortex.backend.core.common.exception.ExerciseCreationException;
import com.cortex.backend.core.common.exception.ExerciseReadException;
import com.cortex.backend.core.common.exception.ExpiredTokenException;
import com.cortex.backend.core.common.exception.FileSizeExceededException;
import com.cortex.backend.core.common.exception.GitSyncException;
import com.cortex.backend.core.common.exception.HashGenerationException;
import com.cortex.backend.core.common.exception.IncorrectCurrentPasswordException;
import com.cortex.backend.core.common.exception.InvalidFileTypeException;
import com.cortex.backend.core.common.exception.InvalidTokenException;
import com.cortex.backend.core.common.exception.InvalidURIException;
import com.cortex.backend.core.common.exception.NewPasswordDoesNotMatchException;
import com.cortex.backend.core.common.exception.NoApprovedMentorshipRequestException;
import com.cortex.backend.core.common.exception.OperationNotPermittedException;
import com.cortex.backend.core.common.exception.ResultNotAvailableException;
import com.cortex.backend.core.common.exception.UnsupportedLanguageException;
import com.cortex.backend.core.common.exception.UserNotPartOfMentorshipException;
import com.resend.core.exception.ResendException;
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
                .code(ACCOUNT_LOCKED.getCode())
                .description(ACCOUNT_LOCKED.getDescription())
                .error(exp.getMessage())
                .build());
  }

  @ExceptionHandler(DisabledException.class)
  public ResponseEntity<ExceptionResponse> handleException(DisabledException exp) {
    return ResponseEntity.status(UNAUTHORIZED)
        .body(
            ExceptionResponse.builder()
                .code(ACCOUNT_DISABLED.getCode())
                .description(ACCOUNT_DISABLED.getDescription())
                .error(exp.getMessage())
                .build());
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ExceptionResponse> handleException(BadCredentialsException exp) {
    return ResponseEntity.status(UNAUTHORIZED)
        .body(
            ExceptionResponse.builder()
                .code(BAD_CREDENTIALS.getCode())
                .description(BAD_CREDENTIALS.getDescription())
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
                .code(USER_ALREADY_EXISTS.getCode())
                .description(USER_ALREADY_EXISTS.getDescription())
                .error(exp.getMessage())
                .build());
  }

  @ExceptionHandler(IncorrectCurrentPasswordException.class)
  public ResponseEntity<ExceptionResponse> handleException(IncorrectCurrentPasswordException exp) {
    return ResponseEntity.status(INCORRECT_CURRENT_PASSWORD.getHttpStatus())
        .body(
            ExceptionResponse.builder()
                .code(INCORRECT_CURRENT_PASSWORD.getCode())
                .description(INCORRECT_CURRENT_PASSWORD.getDescription())
                .error(exp.getMessage())
                .build());
  }

  @ExceptionHandler(NewPasswordDoesNotMatchException.class)
  public ResponseEntity<ExceptionResponse> handleException(NewPasswordDoesNotMatchException exp) {
    return ResponseEntity.status(NEW_PASSWORD_DOES_NOT_MATCH.getHttpStatus())
        .body(
            ExceptionResponse.builder()
                .code(NEW_PASSWORD_DOES_NOT_MATCH.getCode())
                .description(NEW_PASSWORD_DOES_NOT_MATCH.getDescription())
                .error(exp.getMessage())
                .build());
  }

  @ExceptionHandler(InvalidTokenException.class)
  public ResponseEntity<ExceptionResponse> handleException(InvalidTokenException exp) {
    return ResponseEntity.status(INVALID_TOKEN.getHttpStatus())
        .body(
            ExceptionResponse.builder()
                .code(INVALID_TOKEN.getCode())
                .description(INVALID_TOKEN.getDescription())
                .error(exp.getMessage())
                .build());
  }

  @ExceptionHandler(EmailSendingException.class)
  public ResponseEntity<ExceptionResponse> handleEmailSendingException(EmailSendingException ex) {
    ExceptionResponse response = ExceptionResponse.builder()
        .code(EMAIL_SENDING_FAILED.getCode())
        .description(EMAIL_SENDING_FAILED.getDescription())
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
            .code(ACCESS_DENIED.getCode())
            .description(ACCESS_DENIED.getDescription())
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
            .code(BAD_CREDENTIALS.getCode())
            .description(BAD_CREDENTIALS.getDescription())
            .error(ex.getMessage())
            .build());
  }


  @ExceptionHandler(FileSizeExceededException.class)
  public ResponseEntity<ExceptionResponse> handleFileSizeExceededException(
      FileSizeExceededException exp) {
    return ResponseEntity.status(FILE_SIZE_EXCEEDED.getHttpStatus())
        .body(
            ExceptionResponse.builder()
                .code(FILE_SIZE_EXCEEDED.getCode())
                .description(FILE_SIZE_EXCEEDED.getDescription())
                .error(exp.getMessage())
                .build());
  }

  @ExceptionHandler(InvalidFileTypeException.class)
  public ResponseEntity<ExceptionResponse> handleInvalidFileTypeException(
      InvalidFileTypeException exp) {
    return ResponseEntity.status(INVALID_FILE_TYPE.getHttpStatus())
        .body(
            ExceptionResponse.builder()
                .code(INVALID_FILE_TYPE.getCode())
                .description(INVALID_FILE_TYPE.getDescription())
                .error(exp.getMessage())
                .build());
  }

  @ExceptionHandler(InvalidURIException.class)
  public ResponseEntity<ExceptionResponse> handleInvalidURLException(
      InvalidURIException exp) {
    return ResponseEntity.status(INVALID_URI.getHttpStatus())
        .body(
            ExceptionResponse.builder()
                .code(INVALID_URI.getCode())
                .description(INVALID_URI.getDescription())
                .error(exp.getMessage())
                .build());
  }


  @ExceptionHandler(ExpiredTokenException.class)
  public ResponseEntity<ExceptionResponse> handleExpiredJwtToken(
      ExpiredTokenException exp) {
    return ResponseEntity.status(EXPIRED_TOKEN.getHttpStatus())
        .body(
            ExceptionResponse.builder()
                .code(EXPIRED_TOKEN.getCode())
                .description(EXPIRED_TOKEN.getDescription())
                .error(exp.getMessage())
                .build());
  }

  @ExceptionHandler(GitSyncException.class)
  public ResponseEntity<ExceptionResponse> handleGitSyncException(GitSyncException exp) {
    return ResponseEntity.status(GITHUB_SYNC_FAILED.getHttpStatus())
        .body(
            ExceptionResponse.builder()
                .code(GITHUB_SYNC_FAILED.getCode())
                .description(GITHUB_SYNC_FAILED.getDescription())
                .error(exp.getMessage())
                .build());
  }

  @ExceptionHandler(ExerciseCreationException.class)
  public ResponseEntity<ExceptionResponse> handleExcerciseCreationException(
      ExerciseCreationException exp) {
    return ResponseEntity.status(EXERCISE_CREATE_FAILED.getHttpStatus())
        .body(
            ExceptionResponse.builder()
                .code(EXERCISE_CREATE_FAILED.getCode())
                .description(EXERCISE_CREATE_FAILED.getDescription())
                .error(exp.getMessage())
                .build());
  }


  @ExceptionHandler(ExerciseReadException.class)
  public ResponseEntity<ExceptionResponse> handleExcerciseReadException(
      ExerciseReadException exp) {
    return ResponseEntity.status(EXERCISE_READ_FAILED.getHttpStatus())
        .body(
            ExceptionResponse.builder()
                .code(EXERCISE_READ_FAILED.getCode())
                .description(EXERCISE_READ_FAILED.getDescription())
                .error(exp.getMessage())
                .build());
  }

  @ExceptionHandler(UnsupportedLanguageException.class)
  public ResponseEntity<ExceptionResponse> handleUnsupportedLanguageException(
      UnsupportedLanguageException exp) {
    return ResponseEntity.status(UNSUPPORTED_LANGUAGE.getHttpStatus())
        .body(
            ExceptionResponse.builder()
                .code(UNSUPPORTED_LANGUAGE.getCode())
                .description(UNSUPPORTED_LANGUAGE.getDescription())
                .error(exp.getMessage())
                .build());
  }

  @ExceptionHandler(HashGenerationException.class)
  public ResponseEntity<ExceptionResponse> handleHashGenerationException(
      HashGenerationException exp) {
    return ResponseEntity.status(HASH_GENERATION_FAILED.getHttpStatus())
        .body(
            ExceptionResponse.builder()
                .code(HASH_GENERATION_FAILED.getCode())
                .description(HASH_GENERATION_FAILED.getDescription())
                .error(exp.getMessage())
                .build());
  }

  @ExceptionHandler(ResultNotAvailableException.class)
  public ResponseEntity<ExceptionResponse> handleResultNotAvailableException(
      ResultNotAvailableException exp) {
    return ResponseEntity.status(RESULT_NOT_AVAILABLE.getHttpStatus())
        .body(
            ExceptionResponse.builder()
                .code(RESULT_NOT_AVAILABLE.getCode())
                .description(RESULT_NOT_AVAILABLE.getDescription())
                .error(exp.getMessage())
                .build());
  }

  @ExceptionHandler(ContentChangedException.class)
  public ResponseEntity<ExceptionResponse> handleContentChangedException(
      ContentChangedException exp) {
    return ResponseEntity.status(CONTENT_CHANGED.getHttpStatus())
        .body(
            ExceptionResponse.builder()
                .code(CONTENT_CHANGED.getCode())
                .description(CONTENT_CHANGED.getDescription())
                .error(exp.getMessage())
                .build());
  }

  @ExceptionHandler(CodeExecutionException.class)
  public ResponseEntity<ExceptionResponse> handleCodeExecutionException(
      CodeExecutionException exp) {
    return ResponseEntity.status(CODE_EXECUTION_FAILED.getHttpStatus())
        .body(
            ExceptionResponse.builder()
                .code(CODE_EXECUTION_FAILED.getCode())
                .description(CODE_EXECUTION_FAILED.getDescription())
                .error(exp.getMessage())
                .build());
  }

  @ExceptionHandler(ContainerExecutionException.class)
  public ResponseEntity<ExceptionResponse> handleContainerExecutionException(
      ContainerExecutionException exp) {
    return ResponseEntity.status(CONTAINER_EXECUTION_FAILED.getHttpStatus())
        .body(
            ExceptionResponse.builder()
                .code(CONTAINER_EXECUTION_FAILED.getCode())
                .description(CONTAINER_EXECUTION_FAILED.getDescription())
                .error(exp.getMessage())
                .build());
  }

  @ExceptionHandler(UserNotPartOfMentorshipException.class)
  public ResponseEntity<ExceptionResponse> handleUserNotPartOfMentorshipException(
      UserNotPartOfMentorshipException exp) {
    return ResponseEntity.status(USER_NOT_PART_OF_MENTORSHIP.getHttpStatus())
        .body(
            ExceptionResponse.builder()
                .code(USER_NOT_PART_OF_MENTORSHIP.getCode())
                .description(USER_NOT_PART_OF_MENTORSHIP.getDescription())
                .error(exp.getReason())
                .build());
  }

  @ExceptionHandler(NoApprovedMentorshipRequestException.class)
  public ResponseEntity<ExceptionResponse> handleUserNotPartOfMentorshipException(
      NoApprovedMentorshipRequestException exp) {
    return ResponseEntity.status(NO_APPROVED_MENTORSHIP_REQUEST.getHttpStatus())
        .body(
            ExceptionResponse.builder()
                .code(NO_APPROVED_MENTORSHIP_REQUEST.getCode())
                .description(NO_APPROVED_MENTORSHIP_REQUEST.getDescription())
                .error(exp.getReason())
                .build());
  }


  @ExceptionHandler(Exception.class)
  public ResponseEntity<ExceptionResponse> handleException(Exception exp) {

    log.error("Internal error", exp);

    return ResponseEntity.status(INTERNAL_SERVER_ERROR)
        .body(
            ExceptionResponse.builder()
                .description("Internal error, please contact support")
                .error(exp.getMessage())
                .build());
  }
}
