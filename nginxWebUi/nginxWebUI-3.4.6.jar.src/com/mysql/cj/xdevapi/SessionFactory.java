/*     */ package com.mysql.cj.xdevapi;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.conf.ConnectionUrl;
/*     */ import com.mysql.cj.conf.HostInfo;
/*     */ import com.mysql.cj.conf.PropertyDefinitions;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.exceptions.CJCommunicationsException;
/*     */ import com.mysql.cj.exceptions.CJException;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.InvalidConnectionAttributeException;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SessionFactory
/*     */ {
/*     */   protected ConnectionUrl parseUrl(String url) {
/*  66 */     ConnectionUrl connUrl = ConnectionUrl.getConnectionUrlInstance(url, null);
/*  67 */     if (connUrl == null || (connUrl.getType() != ConnectionUrl.Type.XDEVAPI_SESSION && connUrl.getType() != ConnectionUrl.Type.XDEVAPI_DNS_SRV_SESSION)) {
/*  68 */       throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, "Initialization via URL failed for \"" + url + "\"");
/*     */     }
/*  70 */     return connUrl;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Session getSession(ConnectionUrl connUrl) {
/*     */     CJCommunicationsException cJCommunicationsException;
/*  81 */     CJException latestException = null;
/*  82 */     List<HostInfo> hostsList = connUrl.getHostsList();
/*  83 */     for (HostInfo hi : hostsList) {
/*     */       try {
/*  85 */         return new SessionImpl(hi);
/*  86 */       } catch (CJCommunicationsException e) {
/*  87 */         if (e.getCause() == null) {
/*  88 */           throw e;
/*     */         }
/*  90 */         cJCommunicationsException = e;
/*     */       } 
/*     */     } 
/*  93 */     if (cJCommunicationsException != null) {
/*  94 */       throw (CJCommunicationsException)ExceptionFactory.createException(CJCommunicationsException.class, Messages.getString("Session.Create.Failover.0"), cJCommunicationsException);
/*     */     }
/*  96 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Session getSession(String url) {
/* 107 */     return getSession(parseUrl(url));
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
/*     */   public Session getSession(Properties properties) {
/* 119 */     if (properties.containsKey(PropertyKey.xdevapiDnsSrv.getKeyName()) && ((Boolean)PropertyDefinitions.getPropertyDefinition(PropertyKey.xdevapiDnsSrv)
/* 120 */       .parseObject(properties.getProperty(PropertyKey.xdevapiDnsSrv.getKeyName()), null)).booleanValue()) {
/*     */       
/* 122 */       ConnectionUrl connectionUrl = ConnectionUrl.getConnectionUrlInstance(ConnectionUrl.Type.XDEVAPI_DNS_SRV_SESSION.getScheme(), properties);
/* 123 */       return getSession(connectionUrl);
/*     */     } 
/*     */     
/* 126 */     ConnectionUrl connUrl = ConnectionUrl.getConnectionUrlInstance(ConnectionUrl.Type.XDEVAPI_SESSION.getScheme(), properties);
/* 127 */     return new SessionImpl(connUrl.getMainHost());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\SessionFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */