package org.nervos.muta.client.type.graphql_schema;

import java.math.BigInteger;
import java.util.List;
import lombok.*;
import org.web3j.rlp.*;

/**
 * Imply a transaction with needed params
 *
 * @author Lycrus Hamster
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class InputRawTransaction {

    /** which chainId this transaction belongs to */
    @NonNull private GHash chainId;
    /** limitation of cycles */
    @NonNull private GUint64 cyclesLimit;
    /** price of cycles */
    @NonNull private GUint64 cyclesPrice;
    /** a random nonce to make transaction unique in timeout */
    @NonNull private GHash nonce;
    /** method name of the service */
    @NonNull private String method;
    /** the service of the query */
    @NonNull private String serviceName;
    /** the payload of the method, it acts like param of function */
    @NonNull private String payload;
    /** this transaction is valid before timeout */
    @NonNull private GUint64 timeout;
    /**
     * who sends this transaction, this send should be justified by signature, no matter single
     * sender or Multi-Sig
     */
    @NonNull private GAddress sender;

    public byte[] encode() {
        return RlpEncoder.encode(
                new RlpList(
                        RlpString.create(this.chainId.get()),
                        RlpString.create(this.cyclesLimit.get()),
                        RlpString.create(this.cyclesPrice.get()),
                        RlpString.create(this.nonce.get()),
                        RlpString.create(this.method),
                        RlpString.create(this.serviceName),
                        RlpString.create(this.payload),
                        RlpString.create(this.timeout.get()),
                        new RlpList(RlpString.create(this.sender.get()))));
    }

    public static InputRawTransaction decode(byte[] input) {
        RlpList list = RlpDecoder.decode(input);
        List<RlpType> items = ((RlpList) list.getValues().get(0)).getValues();
        return new InputRawTransaction(
                GHash.fromByteArray(((RlpString) items.get(0)).getBytes()),
                GUint64.fromBigInteger(new BigInteger(((RlpString) items.get(1)).getBytes())),
                GUint64.fromBigInteger(new BigInteger(((RlpString) items.get(2)).getBytes())),
                GHash.fromByteArray(((RlpString) items.get(3)).getBytes()),
                new String(((RlpString) items.get(4)).getBytes()),
                new String(((RlpString) items.get(5)).getBytes()),
                new String(((RlpString) items.get(6)).getBytes()),
                GUint64.fromBigInteger(new BigInteger(((RlpString) items.get(7)).getBytes())),
                GAddress.fromByteArray(
                        ((RlpString) ((RlpList) items.get(8)).getValues().get(0)).getBytes()));
    }
}
