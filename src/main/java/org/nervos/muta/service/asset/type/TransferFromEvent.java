package org.nervos.muta.service.asset.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferFromEvent {
    public String asset_id;
    public String caller;
    public String sender;
    public String recipient;
    public long value;
}
