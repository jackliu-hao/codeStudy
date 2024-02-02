package cn.hutool.db;

import cn.hutool.core.lang.func.VoidFunc1;
import cn.hutool.db.dialect.Dialect;
import cn.hutool.db.dialect.DialectFactory;
import cn.hutool.db.ds.DSFactory;
import cn.hutool.db.sql.Wrapper;
import cn.hutool.db.transaction.TransactionLevel;
import cn.hutool.log.StaticLog;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

public class Db extends AbstractDb {
   private static final long serialVersionUID = -3378415769645309514L;

   public static Db use() {
      return use(DSFactory.get());
   }

   public static Db use(String group) {
      return use(DSFactory.get(group));
   }

   public static Db use(DataSource ds) {
      return ds == null ? null : new Db(ds);
   }

   public static Db use(DataSource ds, Dialect dialect) {
      return new Db(ds, dialect);
   }

   public static Db use(DataSource ds, String driverClassName) {
      return new Db(ds, DialectFactory.newDialect(driverClassName));
   }

   public Db(DataSource ds) {
      this(ds, DialectFactory.getDialect(ds));
   }

   public Db(DataSource ds, String driverClassName) {
      this(ds, DialectFactory.newDialect(driverClassName));
   }

   public Db(DataSource ds, Dialect dialect) {
      super(ds, dialect);
   }

   public Db setWrapper(Character wrapperChar) {
      return (Db)super.setWrapper(wrapperChar);
   }

   public Db setWrapper(Wrapper wrapper) {
      return (Db)super.setWrapper(wrapper);
   }

   public Db disableWrapper() {
      return (Db)super.disableWrapper();
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
      }

      ThreadLocalConnection.INSTANCE.close(this.ds);
   }

   public Db tx(VoidFunc1<Db> func) throws SQLException {
      return this.tx((TransactionLevel)null, func);
   }

   public Db tx(TransactionLevel transactionLevel, VoidFunc1<Db> func) throws SQLException {
      Connection conn = this.getConnection();
      this.checkTransactionSupported(conn);
      if (null != transactionLevel) {
         int level = transactionLevel.getLevel();
         if (conn.getTransactionIsolation() < level) {
            conn.setTransactionIsolation(level);
         }
      }

      boolean autoCommit = conn.getAutoCommit();
      if (autoCommit) {
         conn.setAutoCommit(false);
      }

      try {
         func.call(this);
         conn.commit();
      } catch (Throwable var9) {
         this.quietRollback(conn);
         throw var9 instanceof SQLException ? (SQLException)var9 : new SQLException(var9);
      } finally {
         this.quietSetAutoCommit(conn, autoCommit);
         this.closeConnection(conn);
      }

      return this;
   }

   private void quietRollback(Connection conn) {
      if (null != conn) {
         try {
            conn.rollback();
         } catch (Exception var3) {
            StaticLog.error(var3);
         }
      }

   }

   private void quietSetAutoCommit(Connection conn, Boolean autoCommit) {
      if (null != conn && null != autoCommit) {
         try {
            conn.setAutoCommit(autoCommit);
         } catch (Exception var4) {
            StaticLog.error(var4);
         }
      }

   }
}
