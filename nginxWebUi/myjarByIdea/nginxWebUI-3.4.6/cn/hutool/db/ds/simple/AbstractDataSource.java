package cn.hutool.db.ds.simple;

import java.io.Closeable;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import javax.sql.DataSource;

public abstract class AbstractDataSource implements DataSource, Cloneable, Closeable {
   public PrintWriter getLogWriter() {
      return DriverManager.getLogWriter();
   }

   public void setLogWriter(PrintWriter out) {
      DriverManager.setLogWriter(out);
   }

   public void setLoginTimeout(int seconds) {
      DriverManager.setLoginTimeout(seconds);
   }

   public int getLoginTimeout() {
      return DriverManager.getLoginTimeout();
   }

   public <T> T unwrap(Class<T> iface) throws SQLException {
      throw new SQLException("Can't support unwrap method!");
   }

   public boolean isWrapperFor(Class<?> iface) throws SQLException {
      throw new SQLException("Can't support isWrapperFor method!");
   }

   public Logger getParentLogger() throws SQLFeatureNotSupportedException {
      throw new SQLFeatureNotSupportedException("DataSource can't support getParentLogger method!");
   }
}
