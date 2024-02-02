package com.mysql.cj.jdbc.interceptors;

import com.mysql.cj.Messages;
import com.mysql.cj.MysqlConnection;
import com.mysql.cj.Query;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.interceptors.QueryInterceptor;
import com.mysql.cj.jdbc.result.ResultSetInternalMethods;
import com.mysql.cj.log.Log;
import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.protocol.ServerSession;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResultSetScannerInterceptor implements QueryInterceptor {
   public static final String PNAME_resultSetScannerRegex = "resultSetScannerRegex";
   protected Pattern regexP;

   public QueryInterceptor init(MysqlConnection conn, Properties props, Log log) {
      String regexFromUser = props.getProperty("resultSetScannerRegex");
      if (regexFromUser != null && regexFromUser.length() != 0) {
         try {
            this.regexP = Pattern.compile(regexFromUser);
            return this;
         } catch (Throwable var6) {
            throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("ResultSetScannerInterceptor.1"), var6);
         }
      } else {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("ResultSetScannerInterceptor.0"));
      }
   }

   public <T extends Resultset> T postProcess(Supplier<String> sql, Query interceptedQuery, final T originalResultSet, ServerSession serverSession) {
      return (Resultset)Proxy.newProxyInstance(originalResultSet.getClass().getClassLoader(), new Class[]{Resultset.class, ResultSetInternalMethods.class}, new InvocationHandler() {
         public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("equals".equals(method.getName())) {
               return args[0].equals(this);
            } else {
               Object invocationResult = method.invoke(originalResultSet, args);
               String methodName = method.getName();
               if (invocationResult != null && invocationResult instanceof String || "getString".equals(methodName) || "getObject".equals(methodName) || "getObjectStoredProc".equals(methodName)) {
                  Matcher matcher = ResultSetScannerInterceptor.this.regexP.matcher(invocationResult.toString());
                  if (matcher.matches()) {
                     throw new SQLException(Messages.getString("ResultSetScannerInterceptor.2"));
                  }
               }

               return invocationResult;
            }
         }
      });
   }

   public <T extends Resultset> T preProcess(Supplier<String> sql, Query interceptedQuery) {
      return null;
   }

   public boolean executeTopLevelOnly() {
      return false;
   }

   public void destroy() {
   }
}
