package com.google.zxing.oned.rss.expanded.decoders;

import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitArray;

final class AI013x0x1xDecoder extends AI01weightDecoder {
   private static final int HEADER_SIZE = 8;
   private static final int WEIGHT_SIZE = 20;
   private static final int DATE_SIZE = 16;
   private final String dateCode;
   private final String firstAIdigits;

   AI013x0x1xDecoder(BitArray information, String firstAIdigits, String dateCode) {
      super(information);
      this.dateCode = dateCode;
      this.firstAIdigits = firstAIdigits;
   }

   public String parseInformation() throws NotFoundException {
      if (this.getInformation().getSize() != 84) {
         throw NotFoundException.getNotFoundInstance();
      } else {
         StringBuilder buf = new StringBuilder();
         this.encodeCompressedGtin(buf, 8);
         this.encodeCompressedWeight(buf, 48, 20);
         this.encodeCompressedDate(buf, 68);
         return buf.toString();
      }
   }

   private void encodeCompressedDate(StringBuilder buf, int currentPos) {
      int numericDate;
      if ((numericDate = this.getGeneralDecoder().extractNumericValueFromBitArray(currentPos, 16)) != 38400) {
         buf.append('(');
         buf.append(this.dateCode);
         buf.append(')');
         int day = numericDate % 32;
         int month = (numericDate /= 32) % 12 + 1;
         if ((numericDate /= 12) / 10 == 0) {
            buf.append('0');
         }

         buf.append(numericDate);
         if (month / 10 == 0) {
            buf.append('0');
         }

         buf.append(month);
         if (day / 10 == 0) {
            buf.append('0');
         }

         buf.append(day);
      }
   }

   protected void addWeightCode(StringBuilder buf, int weight) {
      buf.append('(');
      buf.append(this.firstAIdigits);
      buf.append(weight / 100000);
      buf.append(')');
   }

   protected int checkWeight(int weight) {
      return weight % 100000;
   }
}
