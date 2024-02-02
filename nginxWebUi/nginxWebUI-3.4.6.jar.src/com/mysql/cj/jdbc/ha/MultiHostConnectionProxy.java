/*     */ package com.mysql.cj.jdbc.ha;
/*     */ 
/*     */ import com.mysql.cj.conf.ConnectionUrl;
/*     */ import com.mysql.cj.conf.HostInfo;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.conf.RuntimeProperty;
/*     */ import com.mysql.cj.jdbc.ConnectionImpl;
/*     */ import com.mysql.cj.jdbc.JdbcConnection;
/*     */ import com.mysql.cj.util.Util;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class MultiHostConnectionProxy
/*     */   implements InvocationHandler
/*     */ {
/*     */   private static final String METHOD_GET_MULTI_HOST_SAFE_PROXY = "getMultiHostSafeProxy";
/*     */   private static final String METHOD_EQUALS = "equals";
/*     */   private static final String METHOD_CLOSE = "close";
/*     */   private static final String METHOD_ABORT_INTERNAL = "abortInternal";
/*     */   private static final String METHOD_ABORT = "abort";
/*     */   private static final String METHOD_IS_CLOSED = "isClosed";
/*     */   private static final String METHOD_GET_AUTO_COMMIT = "getAutoCommit";
/*     */   private static final String METHOD_GET_CATALOG = "getCatalog";
/*     */   private static final String METHOD_GET_SCHEMA = "getSchema";
/*     */   private static final String METHOD_GET_DATABASE = "getDatabase";
/*     */   private static final String METHOD_GET_TRANSACTION_ISOLATION = "getTransactionIsolation";
/*     */   private static final String METHOD_GET_SESSION_MAX_ROWS = "getSessionMaxRows";
/*     */   List<HostInfo> hostsList;
/*     */   protected ConnectionUrl connectionUrl;
/*     */   boolean autoReconnect = false;
/*  73 */   JdbcConnection thisAsConnection = null;
/*  74 */   JdbcConnection parentProxyConnection = null;
/*  75 */   JdbcConnection topProxyConnection = null;
/*     */   
/*  77 */   JdbcConnection currentConnection = null;
/*     */   
/*     */   boolean isClosed = false;
/*     */   boolean closedExplicitly = false;
/*  81 */   String closedReason = null;
/*     */ 
/*     */ 
/*     */   
/*  85 */   protected Throwable lastExceptionDealtWith = null;
/*     */ 
/*     */   
/*     */   class JdbcInterfaceProxy
/*     */     implements InvocationHandler
/*     */   {
/*  91 */     Object invokeOn = null;
/*     */     
/*     */     JdbcInterfaceProxy(Object toInvokeOn) {
/*  94 */       this.invokeOn = toInvokeOn;
/*     */     }
/*     */     
/*     */     public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
/*  98 */       if ("equals".equals(method.getName()))
/*     */       {
/* 100 */         return Boolean.valueOf(args[0].equals(this));
/*     */       }
/*     */       
/* 103 */       synchronized (MultiHostConnectionProxy.this) {
/* 104 */         Object result = null;
/*     */         
/*     */         try {
/* 107 */           result = method.invoke(this.invokeOn, args);
/* 108 */           result = MultiHostConnectionProxy.this.proxyIfReturnTypeIsJdbcInterface(method.getReturnType(), result);
/* 109 */         } catch (InvocationTargetException e) {
/* 110 */           MultiHostConnectionProxy.this.dealWithInvocationException(e);
/*     */         } 
/*     */         
/* 113 */         return result;
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
/*     */   MultiHostConnectionProxy() throws SQLException {
/* 125 */     this.thisAsConnection = getNewWrapperForThisAsConnection();
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
/*     */   MultiHostConnectionProxy(ConnectionUrl connectionUrl) throws SQLException {
/* 137 */     this();
/* 138 */     initializeHostsSpecs(connectionUrl, connectionUrl.getHostsList());
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
/*     */   int initializeHostsSpecs(ConnectionUrl connUrl, List<HostInfo> hosts) {
/* 153 */     this.connectionUrl = connUrl;
/*     */     
/* 155 */     Properties props = connUrl.getConnectionArgumentsAsProperties();
/*     */     
/* 157 */     this
/* 158 */       .autoReconnect = ("true".equalsIgnoreCase(props.getProperty(PropertyKey.autoReconnect.getKeyName())) || "true".equalsIgnoreCase(props.getProperty(PropertyKey.autoReconnectForPools.getKeyName())));
/*     */     
/* 160 */     this.hostsList = new ArrayList<>(hosts);
/* 161 */     int numHosts = this.hostsList.size();
/* 162 */     return numHosts;
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
/*     */   protected JdbcConnection getProxy() {
/* 174 */     return (this.topProxyConnection != null) ? this.topProxyConnection : this.thisAsConnection;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected JdbcConnection getParentProxy() {
/* 184 */     return this.parentProxyConnection;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void setProxy(JdbcConnection proxyConn) {
/* 195 */     if (this.parentProxyConnection == null) {
/* 196 */       this.parentProxyConnection = proxyConn;
/*     */     }
/* 198 */     this.topProxyConnection = proxyConn;
/* 199 */     propagateProxyDown(proxyConn);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void propagateProxyDown(JdbcConnection proxyConn) {
/* 210 */     this.currentConnection.setProxy(proxyConn);
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
/*     */   JdbcConnection getNewWrapperForThisAsConnection() throws SQLException {
/* 222 */     return new MultiHostMySQLConnection(this);
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
/*     */   Object proxyIfReturnTypeIsJdbcInterface(Class<?> returnType, Object toProxy) {
/* 236 */     if (toProxy != null && 
/* 237 */       Util.isJdbcInterface(returnType)) {
/* 238 */       Class<?> toProxyClass = toProxy.getClass();
/* 239 */       return Proxy.newProxyInstance(toProxyClass.getClassLoader(), Util.getImplementedInterfaces(toProxyClass), getNewJdbcInterfaceProxy(toProxy));
/*     */     } 
/*     */     
/* 242 */     return toProxy;
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
/*     */   InvocationHandler getNewJdbcInterfaceProxy(Object toProxy) {
/* 254 */     return new JdbcInterfaceProxy(toProxy);
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
/*     */   void dealWithInvocationException(InvocationTargetException e) throws SQLException, Throwable, InvocationTargetException {
/* 270 */     Throwable t = e.getTargetException();
/*     */     
/* 272 */     if (t != null) {
/* 273 */       if (this.lastExceptionDealtWith != t && shouldExceptionTriggerConnectionSwitch(t)) {
/* 274 */         invalidateCurrentConnection();
/* 275 */         pickNewConnection();
/* 276 */         this.lastExceptionDealtWith = t;
/*     */       } 
/* 278 */       throw t;
/*     */     } 
/* 280 */     throw e;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   abstract boolean shouldExceptionTriggerConnectionSwitch(Throwable paramThrowable);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   abstract boolean isSourceConnection();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   boolean isMasterConnection() {
/* 307 */     return isSourceConnection();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized void invalidateCurrentConnection() throws SQLException {
/* 317 */     invalidateConnection(this.currentConnection);
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
/*     */   synchronized void invalidateConnection(JdbcConnection conn) throws SQLException {
/*     */     try {
/* 330 */       if (conn != null && !conn.isClosed()) {
/* 331 */         conn.realClose(true, !conn.getAutoCommit(), true, null);
/*     */       }
/* 333 */     } catch (SQLException sQLException) {}
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
/*     */   abstract void pickNewConnection() throws SQLException;
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
/*     */   synchronized ConnectionImpl createConnectionForHost(HostInfo hostInfo) throws SQLException {
/* 357 */     ConnectionImpl conn = (ConnectionImpl)ConnectionImpl.getInstance(hostInfo);
/* 358 */     JdbcConnection topmostProxy = getProxy();
/* 359 */     if (topmostProxy != this.thisAsConnection) {
/* 360 */       conn.setProxy(this.thisAsConnection);
/*     */     }
/* 362 */     conn.setProxy(topmostProxy);
/* 363 */     return conn;
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
/*     */   void syncSessionState(JdbcConnection source, JdbcConnection target) throws SQLException {
/* 377 */     if (source == null || target == null) {
/*     */       return;
/*     */     }
/*     */     
/* 381 */     RuntimeProperty<Boolean> sourceUseLocalSessionState = source.getPropertySet().getBooleanProperty(PropertyKey.useLocalSessionState);
/* 382 */     boolean prevUseLocalSessionState = ((Boolean)sourceUseLocalSessionState.getValue()).booleanValue();
/* 383 */     sourceUseLocalSessionState.setValue(Boolean.valueOf(true));
/* 384 */     boolean readOnly = source.isReadOnly();
/* 385 */     sourceUseLocalSessionState.setValue(Boolean.valueOf(prevUseLocalSessionState));
/*     */     
/* 387 */     syncSessionState(source, target, readOnly);
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
/*     */   void syncSessionState(JdbcConnection source, JdbcConnection target, boolean readOnly) throws SQLException {
/* 403 */     if (target != null) {
/* 404 */       target.setReadOnly(readOnly);
/*     */     }
/*     */     
/* 407 */     if (source == null || target == null) {
/*     */       return;
/*     */     }
/*     */     
/* 411 */     RuntimeProperty<Boolean> sourceUseLocalSessionState = source.getPropertySet().getBooleanProperty(PropertyKey.useLocalSessionState);
/* 412 */     boolean prevUseLocalSessionState = ((Boolean)sourceUseLocalSessionState.getValue()).booleanValue();
/* 413 */     sourceUseLocalSessionState.setValue(Boolean.valueOf(true));
/*     */     
/* 415 */     target.setAutoCommit(source.getAutoCommit());
/* 416 */     String db = source.getDatabase();
/* 417 */     if (db != null && !db.isEmpty()) {
/* 418 */       target.setDatabase(db);
/*     */     }
/* 420 */     target.setTransactionIsolation(source.getTransactionIsolation());
/* 421 */     target.setSessionMaxRows(source.getSessionMaxRows());
/*     */     
/* 423 */     sourceUseLocalSessionState.setValue(Boolean.valueOf(prevUseLocalSessionState));
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
/*     */   abstract void doClose() throws SQLException;
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
/*     */   abstract void doAbortInternal() throws SQLException;
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
/*     */   abstract void doAbort(Executor paramExecutor) throws SQLException;
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
/*     */   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
/* 469 */     String methodName = method.getName();
/*     */     
/* 471 */     if ("getMultiHostSafeProxy".equals(methodName)) {
/* 472 */       return this.thisAsConnection;
/*     */     }
/*     */     
/* 475 */     if ("equals".equals(methodName))
/*     */     {
/* 477 */       return Boolean.valueOf(args[0].equals(this));
/*     */     }
/*     */ 
/*     */     
/* 481 */     if (method.getDeclaringClass().equals(Object.class)) {
/* 482 */       return method.invoke(this, args);
/*     */     }
/*     */     
/* 485 */     synchronized (this) {
/* 486 */       if ("close".equals(methodName)) {
/* 487 */         doClose();
/* 488 */         this.isClosed = true;
/* 489 */         this.closedReason = "Connection explicitly closed.";
/* 490 */         this.closedExplicitly = true;
/* 491 */         return null;
/*     */       } 
/*     */       
/* 494 */       if ("abortInternal".equals(methodName)) {
/* 495 */         doAbortInternal();
/* 496 */         this.currentConnection.abortInternal();
/* 497 */         this.isClosed = true;
/* 498 */         this.closedReason = "Connection explicitly closed.";
/* 499 */         return null;
/*     */       } 
/*     */       
/* 502 */       if ("abort".equals(methodName) && args.length == 1) {
/* 503 */         doAbort((Executor)args[0]);
/* 504 */         this.isClosed = true;
/* 505 */         this.closedReason = "Connection explicitly closed.";
/* 506 */         return null;
/*     */       } 
/*     */       
/* 509 */       if ("isClosed".equals(methodName)) {
/* 510 */         return Boolean.valueOf(this.isClosed);
/*     */       }
/*     */       
/*     */       try {
/* 514 */         return invokeMore(proxy, method, args);
/* 515 */       } catch (InvocationTargetException e) {
/* 516 */         throw (e.getCause() != null) ? e.getCause() : e;
/* 517 */       } catch (Exception e) {
/*     */         
/* 519 */         Class<?>[] declaredException = method.getExceptionTypes();
/* 520 */         for (Class<?> declEx : declaredException) {
/* 521 */           if (declEx.isAssignableFrom(e.getClass())) {
/* 522 */             throw e;
/*     */           }
/*     */         } 
/* 525 */         throw new IllegalStateException(e.getMessage(), e);
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
/*     */ 
/*     */   
/*     */   abstract Object invokeMore(Object paramObject, Method paramMethod, Object[] paramArrayOfObject) throws Throwable;
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
/*     */   protected boolean allowedOnClosedConnection(Method method) {
/* 553 */     String methodName = method.getName();
/*     */     
/* 555 */     return (methodName.equals("getAutoCommit") || methodName.equals("getCatalog") || methodName.equals("getSchema") || methodName
/* 556 */       .equals("getDatabase") || methodName.equals("getTransactionIsolation") || methodName
/* 557 */       .equals("getSessionMaxRows"));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\ha\MultiHostConnectionProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */