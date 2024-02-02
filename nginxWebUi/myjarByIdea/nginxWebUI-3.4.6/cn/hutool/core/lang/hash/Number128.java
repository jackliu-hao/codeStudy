package cn.hutool.core.lang.hash;

public class Number128 extends Number {
   private static final long serialVersionUID = 1L;
   private long lowValue;
   private long highValue;

   public Number128(long lowValue, long highValue) {
      this.lowValue = lowValue;
      this.highValue = highValue;
   }

   public long getLowValue() {
      return this.lowValue;
   }

   public void setLowValue(long lowValue) {
      this.lowValue = lowValue;
   }

   public long getHighValue() {
      return this.highValue;
   }

   public void setHighValue(long hiValue) {
      this.highValue = hiValue;
   }

   public long[] getLongArray() {
      return new long[]{this.lowValue, this.highValue};
   }

   public int intValue() {
      return (int)this.longValue();
   }

   public long longValue() {
      return this.lowValue;
   }

   public float floatValue() {
      return (float)this.longValue();
   }

   public double doubleValue() {
      return (double)this.longValue();
   }
}
