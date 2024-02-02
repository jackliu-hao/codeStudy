package com.mysql.cj.jdbc.ha;

import com.mysql.cj.conf.ConnectionUrl;
import com.mysql.cj.conf.HostInfo;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.exceptions.CJCommunicationsException;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.jdbc.ConnectionImpl;
import com.mysql.cj.jdbc.JdbcConnection;
import com.mysql.cj.jdbc.JdbcPropertySetImpl;
import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import com.mysql.cj.jdbc.exceptions.SQLError;
import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
import com.mysql.cj.util.Util;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.concurrent.Executor;

public class FailoverConnectionProxy extends MultiHostConnectionProxy {
   private static final String METHOD_SET_READ_ONLY = "setReadOnly";
   private static final String METHOD_SET_AUTO_COMMIT = "setAutoCommit";
   private static final String METHOD_COMMIT = "commit";
   private static final String METHOD_ROLLBACK = "rollback";
   private static final int NO_CONNECTION_INDEX = -1;
   private static final int DEFAULT_PRIMARY_HOST_INDEX = 0;
   private int secondsBeforeRetryPrimaryHost;
   private long queriesBeforeRetryPrimaryHost;
   private boolean failoverReadOnly;
   private int retriesAllDown;
   private int currentHostIndex = -1;
   private int primaryHostIndex = 0;
   private Boolean explicitlyReadOnly = null;
   private boolean explicitlyAutoCommit = true;
   private boolean enableFallBackToPrimaryHost = true;
   private long primaryHostFailTimeMillis = 0L;
   private long queriesIssuedSinceFailover = 0L;

   public static JdbcConnection createProxyInstance(ConnectionUrl connectionUrl) throws SQLException {
      FailoverConnectionProxy connProxy = new FailoverConnectionProxy(connectionUrl);
      return (JdbcConnection)Proxy.newProxyInstance(JdbcConnection.class.getClassLoader(), new Class[]{JdbcConnection.class}, connProxy);
   }

   private FailoverConnectionProxy(ConnectionUrl connectionUrl) throws SQLException {
      super(connectionUrl);
      JdbcPropertySetImpl connProps = new JdbcPropertySetImpl();
      connProps.initializeProperties(connectionUrl.getConnectionArgumentsAsProperties());
      this.secondsBeforeRetryPrimaryHost = (Integer)connProps.getIntegerProperty(PropertyKey.secondsBeforeRetrySource).getValue();
      this.queriesBeforeRetryPrimaryHost = (long)(Integer)connProps.getIntegerProperty(PropertyKey.queriesBeforeRetrySource).getValue();
      this.failoverReadOnly = (Boolean)connProps.getBooleanProperty(PropertyKey.failOverReadOnly).getValue();
      this.retriesAllDown = (Integer)connProps.getIntegerProperty(PropertyKey.retriesAllDown).getValue();
      this.enableFallBackToPrimaryHost = this.secondsBeforeRetryPrimaryHost > 0 || this.queriesBeforeRetryPrimaryHost > 0L;
      this.pickNewConnection();
      this.explicitlyAutoCommit = this.currentConnection.getAutoCommit();
   }

   MultiHostConnectionProxy.JdbcInterfaceProxy getNewJdbcInterfaceProxy(Object toProxy) {
      return new FailoverJdbcInterfaceProxy(toProxy);
   }

   boolean shouldExceptionTriggerConnectionSwitch(Throwable t) {
      String sqlState = null;
      if (!(t instanceof CommunicationsException) && !(t instanceof CJCommunicationsException)) {
         if (t instanceof SQLException) {
            sqlState = ((SQLException)t).getSQLState();
         } else if (t instanceof CJException) {
            sqlState = ((CJException)t).getSQLState();
         }

         return sqlState != null && sqlState.startsWith("08");
      } else {
         return true;
      }
   }

   boolean isSourceConnection() {
      return this.connectedToPrimaryHost();
   }

   synchronized void pickNewConnection() throws SQLException {
      if (!this.isClosed || !this.closedExplicitly) {
         if (this.isConnected() && !this.readyToFallBackToPrimaryHost()) {
            this.failOver();
         } else {
            try {
               this.connectTo(this.primaryHostIndex);
            } catch (SQLException var2) {
               this.resetAutoFallBackCounters();
               this.failOver(this.primaryHostIndex);
            }
         }

      }
   }

   synchronized ConnectionImpl createConnectionForHostIndex(int hostIndex) throws SQLException {
      return this.createConnectionForHost((HostInfo)this.hostsList.get(hostIndex));
   }

   private synchronized void connectTo(int hostIndex) throws SQLException {
      try {
         this.switchCurrentConnectionTo(hostIndex, this.createConnectionForHostIndex(hostIndex));
      } catch (SQLException var6) {
         SQLException e = var6;
         if (this.currentConnection != null) {
            StringBuilder msg = (new StringBuilder("Connection to ")).append(this.isPrimaryHostIndex(hostIndex) ? "primary" : "secondary").append(" host '").append(this.hostsList.get(hostIndex)).append("' failed");

            try {
               this.currentConnection.getSession().getLog().logWarn(msg.toString(), e);
            } catch (CJException var5) {
               throw SQLExceptionsMapping.translateException(var6, this.currentConnection.getExceptionInterceptor());
            }
         }

         throw var6;
      }
   }

   private synchronized void switchCurrentConnectionTo(int hostIndex, JdbcConnection connection) throws SQLException {
      this.invalidateCurrentConnection();
      boolean readOnly;
      if (this.isPrimaryHostIndex(hostIndex)) {
         readOnly = this.explicitlyReadOnly == null ? false : this.explicitlyReadOnly;
      } else if (this.failoverReadOnly) {
         readOnly = true;
      } else if (this.explicitlyReadOnly != null) {
         readOnly = this.explicitlyReadOnly;
      } else if (this.currentConnection != null) {
         readOnly = this.currentConnection.isReadOnly();
      } else {
         readOnly = false;
      }

      this.syncSessionState(this.currentConnection, connection, readOnly);
      this.currentConnection = connection;
      this.currentHostIndex = hostIndex;
   }

   private synchronized void failOver() throws SQLException {
      this.failOver(this.currentHostIndex);
   }

   private synchronized void failOver(int failedHostIdx) throws SQLException {
      int prevHostIndex = this.currentHostIndex;
      int nextHostIndex = this.nextHost(failedHostIdx, false);
      int firstHostIndexTried = nextHostIndex;
      SQLException lastExceptionCaught = null;
      int attempts = 0;
      boolean gotConnection = false;
      boolean firstConnOrPassedByPrimaryHost = prevHostIndex == -1 || this.isPrimaryHostIndex(prevHostIndex);

      do {
         try {
            firstConnOrPassedByPrimaryHost = firstConnOrPassedByPrimaryHost || this.isPrimaryHostIndex(nextHostIndex);
            this.connectTo(nextHostIndex);
            if (firstConnOrPassedByPrimaryHost && this.connectedToSecondaryHost()) {
               this.resetAutoFallBackCounters();
            }

            gotConnection = true;
         } catch (SQLException var13) {
            lastExceptionCaught = var13;
            if (!this.shouldExceptionTriggerConnectionSwitch(var13)) {
               throw var13;
            }

            int newNextHostIndex = this.nextHost(nextHostIndex, attempts > 0);
            if (newNextHostIndex == firstHostIndexTried && newNextHostIndex == (newNextHostIndex = this.nextHost(nextHostIndex, true))) {
               ++attempts;

               try {
                  Thread.sleep(250L);
               } catch (InterruptedException var12) {
               }
            }

            nextHostIndex = newNextHostIndex;
         }
      } while(attempts < this.retriesAllDown && !gotConnection);

      if (!gotConnection) {
         throw lastExceptionCaught;
      }
   }

   synchronized void fallBackToPrimaryIfAvailable() {
      JdbcConnection connection = null;

      try {
         connection = this.createConnectionForHostIndex(this.primaryHostIndex);
         this.switchCurrentConnectionTo(this.primaryHostIndex, connection);
      } catch (SQLException var5) {
         if (connection != null) {
            try {
               connection.close();
            } catch (SQLException var4) {
            }
         }

         this.resetAutoFallBackCounters();
      }

   }

   private int nextHost(int currHostIdx, boolean vouchForPrimaryHost) {
      int nextHostIdx = (currHostIdx + 1) % this.hostsList.size();
      if (this.isPrimaryHostIndex(nextHostIdx) && this.isConnected() && !vouchForPrimaryHost && this.enableFallBackToPrimaryHost && !this.readyToFallBackToPrimaryHost()) {
         nextHostIdx = this.nextHost(nextHostIdx, vouchForPrimaryHost);
      }

      return nextHostIdx;
   }

   synchronized void incrementQueriesIssuedSinceFailover() {
      ++this.queriesIssuedSinceFailover;
   }

   synchronized boolean readyToFallBackToPrimaryHost() {
      return this.enableFallBackToPrimaryHost && this.connectedToSecondaryHost() && (this.secondsBeforeRetryPrimaryHostIsMet() || this.queriesBeforeRetryPrimaryHostIsMet());
   }

   synchronized boolean isConnected() {
      return this.currentHostIndex != -1;
   }

   synchronized boolean isPrimaryHostIndex(int hostIndex) {
      return hostIndex == this.primaryHostIndex;
   }

   synchronized boolean connectedToPrimaryHost() {
      return this.isPrimaryHostIndex(this.currentHostIndex);
   }

   synchronized boolean connectedToSecondaryHost() {
      return this.currentHostIndex >= 0 && !this.isPrimaryHostIndex(this.currentHostIndex);
   }

   private synchronized boolean secondsBeforeRetryPrimaryHostIsMet() {
      return this.secondsBeforeRetryPrimaryHost > 0 && Util.secondsSinceMillis(this.primaryHostFailTimeMillis) >= (long)this.secondsBeforeRetryPrimaryHost;
   }

   private synchronized boolean queriesBeforeRetryPrimaryHostIsMet() {
      return this.queriesBeforeRetryPrimaryHost > 0L && this.queriesIssuedSinceFailover >= this.queriesBeforeRetryPrimaryHost;
   }

   private synchronized void resetAutoFallBackCounters() {
      this.primaryHostFailTimeMillis = System.currentTimeMillis();
      this.queriesIssuedSinceFailover = 0L;
   }

   synchronized void doClose() throws SQLException {
      this.currentConnection.close();
   }

   synchronized void doAbortInternal() throws SQLException {
      this.currentConnection.abortInternal();
   }

   synchronized void doAbort(Executor executor) throws SQLException {
      this.currentConnection.abort(executor);
   }

   public Object invokeMore(Object proxy, Method method, Object[] args) throws Throwable {
      String methodName = method.getName();
      if ("setReadOnly".equals(methodName)) {
         this.explicitlyReadOnly = (Boolean)args[0];
         if (this.failoverReadOnly && this.connectedToSecondaryHost()) {
            return null;
         }
      }

      if (this.isClosed && !this.allowedOnClosedConnection(method)) {
         if (!this.autoReconnect || this.closedExplicitly) {
            String reason = "No operations allowed after connection closed.";
            if (this.closedReason != null) {
               reason = reason + "  " + this.closedReason;
            }

            throw SQLError.createSQLException(reason, "08003", (ExceptionInterceptor)null);
         }

         this.currentHostIndex = -1;
         this.pickNewConnection();
         this.isClosed = false;
         this.closedReason = null;
      }

      Object result = null;

      try {
         result = method.invoke(this.thisAsConnection, args);
         result = this.proxyIfReturnTypeIsJdbcInterface(method.getReturnType(), result);
      } catch (InvocationTargetException var7) {
         this.dealWithInvocationException(var7);
      }

      if ("setAutoCommit".equals(methodName)) {
         this.explicitlyAutoCommit = (Boolean)args[0];
      }

      if ((this.explicitlyAutoCommit || "commit".equals(methodName) || "rollback".equals(methodName)) && this.readyToFallBackToPrimaryHost()) {
         this.fallBackToPrimaryIfAvailable();
      }

      return result;
   }

   class FailoverJdbcInterfaceProxy extends MultiHostConnectionProxy.JdbcInterfaceProxy {
      FailoverJdbcInterfaceProxy(Object toInvokeOn) {
         super(toInvokeOn);
      }

      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         String methodName = method.getName();
         boolean isExecute = methodName.startsWith("execute");
         if (FailoverConnectionProxy.this.connectedToSecondaryHost() && isExecute) {
            FailoverConnectionProxy.this.incrementQueriesIssuedSinceFailover();
         }

         Object result = super.invoke(proxy, method, args);
         if (FailoverConnectionProxy.this.explicitlyAutoCommit && isExecute && FailoverConnectionProxy.this.readyToFallBackToPrimaryHost()) {
            FailoverConnectionProxy.this.fallBackToPrimaryIfAvailable();
         }

         return result;
      }
   }
}
