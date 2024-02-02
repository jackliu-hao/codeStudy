package org.xnio.sasl;

import java.nio.ByteBuffer;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

final class SaslClientWrapper extends SaslWrapper {
   private final SaslClient saslClient;

   SaslClientWrapper(SaslClient saslClient) {
      this.saslClient = saslClient;
   }

   public byte[] wrap(byte[] bytes, int off, int len) throws SaslException {
      return this.saslClient.wrap(bytes, off, len);
   }

   public byte[] unwrap(byte[] bytes, int off, int len) throws SaslException {
      return this.saslClient.unwrap(bytes, off, len);
   }

   public byte[] wrap(ByteBuffer source) throws SaslException {
      return SaslUtils.wrap(this.saslClient, source);
   }

   public byte[] unwrap(ByteBuffer source) throws SaslException {
      return SaslUtils.unwrap(this.saslClient, source);
   }
}
