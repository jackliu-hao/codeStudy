package cn.hutool.core.lang.mutable;

import cn.hutool.core.util.NumberUtil;

public class MutableInt extends Number implements Comparable<MutableInt>, Mutable<Number> {
   private static final long serialVersionUID = 1L;
   private int value;

   public MutableInt() {
   }

   public MutableInt(int value) {
      this.value = value;
   }

   public MutableInt(Number value) {
      this(value.intValue());
   }

   public MutableInt(String value) throws NumberFormatException {
      this.value = Integer.parseInt(value);
   }

   public Integer get() {
      return this.value;
   }

   public void set(int value) {
      this.value = value;
   }

   public void set(Number value) {
      this.value = value.intValue();
   }

   public MutableInt increment() {
      ++this.value;
      return this;
   }

   public MutableInt decrement() {
      --this.value;
      return this;
   }

   public MutableInt add(int operand) {
      this.value += operand;
      return this;
   }

   public MutableInt add(Number operand) {
      this.value += operand.intValue();
      return this;
   }

   public MutableInt subtract(int operand) {
      this.value -= operand;
      return this;
   }

   public MutableInt subtract(Number operand) {
      this.value -= operand.intValue();
      return this;
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
      if (obj instanceof MutableInt) {
         return this.value == ((MutableInt)obj).intValue();
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.value;
   }

   public int compareTo(MutableInt other) {
      return NumberUtil.compare(this.value, other.value);
   }

   public String toString() {
      return String.valueOf(this.value);
   }
}
