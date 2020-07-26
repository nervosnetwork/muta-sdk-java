package org.nervos.muta.client.type;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigInteger;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.nervos.muta.client.type.graphql_schema.GUint64;
import org.nervos.muta.client.type.graphql_schema.ServiceResponse;

@Data
@AllArgsConstructor
public class ParsedServiceResponse<T> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private GUint64 code;
    private T succeedData;
    private String errorMessage;

    public static <T> ParsedServiceResponse<T> fromServiceResponse(
            ServiceResponse input, TypeReference<T> tr) throws JsonProcessingException {
        if (input.isError()) {
            return new ParsedServiceResponse<T>(input.getCode(), null, input.getErrorMessage());
        }

        String succeedMsg = input.getSucceedData();

        // it's weird design for muta
        if ("".equals(succeedMsg)) {
            succeedMsg = "null";
        }

        T ret = objectMapper.readValue(succeedMsg, tr);

        return new ParsedServiceResponse<T>(input.getCode(), ret, input.getErrorMessage());
    }

    public boolean isError() {
        return code.get().compareTo(BigInteger.ZERO) != 0;
    }
}
