package org.nervos.muta;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class EventRegisterEntry<T> {
    private String service;
    private String name;
    private TypeReference<T> dataType;
}
