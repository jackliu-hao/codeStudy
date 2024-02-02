package org.h2.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.h2.message.DbException;
import org.h2.message.Trace;
import org.h2.message.TraceObject;
import org.h2.result.ResultInterface;
import org.h2.util.MathUtils;
import org.h2.value.DataType;
import org.h2.value.ValueToObjectConverter;

public final class JdbcResultSetMetaData extends TraceObject implements ResultSetMetaData {
   private final String catalog;
   private final JdbcResultSet rs;
   private final JdbcPreparedStatement prep;
   private final ResultInterface result;
   private final int columnCount;

   JdbcResultSetMetaData(JdbcResultSet var1, JdbcPreparedStatement var2, ResultInterface var3, String var4, Trace var5, int var6) {
      this.setTrace(var5, 5, var6);
      this.catalog = var4;
      this.rs = var1;
      this.prep = var2;
      this.result = var3;
      this.columnCount = var3.getVisibleColumnCount();
   }

   public int getColumnCount() throws SQLException {
      try {
         this.debugCodeCall("getColumnCount");
         this.checkClosed();
         return this.columnCount;
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public String getColumnLabel(int var1) throws SQLException {
      try {
         return this.result.getAlias(this.getColumn("getColumnLabel", var1));
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public String getColumnName(int var1) throws SQLException {
      try {
         return this.result.getColumnName(this.getColumn("getColumnName", var1));
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public int getColumnType(int var1) throws SQLException {
      try {
         return DataType.convertTypeToSQLType(this.result.getColumnType(this.getColumn("getColumnType", var1)));
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public String getColumnTypeName(int var1) throws SQLException {
      try {
         return this.result.getColumnType(this.getColumn("getColumnTypeName", var1)).getDeclaredTypeName();
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public String getSchemaName(int var1) throws SQLException {
      try {
         String var2 = this.result.getSchemaName(this.getColumn("getSchemaName", var1));
         return var2 == null ? "" : var2;
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public String getTableName(int var1) throws SQLException {
      try {
         String var2 = this.result.getTableName(this.getColumn("getTableName", var1));
         return var2 == null ? "" : var2;
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public String getCatalogName(int var1) throws SQLException {
      try {
         this.getColumn("getCatalogName", var1);
         return this.catalog == null ? "" : this.catalog;
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public boolean isAutoIncrement(int var1) throws SQLException {
      try {
         return this.result.isIdentity(this.getColumn("isAutoIncrement", var1));
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public boolean isCaseSensitive(int var1) throws SQLException {
      try {
         this.getColumn("isCaseSensitive", var1);
         return true;
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public boolean isSearchable(int var1) throws SQLException {
      try {
         this.getColumn("isSearchable", var1);
         return true;
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public boolean isCurrency(int var1) throws SQLException {
      try {
         this.getColumn("isCurrency", var1);
         return false;
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public int isNullable(int var1) throws SQLException {
      try {
         return this.result.getNullable(this.getColumn("isNullable", var1));
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public boolean isSigned(int var1) throws SQLException {
      try {
         return DataType.isNumericType(this.result.getColumnType(this.getColumn("isSigned", var1)).getValueType());
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public boolean isReadOnly(int var1) throws SQLException {
      try {
         this.getColumn("isReadOnly", var1);
         return false;
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public boolean isWritable(int var1) throws SQLException {
      try {
         this.getColumn("isWritable", var1);
         return true;
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public boolean isDefinitelyWritable(int var1) throws SQLException {
      try {
         this.getColumn("isDefinitelyWritable", var1);
         return false;
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public String getColumnClassName(int var1) throws SQLException {
      try {
         int var2 = this.result.getColumnType(this.getColumn("getColumnClassName", var1)).getValueType();
         return ValueToObjectConverter.getDefaultClass(var2, true).getName();
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public int getPrecision(int var1) throws SQLException {
      try {
         return MathUtils.convertLongToInt(this.result.getColumnType(this.getColumn("getPrecision", var1)).getPrecision());
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public int getScale(int var1) throws SQLException {
      try {
         return this.result.getColumnType(this.getColumn("getScale", var1)).getScale();
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public int getColumnDisplaySize(int var1) throws SQLException {
      try {
         return this.result.getColumnType(this.getColumn("getColumnDisplaySize", var1)).getDisplaySize();
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   private void checkClosed() {
      if (this.rs != null) {
         this.rs.checkClosed();
      }

      if (this.prep != null) {
         this.prep.checkClosed();
      }

   }

   private int getColumn(String var1, int var2) {
      this.debugCodeCall(var1, (long)var2);
      this.checkClosed();
      if (var2 >= 1 && var2 <= this.columnCount) {
         return var2 - 1;
      } else {
         throw DbException.getInvalidValueException("columnIndex", var2);
      }
   }

   public <T> T unwrap(Class<T> var1) throws SQLException {
      try {
         if (this.isWrapperFor(var1)) {
            return this;
         } else {
            throw DbException.getInvalidValueException("iface", var1);
         }
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public boolean isWrapperFor(Class<?> var1) throws SQLException {
      return var1 != null && var1.isAssignableFrom(this.getClass());
   }

   public String toString() {
      return this.getTraceObjectName() + ": columns=" + this.columnCount;
   }
}
