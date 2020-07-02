package org.nervos.muta.service.authorization.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddVerifiedItemPayload {
  private String service_name;
  private String method_name;
}
