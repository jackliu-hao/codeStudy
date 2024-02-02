package org.h2.jdbcx;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.StatementEventListener;
import javax.sql.XAConnection;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import org.h2.jdbc.JdbcConnection;
import org.h2.message.DbException;
import org.h2.message.TraceObject;
import org.h2.util.Utils;

public final class JdbcXAConnection extends TraceObject implements XAConnection, XAResource {
   private final JdbcDataSourceFactory factory;
   private JdbcConnection physicalConn;
   private volatile Connection handleConn;
   private final ArrayList<ConnectionEventListener> listeners = Utils.newSmallArrayList();
   private Xid currentTransaction;
   private boolean prepared;

   JdbcXAConnection(JdbcDataSourceFactory var1, int var2, JdbcConnection var3) {
      this.factory = var1;
      this.setTrace(var1.getTrace(), 13, var2);
      this.physicalConn = var3;
   }

   public XAResource getXAResource() {
      this.debugCodeCall("getXAResource");
      return this;
   }

   public void close() throws SQLException {
      this.debugCodeCall("close");
      Connection var1 = this.handleConn;
      if (var1 != null) {
         this.listeners.clear();
         var1.close();
      }

      if (this.physicalConn != null) {
         try {
            this.physicalConn.close();
         } finally {
            this.physicalConn = null;
         }
      }

   }

   public Connection getConnection() throws SQLException {
      this.debugCodeCall("getConnection");
      Connection var1 = this.handleConn;
      if (var1 != null) {
         var1.close();
      }

      this.physicalConn.rollback();
      this.handleConn = new PooledJdbcConnection(this.physicalConn);
      return this.handleConn;
   }

   public void addConnectionEventListener(ConnectionEventListener var1) {
      this.debugCode("addConnectionEventListener(listener)");
      this.listeners.add(var1);
   }

   public void removeConnectionEventListener(ConnectionEventListener var1) {
      this.debugCode("removeConnectionEventListener(listener)");
      this.listeners.remove(var1);
   }

   void closedHandle() {
      this.debugCodeCall("closedHandle");
      ConnectionEvent var1 = new ConnectionEvent(this);

      for(int var2 = this.listeners.size() - 1; var2 >= 0; --var2) {
         ConnectionEventListener var3 = (ConnectionEventListener)this.listeners.get(var2);
         var3.connectionClosed(var1);
      }

      this.handleConn = null;
   }

   public int getTransactionTimeout() {
      this.debugCodeCall("getTransactionTimeout");
      return 0;
   }

   public boolean setTransactionTimeout(int var1) {
      this.debugCodeCall("setTransactionTimeout", (long)var1);
      return false;
   }

   public boolean isSameRM(XAResource var1) {
      this.debugCode("isSameRM(xares)");
      return var1 == this;
   }

   public Xid[] recover(int var1) throws XAException {
      this.debugCodeCall("recover", quoteFlags(var1));
      this.checkOpen();

      try {
         Statement var2 = this.physicalConn.createStatement();
         Throwable var20 = null;

         try {
            ResultSet var4 = var2.executeQuery("SELECT * FROM INFORMATION_SCHEMA.IN_DOUBT ORDER BY TRANSACTION_NAME");
            ArrayList var5 = Utils.newSmallArrayList();

            while(var4.next()) {
               String var6 = var4.getString("TRANSACTION_NAME");
               int var7 = getNextId(15);
               JdbcXid var8 = new JdbcXid(this.factory, var7, var6);
               var5.add(var8);
            }

            var4.close();
            Xid[] var21 = (Xid[])var5.toArray(new Xid[0]);
            if (!var5.isEmpty()) {
               this.prepared = true;
            }

            Xid[] var22 = var21;
            return var22;
         } catch (Throwable var17) {
            var20 = var17;
            throw var17;
         } finally {
            if (var2 != null) {
               if (var20 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var16) {
                     var20.addSuppressed(var16);
                  }
               } else {
                  var2.close();
               }
            }

         }
      } catch (SQLException var19) {
         XAException var3 = new XAException(-3);
         var3.initCause(var19);
         throw var3;
      }
   }

   public int prepare(Xid var1) throws XAException {
      if (this.isDebugEnabled()) {
         this.debugCode("prepare(" + quoteXid(var1) + ')');
      }

      this.checkOpen();
      if (!this.currentTransaction.equals(var1)) {
         throw new XAException(-5);
      } else {
         try {
            Statement var2 = this.physicalConn.createStatement();
            Throwable var3 = null;

            try {
               var2.execute(JdbcXid.toString(new StringBuilder("PREPARE COMMIT \""), var1).append('"').toString());
               this.prepared = true;
            } catch (Throwable var13) {
               var3 = var13;
               throw var13;
            } finally {
               if (var2 != null) {
                  if (var3 != null) {
                     try {
                        var2.close();
                     } catch (Throwable var12) {
                        var3.addSuppressed(var12);
                     }
                  } else {
                     var2.close();
                  }
               }

            }

            return 0;
         } catch (SQLException var15) {
            throw convertException(var15);
         }
      }
   }

   public void forget(Xid var1) {
      if (this.isDebugEnabled()) {
         this.debugCode("forget(" + quoteXid(var1) + ')');
      }

      this.prepared = false;
   }

   public void rollback(Xid var1) throws XAException {
      if (this.isDebugEnabled()) {
         this.debugCode("rollback(" + quoteXid(var1) + ')');
      }

      try {
         if (this.prepared) {
            Statement var2 = this.physicalConn.createStatement();
            Throwable var3 = null;

            try {
               var2.execute(JdbcXid.toString(new StringBuilder("ROLLBACK TRANSACTION \""), var1).append('"').toString());
            } catch (Throwable var13) {
               var3 = var13;
               throw var13;
            } finally {
               if (var2 != null) {
                  if (var3 != null) {
                     try {
                        var2.close();
                     } catch (Throwable var12) {
                        var3.addSuppressed(var12);
                     }
                  } else {
                     var2.close();
                  }
               }

            }

            this.prepared = false;
         } else {
            this.physicalConn.rollback();
         }

         this.physicalConn.setAutoCommit(true);
      } catch (SQLException var15) {
         throw convertException(var15);
      }

      this.currentTransaction = null;
   }

   public void end(Xid var1, int var2) throws XAException {
      if (this.isDebugEnabled()) {
         this.debugCode("end(" + quoteXid(var1) + ", " + quoteFlags(var2) + ')');
      }

      if (var2 != 33554432) {
         if (!this.currentTransaction.equals(var1)) {
            throw new XAException(-9);
         } else {
            this.prepared = false;
         }
      }
   }

   public void start(Xid var1, int var2) throws XAException {
      if (this.isDebugEnabled()) {
         this.debugCode("start(" + quoteXid(var1) + ", " + quoteFlags(var2) + ')');
      }

      if (var2 != 134217728) {
         if (var2 == 2097152) {
            if (this.currentTransaction != null && !this.currentTransaction.equals(var1)) {
               throw new XAException(-3);
            }
         } else if (this.currentTransaction != null) {
            throw new XAException(-4);
         }

         try {
            this.physicalConn.setAutoCommit(false);
         } catch (SQLException var4) {
            throw convertException(var4);
         }

         this.currentTransaction = var1;
         this.prepared = false;
      }
   }

   public void commit(Xid var1, boolean var2) throws XAException {
      if (this.isDebugEnabled()) {
         this.debugCode("commit(" + quoteXid(var1) + ", " + var2 + ')');
      }

      try {
         if (var2) {
            this.physicalConn.commit();
         } else {
            Statement var3 = this.physicalConn.createStatement();
            Throwable var4 = null;

            try {
               var3.execute(JdbcXid.toString(new StringBuilder("COMMIT TRANSACTION \""), var1).append('"').toString());
               this.prepared = false;
            } catch (Throwable var14) {
               var4 = var14;
               throw var14;
            } finally {
               if (var3 != null) {
                  if (var4 != null) {
                     try {
                        var3.close();
                     } catch (Throwable var13) {
                        var4.addSuppressed(var13);
                     }
                  } else {
                     var3.close();
                  }
               }

            }
         }

         this.physicalConn.setAutoCommit(true);
      } catch (SQLException var16) {
         throw convertException(var16);
      }

      this.currentTransaction = null;
   }

   public void addStatementEventListener(StatementEventListener var1) {
      throw new UnsupportedOperationException();
   }

   public void removeStatementEventListener(StatementEventListener var1) {
      throw new UnsupportedOperationException();
   }

   public String toString() {
      return this.getTraceObjectName() + ": " + this.physicalConn;
   }

   private static XAException convertException(SQLException var0) {
      XAException var1 = new XAException(var0.getMessage());
      var1.initCause(var0);
      return var1;
   }

   private static String quoteXid(Xid var0) {
      return JdbcXid.toString(new StringBuilder(), var0).toString().replace('-', '$');
   }

   private static String quoteFlags(int var0) {
      StringBuilder var1 = new StringBuilder();
      if ((var0 & 8388608) != 0) {
         var1.append("|XAResource.TMENDRSCAN");
      }

      if ((var0 & 536870912) != 0) {
         var1.append("|XAResource.TMFAIL");
      }

      if ((var0 & 2097152) != 0) {
         var1.append("|XAResource.TMJOIN");
      }

      if ((var0 & 1073741824) != 0) {
         var1.append("|XAResource.TMONEPHASE");
      }

      if ((var0 & 134217728) != 0) {
         var1.append("|XAResource.TMRESUME");
      }

      if ((var0 & 16777216) != 0) {
         var1.append("|XAResource.TMSTARTRSCAN");
      }

      if ((var0 & 67108864) != 0) {
         var1.append("|XAResource.TMSUCCESS");
      }

      if ((var0 & 33554432) != 0) {
         var1.append("|XAResource.TMSUSPEND");
      }

      if ((var0 & 3) != 0) {
         var1.append("|XAResource.XA_RDONLY");
      }

      if (var1.length() == 0) {
         var1.append("|XAResource.TMNOFLAGS");
      }

      return var1.substring(1);
   }

   private void checkOpen() throws XAException {
      if (this.physicalConn == null) {
         throw new XAException(-3);
      }
   }

   final class PooledJdbcConnection extends JdbcConnection {
      private boolean isClosed;

      public PooledJdbcConnection(JdbcConnection var2) {
         super(var2);
      }

      public synchronized void close() throws SQLException {
         if (!this.isClosed) {
            try {
               this.rollback();
               this.setAutoCommit(true);
            } catch (SQLException var2) {
            }

            JdbcXAConnection.this.closedHandle();
            this.isClosed = true;
         }

      }

      public synchronized boolean isClosed() throws SQLException {
         return this.isClosed || super.isClosed();
      }

      protected synchronized void checkClosed() {
         if (this.isClosed) {
            throw DbException.get(90007);
         } else {
            super.checkClosed();
         }
      }
   }
}
