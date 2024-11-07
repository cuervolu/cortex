package com.cortex.backend.core.common.exception;

import com.cortex.backend.core.common.BusinessErrorCodes;
import org.springframework.web.server.ResponseStatusException;

public class NoApprovedMentorshipRequestException extends ResponseStatusException {

  private static final BusinessErrorCodes ERROR_CODE = BusinessErrorCodes.NO_APPROVED_MENTORSHIP_REQUEST;

  public NoApprovedMentorshipRequestException() {
    super(ERROR_CODE.getHttpStatus(), ERROR_CODE.getDescription());
  }

  public int getBusinessErrorCode() {
    return ERROR_CODE.getCode();
  }
}