package cn.hutool.core.lang.mutable;

import cn.hutool.core.util.NumberUtil;

public class MutableFloat extends Number implements Comparable<MutableFloat>, Mutable<Number> {
   private static final long serialVersionUID = 1L;
   private float value;

   public MutableFloat() {
   }

   public MutableFloat(float value) {
      this.value = value;
   }

   public MutableFloat(Number value) {
      this(value.floatValue());
   }

   public MutableFloat(String value) throws NumberFormatException {
      this.value = Float.parseFloat(value);
   }

   public Float get() {
      return this.value;
   }

   public void set(float value) {
      this.value = value;
   }

   public void set(Number value) {
      this.value = value.floatValue();
   }

   public MutableFloat increment() {
      ++this.value;
      return this;
   }

   public MutableFloat decrement() {
      --this.value;
      return this;
   }

   public MutableFloat add(float operand) {
      this.value += operand;
      return this;
   }

   public MutableFloat add(Number operand) {
      this.value += operand.floatValue();
      return this;
   }

   public MutableFloat subtract(float operand) {
      this.value -= operand;
      return this;
   }

   public MutableFloat subtract(Number operand) {
      this.value -= operand.floatValue();
      return this;
   }

   public int intValue() {
      return (int)this.value;
   }

   public long longValue() {
      return (long)this.value;
   }

   public float floatValue() {
      return this.value;
   }

   public double doubleValue() {
      return (double)this.value;
   }

   public boolean equals(Object obj) {
      if (obj instanceof MutableFloat) {
         return Float.floatToIntBits(((MutableFloat)obj).value) == Float.floatToIntBits(this.value);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Float.floatToIntBits(this.value);
   }

   public int compareTo(MutableFloat other) {
      return NumberUtil.compare((double)this.value, (double)other.value);
   }

   public String toString() {
      return String.valueOf(this.value);
   }
}
