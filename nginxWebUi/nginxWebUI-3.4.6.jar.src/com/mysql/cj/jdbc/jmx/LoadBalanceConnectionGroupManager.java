/*     */ package com.mysql.cj.jdbc.jmx;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.jdbc.ConnectionGroupManager;
/*     */ import com.mysql.cj.jdbc.exceptions.SQLError;
/*     */ import java.lang.management.ManagementFactory;
/*     */ import java.sql.SQLException;
/*     */ import javax.management.MBeanServer;
/*     */ import javax.management.ObjectName;
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
/*     */ public class LoadBalanceConnectionGroupManager
/*     */   implements LoadBalanceConnectionGroupManagerMBean
/*     */ {
/*     */   private boolean isJmxRegistered = false;
/*     */   
/*     */   public synchronized void registerJmx() throws SQLException {
/*  51 */     if (this.isJmxRegistered) {
/*     */       return;
/*     */     }
/*  54 */     MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
/*     */     try {
/*  56 */       ObjectName name = new ObjectName("com.mysql.cj.jdbc.jmx:type=LoadBalanceConnectionGroupManager");
/*  57 */       mbs.registerMBean(this, name);
/*  58 */       this.isJmxRegistered = true;
/*  59 */     } catch (Exception e) {
/*  60 */       throw SQLError.createSQLException(Messages.getString("LoadBalanceConnectionGroupManager.0"), null, e, null);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void addHost(String group, String host, boolean forExisting) {
/*     */     try {
/*  68 */       ConnectionGroupManager.addHost(group, host, forExisting);
/*  69 */     } catch (Exception e) {
/*  70 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int getActiveHostCount(String group) {
/*  76 */     return ConnectionGroupManager.getActiveHostCount(group);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getActiveLogicalConnectionCount(String group) {
/*  81 */     return ConnectionGroupManager.getActiveLogicalConnectionCount(group);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getActivePhysicalConnectionCount(String group) {
/*  86 */     return ConnectionGroupManager.getActivePhysicalConnectionCount(group);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getTotalHostCount(String group) {
/*  91 */     return ConnectionGroupManager.getTotalHostCount(group);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long getTotalLogicalConnectionCount(String group) {
/*  97 */     return ConnectionGroupManager.getTotalLogicalConnectionCount(group);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long getTotalPhysicalConnectionCount(String group) {
/* 103 */     return ConnectionGroupManager.getTotalPhysicalConnectionCount(group);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long getTotalTransactionCount(String group) {
/* 109 */     return ConnectionGroupManager.getTotalTransactionCount(group);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeHost(String group, String host) throws SQLException {
/* 115 */     ConnectionGroupManager.removeHost(group, host);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getActiveHostsList(String group) {
/* 121 */     return ConnectionGroupManager.getActiveHostLists(group);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getRegisteredConnectionGroups() {
/* 126 */     return ConnectionGroupManager.getRegisteredConnectionGroups();
/*     */   }
/*     */ 
/*     */   
/*     */   public void stopNewConnectionsToHost(String group, String host) throws SQLException {
/* 131 */     ConnectionGroupManager.removeHost(group, host);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\jmx\LoadBalanceConnectionGroupManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */