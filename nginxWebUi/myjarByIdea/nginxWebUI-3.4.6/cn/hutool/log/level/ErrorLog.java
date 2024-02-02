package cn.hutool.log.level;

public interface ErrorLog {
   boolean isErrorEnabled();

   void error(Throwable var1);

   void error(String var1, Object... var2);

   void error(Throwable var1, String var2, Object... var3);

   void error(String var1, Throwable var2, String var3, Object... var4);
}
