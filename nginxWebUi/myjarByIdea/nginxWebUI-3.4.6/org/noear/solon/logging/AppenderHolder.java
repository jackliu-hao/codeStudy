package org.noear.solon.logging;

import java.util.LinkedHashMap;
import java.util.Map;
import org.noear.solon.Solon;
import org.noear.solon.core.util.PrintUtil;
import org.noear.solon.logging.event.Appender;
import org.noear.solon.logging.event.Level;
import org.noear.solon.logging.event.LogEvent;

public final class AppenderHolder {
   Appender real;
   private String name;
   private boolean enable = true;
   private Level level;

   public AppenderHolder(String name, Appender real) {
      this.real = real;
      this.name = name;
      real.setName(name);
      real.start();
      if (Solon.app() != null) {
         String levelStr = Solon.cfg().get("solon.logging.appender." + this.getName() + ".level");
         this.setLevel(Level.of(levelStr, real.getDefaultLevel()));
         this.enable = Solon.cfg().getBool("solon.logging.appender." + this.getName() + ".enable", true);
         Map<String, Object> meta = new LinkedHashMap();
         meta.put("level", this.getLevel().name());
         meta.put("enable", this.enable);
         PrintUtil.info("Logging", this.getName() + " " + meta);
      } else {
         this.setLevel(real.getDefaultLevel());
      }

   }

   public String getName() {
      return this.name;
   }

   public boolean getEnable() {
      return this.enable;
   }

   public Level getLevel() {
      return this.level;
   }

   public void setLevel(Level level) {
      this.level = level;
   }

   public void append(LogEvent logEvent) {
      if (this.enable) {
         if (this.level.code <= logEvent.getLevel().code) {
            this.real.append(logEvent);
         }
      }
   }

   public void stop() {
      this.real.stop();
   }
}
