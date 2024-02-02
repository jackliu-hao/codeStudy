/*     */ package cn.hutool.db.sql;
/*     */ 
/*     */ import cn.hutool.core.collection.ArrayIter;
/*     */ import cn.hutool.core.lang.func.Func1;
/*     */ import cn.hutool.db.DbUtil;
/*     */ import cn.hutool.db.StatementUtil;
/*     */ import cn.hutool.db.handler.RsHandler;
/*     */ import java.sql.CallableStatement;
/*     */ import java.sql.Connection;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SqlExecutor
/*     */ {
/*     */   public static int execute(Connection conn, String sql, Map<String, Object> paramMap) throws SQLException {
/*  39 */     NamedSql namedSql = new NamedSql(sql, paramMap);
/*  40 */     return execute(conn, namedSql.getSql(), namedSql.getParams());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int execute(Connection conn, String sql, Object... params) throws SQLException {
/*  55 */     PreparedStatement ps = null;
/*     */     try {
/*  57 */       ps = StatementUtil.prepareStatement(conn, sql, params);
/*  58 */       return ps.executeUpdate();
/*     */     } finally {
/*  60 */       DbUtil.close(new Object[] { ps });
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean call(Connection conn, String sql, Object... params) throws SQLException {
/*  75 */     CallableStatement call = null;
/*     */     try {
/*  77 */       call = StatementUtil.prepareCall(conn, sql, params);
/*  78 */       return call.execute();
/*     */     } finally {
/*  80 */       DbUtil.close(new Object[] { call });
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ResultSet callQuery(Connection conn, String sql, Object... params) throws SQLException {
/*  96 */     return StatementUtil.prepareCall(conn, sql, params).executeQuery();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Long executeForGeneratedKey(Connection conn, String sql, Map<String, Object> paramMap) throws SQLException {
/* 112 */     NamedSql namedSql = new NamedSql(sql, paramMap);
/* 113 */     return executeForGeneratedKey(conn, namedSql.getSql(), namedSql.getParams());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Long executeForGeneratedKey(Connection conn, String sql, Object... params) throws SQLException {
/* 128 */     PreparedStatement ps = null;
/* 129 */     ResultSet rs = null;
/*     */     try {
/* 131 */       ps = StatementUtil.prepareStatement(conn, sql, params);
/* 132 */       ps.executeUpdate();
/* 133 */       rs = ps.getGeneratedKeys();
/* 134 */       if (rs != null && rs.next()) {
/*     */         try {
/* 136 */           return Long.valueOf(rs.getLong(1));
/* 137 */         } catch (SQLException sQLException) {}
/*     */       }
/*     */ 
/*     */       
/* 141 */       return null;
/*     */     } finally {
/* 143 */       DbUtil.close(new Object[] { ps });
/* 144 */       DbUtil.close(new Object[] { rs });
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static int[] executeBatch(Connection conn, String sql, Object[]... paramsBatch) throws SQLException {
/* 162 */     return executeBatch(conn, sql, (Iterable<Object[]>)new ArrayIter((Object[])paramsBatch));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int[] executeBatch(Connection conn, String sql, Iterable<Object[]> paramsBatch) throws SQLException {
/* 177 */     PreparedStatement ps = null;
/*     */     try {
/* 179 */       ps = StatementUtil.prepareStatementForBatch(conn, sql, paramsBatch);
/* 180 */       return ps.executeBatch();
/*     */     } finally {
/* 182 */       DbUtil.close(new Object[] { ps });
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int[] executeBatch(Connection conn, String... sqls) throws SQLException {
/* 198 */     return executeBatch(conn, (Iterable<String>)new ArrayIter((Object[])sqls));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int[] executeBatch(Connection conn, Iterable<String> sqls) throws SQLException {
/* 213 */     Statement statement = null;
/*     */     try {
/* 215 */       statement = conn.createStatement();
/* 216 */       for (String sql : sqls) {
/* 217 */         statement.addBatch(sql);
/*     */       }
/* 219 */       return statement.executeBatch();
/*     */     } finally {
/* 221 */       DbUtil.close(new Object[] { statement });
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T query(Connection conn, String sql, RsHandler<T> rsh, Map<String, Object> paramMap) throws SQLException {
/* 239 */     NamedSql namedSql = new NamedSql(sql, paramMap);
/* 240 */     return query(conn, namedSql.getSql(), rsh, namedSql.getParams());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T query(Connection conn, SqlBuilder sqlBuilder, RsHandler<T> rsh) throws SQLException {
/* 256 */     return query(conn, sqlBuilder.build(), rsh, sqlBuilder.getParamValueArray());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T query(Connection conn, String sql, RsHandler<T> rsh, Object... params) throws SQLException {
/* 272 */     PreparedStatement ps = null;
/*     */     try {
/* 274 */       ps = StatementUtil.prepareStatement(conn, sql, params);
/* 275 */       return (T)executeQuery(ps, (RsHandler)rsh);
/*     */     } finally {
/* 277 */       DbUtil.close(new Object[] { ps });
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T query(Connection conn, Func1<Connection, PreparedStatement> statementFunc, RsHandler<T> rsh) throws SQLException {
/* 294 */     PreparedStatement ps = null;
/*     */     try {
/* 296 */       ps = (PreparedStatement)statementFunc.call(conn);
/* 297 */       return (T)executeQuery(ps, (RsHandler)rsh);
/* 298 */     } catch (Exception e) {
/* 299 */       if (e instanceof SQLException) {
/* 300 */         throw (SQLException)e;
/*     */       }
/* 302 */       throw new RuntimeException(e);
/*     */     } finally {
/* 304 */       DbUtil.close(new Object[] { ps });
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int executeUpdate(PreparedStatement ps, Object... params) throws SQLException {
/* 322 */     StatementUtil.fillParams(ps, params);
/* 323 */     return ps.executeUpdate();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean execute(PreparedStatement ps, Object... params) throws SQLException {
/* 337 */     StatementUtil.fillParams(ps, params);
/* 338 */     return ps.execute();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T query(PreparedStatement ps, RsHandler<T> rsh, Object... params) throws SQLException {
/* 353 */     StatementUtil.fillParams(ps, params);
/* 354 */     return executeQuery(ps, rsh);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T queryAndClosePs(PreparedStatement ps, RsHandler<T> rsh, Object... params) throws SQLException {
/*     */     try {
/* 369 */       return (T)query(ps, (RsHandler)rsh, params);
/*     */     } finally {
/* 371 */       DbUtil.close(new Object[] { ps });
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static <T> T executeQuery(PreparedStatement ps, RsHandler<T> rsh) throws SQLException {
/* 386 */     ResultSet rs = null;
/*     */     try {
/* 388 */       rs = ps.executeQuery();
/* 389 */       return (T)rsh.handle(rs);
/*     */     } finally {
/* 391 */       DbUtil.close(new Object[] { rs });
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\sql\SqlExecutor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */