package org.nervos.muta.client.batch;

/** This interface is a mark to justify which GraphQl queries are capable of batch */
public interface BatchQuery {
    String getBatchQuery();

    String getBatchParamPrefix();

    String getBatchParamType();

    String getBatchAliasPrefix();

    String getBatchQueryFragment();

    Object getParamValue();
}
