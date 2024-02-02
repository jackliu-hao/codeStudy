package org.noear.solon.logging;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.noear.solon.core.util.PrintUtil;
import org.noear.solon.logging.appender.ConsoleAppender;
import org.noear.solon.logging.event.Appender;
import org.noear.solon.logging.event.LogEvent;

public class AppenderManager {
   private static AppenderManager instance;
   protected Map<String, AppenderHolder> appenderMap = new LinkedHashMap();

   public static AppenderManager getInstance() {
      if (instance == null) {
         Class var0 = AppenderManager.class;
         synchronized(AppenderManager.class) {
            if (instance == null) {
               instance = new AppenderManager();
            }
         }
      }

      return instance;
   }

   private AppenderManager() {
      this.register("console", new ConsoleAppender());
   }

   public void register(String name, Appender appender) {
      this.appenderMap.putIfAbsent(name, new AppenderHolder(name, appender));
      PrintUtil.info("Logging", "LogAppender registered from the " + appender.getClass().getTypeName() + "#" + name);
   }

   public AppenderHolder get(String name) {
      return (AppenderHolder)this.appenderMap.get(name);
   }

   public void append(LogEvent logEvent) {
      Iterator var2 = this.appenderMap.values().iterator();

      while(var2.hasNext()) {
         AppenderHolder appender = (AppenderHolder)var2.next();
         appender.append(logEvent);
      }

   }

   public void stop() {
      Iterator var1 = this.appenderMap.values().iterator();

      while(var1.hasNext()) {
         AppenderHolder appender = (AppenderHolder)var1.next();
         appender.stop();
      }

   }
}
