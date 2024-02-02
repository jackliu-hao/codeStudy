/*     */ package org.h2.jdbcx;
/*     */ 
/*     */ import java.sql.Connection;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.ArrayList;
/*     */ import javax.sql.ConnectionEvent;
/*     */ import javax.sql.ConnectionEventListener;
/*     */ import javax.sql.StatementEventListener;
/*     */ import javax.sql.XAConnection;
/*     */ import javax.transaction.xa.XAException;
/*     */ import javax.transaction.xa.XAResource;
/*     */ import javax.transaction.xa.Xid;
/*     */ import org.h2.jdbc.JdbcConnection;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.message.TraceObject;
/*     */ import org.h2.util.Utils;
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
/*     */ public final class JdbcXAConnection
/*     */   extends TraceObject
/*     */   implements XAConnection, XAResource
/*     */ {
/*     */   private final JdbcDataSourceFactory factory;
/*     */   private JdbcConnection physicalConn;
/*     */   private volatile Connection handleConn;
/*  44 */   private final ArrayList<ConnectionEventListener> listeners = Utils.newSmallArrayList();
/*     */   
/*     */   private Xid currentTransaction;
/*     */   private boolean prepared;
/*     */   
/*     */   JdbcXAConnection(JdbcDataSourceFactory paramJdbcDataSourceFactory, int paramInt, JdbcConnection paramJdbcConnection) {
/*  50 */     this.factory = paramJdbcDataSourceFactory;
/*  51 */     setTrace(paramJdbcDataSourceFactory.getTrace(), 13, paramInt);
/*  52 */     this.physicalConn = paramJdbcConnection;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public XAResource getXAResource() {
/*  62 */     debugCodeCall("getXAResource");
/*  63 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws SQLException {
/*  72 */     debugCodeCall("close");
/*  73 */     Connection connection = this.handleConn;
/*  74 */     if (connection != null) {
/*  75 */       this.listeners.clear();
/*  76 */       connection.close();
/*     */     } 
/*  78 */     if (this.physicalConn != null) {
/*     */       try {
/*  80 */         this.physicalConn.close();
/*     */       } finally {
/*  82 */         this.physicalConn = null;
/*     */       } 
/*     */     }
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
/*     */   public Connection getConnection() throws SQLException {
/*  96 */     debugCodeCall("getConnection");
/*  97 */     Connection connection = this.handleConn;
/*  98 */     if (connection != null) {
/*  99 */       connection.close();
/*     */     }
/*     */     
/* 102 */     this.physicalConn.rollback();
/* 103 */     this.handleConn = (Connection)new PooledJdbcConnection(this.physicalConn);
/* 104 */     return this.handleConn;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addConnectionEventListener(ConnectionEventListener paramConnectionEventListener) {
/* 114 */     debugCode("addConnectionEventListener(listener)");
/* 115 */     this.listeners.add(paramConnectionEventListener);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeConnectionEventListener(ConnectionEventListener paramConnectionEventListener) {
/* 125 */     debugCode("removeConnectionEventListener(listener)");
/* 126 */     this.listeners.remove(paramConnectionEventListener);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void closedHandle() {
/* 133 */     debugCodeCall("closedHandle");
/* 134 */     ConnectionEvent connectionEvent = new ConnectionEvent(this);
/*     */ 
/*     */     
/* 137 */     for (int i = this.listeners.size() - 1; i >= 0; i--) {
/* 138 */       ConnectionEventListener connectionEventListener = this.listeners.get(i);
/* 139 */       connectionEventListener.connectionClosed(connectionEvent);
/*     */     } 
/* 141 */     this.handleConn = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getTransactionTimeout() {
/* 151 */     debugCodeCall("getTransactionTimeout");
/* 152 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean setTransactionTimeout(int paramInt) {
/* 163 */     debugCodeCall("setTransactionTimeout", paramInt);
/* 164 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSameRM(XAResource paramXAResource) {
/* 175 */     debugCode("isSameRM(xares)");
/* 176 */     return (paramXAResource == this);
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
/*     */   public Xid[] recover(int paramInt) throws XAException {
/* 189 */     debugCodeCall("recover", quoteFlags(paramInt));
/* 190 */     checkOpen();
/* 191 */     try (Statement null = this.physicalConn.createStatement()) {
/* 192 */       ResultSet resultSet = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.IN_DOUBT ORDER BY TRANSACTION_NAME");
/* 193 */       ArrayList<JdbcXid> arrayList = Utils.newSmallArrayList();
/* 194 */       while (resultSet.next()) {
/* 195 */         String str = resultSet.getString("TRANSACTION_NAME");
/* 196 */         int i = getNextId(15);
/* 197 */         JdbcXid jdbcXid = new JdbcXid(this.factory, i, str);
/* 198 */         arrayList.add(jdbcXid);
/*     */       } 
/* 200 */       resultSet.close();
/* 201 */       Xid[] arrayOfXid = arrayList.<Xid>toArray(new Xid[0]);
/* 202 */       if (!arrayList.isEmpty()) {
/* 203 */         this.prepared = true;
/*     */       }
/* 205 */       return arrayOfXid;
/* 206 */     } catch (SQLException sQLException) {
/* 207 */       XAException xAException = new XAException(-3);
/* 208 */       xAException.initCause(sQLException);
/* 209 */       throw xAException;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int prepare(Xid paramXid) throws XAException {
/* 221 */     if (isDebugEnabled()) {
/* 222 */       debugCode("prepare(" + quoteXid(paramXid) + ')');
/*     */     }
/* 224 */     checkOpen();
/* 225 */     if (!this.currentTransaction.equals(paramXid)) {
/* 226 */       throw new XAException(-5);
/*     */     }
/*     */     
/* 229 */     try (Statement null = this.physicalConn.createStatement()) {
/* 230 */       statement.execute(JdbcXid.toString(new StringBuilder("PREPARE COMMIT \""), paramXid).append('"').toString());
/* 231 */       this.prepared = true;
/* 232 */     } catch (SQLException sQLException) {
/* 233 */       throw convertException(sQLException);
/*     */     } 
/* 235 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void forget(Xid paramXid) {
/* 246 */     if (isDebugEnabled()) {
/* 247 */       debugCode("forget(" + quoteXid(paramXid) + ')');
/*     */     }
/* 249 */     this.prepared = false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void rollback(Xid paramXid) throws XAException {
/* 259 */     if (isDebugEnabled()) {
/* 260 */       debugCode("rollback(" + quoteXid(paramXid) + ')');
/*     */     }
/*     */     try {
/* 263 */       if (this.prepared) {
/* 264 */         try (Statement null = this.physicalConn.createStatement()) {
/* 265 */           statement.execute(JdbcXid.toString(new StringBuilder("ROLLBACK TRANSACTION \""), paramXid)
/* 266 */               .append('"').toString());
/*     */         } 
/* 268 */         this.prepared = false;
/*     */       } else {
/* 270 */         this.physicalConn.rollback();
/*     */       } 
/* 272 */       this.physicalConn.setAutoCommit(true);
/* 273 */     } catch (SQLException sQLException) {
/* 274 */       throw convertException(sQLException);
/*     */     } 
/* 276 */     this.currentTransaction = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void end(Xid paramXid, int paramInt) throws XAException {
/* 287 */     if (isDebugEnabled()) {
/* 288 */       debugCode("end(" + quoteXid(paramXid) + ", " + quoteFlags(paramInt) + ')');
/*     */     }
/*     */     
/* 291 */     if (paramInt == 33554432) {
/*     */       return;
/*     */     }
/* 294 */     if (!this.currentTransaction.equals(paramXid)) {
/* 295 */       throw new XAException(-9);
/*     */     }
/* 297 */     this.prepared = false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void start(Xid paramXid, int paramInt) throws XAException {
/* 308 */     if (isDebugEnabled()) {
/* 309 */       debugCode("start(" + quoteXid(paramXid) + ", " + quoteFlags(paramInt) + ')');
/*     */     }
/* 311 */     if (paramInt == 134217728) {
/*     */       return;
/*     */     }
/* 314 */     if (paramInt == 2097152) {
/* 315 */       if (this.currentTransaction != null && !this.currentTransaction.equals(paramXid)) {
/* 316 */         throw new XAException(-3);
/*     */       }
/* 318 */     } else if (this.currentTransaction != null) {
/* 319 */       throw new XAException(-4);
/*     */     } 
/*     */     try {
/* 322 */       this.physicalConn.setAutoCommit(false);
/* 323 */     } catch (SQLException sQLException) {
/* 324 */       throw convertException(sQLException);
/*     */     } 
/* 326 */     this.currentTransaction = paramXid;
/* 327 */     this.prepared = false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void commit(Xid paramXid, boolean paramBoolean) throws XAException {
/* 338 */     if (isDebugEnabled()) {
/* 339 */       debugCode("commit(" + quoteXid(paramXid) + ", " + paramBoolean + ')');
/*     */     }
/*     */     
/*     */     try {
/* 343 */       if (paramBoolean) {
/* 344 */         this.physicalConn.commit();
/*     */       } else {
/* 346 */         try (Statement null = this.physicalConn.createStatement()) {
/* 347 */           statement.execute(
/* 348 */               JdbcXid.toString(new StringBuilder("COMMIT TRANSACTION \""), paramXid).append('"').toString());
/* 349 */           this.prepared = false;
/*     */         } 
/*     */       } 
/* 352 */       this.physicalConn.setAutoCommit(true);
/* 353 */     } catch (SQLException sQLException) {
/* 354 */       throw convertException(sQLException);
/*     */     } 
/* 356 */     this.currentTransaction = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addStatementEventListener(StatementEventListener paramStatementEventListener) {
/* 366 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeStatementEventListener(StatementEventListener paramStatementEventListener) {
/* 376 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 384 */     return getTraceObjectName() + ": " + this.physicalConn;
/*     */   }
/*     */   
/*     */   private static XAException convertException(SQLException paramSQLException) {
/* 388 */     XAException xAException = new XAException(paramSQLException.getMessage());
/* 389 */     xAException.initCause(paramSQLException);
/* 390 */     return xAException;
/*     */   }
/*     */   
/*     */   private static String quoteXid(Xid paramXid) {
/* 394 */     return JdbcXid.toString(new StringBuilder(), paramXid).toString().replace('-', '$');
/*     */   }
/*     */   
/*     */   private static String quoteFlags(int paramInt) {
/* 398 */     StringBuilder stringBuilder = new StringBuilder();
/* 399 */     if ((paramInt & 0x800000) != 0) {
/* 400 */       stringBuilder.append("|XAResource.TMENDRSCAN");
/*     */     }
/* 402 */     if ((paramInt & 0x20000000) != 0) {
/* 403 */       stringBuilder.append("|XAResource.TMFAIL");
/*     */     }
/* 405 */     if ((paramInt & 0x200000) != 0) {
/* 406 */       stringBuilder.append("|XAResource.TMJOIN");
/*     */     }
/* 408 */     if ((paramInt & 0x40000000) != 0) {
/* 409 */       stringBuilder.append("|XAResource.TMONEPHASE");
/*     */     }
/* 411 */     if ((paramInt & 0x8000000) != 0) {
/* 412 */       stringBuilder.append("|XAResource.TMRESUME");
/*     */     }
/* 414 */     if ((paramInt & 0x1000000) != 0) {
/* 415 */       stringBuilder.append("|XAResource.TMSTARTRSCAN");
/*     */     }
/* 417 */     if ((paramInt & 0x4000000) != 0) {
/* 418 */       stringBuilder.append("|XAResource.TMSUCCESS");
/*     */     }
/* 420 */     if ((paramInt & 0x2000000) != 0) {
/* 421 */       stringBuilder.append("|XAResource.TMSUSPEND");
/*     */     }
/* 423 */     if ((paramInt & 0x3) != 0) {
/* 424 */       stringBuilder.append("|XAResource.XA_RDONLY");
/*     */     }
/* 426 */     if (stringBuilder.length() == 0) {
/* 427 */       stringBuilder.append("|XAResource.TMNOFLAGS");
/*     */     }
/* 429 */     return stringBuilder.substring(1);
/*     */   }
/*     */   
/*     */   private void checkOpen() throws XAException {
/* 433 */     if (this.physicalConn == null) {
/* 434 */       throw new XAException(-3);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   final class PooledJdbcConnection
/*     */     extends JdbcConnection
/*     */   {
/*     */     private boolean isClosed;
/*     */ 
/*     */     
/*     */     public PooledJdbcConnection(JdbcConnection param1JdbcConnection) {
/* 446 */       super(param1JdbcConnection);
/*     */     }
/*     */ 
/*     */     
/*     */     public synchronized void close() throws SQLException {
/* 451 */       if (!this.isClosed) {
/*     */         try {
/* 453 */           rollback();
/* 454 */           setAutoCommit(true);
/* 455 */         } catch (SQLException sQLException) {}
/*     */ 
/*     */         
/* 458 */         JdbcXAConnection.this.closedHandle();
/* 459 */         this.isClosed = true;
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public synchronized boolean isClosed() throws SQLException {
/* 465 */       return (this.isClosed || super.isClosed());
/*     */     }
/*     */ 
/*     */     
/*     */     protected synchronized void checkClosed() {
/* 470 */       if (this.isClosed) {
/* 471 */         throw DbException.get(90007);
/*     */       }
/* 473 */       super.checkClosed();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\jdbcx\JdbcXAConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */