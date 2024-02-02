/*     */ package cn.hutool.db;
/*     */ 
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.db.dialect.Dialect;
/*     */ import cn.hutool.db.dialect.DialectFactory;
/*     */ import cn.hutool.db.handler.EntityListHandler;
/*     */ import cn.hutool.db.handler.HandleHelper;
/*     */ import cn.hutool.db.handler.PageResultHandler;
/*     */ import cn.hutool.db.handler.RsHandler;
/*     */ import cn.hutool.db.sql.Condition;
/*     */ import cn.hutool.db.sql.Query;
/*     */ import cn.hutool.db.sql.SqlBuilder;
/*     */ import cn.hutool.db.sql.SqlUtil;
/*     */ import java.lang.invoke.SerializedLambda;
/*     */ import java.sql.Connection;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import javax.sql.DataSource;
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
/*     */ public class SqlConnRunner
/*     */   extends DialectRunner
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   
/*     */   public static SqlConnRunner create(Dialect dialect) {
/*  39 */     return new SqlConnRunner(dialect);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SqlConnRunner create(DataSource ds) {
/*  49 */     return new SqlConnRunner(DialectFactory.getDialect(ds));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SqlConnRunner create(String driverClassName) {
/*  59 */     return new SqlConnRunner(driverClassName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SqlConnRunner(Dialect dialect) {
/*  70 */     super(dialect);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SqlConnRunner(String driverClassName) {
/*  79 */     super(driverClassName);
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
/*     */   
/*     */   public int[] insert(Connection conn, Collection<Entity> records) throws SQLException {
/*  97 */     return insert(conn, records.<Entity>toArray(new Entity[0]));
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
/*     */   public int insert(Connection conn, Entity record) throws SQLException {
/* 110 */     return insert(conn, new Entity[] { record })[0];
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
/*     */   public List<Object> insertForGeneratedKeys(Connection conn, Entity record) throws SQLException {
/* 123 */     return insert(conn, record, HandleHelper::handleRowToList);
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
/*     */   public Long insertForGeneratedKey(Connection conn, Entity record) throws SQLException {
/* 136 */     return insert(conn, record, rs -> {
/*     */           Long generatedKey = null;
/*     */           if (rs != null && rs.next()) {
/*     */             try {
/*     */               generatedKey = Long.valueOf(rs.getLong(1));
/* 141 */             } catch (SQLException sQLException) {}
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T find(Connection conn, Collection<String> fields, Entity where, RsHandler<T> rsh) throws SQLException {
/* 162 */     return find(conn, Query.of(where).setFields(fields), rsh);
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
/*     */   public <T> T find(Connection conn, Entity where, RsHandler<T> rsh, String... fields) throws SQLException {
/* 178 */     return find(conn, CollUtil.newArrayList((Object[])fields), where, rsh);
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
/*     */   public List<Entity> find(Connection conn, Entity where) throws SQLException {
/* 191 */     return find(conn, where.getFieldNames(), where, (RsHandler<List<Entity>>)new EntityListHandler(this.caseInsensitive));
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
/*     */   public List<Entity> findAll(Connection conn, Entity where) throws SQLException {
/* 203 */     return find(conn, where, (RsHandler<List<Entity>>)new EntityListHandler(this.caseInsensitive), new String[0]);
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
/*     */   public List<Entity> findAll(Connection conn, String tableName) throws SQLException {
/* 215 */     return findAll(conn, Entity.create(tableName));
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
/*     */   public List<Entity> findBy(Connection conn, String tableName, String field, Object value) throws SQLException {
/* 229 */     return findAll(conn, Entity.create(tableName).set(field, value));
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
/*     */   public List<Entity> findLike(Connection conn, String tableName, String field, String value, Condition.LikeType likeType) throws SQLException {
/* 244 */     return findAll(conn, Entity.create(tableName).set(field, SqlUtil.buildLikeValue(value, likeType, true)));
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
/*     */   public List<Entity> findIn(Connection conn, String tableName, String field, Object... values) throws SQLException {
/* 258 */     return findAll(conn, Entity.create(tableName).set(field, values));
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
/*     */   public long count(Connection conn, CharSequence selectSql, Object... params) throws SQLException {
/* 272 */     return count(conn, SqlBuilder.of(selectSql).addParams(params));
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
/*     */   
/*     */   public <T> T page(Connection conn, Collection<String> fields, Entity where, int pageNumber, int numPerPage, RsHandler<T> rsh) throws SQLException {
/* 290 */     return page(conn, Query.of(where).setFields(fields).setPage(new Page(pageNumber, numPerPage)), rsh);
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
/*     */   public PageResult<Entity> page(Connection conn, SqlBuilder sqlBuilder, Page page) throws SQLException {
/* 306 */     PageResultHandler pageResultHandler = new PageResultHandler(new PageResult(page.getPageNumber(), page.getPageSize(), (int)count(conn, sqlBuilder)), this.caseInsensitive);
/*     */     
/* 308 */     return page(conn, sqlBuilder, page, (RsHandler<PageResult<Entity>>)pageResultHandler);
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
/*     */   public PageResult<Entity> page(Connection conn, Collection<String> fields, Entity where, int page, int numPerPage) throws SQLException {
/* 324 */     return page(conn, fields, where, new Page(page, numPerPage));
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
/*     */   public PageResult<Entity> page(Connection conn, Entity where, Page page) throws SQLException {
/* 338 */     return page(conn, (Collection<String>)null, where, page);
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
/*     */   public PageResult<Entity> page(Connection conn, Collection<String> fields, Entity where, Page page) throws SQLException {
/* 354 */     PageResultHandler pageResultHandler = new PageResultHandler(new PageResult(page.getPageNumber(), page.getPageSize(), (int)count(conn, where)), this.caseInsensitive);
/*     */     
/* 356 */     return page(conn, fields, where, page, (RsHandler<PageResult<Entity>>)pageResultHandler);
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
/*     */   public <T> T page(Connection conn, Collection<String> fields, Entity where, Page page, RsHandler<T> handler) throws SQLException {
/* 373 */     return page(conn, Query.of(where).setFields(fields).setPage(page), handler);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\SqlConnRunner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */