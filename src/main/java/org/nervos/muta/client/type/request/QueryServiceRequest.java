package org.nervos.muta.client.type.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.nervos.muta.client.type.graphql_schema.GAddress;
import org.nervos.muta.client.type.graphql_schema.GUint64;

/**
 * A class to indicate a QueryService <b>query</b> in GraphQl. In addition, it contains the param of
 * request
 *
 * @author Lycrus Hamster
 */
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

    /** service name means which service you want send your query to */
    private String serviceName;
    /** method means which method/function in service you want to invoke */
    private String method;
    /** payload acts like a param, it will be passed together with method to service */
    private String payload;
    /** height is based on which block height this query runs */
    private GUint64 height;
    /** you can specify the caller of the query, you don't need the private key of the address */
    private GAddress caller;
    /** set a cycle price */
    private GUint64 cyclePrice;
    /** set the limitation of cycle */
    private GUint64 cycleLimit;
}
