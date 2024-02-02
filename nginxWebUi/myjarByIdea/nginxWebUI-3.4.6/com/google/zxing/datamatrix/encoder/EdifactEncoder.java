package com.google.zxing.datamatrix.encoder;

final class EdifactEncoder implements Encoder {
   public int getEncodingMode() {
      return 4;
   }

   public void encode(EncoderContext context) {
      StringBuilder buffer = new StringBuilder();

      while(context.hasMoreCharacters()) {
         encodeChar(context.getCurrentChar(), buffer);
         ++context.pos;
         if (buffer.length() >= 4) {
            context.writeCodewords(encodeToCodewords(buffer, 0));
            buffer.delete(0, 4);
            if (HighLevelEncoder.lookAheadTest(context.getMessage(), context.pos, this.getEncodingMode()) != this.getEncodingMode()) {
               context.signalEncoderChange(0);
               break;
            }
         }
      }

      buffer.append('\u001f');
      handleEOD(context, buffer);
   }

   private static void handleEOD(EncoderContext context, CharSequence buffer) {
      try {
         int count;
         if ((count = buffer.length()) != 0) {
            int restChars;
            if (count == 1) {
               context.updateSymbolInfo();
               restChars = context.getSymbolInfo().getDataCapacity() - context.getCodewordCount();
               if (context.getRemainingCharacters() == 0 && restChars <= 2) {
                  return;
               }
            }

            if (count > 4) {
               throw new IllegalStateException("Count must not exceed 4");
            } else {
               restChars = count - 1;
               String encoded = encodeToCodewords(buffer, 0);
               boolean restInAscii = !context.hasMoreCharacters() && restChars <= 2;
               if (restChars <= 2) {
                  context.updateSymbolInfo(context.getCodewordCount() + restChars);
                  if (context.getSymbolInfo().getDataCapacity() - context.getCodewordCount() >= 3) {
                     restInAscii = false;
                     context.updateSymbolInfo(context.getCodewordCount() + encoded.length());
                  }
               }

               if (restInAscii) {
                  context.resetSymbolInfo();
                  context.pos -= restChars;
               } else {
                  context.writeCodewords(encoded);
               }

            }
         }
      } finally {
         context.signalEncoderChange(0);
      }
   }

   private static void encodeChar(char c, StringBuilder sb) {
      if (c >= ' ' && c <= '?') {
         sb.append(c);
      } else if (c >= '@' && c <= '^') {
         sb.append((char)(c - 64));
      } else {
         HighLevelEncoder.illegalCharacter(c);
      }
   }

   private static String encodeToCodewords(CharSequence sb, int startPos) {
      int len;
      if ((len = sb.length() - startPos) == 0) {
         throw new IllegalStateException("StringBuilder must not be empty");
      } else {
         char c1 = sb.charAt(startPos);
         char c2 = len >= 2 ? sb.charAt(startPos + 1) : 0;
         char c3 = len >= 3 ? sb.charAt(startPos + 2) : 0;
         char c4 = len >= 4 ? sb.charAt(startPos + 3) : 0;
         int v;
         char cw1 = (char)((v = (c1 << 18) + (c2 << 12) + (c3 << 6) + c4) >> 16 & 255);
         char cw2 = (char)(v >> 8 & 255);
         char cw3 = (char)(v & 255);
         StringBuilder res;
         (res = new StringBuilder(3)).append(cw1);
         if (len >= 2) {
            res.append(cw2);
         }

         if (len >= 3) {
            res.append(cw3);
         }

         return res.toString();
      }
   }
}
