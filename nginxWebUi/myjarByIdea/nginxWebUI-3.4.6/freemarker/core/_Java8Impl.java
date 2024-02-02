package freemarker.core;

import java.lang.reflect.Method;

public class _Java8Impl implements _Java8 {
   public static final _Java8 INSTANCE = new _Java8Impl();

   private _Java8Impl() {
   }

   public boolean isDefaultMethod(Method method) {
      return method.isDefault();
   }
}
