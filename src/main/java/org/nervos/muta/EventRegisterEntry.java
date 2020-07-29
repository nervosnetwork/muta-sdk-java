package org.nervos.muta;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * And request entry for registering concerned event
 *
 * @param <T> type param to unmarshall event data
 */
@AllArgsConstructor
@Builder
@Data
public class EventRegisterEntry<T> {
    /** service name */
    private String service;
    /** topic name */
    private String name;
    /** corresponding java class */
    private TypeReference<T> dataType;
}
