package org.nervos.muta.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReceiptResponseError extends ServiceResponseError {
  private String serviceName;
  private String method;

  public ReceiptResponseError(String message) {
    super(message);
  }

  public ReceiptResponseError(String serviceName, String method, String code, String errorMessage) {
    super(
        "ReceiptResponseError, serviceName: "
            + serviceName
            + ", method: "
            + method
            + ", returns error code: "
            + code
            + ", errorMessage: "
            + errorMessage);

    this.serviceName = serviceName;
    this.method = method;
    this.code = code;
    this.errorMessage = errorMessage;
  }
}
