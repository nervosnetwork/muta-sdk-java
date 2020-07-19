package org.nervos.muta.exception;

import java.io.IOException;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Indicate that an error occurs while running tx before hook
 *
 * @author Lycrus Hamster
 */
@EqualsAndHashCode(callSuper = true)
@Data
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
