package org.h2.jdbc;

import java.sql.ParameterMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import org.h2.command.CommandInterface;
import org.h2.expression.ParameterInterface;
import org.h2.message.DbException;
import org.h2.message.Trace;
import org.h2.message.TraceObject;
import org.h2.util.MathUtils;
import org.h2.value.DataType;
import org.h2.value.TypeInfo;
import org.h2.value.ValueToObjectConverter;

public final class JdbcParameterMetaData extends TraceObject implements ParameterMetaData {
   private final JdbcPreparedStatement prep;
   private final int paramCount;
   private final ArrayList<? extends ParameterInterface> parameters;

   JdbcParameterMetaData(Trace var1, JdbcPreparedStatement var2, CommandInterface var3, int var4) {
      this.setTrace(var1, 11, var4);
      this.prep = var2;
      this.parameters = var3.getParameters();
      this.paramCount = this.parameters.size();
   }

   public int getParameterCount() throws SQLException {
      try {
         this.debugCodeCall("getParameterCount");
         this.checkClosed();
         return this.paramCount;
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public int getParameterMode(int var1) throws SQLException {
      try {
         this.debugCodeCall("getParameterMode", (long)var1);
         this.getParameter(var1);
         return 1;
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public int getParameterType(int var1) throws SQLException {
      try {
         this.debugCodeCall("getParameterType", (long)var1);
         TypeInfo var2 = this.getParameter(var1).getType();
         if (var2.getValueType() == -1) {
            var2 = TypeInfo.TYPE_VARCHAR;
         }

         return DataType.convertTypeToSQLType(var2);
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public int getPrecision(int var1) throws SQLException {
      try {
         this.debugCodeCall("getPrecision", (long)var1);
         TypeInfo var2 = this.getParameter(var1).getType();
         return var2.getValueType() == -1 ? 0 : MathUtils.convertLongToInt(var2.getPrecision());
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public int getScale(int var1) throws SQLException {
      try {
         this.debugCodeCall("getScale", (long)var1);
         TypeInfo var2 = this.getParameter(var1).getType();
         return var2.getValueType() == -1 ? 0 : var2.getScale();
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public int isNullable(int var1) throws SQLException {
      try {
         this.debugCodeCall("isNullable", (long)var1);
         return this.getParameter(var1).getNullable();
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public boolean isSigned(int var1) throws SQLException {
      try {
         this.debugCodeCall("isSigned", (long)var1);
         this.getParameter(var1);
         return true;
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public String getParameterClassName(int var1) throws SQLException {
      try {
         this.debugCodeCall("getParameterClassName", (long)var1);
         int var2 = this.getParameter(var1).getType().getValueType();
         if (var2 == -1) {
            var2 = 2;
         }

         return ValueToObjectConverter.getDefaultClass(var2, true).getName();
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public String getParameterTypeName(int var1) throws SQLException {
      try {
         this.debugCodeCall("getParameterTypeName", (long)var1);
         TypeInfo var2 = this.getParameter(var1).getType();
         if (var2.getValueType() == -1) {
            var2 = TypeInfo.TYPE_VARCHAR;
         }

         return var2.getDeclaredTypeName();
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   private ParameterInterface getParameter(int var1) {
      this.checkClosed();
      if (var1 >= 1 && var1 <= this.paramCount) {
         return (ParameterInterface)this.parameters.get(var1 - 1);
      } else {
         throw DbException.getInvalidValueException("param", var1);
      }
   }

   private void checkClosed() {
      this.prep.checkClosed();
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
      return this.getTraceObjectName() + ": parameterCount=" + this.paramCount;
   }
}
