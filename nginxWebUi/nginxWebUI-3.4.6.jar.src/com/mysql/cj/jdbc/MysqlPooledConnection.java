/*     */ package com.mysql.cj.jdbc;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.exceptions.CJException;
/*     */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*     */ import com.mysql.cj.jdbc.exceptions.SQLError;
/*     */ import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import javax.sql.ConnectionEvent;
/*     */ import javax.sql.ConnectionEventListener;
/*     */ import javax.sql.PooledConnection;
/*     */ import javax.sql.StatementEvent;
/*     */ import javax.sql.StatementEventListener;
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
/*     */ public class MysqlPooledConnection
/*     */   implements PooledConnection
/*     */ {
/*     */   public static final int CONNECTION_ERROR_EVENT = 1;
/*     */   public static final int CONNECTION_CLOSED_EVENT = 2;
/*     */   private Map<ConnectionEventListener, ConnectionEventListener> connectionEventListeners;
/*     */   private Connection logicalHandle;
/*     */   private JdbcConnection physicalConn;
/*     */   private ExceptionInterceptor exceptionInterceptor;
/*     */   
/*     */   protected static MysqlPooledConnection getInstance(JdbcConnection connection) throws SQLException {
/*  55 */     return new MysqlPooledConnection(connection);
/*     */   }
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
/*  76 */   private final Map<StatementEventListener, StatementEventListener> statementEventListeners = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MysqlPooledConnection(JdbcConnection connection) {
/*  85 */     this.logicalHandle = null;
/*  86 */     this.physicalConn = connection;
/*  87 */     this.connectionEventListeners = new HashMap<>();
/*  88 */     this.exceptionInterceptor = this.physicalConn.getExceptionInterceptor();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void addConnectionEventListener(ConnectionEventListener connectioneventlistener) {
/*  94 */     if (this.connectionEventListeners != null) {
/*  95 */       this.connectionEventListeners.put(connectioneventlistener, connectioneventlistener);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void removeConnectionEventListener(ConnectionEventListener connectioneventlistener) {
/* 102 */     if (this.connectionEventListeners != null) {
/* 103 */       this.connectionEventListeners.remove(connectioneventlistener);
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized Connection getConnection() throws SQLException {
/*     */     
/* 109 */     try { return getConnection(true, false); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   
/*     */   protected synchronized Connection getConnection(boolean resetServerState, boolean forXa) throws SQLException {
/* 114 */     if (this.physicalConn == null) {
/*     */       
/* 116 */       SQLException sqlException = SQLError.createSQLException(Messages.getString("MysqlPooledConnection.0"), this.exceptionInterceptor);
/* 117 */       callConnectionEventListeners(1, sqlException);
/*     */       
/* 119 */       throw sqlException;
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 124 */       if (this.logicalHandle != null) {
/* 125 */         ((ConnectionWrapper)this.logicalHandle).close(false);
/*     */       }
/*     */       
/* 128 */       if (resetServerState) {
/* 129 */         this.physicalConn.resetServerState();
/*     */       }
/*     */       
/* 132 */       this.logicalHandle = ConnectionWrapper.getInstance(this, this.physicalConn, forXa);
/* 133 */     } catch (SQLException sqlException) {
/* 134 */       callConnectionEventListeners(1, sqlException);
/*     */       
/* 136 */       throw sqlException;
/*     */     } 
/*     */     
/* 139 */     return this.logicalHandle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void close() throws SQLException {
/*     */     
/* 149 */     try { if (this.physicalConn != null) {
/* 150 */         this.physicalConn.close();
/*     */         
/* 152 */         this.physicalConn = null;
/*     */       } 
/*     */       
/* 155 */       if (this.connectionEventListeners != null) {
/* 156 */         this.connectionEventListeners.clear();
/*     */         
/* 158 */         this.connectionEventListeners = null;
/*     */       } 
/*     */       
/* 161 */       this.statementEventListeners.clear(); return; }
/* 162 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
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
/*     */   protected synchronized void callConnectionEventListeners(int eventType, SQLException sqlException) {
/* 178 */     if (this.connectionEventListeners == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 183 */     Iterator<Map.Entry<ConnectionEventListener, ConnectionEventListener>> iterator = this.connectionEventListeners.entrySet().iterator();
/*     */     
/* 185 */     ConnectionEvent connectionevent = new ConnectionEvent(this, sqlException);
/*     */     
/* 187 */     while (iterator.hasNext()) {
/*     */       
/* 189 */       ConnectionEventListener connectioneventlistener = (ConnectionEventListener)((Map.Entry)iterator.next()).getValue();
/*     */       
/* 191 */       if (eventType == 2) {
/* 192 */         connectioneventlistener.connectionClosed(connectionevent); continue;
/* 193 */       }  if (eventType == 1) {
/* 194 */         connectioneventlistener.connectionErrorOccurred(connectionevent);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   protected ExceptionInterceptor getExceptionInterceptor() {
/* 200 */     return this.exceptionInterceptor;
/*     */   }
/*     */ 
/*     */   
/*     */   public void addStatementEventListener(StatementEventListener listener) {
/* 205 */     synchronized (this.statementEventListeners) {
/* 206 */       this.statementEventListeners.put(listener, listener);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeStatementEventListener(StatementEventListener listener) {
/* 212 */     synchronized (this.statementEventListeners) {
/* 213 */       this.statementEventListeners.remove(listener);
/*     */     } 
/*     */   }
/*     */   
/*     */   void fireStatementEvent(StatementEvent event) throws SQLException {
/* 218 */     synchronized (this.statementEventListeners) {
/* 219 */       for (StatementEventListener listener : this.statementEventListeners.keySet())
/* 220 */         listener.statementClosed(event); 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\MysqlPooledConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */