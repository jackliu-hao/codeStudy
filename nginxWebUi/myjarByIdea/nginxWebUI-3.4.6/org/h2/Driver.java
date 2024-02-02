package org.h2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;
import org.h2.jdbc.JdbcConnection;
import org.h2.message.DbException;

public class Driver implements java.sql.Driver, JdbcDriverBackwardsCompat {
   private static final Driver INSTANCE = new Driver();
   private static final String DEFAULT_URL = "jdbc:default:connection";
   private static final ThreadLocal<Connection> DEFAULT_CONNECTION = new ThreadLocal();
   private static boolean registered;

   public Connection connect(String var1, Properties var2) throws SQLException {
      if (var1 == null) {
         throw DbException.getJdbcSQLException(90046, (Throwable)null, "jdbc:h2:{ {.|mem:}[name] | [file:]fileName | {tcp|ssl}:[//]server[:port][,server2[:port]]/name }[;key=value...]", null);
      } else if (var1.startsWith("jdbc:h2:")) {
         return new JdbcConnection(var1, var2, (String)null, (Object)null, false);
      } else {
         return var1.equals("jdbc:default:connection") ? (Connection)DEFAULT_CONNECTION.get() : null;
      }
   }

   public boolean acceptsURL(String var1) throws SQLException {
      if (var1 == null) {
         throw DbException.getJdbcSQLException(90046, (Throwable)null, "jdbc:h2:{ {.|mem:}[name] | [file:]fileName | {tcp|ssl}:[//]server[:port][,server2[:port]]/name }[;key=value...]", null);
      } else if (var1.startsWith("jdbc:h2:")) {
         return true;
      } else if (var1.equals("jdbc:default:connection")) {
         return DEFAULT_CONNECTION.get() != null;
      } else {
         return false;
      }
   }

   public int getMajorVersion() {
      return 2;
   }

   public int getMinorVersion() {
      return 1;
   }

   public DriverPropertyInfo[] getPropertyInfo(String var1, Properties var2) {
      return new DriverPropertyInfo[0];
   }

   public boolean jdbcCompliant() {
      return true;
   }

   public Logger getParentLogger() {
      return null;
   }

   public static synchronized Driver load() {
      try {
         if (!registered) {
            registered = true;
            DriverManager.registerDriver(INSTANCE);
         }
      } catch (SQLException var1) {
         DbException.traceThrowable(var1);
      }

      return INSTANCE;
   }

   public static synchronized void unload() {
      try {
         if (registered) {
            registered = false;
            DriverManager.deregisterDriver(INSTANCE);
         }
      } catch (SQLException var1) {
         DbException.traceThrowable(var1);
      }

   }

   public static void setDefaultConnection(Connection var0) {
      if (var0 == null) {
         DEFAULT_CONNECTION.remove();
      } else {
         DEFAULT_CONNECTION.set(var0);
      }

   }

   public static void setThreadContextClassLoader(Thread var0) {
      try {
         var0.setContextClassLoader(Driver.class.getClassLoader());
      } catch (Throwable var2) {
      }

   }

   static {
      load();
   }
}
