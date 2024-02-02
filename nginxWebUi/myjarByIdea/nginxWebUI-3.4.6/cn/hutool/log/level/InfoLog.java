package cn.hutool.log.level;

public interface InfoLog {
   boolean isInfoEnabled();

   void info(Throwable var1);

   void info(String var1, Object... var2);

   void info(Throwable var1, String var2, Object... var3);

   void info(String var1, Throwable var2, String var3, Object... var4);
}
