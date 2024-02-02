package cn.hutool.core.lang.mutable;

import cn.hutool.core.util.NumberUtil;

public class MutableByte extends Number implements Comparable<MutableByte>, Mutable<Number> {
   private static final long serialVersionUID = 1L;
   private byte value;

   public MutableByte() {
   }

   public MutableByte(byte value) {
      this.value = value;
   }

   public MutableByte(Number value) {
      this(value.byteValue());
   }

   public MutableByte(String value) throws NumberFormatException {
      this.value = Byte.parseByte(value);
   }

   public Byte get() {
      return this.value;
   }

   public void set(byte value) {
      this.value = value;
   }

   public void set(Number value) {
      this.value = value.byteValue();
   }

   public MutableByte increment() {
      ++this.value;
      return this;
   }

   public MutableByte decrement() {
      --this.value;
      return this;
   }

   public MutableByte add(byte operand) {
      this.value += operand;
      return this;
   }

   public MutableByte add(Number operand) {
      this.value += operand.byteValue();
      return this;
   }

   public MutableByte subtract(byte operand) {
      this.value -= operand;
      return this;
   }

   public MutableByte subtract(Number operand) {
      this.value -= operand.byteValue();
      return this;
   }

   public byte byteValue() {
      return this.value;
   }

   public int intValue() {
      return this.value;
   }

   public long longValue() {
      return (long)this.value;
   }

   public float floatValue() {
      return (float)this.value;
   }

   public double doubleValue() {
      return (double)this.value;
   }

   public boolean equals(Object obj) {
      if (obj instanceof MutableByte) {
         return this.value == ((MutableByte)obj).byteValue();
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.value;
   }

   public int compareTo(MutableByte other) {
      return NumberUtil.compare(this.value, other.value);
   }

   public String toString() {
      return String.valueOf(this.value);
   }
}
