package org.nervos.muta.client.type.primitive;

import org.nervos.muta.client.type.graphql_schema_scalar.Address;
import org.nervos.muta.client.type.graphql_schema_scalar.Hash;
import org.nervos.muta.client.type.graphql_schema_scalar.Uint64;

public class RawTransaction {
  private Hash chain_id;
  private Uint64 cycles_price;
  private Uint64 cycles_limit;
  private Hash nonce;
  private TransactionRequest request;
  private Uint64 timeout;
  private Address sender;
}
