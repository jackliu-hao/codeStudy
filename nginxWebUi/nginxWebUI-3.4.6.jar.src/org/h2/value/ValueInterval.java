/*     */ package org.h2.value;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import java.math.RoundingMode;
/*     */ import org.h2.api.Interval;
/*     */ import org.h2.api.IntervalQualifier;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.util.DateTimeUtils;
/*     */ import org.h2.util.IntervalUtils;
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
/*     */ public final class ValueInterval
/*     */   extends Value
/*     */ {
/*     */   public static final int DEFAULT_PRECISION = 2;
/*     */   public static final int MAXIMUM_PRECISION = 18;
/*     */   public static final int DEFAULT_SCALE = 6;
/*     */   public static final int MAXIMUM_SCALE = 9;
/*  48 */   private static final long[] MULTIPLIERS = new long[] { 1000000000L, 12L, 24L, 1440L, 86400000000000L, 60L, 3600000000000L, 60000000000L };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int valueType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final boolean negative;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final long leading;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final long remaining;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueInterval from(IntervalQualifier paramIntervalQualifier, boolean paramBoolean, long paramLong1, long paramLong2) {
/*  89 */     paramBoolean = IntervalUtils.validateInterval(paramIntervalQualifier, paramBoolean, paramLong1, paramLong2);
/*  90 */     return 
/*  91 */       (ValueInterval)Value.cache(new ValueInterval(paramIntervalQualifier.ordinal() + 22, paramBoolean, paramLong1, paramLong2));
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
/*     */   
/*     */   public static int getDisplaySize(int paramInt1, int paramInt2, int paramInt3) {
/* 108 */     switch (paramInt1) {
/*     */ 
/*     */       
/*     */       case 22:
/*     */       case 25:
/* 113 */         return 17 + paramInt2;
/*     */       
/*     */       case 23:
/* 116 */         return 18 + paramInt2;
/*     */       
/*     */       case 24:
/* 119 */         return 16 + paramInt2;
/*     */       
/*     */       case 26:
/* 122 */         return 19 + paramInt2;
/*     */ 
/*     */       
/*     */       case 27:
/* 126 */         return (paramInt3 > 0) ? (20 + paramInt2 + paramInt3) : (19 + paramInt2);
/*     */       
/*     */       case 28:
/* 129 */         return 29 + paramInt2;
/*     */       
/*     */       case 29:
/* 132 */         return 27 + paramInt2;
/*     */       
/*     */       case 30:
/* 135 */         return 32 + paramInt2;
/*     */ 
/*     */       
/*     */       case 31:
/* 139 */         return (paramInt3 > 0) ? (36 + paramInt2 + paramInt3) : (35 + paramInt2);
/*     */       
/*     */       case 32:
/* 142 */         return 30 + paramInt2;
/*     */ 
/*     */       
/*     */       case 33:
/* 146 */         return (paramInt3 > 0) ? (34 + paramInt2 + paramInt3) : (33 + paramInt2);
/*     */ 
/*     */       
/*     */       case 34:
/* 150 */         return (paramInt3 > 0) ? (33 + paramInt2 + paramInt3) : (32 + paramInt2);
/*     */     } 
/* 152 */     throw DbException.getUnsupportedException(Integer.toString(paramInt1));
/*     */   }
/*     */ 
/*     */   
/*     */   private ValueInterval(int paramInt, boolean paramBoolean, long paramLong1, long paramLong2) {
/* 157 */     this.valueType = paramInt;
/* 158 */     this.negative = paramBoolean;
/* 159 */     this.leading = paramLong1;
/* 160 */     this.remaining = paramLong2;
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 165 */     return IntervalUtils.appendInterval(paramStringBuilder, getQualifier(), this.negative, this.leading, this.remaining);
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/* 170 */     return TypeInfo.getTypeInfo(this.valueType);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getValueType() {
/* 175 */     return this.valueType;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMemory() {
/* 181 */     return 48;
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
/*     */   boolean checkPrecision(long paramLong) {
/* 193 */     if (paramLong < 18L) {
/* 194 */       long l2; for (long l1 = this.leading, l3 = 0L; l1 >= l2; l2 *= 10L) {
/* 195 */         if (++l3 > paramLong) {
/* 196 */           return false;
/*     */         }
/*     */       } 
/*     */     } 
/* 200 */     return true;
/*     */   }
/*     */   
/*     */   ValueInterval setPrecisionAndScale(TypeInfo paramTypeInfo, Object paramObject) {
/* 204 */     int i = paramTypeInfo.getScale();
/* 205 */     ValueInterval valueInterval = this;
/* 206 */     if (i < 9) {
/*     */       long l1;
/* 208 */       switch (this.valueType) {
/*     */         case 27:
/* 210 */           l1 = 1000000000L;
/*     */           break;
/*     */         case 31:
/* 213 */           l1 = 86400000000000L;
/*     */           break;
/*     */         case 33:
/* 216 */           l1 = 3600000000000L;
/*     */           break;
/*     */         case 34:
/* 219 */           l1 = 60000000000L;
/*     */           break;
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
/*     */         default:
/* 235 */           if (!valueInterval.checkPrecision(paramTypeInfo.getPrecision())) {
/* 236 */             throw valueInterval.getValueTooLongException(paramTypeInfo, paramObject);
/*     */           }
/* 238 */           return valueInterval;
/*     */       }  long l2 = this.leading; long l3 = DateTimeUtils.convertScale(this.remaining, i, (l2 == 999999999999999999L) ? l1 : Long.MAX_VALUE); if (l3 != this.remaining) { if (l3 >= l1) {
/*     */           l2++; l3 -= l1;
/*     */         }  valueInterval = from(valueInterval.getQualifier(), valueInterval.isNegative(), l2, l3); }
/*     */     
/* 243 */     }  } public String getString() { return IntervalUtils.appendInterval(new StringBuilder(), getQualifier(), this.negative, this.leading, this.remaining)
/* 244 */       .toString(); }
/*     */ 
/*     */ 
/*     */   
/*     */   public long getLong() {
/* 249 */     long l = this.leading;
/* 250 */     if (this.valueType >= 27 && this.remaining != 0L && this.remaining >= MULTIPLIERS[this.valueType - 27] >> 1L)
/*     */     {
/* 252 */       l++;
/*     */     }
/* 254 */     return this.negative ? -l : l;
/*     */   }
/*     */ 
/*     */   
/*     */   public BigDecimal getBigDecimal() {
/* 259 */     if (this.valueType < 27 || this.remaining == 0L) {
/* 260 */       return BigDecimal.valueOf(this.negative ? -this.leading : this.leading);
/*     */     }
/* 262 */     BigDecimal bigDecimal1 = BigDecimal.valueOf(MULTIPLIERS[this.valueType - 27]);
/*     */ 
/*     */     
/* 265 */     BigDecimal bigDecimal2 = BigDecimal.valueOf(this.leading).add(BigDecimal.valueOf(this.remaining).divide(bigDecimal1, bigDecimal1.precision(), RoundingMode.HALF_DOWN)).stripTrailingZeros();
/* 266 */     return this.negative ? bigDecimal2.negate() : bigDecimal2;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getFloat() {
/* 271 */     if (this.valueType < 27 || this.remaining == 0L) {
/* 272 */       return this.negative ? (float)-this.leading : (float)this.leading;
/*     */     }
/* 274 */     return getBigDecimal().floatValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public double getDouble() {
/* 279 */     if (this.valueType < 27 || this.remaining == 0L) {
/* 280 */       return this.negative ? -this.leading : this.leading;
/*     */     }
/* 282 */     return getBigDecimal().doubleValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Interval getInterval() {
/* 291 */     return new Interval(getQualifier(), this.negative, this.leading, this.remaining);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IntervalQualifier getQualifier() {
/* 300 */     return IntervalQualifier.valueOf(this.valueType - 22);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isNegative() {
/* 309 */     return this.negative;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getLeading() {
/* 319 */     return this.leading;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getRemaining() {
/* 329 */     return this.remaining;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 335 */     int i = 1;
/* 336 */     i = 31 * i + this.valueType;
/* 337 */     i = 31 * i + (this.negative ? 1231 : 1237);
/* 338 */     i = 31 * i + (int)(this.leading ^ this.leading >>> 32L);
/* 339 */     i = 31 * i + (int)(this.remaining ^ this.remaining >>> 32L);
/* 340 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object paramObject) {
/* 345 */     if (this == paramObject) {
/* 346 */       return true;
/*     */     }
/* 348 */     if (!(paramObject instanceof ValueInterval)) {
/* 349 */       return false;
/*     */     }
/* 351 */     ValueInterval valueInterval = (ValueInterval)paramObject;
/* 352 */     return (this.valueType == valueInterval.valueType && this.negative == valueInterval.negative && this.leading == valueInterval.leading && this.remaining == valueInterval.remaining);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int compareTypeSafe(Value paramValue, CompareMode paramCompareMode, CastDataProvider paramCastDataProvider) {
/* 358 */     ValueInterval valueInterval = (ValueInterval)paramValue;
/* 359 */     if (this.negative != valueInterval.negative) {
/* 360 */       return this.negative ? -1 : 1;
/*     */     }
/* 362 */     int i = Long.compare(this.leading, valueInterval.leading);
/* 363 */     if (i == 0) {
/* 364 */       i = Long.compare(this.remaining, valueInterval.remaining);
/*     */     }
/* 366 */     return this.negative ? -i : i;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSignum() {
/* 371 */     return this.negative ? -1 : ((this.leading == 0L && this.remaining == 0L) ? 0 : 1);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value add(Value paramValue) {
/* 376 */     return IntervalUtils.intervalFromAbsolute(getQualifier(), 
/* 377 */         IntervalUtils.intervalToAbsolute(this).add(IntervalUtils.intervalToAbsolute((ValueInterval)paramValue)));
/*     */   }
/*     */ 
/*     */   
/*     */   public Value subtract(Value paramValue) {
/* 382 */     return IntervalUtils.intervalFromAbsolute(getQualifier(), 
/* 383 */         IntervalUtils.intervalToAbsolute(this).subtract(IntervalUtils.intervalToAbsolute((ValueInterval)paramValue)));
/*     */   }
/*     */ 
/*     */   
/*     */   public Value negate() {
/* 388 */     if (this.leading == 0L && this.remaining == 0L) {
/* 389 */       return this;
/*     */     }
/* 391 */     return Value.cache(new ValueInterval(this.valueType, !this.negative, this.leading, this.remaining));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ValueInterval.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */