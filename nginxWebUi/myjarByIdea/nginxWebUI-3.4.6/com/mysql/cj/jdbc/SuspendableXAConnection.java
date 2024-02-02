package com.mysql.cj.jdbc;

import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.sql.XAConnection;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

public class SuspendableXAConnection extends MysqlPooledConnection implements XAConnection, XAResource {
   private static final Map<Xid, XAConnection> XIDS_TO_PHYSICAL_CONNECTIONS = new HashMap();
   private Xid currentXid;
   private XAConnection currentXAConnection;
   private XAResource currentXAResource;
   private JdbcConnection underlyingConnection;

   protected static SuspendableXAConnection getInstance(JdbcConnection mysqlConnection) throws SQLException {
      return new SuspendableXAConnection(mysqlConnection);
   }

   public SuspendableXAConnection(JdbcConnection connection) {
      super(connection);
      this.underlyingConnection = connection;
   }

   private static synchronized XAConnection findConnectionForXid(JdbcConnection connectionToWrap, Xid xid) throws SQLException {
      XAConnection conn = (XAConnection)XIDS_TO_PHYSICAL_CONNECTIONS.get(xid);
      if (conn == null) {
         conn = new MysqlXAConnection(connectionToWrap, (Boolean)connectionToWrap.getPropertySet().getBooleanProperty(PropertyKey.logXaCommands).getValue());
         XIDS_TO_PHYSICAL_CONNECTIONS.put(xid, conn);
      }

      return (XAConnection)conn;
   }

   private static synchronized void removeXAConnectionMapping(Xid xid) {
      XIDS_TO_PHYSICAL_CONNECTIONS.remove(xid);
   }

   private synchronized void switchToXid(Xid xid) throws XAException {
      if (xid == null) {
         throw new XAException();
      } else {
         try {
            if (!xid.equals(this.currentXid)) {
               XAConnection toSwitchTo = findConnectionForXid(this.underlyingConnection, xid);
               this.currentXAConnection = toSwitchTo;
               this.currentXid = xid;
               this.currentXAResource = toSwitchTo.getXAResource();
            }

         } catch (SQLException var3) {
            throw new XAException();
         }
      }
   }

   public XAResource getXAResource() throws SQLException {
      try {
         return this;
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2);
      }
   }

   public void commit(Xid xid, boolean arg1) throws XAException {
      this.switchToXid(xid);
      this.currentXAResource.commit(xid, arg1);
      removeXAConnectionMapping(xid);
   }

   public void end(Xid xid, int arg1) throws XAException {
      this.switchToXid(xid);
      this.currentXAResource.end(xid, arg1);
   }

   public void forget(Xid xid) throws XAException {
      this.switchToXid(xid);
      this.currentXAResource.forget(xid);
      removeXAConnectionMapping(xid);
   }

   public int getTransactionTimeout() throws XAException {
      return 0;
   }

   public boolean isSameRM(XAResource xaRes) throws XAException {
      return xaRes == this;
   }

   public int prepare(Xid xid) throws XAException {
      this.switchToXid(xid);
      return this.currentXAResource.prepare(xid);
   }

   public Xid[] recover(int flag) throws XAException {
      return MysqlXAConnection.recover(this.underlyingConnection, flag);
   }

   public void rollback(Xid xid) throws XAException {
      this.switchToXid(xid);
      this.currentXAResource.rollback(xid);
      removeXAConnectionMapping(xid);
   }

   public boolean setTransactionTimeout(int arg0) throws XAException {
      return false;
   }

   public void start(Xid xid, int arg1) throws XAException {
      this.switchToXid(xid);
      if (arg1 != 2097152) {
         this.currentXAResource.start(xid, arg1);
      } else {
         this.currentXAResource.start(xid, 134217728);
      }
   }

   public synchronized Connection getConnection() throws SQLException {
      try {
         return this.currentXAConnection == null ? this.getConnection(false, true) : this.currentXAConnection.getConnection();
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2);
      }
   }

   public void close() throws SQLException {
      try {
         if (this.currentXAConnection == null) {
            super.close();
         } else {
            removeXAConnectionMapping(this.currentXid);
            this.currentXAConnection.close();
         }

      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2);
      }
   }
}
