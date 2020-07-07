package org.nervos.muta.client.type.graphql_schema;

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
import org.bouncycastle.util.encoders.Hex;
import org.nervos.muta.client.type.primitive.U64;
import org.nervos.muta.util.Util;

@EqualsAndHashCode
@JsonDeserialize(using = GUint64.GUint64Deserializer.class)
@JsonSerialize(using = GUint64.GUint64Serializer.class)
public class GUint64 {

    public static BigInteger MAX = new BigInteger("FFFFFFFFFFFFFFFF", 16);
    public static BigInteger MIN = BigInteger.ZERO;

    public static GUint64 ZERO = GUint64.fromLong(0);
    public static GUint64 ONE = GUint64.fromLong(1);
    private BigInteger inner;

    private GUint64(BigInteger input) {
        if (is_invalid(input)) {
            throw new RuntimeException("GUint64 overflow or underflow");
        }
        this.inner = input;
    }

    public static boolean is_invalid(BigInteger bi) {
        return bi.compareTo(MAX) > 0 || bi.compareTo(MIN) < 0;
    }

    public static GUint64 fromBigInteger(BigInteger bigInteger) {
        return new GUint64(bigInteger);
    }

    public static GUint64 fromHexString(String hexString) {
        Util.isValidHex(hexString);
        // add sign byte 0x00 to indicate that's a positive number
        hexString = "00" + Util.remove0x(hexString);
        byte[] data = Hex.decode(hexString);
        BigInteger bigInteger = new BigInteger(data);

        if (is_invalid(bigInteger)) {
            throw new RuntimeException("GUint64 overflow or underflow while deserializing");
        }

        return new GUint64(bigInteger);
    }

    public static GUint64 fromLong(long input) {
        return new GUint64(BigInteger.valueOf(input));
    }

    public void set(BigInteger input) {
        if (is_invalid(input)) {
            throw new RuntimeException("GUint64 overflow or underflow");
        }
        this.inner = input;
    }

    public BigInteger get() {
        return this.inner;
    }

    @Override
    public String toString() {
        return "GUint64{" + inner.toString(16) + '}';
    }

    public static class GUint64Serializer extends StdSerializer<GUint64> {

        public GUint64Serializer() {
            this(null);
        }

        public GUint64Serializer(Class<GUint64> t) {
            super(t);
        }

        @Override
        public void serialize(GUint64 value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            gen.writeString(Util.start0x(value.inner.toString(16)));
        }
    }

    public static class GUint64Deserializer extends StdDeserializer<GUint64> {
        public GUint64Deserializer() {
            this(null);
        }

        public GUint64Deserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public GUint64 deserialize(JsonParser jsonParser, DeserializationContext ctxt)
                throws IOException {
            ObjectCodec oc = jsonParser.getCodec();
            JsonNode root = oc.readTree(jsonParser);
            String hex = root.asText();
            return GUint64.fromHexString(hex);
        }
    }

    public U64 toU64() {
        return U64.fromBigInteger(this.get());
    }
}
