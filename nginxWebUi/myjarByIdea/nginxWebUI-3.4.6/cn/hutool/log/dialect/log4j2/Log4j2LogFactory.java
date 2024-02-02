package cn.hutool.log.dialect.log4j2;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.apache.logging.log4j.LogManager;

public class Log4j2LogFactory extends LogFactory {
   public Log4j2LogFactory() {
      super("Log4j2");
      this.checkLogExist(LogManager.class);
   }

   public Log createLog(String name) {
      return new Log4j2Log(name);
   }

   public Log createLog(Class<?> clazz) {
      return new Log4j2Log(clazz);
   }
}
