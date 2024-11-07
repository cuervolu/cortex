package com.cortex.backend.core.common.exception;

import com.cortex.backend.core.common.BusinessErrorCodes;
import org.springframework.web.server.ResponseStatusException;

public class UserNotPartOfMentorshipException extends ResponseStatusException {

  private static final BusinessErrorCodes ERROR_CODE = BusinessErrorCodes.USER_NOT_PART_OF_MENTORSHIP;

  public UserNotPartOfMentorshipException() {
    super(ERROR_CODE.getHttpStatus(), ERROR_CODE.getDescription());
  }

  public int getBusinessErrorCode() {
    return ERROR_CODE.getCode();
  }
}