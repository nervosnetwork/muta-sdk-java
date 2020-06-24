package org.nervos.muta.service.multi_sig.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetThresholdPayload {
    private VerifySignaturePayload witness;
    private String multi_sig_address;
    private int new_threshold;
}
