package cn.hutool.log.dialect.log4j;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.AbstractLog;
import cn.hutool.log.level.Level;
import org.apache.log4j.Logger;

public class Log4jLog extends AbstractLog {
   private static final long serialVersionUID = -6843151523380063975L;
   private final Logger logger;

   public Log4jLog(Logger logger) {
      this.logger = logger;
   }

   public Log4jLog(Class<?> clazz) {
      this(null == clazz ? "null" : clazz.getName());
   }

   public Log4jLog(String name) {
      this(Logger.getLogger(name));
   }

   public String getName() {
      return this.logger.getName();
   }

   public boolean isTraceEnabled() {
      return this.logger.isTraceEnabled();
   }

   public void trace(String fqcn, Throwable t, String format, Object... arguments) {
      this.log(fqcn, Level.TRACE, t, format, arguments);
   }

   public boolean isDebugEnabled() {
      return this.logger.isDebugEnabled();
   }

   public void debug(String fqcn, Throwable t, String format, Object... arguments) {
      this.log(fqcn, Level.DEBUG, t, format, arguments);
   }

   public boolean isInfoEnabled() {
      return this.logger.isInfoEnabled();
   }

   public void info(String fqcn, Throwable t, String format, Object... arguments) {
      this.log(fqcn, Level.INFO, t, format, arguments);
   }

   public boolean isWarnEnabled() {
      return this.logger.isEnabledFor(org.apache.log4j.Level.WARN);
   }

   public void warn(String fqcn, Throwable t, String format, Object... arguments) {
      this.log(fqcn, Level.WARN, t, format, arguments);
   }

   public boolean isErrorEnabled() {
      return this.logger.isEnabledFor(org.apache.log4j.Level.ERROR);
   }

   public void error(String fqcn, Throwable t, String format, Object... arguments) {
      this.log(fqcn, Level.ERROR, t, format, arguments);
   }

   public void log(String fqcn, Level level, Throwable t, String format, Object... arguments) {
      org.apache.log4j.Level log4jLevel;
      switch (level) {
         case TRACE:
            log4jLevel = org.apache.log4j.Level.TRACE;
            break;
         case DEBUG:
            log4jLevel = org.apache.log4j.Level.DEBUG;
            break;
         case INFO:
            log4jLevel = org.apache.log4j.Level.INFO;
            break;
         case WARN:
            log4jLevel = org.apache.log4j.Level.WARN;
            break;
         case ERROR:
            log4jLevel = org.apache.log4j.Level.ERROR;
            break;
         default:
            throw new Error(StrUtil.format("Can not identify level: {}", new Object[]{level}));
      }

      if (this.logger.isEnabledFor(log4jLevel)) {
         this.logger.log(fqcn, log4jLevel, StrUtil.format(format, arguments), t);
      }

   }
}
