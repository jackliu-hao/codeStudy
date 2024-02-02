package freemarker.ext.beans;

import java.lang.reflect.Method;

final class FastPropertyDescriptor {
   private final Method readMethod;
   private final Method indexedReadMethod;

   public FastPropertyDescriptor(Method readMethod, Method indexedReadMethod) {
      this.readMethod = readMethod;
      this.indexedReadMethod = indexedReadMethod;
   }

   public Method getReadMethod() {
      return this.readMethod;
   }

   public Method getIndexedReadMethod() {
      return this.indexedReadMethod;
   }
}
