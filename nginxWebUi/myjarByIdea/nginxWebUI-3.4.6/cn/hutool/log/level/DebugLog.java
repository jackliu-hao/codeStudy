package cn.hutool.log.level;

public interface DebugLog {
   boolean isDebugEnabled();

   void debug(Throwable var1);

   void debug(String var1, Object... var2);

   void debug(Throwable var1, String var2, Object... var3);

   void debug(String var1, Throwable var2, String var3, Object... var4);
}
