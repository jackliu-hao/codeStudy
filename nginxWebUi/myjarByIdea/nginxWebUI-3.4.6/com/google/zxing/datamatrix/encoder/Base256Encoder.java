package com.google.zxing.datamatrix.encoder;

final class Base256Encoder implements Encoder {
   public int getEncodingMode() {
      return 5;
   }

   public void encode(EncoderContext context) {
      StringBuilder buffer;
      (buffer = new StringBuilder()).append('\u0000');

      int dataCount;
      while(context.hasMoreCharacters()) {
         dataCount = context.getCurrentChar();
         buffer.append((char)dataCount);
         ++context.pos;
         int newMode;
         if ((newMode = HighLevelEncoder.lookAheadTest(context.getMessage(), context.pos, this.getEncodingMode())) != this.getEncodingMode()) {
            context.signalEncoderChange(newMode);
            break;
         }
      }

      dataCount = buffer.length() - 1;
      int currentSize = context.getCodewordCount() + dataCount + 1;
      context.updateSymbolInfo(currentSize);
      boolean mustPad = context.getSymbolInfo().getDataCapacity() - currentSize > 0;
      if (context.hasMoreCharacters() || mustPad) {
         if (dataCount <= 249) {
            buffer.setCharAt(0, (char)dataCount);
         } else {
            if (dataCount > 1555) {
               throw new IllegalStateException("Message length not in valid ranges: " + dataCount);
            }

            buffer.setCharAt(0, (char)(dataCount / 250 + 249));
            buffer.insert(1, (char)(dataCount % 250));
         }
      }

      int i = 0;

      for(int c = buffer.length(); i < c; ++i) {
         context.writeCodeword(randomize255State(buffer.charAt(i), context.getCodewordCount() + 1));
      }

   }

   private static char randomize255State(char ch, int codewordPosition) {
      int pseudoRandom = codewordPosition * 149 % 255 + 1;
      int tempVariable;
      return (tempVariable = ch + pseudoRandom) <= 255 ? (char)tempVariable : (char)(tempVariable - 256);
   }
}
