package org.nervos.muta.service.asset.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllowanceResponse {
    public String asset_id;
    public String grantor;
    public String grantee;
    public long value;
}
