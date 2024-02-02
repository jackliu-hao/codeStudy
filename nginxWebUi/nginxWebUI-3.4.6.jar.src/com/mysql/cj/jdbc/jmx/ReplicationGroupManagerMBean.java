/*     */ package com.mysql.cj.jdbc.jmx;
/*     */ 
/*     */ import java.sql.SQLException;
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
/*     */ public interface ReplicationGroupManagerMBean
/*     */ {
/*     */   void addReplicaHost(String paramString1, String paramString2) throws SQLException;
/*     */   
/*     */   @Deprecated
/*     */   default void addSlaveHost(String groupFilter, String host) throws SQLException {
/*  50 */     addReplicaHost(groupFilter, host);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void removeReplicaHost(String paramString1, String paramString2) throws SQLException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   default void removeSlaveHost(String groupFilter, String host) throws SQLException {
/*  67 */     removeReplicaHost(groupFilter, host);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void promoteReplicaToSource(String paramString1, String paramString2) throws SQLException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   default void promoteSlaveToMaster(String groupFilter, String host) throws SQLException {
/*  84 */     promoteReplicaToSource(groupFilter, host);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void removeSourceHost(String paramString1, String paramString2) throws SQLException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   default void removeMasterHost(String groupFilter, String host) throws SQLException {
/* 101 */     removeSourceHost(groupFilter, host);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   String getSourceHostsList(String paramString);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   default String getMasterHostsList(String group) {
/* 116 */     return getSourceHostsList(group);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   String getReplicaHostsList(String paramString);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   default String getSlaveHostsList(String group) {
/* 131 */     return getReplicaHostsList(group);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   String getRegisteredConnectionGroups();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int getActiveSourceHostCount(String paramString);
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   default int getActiveMasterHostCount(String group) {
/* 148 */     return getActiveSourceHostCount(group);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int getActiveReplicaHostCount(String paramString);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   default int getActiveSlaveHostCount(String group) {
/* 163 */     return getActiveReplicaHostCount(group);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int getReplicaPromotionCount(String paramString);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   default int getSlavePromotionCount(String group) {
/* 178 */     return getReplicaPromotionCount(group);
/*     */   }
/*     */   
/*     */   long getTotalLogicalConnectionCount(String paramString);
/*     */   
/*     */   long getActiveLogicalConnectionCount(String paramString);
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\jmx\ReplicationGroupManagerMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */