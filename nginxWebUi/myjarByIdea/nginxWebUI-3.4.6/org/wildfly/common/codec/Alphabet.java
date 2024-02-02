package org.wildfly.common.codec;

public abstract class Alphabet {
   private final boolean littleEndian;

   Alphabet(boolean littleEndian) {
      this.littleEndian = littleEndian;
   }

   public boolean isLittleEndian() {
      return this.littleEndian;
   }

   public abstract int encode(int var1);

   public abstract int decode(int var1);
}
