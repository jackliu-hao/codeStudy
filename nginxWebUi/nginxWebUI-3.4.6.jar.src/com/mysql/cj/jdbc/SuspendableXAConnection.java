/*     */ package com.mysql.cj.jdbc;
/*     */ 
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.exceptions.CJException;
/*     */ import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.sql.XAConnection;
/*     */ import javax.transaction.xa.XAException;
/*     */ import javax.transaction.xa.XAResource;
/*     */ import javax.transaction.xa.Xid;
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
/*     */ public class SuspendableXAConnection
/*     */   extends MysqlPooledConnection
/*     */   implements XAConnection, XAResource
/*     */ {
/*     */   protected static SuspendableXAConnection getInstance(JdbcConnection mysqlConnection) throws SQLException {
/*  46 */     return new SuspendableXAConnection(mysqlConnection);
/*     */   }
/*     */   
/*     */   public SuspendableXAConnection(JdbcConnection connection) {
/*  50 */     super(connection);
/*  51 */     this.underlyingConnection = connection;
/*     */   }
/*     */   
/*  54 */   private static final Map<Xid, XAConnection> XIDS_TO_PHYSICAL_CONNECTIONS = new HashMap<>();
/*     */ 
/*     */   
/*     */   private Xid currentXid;
/*     */ 
/*     */   
/*     */   private XAConnection currentXAConnection;
/*     */   
/*     */   private XAResource currentXAResource;
/*     */   
/*     */   private JdbcConnection underlyingConnection;
/*     */ 
/*     */   
/*     */   private static synchronized XAConnection findConnectionForXid(JdbcConnection connectionToWrap, Xid xid) throws SQLException {
/*  68 */     XAConnection conn = XIDS_TO_PHYSICAL_CONNECTIONS.get(xid);
/*     */     
/*  70 */     if (conn == null) {
/*  71 */       conn = new MysqlXAConnection(connectionToWrap, ((Boolean)connectionToWrap.getPropertySet().getBooleanProperty(PropertyKey.logXaCommands).getValue()).booleanValue());
/*  72 */       XIDS_TO_PHYSICAL_CONNECTIONS.put(xid, conn);
/*     */     } 
/*     */     
/*  75 */     return conn;
/*     */   }
/*     */   
/*     */   private static synchronized void removeXAConnectionMapping(Xid xid) {
/*  79 */     XIDS_TO_PHYSICAL_CONNECTIONS.remove(xid);
/*     */   }
/*     */   
/*     */   private synchronized void switchToXid(Xid xid) throws XAException {
/*  83 */     if (xid == null) {
/*  84 */       throw new XAException();
/*     */     }
/*     */     
/*     */     try {
/*  88 */       if (!xid.equals(this.currentXid)) {
/*  89 */         XAConnection toSwitchTo = findConnectionForXid(this.underlyingConnection, xid);
/*  90 */         this.currentXAConnection = toSwitchTo;
/*  91 */         this.currentXid = xid;
/*  92 */         this.currentXAResource = toSwitchTo.getXAResource();
/*     */       } 
/*  94 */     } catch (SQLException sqlEx) {
/*  95 */       throw new XAException();
/*     */     } 
/*     */   }
/*     */   
/*     */   public XAResource getXAResource() throws SQLException {
/*     */     
/* 101 */     try { return this; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
/*     */   
/*     */   }
/*     */   
/*     */   public void commit(Xid xid, boolean arg1) throws XAException {
/* 106 */     switchToXid(xid);
/* 107 */     this.currentXAResource.commit(xid, arg1);
/* 108 */     removeXAConnectionMapping(xid);
/*     */   }
/*     */ 
/*     */   
/*     */   public void end(Xid xid, int arg1) throws XAException {
/* 113 */     switchToXid(xid);
/* 114 */     this.currentXAResource.end(xid, arg1);
/*     */   }
/*     */ 
/*     */   
/*     */   public void forget(Xid xid) throws XAException {
/* 119 */     switchToXid(xid);
/* 120 */     this.currentXAResource.forget(xid);
/*     */     
/* 122 */     removeXAConnectionMapping(xid);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getTransactionTimeout() throws XAException {
/* 127 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isSameRM(XAResource xaRes) throws XAException {
/* 132 */     return (xaRes == this);
/*     */   }
/*     */ 
/*     */   
/*     */   public int prepare(Xid xid) throws XAException {
/* 137 */     switchToXid(xid);
/* 138 */     return this.currentXAResource.prepare(xid);
/*     */   }
/*     */ 
/*     */   
/*     */   public Xid[] recover(int flag) throws XAException {
/* 143 */     return MysqlXAConnection.recover(this.underlyingConnection, flag);
/*     */   }
/*     */ 
/*     */   
/*     */   public void rollback(Xid xid) throws XAException {
/* 148 */     switchToXid(xid);
/* 149 */     this.currentXAResource.rollback(xid);
/* 150 */     removeXAConnectionMapping(xid);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean setTransactionTimeout(int arg0) throws XAException {
/* 155 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void start(Xid xid, int arg1) throws XAException {
/* 160 */     switchToXid(xid);
/*     */     
/* 162 */     if (arg1 != 2097152) {
/* 163 */       this.currentXAResource.start(xid, arg1);
/*     */ 
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 172 */     this.currentXAResource.start(xid, 134217728);
/*     */   }
/*     */   
/*     */   public synchronized Connection getConnection() throws SQLException {
/*     */     
/* 177 */     try { if (this.currentXAConnection == null) {
/* 178 */         return getConnection(false, true);
/*     */       }
/*     */       
/* 181 */       return this.currentXAConnection.getConnection(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
/*     */   
/*     */   }
/*     */   public void close() throws SQLException {
/*     */     
/* 186 */     try { if (this.currentXAConnection == null) {
/* 187 */         super.close();
/*     */       } else {
/* 189 */         removeXAConnectionMapping(this.currentXid);
/* 190 */         this.currentXAConnection.close();
/*     */       }  return; }
/* 192 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
/*     */   
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\SuspendableXAConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */