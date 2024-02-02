package cn.hutool.log.dialect.commons;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

public class ApacheCommonsLogFactory extends LogFactory {
   public ApacheCommonsLogFactory() {
      super("Apache Common Logging");
      this.checkLogExist(org.apache.commons.logging.LogFactory.class);
   }

   public Log createLog(String name) {
      try {
         return new ApacheCommonsLog4JLog(name);
      } catch (Exception var3) {
         return new ApacheCommonsLog(name);
      }
   }

   public Log createLog(Class<?> clazz) {
      try {
         return new ApacheCommonsLog4JLog(clazz);
      } catch (Exception var3) {
         return new ApacheCommonsLog(clazz);
      }
   }

   protected void checkLogExist(Class<?> logClassName) {
      super.checkLogExist(logClassName);
      this.getLog(ApacheCommonsLogFactory.class);
   }
}
