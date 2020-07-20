package org.nervos.muta.client.type.graphql_schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event of Muta-Chain
 *
 * @author Lycrus Hamster
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    /** which service this event emits from */
    private String service;
    /** the name of this event, a.k.a. topic */
    private String name;
    /** concrete info of event */
    private String data;
}
