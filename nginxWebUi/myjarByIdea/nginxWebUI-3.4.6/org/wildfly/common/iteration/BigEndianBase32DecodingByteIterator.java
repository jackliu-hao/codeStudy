package org.wildfly.common.iteration;

import org.wildfly.common._private.CommonMessages;
import org.wildfly.common.codec.Base32Alphabet;

final class BigEndianBase32DecodingByteIterator extends Base32DecodingByteIterator {
   private final Base32Alphabet alphabet;

   BigEndianBase32DecodingByteIterator(CodePointIterator iter, boolean requirePadding, Base32Alphabet alphabet) {
      super(iter, requirePadding);
      this.alphabet = alphabet;
   }

   int calc0(int b0, int b1) {
      int d0 = this.alphabet.decode(b0);
      int d1 = this.alphabet.decode(b1);
      if (d0 != -1 && d1 != -1) {
         return (d0 << 3 | d1 >> 2) & 255;
      } else {
         throw CommonMessages.msg.invalidBase32Character();
      }
   }

   int calc1(int b1, int b2, int b3) {
      int d1 = this.alphabet.decode(b1);
      int d2 = this.alphabet.decode(b2);
      int d3 = this.alphabet.decode(b3);
      if (d1 != -1 && d2 != -1 && d3 != -1) {
         return (d1 << 6 | d2 << 1 | d3 >> 4) & 255;
      } else {
         throw CommonMessages.msg.invalidBase32Character();
      }
   }

   int calc2(int b3, int b4) {
      int d3 = this.alphabet.decode(b3);
      int d4 = this.alphabet.decode(b4);
      if (d3 != -1 && d4 != -1) {
         return (d3 << 4 | d4 >> 1) & 255;
      } else {
         throw CommonMessages.msg.invalidBase32Character();
      }
   }

   int calc3(int b4, int b5, int b6) {
      int d4 = this.alphabet.decode(b4);
      int d5 = this.alphabet.decode(b5);
      int d6 = this.alphabet.decode(b6);
      if (d4 != -1 && d5 != -1 && d6 != -1) {
         return (d4 << 7 | d5 << 2 | d6 >> 3) & 255;
      } else {
         throw CommonMessages.msg.invalidBase32Character();
      }
   }

   int calc4(int b6, int b7) {
      int d6 = this.alphabet.decode(b6);
      int d7 = this.alphabet.decode(b7);
      if (d6 != -1 && d7 != -1) {
         return (d6 << 5 | d7) & 255;
      } else {
         throw CommonMessages.msg.invalidBase32Character();
      }
   }
}
