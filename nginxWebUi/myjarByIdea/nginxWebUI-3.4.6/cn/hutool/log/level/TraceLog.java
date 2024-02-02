package cn.hutool.log.level;

public interface TraceLog {
   boolean isTraceEnabled();

   void trace(Throwable var1);

   void trace(String var1, Object... var2);

   void trace(Throwable var1, String var2, Object... var3);

   void trace(String var1, Throwable var2, String var3, Object... var4);
}
