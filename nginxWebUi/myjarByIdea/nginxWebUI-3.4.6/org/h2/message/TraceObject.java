package org.h2.message;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.atomic.AtomicIntegerArray;
import org.h2.util.StringUtils;

public abstract class TraceObject {
   protected static final int CALLABLE_STATEMENT = 0;
   protected static final int CONNECTION = 1;
   protected static final int DATABASE_META_DATA = 2;
   protected static final int PREPARED_STATEMENT = 3;
   protected static final int RESULT_SET = 4;
   protected static final int RESULT_SET_META_DATA = 5;
   protected static final int SAVEPOINT = 6;
   protected static final int STATEMENT = 8;
   protected static final int BLOB = 9;
   protected static final int CLOB = 10;
   protected static final int PARAMETER_META_DATA = 11;
   protected static final int DATA_SOURCE = 12;
   protected static final int XA_DATA_SOURCE = 13;
   protected static final int XID = 15;
   protected static final int ARRAY = 16;
   protected static final int SQLXML = 17;
   private static final int LAST = 18;
   private static final AtomicIntegerArray ID = new AtomicIntegerArray(18);
   private static final String[] PREFIX = new String[]{"call", "conn", "dbMeta", "prep", "rs", "rsMeta", "sp", "ex", "stat", "blob", "clob", "pMeta", "ds", "xads", "xares", "xid", "ar", "sqlxml"};
   private static final SQLException SQL_OOME;
   protected Trace trace;
   private int traceType;
   private int id;

   protected void setTrace(Trace var1, int var2, int var3) {
      this.trace = var1;
      this.traceType = var2;
      this.id = var3;
   }

   public int getTraceId() {
      return this.id;
   }

   public String getTraceObjectName() {
      return PREFIX[this.traceType] + this.id;
   }

   protected static int getNextId(int var0) {
      return ID.getAndIncrement(var0);
   }

   protected final boolean isDebugEnabled() {
      return this.trace.isDebugEnabled();
   }

   protected final boolean isInfoEnabled() {
      return this.trace.isInfoEnabled();
   }

   protected final void debugCodeAssign(String var1, int var2, int var3, String var4) {
      if (this.trace.isDebugEnabled()) {
         this.trace.debugCode(var1 + ' ' + PREFIX[var2] + var3 + " = " + this.getTraceObjectName() + '.' + var4 + ';');
      }

   }

   protected final void debugCodeCall(String var1) {
      if (this.trace.isDebugEnabled()) {
         this.trace.debugCode(this.getTraceObjectName() + '.' + var1 + "();");
      }

   }

   protected final void debugCodeCall(String var1, long var2) {
      if (this.trace.isDebugEnabled()) {
         this.trace.debugCode(this.getTraceObjectName() + '.' + var1 + '(' + var2 + ");");
      }

   }

   protected final void debugCodeCall(String var1, String var2) {
      if (this.trace.isDebugEnabled()) {
         this.trace.debugCode(this.getTraceObjectName() + '.' + var1 + '(' + quote(var2) + ");");
      }

   }

   protected final void debugCode(String var1) {
      if (this.trace.isDebugEnabled()) {
         this.trace.debugCode(this.getTraceObjectName() + '.' + var1 + ';');
      }

   }

   protected static String quote(String var0) {
      return StringUtils.quoteJavaString(var0);
   }

   protected static String quoteTime(Time var0) {
      return var0 == null ? "null" : "Time.valueOf(\"" + var0.toString() + "\")";
   }

   protected static String quoteTimestamp(Timestamp var0) {
      return var0 == null ? "null" : "Timestamp.valueOf(\"" + var0.toString() + "\")";
   }

   protected static String quoteDate(Date var0) {
      return var0 == null ? "null" : "Date.valueOf(\"" + var0.toString() + "\")";
   }

   protected static String quoteBigDecimal(BigDecimal var0) {
      return var0 == null ? "null" : "new BigDecimal(\"" + var0.toString() + "\")";
   }

   protected static String quoteBytes(byte[] var0) {
      if (var0 == null) {
         return "null";
      } else {
         StringBuilder var1 = (new StringBuilder(var0.length * 2 + 45)).append("org.h2.util.StringUtils.convertHexToBytes(\"");
         return StringUtils.convertBytesToHex(var1, var0).append("\")").toString();
      }
   }

   protected static String quoteArray(String[] var0) {
      return StringUtils.quoteJavaStringArray(var0);
   }

   protected static String quoteIntArray(int[] var0) {
      return StringUtils.quoteJavaIntArray(var0);
   }

   protected static String quoteMap(Map<String, Class<?>> var0) {
      if (var0 == null) {
         return "null";
      } else {
         return var0.size() == 0 ? "new Map()" : "new Map() /* " + var0.toString() + " */";
      }
   }

   protected SQLException logAndConvert(Throwable var1) {
      SQLException var2 = null;

      try {
         var2 = DbException.toSQLException(var1);
         if (this.trace == null) {
            DbException.traceThrowable(var2);
         } else {
            int var3 = var2.getErrorCode();
            if (var3 >= 23000 && var3 < 24000) {
               this.trace.info((Throwable)var2, (String)"exception");
            } else {
               this.trace.error(var2, "exception");
            }
         }
      } catch (Throwable var6) {
         if (var2 == null) {
            try {
               var2 = new SQLException("GeneralError", "HY000", 50000, var1);
            } catch (NoClassDefFoundError | OutOfMemoryError var5) {
               return SQL_OOME;
            }
         }

         var2.addSuppressed(var6);
      }

      return var2;
   }

   protected SQLException unsupported(String var1) {
      try {
         throw DbException.getUnsupportedException(var1);
      } catch (Exception var3) {
         return this.logAndConvert(var3);
      }
   }

   static {
      SQL_OOME = DbException.SQL_OOME;
   }
}
