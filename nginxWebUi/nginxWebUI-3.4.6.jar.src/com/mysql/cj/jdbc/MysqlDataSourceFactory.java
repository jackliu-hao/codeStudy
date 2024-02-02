/*     */ package com.mysql.cj.jdbc;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import java.util.Hashtable;
/*     */ import javax.naming.Context;
/*     */ import javax.naming.Name;
/*     */ import javax.naming.RefAddr;
/*     */ import javax.naming.Reference;
/*     */ import javax.naming.spi.ObjectFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MysqlDataSourceFactory
/*     */   implements ObjectFactory
/*     */ {
/*  50 */   protected static final String DATA_SOURCE_CLASS_NAME = MysqlDataSource.class.getName();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  55 */   protected static final String POOL_DATA_SOURCE_CLASS_NAME = MysqlConnectionPoolDataSource.class.getName();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  61 */   protected static final String XA_DATA_SOURCE_CLASS_NAME = MysqlXADataSource.class.getName();
/*     */ 
/*     */   
/*     */   public Object getObjectInstance(Object refObj, Name nm, Context ctx, Hashtable<?, ?> env) throws Exception {
/*  65 */     Reference ref = (Reference)refObj;
/*  66 */     String className = ref.getClassName();
/*     */     
/*  68 */     if (className != null && (className
/*  69 */       .equals(DATA_SOURCE_CLASS_NAME) || className.equals(POOL_DATA_SOURCE_CLASS_NAME) || className.equals(XA_DATA_SOURCE_CLASS_NAME))) {
/*  70 */       MysqlDataSource dataSource = null;
/*     */       
/*     */       try {
/*  73 */         dataSource = (MysqlDataSource)Class.forName(className).newInstance();
/*  74 */       } catch (Exception ex) {
/*  75 */         throw new RuntimeException(Messages.getString("MysqlDataSourceFactory.0", new Object[] { className, ex.toString() }));
/*     */       } 
/*     */       
/*  78 */       int portNumber = 3306;
/*     */       
/*  80 */       String portNumberAsString = nullSafeRefAddrStringGet("port", ref);
/*     */       
/*  82 */       if (portNumberAsString != null) {
/*  83 */         portNumber = Integer.parseInt(portNumberAsString);
/*     */       }
/*     */       
/*  86 */       dataSource.setPort(portNumber);
/*     */       
/*  88 */       String user = nullSafeRefAddrStringGet(PropertyKey.USER.getKeyName(), ref);
/*     */       
/*  90 */       if (user != null) {
/*  91 */         dataSource.setUser(user);
/*     */       }
/*     */       
/*  94 */       String password = nullSafeRefAddrStringGet(PropertyKey.PASSWORD.getKeyName(), ref);
/*     */       
/*  96 */       if (password != null) {
/*  97 */         dataSource.setPassword(password);
/*     */       }
/*     */       
/* 100 */       String serverName = nullSafeRefAddrStringGet("serverName", ref);
/*     */       
/* 102 */       if (serverName != null) {
/* 103 */         dataSource.setServerName(serverName);
/*     */       }
/*     */       
/* 106 */       String databaseName = nullSafeRefAddrStringGet("databaseName", ref);
/*     */       
/* 108 */       if (databaseName != null) {
/* 109 */         dataSource.setDatabaseName(databaseName);
/*     */       }
/*     */       
/* 112 */       String explicitUrlAsString = nullSafeRefAddrStringGet("explicitUrl", ref);
/*     */       
/* 114 */       if (explicitUrlAsString != null && 
/* 115 */         Boolean.valueOf(explicitUrlAsString).booleanValue()) {
/* 116 */         dataSource.setUrl(nullSafeRefAddrStringGet("url", ref));
/*     */       }
/*     */ 
/*     */       
/* 120 */       dataSource.setPropertiesViaRef(ref);
/*     */       
/* 122 */       return dataSource;
/*     */     } 
/*     */ 
/*     */     
/* 126 */     return null;
/*     */   }
/*     */   
/*     */   private String nullSafeRefAddrStringGet(String referenceName, Reference ref) {
/* 130 */     RefAddr refAddr = ref.get(referenceName);
/*     */     
/* 132 */     String asString = (refAddr != null) ? (String)refAddr.getContent() : null;
/*     */     
/* 134 */     return asString;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\MysqlDataSourceFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */