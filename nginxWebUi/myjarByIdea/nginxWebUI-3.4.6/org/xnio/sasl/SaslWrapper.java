package org.xnio.sasl;

import java.nio.ByteBuffer;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;

public abstract class SaslWrapper {
   public abstract byte[] wrap(byte[] var1, int var2, int var3) throws SaslException;

   public final byte[] wrap(byte[] bytes) throws SaslException {
      return this.unwrap(bytes, 0, bytes.length);
   }

   public abstract byte[] wrap(ByteBuffer var1) throws SaslException;

   public abstract byte[] unwrap(byte[] var1, int var2, int var3) throws SaslException;

   public final byte[] unwrap(byte[] bytes) throws SaslException {
      return this.unwrap(bytes, 0, bytes.length);
   }

   public abstract byte[] unwrap(ByteBuffer var1) throws SaslException;

   public final void wrap(ByteBuffer destination, ByteBuffer source) throws SaslException {
      destination.put(this.wrap(source));
   }

   public final void unwrap(ByteBuffer destination, ByteBuffer source) throws SaslException {
      destination.put(this.wrap(source));
   }

   public static SaslWrapper create(SaslClient saslClient) {
      return new SaslClientWrapper(saslClient);
   }

   public static SaslWrapper create(SaslServer saslServer) {
      return new SaslServerWrapper(saslServer);
   }
}
