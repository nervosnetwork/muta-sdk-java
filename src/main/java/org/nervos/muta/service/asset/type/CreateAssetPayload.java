package org.nervos.muta.service.asset.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAssetPayload {
    private String name;
    private String symbol;
    private long supply;
}
