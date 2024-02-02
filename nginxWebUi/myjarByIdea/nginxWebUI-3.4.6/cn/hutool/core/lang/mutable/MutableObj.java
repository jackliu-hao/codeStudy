package cn.hutool.core.lang.mutable;

import java.io.Serializable;

public class MutableObj<T> implements Mutable<T>, Serializable {
   private static final long serialVersionUID = 1L;
   private T value;

   public static <T> MutableObj<T> of(T value) {
      return new MutableObj(value);
   }

   public MutableObj() {
   }

   public MutableObj(T value) {
      this.value = value;
   }

   public T get() {
      return this.value;
   }

   public void set(T value) {
      this.value = value;
   }

   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else if (this == obj) {
         return true;
      } else if (this.getClass() == obj.getClass()) {
         MutableObj<?> that = (MutableObj)obj;
         return this.value.equals(that.value);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.value == null ? 0 : this.value.hashCode();
   }

   public String toString() {
      return this.value == null ? "null" : this.value.toString();
   }
}
