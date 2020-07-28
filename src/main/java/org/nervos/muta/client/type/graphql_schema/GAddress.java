package org.nervos.muta.client.type.graphql_schema;

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
import org.nervos.muta.client.type.primitive.Address;
import org.nervos.muta.util.Bech32Util;
import org.nervos.muta.util.Util;

@EqualsAndHashCode
@JsonDeserialize(using = GAddress.GAddressDeserializer.class)
@JsonSerialize(using = GAddress.GAddressSerializer.class)
public class GAddress {
    public static int LENGTH = 20;
    private final byte[] b;
    private final String bech32str;

    private GAddress(byte[] b) {
        if (is_invalid(b)) {
            throw new RuntimeException("GAddress construction fails, input bytes[] != 20");
        }
        this.b = b;
        this.bech32str = Bech32Util.encodeAddress(b);
    }

    public static boolean is_invalid(byte[] b) {
        return b.length != LENGTH;
    }

    public static GAddress fromByteArray(byte[] b) {
        return new GAddress(b);
    }

    public static GAddress fromHexString(String hexString) {
        Util.isValidHex(hexString);
        byte[] b = Hex.decode(Util.remove0x(hexString));
        return new GAddress(b);
    }

    public static GAddress fromBech32(String bech32String) {
        byte[] address = Bech32Util.decodeAddress(bech32String);
        return fromByteArray(address);
    }

    public byte[] get() {
        return this.b;
    }

    @Override
    public String toString() {
        return "GAddress{" + Hex.toHexString(b) + '}';
    }

    public Address toAddress() {
        return Address.fromByteArray(this.get());
    }

    public static class GAddressSerializer extends StdSerializer<GAddress> {
        public GAddressSerializer() {
            this(null);
        }

        public GAddressSerializer(Class<GAddress> t) {
            super(t);
        }

        @Override
        public void serialize(GAddress value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            gen.writeString(value.bech32str);
        }
    }

    public static class GAddressDeserializer extends StdDeserializer<GAddress> {
        public GAddressDeserializer() {
            this(null);
        }

        public GAddressDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public GAddress deserialize(JsonParser jsonParser, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            ObjectCodec oc = jsonParser.getCodec();
            JsonNode root = oc.readTree(jsonParser);
            String bech32String = root.asText();

            return GAddress.fromBech32(bech32String);
        }
    }
}
