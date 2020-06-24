package org.nervos.muta.test.client;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.nervos.muta.client.batch.BatchClient;
import org.nervos.muta.client.batch.BatchQuery;
import org.nervos.muta.client.type.request.GetBlockRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BatchClientTest {

    public static BatchClient batchClient;

    public BatchClientTest() {
        batchClient = BatchClient.defaultClient();
    }

    @Test
    @Order(1)
    public void batchQueryTest() throws IOException {
        GetBlockRequest getBlockRequest = new GetBlockRequest(null);
        GetBlockRequest getBlockRequest2 = new GetBlockRequest("0x00");
        List<BatchQuery> batchQueries = new ArrayList<>();
        batchQueries.add(getBlockRequest);
        batchQueries.add(getBlockRequest2);
        batchClient.query(batchQueries);
    }
}
