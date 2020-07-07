package org.nervos.muta.client.type.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.nervos.muta.client.type.graphql_schema.InputRawTransaction;
import org.nervos.muta.client.type.graphql_schema.InputTransactionEncryption;

@Data
@AllArgsConstructor
public class SendTransactionRequest {
    public static String operation = "sendTransaction";

    public static String query =
            "mutation sendTransaction(\n"
                    + "  $inputRaw: InputRawTransaction!\n"
                    + "  $inputEncryption: InputTransactionEncryption!\n"
                    + ") {\n"
                    + "  sendTransaction(inputRaw: $inputRaw, inputEncryption: $inputEncryption)\n"
                    + "}";

    @NonNull private InputRawTransaction inputRaw;
    @NonNull private InputTransactionEncryption inputEncryption;
}
