package com.mysql.cj.jdbc;

import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.util.Util;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.Map;

abstract class WrapperBase {
   protected MysqlPooledConnection pooledConnection;
   protected Map<Class<?>, Object> unwrappedInterfaces = null;
   protected ExceptionInterceptor exceptionInterceptor;

   protected void checkAndFireConnectionError(SQLException sqlEx) throws SQLException {
      if (this.pooledConnection != null && "08S01".equals(sqlEx.getSQLState())) {
         this.pooledConnection.callConnectionEventListeners(1, sqlEx);
      }

      throw sqlEx;
   }

   protected WrapperBase(MysqlPooledConnection pooledConnection) {
      this.pooledConnection = pooledConnection;
      this.exceptionInterceptor = this.pooledConnection.getExceptionInterceptor();
   }

   protected class ConnectionErrorFiringInvocationHandler implements InvocationHandler {
      Object invokeOn = null;

      public ConnectionErrorFiringInvocationHandler(Object toInvokeOn) {
         this.invokeOn = toInvokeOn;
      }

      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         if ("equals".equals(method.getName())) {
            return args[0].equals(this);
         } else {
            Object result = null;

            try {
               result = method.invoke(this.invokeOn, args);
               if (result != null) {
                  result = this.proxyIfInterfaceIsJdbc(result, result.getClass());
               }
            } catch (InvocationTargetException var6) {
               if (!(var6.getTargetException() instanceof SQLException)) {
                  throw var6;
               }

               WrapperBase.this.checkAndFireConnectionError((SQLException)var6.getTargetException());
            }

            return result;
         }
      }

      private Object proxyIfInterfaceIsJdbc(Object toProxy, Class<?> clazz) {
         Class<?>[] interfaces = clazz.getInterfaces();
         int var5 = interfaces.length;
         byte var6 = 0;
         if (var6 < var5) {
            Class<?> iclass = interfaces[var6];
            String packageName = Util.getPackageName(iclass);
            return !"java.sql".equals(packageName) && !"javax.sql".equals(packageName) ? this.proxyIfInterfaceIsJdbc(toProxy, iclass) : Proxy.newProxyInstance(toProxy.getClass().getClassLoader(), interfaces, WrapperBase.this.new ConnectionErrorFiringInvocationHandler(toProxy));
         } else {
            return toProxy;
         }
      }
   }
}
