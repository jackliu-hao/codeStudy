package org.h2.expression.aggregate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import org.h2.api.IntervalQualifier;
import org.h2.command.query.QueryOrderBy;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.index.Cursor;
import org.h2.index.Index;
import org.h2.mode.DefaultNullOrdering;
import org.h2.result.SearchRow;
import org.h2.table.Column;
import org.h2.table.Table;
import org.h2.table.TableFilter;
import org.h2.util.DateTimeUtils;
import org.h2.util.IntervalUtils;
import org.h2.value.CompareMode;
import org.h2.value.Value;
import org.h2.value.ValueDate;
import org.h2.value.ValueInterval;
import org.h2.value.ValueNull;
import org.h2.value.ValueNumeric;
import org.h2.value.ValueTime;
import org.h2.value.ValueTimeTimeZone;
import org.h2.value.ValueTimestamp;
import org.h2.value.ValueTimestampTimeZone;

final class Percentile {
   static final BigDecimal HALF = BigDecimal.valueOf(0.5);

   private static boolean isNullsLast(DefaultNullOrdering var0, Index var1) {
      return var0.compareNull(true, var1.getIndexColumns()[0].sortType) > 0;
   }

   static Index getColumnIndex(Database var0, Expression var1) {
      DefaultNullOrdering var2 = var0.getDefaultNullOrdering();
      if (var1 instanceof ExpressionColumn) {
         ExpressionColumn var3 = (ExpressionColumn)var1;
         Column var4 = var3.getColumn();
         TableFilter var5 = var3.getTableFilter();
         if (var5 != null) {
            Table var6 = var5.getTable();
            ArrayList var7 = var6.getIndexes();
            Index var8 = null;
            if (var7 != null) {
               boolean var9 = var4.isNullable();
               int var10 = 1;

               for(int var11 = var7.size(); var10 < var11; ++var10) {
                  Index var12 = (Index)var7.get(var10);
                  if (var12.canFindNext() && var12.isFirstColumn(var4) && (var8 == null || var8.getColumns().length > var12.getColumns().length || var9 && isNullsLast(var2, var8) && !isNullsLast(var2, var12))) {
                     var8 = var12;
                  }
               }
            }

            return var8;
         }
      }

      return null;
   }

   static Value getValue(SessionLocal var0, Value[] var1, int var2, ArrayList<QueryOrderBy> var3, BigDecimal var4, boolean var5) {
      CompareMode var6 = var0.getDatabase().getCompareMode();
      Arrays.sort(var1, var6);
      int var7 = var1.length;
      boolean var8 = var3 != null && (((QueryOrderBy)var3.get(0)).sortType & 1) != 0;
      BigDecimal var9 = BigDecimal.valueOf((long)(var7 - 1)).multiply(var4);
      int var10 = var9.intValue();
      BigDecimal var11 = var9.subtract(BigDecimal.valueOf((long)var10));
      int var12;
      if (var11.signum() == 0) {
         var5 = false;
         var12 = var10;
      } else {
         var12 = var10 + 1;
         if (!var5) {
            if (var11.compareTo(HALF) > 0) {
               var10 = var12;
            } else {
               var12 = var10;
            }
         }
      }

      if (var8) {
         var10 = var7 - 1 - var10;
         var12 = var7 - 1 - var12;
      }

      Value var13 = var1[var10];
      return !var5 ? var13 : interpolate(var13, var1[var12], var11, var2, var0, var6);
   }

   static Value getFromIndex(SessionLocal var0, Expression var1, int var2, ArrayList<QueryOrderBy> var3, BigDecimal var4, boolean var5) {
      Database var6 = var0.getDatabase();
      Index var7 = getColumnIndex(var6, var1);
      long var8 = var7.getRowCount(var0);
      if (var8 == 0L) {
         return ValueNull.INSTANCE;
      } else {
         Cursor var10 = var7.find(var0, (SearchRow)null, (SearchRow)null);
         var10.next();
         int var11 = var7.getColumns()[0].getColumnId();
         ExpressionColumn var12 = (ExpressionColumn)var1;
         boolean var13;
         if (var12.getColumn().isNullable()) {
            for(var13 = false; var8 > 0L; var13 = true) {
               SearchRow var14 = var10.getSearchRow();
               if (var14 == null) {
                  return ValueNull.INSTANCE;
               }

               if (var14.getValue(var11) != ValueNull.INSTANCE) {
                  break;
               }

               --var8;
               var10.next();
            }

            if (var8 == 0L) {
               return ValueNull.INSTANCE;
            }

            if (!var13 && isNullsLast(var6.getDefaultNullOrdering(), var7)) {
               TableFilter var15 = var12.getTableFilter();
               SearchRow var16 = var15.getTable().getTemplateSimpleRow(true);
               var16.setValue(var11, ValueNull.INSTANCE);

               for(Cursor var17 = var7.find(var0, var16, var16); var17.next(); --var8) {
               }

               if (var8 <= 0L) {
                  return ValueNull.INSTANCE;
               }
            }
         }

         var13 = (var3 != null ? ((QueryOrderBy)var3.get(0)).sortType & 1 : 0) != (var7.getIndexColumns()[0].sortType & 1);
         BigDecimal var26 = BigDecimal.valueOf(var8 - 1L).multiply(var4);
         long var27 = var26.longValue();
         BigDecimal var28 = var26.subtract(BigDecimal.valueOf(var27));
         long var18;
         if (var28.signum() == 0) {
            var5 = false;
            var18 = var27;
         } else {
            var18 = var27 + 1L;
            if (!var5) {
               if (var28.compareTo(HALF) > 0) {
                  var27 = var18;
               } else {
                  var18 = var27;
               }
            }
         }

         long var20 = var13 ? var8 - 1L - var18 : var27;

         for(int var22 = 0; (long)var22 < var20; ++var22) {
            var10.next();
         }

         SearchRow var29 = var10.getSearchRow();
         if (var29 == null) {
            return ValueNull.INSTANCE;
         } else {
            Value var23 = var29.getValue(var11);
            if (var23 == ValueNull.INSTANCE) {
               return var23;
            } else if (var5) {
               var10.next();
               var29 = var10.getSearchRow();
               if (var29 == null) {
                  return var23;
               } else {
                  Value var24 = var29.getValue(var11);
                  if (var24 == ValueNull.INSTANCE) {
                     return var23;
                  } else {
                     if (var13) {
                        Value var25 = var23;
                        var23 = var24;
                        var24 = var25;
                     }

                     return interpolate(var23, var24, var28, var2, var0, var6.getCompareMode());
                  }
               }
            } else {
               return var23;
            }
         }
      }
   }

   private static Value interpolate(Value var0, Value var1, BigDecimal var2, int var3, SessionLocal var4, CompareMode var5) {
      if (var0.compareTo(var1, var4, var5) == 0) {
         return var0;
      } else {
         BigDecimal var8;
         BigDecimal var9;
         BigDecimal var10;
         int var11;
         BigDecimal var12;
         BigDecimal var13;
         switch (var3) {
            case 9:
            case 10:
            case 11:
               return ValueNumeric.get(interpolateDecimal(BigDecimal.valueOf((long)var0.getInt()), BigDecimal.valueOf((long)var1.getInt()), var2));
            case 12:
               return ValueNumeric.get(interpolateDecimal(BigDecimal.valueOf(var0.getLong()), BigDecimal.valueOf(var1.getLong()), var2));
            case 13:
            case 16:
               return ValueNumeric.get(interpolateDecimal(var0.getBigDecimal(), var1.getBigDecimal(), var2));
            case 14:
            case 15:
               return ValueNumeric.get(interpolateDecimal(BigDecimal.valueOf(var0.getDouble()), BigDecimal.valueOf(var1.getDouble()), var2));
            case 17:
               ValueDate var22 = (ValueDate)var0;
               ValueDate var26 = (ValueDate)var1;
               var8 = BigDecimal.valueOf(DateTimeUtils.absoluteDayFromDateValue(var22.getDateValue()));
               var9 = BigDecimal.valueOf(DateTimeUtils.absoluteDayFromDateValue(var26.getDateValue()));
               return ValueDate.fromDateValue(DateTimeUtils.dateValueFromAbsoluteDay(interpolateDecimal(var8, var9, var2).longValue()));
            case 18:
               ValueTime var21 = (ValueTime)var0;
               ValueTime var25 = (ValueTime)var1;
               var8 = BigDecimal.valueOf(var21.getNanos());
               var9 = BigDecimal.valueOf(var25.getNanos());
               return ValueTime.fromNanos(interpolateDecimal(var8, var9, var2).longValue());
            case 19:
               ValueTimeTimeZone var20 = (ValueTimeTimeZone)var0;
               ValueTimeTimeZone var24 = (ValueTimeTimeZone)var1;
               var8 = BigDecimal.valueOf(var20.getNanos());
               var9 = BigDecimal.valueOf(var24.getNanos());
               var10 = BigDecimal.valueOf((long)var20.getTimeZoneOffsetSeconds()).multiply(BigDecimal.ONE.subtract(var2)).add(BigDecimal.valueOf((long)var24.getTimeZoneOffsetSeconds()).multiply(var2));
               var11 = var10.intValue();
               var12 = BigDecimal.valueOf((long)var11);
               var13 = interpolateDecimal(var8, var9, var2);
               if (var10.compareTo(var12) != 0) {
                  var13 = var13.add(var10.subtract(var12).multiply(BigDecimal.valueOf(1000000000L)));
               }

               long var30 = var13.longValue();
               if (var30 < 0L) {
                  var30 += 1000000000L;
                  ++var11;
               } else if (var30 >= 86400000000000L) {
                  var30 -= 1000000000L;
                  --var11;
               }

               return ValueTimeTimeZone.fromNanos(var30, var11);
            case 20:
               ValueTimestamp var19 = (ValueTimestamp)var0;
               ValueTimestamp var23 = (ValueTimestamp)var1;
               var8 = timestampToDecimal(var19.getDateValue(), var19.getTimeNanos());
               var9 = timestampToDecimal(var23.getDateValue(), var23.getTimeNanos());
               BigInteger[] var27 = interpolateDecimal(var8, var9, var2).toBigInteger().divideAndRemainder(IntervalUtils.NANOS_PER_DAY_BI);
               long var28 = var27[0].longValue();
               long var29 = var27[1].longValue();
               if (var29 < 0L) {
                  var29 += 86400000000000L;
                  --var28;
               }

               return ValueTimestamp.fromDateValueAndNanos(DateTimeUtils.dateValueFromAbsoluteDay(var28), var29);
            case 21:
               ValueTimestampTimeZone var6 = (ValueTimestampTimeZone)var0;
               ValueTimestampTimeZone var7 = (ValueTimestampTimeZone)var1;
               var8 = timestampToDecimal(var6.getDateValue(), var6.getTimeNanos());
               var9 = timestampToDecimal(var7.getDateValue(), var7.getTimeNanos());
               var10 = BigDecimal.valueOf((long)var6.getTimeZoneOffsetSeconds()).multiply(BigDecimal.ONE.subtract(var2)).add(BigDecimal.valueOf((long)var7.getTimeZoneOffsetSeconds()).multiply(var2));
               var11 = var10.intValue();
               var12 = BigDecimal.valueOf((long)var11);
               var13 = interpolateDecimal(var8, var9, var2);
               if (var10.compareTo(var12) != 0) {
                  var13 = var13.add(var10.subtract(var12).multiply(BigDecimal.valueOf(1000000000L)));
               }

               BigInteger[] var14 = var13.toBigInteger().divideAndRemainder(IntervalUtils.NANOS_PER_DAY_BI);
               long var15 = var14[0].longValue();
               long var17 = var14[1].longValue();
               if (var17 < 0L) {
                  var17 += 86400000000000L;
                  --var15;
               }

               return ValueTimestampTimeZone.fromDateValueAndNanos(DateTimeUtils.dateValueFromAbsoluteDay(var15), var17, var11);
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
               return IntervalUtils.intervalFromAbsolute(IntervalQualifier.valueOf(var3 - 22), interpolateDecimal(new BigDecimal(IntervalUtils.intervalToAbsolute((ValueInterval)var0)), new BigDecimal(IntervalUtils.intervalToAbsolute((ValueInterval)var1)), var2).toBigInteger());
            default:
               return var2.compareTo(HALF) > 0 ? var1 : var0;
         }
      }
   }

   private static BigDecimal timestampToDecimal(long var0, long var2) {
      return new BigDecimal(BigInteger.valueOf(DateTimeUtils.absoluteDayFromDateValue(var0)).multiply(IntervalUtils.NANOS_PER_DAY_BI).add(BigInteger.valueOf(var2)));
   }

   private static BigDecimal interpolateDecimal(BigDecimal var0, BigDecimal var1, BigDecimal var2) {
      return var0.multiply(BigDecimal.ONE.subtract(var2)).add(var1.multiply(var2));
   }

   private Percentile() {
   }
}
