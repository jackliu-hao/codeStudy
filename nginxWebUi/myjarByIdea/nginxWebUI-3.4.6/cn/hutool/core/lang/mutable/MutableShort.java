package cn.hutool.core.lang.mutable;

import cn.hutool.core.util.NumberUtil;

public class MutableShort extends Number implements Comparable<MutableShort>, Mutable<Number> {
   private static final long serialVersionUID = 1L;
   private short value;

   public MutableShort() {
   }

   public MutableShort(short value) {
      this.value = value;
   }

   public MutableShort(Number value) {
      this(value.shortValue());
   }

   public MutableShort(String value) throws NumberFormatException {
      this.value = Short.parseShort(value);
   }

   public Short get() {
      return this.value;
   }

   public void set(short value) {
      this.value = value;
   }

   public void set(Number value) {
      this.value = value.shortValue();
   }

   public MutableShort increment() {
      ++this.value;
      return this;
   }

   public MutableShort decrement() {
      --this.value;
      return this;
   }

   public MutableShort add(short operand) {
      this.value += operand;
      return this;
   }

   public MutableShort add(Number operand) {
      this.value += operand.shortValue();
      return this;
   }

   public MutableShort subtract(short operand) {
      this.value -= operand;
      return this;
   }

   public MutableShort subtract(Number operand) {
      this.value -= operand.shortValue();
      return this;
   }

   public short shortValue() {
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
      if (obj instanceof MutableShort) {
         return this.value == ((MutableShort)obj).shortValue();
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.value;
   }

   public int compareTo(MutableShort other) {
      return NumberUtil.compare(this.value, other.value);
   }

   public String toString() {
      return String.valueOf(this.value);
   }
}
