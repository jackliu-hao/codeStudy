package com.sun.mail.iap;

import com.sun.mail.util.ASCIIUtility;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ResponseInputStream {
   private static final int minIncrement = 256;
   private static final int maxIncrement = 262144;
   private static final int incrementSlop = 16;
   private BufferedInputStream bin;

   public ResponseInputStream(InputStream in) {
      this.bin = new BufferedInputStream(in, 2048);
   }

   public ByteArray readResponse() throws IOException {
      return this.readResponse((ByteArray)null);
   }

   public ByteArray readResponse(ByteArray ba) throws IOException {
      if (ba == null) {
         ba = new ByteArray(new byte[128], 0, 128);
      }

      byte[] buffer = ba.getBytes();
      int idx = 0;

      while(true) {
         int b = 0;
         boolean gotCRLF = false;

         int i;
         while(!gotCRLF && (b = this.bin.read()) != -1) {
            switch (b) {
               case 10:
                  if (idx > 0 && buffer[idx - 1] == 13) {
                     gotCRLF = true;
                  }
               default:
                  if (idx >= buffer.length) {
                     i = buffer.length;
                     if (i > 262144) {
                        i = 262144;
                     }

                     ba.grow(i);
                     buffer = ba.getBytes();
                  }

                  buffer[idx++] = (byte)b;
            }
         }

         if (b == -1) {
            throw new IOException("Connection dropped by server?");
         }

         if (idx < 5 || buffer[idx - 3] != 125) {
            break;
         }

         for(i = idx - 4; i >= 0 && buffer[i] != 123; --i) {
         }

         if (i < 0) {
            break;
         }

         int count = false;

         int count;
         try {
            count = ASCIIUtility.parseInt(buffer, i + 1, idx - 3);
         } catch (NumberFormatException var10) {
            break;
         }

         if (count > 0) {
            int avail = buffer.length - idx;
            if (count + 16 > avail) {
               ba.grow(256 > count + 16 - avail ? 256 : count + 16 - avail);
               buffer = ba.getBytes();
            }

            while(count > 0) {
               int actual = this.bin.read(buffer, idx, count);
               count -= actual;
               idx += actual;
            }
         }
      }

      ba.setCount(idx);
      return ba;
   }
}
