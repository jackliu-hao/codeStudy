/*    */ package com.mysql.cj.jdbc.ha;
/*    */ 
/*    */ import com.mysql.cj.Messages;
/*    */ import com.mysql.cj.exceptions.CJException;
/*    */ import com.mysql.cj.jdbc.exceptions.SQLError;
/*    */ import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LoadBalancedMySQLConnection
/*    */   extends MultiHostMySQLConnection
/*    */   implements LoadBalancedConnection
/*    */ {
/*    */   public LoadBalancedMySQLConnection(LoadBalancedConnectionProxy proxy) {
/* 40 */     super(proxy);
/*    */   }
/*    */ 
/*    */   
/*    */   public LoadBalancedConnectionProxy getThisAsProxy() {
/* 45 */     return (LoadBalancedConnectionProxy)super.getThisAsProxy();
/*    */   }
/*    */   
/*    */   public void close() throws SQLException {
/*    */     
/* 50 */     try { getThisAsProxy().doClose(); return; }
/* 51 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*    */   
/*    */   } public void ping() throws SQLException {
/*    */     
/* 55 */     try { ping(true); return; }
/* 56 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*    */   
/*    */   } public void ping(boolean allConnections) throws SQLException {
/*    */     
/* 60 */     try { if (allConnections) {
/* 61 */         getThisAsProxy().doPing();
/*    */       } else {
/* 63 */         getActiveMySQLConnection().ping();
/*    */       }  return; }
/* 65 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*    */   
/*    */   } public boolean addHost(String host) throws SQLException {
/*    */     
/* 69 */     try { return getThisAsProxy().addHost(host); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*    */   
/*    */   }
/*    */   public void removeHost(String host) throws SQLException {
/*    */     
/* 74 */     try { getThisAsProxy().removeHost(host); return; }
/* 75 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*    */   
/*    */   } public void removeHostWhenNotInUse(String host) throws SQLException {
/*    */     
/* 79 */     try { getThisAsProxy().removeHostWhenNotInUse(host); return; }
/* 80 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*    */   
/*    */   }
/*    */   public boolean isWrapperFor(Class<?> iface) throws SQLException {
/*    */     
/* 85 */     try { return iface.isInstance(this); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*    */   
/*    */   }
/*    */   
/*    */   public <T> T unwrap(Class<T> iface) throws SQLException {
/*    */     try {
/*    */       try {
/* 92 */         return iface.cast(this);
/* 93 */       } catch (ClassCastException cce) {
/* 94 */         throw SQLError.createSQLException(Messages.getString("Common.UnableToUnwrap", new Object[] { iface.toString() }), "S1009", 
/* 95 */             getExceptionInterceptor());
/*    */       } 
/*    */     } catch (CJException cJException) {
/*    */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\ha\LoadBalancedMySQLConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */