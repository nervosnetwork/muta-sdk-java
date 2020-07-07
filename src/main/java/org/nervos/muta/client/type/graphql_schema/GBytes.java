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
import org.nervos.muta.client.type.primitive.Bytes;
import org.nervos.muta.util.Util;

@EqualsAndHashCode
@JsonDeserialize(using = GBytes.GBytesDeserializer.class)
@JsonSerialize(using = GBytes.GBytesSerializer.class)
public class GBytes {

    private final byte[] b;

    private GBytes(byte[] b) {
        this.b = b;
    }

    public static GBytes fromByteArray(byte[] b) {
        return new GBytes(b);
    }

    public static GBytes fromHexString(String hexString) {
        Util.isValidHex(hexString);

        byte[] b = Hex.decode(Util.remove0x(hexString));
        return new GBytes(b);
    }

    public byte[] get() {
        return this.b;
    }

    @Override
    public String toString() {
        return "GBytes{" + Hex.toHexString(b) + '}';
    }

    public static class GBytesSerializer extends StdSerializer<GBytes> {
        public GBytesSerializer() {
            this(null);
        }

        public GBytesSerializer(Class<GBytes> t) {
            super(t);
        }

        @Override
        public void serialize(GBytes value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            gen.writeString(Util.start0x(Hex.toHexString(value.b)));
        }
    }

    public static class GBytesDeserializer extends StdDeserializer<GBytes> {
        public GBytesDeserializer() {
            this(null);
        }

        public GBytesDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public GBytes deserialize(JsonParser jsonParser, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            ObjectCodec oc = jsonParser.getCodec();
            JsonNode root = oc.readTree(jsonParser);
            String hex = root.asText();
            return GBytes.fromHexString(hex);
        }
    }

    public Bytes toBytes() {
        return Bytes.fromByteArray(this.get());
    }
}
