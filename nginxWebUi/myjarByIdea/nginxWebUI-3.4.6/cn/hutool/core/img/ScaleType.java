package cn.hutool.core.img;

public enum ScaleType {
   DEFAULT(1),
   FAST(2),
   SMOOTH(4),
   REPLICATE(8),
   AREA_AVERAGING(16);

   private final int value;

   private ScaleType(int value) {
      this.value = value;
   }

   public int getValue() {
      return this.value;
   }
}
