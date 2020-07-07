package org.nervos.muta.service.asset.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.nervos.muta.client.type.primitive.Hash;

@Data
@AllArgsConstructor
public class GetAssetPayload {
    private Hash id;
}
