/*     */ package org.h2.expression;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import org.h2.api.IntervalQualifier;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.function.DateTimeFunction;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.util.DateTimeUtils;
/*     */ import org.h2.util.IntervalUtils;
/*     */ import org.h2.value.DataType;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueDate;
/*     */ import org.h2.value.ValueInterval;
/*     */ import org.h2.value.ValueNull;
/*     */ import org.h2.value.ValueNumeric;
/*     */ import org.h2.value.ValueTime;
/*     */ import org.h2.value.ValueTimeTimeZone;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class IntervalOperation
/*     */   extends Operation2
/*     */ {
/*     */   private static final int INTERVAL_YEAR_DIGITS = 20;
/*     */   private static final int INTERVAL_DAY_DIGITS = 32;
/*     */   
/*     */   public enum IntervalOpType
/*     */   {
/*  47 */     INTERVAL_PLUS_INTERVAL,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  52 */     INTERVAL_MINUS_INTERVAL,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  57 */     INTERVAL_DIVIDE_INTERVAL,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  62 */     DATETIME_PLUS_INTERVAL,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  67 */     DATETIME_MINUS_INTERVAL,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  72 */     INTERVAL_MULTIPLY_NUMERIC,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  77 */     INTERVAL_DIVIDE_NUMERIC,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  82 */     DATETIME_MINUS_DATETIME;
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
/*     */   
/*  97 */   private static final TypeInfo INTERVAL_DIVIDE_INTERVAL_YEAR_TYPE = TypeInfo.getTypeInfo(13, 60L, 40, null);
/*     */ 
/*     */   
/* 100 */   private static final TypeInfo INTERVAL_DIVIDE_INTERVAL_DAY_TYPE = TypeInfo.getTypeInfo(13, 96L, 64, null);
/*     */   
/*     */   private final IntervalOpType opType;
/*     */   
/*     */   private TypeInfo forcedType;
/*     */ 
/*     */   
/*     */   private static BigInteger nanosFromValue(SessionLocal paramSessionLocal, Value paramValue) {
/* 108 */     long[] arrayOfLong = DateTimeUtils.dateAndTimeFromValue(paramValue, (CastDataProvider)paramSessionLocal);
/* 109 */     return BigInteger.valueOf(DateTimeUtils.absoluteDayFromDateValue(arrayOfLong[0])).multiply(IntervalUtils.NANOS_PER_DAY_BI)
/* 110 */       .add(BigInteger.valueOf(arrayOfLong[1]));
/*     */   }
/*     */   
/*     */   public IntervalOperation(IntervalOpType paramIntervalOpType, Expression paramExpression1, Expression paramExpression2, TypeInfo paramTypeInfo) {
/* 114 */     this(paramIntervalOpType, paramExpression1, paramExpression2);
/* 115 */     this.forcedType = paramTypeInfo;
/*     */   }
/*     */   
/*     */   public IntervalOperation(IntervalOpType paramIntervalOpType, Expression paramExpression1, Expression paramExpression2) {
/* 119 */     super(paramExpression1, paramExpression2);
/* 120 */     this.opType = paramIntervalOpType;
/* 121 */     int i = paramExpression1.getType().getValueType(), j = paramExpression2.getType().getValueType();
/* 122 */     switch (paramIntervalOpType) {
/*     */       case INTERVAL_PLUS_INTERVAL:
/*     */       case INTERVAL_MINUS_INTERVAL:
/* 125 */         this.type = TypeInfo.getTypeInfo(Value.getHigherOrder(i, j));
/*     */         break;
/*     */       case INTERVAL_DIVIDE_INTERVAL:
/* 128 */         this.type = DataType.isYearMonthIntervalType(i) ? INTERVAL_DIVIDE_INTERVAL_YEAR_TYPE : INTERVAL_DIVIDE_INTERVAL_DAY_TYPE;
/*     */         break;
/*     */       
/*     */       case DATETIME_PLUS_INTERVAL:
/*     */       case DATETIME_MINUS_INTERVAL:
/*     */       case INTERVAL_MULTIPLY_NUMERIC:
/*     */       case INTERVAL_DIVIDE_NUMERIC:
/* 135 */         this.type = paramExpression1.getType();
/*     */         break;
/*     */       case DATETIME_MINUS_DATETIME:
/* 138 */         if (this.forcedType != null) {
/* 139 */           this.type = this.forcedType; break;
/* 140 */         }  if ((i == 18 || i == 19) && (j == 18 || j == 19)) {
/* 141 */           this.type = TypeInfo.TYPE_INTERVAL_HOUR_TO_SECOND; break;
/* 142 */         }  if (i == 17 && j == 17) {
/* 143 */           this.type = TypeInfo.TYPE_INTERVAL_DAY; break;
/*     */         } 
/* 145 */         this.type = TypeInfo.TYPE_INTERVAL_DAY_TO_SECOND;
/*     */         break;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needParentheses() {
/* 152 */     return (this.forcedType == null);
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 157 */     if (this.forcedType != null) {
/* 158 */       getInnerSQL2(paramStringBuilder.append('('), paramInt);
/* 159 */       getForcedTypeSQL(paramStringBuilder.append(") "), this.forcedType);
/*     */     } else {
/* 161 */       getInnerSQL2(paramStringBuilder, paramInt);
/*     */     } 
/* 163 */     return paramStringBuilder;
/*     */   }
/*     */   
/*     */   private void getInnerSQL2(StringBuilder paramStringBuilder, int paramInt) {
/* 167 */     this.left.getSQL(paramStringBuilder, paramInt, 0).append(' ').append(getOperationToken()).append(' ');
/* 168 */     this.right.getSQL(paramStringBuilder, paramInt, 0);
/*     */   }
/*     */   
/*     */   static StringBuilder getForcedTypeSQL(StringBuilder paramStringBuilder, TypeInfo paramTypeInfo) {
/* 172 */     int i = (int)paramTypeInfo.getPrecision();
/* 173 */     int j = paramTypeInfo.getScale();
/* 174 */     return IntervalQualifier.valueOf(paramTypeInfo.getValueType() - 22).getTypeName(paramStringBuilder, (i == 2) ? -1 : i, (j == 6) ? -1 : j, true);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private char getOperationToken() {
/* 180 */     switch (this.opType) {
/*     */       case INTERVAL_PLUS_INTERVAL:
/*     */       case DATETIME_PLUS_INTERVAL:
/* 183 */         return '+';
/*     */       case INTERVAL_MINUS_INTERVAL:
/*     */       case DATETIME_MINUS_INTERVAL:
/*     */       case DATETIME_MINUS_DATETIME:
/* 187 */         return '-';
/*     */       case INTERVAL_MULTIPLY_NUMERIC:
/* 189 */         return '*';
/*     */       case INTERVAL_DIVIDE_INTERVAL:
/*     */       case INTERVAL_DIVIDE_NUMERIC:
/* 192 */         return '/';
/*     */     } 
/* 194 */     throw DbException.getInternalError("opType=" + this.opType); } public Value getValue(SessionLocal paramSessionLocal) { BigInteger bigInteger1;
/*     */     BigDecimal bigDecimal1;
/*     */     ValueInterval valueInterval;
/*     */     Value value3;
/*     */     BigInteger bigInteger2;
/*     */     BigDecimal bigDecimal2;
/* 200 */     Value value1 = this.left.getValue(paramSessionLocal);
/* 201 */     Value value2 = this.right.getValue(paramSessionLocal);
/* 202 */     if (value1 == ValueNull.INSTANCE || value2 == ValueNull.INSTANCE) {
/* 203 */       return (Value)ValueNull.INSTANCE;
/*     */     }
/* 205 */     int i = value1.getValueType(), j = value2.getValueType();
/* 206 */     switch (this.opType) {
/*     */       case INTERVAL_PLUS_INTERVAL:
/*     */       case INTERVAL_MINUS_INTERVAL:
/* 209 */         bigInteger1 = IntervalUtils.intervalToAbsolute((ValueInterval)value1);
/* 210 */         bigInteger2 = IntervalUtils.intervalToAbsolute((ValueInterval)value2);
/* 211 */         return (Value)IntervalUtils.intervalFromAbsolute(
/* 212 */             IntervalQualifier.valueOf(Value.getHigherOrder(i, j) - 22), (this.opType == IntervalOpType.INTERVAL_PLUS_INTERVAL) ? bigInteger1
/* 213 */             .add(bigInteger2) : bigInteger1.subtract(bigInteger2));
/*     */       
/*     */       case INTERVAL_DIVIDE_INTERVAL:
/* 216 */         return ValueNumeric.get(IntervalUtils.intervalToAbsolute((ValueInterval)value1))
/* 217 */           .divide((Value)ValueNumeric.get(IntervalUtils.intervalToAbsolute((ValueInterval)value2)), this.type);
/*     */       case DATETIME_PLUS_INTERVAL:
/*     */       case DATETIME_MINUS_INTERVAL:
/* 220 */         return getDateTimeWithInterval(paramSessionLocal, value1, value2, i, j);
/*     */       case INTERVAL_MULTIPLY_NUMERIC:
/*     */       case INTERVAL_DIVIDE_NUMERIC:
/* 223 */         bigDecimal1 = new BigDecimal(IntervalUtils.intervalToAbsolute((ValueInterval)value1));
/* 224 */         bigDecimal2 = value2.getBigDecimal();
/* 225 */         return (Value)IntervalUtils.intervalFromAbsolute(IntervalQualifier.valueOf(i - 22), ((this.opType == IntervalOpType.INTERVAL_MULTIPLY_NUMERIC) ? bigDecimal1
/* 226 */             .multiply(bigDecimal2) : bigDecimal1.divide(bigDecimal2))
/* 227 */             .toBigInteger());
/*     */ 
/*     */       
/*     */       case DATETIME_MINUS_DATETIME:
/* 231 */         if ((i == 18 || i == 19) && (j == 18 || j == 19)) {
/*     */           long l;
/* 233 */           if (i == 18 && j == 18) {
/* 234 */             l = ((ValueTime)value1).getNanos() - ((ValueTime)value2).getNanos();
/*     */           } else {
/* 236 */             ValueTimeTimeZone valueTimeTimeZone1 = (ValueTimeTimeZone)value1.convertTo(TypeInfo.TYPE_TIME_TZ, (CastDataProvider)paramSessionLocal);
/* 237 */             ValueTimeTimeZone valueTimeTimeZone2 = (ValueTimeTimeZone)value2.convertTo(TypeInfo.TYPE_TIME_TZ, (CastDataProvider)paramSessionLocal);
/*     */             
/* 239 */             l = valueTimeTimeZone1.getNanos() - valueTimeTimeZone2.getNanos() + (valueTimeTimeZone2.getTimeZoneOffsetSeconds() - valueTimeTimeZone1.getTimeZoneOffsetSeconds()) * 1000000000L;
/*     */           } 
/*     */           
/* 242 */           boolean bool = (l < 0L) ? true : false;
/* 243 */           if (bool) {
/* 244 */             l = -l;
/*     */           }
/* 246 */           valueInterval = ValueInterval.from(IntervalQualifier.HOUR_TO_SECOND, bool, l / 3600000000000L, l % 3600000000000L);
/*     */         }
/* 248 */         else if (this.forcedType != null && DataType.isYearMonthIntervalType(this.forcedType.getValueType())) {
/* 249 */           boolean bool; long[] arrayOfLong1 = DateTimeUtils.dateAndTimeFromValue(value1, (CastDataProvider)paramSessionLocal), arrayOfLong2 = DateTimeUtils.dateAndTimeFromValue(value2, (CastDataProvider)paramSessionLocal);
/*     */           
/* 251 */           long l1 = (i == 18 || i == 19) ? paramSessionLocal.currentTimestamp().getDateValue() : arrayOfLong1[0];
/*     */ 
/*     */           
/* 254 */           long l2 = (j == 18 || j == 19) ? paramSessionLocal.currentTimestamp().getDateValue() : arrayOfLong2[0];
/*     */ 
/*     */ 
/*     */           
/* 258 */           long l3 = 12L * (DateTimeUtils.yearFromDateValue(l1) - DateTimeUtils.yearFromDateValue(l2)) + DateTimeUtils.monthFromDateValue(l1) - DateTimeUtils.monthFromDateValue(l2);
/* 259 */           int k = DateTimeUtils.dayFromDateValue(l1);
/* 260 */           int m = DateTimeUtils.dayFromDateValue(l2);
/* 261 */           if (l3 >= 0L) {
/* 262 */             if (k < m || (k == m && arrayOfLong1[1] < arrayOfLong2[1])) {
/* 263 */               l3--;
/*     */             }
/* 265 */           } else if (k > m || (k == m && arrayOfLong1[1] > arrayOfLong2[1])) {
/* 266 */             l3++;
/*     */           } 
/*     */           
/* 269 */           if (l3 < 0L) {
/* 270 */             bool = true;
/* 271 */             l3 = -l3;
/*     */           } else {
/* 273 */             bool = false;
/*     */           } 
/* 275 */           valueInterval = ValueInterval.from(IntervalQualifier.MONTH, bool, l3, 0L);
/* 276 */         } else if (i == 17 && j == 17) {
/*     */           
/* 278 */           long l = DateTimeUtils.absoluteDayFromDateValue(((ValueDate)value1).getDateValue()) - DateTimeUtils.absoluteDayFromDateValue(((ValueDate)value2).getDateValue());
/* 279 */           boolean bool = (l < 0L) ? true : false;
/* 280 */           if (bool) {
/* 281 */             l = -l;
/*     */           }
/* 283 */           valueInterval = ValueInterval.from(IntervalQualifier.DAY, bool, l, 0L);
/*     */         } else {
/* 285 */           BigInteger bigInteger = nanosFromValue(paramSessionLocal, value1).subtract(nanosFromValue(paramSessionLocal, value2));
/* 286 */           if (i == 21 || j == 21) {
/* 287 */             value1 = value1.convertTo(TypeInfo.TYPE_TIMESTAMP_TZ, (CastDataProvider)paramSessionLocal);
/* 288 */             value2 = value2.convertTo(TypeInfo.TYPE_TIMESTAMP_TZ, (CastDataProvider)paramSessionLocal);
/* 289 */             bigInteger = bigInteger.add(BigInteger.valueOf((((ValueTimestampTimeZone)value2).getTimeZoneOffsetSeconds() - ((ValueTimestampTimeZone)value1)
/* 290 */                   .getTimeZoneOffsetSeconds()) * 1000000000L));
/*     */           } 
/* 292 */           valueInterval = IntervalUtils.intervalFromAbsolute(IntervalQualifier.DAY_TO_SECOND, bigInteger);
/*     */         } 
/* 294 */         if (this.forcedType != null) {
/* 295 */           value3 = valueInterval.castTo(this.forcedType, (CastDataProvider)paramSessionLocal);
/*     */         }
/* 297 */         return value3;
/*     */     } 
/*     */     
/* 300 */     throw DbException.getInternalError("type=" + this.opType); } private Value getDateTimeWithInterval(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2, int paramInt1, int paramInt2) { ValueTimeTimeZone valueTimeTimeZone; BigInteger bigInteger; long[] arrayOfLong;
/*     */     long l1;
/*     */     long l2;
/*     */     BigInteger[] arrayOfBigInteger;
/* 304 */     switch (paramInt1) {
/*     */       case 18:
/* 306 */         if (DataType.isYearMonthIntervalType(paramInt2)) {
/* 307 */           throw DbException.getInternalError("type=" + paramInt2);
/*     */         }
/* 309 */         return (Value)ValueTime.fromNanos(getTimeWithInterval(paramValue2, ((ValueTime)paramValue1).getNanos()));
/*     */       case 19:
/* 311 */         if (DataType.isYearMonthIntervalType(paramInt2)) {
/* 312 */           throw DbException.getInternalError("type=" + paramInt2);
/*     */         }
/* 314 */         valueTimeTimeZone = (ValueTimeTimeZone)paramValue1;
/* 315 */         return (Value)ValueTimeTimeZone.fromNanos(getTimeWithInterval(paramValue2, valueTimeTimeZone.getNanos()), valueTimeTimeZone.getTimeZoneOffsetSeconds());
/*     */       
/*     */       case 17:
/*     */       case 20:
/*     */       case 21:
/* 320 */         if (DataType.isYearMonthIntervalType(paramInt2)) {
/* 321 */           long l = IntervalUtils.intervalToAbsolute((ValueInterval)paramValue2).longValue();
/* 322 */           if (this.opType == IntervalOpType.DATETIME_MINUS_INTERVAL) {
/* 323 */             l = -l;
/*     */           }
/* 325 */           return DateTimeFunction.dateadd(paramSessionLocal, 1, l, paramValue1);
/*     */         } 
/* 327 */         bigInteger = IntervalUtils.intervalToAbsolute((ValueInterval)paramValue2);
/* 328 */         if (paramInt1 == 17) {
/* 329 */           BigInteger bigInteger1 = BigInteger.valueOf(DateTimeUtils.absoluteDayFromDateValue(((ValueDate)paramValue1).getDateValue()));
/* 330 */           bigInteger = bigInteger.divide(IntervalUtils.NANOS_PER_DAY_BI);
/* 331 */           BigInteger bigInteger2 = (this.opType == IntervalOpType.DATETIME_PLUS_INTERVAL) ? bigInteger1.add(bigInteger) : bigInteger1.subtract(bigInteger);
/* 332 */           return (Value)ValueDate.fromDateValue(DateTimeUtils.dateValueFromAbsoluteDay(bigInteger2.longValue()));
/*     */         } 
/* 334 */         arrayOfLong = DateTimeUtils.dateAndTimeFromValue(paramValue1, (CastDataProvider)paramSessionLocal);
/* 335 */         l1 = DateTimeUtils.absoluteDayFromDateValue(arrayOfLong[0]);
/* 336 */         l2 = arrayOfLong[1];
/* 337 */         arrayOfBigInteger = bigInteger.divideAndRemainder(IntervalUtils.NANOS_PER_DAY_BI);
/* 338 */         if (this.opType == IntervalOpType.DATETIME_PLUS_INTERVAL) {
/* 339 */           l1 += arrayOfBigInteger[0].longValue();
/* 340 */           l2 += arrayOfBigInteger[1].longValue();
/*     */         } else {
/* 342 */           l1 -= arrayOfBigInteger[0].longValue();
/* 343 */           l2 -= arrayOfBigInteger[1].longValue();
/*     */         } 
/* 345 */         if (l2 >= 86400000000000L) {
/* 346 */           l2 -= 86400000000000L;
/* 347 */           l1++;
/* 348 */         } else if (l2 < 0L) {
/* 349 */           l2 += 86400000000000L;
/* 350 */           l1--;
/*     */         } 
/* 352 */         return DateTimeUtils.dateTimeToValue(paramValue1, DateTimeUtils.dateValueFromAbsoluteDay(l1), l2);
/*     */     } 
/*     */ 
/*     */     
/* 356 */     throw DbException.getInternalError("type=" + this.opType); }
/*     */ 
/*     */   
/*     */   private long getTimeWithInterval(Value paramValue, long paramLong) {
/* 360 */     BigInteger bigInteger1 = BigInteger.valueOf(paramLong);
/* 361 */     BigInteger bigInteger2 = IntervalUtils.intervalToAbsolute((ValueInterval)paramValue);
/* 362 */     BigInteger bigInteger3 = (this.opType == IntervalOpType.DATETIME_PLUS_INTERVAL) ? bigInteger1.add(bigInteger2) : bigInteger1.subtract(bigInteger2);
/* 363 */     if (bigInteger3.signum() < 0 || bigInteger3.compareTo(IntervalUtils.NANOS_PER_DAY_BI) >= 0) {
/* 364 */       throw DbException.get(22003, bigInteger3.toString());
/*     */     }
/* 366 */     paramLong = bigInteger3.longValue();
/* 367 */     return paramLong;
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 372 */     this.left = this.left.optimize(paramSessionLocal);
/* 373 */     this.right = this.right.optimize(paramSessionLocal);
/* 374 */     if (this.left.isConstant() && this.right.isConstant()) {
/* 375 */       return ValueExpression.get(getValue(paramSessionLocal));
/*     */     }
/* 377 */     return this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\IntervalOperation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */