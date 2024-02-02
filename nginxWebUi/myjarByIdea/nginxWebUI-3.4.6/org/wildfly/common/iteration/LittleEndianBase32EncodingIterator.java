package org.wildfly.common.iteration;

import org.wildfly.common.codec.Base32Alphabet;

final class LittleEndianBase32EncodingIterator extends Base32EncodingCodePointIterator {
   private final Base32Alphabet alphabet;

   LittleEndianBase32EncodingIterator(ByteIterator iter, boolean addPadding, Base32Alphabet alphabet) {
      super(iter, addPadding);
      this.alphabet = alphabet;
   }

   int calc0(int b0) {
      return this.alphabet.encode(b0 & 31);
   }

   int calc1(int b0, int b1) {
      return this.alphabet.encode((b1 << 3 | b0 >> 5) & 31);
   }

   int calc2(int b1) {
      return this.alphabet.encode(b1 >> 2 & 31);
   }

   int calc3(int b1, int b2) {
      return this.alphabet.encode((b2 << 1 | b1 >> 7) & 31);
   }

   int calc4(int b2, int b3) {
      return this.alphabet.encode((b3 << 4 | b2 >> 4) & 31);
   }

   int calc5(int b3) {
      return this.alphabet.encode(b3 >> 1 & 31);
   }

   int calc6(int b3, int b4) {
      return this.alphabet.encode((b4 << 2 | b3 >> 6) & 31);
   }

   int calc7(int b4) {
      return this.alphabet.encode(b4 >> 3 & 31);
   }
}
