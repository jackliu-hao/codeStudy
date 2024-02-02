package com.mysql.cj.jdbc;

import java.sql.Connection;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.util.Properties;

public interface ClientInfoProvider {
   void initialize(Connection var1, Properties var2) throws SQLException;

   void destroy() throws SQLException;

   Properties getClientInfo(Connection var1) throws SQLException;

   String getClientInfo(Connection var1, String var2) throws SQLException;

   void setClientInfo(Connection var1, Properties var2) throws SQLClientInfoException;

   void setClientInfo(Connection var1, String var2, String var3) throws SQLClientInfoException;
}
