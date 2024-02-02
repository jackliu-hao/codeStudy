package cn.hutool.log.dialect.console;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

public class ConsoleColorLogFactory extends LogFactory {
   public ConsoleColorLogFactory() {
      super("Hutool Console Color Logging");
   }

   public Log createLog(String name) {
      return new ConsoleColorLog(name);
   }

   public Log createLog(Class<?> clazz) {
      return new ConsoleColorLog(clazz);
   }
}
