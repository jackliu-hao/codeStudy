/*     */ package com.mysql.cj.jdbc.ha;
/*     */ 
/*     */ import com.mysql.cj.jdbc.JdbcConnection;
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
/*     */ public interface ReplicationConnection
/*     */   extends JdbcConnection
/*     */ {
/*     */   long getConnectionGroupId();
/*     */   
/*     */   JdbcConnection getCurrentConnection();
/*     */   
/*     */   JdbcConnection getSourceConnection();
/*     */   
/*     */   @Deprecated
/*     */   default JdbcConnection getMasterConnection() {
/*  51 */     return getSourceConnection();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void promoteReplicaToSource(String paramString) throws SQLException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   default void promoteSlaveToMaster(String host) throws SQLException {
/*  66 */     promoteReplicaToSource(host);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void removeSourceHost(String paramString) throws SQLException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   default void removeMasterHost(String host) throws SQLException {
/*  81 */     removeSourceHost(host);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void removeSourceHost(String paramString, boolean paramBoolean) throws SQLException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   default void removeMasterHost(String host, boolean waitUntilNotInUse) throws SQLException {
/*  98 */     removeSourceHost(host, waitUntilNotInUse);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isHostSource(String paramString);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   default boolean isHostMaster(String host) {
/* 113 */     return isHostSource(host);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   JdbcConnection getReplicaConnection();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   default JdbcConnection getSlavesConnection() {
/* 126 */     return getReplicaConnection();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void addReplicaHost(String paramString) throws SQLException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   default void addSlaveHost(String host) throws SQLException {
/* 141 */     addReplicaHost(host);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void removeReplica(String paramString) throws SQLException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   default void removeSlave(String host) throws SQLException {
/* 156 */     removeReplica(host);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void removeReplica(String paramString, boolean paramBoolean) throws SQLException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   default void removeSlave(String host, boolean closeGently) throws SQLException {
/* 173 */     removeReplica(host, closeGently);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isHostReplica(String paramString);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   default boolean isHostSlave(String host) {
/* 188 */     return isHostReplica(host);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\ha\ReplicationConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */