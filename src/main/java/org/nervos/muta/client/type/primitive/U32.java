package org.nervos.muta.client.type.primitive;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.math.BigInteger;
import lombok.EqualsAndHashCode;
import org.nervos.muta.util.Util;

@EqualsAndHashCode
@JsonDeserialize(using = U32.U32Deserializer.class)
@JsonSerialize(using = U32.U32Serializer.class)
public class U32 {
    public static BigInteger MAX = new BigInteger("ffffffff", 16);
    public static BigInteger MIN = BigInteger.ZERO;

    public static U32 ZERO = U32.fromBigInteger(BigInteger.ZERO);

    private final BigInteger inner;

    private U32(BigInteger inner) {
        if (inner.compareTo(MAX) > 0 || inner.compareTo(MIN) < 0) {
            throw new RuntimeException("U8, out of range");
        }
        this.inner = inner;
    }

    public static U32 fromBigInteger(BigInteger input) {
        return new U32(input);
    }

    public static U32 fromLong(long input) {
        return new U32(BigInteger.valueOf(input));
    }

    public static U32 fromHexString(String hexString) {
        Util.isValidHex(hexString);
        return new U32(new BigInteger(Util.remove0x(hexString), 16));
    }

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        String se = objectMapper.writeValueAsString(U32.fromLong(1234));
        System.out.println(se);

        U32 de = objectMapper.readValue(se, U32.class);
        System.out.println(de);
    }

    @Override
    public String toString() {
        return "U32{" + inner.toString() + '}';
    }

    public static class U32Serializer extends StdSerializer<U32> {

        public U32Serializer() {
            this(null);
        }

        public U32Serializer(Class<U32> t) {
            super(t);
        }

        @Override
        public void serialize(U32 value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            gen.writeNumber(value.inner);
        }
    }

    public static class U32Deserializer extends StdDeserializer<U32> {
        public U32Deserializer() {
            this(null);
        }

        public U32Deserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public U32 deserialize(JsonParser jsonParser, DeserializationContext ctxt)
                throws IOException {
            ObjectCodec oc = jsonParser.getCodec();
            JsonNode root = oc.readTree(jsonParser);
            String decimal = root.asText();
            return U32.fromBigInteger(new BigInteger(decimal));
        }
    }
}
