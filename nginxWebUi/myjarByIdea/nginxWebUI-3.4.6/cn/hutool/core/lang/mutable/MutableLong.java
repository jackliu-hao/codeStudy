package cn.hutool.core.lang.mutable;

import cn.hutool.core.util.NumberUtil;

public class MutableLong extends Number implements Comparable<MutableLong>, Mutable<Number> {
   private static final long serialVersionUID = 1L;
   private long value;

   public MutableLong() {
   }

   public MutableLong(long value) {
      this.value = value;
   }

   public MutableLong(Number value) {
      this(value.longValue());
   }

   public MutableLong(String value) throws NumberFormatException {
      this.value = Long.parseLong(value);
   }

   public Long get() {
      return this.value;
   }

   public void set(long value) {
      this.value = value;
   }

   public void set(Number value) {
      this.value = value.longValue();
   }

   public MutableLong increment() {
      ++this.value;
      return this;
   }

   public MutableLong decrement() {
      --this.value;
      return this;
   }

   public MutableLong add(long operand) {
      this.value += operand;
      return this;
   }

   public MutableLong add(Number operand) {
      this.value += operand.longValue();
      return this;
   }

   public MutableLong subtract(long operand) {
      this.value -= operand;
      return this;
   }

   public MutableLong subtract(Number operand) {
      this.value -= operand.longValue();
      return this;
   }

   public int intValue() {
      return (int)this.value;
   }

   public long longValue() {
      return this.value;
   }

   public float floatValue() {
      return (float)this.value;
   }

   public double doubleValue() {
      return (double)this.value;
   }

   public boolean equals(Object obj) {
      if (obj instanceof MutableLong) {
         return this.value == ((MutableLong)obj).longValue();
      } else {
         return false;
      }
   }

   public int hashCode() {
      return (int)(this.value ^ this.value >>> 32);
   }

   public int compareTo(MutableLong other) {
      return NumberUtil.compare(this.value, other.value);
   }

   public String toString() {
      return String.valueOf(this.value);
   }
}
