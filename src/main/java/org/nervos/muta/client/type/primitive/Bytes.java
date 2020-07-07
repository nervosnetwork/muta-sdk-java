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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import org.nervos.muta.client.type.graphql_schema.GBytes;
import org.nervos.muta.util.Util;

@EqualsAndHashCode
@JsonDeserialize(using = Bytes.BytesDeserializer.class)
@JsonSerialize(using = Bytes.BytesSerializer.class)
public class Bytes {

    private final byte[] b;

    private Bytes(byte[] b) {
        this.b = b;
    }

    public static Bytes fromByteArray(byte[] b) {
        return new Bytes(b);
    }

    public static Bytes fromHexString(String hexString) {
        Util.isValidHex(hexString);
        byte[] b = org.bouncycastle.util.encoders.Hex.decode(Util.remove0x(hexString));
        return new Bytes(b);
    }

    public byte[] get() {
        return this.b;
    }

    @Override
    public String toString() {
        return "Bytes{" + org.bouncycastle.util.encoders.Hex.toHexString(b) + '}';
    }

    public static class BytesSerializer extends StdSerializer<Bytes> {
        public BytesSerializer() {
            this(null);
        }

        public BytesSerializer(Class<Bytes> t) {
            super(t);
        }

        @Override
        public void serialize(Bytes value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            int[] ii = new int[value.b.length];
            for (int index = 0; index < value.b.length; index++) {
                ii[index] = Byte.toUnsignedInt(value.b[index]);
            }

            gen.writeArray(ii, 0, ii.length);
        }
    }

    public static class BytesDeserializer extends StdDeserializer<Bytes> {
        public BytesDeserializer() {
            this(null);
        }

        public BytesDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public Bytes deserialize(JsonParser jsonParser, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {

            List<Byte> data = new ArrayList<>();

            ObjectCodec oc = jsonParser.getCodec();
            JsonNode root = oc.readTree(jsonParser);
            if (root.isArray()) {
                ArrayNode arrayRoot = (ArrayNode) root;
                byte[] bb = new byte[arrayRoot.size()];

                for (int index = 0; index < arrayRoot.size(); index++) {

                    bb[index] = Integer.valueOf(arrayRoot.get(index).asInt()).byteValue();
                }
                return Bytes.fromByteArray(bb);
            } else {
                throw new IOException("Byte need a int array");
            }
        }
    }

    public GBytes toGBytes() {
        return GBytes.fromByteArray(this.get());
    }
}
