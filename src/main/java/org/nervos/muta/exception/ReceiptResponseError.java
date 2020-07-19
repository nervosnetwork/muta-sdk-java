package org.nervos.muta.exception;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nervos.muta.client.type.graphql_schema.ServiceResponse;

/**
 * Simply indicate error while parse service response {@link
 * org.nervos.muta.Muta#parseServiceResponse(ServiceResponse, TypeReference)}
 *
 * @author Lycrus Hamster
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ReceiptResponseError extends ServiceResponseError {
    private String serviceName;
    private String method;

    public ReceiptResponseError(String message) {
        super(message);
    }

    public ReceiptResponseError(
            String serviceName, String method, String code, String errorMessage) {
        super(
                "ReceiptResponseError, serviceName: "
                        + serviceName
                        + ", method: "
                        + method
                        + ", returns error code: "
                        + code
                        + ", errorMessage: "
                        + errorMessage);

        this.serviceName = serviceName;
        this.method = method;
        this.code = code;
        this.errorMessage = errorMessage;
    }
}
