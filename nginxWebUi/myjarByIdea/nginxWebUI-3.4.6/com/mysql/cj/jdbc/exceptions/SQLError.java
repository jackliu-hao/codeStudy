package com.mysql.cj.jdbc.exceptions;

import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.jdbc.JdbcConnection;
import com.mysql.cj.protocol.PacketReceivedTimeHolder;
import com.mysql.cj.protocol.PacketSentTimeHolder;
import com.mysql.cj.util.Util;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.SQLSyntaxErrorException;
import java.sql.SQLTransientConnectionException;

public class SQLError {
   public static SQLException createSQLException(String message, String sqlState, ExceptionInterceptor interceptor) {
      return createSQLException(message, sqlState, 0, interceptor);
   }

   public static SQLException createSQLException(String message, ExceptionInterceptor interceptor) {
      SQLException sqlEx = new SQLException(message);
      return runThroughExceptionInterceptor(interceptor, sqlEx);
   }

   public static SQLException createSQLException(String message, String sqlState, Throwable cause, ExceptionInterceptor interceptor) {
      SQLException sqlEx = createSQLException(message, sqlState, (ExceptionInterceptor)null);
      if (sqlEx.getCause() == null && cause != null) {
         try {
            sqlEx.initCause(cause);
         } catch (Throwable var6) {
         }
      }

      return runThroughExceptionInterceptor(interceptor, sqlEx);
   }

   public static SQLException createSQLException(String message, String sqlState, int vendorErrorCode, ExceptionInterceptor interceptor) {
      return createSQLException(message, sqlState, vendorErrorCode, false, interceptor);
   }

   public static SQLException createSQLException(String message, String sqlState, int vendorErrorCode, Throwable cause, ExceptionInterceptor interceptor) {
      return createSQLException(message, sqlState, vendorErrorCode, false, cause, interceptor);
   }

   public static SQLException createSQLException(String message, String sqlState, int vendorErrorCode, boolean isTransient, ExceptionInterceptor interceptor) {
      return createSQLException(message, sqlState, vendorErrorCode, isTransient, (Throwable)null, interceptor);
   }

   public static SQLException createSQLException(String message, String sqlState, int vendorErrorCode, boolean isTransient, Throwable cause, ExceptionInterceptor interceptor) {
      try {
         SQLException sqlEx = null;
         if (sqlState != null) {
            if (sqlState.startsWith("08")) {
               if (isTransient) {
                  sqlEx = new SQLTransientConnectionException(message, sqlState, vendorErrorCode);
               } else {
                  sqlEx = new SQLNonTransientConnectionException(message, sqlState, vendorErrorCode);
               }
            } else if (sqlState.startsWith("22")) {
               sqlEx = new SQLDataException(message, sqlState, vendorErrorCode);
            } else if (sqlState.startsWith("23")) {
               sqlEx = new SQLIntegrityConstraintViolationException(message, sqlState, vendorErrorCode);
            } else if (sqlState.startsWith("42")) {
               sqlEx = new SQLSyntaxErrorException(message, sqlState, vendorErrorCode);
            } else if (sqlState.startsWith("40")) {
               sqlEx = new MySQLTransactionRollbackException(message, sqlState, vendorErrorCode);
            } else if (sqlState.startsWith("70100")) {
               sqlEx = new MySQLQueryInterruptedException(message, sqlState, vendorErrorCode);
            } else {
               sqlEx = new SQLException(message, sqlState, vendorErrorCode);
            }
         } else {
            sqlEx = new SQLException(message, sqlState, vendorErrorCode);
         }

         if (cause != null) {
            try {
               ((SQLException)sqlEx).initCause(cause);
            } catch (Throwable var8) {
            }
         }

         return runThroughExceptionInterceptor(interceptor, (SQLException)sqlEx);
      } catch (Exception var9) {
         SQLException unexpectedEx = new SQLException("Unable to create correct SQLException class instance, error class/codes may be incorrect. Reason: " + Util.stackTraceToString(var9), "S1000");
         return runThroughExceptionInterceptor(interceptor, unexpectedEx);
      }
   }

   public static SQLException createCommunicationsException(JdbcConnection conn, PacketSentTimeHolder packetSentTimeHolder, PacketReceivedTimeHolder packetReceivedTimeHolder, Exception underlyingException, ExceptionInterceptor interceptor) {
      SQLException exToReturn = new CommunicationsException(conn, packetSentTimeHolder, packetReceivedTimeHolder, underlyingException);
      if (underlyingException != null) {
         try {
            exToReturn.initCause(underlyingException);
         } catch (Throwable var7) {
         }
      }

      return runThroughExceptionInterceptor(interceptor, exToReturn);
   }

   public static SQLException createCommunicationsException(String message, Throwable underlyingException, ExceptionInterceptor interceptor) {
      SQLException exToReturn = null;
      exToReturn = new CommunicationsException(message, underlyingException);
      if (underlyingException != null) {
         try {
            exToReturn.initCause(underlyingException);
         } catch (Throwable var5) {
         }
      }

      return runThroughExceptionInterceptor(interceptor, exToReturn);
   }

   private static SQLException runThroughExceptionInterceptor(ExceptionInterceptor exInterceptor, SQLException sqlEx) {
      if (exInterceptor != null) {
         SQLException interceptedEx = (SQLException)exInterceptor.interceptException(sqlEx);
         if (interceptedEx != null) {
            return interceptedEx;
         }
      }

      return sqlEx;
   }

   public static SQLException createBatchUpdateException(SQLException underlyingEx, long[] updateCounts, ExceptionInterceptor interceptor) throws SQLException {
      SQLException newEx = (SQLException)Util.getInstance("java.sql.BatchUpdateException", new Class[]{String.class, String.class, Integer.TYPE, long[].class, Throwable.class}, new Object[]{underlyingEx.getMessage(), underlyingEx.getSQLState(), underlyingEx.getErrorCode(), updateCounts, underlyingEx}, interceptor);
      return runThroughExceptionInterceptor(interceptor, newEx);
   }

   public static SQLException createSQLFeatureNotSupportedException() {
      return new SQLFeatureNotSupportedException();
   }

   public static SQLException createSQLFeatureNotSupportedException(String message, String sqlState, ExceptionInterceptor interceptor) throws SQLException {
      SQLException newEx = new SQLFeatureNotSupportedException(message, sqlState);
      return runThroughExceptionInterceptor(interceptor, newEx);
   }
}
