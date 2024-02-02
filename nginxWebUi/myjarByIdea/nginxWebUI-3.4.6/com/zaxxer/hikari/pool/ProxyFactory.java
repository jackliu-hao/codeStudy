package com.zaxxer.hikari.pool;

import com.zaxxer.hikari.util.FastList;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public final class ProxyFactory {
   private ProxyFactory() {
   }

   static ProxyConnection getProxyConnection(PoolEntry var0, Connection var1, FastList<Statement> var2, ProxyLeakTask var3, long var4, boolean var6, boolean var7) {
      return new HikariProxyConnection(var0, var1, var2, var3, var4, var6, var7);
   }

   static Statement getProxyStatement(ProxyConnection var0, Statement var1) {
      return new HikariProxyStatement(var0, var1);
   }

   static CallableStatement getProxyCallableStatement(ProxyConnection var0, CallableStatement var1) {
      return new HikariProxyCallableStatement(var0, var1);
   }

   static PreparedStatement getProxyPreparedStatement(ProxyConnection var0, PreparedStatement var1) {
      return new HikariProxyPreparedStatement(var0, var1);
   }

   static ResultSet getProxyResultSet(ProxyConnection var0, ProxyStatement var1, ResultSet var2) {
      return new HikariProxyResultSet(var0, var1, var2);
   }

   static DatabaseMetaData getProxyDatabaseMetaData(ProxyConnection var0, DatabaseMetaData var1) {
      return new HikariProxyDatabaseMetaData(var0, var1);
   }
}
