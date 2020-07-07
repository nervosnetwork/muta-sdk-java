package org.nervos.muta.service.multi_sig.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nervos.muta.client.type.primitive.Address;
import org.nervos.muta.client.type.primitive.U8;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressWithWeight {
    private Address address;
    private U8 weight;
}
