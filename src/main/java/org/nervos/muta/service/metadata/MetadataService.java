package org.nervos.muta.service.metadata;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.nervos.muta.Muta;
import org.nervos.muta.client.type.primitive.Metadata;

@AllArgsConstructor
@Getter
public class MetadataService {
  public static final String SERVICE_NAME = "metadata";
  public static final String METHOD_GET_METADATA = "get_metadata";
  private final Muta muta;

  public Metadata getMetadata() throws IOException {
    Metadata metadata =
        muta.queryService(SERVICE_NAME, METHOD_GET_METADATA, new TypeReference<Metadata>() {});

    return metadata;
  }
}
