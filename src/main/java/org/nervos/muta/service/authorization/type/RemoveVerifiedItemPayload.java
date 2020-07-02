package org.nervos.muta.service.authorization.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemoveVerifiedItemPayload {
  private String service_name;
}
