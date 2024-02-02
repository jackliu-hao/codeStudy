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
/*     */ public class SequentialBalanceStrategy
/*     */   implements BalanceStrategy
/*     */ {
/*  46 */   private int currentHostIndex = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConnectionImpl pickConnection(InvocationHandler proxy, List<String> configuredHosts, Map<String, JdbcConnection> liveConnections, long[] responseTimes, int numRetries) throws SQLException {
/*  54 */     int numHosts = configuredHosts.size();
/*     */     
/*  56 */     SQLException ex = null;
/*     */     
/*  58 */     Map<String, Long> blockList = ((LoadBalancedConnectionProxy)proxy).getGlobalBlocklist();
/*     */     
/*  60 */     for (int attempts = 0; attempts < numRetries; attempts++) {
/*  61 */       if (numHosts == 1) {
/*  62 */         this.currentHostIndex = 0;
/*  63 */       } else if (this.currentHostIndex == -1) {
/*  64 */         int random = (int)Math.floor(Math.random() * numHosts);
/*     */         int i;
/*  66 */         for (i = random; i < numHosts; i++) {
/*  67 */           if (!blockList.containsKey(configuredHosts.get(i))) {
/*  68 */             this.currentHostIndex = i;
/*     */             
/*     */             break;
/*     */           } 
/*     */         } 
/*  73 */         if (this.currentHostIndex == -1) {
/*  74 */           for (i = 0; i < random; i++) {
/*  75 */             if (!blockList.containsKey(configuredHosts.get(i))) {
/*  76 */               this.currentHostIndex = i;
/*     */               
/*     */               break;
/*     */             } 
/*     */           } 
/*     */         }
/*  82 */         if (this.currentHostIndex == -1) {
/*  83 */           blockList = ((LoadBalancedConnectionProxy)proxy).getGlobalBlocklist();
/*     */ 
/*     */           
/*     */           try {
/*  87 */             Thread.sleep(250L);
/*  88 */           } catch (InterruptedException interruptedException) {}
/*     */ 
/*     */ 
/*     */           
/*     */           continue;
/*     */         } 
/*     */       } else {
/*  95 */         int i = this.currentHostIndex + 1;
/*  96 */         boolean foundGoodHost = false;
/*     */         
/*  98 */         for (; i < numHosts; i++) {
/*  99 */           if (!blockList.containsKey(configuredHosts.get(i))) {
/* 100 */             this.currentHostIndex = i;
/* 101 */             foundGoodHost = true;
/*     */             
/*     */             break;
/*     */           } 
/*     */         } 
/* 106 */         if (!foundGoodHost) {
/* 107 */           for (i = 0; i < this.currentHostIndex; i++) {
/* 108 */             if (!blockList.containsKey(configuredHosts.get(i))) {
/* 109 */               this.currentHostIndex = i;
/* 110 */               foundGoodHost = true;
/*     */               
/*     */               break;
/*     */             } 
/*     */           } 
/*     */         }
/* 116 */         if (!foundGoodHost) {
/* 117 */           blockList = ((LoadBalancedConnectionProxy)proxy).getGlobalBlocklist();
/*     */ 
/*     */           
/*     */           try {
/* 121 */             Thread.sleep(250L);
/* 122 */           } catch (InterruptedException interruptedException) {}
/*     */ 
/*     */           
/*     */           continue;
/*     */         } 
/*     */       } 
/*     */       
/* 129 */       String hostPortSpec = configuredHosts.get(this.currentHostIndex);
/*     */       
/* 131 */       ConnectionImpl conn = (ConnectionImpl)liveConnections.get(hostPortSpec);
/*     */       
/* 133 */       if (conn == null) {
/*     */         try {
/* 135 */           conn = ((LoadBalancedConnectionProxy)proxy).createConnectionForHost(hostPortSpec);
/* 136 */         } catch (SQLException sqlEx) {
/* 137 */           ex = sqlEx;
/*     */           
/* 139 */           if (((LoadBalancedConnectionProxy)proxy).shouldExceptionTriggerConnectionSwitch(sqlEx)) {
/*     */             
/* 141 */             ((LoadBalancedConnectionProxy)proxy).addToGlobalBlocklist(hostPortSpec);
/*     */             
/*     */             try {
/* 144 */               Thread.sleep(250L);
/* 145 */             } catch (InterruptedException interruptedException) {}
/*     */           
/*     */           }
/*     */           else {
/*     */             
/* 150 */             throw sqlEx;
/*     */           } 
/*     */         } 
/*     */       }
/* 154 */       return conn;
/*     */     } 
/*     */     
/* 157 */     if (ex != null) {
/* 158 */       throw ex;
/*     */     }
/*     */     
/* 161 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\ha\SequentialBalanceStrategy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */