package com.sun.mail.util;

import java.io.IOException;
import java.io.InputStream;

public class QDecoderStream extends QPDecoderStream {
   public QDecoderStream(InputStream in) {
      super(in);
   }

   public int read() throws IOException {
      int c = this.in.read();
      if (c == 95) {
         return 32;
      } else if (c == 61) {
         this.ba[0] = (byte)this.in.read();
         this.ba[1] = (byte)this.in.read();

         try {
            return ASCIIUtility.parseInt(this.ba, 0, 2, 16);
         } catch (NumberFormatException var3) {
            throw new DecodingException("QDecoder: Error in QP stream " + var3.getMessage());
         }
      } else {
         return c;
      }
   }
}
