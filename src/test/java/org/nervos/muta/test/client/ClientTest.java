package org.nervos.muta.test.client;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.nervos.muta.client.Client;
import org.nervos.muta.client.type.graphql_schema_scalar.Uint64;
import org.nervos.muta.client.type.response.Block;

public class ClientTest {

  public static Client client;

  public ClientTest() {
    client = Client.defaultClient();
  }

  @Test
  @Order(1)
  public void getBlock() throws IOException {
    Block block = client.getBlock(Uint64.ZERO);
    Assertions.assertEquals("0x0000000000000000", block.getHeader().getHeight());
  }

  @Test
  @Order(1)
  public void getLatestBlock() throws IOException {
    Block block = client.getBlock(null);
    Assertions.assertNotNull(block.getHeader());
  }
}
