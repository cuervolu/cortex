package com.cortex.backend.payments.internal;

import com.cortex.backend.core.common.exception.FailedToActivateLicenseException;
import com.cortex.backend.core.common.exception.FailedToDeactivateLicenseException;
import com.cortex.backend.core.common.exception.FailedToValidateLicenseException;
import com.cortex.backend.lemonsqueezy.licensekeys.LicenseKeyService;
import com.cortex.backend.lemonsqueezy.licensekeys.dto.LicenseActivationResponse;
import com.cortex.backend.lemonsqueezy.licensekeys.dto.LicenseDeactivationResponse;
import com.cortex.backend.lemonsqueezy.licensekeys.dto.LicenseValidationResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentsService {

  private final LicenseKeyService licenseKeyService;

  public LicenseActivationResponse activateLicense(String licenseKey, String instanceName) {
    try {
      log.info("Activating license key: {}", licenseKey);
      return licenseKeyService.activateLicenseKey(licenseKey, instanceName);
    } catch (IOException e) {
      log.error("Error activating license key: {}", e.getMessage());
      throw new FailedToActivateLicenseException(e.getMessage());
    }
  }

  public LicenseDeactivationResponse deactivateLicense(String licenseKey, String instanceId) {
    try {
      log.info("Deactivating license key: {}", licenseKey);
      return licenseKeyService.deactivateLicenseKey(licenseKey, instanceId);
    } catch (IOException e) {
      log.error("Error deactivating license key: {}", e.getMessage());
      throw new FailedToDeactivateLicenseException(e.getMessage());
    }
  }


  public LicenseValidationResponse validateLicense(String licenseKey, String instanceId) {
    try {
      log.info("Validating license key: {}", licenseKey);
      return licenseKeyService.validateLicenseKey(licenseKey, instanceId);
    } catch (IOException e) {
      log.error("Error validating license key: {}", e.getMessage());
      throw new FailedToValidateLicenseException(e.getMessage());
    }
  }
}
