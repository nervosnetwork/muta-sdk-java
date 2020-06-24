package org.nervos.muta.service.asset.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllowanceResponse {
    private String asset_id;
    private String grantor;
    private String grantee;
    private long value;
}
