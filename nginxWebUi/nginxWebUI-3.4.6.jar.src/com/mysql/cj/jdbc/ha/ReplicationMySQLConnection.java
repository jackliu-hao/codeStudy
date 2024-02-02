/*     */ package com.mysql.cj.jdbc.ha;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.exceptions.CJException;
/*     */ import com.mysql.cj.jdbc.JdbcConnection;
/*     */ import com.mysql.cj.jdbc.exceptions.SQLError;
/*     */ import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Properties;
/*     */ import java.util.concurrent.Executor;
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
/*     */ public class ReplicationMySQLConnection
/*     */   extends MultiHostMySQLConnection
/*     */   implements ReplicationConnection
/*     */ {
/*     */   public ReplicationMySQLConnection(MultiHostConnectionProxy proxy) {
/*  43 */     super(proxy);
/*     */   }
/*     */ 
/*     */   
/*     */   public ReplicationConnectionProxy getThisAsProxy() {
/*  48 */     return (ReplicationConnectionProxy)super.getThisAsProxy();
/*     */   }
/*     */ 
/*     */   
/*     */   public JdbcConnection getActiveMySQLConnection() {
/*  53 */     return getCurrentConnection();
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized JdbcConnection getCurrentConnection() {
/*  58 */     return getThisAsProxy().getCurrentConnection();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getConnectionGroupId() {
/*  63 */     return getThisAsProxy().getConnectionGroupId();
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized JdbcConnection getSourceConnection() {
/*  68 */     return getThisAsProxy().getSourceConnection();
/*     */   }
/*     */   
/*     */   private JdbcConnection getValidatedSourceConnection() {
/*  72 */     JdbcConnection conn = (getThisAsProxy()).sourceConnection;
/*     */     try {
/*  74 */       return (conn == null || conn.isClosed()) ? null : conn;
/*  75 */     } catch (SQLException e) {
/*  76 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void promoteReplicaToSource(String host) throws SQLException {
/*     */     
/*  82 */     try { getThisAsProxy().promoteReplicaToSource(host); return; }
/*  83 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   } public void removeSourceHost(String host) throws SQLException {
/*     */     
/*  87 */     try { getThisAsProxy().removeSourceHost(host); return; }
/*  88 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   } public void removeSourceHost(String host, boolean waitUntilNotInUse) throws SQLException {
/*     */     
/*  92 */     try { getThisAsProxy().removeSourceHost(host, waitUntilNotInUse); return; }
/*  93 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public boolean isHostSource(String host) {
/*  97 */     return getThisAsProxy().isHostSource(host);
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized JdbcConnection getReplicaConnection() {
/* 102 */     return getThisAsProxy().getReplicasConnection();
/*     */   }
/*     */   
/*     */   private JdbcConnection getValidatedReplicasConnection() {
/* 106 */     JdbcConnection conn = (getThisAsProxy()).replicasConnection;
/*     */     try {
/* 108 */       return (conn == null || conn.isClosed()) ? null : conn;
/* 109 */     } catch (SQLException e) {
/* 110 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void addReplicaHost(String host) throws SQLException {
/*     */     
/* 116 */     try { getThisAsProxy().addReplicaHost(host); return; }
/* 117 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   } public void removeReplica(String host) throws SQLException {
/*     */     
/* 121 */     try { getThisAsProxy().removeReplica(host); return; }
/* 122 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   } public void removeReplica(String host, boolean closeGently) throws SQLException {
/*     */     
/* 126 */     try { getThisAsProxy().removeReplica(host, closeGently); return; }
/* 127 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public boolean isHostReplica(String host) {
/* 131 */     return getThisAsProxy().isHostReplica(host);
/*     */   }
/*     */   
/*     */   public void setReadOnly(boolean readOnlyFlag) throws SQLException {
/*     */     
/* 136 */     try { getThisAsProxy().setReadOnly(readOnlyFlag); return; }
/* 137 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   } public boolean isReadOnly() throws SQLException {
/*     */     
/* 141 */     try { return getThisAsProxy().isReadOnly(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public synchronized void ping() throws SQLException {
/*     */     try {
/*     */       try {
/*     */         JdbcConnection conn;
/* 148 */         if ((conn = getValidatedSourceConnection()) != null) {
/* 149 */           conn.ping();
/*     */         }
/* 151 */       } catch (SQLException e) {
/* 152 */         if (isSourceConnection())
/* 153 */           throw e; 
/*     */       } 
/*     */       try {
/*     */         JdbcConnection conn;
/* 157 */         if ((conn = getValidatedReplicasConnection()) != null) {
/* 158 */           conn.ping();
/*     */         }
/* 160 */       } catch (SQLException e) {
/* 161 */         if (!isSourceConnection())
/* 162 */           throw e; 
/*     */       }  return;
/*     */     } catch (CJException cJException) {
/* 165 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*     */     } 
/*     */   } public synchronized void changeUser(String userName, String newPassword) throws SQLException {
/*     */     try {
/*     */       JdbcConnection conn;
/* 170 */       if ((conn = getValidatedSourceConnection()) != null) {
/* 171 */         conn.changeUser(userName, newPassword);
/*     */       }
/* 173 */       if ((conn = getValidatedReplicasConnection()) != null)
/* 174 */         conn.changeUser(userName, newPassword);  return;
/*     */     } catch (CJException cJException) {
/* 176 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*     */     } 
/*     */   }
/*     */   public synchronized void setStatementComment(String comment) {
/*     */     JdbcConnection conn;
/* 181 */     if ((conn = getValidatedSourceConnection()) != null) {
/* 182 */       conn.setStatementComment(comment);
/*     */     }
/* 184 */     if ((conn = getValidatedReplicasConnection()) != null) {
/* 185 */       conn.setStatementComment(comment);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasSameProperties(JdbcConnection c) {
/* 191 */     JdbcConnection connM = getValidatedSourceConnection();
/* 192 */     JdbcConnection connS = getValidatedReplicasConnection();
/* 193 */     if (connM == null && connS == null) {
/* 194 */       return false;
/*     */     }
/* 196 */     return ((connM == null || connM.hasSameProperties(c)) && (connS == null || connS.hasSameProperties(c)));
/*     */   }
/*     */ 
/*     */   
/*     */   public Properties getProperties() {
/* 201 */     Properties props = new Properties();
/*     */     JdbcConnection conn;
/* 203 */     if ((conn = getValidatedSourceConnection()) != null) {
/* 204 */       props.putAll(conn.getProperties());
/*     */     }
/* 206 */     if ((conn = getValidatedReplicasConnection()) != null) {
/* 207 */       props.putAll(conn.getProperties());
/*     */     }
/*     */     
/* 210 */     return props;
/*     */   }
/*     */   
/*     */   public void abort(Executor executor) throws SQLException {
/*     */     
/* 215 */     try { getThisAsProxy().doAbort(executor); return; }
/* 216 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   } public void abortInternal() throws SQLException {
/*     */     
/* 220 */     try { getThisAsProxy().doAbortInternal(); return; }
/* 221 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   public void setProxy(JdbcConnection proxy) {
/* 225 */     getThisAsProxy().setProxy(proxy);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isWrapperFor(Class<?> iface) throws SQLException {
/*     */     
/* 231 */     try { return iface.isInstance(this); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*     */   
/*     */   }
/*     */   
/*     */   public <T> T unwrap(Class<T> iface) throws SQLException {
/*     */     try {
/*     */       try {
/* 238 */         return iface.cast(this);
/* 239 */       } catch (ClassCastException cce) {
/* 240 */         throw SQLError.createSQLException(Messages.getString("Common.UnableToUnwrap", new Object[] { iface.toString() }), "S1009", 
/* 241 */             getExceptionInterceptor());
/*     */       } 
/*     */     } catch (CJException cJException) {
/*     */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*     */     } 
/*     */   } @Deprecated
/*     */   public synchronized void clearHasTriedMaster() {
/* 248 */     (getThisAsProxy()).sourceConnection.clearHasTriedMaster();
/* 249 */     (getThisAsProxy()).replicasConnection.clearHasTriedMaster();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\ha\ReplicationMySQLConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */