package com.mysql.cj.jdbc;

import com.mysql.cj.Messages;
import com.mysql.cj.MysqlType;
import com.mysql.cj.Session;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.jdbc.exceptions.SQLError;
import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
import com.mysql.cj.jdbc.result.ResultSetMetaData;
import com.mysql.cj.result.Field;
import java.sql.ParameterMetaData;
import java.sql.SQLException;

public class MysqlParameterMetadata implements ParameterMetaData {
   boolean returnSimpleMetadata = false;
   ResultSetMetaData metadata = null;
   int parameterCount = 0;
   private ExceptionInterceptor exceptionInterceptor;

   public MysqlParameterMetadata(Session session, Field[] fieldInfo, int parameterCount, ExceptionInterceptor exceptionInterceptor) {
      this.metadata = new ResultSetMetaData(session, fieldInfo, false, true, exceptionInterceptor);
      this.parameterCount = parameterCount;
      this.exceptionInterceptor = exceptionInterceptor;
   }

   MysqlParameterMetadata(int count) {
      this.parameterCount = count;
      this.returnSimpleMetadata = true;
   }

   public int getParameterCount() throws SQLException {
      try {
         return this.parameterCount;
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.exceptionInterceptor);
      }
   }

   public int isNullable(int arg0) throws SQLException {
      try {
         this.checkAvailable();
         return this.metadata.isNullable(arg0);
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.exceptionInterceptor);
      }
   }

   private void checkAvailable() throws SQLException {
      if (this.metadata == null || this.metadata.getFields() == null) {
         throw SQLError.createSQLException(Messages.getString("MysqlParameterMetadata.0"), "S1C00", this.exceptionInterceptor);
      }
   }

   public boolean isSigned(int arg0) throws SQLException {
      try {
         if (this.returnSimpleMetadata) {
            this.checkBounds(arg0);
            return false;
         } else {
            this.checkAvailable();
            return this.metadata.isSigned(arg0);
         }
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.exceptionInterceptor);
      }
   }

   public int getPrecision(int arg0) throws SQLException {
      try {
         if (this.returnSimpleMetadata) {
            this.checkBounds(arg0);
            return 0;
         } else {
            this.checkAvailable();
            return this.metadata.getPrecision(arg0);
         }
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.exceptionInterceptor);
      }
   }

   public int getScale(int arg0) throws SQLException {
      try {
         if (this.returnSimpleMetadata) {
            this.checkBounds(arg0);
            return 0;
         } else {
            this.checkAvailable();
            return this.metadata.getScale(arg0);
         }
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.exceptionInterceptor);
      }
   }

   public int getParameterType(int arg0) throws SQLException {
      try {
         if (this.returnSimpleMetadata) {
            this.checkBounds(arg0);
            return MysqlType.VARCHAR.getJdbcType();
         } else {
            this.checkAvailable();
            return this.metadata.getColumnType(arg0);
         }
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.exceptionInterceptor);
      }
   }

   public String getParameterTypeName(int arg0) throws SQLException {
      try {
         if (this.returnSimpleMetadata) {
            this.checkBounds(arg0);
            return MysqlType.VARCHAR.getName();
         } else {
            this.checkAvailable();
            return this.metadata.getColumnTypeName(arg0);
         }
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.exceptionInterceptor);
      }
   }

   public String getParameterClassName(int arg0) throws SQLException {
      try {
         if (this.returnSimpleMetadata) {
            this.checkBounds(arg0);
            return "java.lang.String";
         } else {
            this.checkAvailable();
            return this.metadata.getColumnClassName(arg0);
         }
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.exceptionInterceptor);
      }
   }

   public int getParameterMode(int arg0) throws SQLException {
      try {
         return 1;
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.exceptionInterceptor);
      }
   }

   private void checkBounds(int paramNumber) throws SQLException {
      if (paramNumber < 1) {
         throw SQLError.createSQLException(Messages.getString("MysqlParameterMetadata.1", new Object[]{paramNumber}), "S1009", this.exceptionInterceptor);
      } else if (paramNumber > this.parameterCount) {
         throw SQLError.createSQLException(Messages.getString("MysqlParameterMetadata.2", new Object[]{paramNumber, this.parameterCount}), "S1009", this.exceptionInterceptor);
      }
   }

   public boolean isWrapperFor(Class<?> iface) throws SQLException {
      try {
         return iface.isInstance(this);
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.exceptionInterceptor);
      }
   }

   public <T> T unwrap(Class<T> iface) throws SQLException {
      try {
         try {
            return iface.cast(this);
         } catch (ClassCastException var4) {
            throw SQLError.createSQLException(Messages.getString("Common.UnableToUnwrap", new Object[]{iface.toString()}), "S1009", this.exceptionInterceptor);
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.exceptionInterceptor);
      }
   }
}
