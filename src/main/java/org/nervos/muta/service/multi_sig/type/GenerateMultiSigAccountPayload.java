package org.nervos.muta.service.multi_sig.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenerateMultiSigAccountPayload {
    private String owner;
    private List<AddressWithWeight> addr_with_weight;
    private int threshold;
    private String memo;
}
