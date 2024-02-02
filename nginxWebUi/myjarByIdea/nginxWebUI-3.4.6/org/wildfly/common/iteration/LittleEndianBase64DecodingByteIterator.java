package org.wildfly.common.iteration;

import org.wildfly.common._private.CommonMessages;
import org.wildfly.common.codec.Base64Alphabet;

final class LittleEndianBase64DecodingByteIterator extends Base64DecodingByteIterator {
   private final Base64Alphabet alphabet;

   LittleEndianBase64DecodingByteIterator(CodePointIterator iter, boolean requirePadding, Base64Alphabet alphabet) {
      super(iter, requirePadding);
      this.alphabet = alphabet;
   }

   int calc0(int b0, int b1) {
      int d0 = this.alphabet.decode(b0);
      int d1 = this.alphabet.decode(b1);
      if (d0 != -1 && d1 != -1) {
         return (d0 | d1 << 6) & 255;
      } else {
         throw CommonMessages.msg.invalidBase64Character();
      }
   }

   int calc1(int b1, int b2) {
      int d1 = this.alphabet.decode(b1);
      int d2 = this.alphabet.decode(b2);
      if (d1 != -1 && d2 != -1) {
         return (d1 >> 2 | d2 << 4) & 255;
      } else {
         throw CommonMessages.msg.invalidBase64Character();
      }
   }

   int calc2(int b2, int b3) {
      int d2 = this.alphabet.decode(b2);
      int d3 = this.alphabet.decode(b3);
      if (d2 != -1 && d3 != -1) {
         return (d2 >> 4 | d3 << 2) & 255;
      } else {
         throw CommonMessages.msg.invalidBase64Character();
      }
   }
}
