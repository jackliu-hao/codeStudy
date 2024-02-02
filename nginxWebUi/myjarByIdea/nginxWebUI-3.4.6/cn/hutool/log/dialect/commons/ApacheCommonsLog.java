package cn.hutool.log.dialect.commons;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.AbstractLog;
import cn.hutool.log.level.Level;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ApacheCommonsLog extends AbstractLog {
   private static final long serialVersionUID = -6843151523380063975L;
   private final transient Log logger;
   private final String name;

   public ApacheCommonsLog(Log logger, String name) {
      this.logger = logger;
      this.name = name;
   }

   public ApacheCommonsLog(Class<?> clazz) {
      this(LogFactory.getLog(clazz), null == clazz ? "null" : clazz.getName());
   }

   public ApacheCommonsLog(String name) {
      this(LogFactory.getLog(name), name);
   }

   public String getName() {
      return this.name;
   }

   public boolean isTraceEnabled() {
      return this.logger.isTraceEnabled();
   }

   public void trace(String fqcn, Throwable t, String format, Object... arguments) {
      if (this.isTraceEnabled()) {
         this.logger.trace(StrUtil.format(format, arguments), t);
      }

   }

   public boolean isDebugEnabled() {
      return this.logger.isDebugEnabled();
   }

   public void debug(String fqcn, Throwable t, String format, Object... arguments) {
      if (this.isDebugEnabled()) {
         this.logger.debug(StrUtil.format(format, arguments), t);
      }

   }

   public boolean isInfoEnabled() {
      return this.logger.isInfoEnabled();
   }

   public void info(String fqcn, Throwable t, String format, Object... arguments) {
      if (this.isInfoEnabled()) {
         this.logger.info(StrUtil.format(format, arguments), t);
      }

   }

   public boolean isWarnEnabled() {
      return this.logger.isWarnEnabled();
   }

   public void warn(String format, Object... arguments) {
      if (this.isWarnEnabled()) {
         this.logger.warn(StrUtil.format(format, arguments));
      }

   }

   public void warn(Throwable t, String format, Object... arguments) {
   }

   public void warn(String fqcn, Throwable t, String format, Object... arguments) {
      if (this.isWarnEnabled()) {
         this.logger.warn(StrUtil.format(format, arguments), t);
      }

   }

   public boolean isErrorEnabled() {
      return this.logger.isErrorEnabled();
   }

   public void error(String fqcn, Throwable t, String format, Object... arguments) {
      if (this.isErrorEnabled()) {
         this.logger.warn(StrUtil.format(format, arguments), t);
      }

   }

   public void log(String fqcn, Level level, Throwable t, String format, Object... arguments) {
      switch (level) {
         case TRACE:
            this.trace(t, format, arguments);
            break;
         case DEBUG:
            this.debug(t, format, arguments);
            break;
         case INFO:
            this.info(t, format, arguments);
            break;
         case WARN:
            this.warn(t, format, arguments);
            break;
         case ERROR:
            this.error(t, format, arguments);
            break;
         default:
            throw new Error(StrUtil.format("Can not identify level: {}", new Object[]{level}));
      }

   }
}
