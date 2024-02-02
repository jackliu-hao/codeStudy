package org.h2.expression;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.h2.api.IntervalQualifier;
import org.h2.engine.SessionLocal;
import org.h2.expression.function.DateTimeFunction;
import org.h2.message.DbException;
import org.h2.util.DateTimeUtils;
import org.h2.util.IntervalUtils;
import org.h2.value.DataType;
import org.h2.value.ExtTypeInfo;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueDate;
import org.h2.value.ValueInterval;
import org.h2.value.ValueNull;
import org.h2.value.ValueNumeric;
import org.h2.value.ValueTime;
import org.h2.value.ValueTimeTimeZone;
import org.h2.value.ValueTimestampTimeZone;

public class IntervalOperation extends Operation2 {
   private static final int INTERVAL_YEAR_DIGITS = 20;
   private static final int INTERVAL_DAY_DIGITS = 32;
   private static final TypeInfo INTERVAL_DIVIDE_INTERVAL_YEAR_TYPE = TypeInfo.getTypeInfo(13, 60L, 40, (ExtTypeInfo)null);
   private static final TypeInfo INTERVAL_DIVIDE_INTERVAL_DAY_TYPE = TypeInfo.getTypeInfo(13, 96L, 64, (ExtTypeInfo)null);
   private final IntervalOpType opType;
   private TypeInfo forcedType;

   private static BigInteger nanosFromValue(SessionLocal var0, Value var1) {
      long[] var2 = DateTimeUtils.dateAndTimeFromValue(var1, var0);
      return BigInteger.valueOf(DateTimeUtils.absoluteDayFromDateValue(var2[0])).multiply(IntervalUtils.NANOS_PER_DAY_BI).add(BigInteger.valueOf(var2[1]));
   }

   public IntervalOperation(IntervalOpType var1, Expression var2, Expression var3, TypeInfo var4) {
      this(var1, var2, var3);
      this.forcedType = var4;
   }

   public IntervalOperation(IntervalOpType var1, Expression var2, Expression var3) {
      super(var2, var3);
      this.opType = var1;
      int var4 = var2.getType().getValueType();
      int var5 = var3.getType().getValueType();
      switch (var1) {
         case INTERVAL_PLUS_INTERVAL:
         case INTERVAL_MINUS_INTERVAL:
            this.type = TypeInfo.getTypeInfo(Value.getHigherOrder(var4, var5));
            break;
         case INTERVAL_DIVIDE_INTERVAL:
            this.type = DataType.isYearMonthIntervalType(var4) ? INTERVAL_DIVIDE_INTERVAL_YEAR_TYPE : INTERVAL_DIVIDE_INTERVAL_DAY_TYPE;
            break;
         case DATETIME_PLUS_INTERVAL:
         case DATETIME_MINUS_INTERVAL:
         case INTERVAL_MULTIPLY_NUMERIC:
         case INTERVAL_DIVIDE_NUMERIC:
            this.type = var2.getType();
            break;
         case DATETIME_MINUS_DATETIME:
            if (this.forcedType != null) {
               this.type = this.forcedType;
            } else if ((var4 == 18 || var4 == 19) && (var5 == 18 || var5 == 19)) {
               this.type = TypeInfo.TYPE_INTERVAL_HOUR_TO_SECOND;
            } else if (var4 == 17 && var5 == 17) {
               this.type = TypeInfo.TYPE_INTERVAL_DAY;
            } else {
               this.type = TypeInfo.TYPE_INTERVAL_DAY_TO_SECOND;
            }
      }

   }

   public boolean needParentheses() {
      return this.forcedType == null;
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      if (this.forcedType != null) {
         this.getInnerSQL2(var1.append('('), var2);
         getForcedTypeSQL(var1.append(") "), this.forcedType);
      } else {
         this.getInnerSQL2(var1, var2);
      }

      return var1;
   }

   private void getInnerSQL2(StringBuilder var1, int var2) {
      this.left.getSQL(var1, var2, 0).append(' ').append(this.getOperationToken()).append(' ');
      this.right.getSQL(var1, var2, 0);
   }

   static StringBuilder getForcedTypeSQL(StringBuilder var0, TypeInfo var1) {
      int var2 = (int)var1.getPrecision();
      int var3 = var1.getScale();
      return IntervalQualifier.valueOf(var1.getValueType() - 22).getTypeName(var0, var2 == 2 ? -1 : var2, var3 == 6 ? -1 : var3, true);
   }

   private char getOperationToken() {
      switch (this.opType) {
         case INTERVAL_PLUS_INTERVAL:
         case DATETIME_PLUS_INTERVAL:
            return '+';
         case INTERVAL_MINUS_INTERVAL:
         case DATETIME_MINUS_INTERVAL:
         case DATETIME_MINUS_DATETIME:
            return '-';
         case INTERVAL_DIVIDE_INTERVAL:
         case INTERVAL_DIVIDE_NUMERIC:
            return '/';
         case INTERVAL_MULTIPLY_NUMERIC:
            return '*';
         default:
            throw DbException.getInternalError("opType=" + this.opType);
      }
   }

   public Value getValue(SessionLocal var1) {
      Value var2 = this.left.getValue(var1);
      Value var3 = this.right.getValue(var1);
      if (var2 != ValueNull.INSTANCE && var3 != ValueNull.INSTANCE) {
         int var4 = var2.getValueType();
         int var5 = var3.getValueType();
         BigInteger var7;
         switch (this.opType) {
            case INTERVAL_PLUS_INTERVAL:
            case INTERVAL_MINUS_INTERVAL:
               BigInteger var20 = IntervalUtils.intervalToAbsolute((ValueInterval)var2);
               var7 = IntervalUtils.intervalToAbsolute((ValueInterval)var3);
               return IntervalUtils.intervalFromAbsolute(IntervalQualifier.valueOf(Value.getHigherOrder(var4, var5) - 22), this.opType == IntervalOperation.IntervalOpType.INTERVAL_PLUS_INTERVAL ? var20.add(var7) : var20.subtract(var7));
            case INTERVAL_DIVIDE_INTERVAL:
               return ValueNumeric.get(IntervalUtils.intervalToAbsolute((ValueInterval)var2)).divide(ValueNumeric.get(IntervalUtils.intervalToAbsolute((ValueInterval)var3)), this.type);
            case DATETIME_PLUS_INTERVAL:
            case DATETIME_MINUS_INTERVAL:
               return this.getDateTimeWithInterval(var1, var2, var3, var4, var5);
            case INTERVAL_MULTIPLY_NUMERIC:
            case INTERVAL_DIVIDE_NUMERIC:
               BigDecimal var19 = new BigDecimal(IntervalUtils.intervalToAbsolute((ValueInterval)var2));
               BigDecimal var24 = var3.getBigDecimal();
               return IntervalUtils.intervalFromAbsolute(IntervalQualifier.valueOf(var4 - 22), (this.opType == IntervalOperation.IntervalOpType.INTERVAL_MULTIPLY_NUMERIC ? var19.multiply(var24) : var19.divide(var24)).toBigInteger());
            case DATETIME_MINUS_DATETIME:
               Object var6;
               boolean var9;
               long var18;
               if ((var4 == 18 || var4 == 19) && (var5 == 18 || var5 == 19)) {
                  if (var4 == 18 && var5 == 18) {
                     var18 = ((ValueTime)var2).getNanos() - ((ValueTime)var3).getNanos();
                  } else {
                     ValueTimeTimeZone var23 = (ValueTimeTimeZone)var2.convertTo(TypeInfo.TYPE_TIME_TZ, var1);
                     ValueTimeTimeZone var10 = (ValueTimeTimeZone)var3.convertTo(TypeInfo.TYPE_TIME_TZ, var1);
                     var18 = var23.getNanos() - var10.getNanos() + (long)(var10.getTimeZoneOffsetSeconds() - var23.getTimeZoneOffsetSeconds()) * 1000000000L;
                  }

                  var9 = var18 < 0L;
                  if (var9) {
                     var18 = -var18;
                  }

                  var6 = ValueInterval.from(IntervalQualifier.HOUR_TO_SECOND, var9, var18 / 3600000000000L, var18 % 3600000000000L);
               } else if (this.forcedType != null && DataType.isYearMonthIntervalType(this.forcedType.getValueType())) {
                  long[] var21 = DateTimeUtils.dateAndTimeFromValue(var2, var1);
                  long[] var8 = DateTimeUtils.dateAndTimeFromValue(var3, var1);
                  long var22 = var4 != 18 && var4 != 19 ? var21[0] : var1.currentTimestamp().getDateValue();
                  long var11 = var5 != 18 && var5 != 19 ? var8[0] : var1.currentTimestamp().getDateValue();
                  long var13 = 12L * (long)(DateTimeUtils.yearFromDateValue(var22) - DateTimeUtils.yearFromDateValue(var11)) + (long)DateTimeUtils.monthFromDateValue(var22) - (long)DateTimeUtils.monthFromDateValue(var11);
                  int var15 = DateTimeUtils.dayFromDateValue(var22);
                  int var16 = DateTimeUtils.dayFromDateValue(var11);
                  if (var13 >= 0L) {
                     if (var15 < var16 || var15 == var16 && var21[1] < var8[1]) {
                        --var13;
                     }
                  } else if (var15 > var16 || var15 == var16 && var21[1] > var8[1]) {
                     ++var13;
                  }

                  boolean var17;
                  if (var13 < 0L) {
                     var17 = true;
                     var13 = -var13;
                  } else {
                     var17 = false;
                  }

                  var6 = ValueInterval.from(IntervalQualifier.MONTH, var17, var13, 0L);
               } else if (var4 == 17 && var5 == 17) {
                  var18 = DateTimeUtils.absoluteDayFromDateValue(((ValueDate)var2).getDateValue()) - DateTimeUtils.absoluteDayFromDateValue(((ValueDate)var3).getDateValue());
                  var9 = var18 < 0L;
                  if (var9) {
                     var18 = -var18;
                  }

                  var6 = ValueInterval.from(IntervalQualifier.DAY, var9, var18, 0L);
               } else {
                  var7 = nanosFromValue(var1, var2).subtract(nanosFromValue(var1, var3));
                  if (var4 == 21 || var5 == 21) {
                     var2 = var2.convertTo(TypeInfo.TYPE_TIMESTAMP_TZ, var1);
                     var3 = var3.convertTo(TypeInfo.TYPE_TIMESTAMP_TZ, var1);
                     var7 = var7.add(BigInteger.valueOf((long)(((ValueTimestampTimeZone)var3).getTimeZoneOffsetSeconds() - ((ValueTimestampTimeZone)var2).getTimeZoneOffsetSeconds()) * 1000000000L));
                  }

                  var6 = IntervalUtils.intervalFromAbsolute(IntervalQualifier.DAY_TO_SECOND, var7);
               }

               if (this.forcedType != null) {
                  var6 = ((Value)var6).castTo(this.forcedType, var1);
               }

               return (Value)var6;
            default:
               throw DbException.getInternalError("type=" + this.opType);
         }
      } else {
         return ValueNull.INSTANCE;
      }
   }

   private Value getDateTimeWithInterval(SessionLocal var1, Value var2, Value var3, int var4, int var5) {
      switch (var4) {
         case 17:
         case 20:
         case 21:
            if (DataType.isYearMonthIntervalType(var5)) {
               long var14 = IntervalUtils.intervalToAbsolute((ValueInterval)var3).longValue();
               if (this.opType == IntervalOperation.IntervalOpType.DATETIME_MINUS_INTERVAL) {
                  var14 = -var14;
               }

               return DateTimeFunction.dateadd(var1, 1, var14, var2);
            } else {
               BigInteger var13 = IntervalUtils.intervalToAbsolute((ValueInterval)var3);
               if (var4 == 17) {
                  BigInteger var15 = BigInteger.valueOf(DateTimeUtils.absoluteDayFromDateValue(((ValueDate)var2).getDateValue()));
                  var13 = var13.divide(IntervalUtils.NANOS_PER_DAY_BI);
                  BigInteger var16 = this.opType == IntervalOperation.IntervalOpType.DATETIME_PLUS_INTERVAL ? var15.add(var13) : var15.subtract(var13);
                  return ValueDate.fromDateValue(DateTimeUtils.dateValueFromAbsoluteDay(var16.longValue()));
               }

               long[] var7 = DateTimeUtils.dateAndTimeFromValue(var2, var1);
               long var8 = DateTimeUtils.absoluteDayFromDateValue(var7[0]);
               long var10 = var7[1];
               BigInteger[] var12 = var13.divideAndRemainder(IntervalUtils.NANOS_PER_DAY_BI);
               if (this.opType == IntervalOperation.IntervalOpType.DATETIME_PLUS_INTERVAL) {
                  var8 += var12[0].longValue();
                  var10 += var12[1].longValue();
               } else {
                  var8 -= var12[0].longValue();
                  var10 -= var12[1].longValue();
               }

               if (var10 >= 86400000000000L) {
                  var10 -= 86400000000000L;
                  ++var8;
               } else if (var10 < 0L) {
                  var10 += 86400000000000L;
                  --var8;
               }

               return DateTimeUtils.dateTimeToValue(var2, DateTimeUtils.dateValueFromAbsoluteDay(var8), var10);
            }
         case 18:
            if (DataType.isYearMonthIntervalType(var5)) {
               throw DbException.getInternalError("type=" + var5);
            }

            return ValueTime.fromNanos(this.getTimeWithInterval(var3, ((ValueTime)var2).getNanos()));
         case 19:
            if (DataType.isYearMonthIntervalType(var5)) {
               throw DbException.getInternalError("type=" + var5);
            }

            ValueTimeTimeZone var6 = (ValueTimeTimeZone)var2;
            return ValueTimeTimeZone.fromNanos(this.getTimeWithInterval(var3, var6.getNanos()), var6.getTimeZoneOffsetSeconds());
         default:
            throw DbException.getInternalError("type=" + this.opType);
      }
   }

   private long getTimeWithInterval(Value var1, long var2) {
      BigInteger var4 = BigInteger.valueOf(var2);
      BigInteger var5 = IntervalUtils.intervalToAbsolute((ValueInterval)var1);
      BigInteger var6 = this.opType == IntervalOperation.IntervalOpType.DATETIME_PLUS_INTERVAL ? var4.add(var5) : var4.subtract(var5);
      if (var6.signum() >= 0 && var6.compareTo(IntervalUtils.NANOS_PER_DAY_BI) < 0) {
         var2 = var6.longValue();
         return var2;
      } else {
         throw DbException.get(22003, (String)var6.toString());
      }
   }

   public Expression optimize(SessionLocal var1) {
      this.left = this.left.optimize(var1);
      this.right = this.right.optimize(var1);
      return (Expression)(this.left.isConstant() && this.right.isConstant() ? ValueExpression.get(this.getValue(var1)) : this);
   }

   public static enum IntervalOpType {
      INTERVAL_PLUS_INTERVAL,
      INTERVAL_MINUS_INTERVAL,
      INTERVAL_DIVIDE_INTERVAL,
      DATETIME_PLUS_INTERVAL,
      DATETIME_MINUS_INTERVAL,
      INTERVAL_MULTIPLY_NUMERIC,
      INTERVAL_DIVIDE_NUMERIC,
      DATETIME_MINUS_DATETIME;
   }
}
