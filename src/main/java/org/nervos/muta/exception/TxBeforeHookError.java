package org.nervos.muta.exception;

import java.io.IOException;

public class TxBeforeHookError extends IOException {
    protected String code;
    protected String errorMessage;

    public TxBeforeHookError(String message) {
        super(message);
    }

    public TxBeforeHookError(String code, String errorMessage) {
        super("TxBeforeHookError, code: " + code + ", errorMessage: " + errorMessage);
        this.code = code;
        this.errorMessage = errorMessage;
    }
}
