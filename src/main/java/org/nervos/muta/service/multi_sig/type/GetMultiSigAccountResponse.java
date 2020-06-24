package org.nervos.muta.service.multi_sig.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetMultiSigAccountResponse {

    private MultiSigPermission permission;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MultiSigPermission{
        private String owner;
        private List<Account> accounts;
        private int threshold;
        private String memo;
    }
}
