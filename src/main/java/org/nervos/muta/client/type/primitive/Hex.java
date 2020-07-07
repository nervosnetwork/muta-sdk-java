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
import org.nervos.muta.util.Util;

@EqualsAndHashCode
@JsonDeserialize(using = Hex.HexDeserializer.class)
@JsonSerialize(using = Hex.HexSerializer.class)
public class Hex {
    // must start with 0x prefix
    private final String inner;

    private Hex(String hexString) {
        try {
            Util.isValidHex(hexString);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Hex construction fails, HexString doesn't start with 0x prefix");
        }

        this.inner = hexString;
    }

    public static Hex fromHexString(String hexString) {
        return new Hex(hexString);
    }

    public String get() {
        return this.inner;
    }

    @Override
    public String toString() {
        return "Hex{" + this.inner + '}';
    }

    public static class HexSerializer extends StdSerializer<Hex> {
        public HexSerializer() {
            this(null);
        }

        public HexSerializer(Class<Hex> t) {
            super(t);
        }

        @Override
        public void serialize(Hex value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            gen.writeString(value.inner);
        }
    }

    public static class HexDeserializer extends StdDeserializer<Hex> {
        public HexDeserializer() {
            this(null);
        }

        public HexDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public Hex deserialize(JsonParser jsonParser, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            ObjectCodec oc = jsonParser.getCodec();
            JsonNode root = oc.readTree(jsonParser);
            String hex = root.asText();

            return Hex.fromHexString(hex);
        }
    }
}
