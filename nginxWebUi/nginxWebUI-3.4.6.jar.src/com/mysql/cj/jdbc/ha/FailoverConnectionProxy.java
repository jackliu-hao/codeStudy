/*     */ package com.mysql.cj.jdbc.ha;
/*     */ 
/*     */ import com.mysql.cj.conf.ConnectionUrl;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.exceptions.CJException;
/*     */ import com.mysql.cj.jdbc.ConnectionImpl;
/*     */ import com.mysql.cj.jdbc.JdbcConnection;
/*     */ import com.mysql.cj.jdbc.JdbcPropertySetImpl;
/*     */ import com.mysql.cj.jdbc.exceptions.SQLError;
/*     */ import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
/*     */ import com.mysql.cj.util.Util;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.sql.SQLException;
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
/*     */ 
/*     */ 
/*     */ public class FailoverConnectionProxy
/*     */   extends MultiHostConnectionProxy
/*     */ {
/*     */   private static final String METHOD_SET_READ_ONLY = "setReadOnly";
/*     */   private static final String METHOD_SET_AUTO_COMMIT = "setAutoCommit";
/*     */   private static final String METHOD_COMMIT = "commit";
/*     */   private static final String METHOD_ROLLBACK = "rollback";
/*     */   private static final int NO_CONNECTION_INDEX = -1;
/*     */   private static final int DEFAULT_PRIMARY_HOST_INDEX = 0;
/*     */   private int secondsBeforeRetryPrimaryHost;
/*     */   private long queriesBeforeRetryPrimaryHost;
/*     */   private boolean failoverReadOnly;
/*     */   private int retriesAllDown;
/*  69 */   private int currentHostIndex = -1;
/*  70 */   private int primaryHostIndex = 0;
/*  71 */   private Boolean explicitlyReadOnly = null;
/*     */   
/*     */   private boolean explicitlyAutoCommit = true;
/*     */   private boolean enableFallBackToPrimaryHost = true;
/*  75 */   private long primaryHostFailTimeMillis = 0L;
/*  76 */   private long queriesIssuedSinceFailover = 0L;
/*     */ 
/*     */ 
/*     */   
/*     */   class FailoverJdbcInterfaceProxy
/*     */     extends MultiHostConnectionProxy.JdbcInterfaceProxy
/*     */   {
/*     */     FailoverJdbcInterfaceProxy(Object toInvokeOn) {
/*  84 */       super(toInvokeOn);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
/*  90 */       String methodName = method.getName();
/*     */       
/*  92 */       boolean isExecute = methodName.startsWith("execute");
/*     */       
/*  94 */       if (FailoverConnectionProxy.this.connectedToSecondaryHost() && isExecute) {
/*  95 */         FailoverConnectionProxy.this.incrementQueriesIssuedSinceFailover();
/*     */       }
/*     */       
/*  98 */       Object result = super.invoke(proxy, method, args);
/*     */       
/* 100 */       if (FailoverConnectionProxy.this.explicitlyAutoCommit && isExecute && FailoverConnectionProxy.this.readyToFallBackToPrimaryHost())
/*     */       {
/* 102 */         FailoverConnectionProxy.this.fallBackToPrimaryIfAvailable();
/*     */       }
/*     */       
/* 105 */       return result;
/*     */     }
/*     */   }
/*     */   
/*     */   public static JdbcConnection createProxyInstance(ConnectionUrl connectionUrl) throws SQLException {
/* 110 */     FailoverConnectionProxy connProxy = new FailoverConnectionProxy(connectionUrl);
/*     */     
/* 112 */     return (JdbcConnection)Proxy.newProxyInstance(JdbcConnection.class.getClassLoader(), new Class[] { JdbcConnection.class }, connProxy);
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
/*     */   private FailoverConnectionProxy(ConnectionUrl connectionUrl) throws SQLException {
/* 125 */     super(connectionUrl);
/*     */     
/* 127 */     JdbcPropertySetImpl connProps = new JdbcPropertySetImpl();
/* 128 */     connProps.initializeProperties(connectionUrl.getConnectionArgumentsAsProperties());
/*     */     
/* 130 */     this.secondsBeforeRetryPrimaryHost = ((Integer)connProps.getIntegerProperty(PropertyKey.secondsBeforeRetrySource).getValue()).intValue();
/* 131 */     this.queriesBeforeRetryPrimaryHost = ((Integer)connProps.getIntegerProperty(PropertyKey.queriesBeforeRetrySource).getValue()).intValue();
/* 132 */     this.failoverReadOnly = ((Boolean)connProps.getBooleanProperty(PropertyKey.failOverReadOnly).getValue()).booleanValue();
/* 133 */     this.retriesAllDown = ((Integer)connProps.getIntegerProperty(PropertyKey.retriesAllDown).getValue()).intValue();
/*     */     
/* 135 */     this.enableFallBackToPrimaryHost = (this.secondsBeforeRetryPrimaryHost > 0 || this.queriesBeforeRetryPrimaryHost > 0L);
/*     */     
/* 137 */     pickNewConnection();
/*     */     
/* 139 */     this.explicitlyAutoCommit = this.currentConnection.getAutoCommit();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   MultiHostConnectionProxy.JdbcInterfaceProxy getNewJdbcInterfaceProxy(Object toProxy) {
/* 148 */     return new FailoverJdbcInterfaceProxy(toProxy);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean shouldExceptionTriggerConnectionSwitch(Throwable t) {
/* 157 */     String sqlState = null;
/* 158 */     if (t instanceof com.mysql.cj.jdbc.exceptions.CommunicationsException || t instanceof com.mysql.cj.exceptions.CJCommunicationsException)
/* 159 */       return true; 
/* 160 */     if (t instanceof SQLException) {
/* 161 */       sqlState = ((SQLException)t).getSQLState();
/* 162 */     } else if (t instanceof CJException) {
/* 163 */       sqlState = ((CJException)t).getSQLState();
/*     */     } 
/*     */     
/* 166 */     if (sqlState != null && 
/* 167 */       sqlState.startsWith("08"))
/*     */     {
/* 169 */       return true;
/*     */     }
/*     */ 
/*     */     
/* 173 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isSourceConnection() {
/* 181 */     return connectedToPrimaryHost();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized void pickNewConnection() throws SQLException {
/* 189 */     if (this.isClosed && this.closedExplicitly) {
/*     */       return;
/*     */     }
/*     */     
/* 193 */     if (!isConnected() || readyToFallBackToPrimaryHost()) {
/*     */       try {
/* 195 */         connectTo(this.primaryHostIndex);
/* 196 */       } catch (SQLException e) {
/* 197 */         resetAutoFallBackCounters();
/* 198 */         failOver(this.primaryHostIndex);
/*     */       } 
/*     */     } else {
/* 201 */       failOver();
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
/*     */   synchronized ConnectionImpl createConnectionForHostIndex(int hostIndex) throws SQLException {
/* 216 */     return createConnectionForHost(this.hostsList.get(hostIndex));
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
/*     */   private synchronized void connectTo(int hostIndex) throws SQLException {
/*     */     try {
/* 229 */       switchCurrentConnectionTo(hostIndex, (JdbcConnection)createConnectionForHostIndex(hostIndex));
/* 230 */     } catch (SQLException e) {
/* 231 */       if (this.currentConnection != null) {
/*     */         
/* 233 */         StringBuilder msg = (new StringBuilder("Connection to ")).append(isPrimaryHostIndex(hostIndex) ? "primary" : "secondary").append(" host '").append(this.hostsList.get(hostIndex)).append("' failed");
/*     */         try {
/* 235 */           this.currentConnection.getSession().getLog().logWarn(msg.toString(), e);
/* 236 */         } catch (CJException ex) {
/* 237 */           throw SQLExceptionsMapping.translateException(e, this.currentConnection.getExceptionInterceptor());
/*     */         } 
/*     */       } 
/* 240 */       throw e;
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
/*     */   private synchronized void switchCurrentConnectionTo(int hostIndex, JdbcConnection connection) throws SQLException {
/*     */     boolean readOnly;
/* 255 */     invalidateCurrentConnection();
/*     */ 
/*     */     
/* 258 */     if (isPrimaryHostIndex(hostIndex)) {
/* 259 */       readOnly = (this.explicitlyReadOnly == null) ? false : this.explicitlyReadOnly.booleanValue();
/* 260 */     } else if (this.failoverReadOnly) {
/* 261 */       readOnly = true;
/* 262 */     } else if (this.explicitlyReadOnly != null) {
/* 263 */       readOnly = this.explicitlyReadOnly.booleanValue();
/* 264 */     } else if (this.currentConnection != null) {
/* 265 */       readOnly = this.currentConnection.isReadOnly();
/*     */     } else {
/* 267 */       readOnly = false;
/*     */     } 
/* 269 */     syncSessionState(this.currentConnection, connection, readOnly);
/* 270 */     this.currentConnection = connection;
/* 271 */     this.currentHostIndex = hostIndex;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private synchronized void failOver() throws SQLException {
/* 281 */     failOver(this.currentHostIndex);
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
/*     */   private synchronized void failOver(int failedHostIdx) throws SQLException {
/* 294 */     int prevHostIndex = this.currentHostIndex;
/* 295 */     int nextHostIndex = nextHost(failedHostIdx, false);
/* 296 */     int firstHostIndexTried = nextHostIndex;
/*     */     
/* 298 */     SQLException lastExceptionCaught = null;
/* 299 */     int attempts = 0;
/* 300 */     boolean gotConnection = false;
/* 301 */     boolean firstConnOrPassedByPrimaryHost = (prevHostIndex == -1 || isPrimaryHostIndex(prevHostIndex));
/*     */     do {
/*     */       try {
/* 304 */         firstConnOrPassedByPrimaryHost = (firstConnOrPassedByPrimaryHost || isPrimaryHostIndex(nextHostIndex));
/*     */         
/* 306 */         connectTo(nextHostIndex);
/*     */         
/* 308 */         if (firstConnOrPassedByPrimaryHost && connectedToSecondaryHost()) {
/* 309 */           resetAutoFallBackCounters();
/*     */         }
/* 311 */         gotConnection = true;
/*     */       }
/* 313 */       catch (SQLException e) {
/* 314 */         lastExceptionCaught = e;
/*     */         
/* 316 */         if (shouldExceptionTriggerConnectionSwitch(e)) {
/* 317 */           int newNextHostIndex = nextHost(nextHostIndex, (attempts > 0));
/*     */           
/* 319 */           if (newNextHostIndex == firstHostIndexTried && newNextHostIndex == (newNextHostIndex = nextHost(nextHostIndex, true))) {
/* 320 */             attempts++;
/*     */             
/*     */             try {
/* 323 */               Thread.sleep(250L);
/* 324 */             } catch (InterruptedException interruptedException) {}
/*     */           } 
/*     */ 
/*     */           
/* 328 */           nextHostIndex = newNextHostIndex;
/*     */         } else {
/*     */           
/* 331 */           throw e;
/*     */         } 
/*     */       } 
/* 334 */     } while (attempts < this.retriesAllDown && !gotConnection);
/*     */     
/* 336 */     if (!gotConnection) {
/* 337 */       throw lastExceptionCaught;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized void fallBackToPrimaryIfAvailable() {
/*     */     ConnectionImpl connectionImpl;
/* 345 */     JdbcConnection connection = null;
/*     */     try {
/* 347 */       connectionImpl = createConnectionForHostIndex(this.primaryHostIndex);
/* 348 */       switchCurrentConnectionTo(this.primaryHostIndex, (JdbcConnection)connectionImpl);
/* 349 */     } catch (SQLException e1) {
/* 350 */       if (connectionImpl != null) {
/*     */         try {
/* 352 */           connectionImpl.close();
/* 353 */         } catch (SQLException sQLException) {}
/*     */       }
/*     */ 
/*     */       
/* 357 */       resetAutoFallBackCounters();
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
/*     */ 
/*     */ 
/*     */   
/*     */   private int nextHost(int currHostIdx, boolean vouchForPrimaryHost) {
/* 375 */     int nextHostIdx = (currHostIdx + 1) % this.hostsList.size();
/* 376 */     if (isPrimaryHostIndex(nextHostIdx) && isConnected() && !vouchForPrimaryHost && this.enableFallBackToPrimaryHost && !readyToFallBackToPrimaryHost())
/*     */     {
/* 378 */       nextHostIdx = nextHost(nextHostIdx, vouchForPrimaryHost);
/*     */     }
/* 380 */     return nextHostIdx;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized void incrementQueriesIssuedSinceFailover() {
/* 387 */     this.queriesIssuedSinceFailover++;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized boolean readyToFallBackToPrimaryHost() {
/* 397 */     return (this.enableFallBackToPrimaryHost && connectedToSecondaryHost() && (secondsBeforeRetryPrimaryHostIsMet() || queriesBeforeRetryPrimaryHostIsMet()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized boolean isConnected() {
/* 406 */     return (this.currentHostIndex != -1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized boolean isPrimaryHostIndex(int hostIndex) {
/* 417 */     return (hostIndex == this.primaryHostIndex);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized boolean connectedToPrimaryHost() {
/* 426 */     return isPrimaryHostIndex(this.currentHostIndex);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized boolean connectedToSecondaryHost() {
/* 435 */     return (this.currentHostIndex >= 0 && !isPrimaryHostIndex(this.currentHostIndex));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private synchronized boolean secondsBeforeRetryPrimaryHostIsMet() {
/* 444 */     return (this.secondsBeforeRetryPrimaryHost > 0 && Util.secondsSinceMillis(this.primaryHostFailTimeMillis) >= this.secondsBeforeRetryPrimaryHost);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private synchronized boolean queriesBeforeRetryPrimaryHostIsMet() {
/* 453 */     return (this.queriesBeforeRetryPrimaryHost > 0L && this.queriesIssuedSinceFailover >= this.queriesBeforeRetryPrimaryHost);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private synchronized void resetAutoFallBackCounters() {
/* 460 */     this.primaryHostFailTimeMillis = System.currentTimeMillis();
/* 461 */     this.queriesIssuedSinceFailover = 0L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized void doClose() throws SQLException {
/* 472 */     this.currentConnection.close();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized void doAbortInternal() throws SQLException {
/* 483 */     this.currentConnection.abortInternal();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized void doAbort(Executor executor) throws SQLException {
/* 494 */     this.currentConnection.abort(executor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object invokeMore(Object proxy, Method method, Object[] args) throws Throwable {
/* 503 */     String methodName = method.getName();
/*     */     
/* 505 */     if ("setReadOnly".equals(methodName)) {
/* 506 */       this.explicitlyReadOnly = (Boolean)args[0];
/* 507 */       if (this.failoverReadOnly && connectedToSecondaryHost()) {
/* 508 */         return null;
/*     */       }
/*     */     } 
/*     */     
/* 512 */     if (this.isClosed && !allowedOnClosedConnection(method)) {
/* 513 */       if (this.autoReconnect && !this.closedExplicitly) {
/* 514 */         this.currentHostIndex = -1;
/* 515 */         pickNewConnection();
/* 516 */         this.isClosed = false;
/* 517 */         this.closedReason = null;
/*     */       } else {
/* 519 */         String reason = "No operations allowed after connection closed.";
/* 520 */         if (this.closedReason != null) {
/* 521 */           reason = reason + "  " + this.closedReason;
/*     */         }
/* 523 */         throw SQLError.createSQLException(reason, "08003", null);
/*     */       } 
/*     */     }
/*     */     
/* 527 */     Object result = null;
/*     */     
/*     */     try {
/* 530 */       result = method.invoke(this.thisAsConnection, args);
/* 531 */       result = proxyIfReturnTypeIsJdbcInterface(method.getReturnType(), result);
/* 532 */     } catch (InvocationTargetException e) {
/* 533 */       dealWithInvocationException(e);
/*     */     } 
/*     */     
/* 536 */     if ("setAutoCommit".equals(methodName)) {
/* 537 */       this.explicitlyAutoCommit = ((Boolean)args[0]).booleanValue();
/*     */     }
/*     */     
/* 540 */     if ((this.explicitlyAutoCommit || "commit".equals(methodName) || "rollback".equals(methodName)) && readyToFallBackToPrimaryHost())
/*     */     {
/* 542 */       fallBackToPrimaryIfAvailable();
/*     */     }
/*     */     
/* 545 */     return result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\ha\FailoverConnectionProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */