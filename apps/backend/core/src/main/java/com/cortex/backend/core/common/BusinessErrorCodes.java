package com.cortex.backend.core.common;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BusinessErrorCodes {
  NO_CODE(0, NOT_IMPLEMENTED, "No code"),
  INCORRECT_CURRENT_PASSWORD(300, BAD_REQUEST, "Current password is incorrect"),
  NEW_PASSWORD_DOES_NOT_MATCH(301, BAD_REQUEST, "The new password does not match"),
  ACCOUNT_LOCKED(302, FORBIDDEN, "User account is locked"),
  ACCOUNT_DISABLED(303, FORBIDDEN, "User account is disabled"),
  BAD_CREDENTIALS(304, FORBIDDEN, "Login and / or Password is incorrect"),

  USER_ALREADY_EXISTS(305, BAD_REQUEST, "User already exists"),

  INVALID_TOKEN(306, BAD_REQUEST, "Invalid token"),

  EMAIL_SENDING_FAILED(307, INTERNAL_SERVER_ERROR, "Failed to send email"),
  ACCESS_DENIED(308, FORBIDDEN, "Access denied. You don't have permission to perform this action."),

  FILE_SIZE_EXCEEDED(309, BAD_REQUEST, "File size exceeds the maximum allowed limit"),
  INVALID_FILE_TYPE(310, BAD_REQUEST,
      "Invalid file type. Only JPEG, PNG, and WebP images are allowed"),
  INVALID_URI(311, BAD_REQUEST, "Invalid URI for avatar URL"),
  EXPIRED_TOKEN(312, UNAUTHORIZED, "Token has expired"),
  GITHUB_SYNC_FAILED(313, INTERNAL_SERVER_ERROR, "Failed to sync exercises"),
  EXERCISE_CREATE_FAILED(314, INTERNAL_SERVER_ERROR, "Failed to create exercise"),
  EXERCISE_UPDATE_FAILED(315, INTERNAL_SERVER_ERROR, "Failed to update exercise"),
  EXERCISE_NOT_FOUND(316, NOT_FOUND, "Exercise not found"),
  EXERCISE_READ_FAILED(317, INTERNAL_SERVER_ERROR, "Failed to read exercise"),
  UNSUPPORTED_LANGUAGE(318, BAD_REQUEST, "Unsupported language"),
  HASH_GENERATION_FAILED(319, INTERNAL_SERVER_ERROR, "Failed to generate hash"),
  INVALID_PREREQUISITE(320, BAD_REQUEST, "Invalid prerequisite"),
  INVALID_CONFIGURATION(321, BAD_REQUEST, "Invalid configuration"),
  RESULT_NOT_AVAILABLE(320, NOT_FOUND, "Execution result not available yet"),
  CONTENT_CHANGED(321, BAD_REQUEST, "Content has changed since submission"),
  CODE_EXECUTION_FAILED(322, INTERNAL_SERVER_ERROR, "Code execution failed"),
  CONTAINER_EXECUTION_FAILED(323, INTERNAL_SERVER_ERROR, "Container execution failed"),
  USER_NOT_PART_OF_MENTORSHIP(324, FORBIDDEN, "User is not part of this mentorship"),
  NO_APPROVED_MENTORSHIP_REQUEST(325, NOT_FOUND, "No approved mentorship request found for this mentee"),
  PAYMENT_FAILED(326, INTERNAL_SERVER_ERROR, "Payment failed"),
  RESOURCE_NOT_FOUND(327, NOT_FOUND, "Resource not found"),
  ;
  private final int code;
  private final String description;
  private final HttpStatus httpStatus;

  BusinessErrorCodes(int code, HttpStatus status, String description) {
    this.code = code;
    this.description = description;
    this.httpStatus = status;
  }
}
