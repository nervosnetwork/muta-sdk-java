package org.nervos.muta.service.asset.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferFromPayload {
    public String asset_id;
    public String sender;
    public String recipient;
    public long value;
}
