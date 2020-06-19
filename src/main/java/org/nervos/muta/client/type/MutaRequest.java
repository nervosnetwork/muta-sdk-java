package org.nervos.muta.client.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MutaRequest {

    public String operationName;
    public Object variables;
    public String query;

}
