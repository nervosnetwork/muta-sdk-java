package org.nervos.muta.client.type;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class MutaRequestOption {

    @NonNull
    public String chainId;

    @NonNull
    public String cyclesLimit;

    @NonNull
    public String cyclesPrice;

    @NonNull
    public String timeout;

    @NonNull
    public String caller;

    public int polling_interval;

    public int polling_times;

    public static MutaRequestOption defaultMutaRequestOption(){
        return new MutaRequestOption(
                "0xb6a4d7da21443f5e816e8700eea87610e6d769657d6b8ec73028457bf2ca4036",
                "0xffff",
                "0x0000",
                "0x0014",//20decimal
                "0x0000000000000000000000000000000000000000",
                3000,
                10
        );
    }
}
