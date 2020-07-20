package org.nervos.muta.client.type.graphql_schema;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nervos.muta.client.batch.BatchQueryResponse;

/**
 * A block of Muta-Chain
 *
 * @author Lycrus Hamster
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Block implements BatchQueryResponse {

    /** block header, contains meta info of this block */
    private Header header;
    /** transactions included in block, sorted. */
    private List<String> orderedTxHashes;
    /** the hash of this block */
    private String hash;
}
