package org.nervos.muta.service.authorization;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.nervos.muta.Muta;
import org.nervos.muta.service.authorization.type.AddVerifiedItemPayload;
import org.nervos.muta.service.authorization.type.RemoveVerifiedItemPayload;
import org.nervos.muta.service.authorization.type.SetAdminPayload;

@AllArgsConstructor
@Getter
public class AuthorizationService {
    public static final String SERVICE_NAME = "authorization";
    public static final String METHOD_ADD_VERIFIED_ITEM = "add_verified_item";
    public static final String METHOD_REMOVE_VERIFIED_ITEM = "remove_verified_item";
    public static final String METHOD_SET_ADMIN = "set_admin";
    private final Muta muta;

    public void add_verified_item(AddVerifiedItemPayload addVerifiedItemPayload)
            throws IOException {
        muta.queryService(
                SERVICE_NAME,
                METHOD_ADD_VERIFIED_ITEM,
                addVerifiedItemPayload,
                new TypeReference<Void>() {});
    }

    public void remove_verified_item(RemoveVerifiedItemPayload removeVerifiedItemPayload)
            throws IOException {
        muta.queryService(
                SERVICE_NAME,
                METHOD_REMOVE_VERIFIED_ITEM,
                removeVerifiedItemPayload,
                new TypeReference<Void>() {});
    }

    public void set_admin(SetAdminPayload setAdminPayload) throws IOException {
        muta.queryService(
                SERVICE_NAME, METHOD_SET_ADMIN, setAdminPayload, new TypeReference<Void>() {});
    }
}
