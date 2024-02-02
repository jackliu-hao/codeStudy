package cn.hutool.log.dialect.console;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.ansi.AnsiColor;
import cn.hutool.core.lang.ansi.AnsiEncoder;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.level.Level;
import java.util.function.Function;

public class ConsoleColorLog extends ConsoleLog {
   private static final AnsiColor COLOR_CLASSNAME;
   private static final AnsiColor COLOR_TIME;
   private static final AnsiColor COLOR_NONE;
   private static Function<Level, AnsiColor> colorFactory;

   public static void setColorFactory(Function<Level, AnsiColor> colorFactory) {
      ConsoleColorLog.colorFactory = colorFactory;
   }

   public ConsoleColorLog(String name) {
      super(name);
   }

   public ConsoleColorLog(Class<?> clazz) {
      super(clazz);
   }

   public synchronized void log(String fqcn, Level level, Throwable t, String format, Object... arguments) {
      if (this.isEnabled(level)) {
         String template = AnsiEncoder.encode(COLOR_TIME, "[%s]", colorFactory.apply(level), "[%-5s]%s", COLOR_CLASSNAME, "%-30s: ", COLOR_NONE, "%s%n");
         System.out.format(template, DateUtil.now(), level.name(), " - ", ClassUtil.getShortClassName(this.getName()), StrUtil.format(format, arguments));
      }
   }

   static {
      COLOR_CLASSNAME = AnsiColor.CYAN;
      COLOR_TIME = AnsiColor.WHITE;
      COLOR_NONE = AnsiColor.DEFAULT;
      colorFactory = (level) -> {
         switch (level) {
            case DEBUG:
            case INFO:
               return AnsiColor.GREEN;
            case WARN:
               return AnsiColor.YELLOW;
            case ERROR:
               return AnsiColor.RED;
            case TRACE:
               return AnsiColor.MAGENTA;
            default:
               return COLOR_NONE;
         }
      };
   }
}
