package org.nervos.muta.client.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MutaRequest {

  private String operationName;
  private Object variables;
  private String query;
}
