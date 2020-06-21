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

@Data
@AllArgsConstructor
@Builder
public class RawTransaction {

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
                        //RlpString.create(Hex.decode(Util.remove0x(this.sender)))
                )
        );
        return ret;
    }

    public RawTransaction decode(byte[] input) throws IOException {
        List<RlpType> rlpTypeList = RlpDecoder.decode(input).getValues();
        if (rlpTypeList.size() != 9) {
            throw new IOException("decode InputRawTransaction error");
        }
        return new RawTransaction(
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
