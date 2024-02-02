package cn.hutool.crypto;

public enum CipherMode {
   encrypt(1),
   decrypt(2),
   wrap(3),
   unwrap(4);

   private final int value;

   private CipherMode(int value) {
      this.value = value;
   }

   public int getValue() {
      return this.value;
   }
}
