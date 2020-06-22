package org.nervos.muta.service.multi_sig.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Vector;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetMultiSigAccountResponse {

    public MultiSigPermission permission;

    public static class MultiSigPermission{
        public String owner;
        public Vector<Account> accounts;
        public int threshold;
        public String memo;
    }
}
