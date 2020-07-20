package org.nervos.muta.client.type.graphql_schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.web3j.rlp.RlpEncoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;

/**
 * Imply a transaction with needed params
 *
 * @author Lycrus Hamster
 */
@Data
@AllArgsConstructor
@Builder
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
    /** the payload of the method, it acts like param of function */
    @NonNull private String payload;
    /** the service of the query */
    @NonNull private String serviceName;
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
}
