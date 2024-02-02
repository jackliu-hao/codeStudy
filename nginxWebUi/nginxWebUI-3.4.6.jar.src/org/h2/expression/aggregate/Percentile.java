/*     */ package org.h2.expression.aggregate;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import org.h2.api.IntervalQualifier;
/*     */ import org.h2.command.query.QueryOrderBy;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionColumn;
/*     */ import org.h2.index.Cursor;
/*     */ import org.h2.index.Index;
/*     */ import org.h2.mode.DefaultNullOrdering;
/*     */ import org.h2.result.SearchRow;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.table.TableFilter;
/*     */ import org.h2.util.DateTimeUtils;
/*     */ import org.h2.util.IntervalUtils;
/*     */ import org.h2.value.CompareMode;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueDate;
/*     */ import org.h2.value.ValueInterval;
/*     */ import org.h2.value.ValueNull;
/*     */ import org.h2.value.ValueNumeric;
/*     */ import org.h2.value.ValueTime;
/*     */ import org.h2.value.ValueTimeTimeZone;
/*     */ import org.h2.value.ValueTimestamp;
/*     */ import org.h2.value.ValueTimestampTimeZone;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class Percentile
/*     */ {
/*  48 */   static final BigDecimal HALF = BigDecimal.valueOf(0.5D);
/*     */   
/*     */   private static boolean isNullsLast(DefaultNullOrdering paramDefaultNullOrdering, Index paramIndex) {
/*  51 */     return (paramDefaultNullOrdering.compareNull(true, (paramIndex.getIndexColumns()[0]).sortType) > 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static Index getColumnIndex(Database paramDatabase, Expression paramExpression) {
/*  63 */     DefaultNullOrdering defaultNullOrdering = paramDatabase.getDefaultNullOrdering();
/*  64 */     if (paramExpression instanceof ExpressionColumn) {
/*  65 */       ExpressionColumn expressionColumn = (ExpressionColumn)paramExpression;
/*  66 */       Column column = expressionColumn.getColumn();
/*  67 */       TableFilter tableFilter = expressionColumn.getTableFilter();
/*  68 */       if (tableFilter != null) {
/*  69 */         Table table = tableFilter.getTable();
/*  70 */         ArrayList<Index> arrayList = table.getIndexes();
/*  71 */         Index index = null;
/*  72 */         if (arrayList != null) {
/*  73 */           boolean bool = column.isNullable(); byte b; int i;
/*  74 */           for (b = 1, i = arrayList.size(); b < i; b++) {
/*  75 */             Index index1 = arrayList.get(b);
/*  76 */             if (index1.canFindNext())
/*     */             {
/*     */               
/*  79 */               if (index1.isFirstColumn(column))
/*     */               {
/*     */ 
/*     */                 
/*  83 */                 if (index == null || (index.getColumns()).length > (index1.getColumns()).length || (bool && 
/*  84 */                   isNullsLast(defaultNullOrdering, index) && 
/*  85 */                   !isNullsLast(defaultNullOrdering, index1)))
/*  86 */                   index = index1;  } 
/*     */             }
/*     */           } 
/*     */         } 
/*  90 */         return index;
/*     */       } 
/*     */     } 
/*  93 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static Value getValue(SessionLocal paramSessionLocal, Value[] paramArrayOfValue, int paramInt, ArrayList<QueryOrderBy> paramArrayList, BigDecimal paramBigDecimal, boolean paramBoolean) {
/*     */     int k;
/* 109 */     CompareMode compareMode = paramSessionLocal.getDatabase().getCompareMode();
/* 110 */     Arrays.sort(paramArrayOfValue, (Comparator<? super Value>)compareMode);
/* 111 */     int i = paramArrayOfValue.length;
/* 112 */     boolean bool = (paramArrayList != null && (((QueryOrderBy)paramArrayList.get(0)).sortType & 0x1) != 0) ? true : false;
/* 113 */     BigDecimal bigDecimal1 = BigDecimal.valueOf((i - 1)).multiply(paramBigDecimal);
/* 114 */     int j = bigDecimal1.intValue();
/* 115 */     BigDecimal bigDecimal2 = bigDecimal1.subtract(BigDecimal.valueOf(j));
/*     */     
/* 117 */     if (bigDecimal2.signum() == 0) {
/* 118 */       paramBoolean = false;
/* 119 */       k = j;
/*     */     } else {
/* 121 */       k = j + 1;
/* 122 */       if (!paramBoolean) {
/* 123 */         if (bigDecimal2.compareTo(HALF) > 0) {
/* 124 */           j = k;
/*     */         } else {
/* 126 */           k = j;
/*     */         } 
/*     */       }
/*     */     } 
/* 130 */     if (bool) {
/* 131 */       j = i - 1 - j;
/* 132 */       k = i - 1 - k;
/*     */     } 
/* 134 */     Value value = paramArrayOfValue[j];
/* 135 */     if (!paramBoolean) {
/* 136 */       return value;
/*     */     }
/* 138 */     return interpolate(value, paramArrayOfValue[k], bigDecimal2, paramInt, paramSessionLocal, compareMode);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static Value getFromIndex(SessionLocal paramSessionLocal, Expression paramExpression, int paramInt, ArrayList<QueryOrderBy> paramArrayList, BigDecimal paramBigDecimal, boolean paramBoolean) {
/*     */     long l3;
/* 154 */     Database database = paramSessionLocal.getDatabase();
/* 155 */     Index index = getColumnIndex(database, paramExpression);
/* 156 */     long l1 = index.getRowCount(paramSessionLocal);
/* 157 */     if (l1 == 0L) {
/* 158 */       return (Value)ValueNull.INSTANCE;
/*     */     }
/* 160 */     Cursor cursor = index.find(paramSessionLocal, null, null);
/* 161 */     cursor.next();
/* 162 */     int i = index.getColumns()[0].getColumnId();
/* 163 */     ExpressionColumn expressionColumn = (ExpressionColumn)paramExpression;
/* 164 */     if (expressionColumn.getColumn().isNullable()) {
/* 165 */       boolean bool1 = false;
/*     */ 
/*     */ 
/*     */       
/* 169 */       while (l1 > 0L) {
/* 170 */         SearchRow searchRow1 = cursor.getSearchRow();
/* 171 */         if (searchRow1 == null) {
/* 172 */           return (Value)ValueNull.INSTANCE;
/*     */         }
/* 174 */         if (searchRow1.getValue(i) == ValueNull.INSTANCE) {
/* 175 */           l1--;
/* 176 */           cursor.next();
/* 177 */           bool1 = true;
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 182 */       if (l1 == 0L) {
/* 183 */         return (Value)ValueNull.INSTANCE;
/*     */       }
/*     */ 
/*     */       
/* 187 */       if (!bool1 && isNullsLast(database.getDefaultNullOrdering(), index)) {
/* 188 */         TableFilter tableFilter = expressionColumn.getTableFilter();
/* 189 */         SearchRow searchRow1 = tableFilter.getTable().getTemplateSimpleRow(true);
/* 190 */         searchRow1.setValue(i, (Value)ValueNull.INSTANCE);
/* 191 */         Cursor cursor1 = index.find(paramSessionLocal, searchRow1, searchRow1);
/* 192 */         while (cursor1.next()) {
/* 193 */           l1--;
/*     */         }
/* 195 */         if (l1 <= 0L) {
/* 196 */           return (Value)ValueNull.INSTANCE;
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 201 */     boolean bool = (((paramArrayList != null) ? (((QueryOrderBy)paramArrayList.get(0)).sortType & 0x1) : 0) != ((index.getIndexColumns()[0]).sortType & 0x1)) ? true : false;
/* 202 */     BigDecimal bigDecimal1 = BigDecimal.valueOf(l1 - 1L).multiply(paramBigDecimal);
/* 203 */     long l2 = bigDecimal1.longValue();
/* 204 */     BigDecimal bigDecimal2 = bigDecimal1.subtract(BigDecimal.valueOf(l2));
/*     */     
/* 206 */     if (bigDecimal2.signum() == 0) {
/* 207 */       paramBoolean = false;
/* 208 */       l3 = l2;
/*     */     } else {
/* 210 */       l3 = l2 + 1L;
/* 211 */       if (!paramBoolean) {
/* 212 */         if (bigDecimal2.compareTo(HALF) > 0) {
/* 213 */           l2 = l3;
/*     */         } else {
/* 215 */           l3 = l2;
/*     */         } 
/*     */       }
/*     */     } 
/* 219 */     long l4 = bool ? (l1 - 1L - l3) : l2;
/* 220 */     for (byte b = 0; b < l4; b++) {
/* 221 */       cursor.next();
/*     */     }
/* 223 */     SearchRow searchRow = cursor.getSearchRow();
/* 224 */     if (searchRow == null) {
/* 225 */       return (Value)ValueNull.INSTANCE;
/*     */     }
/* 227 */     Value value = searchRow.getValue(i);
/* 228 */     if (value == ValueNull.INSTANCE) {
/* 229 */       return value;
/*     */     }
/* 231 */     if (paramBoolean) {
/* 232 */       cursor.next();
/* 233 */       searchRow = cursor.getSearchRow();
/* 234 */       if (searchRow == null) {
/* 235 */         return value;
/*     */       }
/* 237 */       Value value1 = searchRow.getValue(i);
/* 238 */       if (value1 == ValueNull.INSTANCE) {
/* 239 */         return value;
/*     */       }
/* 241 */       if (bool) {
/* 242 */         Value value2 = value;
/* 243 */         value = value1;
/* 244 */         value1 = value2;
/*     */       } 
/* 246 */       return interpolate(value, value1, bigDecimal2, paramInt, paramSessionLocal, database.getCompareMode());
/*     */     } 
/* 248 */     return value; } private static Value interpolate(Value paramValue1, Value paramValue2, BigDecimal paramBigDecimal, int paramInt, SessionLocal paramSessionLocal, CompareMode paramCompareMode) { ValueTime valueTime1; ValueTimeTimeZone valueTimeTimeZone1; ValueDate valueDate1; ValueTimestamp valueTimestamp1; ValueTimestampTimeZone valueTimestampTimeZone1; ValueTime valueTime2; ValueTimeTimeZone valueTimeTimeZone2; ValueDate valueDate2; ValueTimestamp valueTimestamp2; ValueTimestampTimeZone valueTimestampTimeZone2; BigDecimal bigDecimal1; BigDecimal bigDecimal2; BigDecimal bigDecimal4; BigInteger[] arrayOfBigInteger1; BigDecimal bigDecimal3; int j; long l1; int i; BigDecimal bigDecimal5; BigDecimal bigDecimal7; long l2; BigDecimal bigDecimal6;
/*     */     long l3;
/*     */     BigInteger[] arrayOfBigInteger2;
/*     */     long l4;
/*     */     long l5;
/* 253 */     if (paramValue1.compareTo(paramValue2, (CastDataProvider)paramSessionLocal, paramCompareMode) == 0) {
/* 254 */       return paramValue1;
/*     */     }
/* 256 */     switch (paramInt) {
/*     */       case 9:
/*     */       case 10:
/*     */       case 11:
/* 260 */         return (Value)ValueNumeric.get(
/* 261 */             interpolateDecimal(BigDecimal.valueOf(paramValue1.getInt()), BigDecimal.valueOf(paramValue2.getInt()), paramBigDecimal));
/*     */       case 12:
/* 263 */         return (Value)ValueNumeric.get(
/* 264 */             interpolateDecimal(BigDecimal.valueOf(paramValue1.getLong()), BigDecimal.valueOf(paramValue2.getLong()), paramBigDecimal));
/*     */       case 13:
/*     */       case 16:
/* 267 */         return (Value)ValueNumeric.get(interpolateDecimal(paramValue1.getBigDecimal(), paramValue2.getBigDecimal(), paramBigDecimal));
/*     */       case 14:
/*     */       case 15:
/* 270 */         return (Value)ValueNumeric.get(
/* 271 */             interpolateDecimal(
/* 272 */               BigDecimal.valueOf(paramValue1.getDouble()), BigDecimal.valueOf(paramValue2.getDouble()), paramBigDecimal));
/*     */       case 18:
/* 274 */         valueTime1 = (ValueTime)paramValue1; valueTime2 = (ValueTime)paramValue2;
/* 275 */         bigDecimal1 = BigDecimal.valueOf(valueTime1.getNanos());
/* 276 */         bigDecimal2 = BigDecimal.valueOf(valueTime2.getNanos());
/* 277 */         return (Value)ValueTime.fromNanos(interpolateDecimal(bigDecimal1, bigDecimal2, paramBigDecimal).longValue());
/*     */       
/*     */       case 19:
/* 280 */         valueTimeTimeZone1 = (ValueTimeTimeZone)paramValue1; valueTimeTimeZone2 = (ValueTimeTimeZone)paramValue2;
/* 281 */         bigDecimal1 = BigDecimal.valueOf(valueTimeTimeZone1.getNanos());
/* 282 */         bigDecimal2 = BigDecimal.valueOf(valueTimeTimeZone2.getNanos());
/*     */ 
/*     */         
/* 285 */         bigDecimal4 = BigDecimal.valueOf(valueTimeTimeZone1.getTimeZoneOffsetSeconds()).multiply(BigDecimal.ONE.subtract(paramBigDecimal)).add(BigDecimal.valueOf(valueTimeTimeZone2.getTimeZoneOffsetSeconds()).multiply(paramBigDecimal));
/* 286 */         j = bigDecimal4.intValue();
/* 287 */         bigDecimal5 = BigDecimal.valueOf(j);
/* 288 */         bigDecimal7 = interpolateDecimal(bigDecimal1, bigDecimal2, paramBigDecimal);
/* 289 */         if (bigDecimal4.compareTo(bigDecimal5) != 0) {
/* 290 */           bigDecimal7 = bigDecimal7.add(bigDecimal4
/* 291 */               .subtract(bigDecimal5).multiply(BigDecimal.valueOf(1000000000L)));
/*     */         }
/* 293 */         l3 = bigDecimal7.longValue();
/* 294 */         if (l3 < 0L) {
/* 295 */           l3 += 1000000000L;
/* 296 */           j++;
/* 297 */         } else if (l3 >= 86400000000000L) {
/* 298 */           l3 -= 1000000000L;
/* 299 */           j--;
/*     */         } 
/* 301 */         return (Value)ValueTimeTimeZone.fromNanos(l3, j);
/*     */       
/*     */       case 17:
/* 304 */         valueDate1 = (ValueDate)paramValue1; valueDate2 = (ValueDate)paramValue2;
/* 305 */         bigDecimal1 = BigDecimal.valueOf(DateTimeUtils.absoluteDayFromDateValue(valueDate1.getDateValue()));
/* 306 */         bigDecimal2 = BigDecimal.valueOf(DateTimeUtils.absoluteDayFromDateValue(valueDate2.getDateValue()));
/* 307 */         return (Value)ValueDate.fromDateValue(
/* 308 */             DateTimeUtils.dateValueFromAbsoluteDay(interpolateDecimal(bigDecimal1, bigDecimal2, paramBigDecimal).longValue()));
/*     */       
/*     */       case 20:
/* 311 */         valueTimestamp1 = (ValueTimestamp)paramValue1; valueTimestamp2 = (ValueTimestamp)paramValue2;
/* 312 */         bigDecimal1 = timestampToDecimal(valueTimestamp1.getDateValue(), valueTimestamp1.getTimeNanos());
/* 313 */         bigDecimal2 = timestampToDecimal(valueTimestamp2.getDateValue(), valueTimestamp2.getTimeNanos());
/*     */         
/* 315 */         arrayOfBigInteger1 = interpolateDecimal(bigDecimal1, bigDecimal2, paramBigDecimal).toBigInteger().divideAndRemainder(IntervalUtils.NANOS_PER_DAY_BI);
/* 316 */         l1 = arrayOfBigInteger1[0].longValue();
/* 317 */         l2 = arrayOfBigInteger1[1].longValue();
/* 318 */         if (l2 < 0L) {
/* 319 */           l2 += 86400000000000L;
/* 320 */           l1--;
/*     */         } 
/* 322 */         return (Value)ValueTimestamp.fromDateValueAndNanos(
/* 323 */             DateTimeUtils.dateValueFromAbsoluteDay(l1), l2);
/*     */       
/*     */       case 21:
/* 326 */         valueTimestampTimeZone1 = (ValueTimestampTimeZone)paramValue1; valueTimestampTimeZone2 = (ValueTimestampTimeZone)paramValue2;
/* 327 */         bigDecimal1 = timestampToDecimal(valueTimestampTimeZone1.getDateValue(), valueTimestampTimeZone1.getTimeNanos());
/* 328 */         bigDecimal2 = timestampToDecimal(valueTimestampTimeZone2.getDateValue(), valueTimestampTimeZone2.getTimeNanos());
/*     */ 
/*     */         
/* 331 */         bigDecimal3 = BigDecimal.valueOf(valueTimestampTimeZone1.getTimeZoneOffsetSeconds()).multiply(BigDecimal.ONE.subtract(paramBigDecimal)).add(BigDecimal.valueOf(valueTimestampTimeZone2.getTimeZoneOffsetSeconds()).multiply(paramBigDecimal));
/* 332 */         i = bigDecimal3.intValue();
/* 333 */         bigDecimal5 = BigDecimal.valueOf(i);
/* 334 */         bigDecimal6 = interpolateDecimal(bigDecimal1, bigDecimal2, paramBigDecimal);
/* 335 */         if (bigDecimal3.compareTo(bigDecimal5) != 0) {
/* 336 */           bigDecimal6 = bigDecimal6.add(bigDecimal3
/* 337 */               .subtract(bigDecimal5).multiply(BigDecimal.valueOf(1000000000L)));
/*     */         }
/* 339 */         arrayOfBigInteger2 = bigDecimal6.toBigInteger().divideAndRemainder(IntervalUtils.NANOS_PER_DAY_BI);
/* 340 */         l4 = arrayOfBigInteger2[0].longValue();
/* 341 */         l5 = arrayOfBigInteger2[1].longValue();
/* 342 */         if (l5 < 0L) {
/* 343 */           l5 += 86400000000000L;
/* 344 */           l4--;
/*     */         } 
/* 346 */         return (Value)ValueTimestampTimeZone.fromDateValueAndNanos(DateTimeUtils.dateValueFromAbsoluteDay(l4), l5, i);
/*     */ 
/*     */       
/*     */       case 22:
/*     */       case 23:
/*     */       case 24:
/*     */       case 25:
/*     */       case 26:
/*     */       case 27:
/*     */       case 28:
/*     */       case 29:
/*     */       case 30:
/*     */       case 31:
/*     */       case 32:
/*     */       case 33:
/*     */       case 34:
/* 362 */         return (Value)IntervalUtils.intervalFromAbsolute(IntervalQualifier.valueOf(paramInt - 22), 
/* 363 */             interpolateDecimal(new BigDecimal(IntervalUtils.intervalToAbsolute((ValueInterval)paramValue1)), new BigDecimal(
/* 364 */                 IntervalUtils.intervalToAbsolute((ValueInterval)paramValue2)), paramBigDecimal)
/* 365 */             .toBigInteger());
/*     */     } 
/*     */     
/* 368 */     return (paramBigDecimal.compareTo(HALF) > 0) ? paramValue2 : paramValue1; }
/*     */ 
/*     */ 
/*     */   
/*     */   private static BigDecimal timestampToDecimal(long paramLong1, long paramLong2) {
/* 373 */     return new BigDecimal(BigInteger.valueOf(DateTimeUtils.absoluteDayFromDateValue(paramLong1))
/* 374 */         .multiply(IntervalUtils.NANOS_PER_DAY_BI).add(BigInteger.valueOf(paramLong2)));
/*     */   }
/*     */   
/*     */   private static BigDecimal interpolateDecimal(BigDecimal paramBigDecimal1, BigDecimal paramBigDecimal2, BigDecimal paramBigDecimal3) {
/* 378 */     return paramBigDecimal1.multiply(BigDecimal.ONE.subtract(paramBigDecimal3)).add(paramBigDecimal2.multiply(paramBigDecimal3));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\aggregate\Percentile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */