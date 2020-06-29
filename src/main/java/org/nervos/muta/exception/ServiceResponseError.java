package org.nervos.muta.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.IOException;

@EqualsAndHashCode(callSuper = true)
@Data
public class ServiceResponseError extends IOException {
    protected String code;
    protected String errorMessage;


    public ServiceResponseError(String message) {
        super(message);
    }

    public ServiceResponseError(String code, String errorMessage)
    {
        super("ServiceResponseError, code: "+code+", errorMessage: "+errorMessage);
        this.code =code;
        this.errorMessage=errorMessage;
    }

}
