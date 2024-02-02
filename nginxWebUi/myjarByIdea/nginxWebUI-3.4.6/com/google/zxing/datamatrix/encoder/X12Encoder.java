package com.google.zxing.datamatrix.encoder;

final class X12Encoder extends C40Encoder {
   public int getEncodingMode() {
      return 3;
   }

   public void encode(EncoderContext context) {
      StringBuilder buffer = new StringBuilder();

      while(context.hasMoreCharacters()) {
         char c = context.getCurrentChar();
         ++context.pos;
         this.encodeChar(c, buffer);
         if (buffer.length() % 3 == 0) {
            writeNextTriplet(context, buffer);
            int newMode;
            if ((newMode = HighLevelEncoder.lookAheadTest(context.getMessage(), context.pos, this.getEncodingMode())) != this.getEncodingMode()) {
               context.signalEncoderChange(newMode);
               break;
            }
         }
      }

      this.handleEOD(context, buffer);
   }

   int encodeChar(char c, StringBuilder sb) {
      if (c == '\r') {
         sb.append('\u0000');
      } else if (c == '*') {
         sb.append('\u0001');
      } else if (c == '>') {
         sb.append('\u0002');
      } else if (c == ' ') {
         sb.append('\u0003');
      } else if (c >= '0' && c <= '9') {
         sb.append((char)(c - 48 + 4));
      } else if (c >= 'A' && c <= 'Z') {
         sb.append((char)(c - 65 + 14));
      } else {
         HighLevelEncoder.illegalCharacter(c);
      }

      return 1;
   }

   void handleEOD(EncoderContext context, StringBuilder buffer) {
      context.updateSymbolInfo();
      int available = context.getSymbolInfo().getDataCapacity() - context.getCodewordCount();
      int count = buffer.length();
      context.pos -= count;
      if (context.getRemainingCharacters() > 1 || available > 1 || context.getRemainingCharacters() != available) {
         context.writeCodeword('þ');
      }

      if (context.getNewEncoding() < 0) {
         context.signalEncoderChange(0);
      }

   }
}
