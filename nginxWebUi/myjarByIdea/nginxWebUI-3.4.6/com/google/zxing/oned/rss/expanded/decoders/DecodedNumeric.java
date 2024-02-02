package com.google.zxing.oned.rss.expanded.decoders;

import com.google.zxing.FormatException;

final class DecodedNumeric extends DecodedObject {
   private final int firstDigit;
   private final int secondDigit;
   static final int FNC1 = 10;

   DecodedNumeric(int newPosition, int firstDigit, int secondDigit) throws FormatException {
      super(newPosition);
      if (firstDigit >= 0 && firstDigit <= 10 && secondDigit >= 0 && secondDigit <= 10) {
         this.firstDigit = firstDigit;
         this.secondDigit = secondDigit;
      } else {
         throw FormatException.getFormatInstance();
      }
   }

   int getFirstDigit() {
      return this.firstDigit;
   }

   int getSecondDigit() {
      return this.secondDigit;
   }

   int getValue() {
      return this.firstDigit * 10 + this.secondDigit;
   }

   boolean isFirstDigitFNC1() {
      return this.firstDigit == 10;
   }

   boolean isSecondDigitFNC1() {
      return this.secondDigit == 10;
   }

   boolean isAnyFNC1() {
      return this.firstDigit == 10 || this.secondDigit == 10;
   }
}
