package org.nervos.muta.exception;

import java.io.IOException;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Simply indicate that a ServiceResponse's error, which means the 'code' field of a ServiceResponse
 * is not zero
 *
 * @author Lycrus Hamster
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ServiceResponseError extends IOException {
    protected String code;
    protected String errorMessage;

    public ServiceResponseError(String message) {
        super(message);
    }

    public ServiceResponseError(String code, String errorMessage) {
        super("ServiceResponseError, code: " + code + ", errorMessage: " + errorMessage);
        this.code = code;
        this.errorMessage = errorMessage;
    }
}
