package cn.hutool.log.dialect.tinylog;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.AbstractLog;
import org.tinylog.Level;
import org.tinylog.configuration.Configuration;
import org.tinylog.format.AdvancedMessageFormatter;
import org.tinylog.format.MessageFormatter;
import org.tinylog.provider.LoggingProvider;
import org.tinylog.provider.ProviderRegistry;

public class TinyLog2 extends AbstractLog {
   private static final long serialVersionUID = 1L;
   private static final int DEPTH = 5;
   private final int level;
   private final String name;
   private static final LoggingProvider provider = ProviderRegistry.getLoggingProvider();
   private static final MessageFormatter formatter = new AdvancedMessageFormatter(Configuration.getLocale(), Configuration.isEscapingEnabled());

   public TinyLog2(Class<?> clazz) {
      this(null == clazz ? "null" : clazz.getName());
   }

   public TinyLog2(String name) {
      this.name = name;
      this.level = provider.getMinimumLevel().ordinal();
   }

   public String getName() {
      return this.name;
   }

   public boolean isTraceEnabled() {
      return this.level <= Level.TRACE.ordinal();
   }

   public void trace(String fqcn, Throwable t, String format, Object... arguments) {
      this.logIfEnabled(fqcn, Level.TRACE, t, format, arguments);
   }

   public boolean isDebugEnabled() {
      return this.level <= Level.DEBUG.ordinal();
   }

   public void debug(String fqcn, Throwable t, String format, Object... arguments) {
      this.logIfEnabled(fqcn, Level.DEBUG, t, format, arguments);
   }

   public boolean isInfoEnabled() {
      return this.level <= Level.INFO.ordinal();
   }

   public void info(String fqcn, Throwable t, String format, Object... arguments) {
      this.logIfEnabled(fqcn, Level.INFO, t, format, arguments);
   }

   public boolean isWarnEnabled() {
      return this.level <= Level.WARN.ordinal();
   }

   public void warn(String fqcn, Throwable t, String format, Object... arguments) {
      this.logIfEnabled(fqcn, Level.WARN, t, format, arguments);
   }

   public boolean isErrorEnabled() {
      return this.level <= Level.ERROR.ordinal();
   }

   public void error(String fqcn, Throwable t, String format, Object... arguments) {
      this.logIfEnabled(fqcn, Level.ERROR, t, format, arguments);
   }

   public void log(String fqcn, cn.hutool.log.level.Level level, Throwable t, String format, Object... arguments) {
      this.logIfEnabled(fqcn, this.toTinyLevel(level), t, format, arguments);
   }

   public boolean isEnabled(cn.hutool.log.level.Level level) {
      return this.level <= this.toTinyLevel(level).ordinal();
   }

   private void logIfEnabled(String fqcn, Level level, Throwable t, String format, Object... arguments) {
      if (null == t) {
         t = getLastArgumentIfThrowable(arguments);
      }

      provider.log(5, (String)null, level, t, formatter, StrUtil.toString(format), arguments);
   }

   private Level toTinyLevel(cn.hutool.log.level.Level level) {
      Level tinyLevel;
      switch (level) {
         case TRACE:
            tinyLevel = Level.TRACE;
            break;
         case DEBUG:
            tinyLevel = Level.DEBUG;
            break;
         case INFO:
            tinyLevel = Level.INFO;
            break;
         case WARN:
            tinyLevel = Level.WARN;
            break;
         case ERROR:
            tinyLevel = Level.ERROR;
            break;
         case OFF:
            tinyLevel = Level.OFF;
            break;
         default:
            throw new Error(StrUtil.format("Can not identify level: {}", new Object[]{level}));
      }

      return tinyLevel;
   }

   private static Throwable getLastArgumentIfThrowable(Object... arguments) {
      return ArrayUtil.isNotEmpty(arguments) && arguments[arguments.length - 1] instanceof Throwable ? (Throwable)arguments[arguments.length - 1] : null;
   }
}
