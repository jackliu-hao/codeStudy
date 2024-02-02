package cn.hutool.core.lang.mutable;

import java.io.Serializable;

public class MutableBool implements Comparable<MutableBool>, Mutable<Boolean>, Serializable {
   private static final long serialVersionUID = 1L;
   private boolean value;

   public MutableBool() {
   }

   public MutableBool(boolean value) {
      this.value = value;
   }

   public MutableBool(String value) throws NumberFormatException {
      this.value = Boolean.parseBoolean(value);
   }

   public Boolean get() {
      return this.value;
   }

   public void set(boolean value) {
      this.value = value;
   }

   public void set(Boolean value) {
      this.value = value;
   }

   public boolean equals(Object obj) {
      if (obj instanceof MutableBool) {
         return this.value == ((MutableBool)obj).value;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.value ? Boolean.TRUE.hashCode() : Boolean.FALSE.hashCode();
   }

   public int compareTo(MutableBool other) {
      return Boolean.compare(this.value, other.value);
   }

   public String toString() {
      return String.valueOf(this.value);
   }
}
