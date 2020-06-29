package org.nervos.muta.service.multi_sig.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    private String address;
    private int weight;

    @JsonProperty("is_multiple")
    private boolean is_multiple;
}
