package org.wildfly.common.archive;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

final class JDKSpecific {
   private JDKSpecific() {
   }

   static ByteBuffer inflate(Inflater inflater, ByteBuffer[] bufs, long offset, int compSize, int uncompSize) throws DataFormatException, IOException {
      int cnt = 0;
      byte[] b = new byte[Math.min(16384, compSize)];
      byte[] out = new byte[uncompSize];
      int op = 0;

      while(cnt < compSize) {
         int rem = compSize - cnt;
         int acnt = Math.min(rem, b.length);
         Archive.readBytes(bufs, offset, b, 0, acnt);
         cnt += acnt;
         inflater.setInput(b, 0, acnt);

         while(true) {
            op += inflater.inflate(out, op, uncompSize - op);
            if (inflater.needsInput()) {
               break;
            }
         }
      }

      if (!inflater.finished()) {
         throw new IOException("Corrupted compression stream");
      } else {
         return ByteBuffer.wrap(out);
      }
   }
}
