package org.noear.solon.logging.integration;

import java.util.Properties;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.logging.AppenderManager;
import org.noear.solon.logging.LogOptions;
import org.noear.solon.logging.event.Appender;
import org.slf4j.MDC;

public class XPluginImp implements Plugin {
   public void start(AopContext context) {
      this.loadAppenderConfig();
   }

   private void loadAppenderConfig() {
      Properties props = Solon.cfg().getProp("solon.logging.appender");
      AppenderManager.getInstance();
      if (props.size() > 0) {
         props.forEach((k, v) -> {
            String key = (String)k;
            String val = (String)v;
            if (key.endsWith(".class")) {
               Appender appender = (Appender)Utils.newInstance(val);
               if (appender != null) {
                  String name = key.substring(0, key.length() - 6);
                  AppenderManager.getInstance().register(name, appender);
               }
            }

         });
      }

      LogOptions.getLoggerLevelInit();
      Solon.app().filter(-9, (ctx, chain) -> {
         MDC.clear();
         chain.doFilter(ctx);
      });
   }

   public void stop() throws Throwable {
      AppenderManager.getInstance().stop();
   }
}
