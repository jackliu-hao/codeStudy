package com.mysql.cj.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;

public class ClientInfoProviderSP implements ClientInfoProvider {
   public static final String PNAME_clientInfoSetSPName = "clientInfoSetSPName";
   public static final String PNAME_clientInfoGetSPName = "clientInfoGetSPName";
   public static final String PNAME_clientInfoGetBulkSPName = "clientInfoGetBulkSPName";
   public static final String PNAME_clientInfoDatabase = "clientInfoDatabase";
   PreparedStatement setClientInfoSp;
   PreparedStatement getClientInfoSp;
   PreparedStatement getClientInfoBulkSp;

   public synchronized void initialize(Connection conn, Properties configurationProps) throws SQLException {
      String identifierQuote = ((JdbcConnection)conn).getSession().getIdentifierQuoteString();
      String setClientInfoSpName = configurationProps.getProperty("clientInfoSetSPName", "setClientInfo");
      String getClientInfoSpName = configurationProps.getProperty("clientInfoGetSPName", "getClientInfo");
      String getClientInfoBulkSpName = configurationProps.getProperty("clientInfoGetBulkSPName", "getClientInfoBulk");
      String clientInfoDatabase = configurationProps.getProperty("clientInfoDatabase", "");
      String db = "".equals(clientInfoDatabase) ? ((JdbcConnection)conn).getDatabase() : clientInfoDatabase;
      this.setClientInfoSp = ((JdbcConnection)conn).clientPrepareStatement("CALL " + identifierQuote + db + identifierQuote + "." + identifierQuote + setClientInfoSpName + identifierQuote + "(?, ?)");
      this.getClientInfoSp = ((JdbcConnection)conn).clientPrepareStatement("CALL" + identifierQuote + db + identifierQuote + "." + identifierQuote + getClientInfoSpName + identifierQuote + "(?)");
      this.getClientInfoBulkSp = ((JdbcConnection)conn).clientPrepareStatement("CALL " + identifierQuote + db + identifierQuote + "." + identifierQuote + getClientInfoBulkSpName + identifierQuote + "()");
   }

   public synchronized void destroy() throws SQLException {
      if (this.setClientInfoSp != null) {
         this.setClientInfoSp.close();
         this.setClientInfoSp = null;
      }

      if (this.getClientInfoSp != null) {
         this.getClientInfoSp.close();
         this.getClientInfoSp = null;
      }

      if (this.getClientInfoBulkSp != null) {
         this.getClientInfoBulkSp.close();
         this.getClientInfoBulkSp = null;
      }

   }

   public synchronized Properties getClientInfo(Connection conn) throws SQLException {
      ResultSet rs = null;
      Properties props = new Properties();

      try {
         this.getClientInfoBulkSp.execute();
         rs = this.getClientInfoBulkSp.getResultSet();

         while(rs.next()) {
            props.setProperty(rs.getString(1), rs.getString(2));
         }
      } finally {
         if (rs != null) {
            rs.close();
         }

      }

      return props;
   }

   public synchronized String getClientInfo(Connection conn, String name) throws SQLException {
      ResultSet rs = null;
      String clientInfo = null;

      try {
         this.getClientInfoSp.setString(1, name);
         this.getClientInfoSp.execute();
         rs = this.getClientInfoSp.getResultSet();
         if (rs.next()) {
            clientInfo = rs.getString(1);
         }
      } finally {
         if (rs != null) {
            rs.close();
         }

      }

      return clientInfo;
   }

   public synchronized void setClientInfo(Connection conn, Properties properties) throws SQLClientInfoException {
      try {
         Enumeration<?> propNames = properties.propertyNames();

         while(propNames.hasMoreElements()) {
            String name = (String)propNames.nextElement();
            String value = properties.getProperty(name);
            this.setClientInfo(conn, name, value);
         }

      } catch (SQLException var6) {
         SQLClientInfoException clientInfoEx = new SQLClientInfoException();
         clientInfoEx.initCause(var6);
         throw clientInfoEx;
      }
   }

   public synchronized void setClientInfo(Connection conn, String name, String value) throws SQLClientInfoException {
      try {
         this.setClientInfoSp.setString(1, name);
         this.setClientInfoSp.setString(2, value);
         this.setClientInfoSp.execute();
      } catch (SQLException var6) {
         SQLClientInfoException clientInfoEx = new SQLClientInfoException();
         clientInfoEx.initCause(var6);
         throw clientInfoEx;
      }
   }
}
