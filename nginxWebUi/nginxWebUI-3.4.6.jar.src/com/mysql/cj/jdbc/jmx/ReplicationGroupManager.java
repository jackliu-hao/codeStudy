/*     */ package com.mysql.cj.jdbc.jmx;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.jdbc.exceptions.SQLError;
/*     */ import com.mysql.cj.jdbc.ha.ReplicationConnectionGroup;
/*     */ import com.mysql.cj.jdbc.ha.ReplicationConnectionGroupManager;
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
/*     */ public class ReplicationGroupManager
/*     */   implements ReplicationGroupManagerMBean
/*     */ {
/*     */   private boolean isJmxRegistered = false;
/*     */   
/*     */   public synchronized void registerJmx() throws SQLException {
/*  47 */     if (this.isJmxRegistered) {
/*     */       return;
/*     */     }
/*  50 */     MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
/*     */     try {
/*  52 */       ObjectName name = new ObjectName("com.mysql.cj.jdbc.jmx:type=ReplicationGroupManager");
/*  53 */       mbs.registerMBean(this, name);
/*  54 */       this.isJmxRegistered = true;
/*  55 */     } catch (Exception e) {
/*  56 */       throw SQLError.createSQLException(Messages.getString("ReplicationGroupManager.0"), null, e, null);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void addReplicaHost(String groupFilter, String host) throws SQLException {
/*  63 */     ReplicationConnectionGroupManager.addReplicaHost(groupFilter, host);
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeReplicaHost(String groupFilter, String host) throws SQLException {
/*  68 */     ReplicationConnectionGroupManager.removeReplicaHost(groupFilter, host);
/*     */   }
/*     */ 
/*     */   
/*     */   public void promoteReplicaToSource(String groupFilter, String host) throws SQLException {
/*  73 */     ReplicationConnectionGroupManager.promoteReplicaToSource(groupFilter, host);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeSourceHost(String groupFilter, String host) throws SQLException {
/*  79 */     ReplicationConnectionGroupManager.removeSourceHost(groupFilter, host);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getSourceHostsList(String group) {
/*  85 */     StringBuilder sb = new StringBuilder("");
/*  86 */     boolean found = false;
/*  87 */     for (String host : ReplicationConnectionGroupManager.getSourceHosts(group)) {
/*  88 */       if (found) {
/*  89 */         sb.append(",");
/*     */       }
/*  91 */       found = true;
/*  92 */       sb.append(host);
/*     */     } 
/*  94 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getReplicaHostsList(String group) {
/*  99 */     StringBuilder sb = new StringBuilder("");
/* 100 */     boolean found = false;
/* 101 */     for (String host : ReplicationConnectionGroupManager.getReplicaHosts(group)) {
/* 102 */       if (found) {
/* 103 */         sb.append(",");
/*     */       }
/* 105 */       found = true;
/* 106 */       sb.append(host);
/*     */     } 
/* 108 */     return sb.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getRegisteredConnectionGroups() {
/* 114 */     StringBuilder sb = new StringBuilder("");
/* 115 */     boolean found = false;
/* 116 */     for (ReplicationConnectionGroup group : ReplicationConnectionGroupManager.getGroupsMatching(null)) {
/* 117 */       if (found) {
/* 118 */         sb.append(",");
/*     */       }
/* 120 */       found = true;
/* 121 */       sb.append(group.getGroupName());
/*     */     } 
/* 123 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getActiveSourceHostCount(String group) {
/* 128 */     return ReplicationConnectionGroupManager.getSourceHosts(group).size();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getActiveReplicaHostCount(String group) {
/* 133 */     return ReplicationConnectionGroupManager.getReplicaHosts(group).size();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getReplicaPromotionCount(String group) {
/* 138 */     return ReplicationConnectionGroupManager.getNumberOfSourcePromotion(group);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getTotalLogicalConnectionCount(String group) {
/* 143 */     return ReplicationConnectionGroupManager.getTotalConnectionCount(group);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getActiveLogicalConnectionCount(String group) {
/* 148 */     return ReplicationConnectionGroupManager.getActiveConnectionCount(group);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\jmx\ReplicationGroupManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */