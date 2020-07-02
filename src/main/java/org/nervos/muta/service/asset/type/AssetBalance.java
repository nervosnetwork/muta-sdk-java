package org.nervos.muta.service.asset.type;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nervos.muta.client.type.primitive.U64;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssetBalance {
  private U64 value;
  private Map<String, U64> allowance;
}
