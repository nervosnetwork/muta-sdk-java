package org.nervos.muta.client.type.primitive;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import org.bouncycastle.util.encoders.Hex;
import org.nervos.muta.client.type.graphql_schema.GAddress;
import org.nervos.muta.util.Bech32Util;
import org.nervos.muta.util.Util;

@EqualsAndHashCode
@JsonDeserialize(using = Address.AddressDeserializer.class)
@JsonSerialize(using = Address.AddressSerializer.class)
public class Address {
    public static int LENGTH = 20;
    public static Address ZERO_ADDRESS =
            fromHexString("0x0000000000000000000000000000000000000000");
    private final byte[] b;

    private final String bech32str;

    private Address(byte[] b) {
        if (is_invalid(b)) {
            throw new RuntimeException("Address construction fails, input bytes[] != 20");
        }
        this.b = b;
        this.bech32str = Bech32Util.encodeAddress(b, null);
    }

    public static boolean is_invalid(byte[] b) {
        return b.length != LENGTH;
    }

    public static Address fromByteArray(byte[] b) {
        return new Address(b);
    }

    public static Address fromHexString(String hexString) {
        Util.isValidHex(hexString);
        byte[] b = Hex.decode(Util.remove0x(hexString));
        return new Address(b);
    }

    public static Address fromBech32(String bech32String) {
        byte[] address = Bech32Util.decodeAddress(bech32String, null);
        return fromByteArray(address);
    }

    public byte[] get() {
        return this.b;
    }

    @Override
    public String toString() {
        return "Address{" + Hex.toHexString(b) + '}';
    }

    public GAddress toGAdress() {
        return GAddress.fromByteArray(this.get());
    }

    public static class AddressSerializer extends StdSerializer<Address> {
        public AddressSerializer() {
            this(null);
        }

        public AddressSerializer(Class<Address> t) {
            super(t);
        }

        @Override
        public void serialize(Address value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            gen.writeString(value.bech32str);
        }
    }

    public static class AddressDeserializer extends StdDeserializer<Address> {
        public AddressDeserializer() {
            this(null);
        }

        public AddressDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public Address deserialize(JsonParser jsonParser, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            ObjectCodec oc = jsonParser.getCodec();
            JsonNode root = oc.readTree(jsonParser);
            String bech32String = root.asText();

            return Address.fromBech32(bech32String);
        }
    }
}
