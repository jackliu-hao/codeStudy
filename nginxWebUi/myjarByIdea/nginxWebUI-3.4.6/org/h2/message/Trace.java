package org.h2.message;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import org.h2.expression.ParameterInterface;
import org.h2.util.StringUtils;

public final class Trace {
   public static final int COMMAND = 0;
   public static final int CONSTRAINT = 1;
   public static final int DATABASE = 2;
   public static final int FUNCTION = 3;
   public static final int FILE_LOCK = 4;
   public static final int INDEX = 5;
   public static final int JDBC = 6;
   public static final int LOCK = 7;
   public static final int SCHEMA = 8;
   public static final int SEQUENCE = 9;
   public static final int SETTING = 10;
   public static final int TABLE = 11;
   public static final int TRIGGER = 12;
   public static final int USER = 13;
   public static final int JDBCX = 14;
   public static final String[] MODULE_NAMES = new String[]{"command", "constraint", "database", "function", "fileLock", "index", "jdbc", "lock", "schema", "sequence", "setting", "table", "trigger", "user", "JDBCX"};
   private final TraceWriter traceWriter;
   private final String module;
   private final String lineSeparator;
   private int traceLevel;

   Trace(TraceWriter var1, int var2) {
      this(var1, MODULE_NAMES[var2]);
   }

   Trace(TraceWriter var1, String var2) {
      this.traceLevel = -1;
      this.traceWriter = var1;
      this.module = var2;
      this.lineSeparator = System.lineSeparator();
   }

   public void setLevel(int var1) {
      this.traceLevel = var1;
   }

   private boolean isEnabled(int var1) {
      if (this.traceLevel == -1) {
         return this.traceWriter.isEnabled(var1);
      } else {
         return var1 <= this.traceLevel;
      }
   }

   public boolean isInfoEnabled() {
      return this.isEnabled(2);
   }

   public boolean isDebugEnabled() {
      return this.isEnabled(3);
   }

   public void error(Throwable var1, String var2) {
      if (this.isEnabled(1)) {
         this.traceWriter.write(1, this.module, var2, var1);
      }

   }

   public void error(Throwable var1, String var2, Object... var3) {
      if (this.isEnabled(1)) {
         var2 = MessageFormat.format(var2, var3);
         this.traceWriter.write(1, this.module, var2, var1);
      }

   }

   public void info(String var1) {
      if (this.isEnabled(2)) {
         this.traceWriter.write(2, this.module, var1, (Throwable)null);
      }

   }

   public void info(String var1, Object... var2) {
      if (this.isEnabled(2)) {
         var1 = MessageFormat.format(var1, var2);
         this.traceWriter.write(2, this.module, var1, (Throwable)null);
      }

   }

   void info(Throwable var1, String var2) {
      if (this.isEnabled(2)) {
         this.traceWriter.write(2, this.module, var2, var1);
      }

   }

   public static String formatParams(ArrayList<? extends ParameterInterface> var0) {
      if (var0.isEmpty()) {
         return "";
      } else {
         StringBuilder var1 = new StringBuilder();
         int var2 = 0;
         Iterator var3 = var0.iterator();

         while(var3.hasNext()) {
            ParameterInterface var4 = (ParameterInterface)var3.next();
            if (var4.isValueSet()) {
               StringBuilder var10000 = var1.append(var2 == 0 ? " {" : ", ");
               ++var2;
               var10000.append(var2).append(": ").append(var4.getParamValue().getTraceSQL());
            }
         }

         if (var2 != 0) {
            var1.append('}');
         }

         return var1.toString();
      }
   }

   public void infoSQL(String var1, String var2, long var3, long var5) {
      if (this.isEnabled(2)) {
         StringBuilder var7 = new StringBuilder(var1.length() + var2.length() + 20);
         var7.append(this.lineSeparator).append("/*SQL");
         boolean var8 = false;
         if (var2.length() > 0) {
            var8 = true;
            var7.append(" l:").append(var1.length());
         }

         if (var3 > 0L) {
            var8 = true;
            var7.append(" #:").append(var3);
         }

         if (var5 > 0L) {
            var8 = true;
            var7.append(" t:").append(var5);
         }

         if (!var8) {
            var7.append(' ');
         }

         var7.append("*/");
         StringUtils.javaEncode(var1, var7, false);
         StringUtils.javaEncode(var2, var7, false);
         var7.append(';');
         var1 = var7.toString();
         this.traceWriter.write(2, this.module, var1, (Throwable)null);
      }
   }

   public void debug(String var1, Object... var2) {
      if (this.isEnabled(3)) {
         var1 = MessageFormat.format(var1, var2);
         this.traceWriter.write(3, this.module, var1, (Throwable)null);
      }

   }

   public void debug(String var1) {
      if (this.isEnabled(3)) {
         this.traceWriter.write(3, this.module, var1, (Throwable)null);
      }

   }

   public void debug(Throwable var1, String var2) {
      if (this.isEnabled(3)) {
         this.traceWriter.write(3, this.module, var2, var1);
      }

   }

   public void infoCode(String var1) {
      if (this.isEnabled(2)) {
         this.traceWriter.write(2, this.module, this.lineSeparator + "/**/" + var1, (Throwable)null);
      }

   }

   void debugCode(String var1) {
      if (this.isEnabled(3)) {
         this.traceWriter.write(3, this.module, this.lineSeparator + "/**/" + var1, (Throwable)null);
      }

   }
}
