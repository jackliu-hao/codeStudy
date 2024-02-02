/*     */ package cn.hutool.db;
/*     */ 
/*     */ import cn.hutool.core.collection.ArrayIter;
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.convert.Convert;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.db.handler.HandleHelper;
/*     */ import cn.hutool.db.handler.RsHandler;
/*     */ import cn.hutool.db.sql.NamedSql;
/*     */ import cn.hutool.db.sql.SqlBuilder;
/*     */ import cn.hutool.db.sql.SqlLog;
/*     */ import cn.hutool.db.sql.SqlUtil;
/*     */ import java.lang.invoke.SerializedLambda;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.sql.CallableStatement;
/*     */ import java.sql.Connection;
/*     */ import java.sql.Date;
/*     */ import java.sql.ParameterMetaData;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.sql.Time;
/*     */ import java.util.Collection;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
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
/*     */ public class StatementUtil
/*     */ {
/*     */   public static PreparedStatement fillParams(PreparedStatement ps, Object... params) throws SQLException {
/*  47 */     if (ArrayUtil.isEmpty(params)) {
/*  48 */       return ps;
/*     */     }
/*  50 */     return fillParams(ps, (Iterable<?>)new ArrayIter(params));
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
/*     */   public static PreparedStatement fillParams(PreparedStatement ps, Iterable<?> params) throws SQLException {
/*  63 */     return fillParams(ps, params, null);
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
/*     */   public static PreparedStatement fillParams(PreparedStatement ps, Iterable<?> params, Map<Integer, Integer> nullTypeCache) throws SQLException {
/*  78 */     if (null == params) {
/*  79 */       return ps;
/*     */     }
/*     */     
/*  82 */     int paramIndex = 1;
/*  83 */     for (Object param : params) {
/*  84 */       setParam(ps, paramIndex++, param, nullTypeCache);
/*     */     }
/*  86 */     return ps;
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
/*     */   public static PreparedStatement prepareStatement(Connection conn, SqlBuilder sqlBuilder) throws SQLException {
/*  99 */     return prepareStatement(conn, sqlBuilder.build(), sqlBuilder.getParamValueArray());
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
/*     */   public static PreparedStatement prepareStatement(Connection conn, String sql, Collection<Object> params) throws SQLException {
/* 113 */     return prepareStatement(conn, sql, params.toArray(new Object[0]));
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
/*     */   public static PreparedStatement prepareStatement(Connection conn, String sql, Object... params) throws SQLException {
/*     */     PreparedStatement ps;
/* 127 */     Assert.notBlank(sql, "Sql String must be not blank!", new Object[0]);
/* 128 */     sql = sql.trim();
/*     */     
/* 130 */     if (ArrayUtil.isNotEmpty(params) && 1 == params.length && params[0] instanceof Map) {
/*     */       
/* 132 */       NamedSql namedSql = new NamedSql(sql, Convert.toMap(String.class, Object.class, params[0]));
/* 133 */       sql = namedSql.getSql();
/* 134 */       params = namedSql.getParams();
/*     */     } 
/*     */     
/* 137 */     SqlLog.INSTANCE.log(sql, ArrayUtil.isEmpty(params) ? null : params);
/*     */     
/* 139 */     if (GlobalDbConfig.returnGeneratedKey && StrUtil.startWithIgnoreCase(sql, "insert")) {
/*     */       
/* 141 */       ps = conn.prepareStatement(sql, 1);
/*     */     } else {
/* 143 */       ps = conn.prepareStatement(sql);
/*     */     } 
/* 145 */     return fillParams(ps, params);
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
/*     */   public static PreparedStatement prepareStatementForBatch(Connection conn, String sql, Object[]... paramsBatch) throws SQLException {
/* 159 */     return prepareStatementForBatch(conn, sql, (Iterable<Object[]>)new ArrayIter((Object[])paramsBatch));
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
/*     */   public static PreparedStatement prepareStatementForBatch(Connection conn, String sql, Iterable<Object[]> paramsBatch) throws SQLException {
/* 173 */     Assert.notBlank(sql, "Sql String must be not blank!", new Object[0]);
/*     */     
/* 175 */     sql = sql.trim();
/* 176 */     SqlLog.INSTANCE.log(sql, paramsBatch);
/* 177 */     PreparedStatement ps = conn.prepareStatement(sql);
/* 178 */     Map<Integer, Integer> nullTypeMap = new HashMap<>();
/* 179 */     for (Object[] params : paramsBatch) {
/* 180 */       fillParams(ps, (Iterable<?>)new ArrayIter(params), nullTypeMap);
/* 181 */       ps.addBatch();
/*     */     } 
/* 183 */     return ps;
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
/*     */   public static PreparedStatement prepareStatementForBatch(Connection conn, String sql, Iterable<String> fields, Entity... entities) throws SQLException {
/* 198 */     Assert.notBlank(sql, "Sql String must be not blank!", new Object[0]);
/*     */     
/* 200 */     sql = sql.trim();
/* 201 */     SqlLog.INSTANCE.logForBatch(sql);
/* 202 */     PreparedStatement ps = conn.prepareStatement(sql);
/*     */     
/* 204 */     Map<Integer, Integer> nullTypeMap = new HashMap<>();
/* 205 */     for (Entity entity : entities) {
/* 206 */       fillParams(ps, CollUtil.valuesOfKeys((Map)entity, fields), nullTypeMap);
/* 207 */       ps.addBatch();
/*     */     } 
/* 209 */     return ps;
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
/*     */   public static CallableStatement prepareCall(Connection conn, String sql, Object... params) throws SQLException {
/* 223 */     Assert.notBlank(sql, "Sql String must be not blank!", new Object[0]);
/*     */     
/* 225 */     sql = sql.trim();
/* 226 */     SqlLog.INSTANCE.log(sql, params);
/* 227 */     CallableStatement call = conn.prepareCall(sql);
/* 228 */     fillParams(call, params);
/* 229 */     return call;
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
/*     */   public static Long getGeneratedKeyOfLong(Statement ps) throws SQLException {
/* 241 */     return getGeneratedKeys(ps, rs -> {
/*     */           Long generatedKey = null;
/*     */           if (rs != null && rs.next()) {
/*     */             try {
/*     */               generatedKey = Long.valueOf(rs.getLong(1));
/* 246 */             } catch (SQLException sQLException) {}
/*     */           }
/*     */           return generatedKey;
/*     */         });
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
/*     */   public static List<Object> getGeneratedKeys(Statement ps) throws SQLException {
/* 262 */     return getGeneratedKeys(ps, HandleHelper::handleRowToList);
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
/*     */   public static <T> T getGeneratedKeys(Statement statement, RsHandler<T> rsHandler) throws SQLException {
/* 275 */     try (ResultSet rs = statement.getGeneratedKeys()) {
/* 276 */       return (T)rsHandler.handle(rs);
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
/*     */   public static int getTypeOfNull(PreparedStatement ps, int paramIndex) {
/* 290 */     int sqlType = 12;
/*     */ 
/*     */     
/*     */     try {
/* 294 */       ParameterMetaData pmd = ps.getParameterMetaData();
/* 295 */       sqlType = pmd.getParameterType(paramIndex);
/* 296 */     } catch (SQLException sQLException) {}
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 301 */     return sqlType;
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
/*     */   public static void setParam(PreparedStatement ps, int paramIndex, Object param) throws SQLException {
/* 314 */     setParam(ps, paramIndex, param, null);
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
/*     */   private static void setParam(PreparedStatement ps, int paramIndex, Object param, Map<Integer, Integer> nullTypeCache) throws SQLException {
/* 330 */     if (null == param) {
/* 331 */       Integer type = (null == nullTypeCache) ? null : nullTypeCache.get(Integer.valueOf(paramIndex));
/* 332 */       if (null == type) {
/* 333 */         type = Integer.valueOf(getTypeOfNull(ps, paramIndex));
/* 334 */         if (null != nullTypeCache) {
/* 335 */           nullTypeCache.put(Integer.valueOf(paramIndex), type);
/*     */         }
/*     */       } 
/* 338 */       ps.setNull(paramIndex, type.intValue());
/*     */     } 
/*     */ 
/*     */     
/* 342 */     if (param instanceof Date) {
/* 343 */       if (param instanceof Date) {
/* 344 */         ps.setDate(paramIndex, (Date)param);
/* 345 */       } else if (param instanceof Time) {
/* 346 */         ps.setTime(paramIndex, (Time)param);
/*     */       } else {
/* 348 */         ps.setTimestamp(paramIndex, SqlUtil.toSqlTimestamp((Date)param));
/*     */       } 
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 354 */     if (param instanceof Number) {
/* 355 */       if (param instanceof BigDecimal) {
/*     */         
/* 357 */         ps.setBigDecimal(paramIndex, (BigDecimal)param);
/*     */         return;
/*     */       } 
/* 360 */       if (param instanceof BigInteger) {
/*     */         
/* 362 */         ps.setBigDecimal(paramIndex, new BigDecimal((BigInteger)param));
/*     */ 
/*     */         
/*     */         return;
/*     */       } 
/*     */     } 
/*     */     
/* 369 */     ps.setObject(paramIndex, param);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\StatementUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */