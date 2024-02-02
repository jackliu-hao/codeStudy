/*     */ package com.mysql.cj.jdbc.ha;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.jdbc.ConnectionImpl;
/*     */ import com.mysql.cj.jdbc.JdbcConnection;
/*     */ import com.mysql.cj.jdbc.exceptions.SQLError;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
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
/*     */ public class RandomBalanceStrategy
/*     */   implements BalanceStrategy
/*     */ {
/*     */   public ConnectionImpl pickConnection(InvocationHandler proxy, List<String> configuredHosts, Map<String, JdbcConnection> liveConnections, long[] responseTimes, int numRetries) throws SQLException {
/*  52 */     int numHosts = configuredHosts.size();
/*     */     
/*  54 */     SQLException ex = null;
/*     */     
/*  56 */     List<String> allowList = new ArrayList<>(numHosts);
/*  57 */     allowList.addAll(configuredHosts);
/*     */     
/*  59 */     Map<String, Long> blockList = ((LoadBalancedConnectionProxy)proxy).getGlobalBlocklist();
/*     */     
/*  61 */     allowList.removeAll(blockList.keySet());
/*     */     
/*  63 */     Map<String, Integer> allowListMap = getArrayIndexMap(allowList);
/*     */     
/*  65 */     for (int attempts = 0; attempts < numRetries; ) {
/*  66 */       int random = (int)Math.floor(Math.random() * allowList.size());
/*  67 */       if (allowList.size() == 0) {
/*  68 */         throw SQLError.createSQLException(Messages.getString("RandomBalanceStrategy.0"), null);
/*     */       }
/*     */       
/*  71 */       String hostPortSpec = allowList.get(random);
/*     */       
/*  73 */       ConnectionImpl conn = (ConnectionImpl)liveConnections.get(hostPortSpec);
/*     */       
/*  75 */       if (conn == null) {
/*     */         try {
/*  77 */           conn = ((LoadBalancedConnectionProxy)proxy).createConnectionForHost(hostPortSpec);
/*  78 */         } catch (SQLException sqlEx) {
/*  79 */           ex = sqlEx;
/*     */           
/*  81 */           if (((LoadBalancedConnectionProxy)proxy).shouldExceptionTriggerConnectionSwitch(sqlEx)) {
/*     */             
/*  83 */             Integer allowListIndex = allowListMap.get(hostPortSpec);
/*     */ 
/*     */             
/*  86 */             if (allowListIndex != null) {
/*  87 */               allowList.remove(allowListIndex.intValue());
/*  88 */               allowListMap = getArrayIndexMap(allowList);
/*     */             } 
/*  90 */             ((LoadBalancedConnectionProxy)proxy).addToGlobalBlocklist(hostPortSpec);
/*     */             
/*  92 */             if (allowList.size() == 0) {
/*  93 */               attempts++;
/*     */               try {
/*  95 */                 Thread.sleep(250L);
/*  96 */               } catch (InterruptedException interruptedException) {}
/*     */ 
/*     */ 
/*     */               
/* 100 */               allowListMap = new HashMap<>(numHosts);
/* 101 */               allowList.addAll(configuredHosts);
/* 102 */               blockList = ((LoadBalancedConnectionProxy)proxy).getGlobalBlocklist();
/*     */               
/* 104 */               allowList.removeAll(blockList.keySet());
/* 105 */               allowListMap = getArrayIndexMap(allowList);
/*     */             } 
/*     */             
/*     */             continue;
/*     */           } 
/*     */           
/* 111 */           throw sqlEx;
/*     */         } 
/*     */       }
/*     */       
/* 115 */       return conn;
/*     */     } 
/*     */     
/* 118 */     if (ex != null) {
/* 119 */       throw ex;
/*     */     }
/*     */     
/* 122 */     return null;
/*     */   }
/*     */   
/*     */   private Map<String, Integer> getArrayIndexMap(List<String> l) {
/* 126 */     Map<String, Integer> m = new HashMap<>(l.size());
/* 127 */     for (int i = 0; i < l.size(); i++) {
/* 128 */       m.put(l.get(i), Integer.valueOf(i));
/*     */     }
/* 130 */     return m;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\ha\RandomBalanceStrategy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */