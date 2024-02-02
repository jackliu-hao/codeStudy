package cn.hutool.log.dialect.jboss;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.AbstractLog;
import cn.hutool.log.level.Level;
import org.jboss.logging.Logger;

public class JbossLog extends AbstractLog {
   private static final long serialVersionUID = -6843151523380063975L;
   private final transient Logger logger;

   public JbossLog(Logger logger) {
      this.logger = logger;
   }

   public JbossLog(Class<?> clazz) {
      this(null == clazz ? "null" : clazz.getName());
   }

   public JbossLog(String name) {
      this(Logger.getLogger(name));
   }

   public String getName() {
      return this.logger.getName();
   }

   public boolean isTraceEnabled() {
      return this.logger.isTraceEnabled();
   }

   public void trace(String fqcn, Throwable t, String format, Object... arguments) {
      if (this.isTraceEnabled()) {
         this.logger.trace((String)fqcn, (Object)StrUtil.format(format, arguments), t);
      }

   }

   public boolean isDebugEnabled() {
      return this.logger.isDebugEnabled();
   }

   public void debug(String fqcn, Throwable t, String format, Object... arguments) {
      if (this.isDebugEnabled()) {
         this.logger.debug((String)fqcn, (Object)StrUtil.format(format, arguments), t);
      }

   }

   public boolean isInfoEnabled() {
      return this.logger.isInfoEnabled();
   }

   public void info(String fqcn, Throwable t, String format, Object... arguments) {
      if (this.isInfoEnabled()) {
         this.logger.info((String)fqcn, (Object)StrUtil.format(format, arguments), t);
      }

   }

   public boolean isWarnEnabled() {
      return this.logger.isEnabled(Logger.Level.WARN);
   }

   public void warn(String fqcn, Throwable t, String format, Object... arguments) {
      if (this.isWarnEnabled()) {
         this.logger.warn((String)fqcn, (Object)StrUtil.format(format, arguments), t);
      }

   }

   public boolean isErrorEnabled() {
      return this.logger.isEnabled(Logger.Level.ERROR);
   }

   public void error(String fqcn, Throwable t, String format, Object... arguments) {
      if (this.isErrorEnabled()) {
         this.logger.error((String)fqcn, (Object)StrUtil.format(format, arguments), t);
      }

   }

   public void log(String fqcn, Level level, Throwable t, String format, Object... arguments) {
      switch (level) {
         case TRACE:
            this.trace(fqcn, t, format, arguments);
            break;
         case DEBUG:
            this.debug(fqcn, t, format, arguments);
            break;
         case INFO:
            this.info(fqcn, t, format, arguments);
            break;
         case WARN:
            this.warn(fqcn, t, format, arguments);
            break;
         case ERROR:
            this.error(fqcn, t, format, arguments);
            break;
         default:
            throw new Error(StrUtil.format("Can not identify level: {}", new Object[]{level}));
      }

   }
}
