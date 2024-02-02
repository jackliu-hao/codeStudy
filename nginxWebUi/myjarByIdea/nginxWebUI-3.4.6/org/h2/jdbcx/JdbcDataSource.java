package org.h2.jdbcx;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.PooledConnection;
import javax.sql.XAConnection;
import javax.sql.XADataSource;
import org.h2.jdbc.JdbcConnection;
import org.h2.message.DbException;
import org.h2.message.TraceObject;
import org.h2.util.StringUtils;

public final class JdbcDataSource extends TraceObject implements XADataSource, DataSource, ConnectionPoolDataSource, Serializable, Referenceable, JdbcDataSourceBackwardsCompat {
   private static final long serialVersionUID = 1288136338451857771L;
   private transient JdbcDataSourceFactory factory;
   private transient PrintWriter logWriter;
   private int loginTimeout;
   private String userName = "";
   private char[] passwordChars = new char[0];
   private String url = "";
   private String description;

   public JdbcDataSource() {
      this.initFactory();
      int var1 = getNextId(12);
      this.setTrace(this.factory.getTrace(), 12, var1);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      this.initFactory();
      var1.defaultReadObject();
   }

   private void initFactory() {
      this.factory = new JdbcDataSourceFactory();
   }

   public int getLoginTimeout() {
      this.debugCodeCall("getLoginTimeout");
      return this.loginTimeout;
   }

   public void setLoginTimeout(int var1) {
      this.debugCodeCall("setLoginTimeout", (long)var1);
      this.loginTimeout = var1;
   }

   public PrintWriter getLogWriter() {
      this.debugCodeCall("getLogWriter");
      return this.logWriter;
   }

   public void setLogWriter(PrintWriter var1) {
      this.debugCodeCall("setLogWriter(out)");
      this.logWriter = var1;
   }

   public Connection getConnection() throws SQLException {
      this.debugCodeCall("getConnection");
      return new JdbcConnection(this.url, (Properties)null, this.userName, StringUtils.cloneCharArray(this.passwordChars), false);
   }

   public Connection getConnection(String var1, String var2) throws SQLException {
      if (this.isDebugEnabled()) {
         this.debugCode("getConnection(" + quote(var1) + ", \"\")");
      }

      return new JdbcConnection(this.url, (Properties)null, var1, var2, false);
   }

   public String getURL() {
      this.debugCodeCall("getURL");
      return this.url;
   }

   public void setURL(String var1) {
      this.debugCodeCall("setURL", var1);
      this.url = var1;
   }

   public String getUrl() {
      this.debugCodeCall("getUrl");
      return this.url;
   }

   public void setUrl(String var1) {
      this.debugCodeCall("setUrl", var1);
      this.url = var1;
   }

   public void setPassword(String var1) {
      this.debugCodeCall("setPassword", "");
      this.passwordChars = var1 == null ? null : var1.toCharArray();
   }

   public void setPasswordChars(char[] var1) {
      if (this.isDebugEnabled()) {
         this.debugCode("setPasswordChars(new char[0])");
      }

      this.passwordChars = var1;
   }

   private static String convertToString(char[] var0) {
      return var0 == null ? null : new String(var0);
   }

   public String getPassword() {
      this.debugCodeCall("getPassword");
      return convertToString(this.passwordChars);
   }

   public String getUser() {
      this.debugCodeCall("getUser");
      return this.userName;
   }

   public void setUser(String var1) {
      this.debugCodeCall("setUser", var1);
      this.userName = var1;
   }

   public String getDescription() {
      this.debugCodeCall("getDescription");
      return this.description;
   }

   public void setDescription(String var1) {
      this.debugCodeCall("getDescription", var1);
      this.description = var1;
   }

   public Reference getReference() {
      this.debugCodeCall("getReference");
      String var1 = JdbcDataSourceFactory.class.getName();
      Reference var2 = new Reference(this.getClass().getName(), var1, (String)null);
      var2.add(new StringRefAddr("url", this.url));
      var2.add(new StringRefAddr("user", this.userName));
      var2.add(new StringRefAddr("password", convertToString(this.passwordChars)));
      var2.add(new StringRefAddr("loginTimeout", Integer.toString(this.loginTimeout)));
      var2.add(new StringRefAddr("description", this.description));
      return var2;
   }

   public XAConnection getXAConnection() throws SQLException {
      this.debugCodeCall("getXAConnection");
      return new JdbcXAConnection(this.factory, getNextId(13), new JdbcConnection(this.url, (Properties)null, this.userName, StringUtils.cloneCharArray(this.passwordChars), false));
   }

   public XAConnection getXAConnection(String var1, String var2) throws SQLException {
      if (this.isDebugEnabled()) {
         this.debugCode("getXAConnection(" + quote(var1) + ", \"\")");
      }

      return new JdbcXAConnection(this.factory, getNextId(13), new JdbcConnection(this.url, (Properties)null, var1, var2, false));
   }

   public PooledConnection getPooledConnection() throws SQLException {
      this.debugCodeCall("getPooledConnection");
      return this.getXAConnection();
   }

   public PooledConnection getPooledConnection(String var1, String var2) throws SQLException {
      if (this.isDebugEnabled()) {
         this.debugCode("getPooledConnection(" + quote(var1) + ", \"\")");
      }

      return this.getXAConnection(var1, var2);
   }

   public <T> T unwrap(Class<T> var1) throws SQLException {
      try {
         if (this.isWrapperFor(var1)) {
            return this;
         } else {
            throw DbException.getInvalidValueException("iface", var1);
         }
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public boolean isWrapperFor(Class<?> var1) throws SQLException {
      return var1 != null && var1.isAssignableFrom(this.getClass());
   }

   public Logger getParentLogger() {
      return null;
   }

   public String toString() {
      return this.getTraceObjectName() + ": url=" + this.url + " user=" + this.userName;
   }
}
