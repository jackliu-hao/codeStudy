package org.h2.util;

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;
import org.h2.Driver;
import org.h2.engine.Constants;
import org.h2.jdbcx.JdbcDataSource;
import org.osgi.framework.BundleContext;
import org.osgi.service.jdbc.DataSourceFactory;

public class OsgiDataSourceFactory implements DataSourceFactory {
   private final Driver driver;

   public OsgiDataSourceFactory(Driver var1) {
      this.driver = var1;
   }

   public DataSource createDataSource(Properties var1) throws SQLException {
      Properties var2 = new Properties();
      if (var1 != null) {
         var2.putAll(var1);
      }

      rejectUnsupportedOptions(var2);
      rejectPoolingOptions(var2);
      JdbcDataSource var3 = new JdbcDataSource();
      setupH2DataSource(var3, var2);
      return var3;
   }

   public ConnectionPoolDataSource createConnectionPoolDataSource(Properties var1) throws SQLException {
      Properties var2 = new Properties();
      if (var1 != null) {
         var2.putAll(var1);
      }

      rejectUnsupportedOptions(var2);
      rejectPoolingOptions(var2);
      JdbcDataSource var3 = new JdbcDataSource();
      setupH2DataSource(var3, var2);
      return var3;
   }

   public XADataSource createXADataSource(Properties var1) throws SQLException {
      Properties var2 = new Properties();
      if (var1 != null) {
         var2.putAll(var1);
      }

      rejectUnsupportedOptions(var2);
      rejectPoolingOptions(var2);
      JdbcDataSource var3 = new JdbcDataSource();
      setupH2DataSource(var3, var2);
      return var3;
   }

   public java.sql.Driver createDriver(Properties var1) throws SQLException {
      if (var1 != null && !var1.isEmpty()) {
         throw new SQLException();
      } else {
         return this.driver;
      }
   }

   private static void rejectUnsupportedOptions(Properties var0) throws SQLFeatureNotSupportedException {
      if (var0.containsKey("roleName")) {
         throw new SQLFeatureNotSupportedException("The roleName property is not supported by H2");
      } else if (var0.containsKey("dataSourceName")) {
         throw new SQLFeatureNotSupportedException("The dataSourceName property is not supported by H2");
      }
   }

   private static void setupH2DataSource(JdbcDataSource var0, Properties var1) {
      if (var1.containsKey("user")) {
         var0.setUser((String)var1.remove("user"));
      }

      if (var1.containsKey("password")) {
         var0.setPassword((String)var1.remove("password"));
      }

      if (var1.containsKey("description")) {
         var0.setDescription((String)var1.remove("description"));
      }

      StringBuilder var2 = new StringBuilder();
      if (var1.containsKey("url")) {
         var2.append(var1.remove("url"));
         var1.remove("networkProtocol");
         var1.remove("serverName");
         var1.remove("portNumber");
         var1.remove("databaseName");
      } else {
         var2.append("jdbc:h2:");
         String var3 = "";
         if (var1.containsKey("networkProtocol")) {
            var3 = (String)var1.remove("networkProtocol");
            var2.append(var3).append(":");
         }

         if (var1.containsKey("serverName")) {
            var2.append("//").append(var1.remove("serverName"));
            if (var1.containsKey("portNumber")) {
               var2.append(":").append(var1.remove("portNumber"));
            }

            var2.append("/");
         } else if (var1.containsKey("portNumber")) {
            var2.append("//localhost:").append(var1.remove("portNumber")).append("/");
         } else if (var3.equals("tcp") || var3.equals("ssl")) {
            var2.append("//localhost/");
         }

         if (var1.containsKey("databaseName")) {
            var2.append(var1.remove("databaseName"));
         }
      }

      Iterator var5 = var1.keySet().iterator();

      while(var5.hasNext()) {
         Object var4 = var5.next();
         var2.append(";").append(var4).append("=").append(var1.get(var4));
      }

      if (var2.length() > "jdbc:h2:".length()) {
         var0.setURL(var2.toString());
      }

   }

   private static void rejectPoolingOptions(Properties var0) throws SQLFeatureNotSupportedException {
      if (var0.containsKey("initialPoolSize") || var0.containsKey("maxIdleTime") || var0.containsKey("maxPoolSize") || var0.containsKey("maxStatements") || var0.containsKey("minPoolSize") || var0.containsKey("propertyCycle")) {
         throw new SQLFeatureNotSupportedException("Pooling properties are not supported by H2");
      }
   }

   static void registerService(BundleContext var0, Driver var1) {
      Hashtable var2 = new Hashtable();
      var2.put("osgi.jdbc.driver.class", Driver.class.getName());
      var2.put("osgi.jdbc.driver.name", "H2 JDBC Driver");
      var2.put("osgi.jdbc.driver.version", Constants.FULL_VERSION);
      var0.registerService(DataSourceFactory.class.getName(), new OsgiDataSourceFactory(var1), var2);
   }
}
