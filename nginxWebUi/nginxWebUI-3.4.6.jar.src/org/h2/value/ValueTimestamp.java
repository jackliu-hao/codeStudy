/*     */ package org.h2.value;
/*     */ 
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.util.DateTimeUtils;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ValueTimestamp
/*     */   extends Value
/*     */ {
/*     */   public static final int DEFAULT_PRECISION = 26;
/*     */   public static final int MAXIMUM_PRECISION = 29;
/*     */   public static final int DEFAULT_SCALE = 6;
/*     */   public static final int MAXIMUM_SCALE = 9;
/*     */   private final long dateValue;
/*     */   private final long timeNanos;
/*     */   
/*     */   private ValueTimestamp(long paramLong1, long paramLong2) {
/*  51 */     if (paramLong1 < -511999999967L || paramLong1 > 512000000415L) {
/*  52 */       throw new IllegalArgumentException("dateValue out of range " + paramLong1);
/*     */     }
/*  54 */     if (paramLong2 < 0L || paramLong2 >= 86400000000000L) {
/*  55 */       throw new IllegalArgumentException("timeNanos out of range " + paramLong2);
/*     */     }
/*  57 */     this.dateValue = paramLong1;
/*  58 */     this.timeNanos = paramLong2;
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
/*     */   public static ValueTimestamp fromDateValueAndNanos(long paramLong1, long paramLong2) {
/*  70 */     return (ValueTimestamp)Value.cache(new ValueTimestamp(paramLong1, paramLong2));
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
/*     */   public static ValueTimestamp parse(String paramString, CastDataProvider paramCastDataProvider) {
/*     */     try {
/*  86 */       return (ValueTimestamp)DateTimeUtils.parseTimestamp(paramString, paramCastDataProvider, false);
/*  87 */     } catch (Exception exception) {
/*  88 */       throw DbException.get(22007, exception, new String[] { "TIMESTAMP", paramString });
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getDateValue() {
/*  99 */     return this.dateValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getTimeNanos() {
/* 108 */     return this.timeNanos;
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/* 113 */     return TypeInfo.TYPE_TIMESTAMP;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getValueType() {
/* 118 */     return 20;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMemory() {
/* 123 */     return 32;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getString() {
/* 128 */     return toString(new StringBuilder(29), false).toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getISOString() {
/* 137 */     return toString(new StringBuilder(29), true).toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 142 */     return toString(paramStringBuilder.append("TIMESTAMP '"), false).append('\'');
/*     */   }
/*     */   
/*     */   private StringBuilder toString(StringBuilder paramStringBuilder, boolean paramBoolean) {
/* 146 */     DateTimeUtils.appendDate(paramStringBuilder, this.dateValue).append(paramBoolean ? 84 : 32);
/* 147 */     return DateTimeUtils.appendTime(paramStringBuilder, this.timeNanos);
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTypeSafe(Value paramValue, CompareMode paramCompareMode, CastDataProvider paramCastDataProvider) {
/* 152 */     ValueTimestamp valueTimestamp = (ValueTimestamp)paramValue;
/* 153 */     int i = Long.compare(this.dateValue, valueTimestamp.dateValue);
/* 154 */     if (i != 0) {
/* 155 */       return i;
/*     */     }
/* 157 */     return Long.compare(this.timeNanos, valueTimestamp.timeNanos);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object paramObject) {
/* 162 */     if (this == paramObject)
/* 163 */       return true; 
/* 164 */     if (!(paramObject instanceof ValueTimestamp)) {
/* 165 */       return false;
/*     */     }
/* 167 */     ValueTimestamp valueTimestamp = (ValueTimestamp)paramObject;
/* 168 */     return (this.dateValue == valueTimestamp.dateValue && this.timeNanos == valueTimestamp.timeNanos);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 173 */     return (int)(this.dateValue ^ this.dateValue >>> 32L ^ this.timeNanos ^ this.timeNanos >>> 32L);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value add(Value paramValue) {
/* 178 */     ValueTimestamp valueTimestamp = (ValueTimestamp)paramValue;
/*     */     
/* 180 */     long l1 = DateTimeUtils.absoluteDayFromDateValue(this.dateValue) + DateTimeUtils.absoluteDayFromDateValue(valueTimestamp.dateValue);
/* 181 */     long l2 = this.timeNanos + valueTimestamp.timeNanos;
/* 182 */     if (l2 >= 86400000000000L) {
/* 183 */       l2 -= 86400000000000L;
/* 184 */       l1++;
/*     */     } 
/* 186 */     return fromDateValueAndNanos(DateTimeUtils.dateValueFromAbsoluteDay(l1), l2);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value subtract(Value paramValue) {
/* 191 */     ValueTimestamp valueTimestamp = (ValueTimestamp)paramValue;
/*     */     
/* 193 */     long l1 = DateTimeUtils.absoluteDayFromDateValue(this.dateValue) - DateTimeUtils.absoluteDayFromDateValue(valueTimestamp.dateValue);
/* 194 */     long l2 = this.timeNanos - valueTimestamp.timeNanos;
/* 195 */     if (l2 < 0L) {
/* 196 */       l2 += 86400000000000L;
/* 197 */       l1--;
/*     */     } 
/* 199 */     return fromDateValueAndNanos(DateTimeUtils.dateValueFromAbsoluteDay(l1), l2);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ValueTimestamp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */