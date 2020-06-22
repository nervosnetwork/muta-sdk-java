package org.nervos.muta.service.multi_sig.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Vector;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifySignaturePayload {
    public Vector<String> pubkeys;
    public Vector<String> signatures;
    public String sender;
}
