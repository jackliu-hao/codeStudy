/*     */ package com.mysql.cj.jdbc.integration.c3p0;
/*     */ 
/*     */ import com.mchange.v2.c3p0.C3P0ProxyConnection;
/*     */ import com.mchange.v2.c3p0.QueryConnectionTester;
/*     */ import com.mysql.cj.jdbc.JdbcConnection;
/*     */ import java.lang.reflect.Method;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class MysqlConnectionTester
/*     */   implements QueryConnectionTester
/*     */ {
/*     */   private static final long serialVersionUID = 3256444690067896368L;
/*  51 */   private static final Object[] NO_ARGS_ARRAY = new Object[0];
/*     */   
/*     */   private transient Method pingMethod;
/*     */   
/*     */   public MysqlConnectionTester() {
/*     */     try {
/*  57 */       this.pingMethod = JdbcConnection.class.getMethod("ping", (Class[])null);
/*  58 */     } catch (Exception exception) {}
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int activeCheckConnection(Connection con) {
/*     */     try {
/*  66 */       if (this.pingMethod != null) {
/*  67 */         if (con instanceof JdbcConnection) {
/*     */           
/*  69 */           ((JdbcConnection)con).ping();
/*     */         } else {
/*     */           
/*  72 */           C3P0ProxyConnection castCon = (C3P0ProxyConnection)con;
/*  73 */           castCon.rawConnectionOperation(this.pingMethod, C3P0ProxyConnection.RAW_CONNECTION, NO_ARGS_ARRAY);
/*     */         } 
/*     */       } else {
/*  76 */         Statement pingStatement = null;
/*     */         
/*     */         try {
/*  79 */           pingStatement = con.createStatement();
/*  80 */           pingStatement.executeQuery("SELECT 1").close();
/*     */         } finally {
/*  82 */           if (pingStatement != null) {
/*  83 */             pingStatement.close();
/*     */           }
/*     */         } 
/*     */       } 
/*     */       
/*  88 */       return 0;
/*  89 */     } catch (Exception ex) {
/*  90 */       return -1;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int statusOnException(Connection arg0, Throwable throwable) {
/*  96 */     if (throwable instanceof com.mysql.cj.jdbc.exceptions.CommunicationsException || throwable instanceof com.mysql.cj.exceptions.CJCommunicationsException) {
/*  97 */       return -1;
/*     */     }
/*     */     
/* 100 */     if (throwable instanceof SQLException) {
/* 101 */       String sqlState = ((SQLException)throwable).getSQLState();
/*     */       
/* 103 */       if (sqlState != null && sqlState.startsWith("08")) {
/* 104 */         return -1;
/*     */       }
/*     */       
/* 107 */       return 0;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 112 */     return -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public int activeCheckConnection(Connection arg0, String arg1) {
/* 117 */     return 0;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\integration\c3p0\MysqlConnectionTester.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */