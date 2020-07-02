package org.nervos.muta.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GraphQlError extends RuntimeException {

  public GraphQlError(String msg) {
    super(msg);
  }
}
