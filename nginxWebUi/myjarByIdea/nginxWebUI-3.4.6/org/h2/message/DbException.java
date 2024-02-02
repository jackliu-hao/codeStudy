package org.h2.message;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import org.h2.api.ErrorCode;
import org.h2.jdbc.JdbcException;
import org.h2.jdbc.JdbcSQLDataException;
import org.h2.jdbc.JdbcSQLException;
import org.h2.jdbc.JdbcSQLFeatureNotSupportedException;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.h2.jdbc.JdbcSQLInvalidAuthorizationSpecException;
import org.h2.jdbc.JdbcSQLNonTransientConnectionException;
import org.h2.jdbc.JdbcSQLNonTransientException;
import org.h2.jdbc.JdbcSQLSyntaxErrorException;
import org.h2.jdbc.JdbcSQLTimeoutException;
import org.h2.jdbc.JdbcSQLTransactionRollbackException;
import org.h2.jdbc.JdbcSQLTransientException;
import org.h2.util.HasSQL;
import org.h2.util.SortedProperties;
import org.h2.util.StringUtils;
import org.h2.util.Utils;
import org.h2.value.TypeInfo;
import org.h2.value.Typed;

public class DbException extends RuntimeException {
   private static final long serialVersionUID = 1L;
   public static final String HIDE_SQL = "--hide--";
   private static final Properties MESSAGES = new Properties();
   public static final SQLException SQL_OOME = new SQLException("OutOfMemoryError", "HY000", 90108, new OutOfMemoryError());
   private static final DbException OOME;
   private Object source;

   private DbException(SQLException var1) {
      super(var1.getMessage(), var1);
   }

   private static String translate(String var0, String... var1) {
      String var2 = MESSAGES.getProperty(var0);
      if (var2 == null) {
         var2 = "(Message " + var0 + " not found)";
      }

      if (var1 != null) {
         for(int var3 = 0; var3 < var1.length; ++var3) {
            String var4 = var1[var3];
            if (var4 != null && var4.length() > 0) {
               var1[var3] = quote(var4);
            }
         }

         var2 = MessageFormat.format(var2, (Object[])var1);
      }

      return var2;
   }

   private static String quote(String var0) {
      int var1 = var0.length();
      StringBuilder var2 = (new StringBuilder(var1 + 2)).append('"');
      int var3 = 0;

      while(true) {
         while(var3 < var1) {
            int var4 = var0.codePointAt(var3);
            var3 += Character.charCount(var4);
            int var5 = Character.getType(var4);
            if (var5 != 0 && (var5 < 12 || var5 > 19 || var4 == 32)) {
               if (var4 == 34 || var4 == 92) {
                  var2.append((char)var4);
               }

               var2.appendCodePoint(var4);
            } else if (var4 <= 65535) {
               StringUtils.appendHex(var2.append('\\'), (long)var4, 2);
            } else {
               StringUtils.appendHex(var2.append("\\+"), (long)var4, 3);
            }
         }

         return var2.append('"').toString();
      }
   }

   public SQLException getSQLException() {
      return (SQLException)this.getCause();
   }

   public int getErrorCode() {
      return this.getSQLException().getErrorCode();
   }

   public DbException addSQL(String var1) {
      SQLException var2 = this.getSQLException();
      if (var2 instanceof JdbcException) {
         JdbcException var3 = (JdbcException)var2;
         if (var3.getSQL() == null) {
            var3.setSQL(filterSQL(var1));
         }

         return this;
      } else {
         var2 = getJdbcSQLException(var2.getMessage(), var1, var2.getSQLState(), var2.getErrorCode(), var2, (String)null);
         return new DbException(var2);
      }
   }

   public static DbException get(int var0) {
      return get(var0, (String)null);
   }

   public static DbException get(int var0, String var1) {
      return get(var0, var1);
   }

   public static DbException get(int var0, Throwable var1, String... var2) {
      return new DbException(getJdbcSQLException(var0, var1, var2));
   }

   public static DbException get(int var0, String... var1) {
      return new DbException(getJdbcSQLException(var0, (Throwable)null, var1));
   }

   public static DbException fromUser(String var0, String var1) {
      return new DbException(getJdbcSQLException(var1, (String)null, var0, 0, (Throwable)null, (String)null));
   }

   public static DbException getSyntaxError(String var0, int var1) {
      var0 = StringUtils.addAsterisk(var0, var1);
      return get(42000, (String)var0);
   }

   public static DbException getSyntaxError(String var0, int var1, String var2) {
      var0 = StringUtils.addAsterisk(var0, var1);
      return new DbException(getJdbcSQLException(42001, (Throwable)null, var0, var2));
   }

   public static DbException getSyntaxError(int var0, String var1, int var2, String... var3) {
      var1 = StringUtils.addAsterisk(var1, var2);
      String var4 = ErrorCode.getState(var0);
      String var5 = translate(var4, var3);
      return new DbException(getJdbcSQLException(var5, var1, var4, var0, (Throwable)null, (String)null));
   }

   public static DbException getUnsupportedException(String var0) {
      return get(50100, (String)var0);
   }

   public static DbException getInvalidValueException(String var0, Object var1) {
      return get(90008, var1 == null ? "null" : var1.toString(), var0);
   }

   public static DbException getInvalidExpressionTypeException(String var0, Typed var1) {
      TypeInfo var2 = var1.getType();
      return var2.getValueType() == -1 ? get(50004, (String)((HasSQL)(var1 instanceof HasSQL ? (HasSQL)var1 : var2)).getTraceSQL()) : get(90008, var2.getTraceSQL(), var0);
   }

   public static DbException getValueTooLongException(String var0, String var1, long var2) {
      int var4 = var1.length();
      int var5 = var2 >= 0L ? 22 : 0;
      StringBuilder var6 = var4 > 80 ? (new StringBuilder(83 + var5)).append(var1, 0, 80).append("...") : (new StringBuilder(var4 + var5)).append(var1);
      if (var2 >= 0L) {
         var6.append(" (").append(var2).append(')');
      }

      return get(22001, (String[])(var0, var6.toString()));
   }

   public static DbException getFileVersionError(String var0) {
      return get(90048, "Old database: " + var0 + " - please convert the database to a SQL script and re-create it.");
   }

   public static RuntimeException getInternalError(String var0) {
      RuntimeException var1 = new RuntimeException(var0);
      traceThrowable(var1);
      return var1;
   }

   public static RuntimeException getInternalError() {
      return getInternalError("Unexpected code path");
   }

   public static SQLException toSQLException(Throwable var0) {
      return var0 instanceof SQLException ? (SQLException)var0 : convert(var0).getSQLException();
   }

   public static DbException convert(Throwable var0) {
      try {
         if (var0 instanceof DbException) {
            return (DbException)var0;
         } else if (var0 instanceof SQLException) {
            return new DbException((SQLException)var0);
         } else if (var0 instanceof InvocationTargetException) {
            return convertInvocation((InvocationTargetException)var0, (String)null);
         } else if (var0 instanceof IOException) {
            return get(90028, var0, var0.toString());
         } else if (var0 instanceof OutOfMemoryError) {
            return get(90108, var0);
         } else if (!(var0 instanceof StackOverflowError) && !(var0 instanceof LinkageError)) {
            if (var0 instanceof Error) {
               throw (Error)var0;
            } else {
               return get(50000, var0, var0.toString());
            }
         } else {
            return get(50000, var0, var0.toString());
         }
      } catch (OutOfMemoryError var4) {
         return OOME;
      } catch (Throwable var5) {
         Throwable var1 = var5;

         try {
            DbException var2 = new DbException(new SQLException("GeneralError", "HY000", 50000, var0));
            var2.addSuppressed(var1);
            return var2;
         } catch (OutOfMemoryError var3) {
            return OOME;
         }
      }
   }

   public static DbException convertInvocation(InvocationTargetException var0, String var1) {
      Throwable var2 = var0.getTargetException();
      if (!(var2 instanceof SQLException) && !(var2 instanceof DbException)) {
         var1 = var1 == null ? var2.getMessage() : var1 + ": " + var2.getMessage();
         return get(90105, var2, var1);
      } else {
         return convert(var2);
      }
   }

   public static DbException convertIOException(IOException var0, String var1) {
      if (var1 == null) {
         Throwable var2 = var0.getCause();
         return var2 instanceof DbException ? (DbException)var2 : get(90028, var0, var0.toString());
      } else {
         return get(90031, var0, var0.toString(), var1);
      }
   }

   public static SQLException getJdbcSQLException(int var0) {
      return getJdbcSQLException(var0, (Throwable)null);
   }

   public static SQLException getJdbcSQLException(int var0, String var1) {
      return getJdbcSQLException(var0, (Throwable)null, var1);
   }

   public static SQLException getJdbcSQLException(int var0, Throwable var1, String... var2) {
      String var3 = ErrorCode.getState(var0);
      String var4 = translate(var3, var2);
      return getJdbcSQLException(var4, (String)null, var3, var0, var1, (String)null);
   }

   public static SQLException getJdbcSQLException(String var0, String var1, String var2, int var3, Throwable var4, String var5) {
      var1 = filterSQL(var1);
      switch (var3 / 1000) {
         case 2:
            return new JdbcSQLNonTransientException(var0, var1, var2, var3, var4, var5);
         case 7:
         case 21:
         case 42:
         case 54:
            return new JdbcSQLSyntaxErrorException(var0, var1, var2, var3, var4, var5);
         case 8:
            return new JdbcSQLNonTransientConnectionException(var0, var1, var2, var3, var4, var5);
         case 22:
            return new JdbcSQLDataException(var0, var1, var2, var3, var4, var5);
         case 23:
            return new JdbcSQLIntegrityConstraintViolationException(var0, var1, var2, var3, var4, var5);
         case 28:
            return new JdbcSQLInvalidAuthorizationSpecException(var0, var1, var2, var3, var4, var5);
         case 40:
            return new JdbcSQLTransactionRollbackException(var0, var1, var2, var3, var4, var5);
         default:
            switch (var3) {
               case 50000:
               case 50004:
               case 90001:
               case 90002:
               case 90006:
               case 90007:
               case 90019:
               case 90021:
               case 90024:
               case 90025:
               case 90028:
               case 90029:
               case 90031:
               case 90034:
               case 90040:
               case 90044:
               case 90058:
               case 90062:
               case 90063:
               case 90064:
               case 90065:
               case 90096:
               case 90097:
               case 90101:
               case 90102:
               case 90103:
               case 90104:
               case 90105:
               case 90111:
               case 90124:
               case 90125:
               case 90126:
               case 90127:
               case 90128:
               case 90130:
               case 90134:
               case 90140:
               case 90148:
                  return new JdbcSQLNonTransientException(var0, var1, var2, var3, var4, var5);
               case 50100:
                  return new JdbcSQLFeatureNotSupportedException(var0, var1, var2, var3, var4, var5);
               case 50200:
               case 57014:
               case 90039:
                  return new JdbcSQLTimeoutException(var0, var1, var2, var3, var4, var5);
               case 90000:
               case 90005:
               case 90015:
               case 90016:
               case 90017:
               case 90022:
               case 90023:
               case 90032:
               case 90033:
               case 90035:
               case 90036:
               case 90037:
               case 90038:
               case 90041:
               case 90042:
               case 90043:
               case 90045:
               case 90052:
               case 90054:
               case 90057:
               case 90059:
               case 90068:
               case 90069:
               case 90070:
               case 90071:
               case 90072:
               case 90073:
               case 90074:
               case 90075:
               case 90076:
               case 90077:
               case 90078:
               case 90079:
               case 90080:
               case 90081:
               case 90082:
               case 90083:
               case 90084:
               case 90085:
               case 90086:
               case 90087:
               case 90089:
               case 90090:
               case 90091:
               case 90106:
               case 90107:
               case 90109:
               case 90110:
               case 90114:
               case 90115:
               case 90116:
               case 90118:
               case 90119:
               case 90120:
               case 90122:
               case 90123:
               case 90129:
               case 90132:
               case 90136:
               case 90137:
               case 90139:
               case 90141:
               case 90145:
               case 90150:
               case 90151:
               case 90152:
               case 90153:
               case 90154:
               case 90155:
               case 90156:
               case 90157:
                  return new JdbcSQLSyntaxErrorException(var0, var1, var2, var3, var4, var5);
               case 90003:
               case 90004:
               case 90008:
               case 90009:
               case 90010:
               case 90012:
               case 90014:
               case 90026:
               case 90027:
               case 90053:
               case 90056:
               case 90095:
               case 90142:
                  return new JdbcSQLDataException(var0, var1, var2, var3, var4, var5);
               case 90011:
               case 90013:
               case 90018:
               case 90020:
               case 90030:
               case 90046:
               case 90047:
               case 90048:
               case 90049:
               case 90050:
               case 90055:
               case 90060:
               case 90061:
               case 90066:
               case 90067:
               case 90088:
               case 90093:
               case 90094:
               case 90098:
               case 90099:
               case 90108:
               case 90113:
               case 90117:
               case 90121:
               case 90133:
               case 90135:
               case 90138:
               case 90144:
               case 90146:
               case 90147:
               case 90149:
                  return new JdbcSQLNonTransientConnectionException(var0, var1, var2, var3, var4, var5);
               case 90112:
               case 90131:
               case 90143:
                  return new JdbcSQLTransientException(var0, var1, var2, var3, var4, var5);
               default:
                  return new JdbcSQLException(var0, var1, var2, var3, var4, var5);
            }
      }
   }

   private static String filterSQL(String var0) {
      return var0 != null && var0.contains("--hide--") ? "-" : var0;
   }

   public static String buildMessageForException(JdbcException var0) {
      String var1 = var0.getOriginalMessage();
      StringBuilder var2 = new StringBuilder(var1 != null ? var1 : "- ");
      var1 = var0.getSQL();
      if (var1 != null) {
         var2.append("; SQL statement:\n").append(var1);
      }

      var2.append(" [").append(var0.getErrorCode()).append('-').append(210).append(']');
      return var2.toString();
   }

   public static void printNextExceptions(SQLException var0, PrintWriter var1) {
      int var2 = 0;

      while((var0 = var0.getNextException()) != null) {
         if (var2++ == 100) {
            var1.println("(truncated)");
            return;
         }

         var1.println(var0.toString());
      }

   }

   public static void printNextExceptions(SQLException var0, PrintStream var1) {
      int var2 = 0;

      while((var0 = var0.getNextException()) != null) {
         if (var2++ == 100) {
            var1.println("(truncated)");
            return;
         }

         var1.println(var0.toString());
      }

   }

   public Object getSource() {
      return this.source;
   }

   public void setSource(Object var1) {
      this.source = var1;
   }

   public static void traceThrowable(Throwable var0) {
      PrintWriter var1 = DriverManager.getLogWriter();
      if (var1 != null) {
         var0.printStackTrace(var1);
      }

   }

   static {
      OOME = new DbException(SQL_OOME);

      try {
         byte[] var0 = Utils.getResource("/org/h2/res/_messages_en.prop");
         if (var0 != null) {
            MESSAGES.load(new ByteArrayInputStream(var0));
         }

         String var1 = Locale.getDefault().getLanguage();
         if (!"en".equals(var1)) {
            byte[] var2 = Utils.getResource("/org/h2/res/_messages_" + var1 + ".prop");
            if (var2 != null) {
               SortedProperties var3 = SortedProperties.fromLines(new String(var2, StandardCharsets.UTF_8));
               Iterator var4 = var3.entrySet().iterator();

               while(var4.hasNext()) {
                  Map.Entry var5 = (Map.Entry)var4.next();
                  String var6 = (String)var5.getKey();
                  String var7 = (String)var5.getValue();
                  if (var7 != null && !var7.startsWith("#")) {
                     String var8 = MESSAGES.getProperty(var6);
                     String var9 = var7 + "\n" + var8;
                     MESSAGES.put(var6, var9);
                  }
               }
            }
         }
      } catch (IOException | OutOfMemoryError var10) {
         traceThrowable(var10);
      }

   }
}
