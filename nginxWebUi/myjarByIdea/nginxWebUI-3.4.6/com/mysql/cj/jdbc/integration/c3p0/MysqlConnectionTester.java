package com.mysql.cj.jdbc.integration.c3p0;

import com.mchange.v2.c3p0.C3P0ProxyConnection;
import com.mchange.v2.c3p0.QueryConnectionTester;
import com.mysql.cj.exceptions.CJCommunicationsException;
import com.mysql.cj.jdbc.JdbcConnection;
import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class MysqlConnectionTester implements QueryConnectionTester {
   private static final long serialVersionUID = 3256444690067896368L;
   private static final Object[] NO_ARGS_ARRAY = new Object[0];
   private transient Method pingMethod;

   public MysqlConnectionTester() {
      try {
         this.pingMethod = JdbcConnection.class.getMethod("ping", (Class[])null);
      } catch (Exception var2) {
      }

   }

   public int activeCheckConnection(Connection con) {
      try {
         if (this.pingMethod != null) {
            if (con instanceof JdbcConnection) {
               ((JdbcConnection)con).ping();
            } else {
               C3P0ProxyConnection castCon = (C3P0ProxyConnection)con;
               castCon.rawConnectionOperation(this.pingMethod, C3P0ProxyConnection.RAW_CONNECTION, NO_ARGS_ARRAY);
            }
         } else {
            Statement pingStatement = null;

            try {
               pingStatement = con.createStatement();
               pingStatement.executeQuery("SELECT 1").close();
            } finally {
               if (pingStatement != null) {
                  pingStatement.close();
               }

            }
         }

         return 0;
      } catch (Exception var7) {
         return -1;
      }
   }

   public int statusOnException(Connection arg0, Throwable throwable) {
      if (!(throwable instanceof CommunicationsException) && !(throwable instanceof CJCommunicationsException)) {
         if (throwable instanceof SQLException) {
            String sqlState = ((SQLException)throwable).getSQLState();
            return sqlState != null && sqlState.startsWith("08") ? -1 : 0;
         } else {
            return -1;
         }
      } else {
         return -1;
      }
   }

   public int activeCheckConnection(Connection arg0, String arg1) {
      return 0;
   }
}
