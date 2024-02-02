package com.zaxxer.hikari.pool;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.SQLExceptionOverride;
import com.zaxxer.hikari.metrics.IMetricsTracker;
import com.zaxxer.hikari.util.ClockSource;
import com.zaxxer.hikari.util.DriverDataSource;
import com.zaxxer.hikari.util.PropertyElf;
import com.zaxxer.hikari.util.UtilityElf;
import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class PoolBase {
   private final Logger logger = LoggerFactory.getLogger(PoolBase.class);
   public final HikariConfig config;
   IMetricsTrackerDelegate metricsTracker;
   protected final String poolName;
   volatile String catalog;
   final AtomicReference<Exception> lastConnectionFailure;
   long connectionTimeout;
   long validationTimeout;
   SQLExceptionOverride exceptionOverride;
   private static final String[] RESET_STATES = new String[]{"readOnly", "autoCommit", "isolation", "catalog", "netTimeout", "schema"};
   private static final int UNINITIALIZED = -1;
   private static final int TRUE = 1;
   private static final int FALSE = 0;
   private int networkTimeout;
   private int isNetworkTimeoutSupported;
   private int isQueryTimeoutSupported;
   private int defaultTransactionIsolation;
   private int transactionIsolation;
   private Executor netTimeoutExecutor;
   private DataSource dataSource;
   private final String schema;
   private final boolean isReadOnly;
   private final boolean isAutoCommit;
   private final boolean isUseJdbc4Validation;
   private final boolean isIsolateInternalQueries;
   private volatile boolean isValidChecked;

   PoolBase(HikariConfig config) {
      this.config = config;
      this.networkTimeout = -1;
      this.catalog = config.getCatalog();
      this.schema = config.getSchema();
      this.isReadOnly = config.isReadOnly();
      this.isAutoCommit = config.isAutoCommit();
      this.exceptionOverride = (SQLExceptionOverride)UtilityElf.createInstance(config.getExceptionOverrideClassName(), SQLExceptionOverride.class);
      this.transactionIsolation = UtilityElf.getTransactionIsolation(config.getTransactionIsolation());
      this.isQueryTimeoutSupported = -1;
      this.isNetworkTimeoutSupported = -1;
      this.isUseJdbc4Validation = config.getConnectionTestQuery() == null;
      this.isIsolateInternalQueries = config.isIsolateInternalQueries();
      this.poolName = config.getPoolName();
      this.connectionTimeout = config.getConnectionTimeout();
      this.validationTimeout = config.getValidationTimeout();
      this.lastConnectionFailure = new AtomicReference();
      this.initializeDataSource();
   }

   public String toString() {
      return this.poolName;
   }

   abstract void recycle(PoolEntry var1);

   void quietlyCloseConnection(Connection connection, String closureReason) {
      if (connection != null) {
         try {
            this.logger.debug("{} - Closing connection {}: {}", this.poolName, connection, closureReason);

            try {
               this.setNetworkTimeout(connection, TimeUnit.SECONDS.toMillis(15L));
            } catch (SQLException var8) {
            } finally {
               connection.close();
            }
         } catch (Exception var10) {
            this.logger.debug("{} - Closing connection {} failed", this.poolName, connection, var10);
         }
      }

   }

   boolean isConnectionAlive(Connection connection) {
      try {
         boolean var3;
         try {
            this.setNetworkTimeout(connection, this.validationTimeout);
            int validationSeconds = (int)Math.max(1000L, this.validationTimeout) / 1000;
            if (!this.isUseJdbc4Validation) {
               Statement statement = connection.createStatement();

               try {
                  if (this.isNetworkTimeoutSupported != 1) {
                     this.setQueryTimeout(statement, validationSeconds);
                  }

                  statement.execute(this.config.getConnectionTestQuery());
               } catch (Throwable var12) {
                  if (statement != null) {
                     try {
                        statement.close();
                     } catch (Throwable var11) {
                        var12.addSuppressed(var11);
                     }
                  }

                  throw var12;
               }

               if (statement != null) {
                  statement.close();
               }

               return true;
            }

            var3 = connection.isValid(validationSeconds);
         } finally {
            this.setNetworkTimeout(connection, (long)this.networkTimeout);
            if (this.isIsolateInternalQueries && !this.isAutoCommit) {
               connection.rollback();
            }

         }

         return var3;
      } catch (Exception var14) {
         this.lastConnectionFailure.set(var14);
         this.logger.warn("{} - Failed to validate connection {} ({}). Possibly consider using a shorter maxLifetime value.", this.poolName, connection, var14.getMessage());
         return false;
      }
   }

   Exception getLastConnectionFailure() {
      return (Exception)this.lastConnectionFailure.get();
   }

   public DataSource getUnwrappedDataSource() {
      return this.dataSource;
   }

   PoolEntry newPoolEntry() throws Exception {
      return new PoolEntry(this.newConnection(), this, this.isReadOnly, this.isAutoCommit);
   }

   void resetConnectionState(Connection connection, ProxyConnection proxyConnection, int dirtyBits) throws SQLException {
      int resetBits = 0;
      if ((dirtyBits & 1) != 0 && proxyConnection.getReadOnlyState() != this.isReadOnly) {
         connection.setReadOnly(this.isReadOnly);
         resetBits |= 1;
      }

      if ((dirtyBits & 2) != 0 && proxyConnection.getAutoCommitState() != this.isAutoCommit) {
         connection.setAutoCommit(this.isAutoCommit);
         resetBits |= 2;
      }

      if ((dirtyBits & 4) != 0 && proxyConnection.getTransactionIsolationState() != this.transactionIsolation) {
         connection.setTransactionIsolation(this.transactionIsolation);
         resetBits |= 4;
      }

      if ((dirtyBits & 8) != 0 && this.catalog != null && !this.catalog.equals(proxyConnection.getCatalogState())) {
         connection.setCatalog(this.catalog);
         resetBits |= 8;
      }

      if ((dirtyBits & 16) != 0 && proxyConnection.getNetworkTimeoutState() != this.networkTimeout) {
         this.setNetworkTimeout(connection, (long)this.networkTimeout);
         resetBits |= 16;
      }

      if ((dirtyBits & 32) != 0 && this.schema != null && !this.schema.equals(proxyConnection.getSchemaState())) {
         connection.setSchema(this.schema);
         resetBits |= 32;
      }

      if (resetBits != 0 && this.logger.isDebugEnabled()) {
         this.logger.debug("{} - Reset ({}) on connection {}", this.poolName, this.stringFromResetBits(resetBits), connection);
      }

   }

   void shutdownNetworkTimeoutExecutor() {
      if (this.netTimeoutExecutor instanceof ThreadPoolExecutor) {
         ((ThreadPoolExecutor)this.netTimeoutExecutor).shutdownNow();
      }

   }

   long getLoginTimeout() {
      try {
         return this.dataSource != null ? (long)this.dataSource.getLoginTimeout() : TimeUnit.SECONDS.toSeconds(5L);
      } catch (SQLException var2) {
         return TimeUnit.SECONDS.toSeconds(5L);
      }
   }

   void handleMBeans(HikariPool hikariPool, boolean register) {
      if (this.config.isRegisterMbeans()) {
         try {
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            ObjectName beanConfigName;
            ObjectName beanPoolName;
            if ("true".equals(System.getProperty("hikaricp.jmx.register2.0"))) {
               beanConfigName = new ObjectName("com.zaxxer.hikari:type=PoolConfig,name=" + this.poolName);
               beanPoolName = new ObjectName("com.zaxxer.hikari:type=Pool,name=" + this.poolName);
            } else {
               beanConfigName = new ObjectName("com.zaxxer.hikari:type=PoolConfig (" + this.poolName + ")");
               beanPoolName = new ObjectName("com.zaxxer.hikari:type=Pool (" + this.poolName + ")");
            }

            if (register) {
               if (!mBeanServer.isRegistered(beanConfigName)) {
                  mBeanServer.registerMBean(this.config, beanConfigName);
                  mBeanServer.registerMBean(hikariPool, beanPoolName);
               } else {
                  this.logger.error((String)"{} - JMX name ({}) is already registered.", (Object)this.poolName, (Object)this.poolName);
               }
            } else if (mBeanServer.isRegistered(beanConfigName)) {
               mBeanServer.unregisterMBean(beanConfigName);
               mBeanServer.unregisterMBean(beanPoolName);
            }
         } catch (Exception var6) {
            this.logger.warn("{} - Failed to {} management beans.", this.poolName, register ? "register" : "unregister", var6);
         }

      }
   }

   private void initializeDataSource() {
      String jdbcUrl = this.config.getJdbcUrl();
      String username = this.config.getUsername();
      String password = this.config.getPassword();
      String dsClassName = this.config.getDataSourceClassName();
      String driverClassName = this.config.getDriverClassName();
      String dataSourceJNDI = this.config.getDataSourceJNDI();
      Properties dataSourceProperties = this.config.getDataSourceProperties();
      DataSource ds = this.config.getDataSource();
      if (dsClassName != null && ds == null) {
         ds = (DataSource)UtilityElf.createInstance(dsClassName, DataSource.class);
         PropertyElf.setTargetFromProperties(ds, dataSourceProperties);
      } else if (jdbcUrl != null && ds == null) {
         ds = new DriverDataSource(jdbcUrl, driverClassName, dataSourceProperties, username, password);
      } else if (dataSourceJNDI != null && ds == null) {
         try {
            InitialContext ic = new InitialContext();
            ds = (DataSource)ic.lookup(dataSourceJNDI);
         } catch (NamingException var10) {
            throw new HikariPool.PoolInitializationException(var10);
         }
      }

      if (ds != null) {
         this.setLoginTimeout((DataSource)ds);
         this.createNetworkTimeoutExecutor((DataSource)ds, dsClassName, jdbcUrl);
      }

      this.dataSource = (DataSource)ds;
   }

   private Connection newConnection() throws Exception {
      long start = ClockSource.currentTime();
      Connection connection = null;

      Connection var6;
      try {
         String username = this.config.getUsername();
         String password = this.config.getPassword();
         connection = username == null ? this.dataSource.getConnection() : this.dataSource.getConnection(username, password);
         if (connection == null) {
            throw new SQLTransientConnectionException("DataSource returned null unexpectedly");
         }

         this.setupConnection(connection);
         this.lastConnectionFailure.set((Object)null);
         var6 = connection;
      } catch (Exception var10) {
         if (connection != null) {
            this.quietlyCloseConnection(connection, "(Failed to create/setup connection)");
         } else if (this.getLastConnectionFailure() == null) {
            this.logger.debug((String)"{} - Failed to create/setup connection: {}", (Object)this.poolName, (Object)var10.getMessage());
         }

         this.lastConnectionFailure.set(var10);
         throw var10;
      } finally {
         if (this.metricsTracker != null) {
            this.metricsTracker.recordConnectionCreated(ClockSource.elapsedMillis(start));
         }

      }

      return var6;
   }

   private void setupConnection(Connection connection) throws ConnectionSetupException {
      try {
         if (this.networkTimeout == -1) {
            this.networkTimeout = this.getAndSetNetworkTimeout(connection, this.validationTimeout);
         } else {
            this.setNetworkTimeout(connection, this.validationTimeout);
         }

         if (connection.isReadOnly() != this.isReadOnly) {
            connection.setReadOnly(this.isReadOnly);
         }

         if (connection.getAutoCommit() != this.isAutoCommit) {
            connection.setAutoCommit(this.isAutoCommit);
         }

         this.checkDriverSupport(connection);
         if (this.transactionIsolation != this.defaultTransactionIsolation) {
            connection.setTransactionIsolation(this.transactionIsolation);
         }

         if (this.catalog != null) {
            connection.setCatalog(this.catalog);
         }

         if (this.schema != null) {
            connection.setSchema(this.schema);
         }

         this.executeSql(connection, this.config.getConnectionInitSql(), true);
         this.setNetworkTimeout(connection, (long)this.networkTimeout);
      } catch (SQLException var3) {
         throw new ConnectionSetupException(var3);
      }
   }

   private void checkDriverSupport(Connection connection) throws SQLException {
      if (!this.isValidChecked) {
         this.checkValidationSupport(connection);
         this.checkDefaultIsolation(connection);
         this.isValidChecked = true;
      }

   }

   private void checkValidationSupport(Connection connection) throws SQLException {
      try {
         if (this.isUseJdbc4Validation) {
            connection.isValid(1);
         } else {
            this.executeSql(connection, this.config.getConnectionTestQuery(), false);
         }

      } catch (AbstractMethodError | Exception var3) {
         this.logger.error("{} - Failed to execute{} connection test query ({}).", this.poolName, this.isUseJdbc4Validation ? " isValid() for connection, configure" : "", var3.getMessage());
         throw var3;
      }
   }

   private void checkDefaultIsolation(Connection connection) throws SQLException {
      try {
         this.defaultTransactionIsolation = connection.getTransactionIsolation();
         if (this.transactionIsolation == -1) {
            this.transactionIsolation = this.defaultTransactionIsolation;
         }
      } catch (SQLException var3) {
         this.logger.warn((String)"{} - Default transaction isolation level detection failed ({}).", (Object)this.poolName, (Object)var3.getMessage());
         if (var3.getSQLState() != null && !var3.getSQLState().startsWith("08")) {
            throw var3;
         }
      }

   }

   private void setQueryTimeout(Statement statement, int timeoutSec) {
      if (this.isQueryTimeoutSupported != 0) {
         try {
            statement.setQueryTimeout(timeoutSec);
            this.isQueryTimeoutSupported = 1;
         } catch (Exception var4) {
            if (this.isQueryTimeoutSupported == -1) {
               this.isQueryTimeoutSupported = 0;
               this.logger.info((String)"{} - Failed to set query timeout for statement. ({})", (Object)this.poolName, (Object)var4.getMessage());
            }
         }
      }

   }

   private int getAndSetNetworkTimeout(Connection connection, long timeoutMs) {
      if (this.isNetworkTimeoutSupported != 0) {
         try {
            int originalTimeout = connection.getNetworkTimeout();
            connection.setNetworkTimeout(this.netTimeoutExecutor, (int)timeoutMs);
            this.isNetworkTimeoutSupported = 1;
            return originalTimeout;
         } catch (AbstractMethodError | Exception var5) {
            if (this.isNetworkTimeoutSupported == -1) {
               this.isNetworkTimeoutSupported = 0;
               this.logger.info((String)"{} - Driver does not support get/set network timeout for connections. ({})", (Object)this.poolName, (Object)var5.getMessage());
               if (this.validationTimeout < TimeUnit.SECONDS.toMillis(1L)) {
                  this.logger.warn((String)"{} - A validationTimeout of less than 1 second cannot be honored on drivers without setNetworkTimeout() support.", (Object)this.poolName);
               } else if (this.validationTimeout % TimeUnit.SECONDS.toMillis(1L) != 0L) {
                  this.logger.warn((String)"{} - A validationTimeout with fractional second granularity cannot be honored on drivers without setNetworkTimeout() support.", (Object)this.poolName);
               }
            }
         }
      }

      return 0;
   }

   private void setNetworkTimeout(Connection connection, long timeoutMs) throws SQLException {
      if (this.isNetworkTimeoutSupported == 1) {
         connection.setNetworkTimeout(this.netTimeoutExecutor, (int)timeoutMs);
      }

   }

   private void executeSql(Connection connection, String sql, boolean isCommit) throws SQLException {
      if (sql != null) {
         Statement statement = connection.createStatement();

         try {
            statement.execute(sql);
         } catch (Throwable var8) {
            if (statement != null) {
               try {
                  statement.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (statement != null) {
            statement.close();
         }

         if (this.isIsolateInternalQueries && !this.isAutoCommit) {
            if (isCommit) {
               connection.commit();
            } else {
               connection.rollback();
            }
         }
      }

   }

   private void createNetworkTimeoutExecutor(DataSource dataSource, String dsClassName, String jdbcUrl) {
      if (dsClassName != null && dsClassName.contains("Mysql") || jdbcUrl != null && jdbcUrl.contains("mysql") || dataSource != null && dataSource.getClass().getName().contains("Mysql")) {
         this.netTimeoutExecutor = new SynchronousExecutor();
      } else {
         ThreadFactory threadFactory = this.config.getThreadFactory();
         ThreadFactory threadFactory = threadFactory != null ? threadFactory : new UtilityElf.DefaultThreadFactory(this.poolName + " network timeout executor", true);
         ThreadPoolExecutor executor = (ThreadPoolExecutor)Executors.newCachedThreadPool((ThreadFactory)threadFactory);
         executor.setKeepAliveTime(15L, TimeUnit.SECONDS);
         executor.allowCoreThreadTimeOut(true);
         this.netTimeoutExecutor = executor;
      }

   }

   private void setLoginTimeout(DataSource dataSource) {
      if (this.connectionTimeout != 2147483647L) {
         try {
            dataSource.setLoginTimeout(Math.max(1, (int)TimeUnit.MILLISECONDS.toSeconds(500L + this.connectionTimeout)));
         } catch (Exception var3) {
            this.logger.info((String)"{} - Failed to set login timeout for data source. ({})", (Object)this.poolName, (Object)var3.getMessage());
         }
      }

   }

   private String stringFromResetBits(int bits) {
      StringBuilder sb = new StringBuilder();

      for(int ndx = 0; ndx < RESET_STATES.length; ++ndx) {
         if ((bits & 1 << ndx) != 0) {
            sb.append(RESET_STATES[ndx]).append(", ");
         }
      }

      sb.setLength(sb.length() - 2);
      return sb.toString();
   }

   static final class NopMetricsTrackerDelegate implements IMetricsTrackerDelegate {
   }

   static class MetricsTrackerDelegate implements IMetricsTrackerDelegate {
      final IMetricsTracker tracker;

      MetricsTrackerDelegate(IMetricsTracker tracker) {
         this.tracker = tracker;
      }

      public void recordConnectionUsage(PoolEntry poolEntry) {
         this.tracker.recordConnectionUsageMillis(poolEntry.getMillisSinceBorrowed());
      }

      public void recordConnectionCreated(long connectionCreatedMillis) {
         this.tracker.recordConnectionCreatedMillis(connectionCreatedMillis);
      }

      public void recordBorrowTimeoutStats(long startTime) {
         this.tracker.recordConnectionAcquiredNanos(ClockSource.elapsedNanos(startTime));
      }

      public void recordBorrowStats(PoolEntry poolEntry, long startTime) {
         long now = ClockSource.currentTime();
         poolEntry.lastBorrowed = now;
         this.tracker.recordConnectionAcquiredNanos(ClockSource.elapsedNanos(startTime, now));
      }

      public void recordConnectionTimeout() {
         this.tracker.recordConnectionTimeout();
      }

      public void close() {
         this.tracker.close();
      }
   }

   interface IMetricsTrackerDelegate extends AutoCloseable {
      default void recordConnectionUsage(PoolEntry poolEntry) {
      }

      default void recordConnectionCreated(long connectionCreatedMillis) {
      }

      default void recordBorrowTimeoutStats(long startTime) {
      }

      default void recordBorrowStats(PoolEntry poolEntry, long startTime) {
      }

      default void recordConnectionTimeout() {
      }

      default void close() {
      }
   }

   private static class SynchronousExecutor implements Executor {
      private SynchronousExecutor() {
      }

      public void execute(Runnable command) {
         try {
            command.run();
         } catch (Exception var3) {
            LoggerFactory.getLogger(PoolBase.class).debug((String)"Failed to execute: {}", (Object)command, (Object)var3);
         }

      }

      // $FF: synthetic method
      SynchronousExecutor(Object x0) {
         this();
      }
   }

   static class ConnectionSetupException extends Exception {
      private static final long serialVersionUID = 929872118275916521L;

      ConnectionSetupException(Throwable t) {
         super(t);
      }
   }
}
