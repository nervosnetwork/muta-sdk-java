package org.nervos.muta.client.type.graphql_schema;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nervos.muta.client.batch.BatchQueryResponse;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Block implements BatchQueryResponse {

    private Header header;
    private List<String> orderedTxHashes;
    private String hash;
}
