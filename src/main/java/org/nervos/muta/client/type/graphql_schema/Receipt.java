package org.nervos.muta.client.type.graphql_schema;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nervos.muta.client.batch.BatchQueryResponse;

/** Receipt of a executed transaction. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Receipt implements BatchQueryResponse {
    /** which height of block this receipt is minded on */
    private GUint64 height;
    /** how many cycles are used in the transaction */
    private GUint64 cyclesUsed;
    /** events emitted during the execution */
    private List<Event> events;
    /** the state root after this transaction runs */
    private GHash stateRoot;
    /** the transaction hash of the executed transation */
    private GHash txHash;
    /** response of this transaction */
    private ReceiptResponse response;
}
