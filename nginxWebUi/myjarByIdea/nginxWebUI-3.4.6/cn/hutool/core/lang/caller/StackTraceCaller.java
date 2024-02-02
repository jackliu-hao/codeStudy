package cn.hutool.core.lang.caller;

import cn.hutool.core.exceptions.UtilException;
import java.io.Serializable;

public class StackTraceCaller implements Caller, Serializable {
   private static final long serialVersionUID = 1L;
   private static final int OFFSET = 2;

   public Class<?> getCaller() {
      StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
      if (3 >= stackTrace.length) {
         return null;
      } else {
         String className = stackTrace[3].getClassName();

         try {
            return Class.forName(className);
         } catch (ClassNotFoundException var4) {
            throw new UtilException(var4, "[{}] not found!", new Object[]{className});
         }
      }
   }

   public Class<?> getCallerCaller() {
      StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
      if (4 >= stackTrace.length) {
         return null;
      } else {
         String className = stackTrace[4].getClassName();

         try {
            return Class.forName(className);
         } catch (ClassNotFoundException var4) {
            throw new UtilException(var4, "[{}] not found!", new Object[]{className});
         }
      }
   }

   public Class<?> getCaller(int depth) {
      StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
      if (2 + depth >= stackTrace.length) {
         return null;
      } else {
         String className = stackTrace[2 + depth].getClassName();

         try {
            return Class.forName(className);
         } catch (ClassNotFoundException var5) {
            throw new UtilException(var5, "[{}] not found!", new Object[]{className});
         }
      }
   }

   public boolean isCalledBy(Class<?> clazz) {
      StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
      StackTraceElement[] var3 = stackTrace;
      int var4 = stackTrace.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         StackTraceElement element = var3[var5];
         if (element.getClassName().equals(clazz.getName())) {
            return true;
         }
      }

      return false;
   }
}
