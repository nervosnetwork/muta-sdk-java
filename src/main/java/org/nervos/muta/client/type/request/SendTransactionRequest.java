package org.nervos.muta.client.type.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.bouncycastle.util.encoders.Hex;
import org.nervos.muta.util.Util;
import org.web3j.rlp.*;

import java.io.IOException;
import java.math.BigInteger;
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
    public static class Param {
        @NonNull
        public InputRawTransaction inputRaw;
        @NonNull
        public InputTransactionEncryption inputEncryption;
    }


    @Data
    @AllArgsConstructor
    public static class InputTransactionEncryption {
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
    public static class InputRawTransaction {

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

        public byte[] encode() {
            byte[] ret = RlpEncoder.encode(
                    new RlpList(
                            RlpString.create(Hex.decode(Util.remove0x(this.chainId))),

                            RlpString.create(new BigInteger(Util.remove0x(this.cyclesLimit),16)),
                            RlpString.create(new BigInteger(Util.remove0x(this.cyclesPrice),16)),
                            RlpString.create(Hex.decode(Util.remove0x(this.nonce))),

                            RlpString.create(Util.remove0x(this.method)),
                            RlpString.create(Util.remove0x(this.serviceName)),
                            RlpString.create(Util.remove0x(this.payload)),

                            RlpString.create(new BigInteger(Util.remove0x(this.timeout),16)),
                            new RlpList(
                                    RlpString.create(Hex.decode(Util.remove0x(this.sender)))
                            )
                    )
            );
            //System.out.println("rlp: "+Hex.toHexString(ret));
            return ret;
        }

        public InputRawTransaction decode(byte[] input) throws IOException {
            List<RlpType> rlpTypeList = RlpDecoder.decode(input).getValues();
            if (rlpTypeList.size() != 9) {
                throw new IOException("decode InputRawTransaction error");
            }
            return new InputRawTransaction(
                    ((RlpString) rlpTypeList.get(0)).asString(),
                    ((RlpString) rlpTypeList.get(1)).asString(),
                    ((RlpString) rlpTypeList.get(2)).asString(),
                    ((RlpString) rlpTypeList.get(4)).asString(),
                    ((RlpString) rlpTypeList.get(3)).asString(),
                    ((RlpString) rlpTypeList.get(6)).asString(),
                    ((RlpString) rlpTypeList.get(5)).asString(),
                    ((RlpString) rlpTypeList.get(7)).asString(),
                    ((RlpString) rlpTypeList.get(8)).asString()
            );
        }
    }
}
