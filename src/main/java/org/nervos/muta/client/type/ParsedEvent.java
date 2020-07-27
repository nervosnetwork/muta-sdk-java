package org.nervos.muta.client.type;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.nervos.muta.client.type.graphql_schema.Event;

@Data
public class ParsedEvent<T> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private String service;
    private String name;
    private T data;

    public ParsedEvent(String service, String name, String data, TypeReference<T> tr)
            throws JsonProcessingException {
        this.service = service;
        this.name = name;
        this.data = objectMapper.readValue(data, tr);
    }

    public static <R> ParsedEvent<R> fromEvent(Event event, TypeReference<R> tr)
            throws JsonProcessingException {
        return new ParsedEvent<R>(event.getService(), event.getName(), event.getData(), tr);
    }

    public boolean isMatch(String service, String name) {
        return this.service.equals(service) && this.name.equals(name);
    }
}
