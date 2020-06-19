package org.nervos.muta.client.type.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Vector;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Receipt {
    public String height;
    public String cyclesUsed;
    public Vector<Event> events;
    public String stateRoot;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Event{
        public String service;
        public String data;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReceiptResponse{
        public String serviceName;
        public String method;
        public ServiceResponse response;
    }

}
