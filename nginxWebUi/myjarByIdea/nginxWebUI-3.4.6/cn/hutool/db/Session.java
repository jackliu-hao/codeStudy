package cn.hutool.db;

import cn.hutool.core.lang.func.VoidFunc1;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.dialect.Dialect;
import cn.hutool.db.dialect.DialectFactory;
import cn.hutool.db.ds.DSFactory;
import cn.hutool.db.sql.Wrapper;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import javax.sql.DataSource;

public class Session extends AbstractDb implements Closeable {
   private static final long serialVersionUID = 3421251905539056945L;
   private static final Log log = LogFactory.get();

   public static Session create() {
      return new Session(DSFactory.get());
   }

   public static Session create(String group) {
      return new Session(DSFactory.get(group));
   }

   public static Session create(DataSource ds) {
      return new Session(ds);
   }

   public Session(DataSource ds) {
      this(ds, DialectFactory.getDialect(ds));
   }

   public Session(DataSource ds, String driverClassName) {
      this(ds, DialectFactory.newDialect(driverClassName));
   }

   public Session(DataSource ds, Dialect dialect) {
      super(ds, dialect);
   }

   public SqlConnRunner getRunner() {
      return this.runner;
   }

   public void beginTransaction() throws SQLException {
      Connection conn = this.getConnection();
      this.checkTransactionSupported(conn);
      conn.setAutoCommit(false);
   }

   public void commit() throws SQLException {
      try {
         this.getConnection().commit();
      } finally {
         try {
            this.getConnection().setAutoCommit(true);
         } catch (SQLException var7) {
            log.error(var7);
         }

      }

   }

   public void rollback() throws SQLException {
      try {
         this.getConnection().rollback();
      } finally {
         try {
            this.getConnection().setAutoCommit(true);
         } catch (SQLException var7) {
            log.error(var7);
         }

      }

   }

   public void quietRollback() {
      try {
         this.getConnection().rollback();
      } catch (Exception var10) {
         log.error(var10);
      } finally {
         try {
            this.getConnection().setAutoCommit(true);
         } catch (SQLException var9) {
            log.error(var9);
         }

      }

   }

   public void rollback(Savepoint savepoint) throws SQLException {
      try {
         this.getConnection().rollback(savepoint);
      } finally {
         try {
            this.getConnection().setAutoCommit(true);
         } catch (SQLException var8) {
            log.error(var8);
         }

      }

   }

   public void quietRollback(Savepoint savepoint) {
      try {
         this.getConnection().rollback(savepoint);
      } catch (Exception var11) {
         log.error(var11);
      } finally {
         try {
            this.getConnection().setAutoCommit(true);
         } catch (SQLException var10) {
            log.error(var10);
         }

      }

   }

   public Savepoint setSavepoint() throws SQLException {
      return this.getConnection().setSavepoint();
   }

   public Savepoint setSavepoint(String name) throws SQLException {
      return this.getConnection().setSavepoint(name);
   }

   public void setTransactionIsolation(int level) throws SQLException {
      if (!this.getConnection().getMetaData().supportsTransactionIsolationLevel(level)) {
         throw new SQLException(StrUtil.format("Transaction isolation [{}] not support!", new Object[]{level}));
      } else {
         this.getConnection().setTransactionIsolation(level);
      }
   }

   public void tx(VoidFunc1<Session> func) throws SQLException {
      try {
         this.beginTransaction();
         func.call(this);
         this.commit();
      } catch (Throwable var3) {
         this.quietRollback();
         throw var3 instanceof SQLException ? (SQLException)var3 : new SQLException(var3);
      }
   }

   public Session setWrapper(Character wrapperChar) {
      return (Session)super.setWrapper(wrapperChar);
   }

   public Session setWrapper(Wrapper wrapper) {
      return (Session)super.setWrapper(wrapper);
   }

   public Session disableWrapper() {
      return (Session)super.disableWrapper();
   }

   public Connection getConnection() throws SQLException {
      return ThreadLocalConnection.INSTANCE.get(this.ds);
   }

   public void closeConnection(Connection conn) {
      try {
         if (conn != null && !conn.getAutoCommit()) {
            return;
         }
      } catch (SQLException var3) {
         log.error(var3);
      }

      ThreadLocalConnection.INSTANCE.close(this.ds);
   }

   public void close() {
      this.closeConnection((Connection)null);
   }
}
