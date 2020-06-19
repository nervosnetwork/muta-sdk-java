package org.nervos.muta.client.type.request;

import lombok.AllArgsConstructor;
import lombok.Data;

public class QueryServiceRequest {
    public static String operation = "queryService";

    public static String query = "query queryService(\n" +
            "  $serviceName: String!\n" +
            "  $method: String!\n" +
            "  $payload: String!\n" +
            "  $height: Uint64\n" +
            "  $caller: Address!\n" +
            "  $cyclePrice: Uint64\n" +
            "  $cycleLimit: Uint64\n" +
            ") {\n" +
            "  queryService(\n" +
            "    height: $height\n" +
            "    serviceName: $serviceName\n" +
            "    method: $method\n" +
            "    payload: $payload\n" +
            "    caller: $caller\n" +
            "    cyclesPrice: $cyclePrice\n" +
            "    cyclesLimit: $cycleLimit\n" +
            "  ) {\n" +
            "    code\n" +
            "    errorMessage\n" +
            "    succeedData\n" +
            "  }\n" +
            "}";

    @Data
    @AllArgsConstructor
    public static class Param{
        public String serviceName;
        public String method;
        public String payload;
        public String height;
        public String caller;
        public String cyclePrice;
        public String cycleLimit;
    }

}
