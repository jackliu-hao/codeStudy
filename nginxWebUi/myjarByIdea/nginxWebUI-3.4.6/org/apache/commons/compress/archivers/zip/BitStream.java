package org.apache.commons.compress.archivers.zip;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import org.apache.commons.compress.utils.BitInputStream;

class BitStream extends BitInputStream {
   BitStream(InputStream in) {
      super(in, ByteOrder.LITTLE_ENDIAN);
   }

   int nextBit() throws IOException {
      return (int)this.readBits(1);
   }

   long nextBits(int n) throws IOException {
      if (n >= 0 && n <= 8) {
         return this.readBits(n);
      } else {
         throw new IOException("Trying to read " + n + " bits, at most 8 are allowed");
      }
   }

   int nextByte() throws IOException {
      return (int)this.readBits(8);
   }
}
