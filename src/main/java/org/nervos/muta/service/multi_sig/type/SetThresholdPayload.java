package org.nervos.muta.service.multi_sig.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetThresholdPayload {
    public VerifySignaturePayload witness;
    public String multi_sig_address;
    public int new_threshold;
}
