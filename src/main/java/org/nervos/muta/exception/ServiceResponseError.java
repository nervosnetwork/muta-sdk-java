package org.nervos.muta.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigInteger;

@EqualsAndHashCode(callSuper = true)
@Data
public class ServiceResponseError extends RuntimeException {
    private String serviceName;
    private String method;
    private String code;
    private String errorMessage;

    public ServiceResponseError(String message) {
        super(message);
    }

    public ServiceResponseError(String serviceName, String method, String code, String errorMessage) {
        super(serviceName+":"+method+" returns error code: " +
                code +", "+errorMessage);

        this.serviceName = serviceName;
        this.method = method;
        this.code = code;
        this.errorMessage = errorMessage;

    }

}
