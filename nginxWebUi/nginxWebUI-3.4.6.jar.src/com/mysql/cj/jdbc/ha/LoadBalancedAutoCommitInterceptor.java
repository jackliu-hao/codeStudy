/*     */ package com.mysql.cj.jdbc.ha;
/*     */ 
/*     */ import com.mysql.cj.MysqlConnection;
/*     */ import com.mysql.cj.Query;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.interceptors.QueryInterceptor;
/*     */ import com.mysql.cj.jdbc.JdbcConnection;
/*     */ import com.mysql.cj.log.Log;
/*     */ import com.mysql.cj.protocol.ServerSession;
/*     */ import com.mysql.cj.util.StringUtils;
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
/*     */ public class LoadBalancedAutoCommitInterceptor
/*     */   implements QueryInterceptor
/*     */ {
/*  48 */   private int matchingAfterStatementCount = 0;
/*  49 */   private int matchingAfterStatementThreshold = 0;
/*     */   private String matchingAfterStatementRegex;
/*     */   private JdbcConnection conn;
/*  52 */   private LoadBalancedConnectionProxy proxy = null;
/*     */   
/*     */   private boolean countStatements = false;
/*     */ 
/*     */   
/*     */   public void destroy() {
/*  58 */     this.conn = null;
/*  59 */     this.proxy = null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean executeTopLevelOnly() {
/*  65 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public QueryInterceptor init(MysqlConnection connection, Properties props, Log log) {
/*  70 */     this.conn = (JdbcConnection)connection;
/*     */     
/*  72 */     String autoCommitSwapThresholdAsString = props.getProperty(PropertyKey.loadBalanceAutoCommitStatementThreshold.getKeyName(), "0");
/*     */     try {
/*  74 */       this.matchingAfterStatementThreshold = Integer.parseInt(autoCommitSwapThresholdAsString);
/*  75 */     } catch (NumberFormatException numberFormatException) {}
/*     */ 
/*     */     
/*  78 */     String autoCommitSwapRegex = props.getProperty(PropertyKey.loadBalanceAutoCommitStatementRegex.getKeyName(), "");
/*  79 */     if (!"".equals(autoCommitSwapRegex)) {
/*  80 */       this.matchingAfterStatementRegex = autoCommitSwapRegex;
/*     */     }
/*  82 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T extends com.mysql.cj.protocol.Resultset> T postProcess(Supplier<String> sql, Query interceptedQuery, T originalResultSet, ServerSession serverSession) {
/*     */     try {
/*  92 */       if (!this.countStatements || StringUtils.startsWithIgnoreCase(sql.get(), "SET") || StringUtils.startsWithIgnoreCase(sql.get(), "SHOW") || 
/*  93 */         StringUtils.startsWithIgnoreCase(sql.get(), "USE")) {
/*  94 */         return originalResultSet;
/*     */       }
/*     */ 
/*     */       
/*  98 */       if (!this.conn.getAutoCommit()) {
/*  99 */         this.matchingAfterStatementCount = 0;
/* 100 */         return originalResultSet;
/*     */       } 
/*     */       
/* 103 */       if (this.proxy == null && this.conn.isProxySet()) {
/* 104 */         JdbcConnection connParentProxy = this.conn.getMultiHostParentProxy();
/* 105 */         while (connParentProxy != null && !(connParentProxy instanceof LoadBalancedMySQLConnection)) {
/* 106 */           connParentProxy = connParentProxy.getMultiHostParentProxy();
/*     */         }
/* 108 */         if (connParentProxy != null) {
/* 109 */           this.proxy = ((LoadBalancedMySQLConnection)connParentProxy).getThisAsProxy();
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/* 114 */       if (this.proxy == null) {
/* 115 */         return originalResultSet;
/*     */       }
/*     */ 
/*     */       
/* 119 */       if (this.matchingAfterStatementRegex == null || ((String)sql.get()).matches(this.matchingAfterStatementRegex)) {
/* 120 */         this.matchingAfterStatementCount++;
/*     */       }
/*     */ 
/*     */       
/* 124 */       if (this.matchingAfterStatementCount >= this.matchingAfterStatementThreshold) {
/* 125 */         this.matchingAfterStatementCount = 0;
/*     */         try {
/* 127 */           this.proxy.pickNewConnection();
/* 128 */         } catch (SQLException sQLException) {}
/*     */       
/*     */       }
/*     */     
/*     */     }
/* 133 */     catch (SQLException ex) {
/* 134 */       throw ExceptionFactory.createException(ex.getMessage(), ex);
/*     */     } 
/*     */     
/* 137 */     return originalResultSet;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T extends com.mysql.cj.protocol.Resultset> T preProcess(Supplier<String> sql, Query interceptedQuery) {
/* 143 */     return null;
/*     */   }
/*     */   
/*     */   void pauseCounters() {
/* 147 */     this.countStatements = false;
/*     */   }
/*     */   
/*     */   void resumeCounters() {
/* 151 */     this.countStatements = true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\ha\LoadBalancedAutoCommitInterceptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */