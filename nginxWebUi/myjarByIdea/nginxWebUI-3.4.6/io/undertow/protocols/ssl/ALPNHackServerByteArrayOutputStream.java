package io.undertow.protocols.ssl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;

class ALPNHackServerByteArrayOutputStream extends ByteArrayOutputStream {
   private final SSLEngine sslEngine;
   private byte[] serverHello;
   private final String alpnProtocol;
   private boolean ready = false;

   ALPNHackServerByteArrayOutputStream(SSLEngine sslEngine, byte[] bytes, String alpnProtocol) {
      this.sslEngine = sslEngine;
      this.alpnProtocol = alpnProtocol;

      try {
         this.write(bytes);
      } catch (IOException var5) {
         throw new RuntimeException(var5);
      }

      this.ready = true;
   }

   public void write(byte[] b, int off, int len) {
      if (this.ready && b[off] == 2) {
         this.ready = false;
         this.serverHello = new byte[len];
         System.arraycopy(b, off, this.serverHello, 0, len);

         try {
            this.serverHello = ALPNHackServerHelloExplorer.addAlpnExtensionsToServerHello(this.serverHello, this.alpnProtocol);
         } catch (SSLException var5) {
            throw new RuntimeException(var5);
         }

         ALPNHackSSLEngine.regenerateHashes(this.sslEngine, this, this.toByteArray(), this.serverHello);
      } else {
         super.write(b, off, len);
      }
   }

   byte[] getServerHello() {
      return this.serverHello;
   }
}
