package org.nervos.muta.client.type.primitive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {
    private String method;
    private String service_name;
    private String payload; // JSON string
}
