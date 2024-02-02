package org.h2.util;

import org.h2.Driver;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class DbDriverActivator implements BundleActivator {
   private static final String DATASOURCE_FACTORY_CLASS = "org.osgi.service.jdbc.DataSourceFactory";

   public void start(BundleContext var1) {
      Driver var2 = Driver.load();

      try {
         JdbcUtils.loadUserClass("org.osgi.service.jdbc.DataSourceFactory");
      } catch (Exception var4) {
         return;
      }

      OsgiDataSourceFactory.registerService(var1, var2);
   }

   public void stop(BundleContext var1) {
      Driver.unload();
   }
}
