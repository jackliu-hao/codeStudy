package org.noear.solon.logging;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.logging.event.Level;
import org.noear.solon.logging.model.LoggerLevelEntity;

public class LogOptions {
   private static volatile Map<String, LoggerLevelEntity> loggerLevelMap = new LinkedHashMap();
   private static volatile boolean loggerLevelMapInited = false;
   private static volatile Level rootLevel;

   public static void addLoggerLevel(String loggerExpr, Level level) {
      if (loggerExpr.endsWith(".*")) {
         loggerExpr = loggerExpr.substring(0, loggerExpr.length() - 1);
      }

      if (!loggerLevelMap.containsKey(loggerExpr)) {
         loggerLevelMap.put(loggerExpr, new LoggerLevelEntity(loggerExpr, level));
      }

   }

   public static Collection<LoggerLevelEntity> getLoggerLevels() {
      if (!loggerLevelMapInited) {
         loggerLevelMapInit();
      }

      return loggerLevelMap.values();
   }

   public static void getLoggerLevelInit() {
      if (!loggerLevelMapInited) {
         loggerLevelMapInit();
      }

   }

   public static Level getLoggerLevel(String logger) {
      if (!loggerLevelMapInited) {
         loggerLevelMapInit();
      }

      if (Utils.isEmpty(logger)) {
         return getRootLevel();
      } else {
         Iterator var1 = loggerLevelMap.values().iterator();

         LoggerLevelEntity l;
         do {
            if (!var1.hasNext()) {
               return getRootLevel();
            }

            l = (LoggerLevelEntity)var1.next();
         } while(!logger.startsWith(l.getLoggerExpr()));

         return l.getLevel();
      }
   }

   public static Level getRootLevel() {
      return rootLevel;
   }

   private static synchronized void loggerLevelMapInit() {
      if (!loggerLevelMapInited) {
         if (Solon.app() != null) {
            loggerLevelMapInited = true;
            Properties props = Solon.cfg().getProp("solon.logging.logger");
            if (props.size() > 0) {
               props.forEach((k, v) -> {
                  String key = (String)k;
                  String val = (String)v;
                  if (key.endsWith(".level")) {
                     String loggerExpr = key.substring(0, key.length() - 6);
                     Level loggerLevel = Level.of(val, Level.INFO);
                     addLoggerLevel(loggerExpr, loggerLevel);
                     if ("root".equals(loggerExpr)) {
                        rootLevel = loggerLevel;
                     }
                  }

               });
            }

         }
      }
   }

   static {
      rootLevel = Level.TRACE;
   }
}
