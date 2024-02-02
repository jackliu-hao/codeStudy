/*     */ package com.mysql.cj.jdbc.admin;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.jdbc.ConnectionImpl;
/*     */ import com.mysql.cj.jdbc.Driver;
/*     */ import com.mysql.cj.jdbc.JdbcConnection;
/*     */ import com.mysql.cj.jdbc.exceptions.SQLError;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Properties;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MiniAdmin
/*     */ {
/*     */   private JdbcConnection conn;
/*     */   
/*     */   public MiniAdmin(Connection conn) throws SQLException {
/*  57 */     if (conn == null) {
/*  58 */       throw SQLError.createSQLException(Messages.getString("MiniAdmin.0"), "S1000", null);
/*     */     }
/*     */     
/*  61 */     if (!(conn instanceof JdbcConnection)) {
/*  62 */       throw SQLError.createSQLException(Messages.getString("MiniAdmin.1"), "S1000", ((ConnectionImpl)conn)
/*  63 */           .getExceptionInterceptor());
/*     */     }
/*     */     
/*  66 */     this.conn = (JdbcConnection)conn;
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
/*     */   public MiniAdmin(String jdbcUrl) throws SQLException {
/*  79 */     this(jdbcUrl, new Properties());
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
/*     */   public MiniAdmin(String jdbcUrl, Properties props) throws SQLException {
/*  95 */     this.conn = (JdbcConnection)(new Driver()).connect(jdbcUrl, props);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void shutdown() throws SQLException {
/* 106 */     this.conn.shutdownServer();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\admin\MiniAdmin.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */