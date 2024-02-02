/*     */ package cn.hutool.db.dialect;
/*     */ 
/*     */ import cn.hutool.db.Entity;
/*     */ import cn.hutool.db.Page;
/*     */ import cn.hutool.db.sql.Query;
/*     */ import cn.hutool.db.sql.SqlBuilder;
/*     */ import cn.hutool.db.sql.Wrapper;
/*     */ import java.io.Serializable;
/*     */ import java.sql.Connection;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.SQLException;
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
/*     */ public interface Dialect
/*     */   extends Serializable
/*     */ {
/*     */   Wrapper getWrapper();
/*     */   
/*     */   void setWrapper(Wrapper paramWrapper);
/*     */   
/*     */   PreparedStatement psForInsert(Connection paramConnection, Entity paramEntity) throws SQLException;
/*     */   
/*     */   PreparedStatement psForInsertBatch(Connection paramConnection, Entity... paramVarArgs) throws SQLException;
/*     */   
/*     */   PreparedStatement psForDelete(Connection paramConnection, Query paramQuery) throws SQLException;
/*     */   
/*     */   PreparedStatement psForUpdate(Connection paramConnection, Entity paramEntity, Query paramQuery) throws SQLException;
/*     */   
/*     */   PreparedStatement psForFind(Connection paramConnection, Query paramQuery) throws SQLException;
/*     */   
/*     */   PreparedStatement psForPage(Connection paramConnection, Query paramQuery) throws SQLException;
/*     */   
/*     */   PreparedStatement psForPage(Connection paramConnection, SqlBuilder paramSqlBuilder, Page paramPage) throws SQLException;
/*     */   
/*     */   default PreparedStatement psForCount(Connection conn, Query query) throws SQLException {
/* 136 */     return psForCount(conn, SqlBuilder.create().query(query));
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
/*     */   default PreparedStatement psForCount(Connection conn, SqlBuilder sqlBuilder) throws SQLException {
/* 154 */     sqlBuilder = sqlBuilder.insertPreFragment("SELECT count(1) from(").append(") hutool_alias_count_");
/* 155 */     return psForPage(conn, sqlBuilder, null);
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
/*     */   PreparedStatement psForUpsert(Connection conn, Entity entity, String... keys) throws SQLException {
/* 170 */     throw new SQLException("Unsupported upsert operation of " + dialectName());
/*     */   }
/*     */   
/*     */   String dialectName();
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\dialect\Dialect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */