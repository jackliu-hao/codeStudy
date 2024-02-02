/*    */ package com.mysql.cj.jdbc.ha;
/*    */ 
/*    */ import com.mysql.cj.jdbc.ConnectionImpl;
/*    */ import com.mysql.cj.jdbc.JdbcConnection;
/*    */ import com.mysql.cj.util.StringUtils;
/*    */ import java.lang.reflect.InvocationHandler;
/*    */ import java.sql.SQLException;
/*    */ import java.util.List;
/*    */ import java.util.Map;
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
/*    */ 
/*    */ 
/*    */ public class ServerAffinityStrategy
/*    */   extends RandomBalanceStrategy
/*    */ {
/* 42 */   public String[] affinityOrderedServers = null;
/*    */ 
/*    */   
/*    */   public ServerAffinityStrategy(String affinityOrdervers) {
/* 46 */     if (!StringUtils.isNullOrEmpty(affinityOrdervers)) {
/* 47 */       this.affinityOrderedServers = affinityOrdervers.split(",");
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public ConnectionImpl pickConnection(InvocationHandler proxy, List<String> configuredHosts, Map<String, JdbcConnection> liveConnections, long[] responseTimes, int numRetries) throws SQLException {
/* 54 */     if (this.affinityOrderedServers == null) {
/* 55 */       return super.pickConnection(proxy, configuredHosts, liveConnections, responseTimes, numRetries);
/*    */     }
/* 57 */     Map<String, Long> blockList = ((LoadBalancedConnectionProxy)proxy).getGlobalBlocklist();
/*    */     
/* 59 */     for (String host : this.affinityOrderedServers) {
/* 60 */       if (configuredHosts.contains(host) && !blockList.containsKey(host)) {
/* 61 */         ConnectionImpl conn = (ConnectionImpl)liveConnections.get(host);
/* 62 */         if (conn != null) {
/* 63 */           return conn;
/*    */         }
/*    */         try {
/* 66 */           conn = ((LoadBalancedConnectionProxy)proxy).createConnectionForHost(host);
/* 67 */           return conn;
/* 68 */         } catch (SQLException sqlEx) {
/* 69 */           if (((LoadBalancedConnectionProxy)proxy).shouldExceptionTriggerConnectionSwitch(sqlEx)) {
/* 70 */             ((LoadBalancedConnectionProxy)proxy).addToGlobalBlocklist(host);
/*    */           }
/*    */         } 
/*    */       } 
/*    */     } 
/*    */ 
/*    */     
/* 77 */     return super.pickConnection(proxy, configuredHosts, liveConnections, responseTimes, numRetries);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\ha\ServerAffinityStrategy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */