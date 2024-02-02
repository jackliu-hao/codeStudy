package com.mysql.cj.jdbc;

import java.sql.Connection;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;

public class CommentClientInfoProvider implements ClientInfoProvider {
   private Properties clientInfo;

   public synchronized void initialize(Connection conn, Properties configurationProps) throws SQLException {
      this.clientInfo = new Properties();
   }

   public synchronized void destroy() throws SQLException {
      this.clientInfo = null;
   }

   public synchronized Properties getClientInfo(Connection conn) throws SQLException {
      return this.clientInfo;
   }

   public synchronized String getClientInfo(Connection conn, String name) throws SQLException {
      return this.clientInfo.getProperty(name);
   }

   public synchronized void setClientInfo(Connection conn, Properties properties) throws SQLClientInfoException {
      this.clientInfo = new Properties();
      Enumeration<?> propNames = properties.propertyNames();

      while(propNames.hasMoreElements()) {
         String name = (String)propNames.nextElement();
         this.clientInfo.put(name, properties.getProperty(name));
      }

      this.setComment(conn);
   }

   public synchronized void setClientInfo(Connection conn, String name, String value) throws SQLClientInfoException {
      this.clientInfo.setProperty(name, value);
      this.setComment(conn);
   }

   private synchronized void setComment(Connection conn) {
      StringBuilder commentBuf = new StringBuilder();
      Enumeration<?> propNames = this.clientInfo.propertyNames();

      while(propNames.hasMoreElements()) {
         String name = (String)propNames.nextElement();
         if (commentBuf.length() > 0) {
            commentBuf.append(", ");
         }

         commentBuf.append("" + name);
         commentBuf.append("=");
         commentBuf.append("" + this.clientInfo.getProperty(name));
      }

      ((JdbcConnection)conn).setStatementComment(commentBuf.toString());
   }
}
