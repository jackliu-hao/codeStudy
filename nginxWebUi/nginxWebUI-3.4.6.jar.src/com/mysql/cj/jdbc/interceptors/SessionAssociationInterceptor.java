/*     */ package com.mysql.cj.jdbc.interceptors;
/*     */ 
/*     */ import com.mysql.cj.MysqlConnection;
/*     */ import com.mysql.cj.Query;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.interceptors.QueryInterceptor;
/*     */ import com.mysql.cj.jdbc.JdbcConnection;
/*     */ import com.mysql.cj.log.Log;
/*     */ import com.mysql.cj.protocol.ServerSession;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Properties;
/*     */ import java.util.function.Supplier;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SessionAssociationInterceptor
/*     */   implements QueryInterceptor
/*     */ {
/*     */   protected String currentSessionKey;
/*  49 */   protected static final ThreadLocal<String> sessionLocal = new ThreadLocal<>();
/*     */   private JdbcConnection connection;
/*     */   
/*     */   public static final void setSessionKey(String key) {
/*  53 */     sessionLocal.set(key);
/*     */   }
/*     */   
/*     */   public static final void resetSessionKey() {
/*  57 */     sessionLocal.set(null);
/*     */   }
/*     */   
/*     */   public static final String getSessionKey() {
/*  61 */     return sessionLocal.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean executeTopLevelOnly() {
/*  66 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public QueryInterceptor init(MysqlConnection conn, Properties props, Log log) {
/*  71 */     this.connection = (JdbcConnection)conn;
/*  72 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public <T extends com.mysql.cj.protocol.Resultset> T postProcess(Supplier<String> sql, Query interceptedQuery, T originalResultSet, ServerSession serverSession) {
/*  77 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public <T extends com.mysql.cj.protocol.Resultset> T preProcess(Supplier<String> sql, Query interceptedQuery) {
/*  82 */     String key = getSessionKey();
/*     */     
/*  84 */     if (key != null && !key.equals(this.currentSessionKey)) {
/*     */       
/*     */       try {
/*  87 */         PreparedStatement pstmt = this.connection.clientPrepareStatement("SET @mysql_proxy_session=?");
/*     */         
/*     */         try {
/*  90 */           pstmt.setString(1, key);
/*  91 */           pstmt.execute();
/*     */         } finally {
/*  93 */           pstmt.close();
/*     */         } 
/*  95 */       } catch (SQLException ex) {
/*  96 */         throw ExceptionFactory.createException(ex.getMessage(), ex);
/*     */       } 
/*     */       
/*  99 */       this.currentSessionKey = key;
/*     */     } 
/*     */     
/* 102 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void destroy() {
/* 107 */     this.connection = null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\interceptors\SessionAssociationInterceptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */