package org.h2.jdbc;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import org.h2.command.CommandInterface;
import org.h2.message.DbException;
import org.h2.message.TraceObject;
import org.h2.result.SimpleResult;
import org.h2.value.DataType;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueArray;
import org.h2.value.ValueBigint;
import org.h2.value.ValueToObjectConverter;

public final class JdbcArray extends TraceObject implements Array {
   private ValueArray value;
   private final JdbcConnection conn;

   public JdbcArray(JdbcConnection var1, Value var2, int var3) {
      this.setTrace(var1.getSession().getTrace(), 16, var3);
      this.conn = var1;
      this.value = var2.convertToAnyArray(var1);
   }

   public Object getArray() throws SQLException {
      try {
         this.debugCodeCall("getArray");
         this.checkClosed();
         return this.get();
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public Object getArray(Map<String, Class<?>> var1) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("getArray(" + quoteMap(var1) + ')');
         }

         JdbcConnection.checkMap(var1);
         this.checkClosed();
         return this.get();
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public Object getArray(long var1, int var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("getArray(" + var1 + ", " + var3 + ')');
         }

         this.checkClosed();
         return this.get(var1, var3);
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public Object getArray(long var1, int var3, Map<String, Class<?>> var4) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("getArray(" + var1 + ", " + var3 + ", " + quoteMap(var4) + ')');
         }

         this.checkClosed();
         JdbcConnection.checkMap(var4);
         return this.get(var1, var3);
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   public int getBaseType() throws SQLException {
      try {
         this.debugCodeCall("getBaseType");
         this.checkClosed();
         return DataType.convertTypeToSQLType(this.value.getComponentType());
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public String getBaseTypeName() throws SQLException {
      try {
         this.debugCodeCall("getBaseTypeName");
         this.checkClosed();
         return this.value.getComponentType().getDeclaredTypeName();
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public ResultSet getResultSet() throws SQLException {
      try {
         this.debugCodeCall("getResultSet");
         this.checkClosed();
         return this.getResultSetImpl(1L, Integer.MAX_VALUE);
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public ResultSet getResultSet(Map<String, Class<?>> var1) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("getResultSet(" + quoteMap(var1) + ')');
         }

         this.checkClosed();
         JdbcConnection.checkMap(var1);
         return this.getResultSetImpl(1L, Integer.MAX_VALUE);
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public ResultSet getResultSet(long var1, int var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("getResultSet(" + var1 + ", " + var3 + ')');
         }

         this.checkClosed();
         return this.getResultSetImpl(var1, var3);
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public ResultSet getResultSet(long var1, int var3, Map<String, Class<?>> var4) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("getResultSet(" + var1 + ", " + var3 + ", " + quoteMap(var4) + ')');
         }

         this.checkClosed();
         JdbcConnection.checkMap(var4);
         return this.getResultSetImpl(var1, var3);
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   public void free() {
      this.debugCodeCall("free");
      this.value = null;
   }

   private ResultSet getResultSetImpl(long var1, int var3) {
      int var4 = getNextId(4);
      SimpleResult var5 = new SimpleResult();
      var5.addColumn("INDEX", TypeInfo.TYPE_BIGINT);
      var5.addColumn("VALUE", this.value.getComponentType());
      Value[] var6 = this.value.getList();
      var3 = checkRange(var1, var3, var6.length);

      for(int var7 = (int)var1; (long)var7 < var1 + (long)var3; ++var7) {
         var5.addRow(ValueBigint.get((long)var7), var6[var7 - 1]);
      }

      return new JdbcResultSet(this.conn, (JdbcStatement)null, (CommandInterface)null, var5, var4, true, false, false);
   }

   private void checkClosed() {
      this.conn.checkClosed();
      if (this.value == null) {
         throw DbException.get(90007);
      }
   }

   private Object get() {
      return ValueToObjectConverter.valueToDefaultArray(this.value, this.conn, true);
   }

   private Object get(long var1, int var3) {
      Value[] var4 = this.value.getList();
      var3 = checkRange(var1, var3, var4.length);
      Object[] var5 = new Object[var3];
      int var6 = 0;

      for(int var7 = (int)var1 - 1; var6 < var3; ++var7) {
         var5[var6] = ValueToObjectConverter.valueToDefaultObject(var4[var7], this.conn, true);
         ++var6;
      }

      return var5;
   }

   private static int checkRange(long var0, int var2, int var3) {
      if (var0 >= 1L && (var0 == 1L || var0 <= (long)var3)) {
         int var4 = var3 - (int)var0 + 1;
         if (var2 < 0) {
            throw DbException.getInvalidValueException("count (0.." + var4 + ')', var2);
         } else {
            return Math.min(var4, var2);
         }
      } else {
         throw DbException.getInvalidValueException("index (1.." + var3 + ')', var0);
      }
   }

   public String toString() {
      return this.value == null ? "null" : this.getTraceObjectName() + ": " + this.value.getTraceSQL();
   }
}
