package org.wildfly.common.codec;

public abstract class Base64Alphabet extends Alphabet {
   public static final Base64Alphabet STANDARD = new Base64Alphabet(false) {
      public int encode(int val) {
         if (val <= 25) {
            return 65 + val;
         } else if (val <= 51) {
            return 97 + val - 26;
         } else if (val <= 61) {
            return 48 + val - 52;
         } else if (val == 62) {
            return 43;
         } else {
            assert val == 63;

            return 47;
         }
      }

      public int decode(int codePoint) throws IllegalArgumentException {
         if (65 <= codePoint && codePoint <= 90) {
            return codePoint - 65;
         } else if (97 <= codePoint && codePoint <= 122) {
            return codePoint - 97 + 26;
         } else if (48 <= codePoint && codePoint <= 57) {
            return codePoint - 48 + 52;
         } else if (codePoint == 43) {
            return 62;
         } else {
            return codePoint == 47 ? 63 : -1;
         }
      }
   };

   protected Base64Alphabet(boolean littleEndian) {
      super(littleEndian);
   }

   public abstract int encode(int var1);

   public abstract int decode(int var1);
}
