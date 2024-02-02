package org.xnio.sasl;

import java.nio.ByteBuffer;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;

final class SaslServerWrapper extends SaslWrapper {
   private final SaslServer saslServer;

   SaslServerWrapper(SaslServer saslServer) {
      this.saslServer = saslServer;
   }

   public byte[] wrap(byte[] bytes, int off, int len) throws SaslException {
      return this.saslServer.wrap(bytes, off, len);
   }

   public byte[] unwrap(byte[] bytes, int off, int len) throws SaslException {
      return this.saslServer.unwrap(bytes, off, len);
   }

   public byte[] wrap(ByteBuffer source) throws SaslException {
      return SaslUtils.wrap(this.saslServer, source);
   }

   public byte[] unwrap(ByteBuffer source) throws SaslException {
      return SaslUtils.unwrap(this.saslServer, source);
   }
}
