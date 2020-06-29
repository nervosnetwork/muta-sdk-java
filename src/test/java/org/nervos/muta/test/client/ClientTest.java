package org.nervos.muta.test.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.nervos.muta.client.Client;
import org.nervos.muta.client.type.response.Block;

import java.io.IOException;

public class ClientTest {

    public static Client client;

    public ClientTest(){
        client = Client.defaultClient();
    }

    @Test
    @Order(1)
    public void getBlock() throws IOException {
        Block block = client.getBlock("0x00");
        Assertions.assertEquals("0x0000000000000000",block.getHeader().getHeight());
    }

    @Test
    @Order(1)
    public void getLatestBlock() throws IOException {
        Block block = client.getBlock(null);
        Assertions.assertNotNull(block.getHeader());
    }


}
