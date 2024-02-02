package org.h2.expression;

import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.util.DateTimeUtils;
import org.h2.util.TimeZoneProvider;
import org.h2.value.DataType;
import org.h2.value.ExtTypeInfo;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueInterval;
import org.h2.value.ValueNull;
import org.h2.value.ValueTimeTimeZone;
import org.h2.value.ValueTimestampTimeZone;

public final class TimeZoneOperation extends Operation1_2 {
   public TimeZoneOperation(Expression var1, Expression var2) {
      super(var1, var2);
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      this.left.getSQL(var1, var2, 0).append(" AT ");
      if (this.right != null) {
         this.right.getSQL(var1.append("TIME ZONE "), var2, 0);
      } else {
         var1.append("LOCAL");
      }

      return var1;
   }

   public Value getValue(SessionLocal var1) {
      Object var2 = this.left.getValue(var1).convertTo(this.type, var1);
      int var3 = ((Value)var2).getValueType();
      if ((var3 == 21 || var3 == 19) && this.right != null) {
         Value var4 = this.right.getValue(var1);
         if (var4 != ValueNull.INSTANCE) {
            long var6;
            if (var3 == 21) {
               ValueTimestampTimeZone var5 = (ValueTimestampTimeZone)var2;
               var6 = var5.getDateValue();
               long var8 = var5.getTimeNanos();
               int var10 = var5.getTimeZoneOffsetSeconds();
               int var11 = parseTimeZone(var4, var6, var8, var10, true);
               if (var10 != var11) {
                  var2 = DateTimeUtils.timestampTimeZoneAtOffset(var6, var8, var10, var11);
               }
            } else {
               ValueTimeTimeZone var12 = (ValueTimeTimeZone)var2;
               var6 = var12.getNanos();
               int var13 = var12.getTimeZoneOffsetSeconds();
               int var9 = parseTimeZone(var4, 1008673L, var6, var13, false);
               if (var13 != var9) {
                  var6 += (long)(var9 - var13) * 1000000000L;
                  var2 = ValueTimeTimeZone.fromNanos(DateTimeUtils.normalizeNanosOfDay(var6), var9);
               }
            }
         } else {
            var2 = ValueNull.INSTANCE;
         }
      }

      return (Value)var2;
   }

   private static int parseTimeZone(Value var0, long var1, long var3, int var5, boolean var6) {
      if (DataType.isCharacterStringType(var0.getValueType())) {
         TimeZoneProvider var7;
         try {
            var7 = TimeZoneProvider.ofId(var0.getString());
         } catch (RuntimeException var9) {
            throw DbException.getInvalidValueException("time zone", var0.getTraceSQL());
         }

         if (!var6 && !var7.hasFixedOffset()) {
            throw DbException.getInvalidValueException("time zone", var0.getTraceSQL());
         } else {
            return var7.getTimeZoneOffsetUTC(DateTimeUtils.getEpochSeconds(var1, var3, var5));
         }
      } else {
         return parseInterval(var0);
      }
   }

   public static int parseInterval(Value var0) {
      ValueInterval var1 = (ValueInterval)var0.convertTo(TypeInfo.TYPE_INTERVAL_HOUR_TO_SECOND);
      long var2 = var1.getLeading();
      long var4 = var1.getRemaining();
      if (var2 <= 18L && (var2 != 18L || var4 == 0L) && var4 % 1000000000L == 0L) {
         int var6 = (int)(var2 * 3600L + var4 / 1000000000L);
         if (var1.isNegative()) {
            var6 = -var6;
         }

         return var6;
      } else {
         throw DbException.getInvalidValueException("time zone", var1.getTraceSQL());
      }
   }

   public Expression optimize(SessionLocal var1) {
      this.left = this.left.optimize(var1);
      if (this.right != null) {
         this.right = this.right.optimize(var1);
      }

      TypeInfo var2 = this.left.getType();
      byte var3 = 21;
      boolean var4 = true;
      int var7;
      switch (var2.getValueType()) {
         case 18:
         case 19:
            var3 = 19;
            var7 = var2.getScale();
            break;
         case 20:
         case 21:
            var7 = var2.getScale();
            break;
         default:
            StringBuilder var5 = this.left.getSQL(new StringBuilder(), 3, 0);
            int var6 = var5.length();
            var5.append(" AT ");
            if (this.right != null) {
               this.right.getSQL(var5.append("TIME ZONE "), 3, 0);
            } else {
               var5.append("LOCAL");
            }

            throw DbException.getSyntaxError(var5.toString(), var6, "time, timestamp");
      }

      this.type = TypeInfo.getTypeInfo(var3, -1L, var7, (ExtTypeInfo)null);
      return (Expression)(!this.left.isConstant() || this.right != null && !this.right.isConstant() ? this : ValueExpression.get(this.getValue(var1)));
   }
}
