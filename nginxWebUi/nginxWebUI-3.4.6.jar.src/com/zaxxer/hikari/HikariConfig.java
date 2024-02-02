/*      */ package com.zaxxer.hikari;
/*      */ 
/*      */ import com.zaxxer.hikari.metrics.MetricsTrackerFactory;
/*      */ import com.zaxxer.hikari.util.PropertyElf;
/*      */ import com.zaxxer.hikari.util.UtilityElf;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.lang.reflect.Field;
/*      */ import java.lang.reflect.Modifier;
/*      */ import java.security.AccessControlException;
/*      */ import java.util.Properties;
/*      */ import java.util.Set;
/*      */ import java.util.TreeSet;
/*      */ import java.util.concurrent.ScheduledExecutorService;
/*      */ import java.util.concurrent.ThreadFactory;
/*      */ import java.util.concurrent.ThreadLocalRandom;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import javax.naming.InitialContext;
/*      */ import javax.naming.NamingException;
/*      */ import javax.sql.DataSource;
/*      */ import org.slf4j.Logger;
/*      */ import org.slf4j.LoggerFactory;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class HikariConfig
/*      */   implements HikariConfigMXBean
/*      */ {
/*   51 */   private static final Logger LOGGER = LoggerFactory.getLogger(HikariConfig.class);
/*      */   
/*   53 */   private static final char[] ID_CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
/*   54 */   private static final long CONNECTION_TIMEOUT = TimeUnit.SECONDS.toMillis(30L);
/*   55 */   private static final long VALIDATION_TIMEOUT = TimeUnit.SECONDS.toMillis(5L);
/*   56 */   private static final long SOFT_TIMEOUT_FLOOR = Long.getLong("com.zaxxer.hikari.timeoutMs.floor", 250L).longValue();
/*   57 */   private static final long IDLE_TIMEOUT = TimeUnit.MINUTES.toMillis(10L);
/*   58 */   private static final long MAX_LIFETIME = TimeUnit.MINUTES.toMillis(30L);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static boolean unitTest = false;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  113 */   private Properties dataSourceProperties = new Properties();
/*  114 */   private Properties healthCheckProperties = new Properties();
/*      */   
/*  116 */   private volatile int minIdle = -1;
/*  117 */   private volatile int maxPoolSize = -1;
/*  118 */   private volatile long maxLifetime = MAX_LIFETIME;
/*  119 */   private volatile long connectionTimeout = CONNECTION_TIMEOUT;
/*  120 */   private volatile long validationTimeout = VALIDATION_TIMEOUT;
/*  121 */   private volatile long idleTimeout = IDLE_TIMEOUT;
/*  122 */   private long initializationFailTimeout = 1L; private boolean isAutoCommit = true; private static final long DEFAULT_KEEPALIVE_TIME = 0L; private static final int DEFAULT_POOL_SIZE = 10; private volatile String catalog; private volatile long leakDetectionThreshold; private volatile String username; private volatile String password;
/*      */   private String connectionInitSql;
/*  124 */   private long keepaliveTime = 0L; private String connectionTestQuery; private String dataSourceClassName; private String dataSourceJndiName; private String driverClassName; private String exceptionOverrideClassName; private String jdbcUrl;
/*      */   public HikariConfig() {
/*  126 */     String systemProp = System.getProperty("hikaricp.configurationFile");
/*  127 */     if (systemProp != null)
/*  128 */       loadProperties(systemProp); 
/*      */   }
/*      */   private String poolName; private String schema; private String transactionIsolationName; private boolean isReadOnly; private boolean isIsolateInternalQueries; private boolean isRegisterMbeans; private boolean isAllowPoolSuspension; private DataSource dataSource;
/*      */   private ThreadFactory threadFactory;
/*      */   private ScheduledExecutorService scheduledExecutor;
/*      */   private MetricsTrackerFactory metricsTrackerFactory;
/*      */   private Object metricRegistry;
/*      */   private Object healthCheckRegistry;
/*      */   private volatile boolean sealed;
/*      */   
/*      */   public HikariConfig(Properties properties) {
/*  139 */     this();
/*  140 */     PropertyElf.setTargetFromProperties(this, properties);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HikariConfig(String propertyFileName) {
/*  152 */     this();
/*      */     
/*  154 */     loadProperties(propertyFileName);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getCatalog() {
/*  165 */     return this.catalog;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCatalog(String catalog) {
/*  172 */     this.catalog = catalog;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getConnectionTimeout() {
/*  180 */     return this.connectionTimeout;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setConnectionTimeout(long connectionTimeoutMs) {
/*  187 */     if (connectionTimeoutMs == 0L) {
/*  188 */       this.connectionTimeout = 2147483647L;
/*      */     } else {
/*  190 */       if (connectionTimeoutMs < SOFT_TIMEOUT_FLOOR) {
/*  191 */         throw new IllegalArgumentException("connectionTimeout cannot be less than " + SOFT_TIMEOUT_FLOOR + "ms");
/*      */       }
/*      */       
/*  194 */       this.connectionTimeout = connectionTimeoutMs;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getIdleTimeout() {
/*  202 */     return this.idleTimeout;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setIdleTimeout(long idleTimeoutMs) {
/*  209 */     if (idleTimeoutMs < 0L) {
/*  210 */       throw new IllegalArgumentException("idleTimeout cannot be negative");
/*      */     }
/*  212 */     this.idleTimeout = idleTimeoutMs;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getLeakDetectionThreshold() {
/*  219 */     return this.leakDetectionThreshold;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setLeakDetectionThreshold(long leakDetectionThresholdMs) {
/*  226 */     this.leakDetectionThreshold = leakDetectionThresholdMs;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getMaxLifetime() {
/*  233 */     return this.maxLifetime;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setMaxLifetime(long maxLifetimeMs) {
/*  240 */     this.maxLifetime = maxLifetimeMs;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaximumPoolSize() {
/*  247 */     return this.maxPoolSize;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setMaximumPoolSize(int maxPoolSize) {
/*  254 */     if (maxPoolSize < 1) {
/*  255 */       throw new IllegalArgumentException("maxPoolSize cannot be less than 1");
/*      */     }
/*  257 */     this.maxPoolSize = maxPoolSize;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMinimumIdle() {
/*  264 */     return this.minIdle;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setMinimumIdle(int minIdle) {
/*  271 */     if (minIdle < 0) {
/*  272 */       throw new IllegalArgumentException("minimumIdle cannot be negative");
/*      */     }
/*  274 */     this.minIdle = minIdle;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getPassword() {
/*  283 */     return this.password;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setPassword(String password) {
/*  293 */     this.password = password;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getUsername() {
/*  303 */     return this.username;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setUsername(String username) {
/*  314 */     this.username = username;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getValidationTimeout() {
/*  321 */     return this.validationTimeout;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setValidationTimeout(long validationTimeoutMs) {
/*  328 */     if (validationTimeoutMs < SOFT_TIMEOUT_FLOOR) {
/*  329 */       throw new IllegalArgumentException("validationTimeout cannot be less than " + SOFT_TIMEOUT_FLOOR + "ms");
/*      */     }
/*      */     
/*  332 */     this.validationTimeout = validationTimeoutMs;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getConnectionTestQuery() {
/*  346 */     return this.connectionTestQuery;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setConnectionTestQuery(String connectionTestQuery) {
/*  358 */     checkIfSealed();
/*  359 */     this.connectionTestQuery = connectionTestQuery;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getConnectionInitSql() {
/*  370 */     return this.connectionInitSql;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setConnectionInitSql(String connectionInitSql) {
/*  382 */     checkIfSealed();
/*  383 */     this.connectionInitSql = connectionInitSql;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DataSource getDataSource() {
/*  394 */     return this.dataSource;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDataSource(DataSource dataSource) {
/*  405 */     checkIfSealed();
/*  406 */     this.dataSource = dataSource;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getDataSourceClassName() {
/*  416 */     return this.dataSourceClassName;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDataSourceClassName(String className) {
/*  426 */     checkIfSealed();
/*  427 */     this.dataSourceClassName = className;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addDataSourceProperty(String propertyName, Object value) {
/*  445 */     checkIfSealed();
/*  446 */     this.dataSourceProperties.put(propertyName, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public String getDataSourceJNDI() {
/*  451 */     return this.dataSourceJndiName;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setDataSourceJNDI(String jndiDataSource) {
/*  456 */     checkIfSealed();
/*  457 */     this.dataSourceJndiName = jndiDataSource;
/*      */   }
/*      */ 
/*      */   
/*      */   public Properties getDataSourceProperties() {
/*  462 */     return this.dataSourceProperties;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setDataSourceProperties(Properties dsProperties) {
/*  467 */     checkIfSealed();
/*  468 */     this.dataSourceProperties.putAll(dsProperties);
/*      */   }
/*      */ 
/*      */   
/*      */   public String getDriverClassName() {
/*  473 */     return this.driverClassName;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setDriverClassName(String driverClassName) {
/*  478 */     checkIfSealed();
/*      */     
/*  480 */     Class<?> driverClass = attemptFromContextLoader(driverClassName);
/*      */     try {
/*  482 */       if (driverClass == null) {
/*  483 */         driverClass = getClass().getClassLoader().loadClass(driverClassName);
/*  484 */         LOGGER.debug("Driver class {} found in the HikariConfig class classloader {}", driverClassName, getClass().getClassLoader());
/*      */       } 
/*  486 */     } catch (ClassNotFoundException e) {
/*  487 */       LOGGER.error("Failed to load driver class {} from HikariConfig class classloader {}", driverClassName, getClass().getClassLoader());
/*      */     } 
/*      */     
/*  490 */     if (driverClass == null) {
/*  491 */       throw new RuntimeException("Failed to load driver class " + driverClassName + " in either of HikariConfig class loader or Thread context classloader");
/*      */     }
/*      */     
/*      */     try {
/*  495 */       driverClass.getConstructor(new Class[0]).newInstance(new Object[0]);
/*  496 */       this.driverClassName = driverClassName;
/*      */     }
/*  498 */     catch (Exception e) {
/*  499 */       throw new RuntimeException("Failed to instantiate class " + driverClassName, e);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public String getJdbcUrl() {
/*  505 */     return this.jdbcUrl;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setJdbcUrl(String jdbcUrl) {
/*  510 */     checkIfSealed();
/*  511 */     this.jdbcUrl = jdbcUrl;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isAutoCommit() {
/*  521 */     return this.isAutoCommit;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAutoCommit(boolean isAutoCommit) {
/*  531 */     checkIfSealed();
/*  532 */     this.isAutoCommit = isAutoCommit;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isAllowPoolSuspension() {
/*  542 */     return this.isAllowPoolSuspension;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAllowPoolSuspension(boolean isAllowPoolSuspension) {
/*  554 */     checkIfSealed();
/*  555 */     this.isAllowPoolSuspension = isAllowPoolSuspension;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getInitializationFailTimeout() {
/*  567 */     return this.initializationFailTimeout;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setInitializationFailTimeout(long initializationFailTimeout) {
/*  605 */     checkIfSealed();
/*  606 */     this.initializationFailTimeout = initializationFailTimeout;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isIsolateInternalQueries() {
/*  617 */     return this.isIsolateInternalQueries;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setIsolateInternalQueries(boolean isolate) {
/*  628 */     checkIfSealed();
/*  629 */     this.isIsolateInternalQueries = isolate;
/*      */   }
/*      */ 
/*      */   
/*      */   public MetricsTrackerFactory getMetricsTrackerFactory() {
/*  634 */     return this.metricsTrackerFactory;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setMetricsTrackerFactory(MetricsTrackerFactory metricsTrackerFactory) {
/*  639 */     if (this.metricRegistry != null) {
/*  640 */       throw new IllegalStateException("cannot use setMetricsTrackerFactory() and setMetricRegistry() together");
/*      */     }
/*      */     
/*  643 */     this.metricsTrackerFactory = metricsTrackerFactory;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object getMetricRegistry() {
/*  653 */     return this.metricRegistry;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setMetricRegistry(Object metricRegistry) {
/*  663 */     if (this.metricsTrackerFactory != null) {
/*  664 */       throw new IllegalStateException("cannot use setMetricRegistry() and setMetricsTrackerFactory() together");
/*      */     }
/*      */     
/*  667 */     if (metricRegistry != null) {
/*  668 */       metricRegistry = getObjectOrPerformJndiLookup(metricRegistry);
/*      */       
/*  670 */       if (!UtilityElf.safeIsAssignableFrom(metricRegistry, "com.codahale.metrics.MetricRegistry") && 
/*  671 */         !UtilityElf.safeIsAssignableFrom(metricRegistry, "io.micrometer.core.instrument.MeterRegistry")) {
/*  672 */         throw new IllegalArgumentException("Class must be instance of com.codahale.metrics.MetricRegistry or io.micrometer.core.instrument.MeterRegistry");
/*      */       }
/*      */     } 
/*      */     
/*  676 */     this.metricRegistry = metricRegistry;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object getHealthCheckRegistry() {
/*  687 */     return this.healthCheckRegistry;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setHealthCheckRegistry(Object healthCheckRegistry) {
/*  698 */     checkIfSealed();
/*      */     
/*  700 */     if (healthCheckRegistry != null) {
/*  701 */       healthCheckRegistry = getObjectOrPerformJndiLookup(healthCheckRegistry);
/*      */       
/*  703 */       if (!(healthCheckRegistry instanceof com.codahale.metrics.health.HealthCheckRegistry)) {
/*  704 */         throw new IllegalArgumentException("Class must be an instance of com.codahale.metrics.health.HealthCheckRegistry");
/*      */       }
/*      */     } 
/*      */     
/*  708 */     this.healthCheckRegistry = healthCheckRegistry;
/*      */   }
/*      */ 
/*      */   
/*      */   public Properties getHealthCheckProperties() {
/*  713 */     return this.healthCheckProperties;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setHealthCheckProperties(Properties healthCheckProperties) {
/*  718 */     checkIfSealed();
/*  719 */     this.healthCheckProperties.putAll(healthCheckProperties);
/*      */   }
/*      */ 
/*      */   
/*      */   public void addHealthCheckProperty(String key, String value) {
/*  724 */     checkIfSealed();
/*  725 */     this.healthCheckProperties.setProperty(key, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getKeepaliveTime() {
/*  735 */     return this.keepaliveTime;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setKeepaliveTime(long keepaliveTimeMs) {
/*  745 */     this.keepaliveTime = keepaliveTimeMs;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isReadOnly() {
/*  755 */     return this.isReadOnly;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setReadOnly(boolean readOnly) {
/*  765 */     checkIfSealed();
/*  766 */     this.isReadOnly = readOnly;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isRegisterMbeans() {
/*  777 */     return this.isRegisterMbeans;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setRegisterMbeans(boolean register) {
/*  787 */     checkIfSealed();
/*  788 */     this.isRegisterMbeans = register;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getPoolName() {
/*  795 */     return this.poolName;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setPoolName(String poolName) {
/*  806 */     checkIfSealed();
/*  807 */     this.poolName = poolName;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ScheduledExecutorService getScheduledExecutor() {
/*  817 */     return this.scheduledExecutor;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setScheduledExecutor(ScheduledExecutorService executor) {
/*  827 */     checkIfSealed();
/*  828 */     this.scheduledExecutor = executor;
/*      */   }
/*      */ 
/*      */   
/*      */   public String getTransactionIsolation() {
/*  833 */     return this.transactionIsolationName;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getSchema() {
/*  843 */     return this.schema;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setSchema(String schema) {
/*  853 */     checkIfSealed();
/*  854 */     this.schema = schema;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getExceptionOverrideClassName() {
/*  865 */     return this.exceptionOverrideClassName;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setExceptionOverrideClassName(String exceptionOverrideClassName) {
/*  876 */     checkIfSealed();
/*      */     
/*  878 */     Class<?> overrideClass = attemptFromContextLoader(exceptionOverrideClassName);
/*      */     try {
/*  880 */       if (overrideClass == null) {
/*  881 */         overrideClass = getClass().getClassLoader().loadClass(exceptionOverrideClassName);
/*  882 */         LOGGER.debug("SQLExceptionOverride class {} found in the HikariConfig class classloader {}", exceptionOverrideClassName, getClass().getClassLoader());
/*      */       } 
/*  884 */     } catch (ClassNotFoundException e) {
/*  885 */       LOGGER.error("Failed to load SQLExceptionOverride class {} from HikariConfig class classloader {}", exceptionOverrideClassName, getClass().getClassLoader());
/*      */     } 
/*      */     
/*  888 */     if (overrideClass == null) {
/*  889 */       throw new RuntimeException("Failed to load SQLExceptionOverride class " + exceptionOverrideClassName + " in either of HikariConfig class loader or Thread context classloader");
/*      */     }
/*      */     
/*      */     try {
/*  893 */       overrideClass.getConstructor(new Class[0]).newInstance(new Object[0]);
/*  894 */       this.exceptionOverrideClassName = exceptionOverrideClassName;
/*      */     }
/*  896 */     catch (Exception e) {
/*  897 */       throw new RuntimeException("Failed to instantiate class " + exceptionOverrideClassName, e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setTransactionIsolation(String isolationLevel) {
/*  910 */     checkIfSealed();
/*  911 */     this.transactionIsolationName = isolationLevel;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ThreadFactory getThreadFactory() {
/*  921 */     return this.threadFactory;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setThreadFactory(ThreadFactory threadFactory) {
/*  931 */     checkIfSealed();
/*  932 */     this.threadFactory = threadFactory;
/*      */   }
/*      */ 
/*      */   
/*      */   void seal() {
/*  937 */     this.sealed = true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void copyStateTo(HikariConfig other) {
/*  947 */     for (Field field : HikariConfig.class.getDeclaredFields()) {
/*  948 */       if (!Modifier.isFinal(field.getModifiers())) {
/*  949 */         field.setAccessible(true);
/*      */         try {
/*  951 */           field.set(other, field.get(this));
/*      */         }
/*  953 */         catch (Exception e) {
/*  954 */           throw new RuntimeException("Failed to copy HikariConfig state: " + e.getMessage(), e);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/*  959 */     other.sealed = false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Class<?> attemptFromContextLoader(String driverClassName) {
/*  967 */     ClassLoader threadContextClassLoader = Thread.currentThread().getContextClassLoader();
/*  968 */     if (threadContextClassLoader != null) {
/*      */       try {
/*  970 */         Class<?> driverClass = threadContextClassLoader.loadClass(driverClassName);
/*  971 */         LOGGER.debug("Driver class {} found in Thread context class loader {}", driverClassName, threadContextClassLoader);
/*  972 */         return driverClass;
/*  973 */       } catch (ClassNotFoundException e) {
/*  974 */         LOGGER.debug("Driver class {} not found in Thread context class loader {}, trying classloader {}", new Object[] { driverClassName, threadContextClassLoader, 
/*  975 */               getClass().getClassLoader() });
/*      */       } 
/*      */     }
/*      */     
/*  979 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void validate() {
/*  985 */     if (this.poolName == null) {
/*  986 */       this.poolName = generatePoolName();
/*      */     }
/*  988 */     else if (this.isRegisterMbeans && this.poolName.contains(":")) {
/*  989 */       throw new IllegalArgumentException("poolName cannot contain ':' when used with JMX");
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  994 */     this.catalog = UtilityElf.getNullIfEmpty(this.catalog);
/*  995 */     this.connectionInitSql = UtilityElf.getNullIfEmpty(this.connectionInitSql);
/*  996 */     this.connectionTestQuery = UtilityElf.getNullIfEmpty(this.connectionTestQuery);
/*  997 */     this.transactionIsolationName = UtilityElf.getNullIfEmpty(this.transactionIsolationName);
/*  998 */     this.dataSourceClassName = UtilityElf.getNullIfEmpty(this.dataSourceClassName);
/*  999 */     this.dataSourceJndiName = UtilityElf.getNullIfEmpty(this.dataSourceJndiName);
/* 1000 */     this.driverClassName = UtilityElf.getNullIfEmpty(this.driverClassName);
/* 1001 */     this.jdbcUrl = UtilityElf.getNullIfEmpty(this.jdbcUrl);
/*      */ 
/*      */     
/* 1004 */     if (this.dataSource != null) {
/* 1005 */       if (this.dataSourceClassName != null) {
/* 1006 */         LOGGER.warn("{} - using dataSource and ignoring dataSourceClassName.", this.poolName);
/*      */       }
/*      */     }
/* 1009 */     else if (this.dataSourceClassName != null) {
/* 1010 */       if (this.driverClassName != null) {
/* 1011 */         LOGGER.error("{} - cannot use driverClassName and dataSourceClassName together.", this.poolName);
/*      */ 
/*      */         
/* 1014 */         throw new IllegalStateException("cannot use driverClassName and dataSourceClassName together.");
/*      */       } 
/* 1016 */       if (this.jdbcUrl != null) {
/* 1017 */         LOGGER.warn("{} - using dataSourceClassName and ignoring jdbcUrl.", this.poolName);
/*      */       }
/*      */     }
/* 1020 */     else if (this.jdbcUrl == null && this.dataSourceJndiName == null) {
/*      */ 
/*      */       
/* 1023 */       if (this.driverClassName != null) {
/* 1024 */         LOGGER.error("{} - jdbcUrl is required with driverClassName.", this.poolName);
/* 1025 */         throw new IllegalArgumentException("jdbcUrl is required with driverClassName.");
/*      */       } 
/*      */       
/* 1028 */       LOGGER.error("{} - dataSource or dataSourceClassName or jdbcUrl is required.", this.poolName);
/* 1029 */       throw new IllegalArgumentException("dataSource or dataSourceClassName or jdbcUrl is required.");
/*      */     } 
/*      */     
/* 1032 */     validateNumerics();
/*      */     
/* 1034 */     if (LOGGER.isDebugEnabled() || unitTest) {
/* 1035 */       logConfiguration();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private void validateNumerics() {
/* 1041 */     if (this.maxLifetime != 0L && this.maxLifetime < TimeUnit.SECONDS.toMillis(30L)) {
/* 1042 */       LOGGER.warn("{} - maxLifetime is less than 30000ms, setting to default {}ms.", this.poolName, Long.valueOf(MAX_LIFETIME));
/* 1043 */       this.maxLifetime = MAX_LIFETIME;
/*      */     } 
/*      */ 
/*      */     
/* 1047 */     if (this.keepaliveTime != 0L && this.keepaliveTime < TimeUnit.SECONDS.toMillis(30L)) {
/* 1048 */       LOGGER.warn("{} - keepaliveTime is less than 30000ms, disabling it.", this.poolName);
/* 1049 */       this.keepaliveTime = 0L;
/*      */     } 
/*      */ 
/*      */     
/* 1053 */     if (this.keepaliveTime != 0L && this.maxLifetime != 0L && this.keepaliveTime >= this.maxLifetime) {
/* 1054 */       LOGGER.warn("{} - keepaliveTime is greater than or equal to maxLifetime, disabling it.", this.poolName);
/* 1055 */       this.keepaliveTime = 0L;
/*      */     } 
/*      */     
/* 1058 */     if (this.leakDetectionThreshold > 0L && !unitTest && (
/* 1059 */       this.leakDetectionThreshold < TimeUnit.SECONDS.toMillis(2L) || (this.leakDetectionThreshold > this.maxLifetime && this.maxLifetime > 0L))) {
/* 1060 */       LOGGER.warn("{} - leakDetectionThreshold is less than 2000ms or more than maxLifetime, disabling it.", this.poolName);
/* 1061 */       this.leakDetectionThreshold = 0L;
/*      */     } 
/*      */ 
/*      */     
/* 1065 */     if (this.connectionTimeout < SOFT_TIMEOUT_FLOOR) {
/* 1066 */       LOGGER.warn("{} - connectionTimeout is less than {}ms, setting to {}ms.", new Object[] { this.poolName, Long.valueOf(SOFT_TIMEOUT_FLOOR), Long.valueOf(CONNECTION_TIMEOUT) });
/* 1067 */       this.connectionTimeout = CONNECTION_TIMEOUT;
/*      */     } 
/*      */     
/* 1070 */     if (this.validationTimeout < SOFT_TIMEOUT_FLOOR) {
/* 1071 */       LOGGER.warn("{} - validationTimeout is less than {}ms, setting to {}ms.", new Object[] { this.poolName, Long.valueOf(SOFT_TIMEOUT_FLOOR), Long.valueOf(VALIDATION_TIMEOUT) });
/* 1072 */       this.validationTimeout = VALIDATION_TIMEOUT;
/*      */     } 
/*      */     
/* 1075 */     if (this.maxPoolSize < 1) {
/* 1076 */       this.maxPoolSize = 10;
/*      */     }
/*      */     
/* 1079 */     if (this.minIdle < 0 || this.minIdle > this.maxPoolSize) {
/* 1080 */       this.minIdle = this.maxPoolSize;
/*      */     }
/*      */     
/* 1083 */     if (this.idleTimeout + TimeUnit.SECONDS.toMillis(1L) > this.maxLifetime && this.maxLifetime > 0L && this.minIdle < this.maxPoolSize) {
/* 1084 */       LOGGER.warn("{} - idleTimeout is close to or more than maxLifetime, disabling it.", this.poolName);
/* 1085 */       this.idleTimeout = 0L;
/*      */     }
/* 1087 */     else if (this.idleTimeout != 0L && this.idleTimeout < TimeUnit.SECONDS.toMillis(10L) && this.minIdle < this.maxPoolSize) {
/* 1088 */       LOGGER.warn("{} - idleTimeout is less than 10000ms, setting to default {}ms.", this.poolName, Long.valueOf(IDLE_TIMEOUT));
/* 1089 */       this.idleTimeout = IDLE_TIMEOUT;
/*      */     }
/* 1091 */     else if (this.idleTimeout != IDLE_TIMEOUT && this.idleTimeout != 0L && this.minIdle == this.maxPoolSize) {
/* 1092 */       LOGGER.warn("{} - idleTimeout has been set but has no effect because the pool is operating as a fixed size pool.", this.poolName);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void checkIfSealed() {
/* 1098 */     if (this.sealed) throw new IllegalStateException("The configuration of the pool is sealed once started. Use HikariConfigMXBean for runtime changes.");
/*      */   
/*      */   }
/*      */   
/*      */   private void logConfiguration() {
/* 1103 */     LOGGER.debug("{} - configuration:", this.poolName);
/* 1104 */     Set<String> propertyNames = new TreeSet<>(PropertyElf.getPropertyNames(HikariConfig.class));
/* 1105 */     for (String prop : propertyNames) {
/*      */       try {
/* 1107 */         Object value = PropertyElf.getProperty(prop, this);
/* 1108 */         if ("dataSourceProperties".equals(prop)) {
/* 1109 */           Properties dsProps = PropertyElf.copyProperties(this.dataSourceProperties);
/* 1110 */           dsProps.setProperty("password", "<masked>");
/* 1111 */           value = dsProps;
/*      */         } 
/*      */         
/* 1114 */         if ("initializationFailTimeout".equals(prop) && this.initializationFailTimeout == Long.MAX_VALUE) {
/* 1115 */           value = "infinite";
/*      */         }
/* 1117 */         else if ("transactionIsolation".equals(prop) && this.transactionIsolationName == null) {
/* 1118 */           value = "default";
/*      */         }
/* 1120 */         else if (prop.matches("scheduledExecutorService|threadFactory") && value == null) {
/* 1121 */           value = "internal";
/*      */         }
/* 1123 */         else if (prop.contains("jdbcUrl") && value instanceof String) {
/* 1124 */           value = ((String)value).replaceAll("([?&;]password=)[^&#;]*(.*)", "$1<masked>$2");
/*      */         }
/* 1126 */         else if (prop.contains("password")) {
/* 1127 */           value = "<masked>";
/*      */         }
/* 1129 */         else if (value instanceof String) {
/* 1130 */           value = "\"" + value + "\"";
/*      */         }
/* 1132 */         else if (value == null) {
/* 1133 */           value = "none";
/*      */         } 
/* 1135 */         LOGGER.debug("{}{}", prop + "................................................".substring(0, 32), value);
/*      */       }
/* 1137 */       catch (Exception exception) {}
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void loadProperties(String propertyFileName) {
/* 1145 */     File propFile = new File(propertyFileName); 
/* 1146 */     try { InputStream is = propFile.isFile() ? new FileInputStream(propFile) : getClass().getResourceAsStream(propertyFileName); 
/* 1147 */       try { if (is != null) {
/* 1148 */           Properties props = new Properties();
/* 1149 */           props.load(is);
/* 1150 */           PropertyElf.setTargetFromProperties(this, props);
/*      */         } else {
/*      */           
/* 1153 */           throw new IllegalArgumentException("Cannot find property file: " + propertyFileName);
/*      */         } 
/* 1155 */         if (is != null) is.close();  } catch (Throwable throwable) { if (is != null)
/* 1156 */           try { is.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  } catch (IOException io)
/* 1157 */     { throw new RuntimeException("Failed to read property file", io); }
/*      */   
/*      */   }
/*      */ 
/*      */   
/*      */   private String generatePoolName() {
/* 1163 */     String prefix = "HikariPool-";
/*      */     
/*      */     try {
/* 1166 */       synchronized (System.getProperties()) {
/* 1167 */         String next = String.valueOf(Integer.getInteger("com.zaxxer.hikari.pool_number", 0).intValue() + 1);
/* 1168 */         System.setProperty("com.zaxxer.hikari.pool_number", next);
/* 1169 */         return "HikariPool-" + next;
/*      */       } 
/* 1171 */     } catch (AccessControlException e) {
/*      */ 
/*      */       
/* 1174 */       ThreadLocalRandom random = ThreadLocalRandom.current();
/* 1175 */       StringBuilder buf = new StringBuilder("HikariPool-");
/*      */       
/* 1177 */       for (int i = 0; i < 4; i++) {
/* 1178 */         buf.append(ID_CHARACTERS[random.nextInt(62)]);
/*      */       }
/*      */       
/* 1181 */       LOGGER.info("assigned random pool name '{}' (security manager prevented access to system properties)", buf);
/*      */       
/* 1183 */       return buf.toString();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private Object getObjectOrPerformJndiLookup(Object object) {
/* 1189 */     if (object instanceof String) {
/*      */       try {
/* 1191 */         InitialContext initCtx = new InitialContext();
/* 1192 */         return initCtx.lookup((String)object);
/*      */       }
/* 1194 */       catch (NamingException e) {
/* 1195 */         throw new IllegalArgumentException(e);
/*      */       } 
/*      */     }
/* 1198 */     return object;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\zaxxer\hikari\HikariConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */