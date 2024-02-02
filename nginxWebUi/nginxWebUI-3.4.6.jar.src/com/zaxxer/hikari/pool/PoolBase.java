/*     */ package com.zaxxer.hikari.pool;
/*     */ 
/*     */ import com.zaxxer.hikari.HikariConfig;
/*     */ import com.zaxxer.hikari.SQLExceptionOverride;
/*     */ import com.zaxxer.hikari.metrics.IMetricsTracker;
/*     */ import com.zaxxer.hikari.util.ClockSource;
/*     */ import com.zaxxer.hikari.util.DriverDataSource;
/*     */ import com.zaxxer.hikari.util.PropertyElf;
/*     */ import com.zaxxer.hikari.util.UtilityElf;
/*     */ import java.lang.management.ManagementFactory;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLTransientConnectionException;
/*     */ import java.sql.Statement;
/*     */ import java.util.Properties;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import java.util.concurrent.ThreadPoolExecutor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import javax.management.MBeanServer;
/*     */ import javax.management.ObjectName;
/*     */ import javax.naming.InitialContext;
/*     */ import javax.naming.NamingException;
/*     */ import javax.sql.DataSource;
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
/*     */ abstract class PoolBase
/*     */ {
/*  55 */   private final Logger logger = LoggerFactory.getLogger(PoolBase.class);
/*     */   
/*     */   public final HikariConfig config;
/*     */   
/*     */   IMetricsTrackerDelegate metricsTracker;
/*     */   
/*     */   protected final String poolName;
/*     */   
/*     */   volatile String catalog;
/*     */   
/*     */   final AtomicReference<Exception> lastConnectionFailure;
/*     */   
/*     */   long connectionTimeout;
/*     */   long validationTimeout;
/*     */   SQLExceptionOverride exceptionOverride;
/*  70 */   private static final String[] RESET_STATES = new String[] { "readOnly", "autoCommit", "isolation", "catalog", "netTimeout", "schema" };
/*     */   
/*     */   private static final int UNINITIALIZED = -1;
/*     */   
/*     */   private static final int TRUE = 1;
/*     */   
/*     */   private static final int FALSE = 0;
/*     */   
/*     */   private int networkTimeout;
/*     */   
/*     */   private int isNetworkTimeoutSupported;
/*     */   private int isQueryTimeoutSupported;
/*     */   private int defaultTransactionIsolation;
/*     */   private int transactionIsolation;
/*     */   private Executor netTimeoutExecutor;
/*     */   private DataSource dataSource;
/*     */   private final String schema;
/*     */   private final boolean isReadOnly;
/*     */   private final boolean isAutoCommit;
/*     */   private final boolean isUseJdbc4Validation;
/*     */   private final boolean isIsolateInternalQueries;
/*     */   private volatile boolean isValidChecked;
/*     */   
/*     */   PoolBase(HikariConfig config) {
/*  94 */     this.config = config;
/*     */     
/*  96 */     this.networkTimeout = -1;
/*  97 */     this.catalog = config.getCatalog();
/*  98 */     this.schema = config.getSchema();
/*  99 */     this.isReadOnly = config.isReadOnly();
/* 100 */     this.isAutoCommit = config.isAutoCommit();
/* 101 */     this.exceptionOverride = (SQLExceptionOverride)UtilityElf.createInstance(config.getExceptionOverrideClassName(), SQLExceptionOverride.class, new Object[0]);
/* 102 */     this.transactionIsolation = UtilityElf.getTransactionIsolation(config.getTransactionIsolation());
/*     */     
/* 104 */     this.isQueryTimeoutSupported = -1;
/* 105 */     this.isNetworkTimeoutSupported = -1;
/* 106 */     this.isUseJdbc4Validation = (config.getConnectionTestQuery() == null);
/* 107 */     this.isIsolateInternalQueries = config.isIsolateInternalQueries();
/*     */     
/* 109 */     this.poolName = config.getPoolName();
/* 110 */     this.connectionTimeout = config.getConnectionTimeout();
/* 111 */     this.validationTimeout = config.getValidationTimeout();
/* 112 */     this.lastConnectionFailure = new AtomicReference<>();
/*     */     
/* 114 */     initializeDataSource();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 121 */     return this.poolName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   abstract void recycle(PoolEntry paramPoolEntry);
/*     */ 
/*     */ 
/*     */   
/*     */   void quietlyCloseConnection(Connection connection, String closureReason) {
/* 132 */     if (connection != null) {
/*     */       try {
/* 134 */         this.logger.debug("{} - Closing connection {}: {}", new Object[] { this.poolName, connection, closureReason });
/*     */         
/*     */         try {
/* 137 */           setNetworkTimeout(connection, TimeUnit.SECONDS.toMillis(15L));
/*     */         }
/* 139 */         catch (SQLException sQLException) {
/*     */ 
/*     */         
/*     */         } finally {
/* 143 */           connection.close();
/*     */         }
/*     */       
/* 146 */       } catch (Exception e) {
/* 147 */         this.logger.debug("{} - Closing connection {} failed", new Object[] { this.poolName, connection, e });
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isConnectionAlive(Connection connection) {
/*     */     try {
/*     */       
/* 156 */       try { setNetworkTimeout(connection, this.validationTimeout);
/*     */         
/* 158 */         int validationSeconds = (int)Math.max(1000L, this.validationTimeout) / 1000;
/*     */         
/* 160 */         if (this.isUseJdbc4Validation) {
/* 161 */           return connection.isValid(validationSeconds);
/*     */         }
/*     */         
/* 164 */         Statement statement = connection.createStatement(); 
/* 165 */         try { if (this.isNetworkTimeoutSupported != 1) {
/* 166 */             setQueryTimeout(statement, validationSeconds);
/*     */           }
/*     */           
/* 169 */           statement.execute(this.config.getConnectionTestQuery());
/* 170 */           if (statement != null) statement.close();  } catch (Throwable throwable) { if (statement != null)
/*     */             try { statement.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }
/*     */          }
/* 173 */       finally { setNetworkTimeout(connection, this.networkTimeout);
/*     */         
/* 175 */         if (this.isIsolateInternalQueries && !this.isAutoCommit) {
/* 176 */           connection.rollback();
/*     */         } }
/*     */ 
/*     */       
/* 180 */       return true;
/*     */     }
/* 182 */     catch (Exception e) {
/* 183 */       this.lastConnectionFailure.set(e);
/* 184 */       this.logger.warn("{} - Failed to validate connection {} ({}). Possibly consider using a shorter maxLifetime value.", new Object[] { this.poolName, connection, e
/* 185 */             .getMessage() });
/* 186 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   Exception getLastConnectionFailure() {
/* 192 */     return this.lastConnectionFailure.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public DataSource getUnwrappedDataSource() {
/* 197 */     return this.dataSource;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   PoolEntry newPoolEntry() throws Exception {
/* 206 */     return new PoolEntry(newConnection(), this, this.isReadOnly, this.isAutoCommit);
/*     */   }
/*     */ 
/*     */   
/*     */   void resetConnectionState(Connection connection, ProxyConnection proxyConnection, int dirtyBits) throws SQLException {
/* 211 */     int resetBits = 0;
/*     */     
/* 213 */     if ((dirtyBits & 0x1) != 0 && proxyConnection.getReadOnlyState() != this.isReadOnly) {
/* 214 */       connection.setReadOnly(this.isReadOnly);
/* 215 */       resetBits |= 0x1;
/*     */     } 
/*     */     
/* 218 */     if ((dirtyBits & 0x2) != 0 && proxyConnection.getAutoCommitState() != this.isAutoCommit) {
/* 219 */       connection.setAutoCommit(this.isAutoCommit);
/* 220 */       resetBits |= 0x2;
/*     */     } 
/*     */     
/* 223 */     if ((dirtyBits & 0x4) != 0 && proxyConnection.getTransactionIsolationState() != this.transactionIsolation) {
/* 224 */       connection.setTransactionIsolation(this.transactionIsolation);
/* 225 */       resetBits |= 0x4;
/*     */     } 
/*     */     
/* 228 */     if ((dirtyBits & 0x8) != 0 && this.catalog != null && !this.catalog.equals(proxyConnection.getCatalogState())) {
/* 229 */       connection.setCatalog(this.catalog);
/* 230 */       resetBits |= 0x8;
/*     */     } 
/*     */     
/* 233 */     if ((dirtyBits & 0x10) != 0 && proxyConnection.getNetworkTimeoutState() != this.networkTimeout) {
/* 234 */       setNetworkTimeout(connection, this.networkTimeout);
/* 235 */       resetBits |= 0x10;
/*     */     } 
/*     */     
/* 238 */     if ((dirtyBits & 0x20) != 0 && this.schema != null && !this.schema.equals(proxyConnection.getSchemaState())) {
/* 239 */       connection.setSchema(this.schema);
/* 240 */       resetBits |= 0x20;
/*     */     } 
/*     */     
/* 243 */     if (resetBits != 0 && this.logger.isDebugEnabled()) {
/* 244 */       this.logger.debug("{} - Reset ({}) on connection {}", new Object[] { this.poolName, stringFromResetBits(resetBits), connection });
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   void shutdownNetworkTimeoutExecutor() {
/* 250 */     if (this.netTimeoutExecutor instanceof ThreadPoolExecutor) {
/* 251 */       ((ThreadPoolExecutor)this.netTimeoutExecutor).shutdownNow();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   long getLoginTimeout() {
/*     */     try {
/* 258 */       return (this.dataSource != null) ? this.dataSource.getLoginTimeout() : TimeUnit.SECONDS.toSeconds(5L);
/* 259 */     } catch (SQLException e) {
/* 260 */       return TimeUnit.SECONDS.toSeconds(5L);
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
/*     */   void handleMBeans(HikariPool hikariPool, boolean register) {
/* 275 */     if (!this.config.isRegisterMbeans()) {
/*     */       return;
/*     */     }
/*     */     try {
/*     */       ObjectName beanConfigName, beanPoolName;
/* 280 */       MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
/*     */ 
/*     */       
/* 283 */       if ("true".equals(System.getProperty("hikaricp.jmx.register2.0"))) {
/* 284 */         beanConfigName = new ObjectName("com.zaxxer.hikari:type=PoolConfig,name=" + this.poolName);
/* 285 */         beanPoolName = new ObjectName("com.zaxxer.hikari:type=Pool,name=" + this.poolName);
/*     */       } else {
/* 287 */         beanConfigName = new ObjectName("com.zaxxer.hikari:type=PoolConfig (" + this.poolName + ")");
/* 288 */         beanPoolName = new ObjectName("com.zaxxer.hikari:type=Pool (" + this.poolName + ")");
/*     */       } 
/* 290 */       if (register) {
/* 291 */         if (!mBeanServer.isRegistered(beanConfigName)) {
/* 292 */           mBeanServer.registerMBean(this.config, beanConfigName);
/* 293 */           mBeanServer.registerMBean(hikariPool, beanPoolName);
/*     */         } else {
/* 295 */           this.logger.error("{} - JMX name ({}) is already registered.", this.poolName, this.poolName);
/*     */         }
/*     */       
/* 298 */       } else if (mBeanServer.isRegistered(beanConfigName)) {
/* 299 */         mBeanServer.unregisterMBean(beanConfigName);
/* 300 */         mBeanServer.unregisterMBean(beanPoolName);
/*     */       }
/*     */     
/* 303 */     } catch (Exception e) {
/* 304 */       this.logger.warn("{} - Failed to {} management beans.", new Object[] { this.poolName, register ? "register" : "unregister", e });
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
/*     */   private void initializeDataSource() {
/* 317 */     String jdbcUrl = this.config.getJdbcUrl();
/* 318 */     String username = this.config.getUsername();
/* 319 */     String password = this.config.getPassword();
/* 320 */     String dsClassName = this.config.getDataSourceClassName();
/* 321 */     String driverClassName = this.config.getDriverClassName();
/* 322 */     String dataSourceJNDI = this.config.getDataSourceJNDI();
/* 323 */     Properties dataSourceProperties = this.config.getDataSourceProperties();
/*     */     
/* 325 */     DataSource dataSource = this.config.getDataSource();
/* 326 */     if (dsClassName != null && dataSource == null) {
/* 327 */       dataSource = (DataSource)UtilityElf.createInstance(dsClassName, DataSource.class, new Object[0]);
/* 328 */       PropertyElf.setTargetFromProperties(dataSource, dataSourceProperties);
/*     */     } else {
/* 330 */       DriverDataSource driverDataSource; if (jdbcUrl != null && dataSource == null) {
/* 331 */         driverDataSource = new DriverDataSource(jdbcUrl, driverClassName, dataSourceProperties, username, password);
/*     */       }
/* 333 */       else if (dataSourceJNDI != null && driverDataSource == null) {
/*     */         try {
/* 335 */           InitialContext ic = new InitialContext();
/* 336 */           dataSource = (DataSource)ic.lookup(dataSourceJNDI);
/* 337 */         } catch (NamingException e) {
/* 338 */           throw new HikariPool.PoolInitializationException(e);
/*     */         } 
/*     */       } 
/*     */     } 
/* 342 */     if (dataSource != null) {
/* 343 */       setLoginTimeout(dataSource);
/* 344 */       createNetworkTimeoutExecutor(dataSource, dsClassName, jdbcUrl);
/*     */     } 
/*     */     
/* 347 */     this.dataSource = dataSource;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Connection newConnection() throws Exception {
/* 357 */     long start = ClockSource.currentTime();
/*     */     
/* 359 */     Connection connection = null;
/*     */     try {
/* 361 */       String username = this.config.getUsername();
/* 362 */       String password = this.config.getPassword();
/*     */       
/* 364 */       connection = (username == null) ? this.dataSource.getConnection() : this.dataSource.getConnection(username, password);
/* 365 */       if (connection == null) {
/* 366 */         throw new SQLTransientConnectionException("DataSource returned null unexpectedly");
/*     */       }
/*     */       
/* 369 */       setupConnection(connection);
/* 370 */       this.lastConnectionFailure.set(null);
/* 371 */       return connection;
/*     */     }
/* 373 */     catch (Exception e) {
/* 374 */       if (connection != null) {
/* 375 */         quietlyCloseConnection(connection, "(Failed to create/setup connection)");
/*     */       }
/* 377 */       else if (getLastConnectionFailure() == null) {
/* 378 */         this.logger.debug("{} - Failed to create/setup connection: {}", this.poolName, e.getMessage());
/*     */       } 
/*     */       
/* 381 */       this.lastConnectionFailure.set(e);
/* 382 */       throw e;
/*     */     }
/*     */     finally {
/*     */       
/* 386 */       if (this.metricsTracker != null) {
/* 387 */         this.metricsTracker.recordConnectionCreated(ClockSource.elapsedMillis(start));
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
/*     */   private void setupConnection(Connection connection) throws ConnectionSetupException {
/*     */     try {
/* 401 */       if (this.networkTimeout == -1) {
/* 402 */         this.networkTimeout = getAndSetNetworkTimeout(connection, this.validationTimeout);
/*     */       } else {
/*     */         
/* 405 */         setNetworkTimeout(connection, this.validationTimeout);
/*     */       } 
/*     */       
/* 408 */       if (connection.isReadOnly() != this.isReadOnly) {
/* 409 */         connection.setReadOnly(this.isReadOnly);
/*     */       }
/*     */       
/* 412 */       if (connection.getAutoCommit() != this.isAutoCommit) {
/* 413 */         connection.setAutoCommit(this.isAutoCommit);
/*     */       }
/*     */       
/* 416 */       checkDriverSupport(connection);
/*     */       
/* 418 */       if (this.transactionIsolation != this.defaultTransactionIsolation) {
/* 419 */         connection.setTransactionIsolation(this.transactionIsolation);
/*     */       }
/*     */       
/* 422 */       if (this.catalog != null) {
/* 423 */         connection.setCatalog(this.catalog);
/*     */       }
/*     */       
/* 426 */       if (this.schema != null) {
/* 427 */         connection.setSchema(this.schema);
/*     */       }
/*     */       
/* 430 */       executeSql(connection, this.config.getConnectionInitSql(), true);
/*     */       
/* 432 */       setNetworkTimeout(connection, this.networkTimeout);
/*     */     }
/* 434 */     catch (SQLException e) {
/* 435 */       throw new ConnectionSetupException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void checkDriverSupport(Connection connection) throws SQLException {
/* 446 */     if (!this.isValidChecked) {
/* 447 */       checkValidationSupport(connection);
/* 448 */       checkDefaultIsolation(connection);
/*     */       
/* 450 */       this.isValidChecked = true;
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
/*     */   private void checkValidationSupport(Connection connection) throws SQLException {
/*     */     try {
/* 463 */       if (this.isUseJdbc4Validation) {
/* 464 */         connection.isValid(1);
/*     */       } else {
/*     */         
/* 467 */         executeSql(connection, this.config.getConnectionTestQuery(), false);
/*     */       }
/*     */     
/* 470 */     } catch (Exception|AbstractMethodError e) {
/* 471 */       this.logger.error("{} - Failed to execute{} connection test query ({}).", new Object[] { this.poolName, this.isUseJdbc4Validation ? " isValid() for connection, configure" : "", e.getMessage() });
/* 472 */       throw e;
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
/*     */   private void checkDefaultIsolation(Connection connection) throws SQLException {
/*     */     try {
/* 485 */       this.defaultTransactionIsolation = connection.getTransactionIsolation();
/* 486 */       if (this.transactionIsolation == -1) {
/* 487 */         this.transactionIsolation = this.defaultTransactionIsolation;
/*     */       }
/*     */     }
/* 490 */     catch (SQLException e) {
/* 491 */       this.logger.warn("{} - Default transaction isolation level detection failed ({}).", this.poolName, e.getMessage());
/* 492 */       if (e.getSQLState() != null && !e.getSQLState().startsWith("08")) {
/* 493 */         throw e;
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
/*     */   private void setQueryTimeout(Statement statement, int timeoutSec) {
/* 506 */     if (this.isQueryTimeoutSupported != 0) {
/*     */       try {
/* 508 */         statement.setQueryTimeout(timeoutSec);
/* 509 */         this.isQueryTimeoutSupported = 1;
/*     */       }
/* 511 */       catch (Exception e) {
/* 512 */         if (this.isQueryTimeoutSupported == -1) {
/* 513 */           this.isQueryTimeoutSupported = 0;
/* 514 */           this.logger.info("{} - Failed to set query timeout for statement. ({})", this.poolName, e.getMessage());
/*     */         } 
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
/*     */   private int getAndSetNetworkTimeout(Connection connection, long timeoutMs) {
/* 530 */     if (this.isNetworkTimeoutSupported != 0) {
/*     */       try {
/* 532 */         int originalTimeout = connection.getNetworkTimeout();
/* 533 */         connection.setNetworkTimeout(this.netTimeoutExecutor, (int)timeoutMs);
/* 534 */         this.isNetworkTimeoutSupported = 1;
/* 535 */         return originalTimeout;
/*     */       }
/* 537 */       catch (Exception|AbstractMethodError e) {
/* 538 */         if (this.isNetworkTimeoutSupported == -1) {
/* 539 */           this.isNetworkTimeoutSupported = 0;
/*     */           
/* 541 */           this.logger.info("{} - Driver does not support get/set network timeout for connections. ({})", this.poolName, e.getMessage());
/* 542 */           if (this.validationTimeout < TimeUnit.SECONDS.toMillis(1L)) {
/* 543 */             this.logger.warn("{} - A validationTimeout of less than 1 second cannot be honored on drivers without setNetworkTimeout() support.", this.poolName);
/*     */           }
/* 545 */           else if (this.validationTimeout % TimeUnit.SECONDS.toMillis(1L) != 0L) {
/* 546 */             this.logger.warn("{} - A validationTimeout with fractional second granularity cannot be honored on drivers without setNetworkTimeout() support.", this.poolName);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/* 552 */     return 0;
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
/*     */   private void setNetworkTimeout(Connection connection, long timeoutMs) throws SQLException {
/* 565 */     if (this.isNetworkTimeoutSupported == 1) {
/* 566 */       connection.setNetworkTimeout(this.netTimeoutExecutor, (int)timeoutMs);
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
/*     */   private void executeSql(Connection connection, String sql, boolean isCommit) throws SQLException {
/* 580 */     if (sql != null) {
/* 581 */       Statement statement = connection.createStatement();
/*     */       
/* 583 */       try { statement.execute(sql);
/* 584 */         if (statement != null) statement.close();  } catch (Throwable throwable) { if (statement != null)
/*     */           try { statement.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }
/* 586 */        if (this.isIsolateInternalQueries && !this.isAutoCommit) {
/* 587 */         if (isCommit) {
/* 588 */           connection.commit();
/*     */         } else {
/*     */           
/* 591 */           connection.rollback();
/*     */         } 
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void createNetworkTimeoutExecutor(DataSource dataSource, String dsClassName, String jdbcUrl) {
/* 600 */     if ((dsClassName != null && dsClassName.contains("Mysql")) || (jdbcUrl != null && jdbcUrl
/* 601 */       .contains("mysql")) || (dataSource != null && dataSource
/* 602 */       .getClass().getName().contains("Mysql"))) {
/* 603 */       this.netTimeoutExecutor = new SynchronousExecutor();
/*     */     } else {
/*     */       
/* 606 */       ThreadFactory threadFactory = this.config.getThreadFactory();
/* 607 */       threadFactory = (threadFactory != null) ? threadFactory : (ThreadFactory)new UtilityElf.DefaultThreadFactory(this.poolName + " network timeout executor", true);
/* 608 */       ThreadPoolExecutor executor = (ThreadPoolExecutor)Executors.newCachedThreadPool(threadFactory);
/* 609 */       executor.setKeepAliveTime(15L, TimeUnit.SECONDS);
/* 610 */       executor.allowCoreThreadTimeOut(true);
/* 611 */       this.netTimeoutExecutor = executor;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setLoginTimeout(DataSource dataSource) {
/* 622 */     if (this.connectionTimeout != 2147483647L) {
/*     */       try {
/* 624 */         dataSource.setLoginTimeout(Math.max(1, (int)TimeUnit.MILLISECONDS.toSeconds(500L + this.connectionTimeout)));
/*     */       }
/* 626 */       catch (Exception e) {
/* 627 */         this.logger.info("{} - Failed to set login timeout for data source. ({})", this.poolName, e.getMessage());
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
/*     */   
/*     */   private String stringFromResetBits(int bits) {
/* 644 */     StringBuilder sb = new StringBuilder();
/* 645 */     for (int ndx = 0; ndx < RESET_STATES.length; ndx++) {
/* 646 */       if ((bits & 1 << ndx) != 0) {
/* 647 */         sb.append(RESET_STATES[ndx]).append(", ");
/*     */       }
/*     */     } 
/*     */     
/* 651 */     sb.setLength(sb.length() - 2);
/* 652 */     return sb.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static class ConnectionSetupException
/*     */     extends Exception
/*     */   {
/*     */     private static final long serialVersionUID = 929872118275916521L;
/*     */ 
/*     */ 
/*     */     
/*     */     ConnectionSetupException(Throwable t) {
/* 665 */       super(t);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class SynchronousExecutor
/*     */     implements Executor
/*     */   {
/*     */     private SynchronousExecutor() {}
/*     */ 
/*     */ 
/*     */     
/*     */     public void execute(Runnable command) {
/*     */       try {
/* 680 */         command.run();
/*     */       }
/* 682 */       catch (Exception t) {
/* 683 */         LoggerFactory.getLogger(PoolBase.class).debug("Failed to execute: {}", command, t);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static interface IMetricsTrackerDelegate
/*     */     extends AutoCloseable
/*     */   {
/*     */     default void recordConnectionUsage(PoolEntry poolEntry) {}
/*     */ 
/*     */     
/*     */     default void recordConnectionCreated(long connectionCreatedMillis) {}
/*     */ 
/*     */     
/*     */     default void recordBorrowTimeoutStats(long startTime) {}
/*     */ 
/*     */     
/*     */     default void recordBorrowStats(PoolEntry poolEntry, long startTime) {}
/*     */ 
/*     */     
/*     */     default void recordConnectionTimeout() {}
/*     */     
/*     */     default void close() {}
/*     */   }
/*     */   
/*     */   static class MetricsTrackerDelegate
/*     */     implements IMetricsTrackerDelegate
/*     */   {
/*     */     final IMetricsTracker tracker;
/*     */     
/*     */     MetricsTrackerDelegate(IMetricsTracker tracker) {
/* 715 */       this.tracker = tracker;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void recordConnectionUsage(PoolEntry poolEntry) {
/* 721 */       this.tracker.recordConnectionUsageMillis(poolEntry.getMillisSinceBorrowed());
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void recordConnectionCreated(long connectionCreatedMillis) {
/* 727 */       this.tracker.recordConnectionCreatedMillis(connectionCreatedMillis);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void recordBorrowTimeoutStats(long startTime) {
/* 733 */       this.tracker.recordConnectionAcquiredNanos(ClockSource.elapsedNanos(startTime));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void recordBorrowStats(PoolEntry poolEntry, long startTime) {
/* 739 */       long now = ClockSource.currentTime();
/* 740 */       poolEntry.lastBorrowed = now;
/* 741 */       this.tracker.recordConnectionAcquiredNanos(ClockSource.elapsedNanos(startTime, now));
/*     */     }
/*     */ 
/*     */     
/*     */     public void recordConnectionTimeout() {
/* 746 */       this.tracker.recordConnectionTimeout();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void close() {
/* 752 */       this.tracker.close();
/*     */     }
/*     */   }
/*     */   
/*     */   static final class NopMetricsTrackerDelegate implements IMetricsTrackerDelegate {}
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\zaxxer\hikari\pool\PoolBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */