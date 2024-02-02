/*     */ package cn.hutool.db;
/*     */ 
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.map.MapUtil;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.db.dialect.Dialect;
/*     */ import cn.hutool.db.dialect.DialectFactory;
/*     */ import cn.hutool.db.handler.NumberHandler;
/*     */ import cn.hutool.db.handler.RsHandler;
/*     */ import cn.hutool.db.sql.Query;
/*     */ import cn.hutool.db.sql.SqlBuilder;
/*     */ import cn.hutool.db.sql.SqlExecutor;
/*     */ import cn.hutool.db.sql.SqlUtil;
/*     */ import cn.hutool.db.sql.Wrapper;
/*     */ import java.io.Serializable;
/*     */ import java.sql.Connection;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.SQLException;
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
/*     */ public class DialectRunner
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private Dialect dialect;
/*  35 */   protected boolean caseInsensitive = GlobalDbConfig.caseInsensitive;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DialectRunner(Dialect dialect) {
/*  43 */     this.dialect = dialect;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DialectRunner(String driverClassName) {
/*  52 */     this(DialectFactory.newDialect(driverClassName));
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
/*     */   public int[] insert(Connection conn, Entity... records) throws SQLException {
/*  68 */     checkConn(conn);
/*  69 */     if (ArrayUtil.isEmpty((Object[])records)) {
/*  70 */       return new int[] { 0 };
/*     */     }
/*     */     
/*  73 */     PreparedStatement ps = null;
/*     */     try {
/*  75 */       if (1 == records.length) {
/*     */         
/*  77 */         ps = this.dialect.psForInsert(conn, records[0]);
/*  78 */         return new int[] { ps.executeUpdate() };
/*     */       } 
/*     */ 
/*     */       
/*  82 */       ps = this.dialect.psForInsertBatch(conn, records);
/*  83 */       return ps.executeBatch();
/*     */     } finally {
/*  85 */       DbUtil.close(new Object[] { ps });
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
/*     */   public int upsert(Connection conn, Entity record, String... keys) throws SQLException {
/* 102 */     PreparedStatement ps = null;
/*     */     try {
/* 104 */       ps = getDialect().psForUpsert(conn, record, keys);
/* 105 */     } catch (SQLException sQLException) {}
/*     */ 
/*     */     
/* 108 */     if (null != ps) {
/*     */       try {
/* 110 */         return ps.executeUpdate();
/*     */       } finally {
/* 112 */         DbUtil.close(new Object[] { ps });
/*     */       } 
/*     */     }
/* 115 */     return insertOrUpdate(conn, record, keys);
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
/*     */   public int insertOrUpdate(Connection conn, Entity record, String... keys) throws SQLException {
/* 130 */     Entity where = record.filter(keys);
/* 131 */     if (MapUtil.isNotEmpty((Map)where) && count(conn, where) > 0L) {
/* 132 */       return update(conn, record, where);
/*     */     }
/* 134 */     return insert(conn, new Entity[] { record })[0];
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
/*     */   public <T> T insert(Connection conn, Entity record, RsHandler<T> generatedKeysHandler) throws SQLException {
/* 150 */     checkConn(conn);
/* 151 */     if (MapUtil.isEmpty((Map)record)) {
/* 152 */       throw new SQLException("Empty entity provided!");
/*     */     }
/*     */     
/* 155 */     PreparedStatement ps = null;
/*     */     try {
/* 157 */       ps = this.dialect.psForInsert(conn, record);
/* 158 */       ps.executeUpdate();
/* 159 */       if (null == generatedKeysHandler) {
/* 160 */         return null;
/*     */       }
/* 162 */       return (T)StatementUtil.getGeneratedKeys(ps, (RsHandler)generatedKeysHandler);
/*     */     } finally {
/* 164 */       DbUtil.close(new Object[] { ps });
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
/*     */   public int del(Connection conn, Entity where) throws SQLException {
/* 178 */     checkConn(conn);
/* 179 */     if (MapUtil.isEmpty((Map)where))
/*     */     {
/* 181 */       throw new SQLException("Empty entity provided!");
/*     */     }
/*     */     
/* 184 */     PreparedStatement ps = null;
/*     */     try {
/* 186 */       ps = this.dialect.psForDelete(conn, Query.of(where));
/* 187 */       return ps.executeUpdate();
/*     */     } finally {
/* 189 */       DbUtil.close(new Object[] { ps });
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
/*     */   public int update(Connection conn, Entity record, Entity where) throws SQLException {
/* 204 */     checkConn(conn);
/* 205 */     if (MapUtil.isEmpty((Map)record)) {
/* 206 */       throw new SQLException("Empty entity provided!");
/*     */     }
/* 208 */     if (MapUtil.isEmpty((Map)where))
/*     */     {
/* 210 */       throw new SQLException("Empty where provided!");
/*     */     }
/*     */ 
/*     */     
/* 214 */     String tableName = record.getTableName();
/* 215 */     if (StrUtil.isBlank(tableName)) {
/* 216 */       tableName = where.getTableName();
/* 217 */       record.setTableName(tableName);
/*     */     } 
/*     */     
/* 220 */     Query query = new Query(SqlUtil.buildConditions(where), new String[] { tableName });
/* 221 */     PreparedStatement ps = null;
/*     */     try {
/* 223 */       ps = this.dialect.psForUpdate(conn, record, query);
/* 224 */       return ps.executeUpdate();
/*     */     } finally {
/* 226 */       DbUtil.close(new Object[] { ps });
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
/*     */   public <T> T find(Connection conn, Query query, RsHandler<T> rsh) throws SQLException {
/* 242 */     checkConn(conn);
/* 243 */     Assert.notNull(query, "[query] is null !", new Object[0]);
/* 244 */     return (T)SqlExecutor.queryAndClosePs(this.dialect.psForFind(conn, query), rsh, new Object[0]);
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
/*     */   public long count(Connection conn, Entity where) throws SQLException {
/* 256 */     checkConn(conn);
/* 257 */     return ((Number)SqlExecutor.queryAndClosePs(this.dialect.psForCount(conn, Query.of(where)), (RsHandler)new NumberHandler(), new Object[0])).longValue();
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
/*     */   public long count(Connection conn, SqlBuilder sqlBuilder) throws SQLException {
/* 271 */     checkConn(conn);
/*     */     
/* 273 */     String selectSql = sqlBuilder.build();
/*     */     
/* 275 */     int orderByIndex = StrUtil.indexOfIgnoreCase(selectSql, " order by");
/* 276 */     if (orderByIndex > 0) {
/* 277 */       selectSql = StrUtil.subPre(selectSql, orderByIndex);
/*     */     }
/* 279 */     return ((Number)SqlExecutor.queryAndClosePs(this.dialect.psForCount(conn, 
/* 280 */           SqlBuilder.of(selectSql).addParams(sqlBuilder.getParamValueArray())), (RsHandler)new NumberHandler(), new Object[0]))
/* 281 */       .longValue();
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
/*     */   public <T> T page(Connection conn, Query query, RsHandler<T> rsh) throws SQLException {
/* 296 */     checkConn(conn);
/* 297 */     if (null == query.getPage()) {
/* 298 */       return find(conn, query, rsh);
/*     */     }
/*     */     
/* 301 */     return (T)SqlExecutor.queryAndClosePs(this.dialect.psForPage(conn, query), rsh, new Object[0]);
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
/*     */   public <T> T page(Connection conn, SqlBuilder sqlBuilder, Page page, RsHandler<T> rsh) throws SQLException {
/* 318 */     checkConn(conn);
/* 319 */     if (null == page) {
/* 320 */       return (T)SqlExecutor.query(conn, sqlBuilder, rsh);
/*     */     }
/*     */     
/* 323 */     return (T)SqlExecutor.queryAndClosePs(this.dialect.psForPage(conn, sqlBuilder, page), rsh, new Object[0]);
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
/*     */   public void setCaseInsensitive(boolean caseInsensitive) {
/* 337 */     this.caseInsensitive = caseInsensitive;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Dialect getDialect() {
/* 344 */     return this.dialect;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDialect(Dialect dialect) {
/* 353 */     this.dialect = dialect;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWrapper(Character wrapperChar) {
/* 362 */     setWrapper(new Wrapper(wrapperChar));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWrapper(Wrapper wrapper) {
/* 371 */     this.dialect.setWrapper(wrapper);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void checkConn(Connection conn) {
/* 377 */     Assert.notNull(conn, "Connection object must be not null!", new Object[0]);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\DialectRunner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */