package com.google.zxing.datamatrix.encoder;

class C40Encoder implements Encoder {
   public int getEncodingMode() {
      return 1;
   }

   public void encode(EncoderContext context) {
      StringBuilder buffer = new StringBuilder();

      label47:
      while(context.hasMoreCharacters()) {
         char c = context.getCurrentChar();
         ++context.pos;
         int lastCharSize = this.encodeChar(c, buffer);
         int unwritten = buffer.length() / 3 << 1;
         int curCodewordCount = context.getCodewordCount() + unwritten;
         context.updateSymbolInfo(curCodewordCount);
         int available = context.getSymbolInfo().getDataCapacity() - curCodewordCount;
         if (!context.hasMoreCharacters()) {
            StringBuilder removed = new StringBuilder();
            if (buffer.length() % 3 == 2 && (available < 2 || available > 2)) {
               lastCharSize = this.backtrackOneCharacter(context, buffer, removed, lastCharSize);
            }

            while(true) {
               if (buffer.length() % 3 != 1 || (lastCharSize > 3 || available == 1) && lastCharSize <= 3) {
                  break label47;
               }

               lastCharSize = this.backtrackOneCharacter(context, buffer, removed, lastCharSize);
            }
         }

         int newMode;
         if (buffer.length() % 3 == 0 && (newMode = HighLevelEncoder.lookAheadTest(context.getMessage(), context.pos, this.getEncodingMode())) != this.getEncodingMode()) {
            context.signalEncoderChange(newMode);
            break;
         }
      }

      this.handleEOD(context, buffer);
   }

   private int backtrackOneCharacter(EncoderContext context, StringBuilder buffer, StringBuilder removed, int lastCharSize) {
      int count = buffer.length();
      buffer.delete(count - lastCharSize, count);
      --context.pos;
      char c = context.getCurrentChar();
      lastCharSize = this.encodeChar(c, removed);
      context.resetSymbolInfo();
      return lastCharSize;
   }

   static void writeNextTriplet(EncoderContext context, StringBuilder buffer) {
      context.writeCodewords(encodeToCodewords(buffer, 0));
      buffer.delete(0, 3);
   }

   void handleEOD(EncoderContext context, StringBuilder buffer) {
      int unwritten = buffer.length() / 3 << 1;
      int rest = buffer.length() % 3;
      int curCodewordCount = context.getCodewordCount() + unwritten;
      context.updateSymbolInfo(curCodewordCount);
      int available = context.getSymbolInfo().getDataCapacity() - curCodewordCount;
      if (rest == 2) {
         buffer.append('\u0000');

         while(buffer.length() >= 3) {
            writeNextTriplet(context, buffer);
         }

         if (context.hasMoreCharacters()) {
            context.writeCodeword('þ');
         }
      } else if (available == 1 && rest == 1) {
         while(buffer.length() >= 3) {
            writeNextTriplet(context, buffer);
         }

         if (context.hasMoreCharacters()) {
            context.writeCodeword('þ');
         }

         --context.pos;
      } else {
         if (rest != 0) {
            throw new IllegalStateException("Unexpected case. Please report!");
         }

         while(buffer.length() >= 3) {
            writeNextTriplet(context, buffer);
         }

         if (available > 0 || context.hasMoreCharacters()) {
            context.writeCodeword('þ');
         }
      }

      context.signalEncoderChange(0);
   }

   int encodeChar(char c, StringBuilder sb) {
      if (c == ' ') {
         sb.append('\u0003');
         return 1;
      } else if (c >= '0' && c <= '9') {
         sb.append((char)(c - 48 + 4));
         return 1;
      } else if (c >= 'A' && c <= 'Z') {
         sb.append((char)(c - 65 + 14));
         return 1;
      } else if (c >= 0 && c <= 31) {
         sb.append('\u0000');
         sb.append(c);
         return 2;
      } else if (c >= '!' && c <= '/') {
         sb.append('\u0001');
         sb.append((char)(c - 33));
         return 2;
      } else if (c >= ':' && c <= '@') {
         sb.append('\u0001');
         sb.append((char)(c - 58 + 15));
         return 2;
      } else if (c >= '[' && c <= '_') {
         sb.append('\u0001');
         sb.append((char)(c - 91 + 22));
         return 2;
      } else if (c >= '`' && c <= 127) {
         sb.append('\u0002');
         sb.append((char)(c - 96));
         return 2;
      } else if (c >= 128) {
         sb.append("\u0001\u001e");
         return 2 + this.encodeChar((char)(c - 128), sb);
      } else {
         throw new IllegalArgumentException("Illegal character: " + c);
      }
   }

   private static String encodeToCodewords(CharSequence sb, int startPos) {
      char c1 = sb.charAt(startPos);
      char c2 = sb.charAt(startPos + 1);
      char c3 = sb.charAt(startPos + 2);
      int v;
      char cw1 = (char)((v = c1 * 1600 + c2 * 40 + c3 + 1) / 256);
      char cw2 = (char)(v % 256);
      return new String(new char[]{cw1, cw2});
   }
}
