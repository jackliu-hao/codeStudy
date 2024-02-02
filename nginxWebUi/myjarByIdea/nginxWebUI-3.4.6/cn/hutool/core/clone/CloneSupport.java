package cn.hutool.core.clone;

public class CloneSupport<T> implements Cloneable<T> {
   public T clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new CloneRuntimeException(var2);
      }
   }
}
