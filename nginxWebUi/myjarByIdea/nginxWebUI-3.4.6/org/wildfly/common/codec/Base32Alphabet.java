package org.wildfly.common.codec;

public abstract class Base32Alphabet extends Alphabet {
   public static final Base32Alphabet STANDARD = new Base32Alphabet(false) {
      public int encode(int val) {
         if (val <= 25) {
            return 65 + val;
         } else {
            assert val < 32;

            return 50 + val - 26;
         }
      }

      public int decode(int codePoint) {
         if (65 <= codePoint && codePoint <= 90) {
            return codePoint - 65;
         } else {
            return 50 <= codePoint && codePoint <= 55 ? codePoint - 50 + 26 : -1;
         }
      }
   };
   public static final Base32Alphabet LOWERCASE = new Base32Alphabet(false) {
      public int encode(int val) {
         if (val <= 25) {
            return 97 + val;
         } else {
            assert val < 32;

            return 50 + val - 26;
         }
      }

      public int decode(int codePoint) {
         if (97 <= codePoint && codePoint <= 122) {
            return codePoint - 97;
         } else {
            return 50 <= codePoint && codePoint <= 55 ? codePoint - 50 + 26 : -1;
         }
      }
   };

   protected Base32Alphabet(boolean littleEndian) {
      super(littleEndian);
   }

   public abstract int encode(int var1);

   public abstract int decode(int var1);
}
