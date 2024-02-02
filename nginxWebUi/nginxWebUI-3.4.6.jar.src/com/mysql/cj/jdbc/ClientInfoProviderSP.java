/*     */ package com.mysql.cj.jdbc;
/*     */ 
/*     */ import java.sql.Connection;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
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
/*     */ public class ClientInfoProviderSP
/*     */   implements ClientInfoProvider
/*     */ {
/*     */   public static final String PNAME_clientInfoSetSPName = "clientInfoSetSPName";
/*     */   public static final String PNAME_clientInfoGetSPName = "clientInfoGetSPName";
/*     */   public static final String PNAME_clientInfoGetBulkSPName = "clientInfoGetBulkSPName";
/*     */   public static final String PNAME_clientInfoDatabase = "clientInfoDatabase";
/*     */   PreparedStatement setClientInfoSp;
/*     */   PreparedStatement getClientInfoSp;
/*     */   PreparedStatement getClientInfoBulkSp;
/*     */   
/*     */   public synchronized void initialize(Connection conn, Properties configurationProps) throws SQLException {
/*  54 */     String identifierQuote = ((JdbcConnection)conn).getSession().getIdentifierQuoteString();
/*  55 */     String setClientInfoSpName = configurationProps.getProperty("clientInfoSetSPName", "setClientInfo");
/*  56 */     String getClientInfoSpName = configurationProps.getProperty("clientInfoGetSPName", "getClientInfo");
/*  57 */     String getClientInfoBulkSpName = configurationProps.getProperty("clientInfoGetBulkSPName", "getClientInfoBulk");
/*  58 */     String clientInfoDatabase = configurationProps.getProperty("clientInfoDatabase", "");
/*     */     
/*  60 */     String db = "".equals(clientInfoDatabase) ? ((JdbcConnection)conn).getDatabase() : clientInfoDatabase;
/*     */     
/*  62 */     this.setClientInfoSp = ((JdbcConnection)conn).clientPrepareStatement("CALL " + identifierQuote + db + identifierQuote + "." + identifierQuote + setClientInfoSpName + identifierQuote + "(?, ?)");
/*     */ 
/*     */     
/*  65 */     this.getClientInfoSp = ((JdbcConnection)conn).clientPrepareStatement("CALL" + identifierQuote + db + identifierQuote + "." + identifierQuote + getClientInfoSpName + identifierQuote + "(?)");
/*     */ 
/*     */     
/*  68 */     this.getClientInfoBulkSp = ((JdbcConnection)conn).clientPrepareStatement("CALL " + identifierQuote + db + identifierQuote + "." + identifierQuote + getClientInfoBulkSpName + identifierQuote + "()");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void destroy() throws SQLException {
/*  74 */     if (this.setClientInfoSp != null) {
/*  75 */       this.setClientInfoSp.close();
/*  76 */       this.setClientInfoSp = null;
/*     */     } 
/*     */     
/*  79 */     if (this.getClientInfoSp != null) {
/*  80 */       this.getClientInfoSp.close();
/*  81 */       this.getClientInfoSp = null;
/*     */     } 
/*     */     
/*  84 */     if (this.getClientInfoBulkSp != null) {
/*  85 */       this.getClientInfoBulkSp.close();
/*  86 */       this.getClientInfoBulkSp = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized Properties getClientInfo(Connection conn) throws SQLException {
/*  92 */     ResultSet rs = null;
/*     */     
/*  94 */     Properties props = new Properties();
/*     */     
/*     */     try {
/*  97 */       this.getClientInfoBulkSp.execute();
/*     */       
/*  99 */       rs = this.getClientInfoBulkSp.getResultSet();
/*     */       
/* 101 */       while (rs.next()) {
/* 102 */         props.setProperty(rs.getString(1), rs.getString(2));
/*     */       }
/*     */     } finally {
/* 105 */       if (rs != null) {
/* 106 */         rs.close();
/*     */       }
/*     */     } 
/*     */     
/* 110 */     return props;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized String getClientInfo(Connection conn, String name) throws SQLException {
/* 115 */     ResultSet rs = null;
/*     */     
/* 117 */     String clientInfo = null;
/*     */     
/*     */     try {
/* 120 */       this.getClientInfoSp.setString(1, name);
/* 121 */       this.getClientInfoSp.execute();
/*     */       
/* 123 */       rs = this.getClientInfoSp.getResultSet();
/*     */       
/* 125 */       if (rs.next()) {
/* 126 */         clientInfo = rs.getString(1);
/*     */       }
/*     */     } finally {
/* 129 */       if (rs != null) {
/* 130 */         rs.close();
/*     */       }
/*     */     } 
/*     */     
/* 134 */     return clientInfo;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void setClientInfo(Connection conn, Properties properties) throws SQLClientInfoException {
/*     */     try {
/* 140 */       Enumeration<?> propNames = properties.propertyNames();
/*     */       
/* 142 */       while (propNames.hasMoreElements()) {
/* 143 */         String name = (String)propNames.nextElement();
/* 144 */         String value = properties.getProperty(name);
/*     */         
/* 146 */         setClientInfo(conn, name, value);
/*     */       } 
/* 148 */     } catch (SQLException sqlEx) {
/* 149 */       SQLClientInfoException clientInfoEx = new SQLClientInfoException();
/* 150 */       clientInfoEx.initCause(sqlEx);
/*     */       
/* 152 */       throw clientInfoEx;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void setClientInfo(Connection conn, String name, String value) throws SQLClientInfoException {
/*     */     try {
/* 159 */       this.setClientInfoSp.setString(1, name);
/* 160 */       this.setClientInfoSp.setString(2, value);
/* 161 */       this.setClientInfoSp.execute();
/* 162 */     } catch (SQLException sqlEx) {
/* 163 */       SQLClientInfoException clientInfoEx = new SQLClientInfoException();
/* 164 */       clientInfoEx.initCause(sqlEx);
/*     */       
/* 166 */       throw clientInfoEx;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\ClientInfoProviderSP.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */