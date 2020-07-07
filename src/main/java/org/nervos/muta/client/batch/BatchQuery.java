package org.nervos.muta.client.batch;

public interface BatchQuery {
    public String getBatchQuery();

    public String getBatchParamPrefix();

    public String getBatchParamType();

    public String getBatchAliasPrefix();

    public String getBatchQueryFragment();

    public Object getParamValue();
}
