package cn.hutool.log;

import cn.hutool.core.lang.caller.CallerUtil;
import cn.hutool.log.level.DebugLog;
import cn.hutool.log.level.ErrorLog;
import cn.hutool.log.level.InfoLog;
import cn.hutool.log.level.Level;
import cn.hutool.log.level.TraceLog;
import cn.hutool.log.level.WarnLog;

public interface Log extends TraceLog, DebugLog, InfoLog, WarnLog, ErrorLog {
   static Log get(Class<?> clazz) {
      return LogFactory.get(clazz);
   }

   static Log get(String name) {
      return LogFactory.get(name);
   }

   static Log get() {
      return LogFactory.get(CallerUtil.getCallerCaller());
   }

   String getName();

   boolean isEnabled(Level var1);

   void log(Level var1, String var2, Object... var3);

   void log(Level var1, Throwable var2, String var3, Object... var4);

   void log(String var1, Level var2, Throwable var3, String var4, Object... var5);
}
