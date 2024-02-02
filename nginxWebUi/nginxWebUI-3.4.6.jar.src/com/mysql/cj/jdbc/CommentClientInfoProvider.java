/*     */ package com.mysql.cj.jdbc;
/*     */ 
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLClientInfoException;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Enumeration;
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
/*     */ public class CommentClientInfoProvider
/*     */   implements ClientInfoProvider
/*     */ {
/*     */   private Properties clientInfo;
/*     */   
/*     */   public synchronized void initialize(Connection conn, Properties configurationProps) throws SQLException {
/*  48 */     this.clientInfo = new Properties();
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void destroy() throws SQLException {
/*  53 */     this.clientInfo = null;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized Properties getClientInfo(Connection conn) throws SQLException {
/*  58 */     return this.clientInfo;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized String getClientInfo(Connection conn, String name) throws SQLException {
/*  63 */     return this.clientInfo.getProperty(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void setClientInfo(Connection conn, Properties properties) throws SQLClientInfoException {
/*  68 */     this.clientInfo = new Properties();
/*     */     
/*  70 */     Enumeration<?> propNames = properties.propertyNames();
/*     */     
/*  72 */     while (propNames.hasMoreElements()) {
/*  73 */       String name = (String)propNames.nextElement();
/*     */       
/*  75 */       this.clientInfo.put(name, properties.getProperty(name));
/*     */     } 
/*     */     
/*  78 */     setComment(conn);
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void setClientInfo(Connection conn, String name, String value) throws SQLClientInfoException {
/*  83 */     this.clientInfo.setProperty(name, value);
/*  84 */     setComment(conn);
/*     */   }
/*     */   
/*     */   private synchronized void setComment(Connection conn) {
/*  88 */     StringBuilder commentBuf = new StringBuilder();
/*     */     
/*  90 */     Enumeration<?> propNames = this.clientInfo.propertyNames();
/*     */     
/*  92 */     while (propNames.hasMoreElements()) {
/*  93 */       String name = (String)propNames.nextElement();
/*     */       
/*  95 */       if (commentBuf.length() > 0) {
/*  96 */         commentBuf.append(", ");
/*     */       }
/*     */       
/*  99 */       commentBuf.append("" + name);
/* 100 */       commentBuf.append("=");
/* 101 */       commentBuf.append("" + this.clientInfo.getProperty(name));
/*     */     } 
/*     */     
/* 104 */     ((JdbcConnection)conn).setStatementComment(commentBuf.toString());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\CommentClientInfoProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */