package org.nervos.muta.service.metadata;

import lombok.AllArgsConstructor;
import org.nervos.muta.Muta;
import org.nervos.muta.service.metadata.type.Metadata;

import java.io.IOException;

@AllArgsConstructor
public class MetadataService {
    public Muta muta;

    public static final String SERVICE_NAME = "metadata";
    public static final String METHOD_GET_METADATA = "get_metadata";

    public Metadata getMetadata() throws IOException {
        Metadata metadata = muta.queryService(SERVICE_NAME,METHOD_GET_METADATA, Metadata.class);

        return metadata;
    }
}
