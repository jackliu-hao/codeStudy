package com.mysql.cj.jdbc.ha;

import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.jdbc.exceptions.SQLError;
import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
import java.sql.SQLException;

public class LoadBalancedMySQLConnection extends MultiHostMySQLConnection implements LoadBalancedConnection {
   public LoadBalancedMySQLConnection(LoadBalancedConnectionProxy proxy) {
      super(proxy);
   }

   public LoadBalancedConnectionProxy getThisAsProxy() {
      return (LoadBalancedConnectionProxy)super.getThisAsProxy();
   }

   public void close() throws SQLException {
      try {
         this.getThisAsProxy().doClose();
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public void ping() throws SQLException {
      try {
         this.ping(true);
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public void ping(boolean allConnections) throws SQLException {
      try {
         if (allConnections) {
            this.getThisAsProxy().doPing();
         } else {
            this.getActiveMySQLConnection().ping();
         }

      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public boolean addHost(String host) throws SQLException {
      try {
         return this.getThisAsProxy().addHost(host);
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public void removeHost(String host) throws SQLException {
      try {
         this.getThisAsProxy().removeHost(host);
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public void removeHostWhenNotInUse(String host) throws SQLException {
      try {
         this.getThisAsProxy().removeHostWhenNotInUse(host);
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public boolean isWrapperFor(Class<?> iface) throws SQLException {
      try {
         return iface.isInstance(this);
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public <T> T unwrap(Class<T> iface) throws SQLException {
      try {
         try {
            return iface.cast(this);
         } catch (ClassCastException var4) {
            throw SQLError.createSQLException(Messages.getString("Common.UnableToUnwrap", new Object[]{iface.toString()}), "S1009", this.getExceptionInterceptor());
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }
}
