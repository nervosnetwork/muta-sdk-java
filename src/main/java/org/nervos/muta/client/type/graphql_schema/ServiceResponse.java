package org.nervos.muta.client.type.graphql_schema;

import java.math.BigInteger;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceResponse {
    private GUint64 code;
    private String succeedData;
    private String errorMessage;

    public boolean isError() {
        return code.get().compareTo(BigInteger.ZERO) != 0;
    }
}
