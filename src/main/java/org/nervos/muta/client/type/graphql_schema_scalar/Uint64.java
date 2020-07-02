package org.nervos.muta.client.type.graphql_schema_scalar;

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
import org.bouncycastle.util.encoders.Hex;
import org.nervos.muta.util.Util;

@EqualsAndHashCode
@JsonDeserialize(using = Uint64.Uint64Deserializer.class)
@JsonSerialize(using = Uint64.Uint64Serializer.class)
public class Uint64 {

  public static BigInteger MAX = new BigInteger("FFFFFFFFFFFFFFFF", 16);
  public static BigInteger MIN = BigInteger.ZERO;

  public static Uint64 ZERO = Uint64.fromLong(0);
  public static Uint64 ONE = Uint64.fromLong(1);
  private BigInteger inner;

  private Uint64(BigInteger input) {
    if (is_invalid(input)) {
      throw new RuntimeException("Uint64 overflow or underflow");
    }
    this.inner = input;
  }

  public static boolean is_invalid(BigInteger bi) {
    return bi.compareTo(MAX) > 0 || bi.compareTo(MIN) < 0;
  }

  public static Uint64 fromBigInteger(BigInteger bigInteger) {
    return new Uint64(bigInteger);
  }

  public static Uint64 fromHexString(String hexString) {
    Util.isValidHex(hexString);
    // add sign byte 0x00 to indicate that's a positive number
    hexString = "00" + Util.remove0x(hexString);
    byte[] data = Hex.decode(hexString);
    BigInteger bigInteger = new BigInteger(data);

    if (is_invalid(bigInteger)) {
      throw new RuntimeException("Uint64 overflow or underflow while deserializing");
    }

    return new Uint64(bigInteger);
  }

  public static Uint64 fromLong(long input) {
    return new Uint64(BigInteger.valueOf(input));
  }

  public static void main(String[] args) throws JsonProcessingException {

    ObjectMapper objectMapper = new ObjectMapper();

    String r =
        objectMapper.writeValueAsString(
            Uint64.fromBigInteger(new BigInteger("18446744073709551615", 10)));
    System.out.println(r);
    Uint64 a = objectMapper.readValue("\"18446744073709551615\"", Uint64.class);
    System.out.println(a);
  }

  public void set(BigInteger input) {
    if (is_invalid(input)) {
      throw new RuntimeException("Uint64 overflow or underflow");
    }
    this.inner = input;
  }

  public BigInteger get() {
    return this.inner;
  }

  @Override
  public String toString() {
    return "Uint64{" + inner.toString(16) + '}';
  }

  public static class Uint64Serializer extends StdSerializer<Uint64> {

    public Uint64Serializer() {
      this(null);
    }

    public Uint64Serializer(Class<Uint64> t) {
      super(t);
    }

    @Override
    public void serialize(Uint64 value, JsonGenerator gen, SerializerProvider provider)
        throws IOException {
      gen.writeString(Util.start0x(value.inner.toString(16)));
    }
  }

  public static class Uint64Deserializer extends StdDeserializer<Uint64> {
    public Uint64Deserializer() {
      this(null);
    }

    public Uint64Deserializer(Class<?> vc) {
      super(vc);
    }

    @Override
    public Uint64 deserialize(JsonParser jsonParser, DeserializationContext ctxt)
        throws IOException {
      ObjectCodec oc = jsonParser.getCodec();
      JsonNode root = oc.readTree(jsonParser);
      String hex = root.asText();
      return Uint64.fromHexString(hex);
    }
  }
}
