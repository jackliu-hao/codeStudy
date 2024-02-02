package org.wildfly.common.iteration;

import org.wildfly.common.codec.Base64Alphabet;

final class LittleEndianBase64EncodingIterator extends Base64EncodingIterator {
   private final Base64Alphabet alphabet;

   LittleEndianBase64EncodingIterator(ByteIterator iter, boolean addPadding, Base64Alphabet alphabet) {
      super(iter, addPadding);
      this.alphabet = alphabet;
   }

   int calc0(int b0) {
      return this.alphabet.encode(b0 & 63);
   }

   int calc1(int b0, int b1) {
      return this.alphabet.encode((b1 << 2 | b0 >> 6) & 63);
   }

   int calc2(int b1, int b2) {
      return this.alphabet.encode((b2 << 4 | b1 >> 4) & 63);
   }

   int calc3(int b2) {
      return this.alphabet.encode(b2 >> 2 & 63);
   }
}
