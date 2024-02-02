package cn.hutool.db.sql;

import cn.hutool.core.collection.ArrayIter;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.db.DbUtil;
import cn.hutool.db.StatementUtil;
import cn.hutool.db.handler.RsHandler;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;

public class SqlExecutor {
   public static int execute(Connection conn, String sql, Map<String, Object> paramMap) throws SQLException {
      NamedSql namedSql = new NamedSql(sql, paramMap);
      return execute(conn, namedSql.getSql(), namedSql.getParams());
   }

   public static int execute(Connection conn, String sql, Object... params) throws SQLException {
      PreparedStatement ps = null;

      int var4;
      try {
         ps = StatementUtil.prepareStatement(conn, sql, params);
         var4 = ps.executeUpdate();
      } finally {
         DbUtil.close(ps);
      }

      return var4;
   }

   public static boolean call(Connection conn, String sql, Object... params) throws SQLException {
      CallableStatement call = null;

      boolean var4;
      try {
         call = StatementUtil.prepareCall(conn, sql, params);
         var4 = call.execute();
      } finally {
         DbUtil.close(call);
      }

      return var4;
   }

   public static ResultSet callQuery(Connection conn, String sql, Object... params) throws SQLException {
      return StatementUtil.prepareCall(conn, sql, params).executeQuery();
   }

   public static Long executeForGeneratedKey(Connection conn, String sql, Map<String, Object> paramMap) throws SQLException {
      NamedSql namedSql = new NamedSql(sql, paramMap);
      return executeForGeneratedKey(conn, namedSql.getSql(), namedSql.getParams());
   }

   public static Long executeForGeneratedKey(Connection conn, String sql, Object... params) throws SQLException {
      PreparedStatement ps = null;
      ResultSet rs = null;

      Long var5;
      try {
         ps = StatementUtil.prepareStatement(conn, sql, params);
         ps.executeUpdate();
         rs = ps.getGeneratedKeys();
         if (rs != null && rs.next()) {
            try {
               var5 = rs.getLong(1);
               return var5;
            } catch (SQLException var9) {
            }
         }

         var5 = null;
      } finally {
         DbUtil.close(ps);
         DbUtil.close(rs);
      }

      return var5;
   }

   /** @deprecated */
   @Deprecated
   public static int[] executeBatch(Connection conn, String sql, Object[]... paramsBatch) throws SQLException {
      return executeBatch(conn, sql, (Iterable)(new ArrayIter(paramsBatch)));
   }

   public static int[] executeBatch(Connection conn, String sql, Iterable<Object[]> paramsBatch) throws SQLException {
      PreparedStatement ps = null;

      int[] var4;
      try {
         ps = StatementUtil.prepareStatementForBatch(conn, sql, paramsBatch);
         var4 = ps.executeBatch();
      } finally {
         DbUtil.close(ps);
      }

      return var4;
   }

   public static int[] executeBatch(Connection conn, String... sqls) throws SQLException {
      return executeBatch(conn, (Iterable)(new ArrayIter(sqls)));
   }

   public static int[] executeBatch(Connection conn, Iterable<String> sqls) throws SQLException {
      Statement statement = null;

      try {
         statement = conn.createStatement();
         Iterator var3 = sqls.iterator();

         while(var3.hasNext()) {
            String sql = (String)var3.next();
            statement.addBatch(sql);
         }

         int[] var8 = statement.executeBatch();
         return var8;
      } finally {
         DbUtil.close(statement);
      }
   }

   public static <T> T query(Connection conn, String sql, RsHandler<T> rsh, Map<String, Object> paramMap) throws SQLException {
      NamedSql namedSql = new NamedSql(sql, paramMap);
      return query(conn, namedSql.getSql(), rsh, namedSql.getParams());
   }

   public static <T> T query(Connection conn, SqlBuilder sqlBuilder, RsHandler<T> rsh) throws SQLException {
      return query(conn, sqlBuilder.build(), rsh, sqlBuilder.getParamValueArray());
   }

   public static <T> T query(Connection conn, String sql, RsHandler<T> rsh, Object... params) throws SQLException {
      PreparedStatement ps = null;

      Object var5;
      try {
         ps = StatementUtil.prepareStatement(conn, sql, params);
         var5 = executeQuery(ps, rsh);
      } finally {
         DbUtil.close(ps);
      }

      return var5;
   }

   public static <T> T query(Connection conn, Func1<Connection, PreparedStatement> statementFunc, RsHandler<T> rsh) throws SQLException {
      PreparedStatement ps = null;

      Object var4;
      try {
         ps = (PreparedStatement)statementFunc.call(conn);
         var4 = executeQuery(ps, rsh);
      } catch (Exception var8) {
         if (var8 instanceof SQLException) {
            throw (SQLException)var8;
         }

         throw new RuntimeException(var8);
      } finally {
         DbUtil.close(ps);
      }

      return var4;
   }

   public static int executeUpdate(PreparedStatement ps, Object... params) throws SQLException {
      StatementUtil.fillParams(ps, params);
      return ps.executeUpdate();
   }

   public static boolean execute(PreparedStatement ps, Object... params) throws SQLException {
      StatementUtil.fillParams(ps, params);
      return ps.execute();
   }

   public static <T> T query(PreparedStatement ps, RsHandler<T> rsh, Object... params) throws SQLException {
      StatementUtil.fillParams(ps, params);
      return executeQuery(ps, rsh);
   }

   public static <T> T queryAndClosePs(PreparedStatement ps, RsHandler<T> rsh, Object... params) throws SQLException {
      Object var3;
      try {
         var3 = query(ps, rsh, params);
      } finally {
         DbUtil.close(ps);
      }

      return var3;
   }

   private static <T> T executeQuery(PreparedStatement ps, RsHandler<T> rsh) throws SQLException {
      ResultSet rs = null;

      Object var3;
      try {
         rs = ps.executeQuery();
         var3 = rsh.handle(rs);
      } finally {
         DbUtil.close(rs);
      }

      return var3;
   }
}
