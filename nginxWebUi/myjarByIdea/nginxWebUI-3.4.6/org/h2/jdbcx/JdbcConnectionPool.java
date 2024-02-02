package org.h2.jdbcx;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.PooledConnection;
import org.h2.message.DbException;

public final class JdbcConnectionPool implements DataSource, ConnectionEventListener, JdbcConnectionPoolBackwardsCompat {
   private static final int DEFAULT_TIMEOUT = 30;
   private static final int DEFAULT_MAX_CONNECTIONS = 10;
   private final ConnectionPoolDataSource dataSource;
   private final Queue<PooledConnection> recycledConnections = new ConcurrentLinkedQueue();
   private PrintWriter logWriter;
   private volatile int maxConnections = 10;
   private volatile int timeout = 30;
   private AtomicInteger activeConnections = new AtomicInteger();
   private AtomicBoolean isDisposed = new AtomicBoolean();

   protected JdbcConnectionPool(ConnectionPoolDataSource var1) {
      this.dataSource = var1;
      if (var1 != null) {
         try {
            this.logWriter = var1.getLogWriter();
         } catch (SQLException var3) {
         }
      }

   }

   public static JdbcConnectionPool create(ConnectionPoolDataSource var0) {
      return new JdbcConnectionPool(var0);
   }

   public static JdbcConnectionPool create(String var0, String var1, String var2) {
      JdbcDataSource var3 = new JdbcDataSource();
      var3.setURL(var0);
      var3.setUser(var1);
      var3.setPassword(var2);
      return new JdbcConnectionPool(var3);
   }

   public void setMaxConnections(int var1) {
      if (var1 < 1) {
         throw new IllegalArgumentException("Invalid maxConnections value: " + var1);
      } else {
         this.maxConnections = var1;
      }
   }

   public int getMaxConnections() {
      return this.maxConnections;
   }

   public int getLoginTimeout() {
      return this.timeout;
   }

   public void setLoginTimeout(int var1) {
      if (var1 == 0) {
         var1 = 30;
      }

      this.timeout = var1;
   }

   public void dispose() {
      this.isDisposed.set(true);

      PooledConnection var1;
      while((var1 = (PooledConnection)this.recycledConnections.poll()) != null) {
         this.closeConnection(var1);
      }

   }

   public Connection getConnection() throws SQLException {
      long var1 = System.nanoTime() + (long)this.timeout * 1000000000L;
      int var3 = 0;

      do {
         if (this.activeConnections.incrementAndGet() <= this.maxConnections) {
            try {
               return this.getConnectionNow();
            } catch (Throwable var5) {
               this.activeConnections.decrementAndGet();
               throw var5;
            }
         }

         this.activeConnections.decrementAndGet();
         --var3;
         if (var3 < 0) {
            try {
               var3 = 3;
               Thread.sleep(1L);
            } catch (InterruptedException var6) {
               Thread.currentThread().interrupt();
            }
         }
      } while(System.nanoTime() - var1 <= 0L);

      throw new SQLException("Login timeout", "08001", 8001);
   }

   public Connection getConnection(String var1, String var2) {
      throw new UnsupportedOperationException();
   }

   private Connection getConnectionNow() throws SQLException {
      if (this.isDisposed.get()) {
         throw new IllegalStateException("Connection pool has been disposed.");
      } else {
         PooledConnection var1 = (PooledConnection)this.recycledConnections.poll();
         if (var1 == null) {
            var1 = this.dataSource.getPooledConnection();
         }

         Connection var2 = var1.getConnection();
         var1.addConnectionEventListener(this);
         return var2;
      }
   }

   private void recycleConnection(PooledConnection var1) {
      int var2 = this.activeConnections.decrementAndGet();
      if (var2 < 0) {
         this.activeConnections.incrementAndGet();
         throw new AssertionError();
      } else {
         if (!this.isDisposed.get() && var2 < this.maxConnections) {
            this.recycledConnections.add(var1);
            if (this.isDisposed.get()) {
               this.dispose();
            }
         } else {
            this.closeConnection(var1);
         }

      }
   }

   private void closeConnection(PooledConnection var1) {
      try {
         var1.close();
      } catch (SQLException var3) {
         if (this.logWriter != null) {
            var3.printStackTrace(this.logWriter);
         }
      }

   }

   public void connectionClosed(ConnectionEvent var1) {
      PooledConnection var2 = (PooledConnection)var1.getSource();
      var2.removeConnectionEventListener(this);
      this.recycleConnection(var2);
   }

   public void connectionErrorOccurred(ConnectionEvent var1) {
   }

   public int getActiveConnections() {
      return this.activeConnections.get();
   }

   public PrintWriter getLogWriter() {
      return this.logWriter;
   }

   public void setLogWriter(PrintWriter var1) {
      this.logWriter = var1;
   }

   public <T> T unwrap(Class<T> var1) throws SQLException {
      try {
         if (this.isWrapperFor(var1)) {
            return this;
         } else {
            throw DbException.getInvalidValueException("iface", var1);
         }
      } catch (Exception var3) {
         throw DbException.toSQLException(var3);
      }
   }

   public boolean isWrapperFor(Class<?> var1) throws SQLException {
      return var1 != null && var1.isAssignableFrom(this.getClass());
   }

   public Logger getParentLogger() {
      return null;
   }
}
