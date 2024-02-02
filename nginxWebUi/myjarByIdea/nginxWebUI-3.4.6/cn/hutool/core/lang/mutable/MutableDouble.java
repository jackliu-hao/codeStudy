package cn.hutool.core.lang.mutable;

import cn.hutool.core.util.NumberUtil;

public class MutableDouble extends Number implements Comparable<MutableDouble>, Mutable<Number> {
   private static final long serialVersionUID = 1L;
   private double value;

   public MutableDouble() {
   }

   public MutableDouble(double value) {
      this.value = value;
   }

   public MutableDouble(Number value) {
      this(value.doubleValue());
   }

   public MutableDouble(String value) throws NumberFormatException {
      this.value = Double.parseDouble(value);
   }

   public Double get() {
      return this.value;
   }

   public void set(double value) {
      this.value = value;
   }

   public void set(Number value) {
      this.value = value.doubleValue();
   }

   public MutableDouble increment() {
      ++this.value;
      return this;
   }

   public MutableDouble decrement() {
      --this.value;
      return this;
   }

   public MutableDouble add(double operand) {
      this.value += operand;
      return this;
   }

   public MutableDouble add(Number operand) {
      this.value += operand.doubleValue();
      return this;
   }

   public MutableDouble subtract(double operand) {
      this.value -= operand;
      return this;
   }

   public MutableDouble subtract(Number operand) {
      this.value -= operand.doubleValue();
      return this;
   }

   public int intValue() {
      return (int)this.value;
   }

   public long longValue() {
      return (long)this.value;
   }

   public float floatValue() {
      return (float)this.value;
   }

   public double doubleValue() {
      return this.value;
   }

   public boolean equals(Object obj) {
      if (obj instanceof MutableDouble) {
         return Double.doubleToLongBits(((MutableDouble)obj).value) == Double.doubleToLongBits(this.value);
      } else {
         return false;
      }
   }

   public int hashCode() {
      long bits = Double.doubleToLongBits(this.value);
      return (int)(bits ^ bits >>> 32);
   }

   public int compareTo(MutableDouble other) {
      return NumberUtil.compare(this.value, other.value);
   }

   public String toString() {
      return String.valueOf(this.value);
   }
}
