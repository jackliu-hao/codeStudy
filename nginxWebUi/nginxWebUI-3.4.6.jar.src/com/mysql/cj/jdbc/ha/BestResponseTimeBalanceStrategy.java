/*     */ package com.mysql.cj.jdbc.ha;
/*     */ 
/*     */ import com.mysql.cj.jdbc.ConnectionImpl;
/*     */ import com.mysql.cj.jdbc.JdbcConnection;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.sql.SQLException;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ public class BestResponseTimeBalanceStrategy
/*     */   implements BalanceStrategy
/*     */ {
/*     */   public ConnectionImpl pickConnection(InvocationHandler proxy, List<String> configuredHosts, Map<String, JdbcConnection> liveConnections, long[] responseTimes, int numRetries) throws SQLException {
/*  49 */     Map<String, Long> blockList = ((LoadBalancedConnectionProxy)proxy).getGlobalBlocklist();
/*     */     
/*  51 */     SQLException ex = null;
/*     */     
/*  53 */     for (int attempts = 0; attempts < numRetries; ) {
/*  54 */       long minResponseTime = Long.MAX_VALUE;
/*     */       
/*  56 */       int bestHostIndex = 0;
/*     */ 
/*     */       
/*  59 */       if (blockList.size() == configuredHosts.size()) {
/*  60 */         blockList = ((LoadBalancedConnectionProxy)proxy).getGlobalBlocklist();
/*     */       }
/*     */       
/*  63 */       for (int i = 0; i < responseTimes.length; i++) {
/*  64 */         long candidateResponseTime = responseTimes[i];
/*     */         
/*  66 */         if (candidateResponseTime < minResponseTime && !blockList.containsKey(configuredHosts.get(i))) {
/*  67 */           if (candidateResponseTime == 0L) {
/*  68 */             bestHostIndex = i;
/*     */             
/*     */             break;
/*     */           } 
/*     */           
/*  73 */           bestHostIndex = i;
/*  74 */           minResponseTime = candidateResponseTime;
/*     */         } 
/*     */       } 
/*     */       
/*  78 */       String bestHost = configuredHosts.get(bestHostIndex);
/*     */       
/*  80 */       ConnectionImpl conn = (ConnectionImpl)liveConnections.get(bestHost);
/*     */       
/*  82 */       if (conn == null) {
/*     */         try {
/*  84 */           conn = ((LoadBalancedConnectionProxy)proxy).createConnectionForHost(bestHost);
/*  85 */         } catch (SQLException sqlEx) {
/*  86 */           ex = sqlEx;
/*     */           
/*  88 */           if (((LoadBalancedConnectionProxy)proxy).shouldExceptionTriggerConnectionSwitch(sqlEx)) {
/*  89 */             ((LoadBalancedConnectionProxy)proxy).addToGlobalBlocklist(bestHost);
/*  90 */             blockList.put(bestHost, null);
/*     */             
/*  92 */             if (blockList.size() == configuredHosts.size()) {
/*  93 */               attempts++;
/*     */               try {
/*  95 */                 Thread.sleep(250L);
/*  96 */               } catch (InterruptedException interruptedException) {}
/*     */               
/*  98 */               blockList = ((LoadBalancedConnectionProxy)proxy).getGlobalBlocklist();
/*     */             } 
/*     */             
/*     */             continue;
/*     */           } 
/*     */           
/* 104 */           throw sqlEx;
/*     */         } 
/*     */       }
/*     */       
/* 108 */       return conn;
/*     */     } 
/*     */     
/* 111 */     if (ex != null) {
/* 112 */       throw ex;
/*     */     }
/*     */     
/* 115 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\ha\BestResponseTimeBalanceStrategy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */