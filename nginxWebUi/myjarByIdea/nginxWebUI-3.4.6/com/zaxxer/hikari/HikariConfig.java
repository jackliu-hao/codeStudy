package com.zaxxer.hikari;

import com.codahale.metrics.health.HealthCheckRegistry;
import com.zaxxer.hikari.metrics.MetricsTrackerFactory;
import com.zaxxer.hikari.util.PropertyElf;
import com.zaxxer.hikari.util.UtilityElf;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessControlException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HikariConfig implements HikariConfigMXBean {
   private static final Logger LOGGER = LoggerFactory.getLogger(HikariConfig.class);
   private static final char[] ID_CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
   private static final long CONNECTION_TIMEOUT;
   private static final long VALIDATION_TIMEOUT;
   private static final long SOFT_TIMEOUT_FLOOR;
   private static final long IDLE_TIMEOUT;
   private static final long MAX_LIFETIME;
   private static final long DEFAULT_KEEPALIVE_TIME = 0L;
   private static final int DEFAULT_POOL_SIZE = 10;
   private static boolean unitTest;
   private volatile String catalog;
   private volatile long connectionTimeout;
   private volatile long validationTimeout;
   private volatile long idleTimeout;
   private volatile long leakDetectionThreshold;
   private volatile long maxLifetime;
   private volatile int maxPoolSize;
   private volatile int minIdle;
   private volatile String username;
   private volatile String password;
   private long initializationFailTimeout;
   private String connectionInitSql;
   private String connectionTestQuery;
   private String dataSourceClassName;
   private String dataSourceJndiName;
   private String driverClassName;
   private String exceptionOverrideClassName;
   private String jdbcUrl;
   private String poolName;
   private String schema;
   private String transactionIsolationName;
   private boolean isAutoCommit;
   private boolean isReadOnly;
   private boolean isIsolateInternalQueries;
   private boolean isRegisterMbeans;
   private boolean isAllowPoolSuspension;
   private DataSource dataSource;
   private Properties dataSourceProperties;
   private ThreadFactory threadFactory;
   private ScheduledExecutorService scheduledExecutor;
   private MetricsTrackerFactory metricsTrackerFactory;
   private Object metricRegistry;
   private Object healthCheckRegistry;
   private Properties healthCheckProperties;
   private long keepaliveTime;
   private volatile boolean sealed;

   public HikariConfig() {
      this.dataSourceProperties = new Properties();
      this.healthCheckProperties = new Properties();
      this.minIdle = -1;
      this.maxPoolSize = -1;
      this.maxLifetime = MAX_LIFETIME;
      this.connectionTimeout = CONNECTION_TIMEOUT;
      this.validationTimeout = VALIDATION_TIMEOUT;
      this.idleTimeout = IDLE_TIMEOUT;
      this.initializationFailTimeout = 1L;
      this.isAutoCommit = true;
      this.keepaliveTime = 0L;
      String systemProp = System.getProperty("hikaricp.configurationFile");
      if (systemProp != null) {
         this.loadProperties(systemProp);
      }

   }

   public HikariConfig(Properties properties) {
      this();
      PropertyElf.setTargetFromProperties(this, properties);
   }

   public HikariConfig(String propertyFileName) {
      this();
      this.loadProperties(propertyFileName);
   }

   public String getCatalog() {
      return this.catalog;
   }

   public void setCatalog(String catalog) {
      this.catalog = catalog;
   }

   public long getConnectionTimeout() {
      return this.connectionTimeout;
   }

   public void setConnectionTimeout(long connectionTimeoutMs) {
      if (connectionTimeoutMs == 0L) {
         this.connectionTimeout = 2147483647L;
      } else {
         if (connectionTimeoutMs < SOFT_TIMEOUT_FLOOR) {
            throw new IllegalArgumentException("connectionTimeout cannot be less than " + SOFT_TIMEOUT_FLOOR + "ms");
         }

         this.connectionTimeout = connectionTimeoutMs;
      }

   }

   public long getIdleTimeout() {
      return this.idleTimeout;
   }

   public void setIdleTimeout(long idleTimeoutMs) {
      if (idleTimeoutMs < 0L) {
         throw new IllegalArgumentException("idleTimeout cannot be negative");
      } else {
         this.idleTimeout = idleTimeoutMs;
      }
   }

   public long getLeakDetectionThreshold() {
      return this.leakDetectionThreshold;
   }

   public void setLeakDetectionThreshold(long leakDetectionThresholdMs) {
      this.leakDetectionThreshold = leakDetectionThresholdMs;
   }

   public long getMaxLifetime() {
      return this.maxLifetime;
   }

   public void setMaxLifetime(long maxLifetimeMs) {
      this.maxLifetime = maxLifetimeMs;
   }

   public int getMaximumPoolSize() {
      return this.maxPoolSize;
   }

   public void setMaximumPoolSize(int maxPoolSize) {
      if (maxPoolSize < 1) {
         throw new IllegalArgumentException("maxPoolSize cannot be less than 1");
      } else {
         this.maxPoolSize = maxPoolSize;
      }
   }

   public int getMinimumIdle() {
      return this.minIdle;
   }

   public void setMinimumIdle(int minIdle) {
      if (minIdle < 0) {
         throw new IllegalArgumentException("minimumIdle cannot be negative");
      } else {
         this.minIdle = minIdle;
      }
   }

   public String getPassword() {
      return this.password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public String getUsername() {
      return this.username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public long getValidationTimeout() {
      return this.validationTimeout;
   }

   public void setValidationTimeout(long validationTimeoutMs) {
      if (validationTimeoutMs < SOFT_TIMEOUT_FLOOR) {
         throw new IllegalArgumentException("validationTimeout cannot be less than " + SOFT_TIMEOUT_FLOOR + "ms");
      } else {
         this.validationTimeout = validationTimeoutMs;
      }
   }

   public String getConnectionTestQuery() {
      return this.connectionTestQuery;
   }

   public void setConnectionTestQuery(String connectionTestQuery) {
      this.checkIfSealed();
      this.connectionTestQuery = connectionTestQuery;
   }

   public String getConnectionInitSql() {
      return this.connectionInitSql;
   }

   public void setConnectionInitSql(String connectionInitSql) {
      this.checkIfSealed();
      this.connectionInitSql = connectionInitSql;
   }

   public DataSource getDataSource() {
      return this.dataSource;
   }

   public void setDataSource(DataSource dataSource) {
      this.checkIfSealed();
      this.dataSource = dataSource;
   }

   public String getDataSourceClassName() {
      return this.dataSourceClassName;
   }

   public void setDataSourceClassName(String className) {
      this.checkIfSealed();
      this.dataSourceClassName = className;
   }

   public void addDataSourceProperty(String propertyName, Object value) {
      this.checkIfSealed();
      this.dataSourceProperties.put(propertyName, value);
   }

   public String getDataSourceJNDI() {
      return this.dataSourceJndiName;
   }

   public void setDataSourceJNDI(String jndiDataSource) {
      this.checkIfSealed();
      this.dataSourceJndiName = jndiDataSource;
   }

   public Properties getDataSourceProperties() {
      return this.dataSourceProperties;
   }

   public void setDataSourceProperties(Properties dsProperties) {
      this.checkIfSealed();
      this.dataSourceProperties.putAll(dsProperties);
   }

   public String getDriverClassName() {
      return this.driverClassName;
   }

   public void setDriverClassName(String driverClassName) {
      this.checkIfSealed();
      Class<?> driverClass = this.attemptFromContextLoader(driverClassName);

      try {
         if (driverClass == null) {
            driverClass = this.getClass().getClassLoader().loadClass(driverClassName);
            LOGGER.debug((String)"Driver class {} found in the HikariConfig class classloader {}", (Object)driverClassName, (Object)this.getClass().getClassLoader());
         }
      } catch (ClassNotFoundException var5) {
         LOGGER.error((String)"Failed to load driver class {} from HikariConfig class classloader {}", (Object)driverClassName, (Object)this.getClass().getClassLoader());
      }

      if (driverClass == null) {
         throw new RuntimeException("Failed to load driver class " + driverClassName + " in either of HikariConfig class loader or Thread context classloader");
      } else {
         try {
            driverClass.getConstructor().newInstance();
            this.driverClassName = driverClassName;
         } catch (Exception var4) {
            throw new RuntimeException("Failed to instantiate class " + driverClassName, var4);
         }
      }
   }

   public String getJdbcUrl() {
      return this.jdbcUrl;
   }

   public void setJdbcUrl(String jdbcUrl) {
      this.checkIfSealed();
      this.jdbcUrl = jdbcUrl;
   }

   public boolean isAutoCommit() {
      return this.isAutoCommit;
   }

   public void setAutoCommit(boolean isAutoCommit) {
      this.checkIfSealed();
      this.isAutoCommit = isAutoCommit;
   }

   public boolean isAllowPoolSuspension() {
      return this.isAllowPoolSuspension;
   }

   public void setAllowPoolSuspension(boolean isAllowPoolSuspension) {
      this.checkIfSealed();
      this.isAllowPoolSuspension = isAllowPoolSuspension;
   }

   public long getInitializationFailTimeout() {
      return this.initializationFailTimeout;
   }

   public void setInitializationFailTimeout(long initializationFailTimeout) {
      this.checkIfSealed();
      this.initializationFailTimeout = initializationFailTimeout;
   }

   public boolean isIsolateInternalQueries() {
      return this.isIsolateInternalQueries;
   }

   public void setIsolateInternalQueries(boolean isolate) {
      this.checkIfSealed();
      this.isIsolateInternalQueries = isolate;
   }

   public MetricsTrackerFactory getMetricsTrackerFactory() {
      return this.metricsTrackerFactory;
   }

   public void setMetricsTrackerFactory(MetricsTrackerFactory metricsTrackerFactory) {
      if (this.metricRegistry != null) {
         throw new IllegalStateException("cannot use setMetricsTrackerFactory() and setMetricRegistry() together");
      } else {
         this.metricsTrackerFactory = metricsTrackerFactory;
      }
   }

   public Object getMetricRegistry() {
      return this.metricRegistry;
   }

   public void setMetricRegistry(Object metricRegistry) {
      if (this.metricsTrackerFactory != null) {
         throw new IllegalStateException("cannot use setMetricRegistry() and setMetricsTrackerFactory() together");
      } else {
         if (metricRegistry != null) {
            metricRegistry = this.getObjectOrPerformJndiLookup(metricRegistry);
            if (!UtilityElf.safeIsAssignableFrom(metricRegistry, "com.codahale.metrics.MetricRegistry") && !UtilityElf.safeIsAssignableFrom(metricRegistry, "io.micrometer.core.instrument.MeterRegistry")) {
               throw new IllegalArgumentException("Class must be instance of com.codahale.metrics.MetricRegistry or io.micrometer.core.instrument.MeterRegistry");
            }
         }

         this.metricRegistry = metricRegistry;
      }
   }

   public Object getHealthCheckRegistry() {
      return this.healthCheckRegistry;
   }

   public void setHealthCheckRegistry(Object healthCheckRegistry) {
      this.checkIfSealed();
      if (healthCheckRegistry != null) {
         healthCheckRegistry = this.getObjectOrPerformJndiLookup(healthCheckRegistry);
         if (!(healthCheckRegistry instanceof HealthCheckRegistry)) {
            throw new IllegalArgumentException("Class must be an instance of com.codahale.metrics.health.HealthCheckRegistry");
         }
      }

      this.healthCheckRegistry = healthCheckRegistry;
   }

   public Properties getHealthCheckProperties() {
      return this.healthCheckProperties;
   }

   public void setHealthCheckProperties(Properties healthCheckProperties) {
      this.checkIfSealed();
      this.healthCheckProperties.putAll(healthCheckProperties);
   }

   public void addHealthCheckProperty(String key, String value) {
      this.checkIfSealed();
      this.healthCheckProperties.setProperty(key, value);
   }

   public long getKeepaliveTime() {
      return this.keepaliveTime;
   }

   public void setKeepaliveTime(long keepaliveTimeMs) {
      this.keepaliveTime = keepaliveTimeMs;
   }

   public boolean isReadOnly() {
      return this.isReadOnly;
   }

   public void setReadOnly(boolean readOnly) {
      this.checkIfSealed();
      this.isReadOnly = readOnly;
   }

   public boolean isRegisterMbeans() {
      return this.isRegisterMbeans;
   }

   public void setRegisterMbeans(boolean register) {
      this.checkIfSealed();
      this.isRegisterMbeans = register;
   }

   public String getPoolName() {
      return this.poolName;
   }

   public void setPoolName(String poolName) {
      this.checkIfSealed();
      this.poolName = poolName;
   }

   public ScheduledExecutorService getScheduledExecutor() {
      return this.scheduledExecutor;
   }

   public void setScheduledExecutor(ScheduledExecutorService executor) {
      this.checkIfSealed();
      this.scheduledExecutor = executor;
   }

   public String getTransactionIsolation() {
      return this.transactionIsolationName;
   }

   public String getSchema() {
      return this.schema;
   }

   public void setSchema(String schema) {
      this.checkIfSealed();
      this.schema = schema;
   }

   public String getExceptionOverrideClassName() {
      return this.exceptionOverrideClassName;
   }

   public void setExceptionOverrideClassName(String exceptionOverrideClassName) {
      this.checkIfSealed();
      Class<?> overrideClass = this.attemptFromContextLoader(exceptionOverrideClassName);

      try {
         if (overrideClass == null) {
            overrideClass = this.getClass().getClassLoader().loadClass(exceptionOverrideClassName);
            LOGGER.debug((String)"SQLExceptionOverride class {} found in the HikariConfig class classloader {}", (Object)exceptionOverrideClassName, (Object)this.getClass().getClassLoader());
         }
      } catch (ClassNotFoundException var5) {
         LOGGER.error((String)"Failed to load SQLExceptionOverride class {} from HikariConfig class classloader {}", (Object)exceptionOverrideClassName, (Object)this.getClass().getClassLoader());
      }

      if (overrideClass == null) {
         throw new RuntimeException("Failed to load SQLExceptionOverride class " + exceptionOverrideClassName + " in either of HikariConfig class loader or Thread context classloader");
      } else {
         try {
            overrideClass.getConstructor().newInstance();
            this.exceptionOverrideClassName = exceptionOverrideClassName;
         } catch (Exception var4) {
            throw new RuntimeException("Failed to instantiate class " + exceptionOverrideClassName, var4);
         }
      }
   }

   public void setTransactionIsolation(String isolationLevel) {
      this.checkIfSealed();
      this.transactionIsolationName = isolationLevel;
   }

   public ThreadFactory getThreadFactory() {
      return this.threadFactory;
   }

   public void setThreadFactory(ThreadFactory threadFactory) {
      this.checkIfSealed();
      this.threadFactory = threadFactory;
   }

   void seal() {
      this.sealed = true;
   }

   public void copyStateTo(HikariConfig other) {
      Field[] var2 = HikariConfig.class.getDeclaredFields();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Field field = var2[var4];
         if (!Modifier.isFinal(field.getModifiers())) {
            field.setAccessible(true);

            try {
               field.set(other, field.get(this));
            } catch (Exception var7) {
               throw new RuntimeException("Failed to copy HikariConfig state: " + var7.getMessage(), var7);
            }
         }
      }

      other.sealed = false;
   }

   private Class<?> attemptFromContextLoader(String driverClassName) {
      ClassLoader threadContextClassLoader = Thread.currentThread().getContextClassLoader();
      if (threadContextClassLoader != null) {
         try {
            Class<?> driverClass = threadContextClassLoader.loadClass(driverClassName);
            LOGGER.debug((String)"Driver class {} found in Thread context class loader {}", (Object)driverClassName, (Object)threadContextClassLoader);
            return driverClass;
         } catch (ClassNotFoundException var4) {
            LOGGER.debug("Driver class {} not found in Thread context class loader {}, trying classloader {}", driverClassName, threadContextClassLoader, this.getClass().getClassLoader());
         }
      }

      return null;
   }

   public void validate() {
      if (this.poolName == null) {
         this.poolName = this.generatePoolName();
      } else if (this.isRegisterMbeans && this.poolName.contains(":")) {
         throw new IllegalArgumentException("poolName cannot contain ':' when used with JMX");
      }

      this.catalog = UtilityElf.getNullIfEmpty(this.catalog);
      this.connectionInitSql = UtilityElf.getNullIfEmpty(this.connectionInitSql);
      this.connectionTestQuery = UtilityElf.getNullIfEmpty(this.connectionTestQuery);
      this.transactionIsolationName = UtilityElf.getNullIfEmpty(this.transactionIsolationName);
      this.dataSourceClassName = UtilityElf.getNullIfEmpty(this.dataSourceClassName);
      this.dataSourceJndiName = UtilityElf.getNullIfEmpty(this.dataSourceJndiName);
      this.driverClassName = UtilityElf.getNullIfEmpty(this.driverClassName);
      this.jdbcUrl = UtilityElf.getNullIfEmpty(this.jdbcUrl);
      if (this.dataSource != null) {
         if (this.dataSourceClassName != null) {
            LOGGER.warn((String)"{} - using dataSource and ignoring dataSourceClassName.", (Object)this.poolName);
         }
      } else if (this.dataSourceClassName != null) {
         if (this.driverClassName != null) {
            LOGGER.error((String)"{} - cannot use driverClassName and dataSourceClassName together.", (Object)this.poolName);
            throw new IllegalStateException("cannot use driverClassName and dataSourceClassName together.");
         }

         if (this.jdbcUrl != null) {
            LOGGER.warn((String)"{} - using dataSourceClassName and ignoring jdbcUrl.", (Object)this.poolName);
         }
      } else if (this.jdbcUrl == null && this.dataSourceJndiName == null) {
         if (this.driverClassName != null) {
            LOGGER.error((String)"{} - jdbcUrl is required with driverClassName.", (Object)this.poolName);
            throw new IllegalArgumentException("jdbcUrl is required with driverClassName.");
         }

         LOGGER.error((String)"{} - dataSource or dataSourceClassName or jdbcUrl is required.", (Object)this.poolName);
         throw new IllegalArgumentException("dataSource or dataSourceClassName or jdbcUrl is required.");
      }

      this.validateNumerics();
      if (LOGGER.isDebugEnabled() || unitTest) {
         this.logConfiguration();
      }

   }

   private void validateNumerics() {
      if (this.maxLifetime != 0L && this.maxLifetime < TimeUnit.SECONDS.toMillis(30L)) {
         LOGGER.warn((String)"{} - maxLifetime is less than 30000ms, setting to default {}ms.", (Object)this.poolName, (Object)MAX_LIFETIME);
         this.maxLifetime = MAX_LIFETIME;
      }

      if (this.keepaliveTime != 0L && this.keepaliveTime < TimeUnit.SECONDS.toMillis(30L)) {
         LOGGER.warn((String)"{} - keepaliveTime is less than 30000ms, disabling it.", (Object)this.poolName);
         this.keepaliveTime = 0L;
      }

      if (this.keepaliveTime != 0L && this.maxLifetime != 0L && this.keepaliveTime >= this.maxLifetime) {
         LOGGER.warn((String)"{} - keepaliveTime is greater than or equal to maxLifetime, disabling it.", (Object)this.poolName);
         this.keepaliveTime = 0L;
      }

      if (this.leakDetectionThreshold > 0L && !unitTest && (this.leakDetectionThreshold < TimeUnit.SECONDS.toMillis(2L) || this.leakDetectionThreshold > this.maxLifetime && this.maxLifetime > 0L)) {
         LOGGER.warn((String)"{} - leakDetectionThreshold is less than 2000ms or more than maxLifetime, disabling it.", (Object)this.poolName);
         this.leakDetectionThreshold = 0L;
      }

      if (this.connectionTimeout < SOFT_TIMEOUT_FLOOR) {
         LOGGER.warn("{} - connectionTimeout is less than {}ms, setting to {}ms.", this.poolName, SOFT_TIMEOUT_FLOOR, CONNECTION_TIMEOUT);
         this.connectionTimeout = CONNECTION_TIMEOUT;
      }

      if (this.validationTimeout < SOFT_TIMEOUT_FLOOR) {
         LOGGER.warn("{} - validationTimeout is less than {}ms, setting to {}ms.", this.poolName, SOFT_TIMEOUT_FLOOR, VALIDATION_TIMEOUT);
         this.validationTimeout = VALIDATION_TIMEOUT;
      }

      if (this.maxPoolSize < 1) {
         this.maxPoolSize = 10;
      }

      if (this.minIdle < 0 || this.minIdle > this.maxPoolSize) {
         this.minIdle = this.maxPoolSize;
      }

      if (this.idleTimeout + TimeUnit.SECONDS.toMillis(1L) > this.maxLifetime && this.maxLifetime > 0L && this.minIdle < this.maxPoolSize) {
         LOGGER.warn((String)"{} - idleTimeout is close to or more than maxLifetime, disabling it.", (Object)this.poolName);
         this.idleTimeout = 0L;
      } else if (this.idleTimeout != 0L && this.idleTimeout < TimeUnit.SECONDS.toMillis(10L) && this.minIdle < this.maxPoolSize) {
         LOGGER.warn((String)"{} - idleTimeout is less than 10000ms, setting to default {}ms.", (Object)this.poolName, (Object)IDLE_TIMEOUT);
         this.idleTimeout = IDLE_TIMEOUT;
      } else if (this.idleTimeout != IDLE_TIMEOUT && this.idleTimeout != 0L && this.minIdle == this.maxPoolSize) {
         LOGGER.warn((String)"{} - idleTimeout has been set but has no effect because the pool is operating as a fixed size pool.", (Object)this.poolName);
      }

   }

   private void checkIfSealed() {
      if (this.sealed) {
         throw new IllegalStateException("The configuration of the pool is sealed once started. Use HikariConfigMXBean for runtime changes.");
      }
   }

   private void logConfiguration() {
      LOGGER.debug((String)"{} - configuration:", (Object)this.poolName);
      Set<String> propertyNames = new TreeSet(PropertyElf.getPropertyNames(HikariConfig.class));
      Iterator var2 = propertyNames.iterator();

      while(var2.hasNext()) {
         String prop = (String)var2.next();

         try {
            Object value = PropertyElf.getProperty(prop, this);
            if ("dataSourceProperties".equals(prop)) {
               Properties dsProps = PropertyElf.copyProperties(this.dataSourceProperties);
               dsProps.setProperty("password", "<masked>");
               value = dsProps;
            }

            if ("initializationFailTimeout".equals(prop) && this.initializationFailTimeout == Long.MAX_VALUE) {
               value = "infinite";
            } else if ("transactionIsolation".equals(prop) && this.transactionIsolationName == null) {
               value = "default";
            } else if (prop.matches("scheduledExecutorService|threadFactory") && value == null) {
               value = "internal";
            } else if (prop.contains("jdbcUrl") && value instanceof String) {
               value = ((String)value).replaceAll("([?&;]password=)[^&#;]*(.*)", "$1<masked>$2");
            } else if (prop.contains("password")) {
               value = "<masked>";
            } else if (value instanceof String) {
               value = "\"" + value + "\"";
            } else if (value == null) {
               value = "none";
            }

            LOGGER.debug((String)"{}{}", (Object)(prop + "................................................".substring(0, 32)), (Object)value);
         } catch (Exception var6) {
         }
      }

   }

   private void loadProperties(String propertyFileName) {
      File propFile = new File(propertyFileName);

      try {
         InputStream is = propFile.isFile() ? new FileInputStream(propFile) : this.getClass().getResourceAsStream(propertyFileName);

         try {
            if (is == null) {
               throw new IllegalArgumentException("Cannot find property file: " + propertyFileName);
            }

            Properties props = new Properties();
            props.load((InputStream)is);
            PropertyElf.setTargetFromProperties(this, props);
         } catch (Throwable var7) {
            if (is != null) {
               try {
                  ((InputStream)is).close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (is != null) {
            ((InputStream)is).close();
         }

      } catch (IOException var8) {
         throw new RuntimeException("Failed to read property file", var8);
      }
   }

   private String generatePoolName() {
      String prefix = "HikariPool-";

      try {
         synchronized(System.getProperties()) {
            String next = String.valueOf(Integer.getInteger("com.zaxxer.hikari.pool_number", 0) + 1);
            System.setProperty("com.zaxxer.hikari.pool_number", next);
            return "HikariPool-" + next;
         }
      } catch (AccessControlException var7) {
         ThreadLocalRandom random = ThreadLocalRandom.current();
         StringBuilder buf = new StringBuilder("HikariPool-");

         for(int i = 0; i < 4; ++i) {
            buf.append(ID_CHARACTERS[random.nextInt(62)]);
         }

         LOGGER.info((String)"assigned random pool name '{}' (security manager prevented access to system properties)", (Object)buf);
         return buf.toString();
      }
   }

   private Object getObjectOrPerformJndiLookup(Object object) {
      if (object instanceof String) {
         try {
            InitialContext initCtx = new InitialContext();
            return initCtx.lookup((String)object);
         } catch (NamingException var3) {
            throw new IllegalArgumentException(var3);
         }
      } else {
         return object;
      }
   }

   static {
      CONNECTION_TIMEOUT = TimeUnit.SECONDS.toMillis(30L);
      VALIDATION_TIMEOUT = TimeUnit.SECONDS.toMillis(5L);
      SOFT_TIMEOUT_FLOOR = Long.getLong("com.zaxxer.hikari.timeoutMs.floor", 250L);
      IDLE_TIMEOUT = TimeUnit.MINUTES.toMillis(10L);
      MAX_LIFETIME = TimeUnit.MINUTES.toMillis(30L);
      unitTest = false;
   }
}
