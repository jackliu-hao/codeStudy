package io.undertow.protocols.ssl;

import java.io.ByteArrayOutputStream;
import javax.net.ssl.SSLEngine;

class ALPNHackClientByteArrayOutputStream extends ByteArrayOutputStream {
   private final SSLEngine sslEngine;
   private boolean ready = true;
   private byte[] receivedServerHello;
   private byte[] sentClientHello;

   ALPNHackClientByteArrayOutputStream(SSLEngine sslEngine) {
      this.sslEngine = sslEngine;
   }

   public void write(byte[] b, int off, int len) {
      if (this.ready && b[off] == 2) {
         this.ready = false;
         byte[] newData;
         if (this.receivedServerHello != null) {
            int b1 = b[off + 1];
            int b2 = b[off + 2];
            int b3 = b[off + 3];
            int length = (b1 & 255) << 16 | (b2 & 255) << 8 | b3 & 255;
            if (length + 4 == len) {
               newData = this.receivedServerHello;
            } else {
               newData = new byte[this.receivedServerHello.length + len - 4 - length];
               System.arraycopy(this.receivedServerHello, 0, newData, 0, this.receivedServerHello.length);
               System.arraycopy(b, length + 4, newData, this.receivedServerHello.length, len - 4 - length);
            }
         } else {
            newData = new byte[len];
            System.arraycopy(b, off, newData, 0, len);
         }

         ALPNHackSSLEngine.regenerateHashes(this.sslEngine, this, this.sentClientHello, newData);
      } else {
         super.write(b, off, len);
      }
   }

   byte[] getSentClientHello() {
      return this.sentClientHello;
   }

   void setSentClientHello(byte[] sentClientHello) {
      this.sentClientHello = sentClientHello;
   }

   byte[] getReceivedServerHello() {
      return this.receivedServerHello;
   }

   void setReceivedServerHello(byte[] receivedServerHello) {
      this.receivedServerHello = receivedServerHello;
   }
}
