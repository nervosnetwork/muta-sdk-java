package org.nervos.muta.client.type.primitive;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.math.BigInteger;
import lombok.EqualsAndHashCode;
import org.nervos.muta.client.type.graphql_schema.GUint64;
import org.nervos.muta.util.Util;

@EqualsAndHashCode
@JsonDeserialize(using = U64.U64Deserializer.class)
@JsonSerialize(using = U64.U64Serializer.class)
public class U64 {
    public static BigInteger MAX = new BigInteger("ffffffffffffffff", 16);
    public static BigInteger MIN = BigInteger.ZERO;

    public static U64 ZERO = U64.fromBigInteger(BigInteger.ZERO);

    private final BigInteger inner;

    private U64(BigInteger inner) {
        if (inner.compareTo(MAX) > 0 || inner.compareTo(MIN) < 0) {
            throw new RuntimeException("U64, out of range");
        }
        this.inner = inner;
    }

    public static U64 fromBigInteger(BigInteger input) {
        return new U64(input);
    }

    public static U64 fromLong(long input) {
        return new U64(BigInteger.valueOf(input));
    }

    public static U64 fromHexString(String hexString) {
        Util.isValidHex(hexString);
        return new U64(new BigInteger(Util.remove0x(hexString), 16));
    }

    public BigInteger get() {
        return this.inner;
    }

    @Override
    public String toString() {
        return "U64{" + inner.toString() + '}';
    }

    public static class U64Serializer extends StdSerializer<U64> {

        public U64Serializer() {
            this(null);
        }

        public U64Serializer(Class<U64> t) {
            super(t);
        }

        @Override
        public void serialize(U64 value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            gen.writeNumber(value.inner);
        }
    }

    public static class U64Deserializer extends StdDeserializer<U64> {
        public U64Deserializer() {
            this(null);
        }

        public U64Deserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public U64 deserialize(JsonParser jsonParser, DeserializationContext ctxt)
                throws IOException {
            ObjectCodec oc = jsonParser.getCodec();
            JsonNode root = oc.readTree(jsonParser);
            // because as long maybe not enough to handle u64
            String decimal = root.asText();
            return U64.fromBigInteger(new BigInteger(decimal));
        }
    }

    public GUint64 toGUint64() {
        return GUint64.fromBigInteger(this.get());
    }
}
