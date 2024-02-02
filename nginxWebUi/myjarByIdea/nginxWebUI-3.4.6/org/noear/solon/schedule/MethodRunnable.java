package org.noear.solon.schedule;

import java.lang.reflect.Method;
import org.noear.solon.Utils;

public class MethodRunnable implements Runnable {
   private Object target;
   private Method method;

   public MethodRunnable(Object target, Method method) {
      this.target = target;
      this.method = method;
   }

   public void run() {
      try {
         this.method.invoke(this.target);
      } catch (Throwable var2) {
         Throwable e = Utils.throwableUnwrap(var2);
         throw new ScheduledException(e);
      }
   }
}
