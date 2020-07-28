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
import org.nervos.muta.client.type.primitive.Hash;
import org.nervos.muta.util.Util;

@EqualsAndHashCode
@JsonDeserialize(using = GHash.GHashDeserializer.class)
@JsonSerialize(using = GHash.GHashSerializer.class)
public class GHash {
    public static int LENGTH = 32;
    private final byte[] b;

    private GHash(byte[] b) {
        if (is_invalid(b)) {
            throw new RuntimeException("GHash construction fails, input bytes[] != 32");
        }
        this.b = b;
    }

    public static boolean is_invalid(byte[] b) {
        return b.length != LENGTH;
    }

    public static GHash fromByteArray(byte[] b) {
        return new GHash(b);
    }

    public static GHash fromHexString(String hexString) {
        Util.isValidHex(hexString);
        byte[] b = Hex.decode(Util.remove0x(hexString));
        return new GHash(b);
    }

    public byte[] get() {
        return this.b;
    }

    @Override
    public String toString() {
        return "GHash{" + Hex.toHexString(b) + '}';
    }

    public Hash toHash() {
        return Hash.fromByteArray(this.get());
    }

    public static class GHashSerializer extends StdSerializer<GHash> {
        public GHashSerializer() {
            this(null);
        }

        public GHashSerializer(Class<GHash> t) {
            super(t);
        }

        @Override
        public void serialize(GHash value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            gen.writeString(Util.start0x(Hex.toHexString(value.b)));
        }
    }

    public static class GHashDeserializer extends StdDeserializer<GHash> {
        public GHashDeserializer() {
            this(null);
        }

        public GHashDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public GHash deserialize(JsonParser jsonParser, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            ObjectCodec oc = jsonParser.getCodec();
            JsonNode root = oc.readTree(jsonParser);
            String hex = root.asText();

            return GHash.fromHexString(hex);
        }
    }
}
