package org.nervos.muta.client.type.graphql_schema;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nervos.muta.client.batch.BatchQueryResponse;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Receipt implements BatchQueryResponse {
    private String height;
    private String cyclesUsed;
    private List<Event> events;
    private String stateRoot;
    private String txHash;
    private ReceiptResponse response;
}
