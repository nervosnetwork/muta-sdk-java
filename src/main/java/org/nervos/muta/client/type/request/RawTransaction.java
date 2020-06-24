package org.nervos.muta.client.type.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.bouncycastle.util.encoders.Hex;
import org.nervos.muta.util.Util;
import org.web3j.rlp.RlpEncoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;

import java.math.BigInteger;

@Data
@AllArgsConstructor
@Builder
public class RawTransaction {

    @NonNull
    private String chainId;
    @NonNull
    private String cyclesLimit;
    @NonNull
    private String cyclesPrice;
    @NonNull
    private String method;
    @NonNull
    private String nonce;
    @NonNull
    private String payload;
    @NonNull
    private String serviceName;
    @NonNull
    private String timeout;
    @NonNull
    private String sender;

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
        return ret;
    }

//    public RawTransaction decode(byte[] input) throws IOException {
//        List<RlpType> rlpTypeList = RlpDecoder.decode(input).getValues();
//        if (rlpTypeList.size() != 9) {
//            throw new IOException("decode InputRawTransaction error");
//        }
//        return new RawTransaction(
//                ((RlpString) rlpTypeList.get(0)).asString(),
//                ((RlpString) rlpTypeList.get(1)).asString(),
//                ((RlpString) rlpTypeList.get(2)).asString(),
//                ((RlpString) rlpTypeList.get(4)).asString(),
//                ((RlpString) rlpTypeList.get(3)).asString(),
//                ((RlpString) rlpTypeList.get(6)).asString(),
//                ((RlpString) rlpTypeList.get(5)).asString(),
//                ((RlpString) rlpTypeList.get(7)).asString(),
//                ((RlpString) rlpTypeList.get(8)).asString()
//        );
//    }
}
