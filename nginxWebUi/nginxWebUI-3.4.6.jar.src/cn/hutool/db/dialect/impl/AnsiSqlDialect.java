/*     */ package cn.hutool.db.dialect.impl;
/*     */ 
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.text.CharSequenceUtil;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.db.DbRuntimeException;
/*     */ import cn.hutool.db.Entity;
/*     */ import cn.hutool.db.Page;
/*     */ import cn.hutool.db.StatementUtil;
/*     */ import cn.hutool.db.dialect.Dialect;
/*     */ import cn.hutool.db.dialect.DialectName;
/*     */ import cn.hutool.db.sql.Condition;
/*     */ import cn.hutool.db.sql.Query;
/*     */ import cn.hutool.db.sql.SqlBuilder;
/*     */ import cn.hutool.db.sql.Wrapper;
/*     */ import java.sql.Connection;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AnsiSqlDialect
/*     */   implements Dialect
/*     */ {
/*     */   private static final long serialVersionUID = 2088101129774974580L;
/*  32 */   protected Wrapper wrapper = new Wrapper();
/*     */ 
/*     */   
/*     */   public Wrapper getWrapper() {
/*  36 */     return this.wrapper;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setWrapper(Wrapper wrapper) {
/*  41 */     this.wrapper = wrapper;
/*     */   }
/*     */ 
/*     */   
/*     */   public PreparedStatement psForInsert(Connection conn, Entity entity) throws SQLException {
/*  46 */     SqlBuilder insert = SqlBuilder.create(this.wrapper).insert(entity, dialectName());
/*     */     
/*  48 */     return StatementUtil.prepareStatement(conn, insert);
/*     */   }
/*     */ 
/*     */   
/*     */   public PreparedStatement psForInsertBatch(Connection conn, Entity... entities) throws SQLException {
/*  53 */     if (ArrayUtil.isEmpty((Object[])entities)) {
/*  54 */       throw new DbRuntimeException("Entities for batch insert is empty !");
/*     */     }
/*     */     
/*  57 */     SqlBuilder insert = SqlBuilder.create(this.wrapper).insert(entities[0], dialectName());
/*  58 */     Set<String> fields = (Set<String>)CollUtil.filter(entities[0].keySet(), CharSequenceUtil::isNotBlank);
/*  59 */     return StatementUtil.prepareStatementForBatch(conn, insert.build(), fields, entities);
/*     */   }
/*     */ 
/*     */   
/*     */   public PreparedStatement psForDelete(Connection conn, Query query) throws SQLException {
/*  64 */     Assert.notNull(query, "query must be not null !", new Object[0]);
/*     */     
/*  66 */     Condition[] where = query.getWhere();
/*  67 */     if (ArrayUtil.isEmpty((Object[])where))
/*     */     {
/*  69 */       throw new SQLException("No 'WHERE' condition, we can't prepared statement for delete everything.");
/*     */     }
/*  71 */     SqlBuilder delete = SqlBuilder.create(this.wrapper).delete(query.getFirstTableName()).where(where);
/*     */     
/*  73 */     return StatementUtil.prepareStatement(conn, delete);
/*     */   }
/*     */ 
/*     */   
/*     */   public PreparedStatement psForUpdate(Connection conn, Entity entity, Query query) throws SQLException {
/*  78 */     Assert.notNull(query, "query must be not null !", new Object[0]);
/*     */     
/*  80 */     Condition[] where = query.getWhere();
/*  81 */     if (ArrayUtil.isEmpty((Object[])where))
/*     */     {
/*  83 */       throw new SQLException("No 'WHERE' condition, we can't prepare statement for update everything.");
/*     */     }
/*     */     
/*  86 */     SqlBuilder update = SqlBuilder.create(this.wrapper).update(entity).where(where);
/*     */     
/*  88 */     return StatementUtil.prepareStatement(conn, update);
/*     */   }
/*     */ 
/*     */   
/*     */   public PreparedStatement psForFind(Connection conn, Query query) throws SQLException {
/*  93 */     return psForPage(conn, query);
/*     */   }
/*     */ 
/*     */   
/*     */   public PreparedStatement psForPage(Connection conn, Query query) throws SQLException {
/*  98 */     Assert.notNull(query, "query must be not null !", new Object[0]);
/*  99 */     if (StrUtil.hasBlank((CharSequence[])query.getTableNames())) {
/* 100 */       throw new DbRuntimeException("Table name must be not empty !");
/*     */     }
/*     */     
/* 103 */     SqlBuilder find = SqlBuilder.create(this.wrapper).query(query);
/* 104 */     return psForPage(conn, find, query.getPage());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public PreparedStatement psForPage(Connection conn, SqlBuilder sqlBuilder, Page page) throws SQLException {
/* 110 */     if (null != page) {
/* 111 */       sqlBuilder = wrapPageSql(sqlBuilder.orderBy(page.getOrders()), page);
/*     */     }
/* 113 */     return StatementUtil.prepareStatement(conn, sqlBuilder);
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
/*     */   protected SqlBuilder wrapPageSql(SqlBuilder find, Page page) {
/* 127 */     return find
/* 128 */       .append(" limit ")
/* 129 */       .append(Integer.valueOf(page.getPageSize()))
/* 130 */       .append(" offset ")
/* 131 */       .append(Integer.valueOf(page.getStartPosition()));
/*     */   }
/*     */ 
/*     */   
/*     */   public String dialectName() {
/* 136 */     return DialectName.ANSI.name();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\dialect\impl\AnsiSqlDialect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */