package com.zaxxer.hikari.hibernate;

import com.zaxxer.hikari.HikariConfig;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class HikariConfigurationUtil {
   public static final String CONFIG_PREFIX = "hibernate.hikari.";
   public static final String CONFIG_PREFIX_DATASOURCE = "hibernate.hikari.dataSource.";

   public static HikariConfig loadConfiguration(Map props) {
      Properties hikariProps = new Properties();
      copyProperty("hibernate.connection.isolation", props, "transactionIsolation", hikariProps);
      copyProperty("hibernate.connection.autocommit", props, "autoCommit", hikariProps);
      copyProperty("hibernate.connection.driver_class", props, "driverClassName", hikariProps);
      copyProperty("hibernate.connection.url", props, "jdbcUrl", hikariProps);
      copyProperty("hibernate.connection.username", props, "username", hikariProps);
      copyProperty("hibernate.connection.password", props, "password", hikariProps);
      Iterator var2 = props.keySet().iterator();

      while(var2.hasNext()) {
         Object keyo = var2.next();
         String key = (String)keyo;
         if (key.startsWith("hibernate.hikari.")) {
            hikariProps.setProperty(key.substring("hibernate.hikari.".length()), (String)props.get(key));
         }
      }

      return new HikariConfig(hikariProps);
   }

   private static void copyProperty(String srcKey, Map src, String dstKey, Properties dst) {
      if (src.containsKey(srcKey)) {
         dst.setProperty(dstKey, (String)src.get(srcKey));
      }

   }
}
