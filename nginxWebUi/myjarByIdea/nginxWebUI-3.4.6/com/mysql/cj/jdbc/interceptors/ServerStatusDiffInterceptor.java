package com.mysql.cj.jdbc.interceptors;

import com.mysql.cj.MysqlConnection;
import com.mysql.cj.Query;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.interceptors.QueryInterceptor;
import com.mysql.cj.jdbc.JdbcConnection;
import com.mysql.cj.log.Log;
import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.protocol.ServerSession;
import com.mysql.cj.util.Util;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;

public class ServerStatusDiffInterceptor implements QueryInterceptor {
   private Map<String, String> preExecuteValues = new HashMap();
   private Map<String, String> postExecuteValues = new HashMap();
   private JdbcConnection connection;
   private Log log;

   public QueryInterceptor init(MysqlConnection conn, Properties props, Log l) {
      this.connection = (JdbcConnection)conn;
      this.log = l;
      return this;
   }

   public <T extends Resultset> T postProcess(Supplier<String> sql, Query interceptedQuery, T originalResultSet, ServerSession serverSession) {
      this.populateMapWithSessionStatusValues(this.postExecuteValues);
      this.log.logInfo("Server status change for query:\n" + Util.calculateDifferences(this.preExecuteValues, this.postExecuteValues));
      return null;
   }

   private void populateMapWithSessionStatusValues(Map<String, String> toPopulate) {
      try {
         Statement stmt = this.connection.createStatement();
         Throwable var3 = null;

         try {
            toPopulate.clear();
            ResultSet rs = stmt.executeQuery("SHOW SESSION STATUS");
            Throwable var5 = null;

            try {
               while(rs.next()) {
                  toPopulate.put(rs.getString(1), rs.getString(2));
               }
            } catch (Throwable var30) {
               var5 = var30;
               throw var30;
            } finally {
               if (rs != null) {
                  if (var5 != null) {
                     try {
                        rs.close();
                     } catch (Throwable var29) {
                        var5.addSuppressed(var29);
                     }
                  } else {
                     rs.close();
                  }
               }

            }
         } catch (Throwable var32) {
            var3 = var32;
            throw var32;
         } finally {
            if (stmt != null) {
               if (var3 != null) {
                  try {
                     stmt.close();
                  } catch (Throwable var28) {
                     var3.addSuppressed(var28);
                  }
               } else {
                  stmt.close();
               }
            }

         }

      } catch (SQLException var34) {
         throw ExceptionFactory.createException((String)var34.getMessage(), (Throwable)var34);
      }
   }

   public <T extends Resultset> T preProcess(Supplier<String> sql, Query interceptedQuery) {
      this.populateMapWithSessionStatusValues(this.preExecuteValues);
      return null;
   }

   public boolean executeTopLevelOnly() {
      return true;
   }

   public void destroy() {
      this.connection = null;
      this.log = null;
   }
}
