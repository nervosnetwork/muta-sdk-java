package org.nervos.muta.service.asset.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferFromEvent {
    private String asset_id;
    private String caller;
    private String sender;
    private String recipient;
    private long value;
}
