package org.nervos.muta.client.type.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.web3j.rlp.*;

import java.io.IOException;
import java.util.List;

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
    public static class Param{
        @NonNull
        public InputRawTransaction inputRaw;
        @NonNull
        public InputTransactionEncryption inputEncryption;
    }


    @Data
    @AllArgsConstructor
    public static class InputTransactionEncryption{
        @NonNull
        public String pubkey;
        @NonNull
        public String signature;
        @NonNull
        public String txHash;
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class InputRawTransaction{

        @NonNull
        public String chainId;
        @NonNull
        public String cyclesLimit;
        @NonNull
        public String cyclesPrice;
        @NonNull
        public String method;
        @NonNull
        public String nonce;
        @NonNull
        public String payload;
        @NonNull
        public String serviceName;
        @NonNull
        public String timeout;
        @NonNull
        public String sender;

        public byte[] encode(){
            return RlpEncoder.encode(
                    new RlpList(
                            RlpString.create(this.chainId),//0
                            RlpString.create(this.cyclesLimit),//1
                            RlpString.create(this.cyclesPrice),//2
                            RlpString.create(this.nonce),//3
                            RlpString.create(this.method),//4
                            RlpString.create(this.serviceName),//5
                            RlpString.create(this.payload),//6
                            RlpString.create(this.timeout),//7
                            RlpString.create(this.sender)//8
                    )
            );
        }

        public InputRawTransaction decode(byte[] input) throws IOException {
            List<RlpType> rlpTypeList = RlpDecoder.decode(input).getValues();
            if(rlpTypeList.size()!=9){
                throw new IOException("decode InputRawTransaction error");
            }
            return new InputRawTransaction(
                    ((RlpString)rlpTypeList.get(0)).asString(),
                    ((RlpString)rlpTypeList.get(1)).asString(),
                    ((RlpString)rlpTypeList.get(2)).asString(),
                    ((RlpString)rlpTypeList.get(4)).asString(),
                    ((RlpString)rlpTypeList.get(3)).asString(),
                    ((RlpString)rlpTypeList.get(6)).asString(),
                    ((RlpString)rlpTypeList.get(5)).asString(),
                    ((RlpString)rlpTypeList.get(7)).asString(),
                    ((RlpString)rlpTypeList.get(8)).asString()
            );
        }
    }
}
