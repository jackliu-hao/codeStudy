package cn.hutool.log;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.level.Level;
import java.io.Serializable;

public abstract class AbstractLog implements Log, Serializable {
   private static final long serialVersionUID = -3211115409504005616L;
   private static final String FQCN = AbstractLog.class.getName();

   public boolean isEnabled(Level level) {
      switch (level) {
         case TRACE:
            return this.isTraceEnabled();
         case DEBUG:
            return this.isDebugEnabled();
         case INFO:
            return this.isInfoEnabled();
         case WARN:
            return this.isWarnEnabled();
         case ERROR:
            return this.isErrorEnabled();
         default:
            throw new Error(StrUtil.format("Can not identify level: {}", new Object[]{level}));
      }
   }

   public void trace(Throwable t) {
      this.trace(t, ExceptionUtil.getSimpleMessage(t));
   }

   public void trace(String format, Object... arguments) {
      this.trace((Throwable)null, format, arguments);
   }

   public void trace(Throwable t, String format, Object... arguments) {
      this.trace(FQCN, t, format, arguments);
   }

   public void debug(Throwable t) {
      this.debug(t, ExceptionUtil.getSimpleMessage(t));
   }

   public void debug(String format, Object... arguments) {
      if (null != arguments && 1 == arguments.length && arguments[0] instanceof Throwable) {
         this.debug((Throwable)arguments[0], format);
      } else {
         this.debug((Throwable)null, format, arguments);
      }

   }

   public void debug(Throwable t, String format, Object... arguments) {
      this.debug(FQCN, t, format, arguments);
   }

   public void info(Throwable t) {
      this.info(t, ExceptionUtil.getSimpleMessage(t));
   }

   public void info(String format, Object... arguments) {
      if (null != arguments && 1 == arguments.length && arguments[0] instanceof Throwable) {
         this.info((Throwable)arguments[0], format);
      } else {
         this.info((Throwable)null, format, arguments);
      }

   }

   public void info(Throwable t, String format, Object... arguments) {
      this.info(FQCN, t, format, arguments);
   }

   public void warn(Throwable t) {
      this.warn(t, ExceptionUtil.getSimpleMessage(t));
   }

   public void warn(String format, Object... arguments) {
      if (null != arguments && 1 == arguments.length && arguments[0] instanceof Throwable) {
         this.warn((Throwable)arguments[0], format);
      } else {
         this.warn((Throwable)null, format, arguments);
      }

   }

   public void warn(Throwable t, String format, Object... arguments) {
      this.warn(FQCN, t, format, arguments);
   }

   public void error(Throwable t) {
      this.error(t, ExceptionUtil.getSimpleMessage(t));
   }

   public void error(String format, Object... arguments) {
      if (null != arguments && 1 == arguments.length && arguments[0] instanceof Throwable) {
         this.error((Throwable)arguments[0], format);
      } else {
         this.error((Throwable)null, format, arguments);
      }

   }

   public void error(Throwable t, String format, Object... arguments) {
      this.error(FQCN, t, format, arguments);
   }

   public void log(Level level, String format, Object... arguments) {
      if (null != arguments && 1 == arguments.length && arguments[0] instanceof Throwable) {
         this.log(level, (Throwable)arguments[0], format);
      } else {
         this.log(level, (Throwable)null, format, arguments);
      }

   }

   public void log(Level level, Throwable t, String format, Object... arguments) {
      this.log(FQCN, level, t, format, arguments);
   }
}
