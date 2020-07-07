package org.nervos.muta.client.type.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.nervos.muta.client.type.graphql_schema.GAddress;
import org.nervos.muta.client.type.graphql_schema.GUint64;

@AllArgsConstructor
@Data
public class QueryServiceRequest {
    public static String operation = "queryService";

    public static String query =
            "query queryService(\n"
                    + "  $serviceName: String!\n"
                    + "  $method: String!\n"
                    + "  $payload: String!\n"
                    + "  $height: Uint64\n"
                    + "  $caller: Address!\n"
                    + "  $cyclePrice: Uint64\n"
                    + "  $cycleLimit: Uint64\n"
                    + ") {\n"
                    + "  queryService(\n"
                    + "    height: $height\n"
                    + "    serviceName: $serviceName\n"
                    + "    method: $method\n"
                    + "    payload: $payload\n"
                    + "    caller: $caller\n"
                    + "    cyclesPrice: $cyclePrice\n"
                    + "    cyclesLimit: $cycleLimit\n"
                    + "  ) {\n"
                    + "    code\n"
                    + "    errorMessage\n"
                    + "    succeedData\n"
                    + "  }\n"
                    + "}";

    private String serviceName;
    private String method;
    private String payload;
    private GUint64 height;
    private GAddress caller;
    private GUint64 cyclePrice;
    private GUint64 cycleLimit;
}
