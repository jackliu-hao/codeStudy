package com.google.zxing.oned.rss.expanded;

import com.google.zxing.common.BitArray;
import java.util.List;

final class BitArrayBuilder {
   private BitArrayBuilder() {
   }

   static BitArray buildBitArray(List<ExpandedPair> pairs) {
      int charNumber = (pairs.size() << 1) - 1;
      if (((ExpandedPair)pairs.get(pairs.size() - 1)).getRightChar() == null) {
         --charNumber;
      }

      int size = charNumber * 12;
      BitArray binary = new BitArray(size);
      int accPos = 0;
      int firstValue = ((ExpandedPair)pairs.get(0)).getRightChar().getValue();

      int i;
      for(i = 11; i >= 0; --i) {
         if ((firstValue & 1 << i) != 0) {
            binary.set(accPos);
         }

         ++accPos;
      }

      for(i = 1; i < pairs.size(); ++i) {
         ExpandedPair currentPair;
         int leftValue = (currentPair = (ExpandedPair)pairs.get(i)).getLeftChar().getValue();

         int rightValue;
         for(rightValue = 11; rightValue >= 0; --rightValue) {
            if ((leftValue & 1 << rightValue) != 0) {
               binary.set(accPos);
            }

            ++accPos;
         }

         if (currentPair.getRightChar() != null) {
            rightValue = currentPair.getRightChar().getValue();

            for(int j = 11; j >= 0; --j) {
               if ((rightValue & 1 << j) != 0) {
                  binary.set(accPos);
               }

               ++accPos;
            }
         }
      }

      return binary;
   }
}
