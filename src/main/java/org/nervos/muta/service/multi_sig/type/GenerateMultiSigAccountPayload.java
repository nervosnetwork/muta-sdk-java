package org.nervos.muta.service.multi_sig.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Vector;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenerateMultiSigAccountPayload {
    public String owner;
    public Vector<AddressWithWeight> addr_with_weight;
    public int threshold;
    public String memo;
}
