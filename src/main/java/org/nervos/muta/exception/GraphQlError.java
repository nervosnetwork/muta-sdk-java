package org.nervos.muta.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The exception indicate GraphQl error while client talks to server in GraphQl grammar
 *
 * @author Lycrus Hamster
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GraphQlError extends RuntimeException {

    public GraphQlError(String msg) {
        super(msg);
    }
}
