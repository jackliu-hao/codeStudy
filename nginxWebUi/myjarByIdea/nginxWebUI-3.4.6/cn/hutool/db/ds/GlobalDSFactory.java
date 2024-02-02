package cn.hutool.db.ds;

import cn.hutool.log.StaticLog;
import cn.hutool.setting.Setting;

public class GlobalDSFactory {
   private static volatile DSFactory factory;
   private static final Object lock = new Object();

   public static DSFactory get() {
      if (null == factory) {
         synchronized(lock) {
            if (null == factory) {
               factory = DSFactory.create((Setting)null);
            }
         }
      }

      return factory;
   }

   public static DSFactory set(DSFactory customDSFactory) {
      synchronized(lock) {
         if (null != factory) {
            if (factory.equals(customDSFactory)) {
               return factory;
            }

            factory.destroy();
         }

         StaticLog.debug("Custom use [{}] DataSource.", customDSFactory.dataSourceName);
         factory = customDSFactory;
      }

      return factory;
   }

   static {
      Runtime.getRuntime().addShutdownHook(new Thread() {
         public void run() {
            if (null != GlobalDSFactory.factory) {
               GlobalDSFactory.factory.destroy();
               StaticLog.debug("DataSource: [{}] destroyed.", GlobalDSFactory.factory.dataSourceName);
               GlobalDSFactory.factory = null;
            }

         }
      });
   }
}
