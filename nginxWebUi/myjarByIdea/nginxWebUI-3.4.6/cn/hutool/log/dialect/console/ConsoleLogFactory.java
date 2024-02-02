package cn.hutool.log.dialect.console;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

public class ConsoleLogFactory extends LogFactory {
   public ConsoleLogFactory() {
      super("Hutool Console Logging");
   }

   public Log createLog(String name) {
      return new ConsoleLog(name);
   }

   public Log createLog(Class<?> clazz) {
      return new ConsoleLog(clazz);
   }
}
