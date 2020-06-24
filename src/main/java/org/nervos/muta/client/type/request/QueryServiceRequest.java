package org.nervos.muta.client.type.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class QueryServiceRequest {
    public static String operation = "queryService";

    public static String query =
            "query queryService(\n" +
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

    private String serviceName;
    private String method;
    private String payload;
    private String height;
    private String caller;
    private String cyclePrice;
    private String cycleLimit;

}
