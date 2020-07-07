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
@JsonDeserialize(using = U8.U8Deserializer.class)
@JsonSerialize(using = U8.U8Serializer.class)
public class U8 {
    public static BigInteger MAX = new BigInteger("ff", 16);
    public static BigInteger MIN = BigInteger.ZERO;
    public static U8 ZERO = U8.fromBigInteger(BigInteger.ZERO);

    private BigInteger inner;

    private U8(BigInteger inner) {
        if (inner.compareTo(MAX) > 0 || inner.compareTo(MIN) < 0) {
            throw new RuntimeException("U8, out of range");
        }
        this.inner = inner;
    }

    public static U8 fromBigInteger(BigInteger input) {
        return new U8(input);
    }

    public static U8 fromLong(long input) {
        return new U8(BigInteger.valueOf(input));
    }

    public static U8 fromHexString(String hexString) {
        Util.isValidHex(hexString);
        return new U8(new BigInteger(Util.remove0x(hexString), 16));
    }

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        String se = objectMapper.writeValueAsString(U8.fromLong(1234));
        System.out.println(se);

        U8 de = objectMapper.readValue(se, U8.class);
        System.out.println(de);
    }

    @Override
    public String toString() {
        return "U8{" + inner.toString() + '}';
    }

    public static class U8Serializer extends StdSerializer<U8> {

        public U8Serializer() {
            this(null);
        }

        public U8Serializer(Class<U8> t) {
            super(t);
        }

        @Override
        public void serialize(U8 value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            gen.writeNumber(value.inner);
        }
    }

    public static class U8Deserializer extends StdDeserializer<U8> {
        public U8Deserializer() {
            this(null);
        }

        public U8Deserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public U8 deserialize(JsonParser jsonParser, DeserializationContext ctxt)
                throws IOException {
            ObjectCodec oc = jsonParser.getCodec();
            JsonNode root = oc.readTree(jsonParser);
            // because as long maybe not enough to handle u8
            String decimal = root.asText();
            return U8.fromBigInteger(new BigInteger(decimal));
        }
    }
}
