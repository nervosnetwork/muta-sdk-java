package org.nervos.muta.service.multi_sig.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeMemoPayload {
    public VerifySignaturePayload witness;
    public String multi_sig_address;
    public String new_memo;
}
