package cn.hutool.db.ds;

import cn.hutool.core.clone.CloneRuntimeException;
import cn.hutool.core.io.IoUtil;
import java.io.Closeable;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import javax.sql.DataSource;

public class DataSourceWrapper implements DataSource, Closeable, Cloneable {
   private final DataSource ds;
   private final String driver;

   public static DataSourceWrapper wrap(DataSource ds, String driver) {
      return new DataSourceWrapper(ds, driver);
   }

   public DataSourceWrapper(DataSource ds, String driver) {
      this.ds = ds;
      this.driver = driver;
   }

   public String getDriver() {
      return this.driver;
   }

   public DataSource getRaw() {
      return this.ds;
   }

   public PrintWriter getLogWriter() throws SQLException {
      return this.ds.getLogWriter();
   }

   public void setLogWriter(PrintWriter out) throws SQLException {
      this.ds.setLogWriter(out);
   }

   public void setLoginTimeout(int seconds) throws SQLException {
      this.ds.setLoginTimeout(seconds);
   }

   public int getLoginTimeout() throws SQLException {
      return this.ds.getLoginTimeout();
   }

   public Logger getParentLogger() throws SQLFeatureNotSupportedException {
      return this.ds.getParentLogger();
   }

   public <T> T unwrap(Class<T> iface) throws SQLException {
      return this.ds.unwrap(iface);
   }

   public boolean isWrapperFor(Class<?> iface) throws SQLException {
      return this.ds.isWrapperFor(iface);
   }

   public Connection getConnection() throws SQLException {
      return this.ds.getConnection();
   }

   public Connection getConnection(String username, String password) throws SQLException {
      return this.ds.getConnection(username, password);
   }

   public void close() {
      if (this.ds instanceof AutoCloseable) {
         IoUtil.close((AutoCloseable)this.ds);
      }

   }

   public DataSourceWrapper clone() {
      try {
         return (DataSourceWrapper)super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new CloneRuntimeException(var2);
      }
   }
}
