package cn.hutool.log.level;

public interface WarnLog {
   boolean isWarnEnabled();

   void warn(Throwable var1);

   void warn(String var1, Object... var2);

   void warn(Throwable var1, String var2, Object... var3);

   void warn(String var1, Throwable var2, String var3, Object... var4);
}
