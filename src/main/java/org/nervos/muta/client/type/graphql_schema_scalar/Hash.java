package org.nervos.muta.client.type.graphql_schema_scalar;

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
import org.nervos.muta.util.Util;

@EqualsAndHashCode
@JsonDeserialize(using = Hash.HashDeserializer.class)
@JsonSerialize(using = Hash.HashSerializer.class)
public class Hash {
  public static int LENGTH = 32;
  private final byte[] b;

  private Hash(byte[] b) {
    if (is_invalid(b)) {
      throw new RuntimeException("Hash construction fails, input bytes[] != 32");
    }
    this.b = b;
  }

  public static boolean is_invalid(byte[] b) {
    return b.length != LENGTH;
  }

  public static Hash fromByteArray(byte[] b) {
    return new Hash(b);
  }

  public static Hash fromHexString(String hexString) {
    Util.isValidHex(hexString);
    byte[] b = Hex.decode(Util.remove0x(hexString));
    return new Hash(b);
  }

  public byte[] get() {
    return this.b;
  }

  @Override
  public String toString() {
    return "Hash{" + Hex.toHexString(b) + '}';
  }

  public static class HashSerializer extends StdSerializer<Hash> {
    public HashSerializer() {
      this(null);
    }

    public HashSerializer(Class<Hash> t) {
      super(t);
    }

    @Override
    public void serialize(Hash value, JsonGenerator gen, SerializerProvider provider)
        throws IOException {
      gen.writeString(Util.start0x(Hex.toHexString(value.b)));
    }
  }

  public static class HashDeserializer extends StdDeserializer<Hash> {
    public HashDeserializer() {
      this(null);
    }

    public HashDeserializer(Class<?> vc) {
      super(vc);
    }

    @Override
    public Hash deserialize(JsonParser jsonParser, DeserializationContext ctxt)
        throws IOException, JsonProcessingException {
      ObjectCodec oc = jsonParser.getCodec();
      JsonNode root = oc.readTree(jsonParser);
      String hex = root.asText();

      return Hash.fromHexString(hex);
    }
  }
}
