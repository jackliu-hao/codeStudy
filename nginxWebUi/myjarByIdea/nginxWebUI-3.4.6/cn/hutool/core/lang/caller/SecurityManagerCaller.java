package cn.hutool.core.lang.caller;

import cn.hutool.core.util.ArrayUtil;
import java.io.Serializable;

public class SecurityManagerCaller extends SecurityManager implements Caller, Serializable {
   private static final long serialVersionUID = 1L;
   private static final int OFFSET = 1;

   public Class<?> getCaller() {
      Class<?>[] context = this.getClassContext();
      return null != context && 2 < context.length ? context[2] : null;
   }

   public Class<?> getCallerCaller() {
      Class<?>[] context = this.getClassContext();
      return null != context && 3 < context.length ? context[3] : null;
   }

   public Class<?> getCaller(int depth) {
      Class<?>[] context = this.getClassContext();
      return null != context && 1 + depth < context.length ? context[1 + depth] : null;
   }

   public boolean isCalledBy(Class<?> clazz) {
      Class<?>[] classes = this.getClassContext();
      if (ArrayUtil.isNotEmpty((Object[])classes)) {
         Class[] var3 = classes;
         int var4 = classes.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Class<?> contextClass = var3[var5];
            if (contextClass.equals(clazz)) {
               return true;
            }
         }
      }

      return false;
   }
}
