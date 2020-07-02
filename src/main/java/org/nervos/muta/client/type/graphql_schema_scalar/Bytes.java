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

    byte[] b = Hex.decode(Util.remove0x(hexString));
    return new Bytes(b);
  }

  public byte[] get() {
    return this.b;
  }

  @Override
  public String toString() {
    return "Bytes{" + Hex.toHexString(b) + '}';
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
      gen.writeString(Util.start0x(Hex.toHexString(value.b)));
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
      ObjectCodec oc = jsonParser.getCodec();
      JsonNode root = oc.readTree(jsonParser);
      String hex = root.asText();
      return Bytes.fromHexString(hex);
    }
  }
}
