package org.noear.solon.core.handle;

public class ContextUtil {
   public static final String contentTypeDef = "text/plain;charset=UTF-8";
   private static final ThreadLocal<Context> threadLocal = new InheritableThreadLocal();

   public static void currentSet(Context context) {
      threadLocal.set(context);
   }

   public static void currentRemove() {
      threadLocal.remove();
   }

   public static Context current() {
      return (Context)threadLocal.get();
   }
}
