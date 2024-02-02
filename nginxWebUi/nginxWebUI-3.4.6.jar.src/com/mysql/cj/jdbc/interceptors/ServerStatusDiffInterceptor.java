/*     */ package com.mysql.cj.jdbc.interceptors;
/*     */ 
/*     */ import com.mysql.cj.MysqlConnection;
/*     */ import com.mysql.cj.Query;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.interceptors.QueryInterceptor;
/*     */ import com.mysql.cj.jdbc.JdbcConnection;
/*     */ import com.mysql.cj.log.Log;
/*     */ import com.mysql.cj.protocol.ServerSession;
/*     */ import com.mysql.cj.util.Util;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
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
/*     */ public class ServerStatusDiffInterceptor
/*     */   implements QueryInterceptor
/*     */ {
/*  52 */   private Map<String, String> preExecuteValues = new HashMap<>();
/*     */   
/*  54 */   private Map<String, String> postExecuteValues = new HashMap<>();
/*     */   
/*     */   private JdbcConnection connection;
/*     */   
/*     */   private Log log;
/*     */ 
/*     */   
/*     */   public QueryInterceptor init(MysqlConnection conn, Properties props, Log l) {
/*  62 */     this.connection = (JdbcConnection)conn;
/*  63 */     this.log = l;
/*  64 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T extends com.mysql.cj.protocol.Resultset> T postProcess(Supplier<String> sql, Query interceptedQuery, T originalResultSet, ServerSession serverSession) {
/*  70 */     populateMapWithSessionStatusValues(this.postExecuteValues);
/*     */     
/*  72 */     this.log.logInfo("Server status change for query:\n" + Util.calculateDifferences(this.preExecuteValues, this.postExecuteValues));
/*     */     
/*  74 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void populateMapWithSessionStatusValues(Map<String, String> toPopulate) {
/*  80 */     try (Statement stmt = this.connection.createStatement()) {
/*  81 */       toPopulate.clear();
/*     */       
/*  83 */       try (ResultSet rs = stmt.executeQuery("SHOW SESSION STATUS")) {
/*  84 */         while (rs.next()) {
/*  85 */           toPopulate.put(rs.getString(1), rs.getString(2));
/*     */         }
/*     */       }
/*     */     
/*  89 */     } catch (SQLException ex) {
/*  90 */       throw ExceptionFactory.createException(ex.getMessage(), ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T extends com.mysql.cj.protocol.Resultset> T preProcess(Supplier<String> sql, Query interceptedQuery) {
/*  97 */     populateMapWithSessionStatusValues(this.preExecuteValues);
/*     */     
/*  99 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean executeTopLevelOnly() {
/* 104 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void destroy() {
/* 109 */     this.connection = null;
/* 110 */     this.log = null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\interceptors\ServerStatusDiffInterceptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */