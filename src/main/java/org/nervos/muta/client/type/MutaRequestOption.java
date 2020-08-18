package org.nervos.muta.client.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.nervos.muta.client.type.graphql_schema.GAddress;
import org.nervos.muta.client.type.graphql_schema.GHash;
import org.nervos.muta.client.type.graphql_schema.GUint64;

@AllArgsConstructor
@Data
public class MutaRequestOption {

    @NonNull private GHash chainId;

    @NonNull private GUint64 cyclesLimit;

    @NonNull private GUint64 cyclesPrice;

    @NonNull private GUint64 timeout;

    @NonNull private GAddress caller;

    private int polling_interval;

    private int polling_times;

    public static MutaRequestOption defaultMutaRequestOption() {
        return new MutaRequestOption(
                GHash.fromHexString(
                        "0xb6a4d7da21443f5e816e8700eea87610e6d769657d6b8ec73028457bf2ca4036"),
                GUint64.fromHexadecimalString("0x00000000ffffffff"),
                GUint64.fromHexadecimalString("0x0000000000000000"),
                GUint64.fromHexadecimalString("0x0000000000000014"), // 20decimal
                GAddress.fromHexString("0x0000000000000000000000000000000000000000"),
                2000,
                10);
    }
}
