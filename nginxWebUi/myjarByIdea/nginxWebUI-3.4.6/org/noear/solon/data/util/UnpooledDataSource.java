package org.noear.solon.data.util;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.noear.solon.Utils;

public class UnpooledDataSource implements DataSource {
   private PrintWriter logWriter;
   private String url;
   private String username;
   private String password;
   private String driverClassName;

   public UnpooledDataSource(Properties props) {
      this.url = props.getProperty("url");
      if (Utils.isEmpty(this.url)) {
         this.url = props.getProperty("jdbcUrl");
      }

      if (Utils.isEmpty(this.url)) {
         throw new IllegalArgumentException("Invalid ds url parameter");
      } else {
         this.logWriter = new PrintWriter(System.out);
         this.username = props.getProperty("username");
         this.password = props.getProperty("password");
         this.setDriverClassName(this.driverClassName);
      }
   }

   public UnpooledDataSource(String url, String username, String password, String driverClassName) {
      if (Utils.isEmpty(url)) {
         throw new IllegalArgumentException("Invalid ds url parameter");
      } else {
         this.logWriter = new PrintWriter(System.out);
         this.url = url;
         this.username = username;
         this.password = password;
         this.setDriverClassName(driverClassName);
      }
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public void setDriverClassName(String driverClassName) {
      if (driverClassName != null) {
         try {
            this.driverClassName = driverClassName;
            Class.forName(driverClassName);
         } catch (ClassNotFoundException var3) {
            throw new IllegalArgumentException(var3);
         }
      }
   }

   public Connection getConnection() throws SQLException {
      return this.username == null ? DriverManager.getConnection(this.url) : DriverManager.getConnection(this.url, this.username, this.password);
   }

   public Connection getConnection(String username, String password) throws SQLException {
      return DriverManager.getConnection(this.url, username, password);
   }

   public <T> T unwrap(Class<T> iface) throws SQLException {
      return null;
   }

   public boolean isWrapperFor(Class<?> iface) throws SQLException {
      return false;
   }

   public PrintWriter getLogWriter() throws SQLException {
      return this.logWriter;
   }

   public void setLogWriter(PrintWriter out) throws SQLException {
      this.logWriter = out;
   }

   public void setLoginTimeout(int seconds) throws SQLException {
      DriverManager.setLoginTimeout(seconds);
   }

   public int getLoginTimeout() throws SQLException {
      return DriverManager.getLoginTimeout();
   }

   public Logger getParentLogger() throws SQLFeatureNotSupportedException {
      throw new SQLFeatureNotSupportedException();
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         UnpooledDataSource that = (UnpooledDataSource)o;
         return Objects.equals(this.url, that.url) && Objects.equals(this.username, that.username) && Objects.equals(this.password, that.password);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.url, this.username, this.password});
   }
}
