/*     */ package com.zaxxer.hikari.pool;
/*     */ 
/*     */ import com.zaxxer.hikari.SQLExceptionOverride;
/*     */ import com.zaxxer.hikari.util.ClockSource;
/*     */ import com.zaxxer.hikari.util.FastList;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.sql.CallableStatement;
/*     */ import java.sql.Connection;
/*     */ import java.sql.DatabaseMetaData;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Savepoint;
/*     */ import java.sql.Statement;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.Executor;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
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
/*     */ 
/*     */ 
/*     */ public abstract class ProxyConnection
/*     */   implements Connection
/*     */ {
/*     */   static final int DIRTY_BIT_READONLY = 1;
/*     */   static final int DIRTY_BIT_AUTOCOMMIT = 2;
/*     */   static final int DIRTY_BIT_ISOLATION = 4;
/*     */   static final int DIRTY_BIT_CATALOG = 8;
/*     */   static final int DIRTY_BIT_NETTIMEOUT = 16;
/*     */   static final int DIRTY_BIT_SCHEMA = 32;
/*  72 */   private static final Logger LOGGER = LoggerFactory.getLogger(ProxyConnection.class);
/*     */   
/*  74 */   private static final Set<String> ERROR_STATES = new HashSet<>(); static {
/*  75 */     ERROR_STATES.add("0A000");
/*  76 */     ERROR_STATES.add("57P01");
/*  77 */     ERROR_STATES.add("57P02");
/*  78 */     ERROR_STATES.add("57P03");
/*  79 */     ERROR_STATES.add("01002");
/*  80 */     ERROR_STATES.add("JZ0C0");
/*  81 */     ERROR_STATES.add("JZ0C1");
/*     */   }
/*  83 */   protected Connection delegate; private final PoolEntry poolEntry; private final ProxyLeakTask leakTask; private static final Set<Integer> ERROR_CODES = new HashSet<>(); private final FastList<Statement> openStatements; private int dirtyBits; private long lastAccess; static {
/*  84 */     ERROR_CODES.add(Integer.valueOf(500150));
/*  85 */     ERROR_CODES.add(Integer.valueOf(2399));
/*     */   }
/*     */   private boolean isCommitStateDirty; private boolean isReadOnly;
/*     */   private boolean isAutoCommit;
/*     */   private int networkTimeout;
/*     */   private int transactionIsolation;
/*     */   private String dbcatalog;
/*     */   private String dbschema;
/*     */   
/*     */   protected ProxyConnection(PoolEntry poolEntry, Connection connection, FastList<Statement> openStatements, ProxyLeakTask leakTask, long now, boolean isReadOnly, boolean isAutoCommit) {
/*  95 */     this.poolEntry = poolEntry;
/*  96 */     this.delegate = connection;
/*  97 */     this.openStatements = openStatements;
/*  98 */     this.leakTask = leakTask;
/*  99 */     this.lastAccess = now;
/* 100 */     this.isReadOnly = isReadOnly;
/* 101 */     this.isAutoCommit = isAutoCommit;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final String toString() {
/* 108 */     return getClass().getSimpleName() + '@' + System.identityHashCode(this) + " wrapping " + this.delegate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final boolean getAutoCommitState() {
/* 117 */     return this.isAutoCommit;
/*     */   }
/*     */ 
/*     */   
/*     */   final String getCatalogState() {
/* 122 */     return this.dbcatalog;
/*     */   }
/*     */ 
/*     */   
/*     */   final String getSchemaState() {
/* 127 */     return this.dbschema;
/*     */   }
/*     */ 
/*     */   
/*     */   final int getTransactionIsolationState() {
/* 132 */     return this.transactionIsolation;
/*     */   }
/*     */ 
/*     */   
/*     */   final boolean getReadOnlyState() {
/* 137 */     return this.isReadOnly;
/*     */   }
/*     */ 
/*     */   
/*     */   final int getNetworkTimeoutState() {
/* 142 */     return this.networkTimeout;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final PoolEntry getPoolEntry() {
/* 151 */     return this.poolEntry;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   final SQLException checkException(SQLException sqle) {
/* 157 */     boolean evict = false;
/* 158 */     SQLException nse = sqle;
/* 159 */     SQLExceptionOverride exceptionOverride = (this.poolEntry.getPoolBase()).exceptionOverride;
/* 160 */     for (int depth = 0; this.delegate != ClosedConnection.CLOSED_CONNECTION && nse != null && depth < 10; depth++) {
/* 161 */       String sqlState = nse.getSQLState();
/* 162 */       if ((sqlState != null && sqlState.startsWith("08")) || nse instanceof java.sql.SQLTimeoutException || ERROR_STATES
/*     */         
/* 164 */         .contains(sqlState) || ERROR_CODES
/* 165 */         .contains(Integer.valueOf(nse.getErrorCode()))) {
/*     */         
/* 167 */         if (exceptionOverride != null && exceptionOverride.adjudicate(nse) == SQLExceptionOverride.Override.DO_NOT_EVICT) {
/*     */           break;
/*     */         }
/*     */ 
/*     */         
/* 172 */         evict = true;
/*     */         
/*     */         break;
/*     */       } 
/* 176 */       nse = nse.getNextException();
/*     */     } 
/*     */ 
/*     */     
/* 180 */     if (evict) {
/* 181 */       SQLException exception = (nse != null) ? nse : sqle;
/* 182 */       LOGGER.warn("{} - Connection {} marked as broken because of SQLSTATE({}), ErrorCode({})", new Object[] { this.poolEntry
/* 183 */             .getPoolName(), this.delegate, exception.getSQLState(), Integer.valueOf(exception.getErrorCode()), exception });
/* 184 */       this.leakTask.cancel();
/* 185 */       this.poolEntry.evict("(connection is broken)");
/* 186 */       this.delegate = ClosedConnection.CLOSED_CONNECTION;
/*     */     } 
/*     */     
/* 189 */     return sqle;
/*     */   }
/*     */ 
/*     */   
/*     */   final synchronized void untrackStatement(Statement statement) {
/* 194 */     this.openStatements.remove(statement);
/*     */   }
/*     */ 
/*     */   
/*     */   final void markCommitStateDirty() {
/* 199 */     if (this.isAutoCommit) {
/* 200 */       this.lastAccess = ClockSource.currentTime();
/*     */     } else {
/*     */       
/* 203 */       this.isCommitStateDirty = true;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   void cancelLeakTask() {
/* 209 */     this.leakTask.cancel();
/*     */   }
/*     */ 
/*     */   
/*     */   private synchronized <T extends Statement> T trackStatement(T statement) {
/* 214 */     this.openStatements.add(statement);
/*     */     
/* 216 */     return statement;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private synchronized void closeStatements() {
/* 222 */     int size = this.openStatements.size();
/* 223 */     if (size > 0) {
/* 224 */       for (int i = 0; i < size && this.delegate != ClosedConnection.CLOSED_CONNECTION; i++) { try {
/* 225 */           Statement ignored = (Statement)this.openStatements.get(i);
/*     */           
/* 227 */           if (ignored != null) ignored.close(); 
/* 228 */         } catch (SQLException e) {
/* 229 */           LOGGER.warn("{} - Connection {} marked as broken because of an exception closing open statements during Connection.close()", this.poolEntry
/* 230 */               .getPoolName(), this.delegate);
/* 231 */           this.leakTask.cancel();
/* 232 */           this.poolEntry.evict("(exception closing Statements during Connection.close())");
/* 233 */           this.delegate = ClosedConnection.CLOSED_CONNECTION;
/*     */         }  }
/*     */ 
/*     */       
/* 237 */       this.openStatements.clear();
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
/*     */   public final void close() throws SQLException {
/* 250 */     closeStatements();
/*     */     
/* 252 */     if (this.delegate != ClosedConnection.CLOSED_CONNECTION) {
/* 253 */       this.leakTask.cancel();
/*     */       
/*     */       try {
/* 256 */         if (this.isCommitStateDirty && !this.isAutoCommit) {
/* 257 */           this.delegate.rollback();
/* 258 */           this.lastAccess = ClockSource.currentTime();
/* 259 */           LOGGER.debug("{} - Executed rollback on connection {} due to dirty commit state on close().", this.poolEntry.getPoolName(), this.delegate);
/*     */         } 
/*     */         
/* 262 */         if (this.dirtyBits != 0) {
/* 263 */           this.poolEntry.resetConnectionState(this, this.dirtyBits);
/* 264 */           this.lastAccess = ClockSource.currentTime();
/*     */         } 
/*     */         
/* 267 */         this.delegate.clearWarnings();
/*     */       }
/* 269 */       catch (SQLException e) {
/*     */         
/* 271 */         if (!this.poolEntry.isMarkedEvicted()) {
/* 272 */           throw checkException(e);
/*     */         }
/*     */       } finally {
/*     */         
/* 276 */         this.delegate = ClosedConnection.CLOSED_CONNECTION;
/* 277 */         this.poolEntry.recycle(this.lastAccess);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isClosed() throws SQLException {
/* 287 */     return (this.delegate == ClosedConnection.CLOSED_CONNECTION);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Statement createStatement() throws SQLException {
/* 294 */     return ProxyFactory.getProxyStatement(this, trackStatement(this.delegate.createStatement()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Statement createStatement(int resultSetType, int concurrency) throws SQLException {
/* 301 */     return ProxyFactory.getProxyStatement(this, trackStatement(this.delegate.createStatement(resultSetType, concurrency)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Statement createStatement(int resultSetType, int concurrency, int holdability) throws SQLException {
/* 308 */     return ProxyFactory.getProxyStatement(this, trackStatement(this.delegate.createStatement(resultSetType, concurrency, holdability)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CallableStatement prepareCall(String sql) throws SQLException {
/* 316 */     return ProxyFactory.getProxyCallableStatement(this, trackStatement(this.delegate.prepareCall(sql)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CallableStatement prepareCall(String sql, int resultSetType, int concurrency) throws SQLException {
/* 323 */     return ProxyFactory.getProxyCallableStatement(this, trackStatement(this.delegate.prepareCall(sql, resultSetType, concurrency)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CallableStatement prepareCall(String sql, int resultSetType, int concurrency, int holdability) throws SQLException {
/* 330 */     return ProxyFactory.getProxyCallableStatement(this, trackStatement(this.delegate.prepareCall(sql, resultSetType, concurrency, holdability)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PreparedStatement prepareStatement(String sql) throws SQLException {
/* 337 */     return ProxyFactory.getProxyPreparedStatement(this, trackStatement(this.delegate.prepareStatement(sql)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
/* 344 */     return ProxyFactory.getProxyPreparedStatement(this, trackStatement(this.delegate.prepareStatement(sql, autoGeneratedKeys)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PreparedStatement prepareStatement(String sql, int resultSetType, int concurrency) throws SQLException {
/* 351 */     return ProxyFactory.getProxyPreparedStatement(this, trackStatement(this.delegate.prepareStatement(sql, resultSetType, concurrency)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PreparedStatement prepareStatement(String sql, int resultSetType, int concurrency, int holdability) throws SQLException {
/* 358 */     return ProxyFactory.getProxyPreparedStatement(this, trackStatement(this.delegate.prepareStatement(sql, resultSetType, concurrency, holdability)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
/* 365 */     return ProxyFactory.getProxyPreparedStatement(this, trackStatement(this.delegate.prepareStatement(sql, columnIndexes)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
/* 372 */     return ProxyFactory.getProxyPreparedStatement(this, trackStatement(this.delegate.prepareStatement(sql, columnNames)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DatabaseMetaData getMetaData() throws SQLException {
/* 379 */     markCommitStateDirty();
/* 380 */     return ProxyFactory.getProxyDatabaseMetaData(this, this.delegate.getMetaData());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void commit() throws SQLException {
/* 387 */     this.delegate.commit();
/* 388 */     this.isCommitStateDirty = false;
/* 389 */     this.lastAccess = ClockSource.currentTime();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void rollback() throws SQLException {
/* 396 */     this.delegate.rollback();
/* 397 */     this.isCommitStateDirty = false;
/* 398 */     this.lastAccess = ClockSource.currentTime();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void rollback(Savepoint savepoint) throws SQLException {
/* 405 */     this.delegate.rollback(savepoint);
/* 406 */     this.isCommitStateDirty = false;
/* 407 */     this.lastAccess = ClockSource.currentTime();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAutoCommit(boolean autoCommit) throws SQLException {
/* 414 */     this.delegate.setAutoCommit(autoCommit);
/* 415 */     this.isAutoCommit = autoCommit;
/* 416 */     this.dirtyBits |= 0x2;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setReadOnly(boolean readOnly) throws SQLException {
/* 423 */     this.delegate.setReadOnly(readOnly);
/* 424 */     this.isReadOnly = readOnly;
/* 425 */     this.isCommitStateDirty = false;
/* 426 */     this.dirtyBits |= 0x1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTransactionIsolation(int level) throws SQLException {
/* 433 */     this.delegate.setTransactionIsolation(level);
/* 434 */     this.transactionIsolation = level;
/* 435 */     this.dirtyBits |= 0x4;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCatalog(String catalog) throws SQLException {
/* 442 */     this.delegate.setCatalog(catalog);
/* 443 */     this.dbcatalog = catalog;
/* 444 */     this.dirtyBits |= 0x8;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
/* 451 */     this.delegate.setNetworkTimeout(executor, milliseconds);
/* 452 */     this.networkTimeout = milliseconds;
/* 453 */     this.dirtyBits |= 0x10;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSchema(String schema) throws SQLException {
/* 460 */     this.delegate.setSchema(schema);
/* 461 */     this.dbschema = schema;
/* 462 */     this.dirtyBits |= 0x20;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean isWrapperFor(Class<?> iface) throws SQLException {
/* 469 */     return (iface.isInstance(this.delegate) || (this.delegate != null && this.delegate.isWrapperFor(iface)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final <T> T unwrap(Class<T> iface) throws SQLException {
/* 477 */     if (iface.isInstance(this.delegate)) {
/* 478 */       return (T)this.delegate;
/*     */     }
/* 480 */     if (this.delegate != null) {
/* 481 */       return this.delegate.unwrap(iface);
/*     */     }
/*     */     
/* 484 */     throw new SQLException("Wrapped connection is not an instance of " + iface);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class ClosedConnection
/*     */   {
/* 493 */     static final Connection CLOSED_CONNECTION = getClosedConnection();
/*     */ 
/*     */     
/*     */     private static Connection getClosedConnection() {
/* 497 */       InvocationHandler handler = (proxy, method, args) -> {
/*     */           String methodName = method.getName();
/*     */           
/*     */           if ("isClosed".equals(methodName)) {
/*     */             return Boolean.TRUE;
/*     */           }
/*     */           
/*     */           if ("isValid".equals(methodName)) {
/*     */             return Boolean.FALSE;
/*     */           }
/*     */           if ("abort".equals(methodName)) {
/*     */             return void.class;
/*     */           }
/*     */           if ("close".equals(methodName)) {
/*     */             return void.class;
/*     */           }
/*     */           if ("toString".equals(methodName)) {
/*     */             return ClosedConnection.class.getCanonicalName();
/*     */           }
/*     */           throw new SQLException("Connection is closed");
/*     */         };
/* 518 */       return (Connection)Proxy.newProxyInstance(Connection.class.getClassLoader(), new Class[] { Connection.class }, handler);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\zaxxer\hikari\pool\ProxyConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */