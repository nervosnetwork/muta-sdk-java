package org.nervos.muta.client.type.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nervos.muta.client.batch.BatchQueryResponse;

import java.util.List;

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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Event{
        private String service;
        private String data;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReceiptResponse{
        private String serviceName;
        private String method;
        private ServiceResponse response;
    }

}
