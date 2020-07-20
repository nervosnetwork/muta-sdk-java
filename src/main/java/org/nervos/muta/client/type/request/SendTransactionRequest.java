package org.nervos.muta.client.type.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.nervos.muta.client.type.graphql_schema.InputRawTransaction;
import org.nervos.muta.client.type.graphql_schema.InputTransactionEncryption;

/**
 * A class to indicate a SendTransaction <b>mutation</b> in GraphQl. In addition, it contains the
 * param of request
 *
 * @author Lycrus Hamster
 */
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

    /** the raw transaction to send */
    @NonNull private InputRawTransaction inputRaw;
    /** the signature of the sent transaction. this signature could be a single sig, or Multi-Sig */
    @NonNull private InputTransactionEncryption inputEncryption;
}
