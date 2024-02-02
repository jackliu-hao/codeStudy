package org.h2.mode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ValueExpression;
import org.h2.message.DbException;
import org.h2.util.DateTimeUtils;
import org.h2.util.StringUtils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;
import org.h2.value.ValueTimestamp;
import org.h2.value.ValueTimestampTimeZone;
import org.h2.value.ValueVarchar;

public final class FunctionsMySQL extends ModeFunction {
   private static final int UNIX_TIMESTAMP = 1001;
   private static final int FROM_UNIXTIME = 1002;
   private static final int DATE = 1003;
   private static final int LAST_INSERT_ID = 1004;
   private static final HashMap<String, FunctionInfo> FUNCTIONS = new HashMap();
   private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
   private static final String[] FORMAT_REPLACE;

   public static int unixTimestamp(SessionLocal var0, Value var1) {
      long var2;
      long var5;
      if (var1 instanceof ValueTimestampTimeZone) {
         ValueTimestampTimeZone var4 = (ValueTimestampTimeZone)var1;
         var5 = var4.getTimeNanos();
         var2 = DateTimeUtils.absoluteDayFromDateValue(var4.getDateValue()) * 86400L + var5 / 1000000000L - (long)var4.getTimeZoneOffsetSeconds();
      } else {
         ValueTimestamp var7 = (ValueTimestamp)var1.convertTo(TypeInfo.TYPE_TIMESTAMP, var0);
         var5 = var7.getTimeNanos();
         var2 = var0.currentTimeZone().getEpochSecondsFromLocal(var7.getDateValue(), var5);
      }

      return (int)var2;
   }

   public static String fromUnixTime(int var0) {
      SimpleDateFormat var1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
      return var1.format(new Date((long)var0 * 1000L));
   }

   public static String fromUnixTime(int var0, String var1) {
      var1 = convertToSimpleDateFormat(var1);
      SimpleDateFormat var2 = new SimpleDateFormat(var1, Locale.ENGLISH);
      return var2.format(new Date((long)var0 * 1000L));
   }

   private static String convertToSimpleDateFormat(String var0) {
      String[] var1 = FORMAT_REPLACE;

      for(int var2 = 0; var2 < var1.length; var2 += 2) {
         var0 = StringUtils.replaceAll(var0, var1[var2], var1[var2 + 1]);
      }

      return var0;
   }

   public static FunctionsMySQL getFunction(String var0) {
      FunctionInfo var1 = (FunctionInfo)FUNCTIONS.get(var0);
      return var1 != null ? new FunctionsMySQL(var1) : null;
   }

   FunctionsMySQL(FunctionInfo var1) {
      super(var1);
   }

   protected void checkParameterCount(int var1) {
      byte var2;
      byte var3;
      switch (this.info.type) {
         case 1001:
            var2 = 0;
            var3 = 1;
            break;
         case 1002:
            var2 = 1;
            var3 = 2;
            break;
         case 1003:
            var2 = 1;
            var3 = 1;
            break;
         case 1004:
            var2 = 0;
            var3 = 1;
            break;
         default:
            throw DbException.getInternalError("type=" + this.info.type);
      }

      if (var1 < var2 || var1 > var3) {
         throw DbException.get(7001, (String[])(this.info.name, var2 + ".." + var3));
      }
   }

   public Expression optimize(SessionLocal var1) {
      boolean var2 = this.optimizeArguments(var1);
      this.type = TypeInfo.getTypeInfo(this.info.returnDataType);
      return (Expression)(var2 ? ValueExpression.get(this.getValue(var1)) : this);
   }

   public Value getValue(SessionLocal var1) {
      Value[] var2 = new Value[this.args.length];
      Value var3 = getNullOrValue(var1, this.args, var2, 0);
      Value var4 = getNullOrValue(var1, this.args, var2, 1);
      Object var8;
      switch (this.info.type) {
         case 1001:
            var8 = ValueInteger.get(unixTimestamp(var1, (Value)(var3 == null ? var1.currentTimestamp() : var3)));
            break;
         case 1002:
            var8 = ValueVarchar.get(var4 == null ? fromUnixTime(var3.getInt()) : fromUnixTime(var3.getInt(), var4.getString()));
            break;
         case 1003:
            switch (var3.getValueType()) {
               case 0:
               case 17:
                  var8 = var3;
                  return (Value)var8;
               default:
                  try {
                     var3 = var3.convertTo(TypeInfo.TYPE_TIMESTAMP, var1);
                  } catch (DbException var7) {
                     var8 = ValueNull.INSTANCE;
                     return (Value)var8;
                  }
               case 20:
               case 21:
                  var8 = var3.convertToDate(var1);
                  return (Value)var8;
            }
         case 1004:
            if (this.args.length == 0) {
               Value var5 = var1.getLastIdentity();
               if (var5 == ValueNull.INSTANCE) {
                  var8 = ValueBigint.get(0L);
               } else {
                  var8 = var5.convertToBigint((Object)null);
               }
            } else {
               var8 = var3;
               if (var3 == ValueNull.INSTANCE) {
                  var1.setLastIdentity(ValueNull.INSTANCE);
               } else {
                  var1.setLastIdentity((Value)(var8 = var3.convertToBigint((Object)null)));
               }
            }
            break;
         default:
            throw DbException.getInternalError("type=" + this.info.type);
      }

      return (Value)var8;
   }

   static {
      FUNCTIONS.put("UNIX_TIMESTAMP", new FunctionInfo("UNIX_TIMESTAMP", 1001, -1, 11, false, false));
      FUNCTIONS.put("FROM_UNIXTIME", new FunctionInfo("FROM_UNIXTIME", 1002, -1, 2, false, true));
      FUNCTIONS.put("DATE", new FunctionInfo("DATE", 1003, 1, 17, false, true));
      FUNCTIONS.put("LAST_INSERT_ID", new FunctionInfo("LAST_INSERT_ID", 1004, -1, 12, false, false));
      FORMAT_REPLACE = new String[]{"%a", "EEE", "%b", "MMM", "%c", "MM", "%d", "dd", "%e", "d", "%H", "HH", "%h", "hh", "%I", "hh", "%i", "mm", "%j", "DDD", "%k", "H", "%l", "h", "%M", "MMMM", "%m", "MM", "%p", "a", "%r", "hh:mm:ss a", "%S", "ss", "%s", "ss", "%T", "HH:mm:ss", "%W", "EEEE", "%w", "F", "%Y", "yyyy", "%y", "yy", "%%", "%"};
   }
}
