package org.nervos.muta.client.type.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

public class SendTransactionRequest {
    public static String operation = "sendTransaction";

    public static String query = "mutation sendTransaction(\n" +
            "  $inputRaw: InputRawTransaction!\n" +
            "  $inputEncryption: InputTransactionEncryption!\n" +
            ") {\n" +
            "  sendTransaction(inputRaw: $inputRaw, inputEncryption: $inputEncryption)\n" +
            "}";

    @Data
    @AllArgsConstructor
    public static class Param {
        @NonNull
        public RawTransaction inputRaw;
        @NonNull
        public TransactionEncryption inputEncryption;
    }


}
