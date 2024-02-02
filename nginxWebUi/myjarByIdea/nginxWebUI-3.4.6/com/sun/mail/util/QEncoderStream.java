package com.sun.mail.util;

import java.io.IOException;
import java.io.OutputStream;

public class QEncoderStream extends QPEncoderStream {
   private String specials;
   private static String WORD_SPECIALS = "=_?\"#$%&'(),.:;<>@[\\]^`{|}~";
   private static String TEXT_SPECIALS = "=_?";

   public QEncoderStream(OutputStream out, boolean encodingWord) {
      super(out, Integer.MAX_VALUE);
      this.specials = encodingWord ? WORD_SPECIALS : TEXT_SPECIALS;
   }

   public void write(int c) throws IOException {
      c &= 255;
      if (c == 32) {
         this.output(95, false);
      } else if (c >= 32 && c < 127 && this.specials.indexOf(c) < 0) {
         this.output(c, false);
      } else {
         this.output(c, true);
      }

   }

   public static int encodedLength(byte[] b, boolean encodingWord) {
      int len = 0;
      String specials = encodingWord ? WORD_SPECIALS : TEXT_SPECIALS;

      for(int i = 0; i < b.length; ++i) {
         int c = b[i] & 255;
         if (c >= 32 && c < 127 && specials.indexOf(c) < 0) {
            ++len;
         } else {
            len += 3;
         }
      }

      return len;
   }
}
