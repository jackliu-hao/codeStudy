package com.mysql.cj.jdbc;

import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.jdbc.exceptions.SQLError;
import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.PooledConnection;
import javax.sql.StatementEvent;
import javax.sql.StatementEventListener;

public class MysqlPooledConnection implements PooledConnection {
   public static final int CONNECTION_ERROR_EVENT = 1;
   public static final int CONNECTION_CLOSED_EVENT = 2;
   private Map<ConnectionEventListener, ConnectionEventListener> connectionEventListeners;
   private Connection logicalHandle = null;
   private JdbcConnection physicalConn;
   private ExceptionInterceptor exceptionInterceptor;
   private final Map<StatementEventListener, StatementEventListener> statementEventListeners = new HashMap();

   protected static MysqlPooledConnection getInstance(JdbcConnection connection) throws SQLException {
      return new MysqlPooledConnection(connection);
   }

   public MysqlPooledConnection(JdbcConnection connection) {
      this.physicalConn = connection;
      this.connectionEventListeners = new HashMap();
      this.exceptionInterceptor = this.physicalConn.getExceptionInterceptor();
   }

   public synchronized void addConnectionEventListener(ConnectionEventListener connectioneventlistener) {
      if (this.connectionEventListeners != null) {
         this.connectionEventListeners.put(connectioneventlistener, connectioneventlistener);
      }

   }

   public synchronized void removeConnectionEventListener(ConnectionEventListener connectioneventlistener) {
      if (this.connectionEventListeners != null) {
         this.connectionEventListeners.remove(connectioneventlistener);
      }

   }

   public synchronized Connection getConnection() throws SQLException {
      try {
         return this.getConnection(true, false);
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.exceptionInterceptor);
      }
   }

   protected synchronized Connection getConnection(boolean resetServerState, boolean forXa) throws SQLException {
      if (this.physicalConn == null) {
         SQLException sqlException = SQLError.createSQLException(Messages.getString("MysqlPooledConnection.0"), this.exceptionInterceptor);
         this.callConnectionEventListeners(1, sqlException);
         throw sqlException;
      } else {
         try {
            if (this.logicalHandle != null) {
               ((ConnectionWrapper)this.logicalHandle).close(false);
            }

            if (resetServerState) {
               this.physicalConn.resetServerState();
            }

            this.logicalHandle = ConnectionWrapper.getInstance(this, this.physicalConn, forXa);
         } catch (SQLException var4) {
            this.callConnectionEventListeners(1, var4);
            throw var4;
         }

         return this.logicalHandle;
      }
   }

   public synchronized void close() throws SQLException {
      try {
         if (this.physicalConn != null) {
            this.physicalConn.close();
            this.physicalConn = null;
         }

         if (this.connectionEventListeners != null) {
            this.connectionEventListeners.clear();
            this.connectionEventListeners = null;
         }

         this.statementEventListeners.clear();
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.exceptionInterceptor);
      }
   }

   protected synchronized void callConnectionEventListeners(int eventType, SQLException sqlException) {
      if (this.connectionEventListeners != null) {
         Iterator<Map.Entry<ConnectionEventListener, ConnectionEventListener>> iterator = this.connectionEventListeners.entrySet().iterator();
         ConnectionEvent connectionevent = new ConnectionEvent(this, sqlException);

         while(iterator.hasNext()) {
            ConnectionEventListener connectioneventlistener = (ConnectionEventListener)((Map.Entry)iterator.next()).getValue();
            if (eventType == 2) {
               connectioneventlistener.connectionClosed(connectionevent);
            } else if (eventType == 1) {
               connectioneventlistener.connectionErrorOccurred(connectionevent);
            }
         }

      }
   }

   protected ExceptionInterceptor getExceptionInterceptor() {
      return this.exceptionInterceptor;
   }

   public void addStatementEventListener(StatementEventListener listener) {
      synchronized(this.statementEventListeners) {
         this.statementEventListeners.put(listener, listener);
      }
   }

   public void removeStatementEventListener(StatementEventListener listener) {
      synchronized(this.statementEventListeners) {
         this.statementEventListeners.remove(listener);
      }
   }

   void fireStatementEvent(StatementEvent event) throws SQLException {
      synchronized(this.statementEventListeners) {
         Iterator var3 = this.statementEventListeners.keySet().iterator();

         while(var3.hasNext()) {
            StatementEventListener listener = (StatementEventListener)var3.next();
            listener.statementClosed(event);
         }

      }
   }
}
