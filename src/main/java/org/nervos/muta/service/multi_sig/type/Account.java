package org.nervos.muta.service.multi_sig.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nervos.muta.client.type.primitive.Address;
import org.nervos.muta.client.type.primitive.U8;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    private Address address;
    private U8 weight;

    @JsonProperty("is_multiple")
    private boolean is_multiple;
}
