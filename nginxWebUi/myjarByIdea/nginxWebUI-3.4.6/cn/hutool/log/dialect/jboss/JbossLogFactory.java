package cn.hutool.log.dialect.jboss;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.jboss.logging.Logger;

public class JbossLogFactory extends LogFactory {
   public JbossLogFactory() {
      super("JBoss Logging");
      this.checkLogExist(Logger.class);
   }

   public Log createLog(String name) {
      return new JbossLog(name);
   }

   public Log createLog(Class<?> clazz) {
      return new JbossLog(clazz);
   }
}
