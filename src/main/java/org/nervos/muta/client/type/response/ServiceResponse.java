package org.nervos.muta.client.type.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceResponse {

        public String code;
        public String succeedData;
        public String errorMessage;

}
