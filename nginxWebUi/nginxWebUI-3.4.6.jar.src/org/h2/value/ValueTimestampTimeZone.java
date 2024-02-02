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
/*     */ public final class ValueTimestampTimeZone
/*     */   extends Value
/*     */ {
/*     */   public static final int DEFAULT_PRECISION = 32;
/*     */   public static final int MAXIMUM_PRECISION = 35;
/*     */   private final long dateValue;
/*     */   private final long timeNanos;
/*     */   private final int timeZoneOffsetSeconds;
/*     */   
/*     */   private ValueTimestampTimeZone(long paramLong1, long paramLong2, int paramInt) {
/*  46 */     if (paramLong1 < -511999999967L || paramLong1 > 512000000415L) {
/*  47 */       throw new IllegalArgumentException("dateValue out of range " + paramLong1);
/*     */     }
/*  49 */     if (paramLong2 < 0L || paramLong2 >= 86400000000000L) {
/*  50 */       throw new IllegalArgumentException("timeNanos out of range " + paramLong2);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  58 */     if (paramInt < -64800 || paramInt > 64800)
/*     */     {
/*  60 */       throw new IllegalArgumentException("timeZoneOffsetSeconds out of range " + paramInt);
/*     */     }
/*     */     
/*  63 */     this.dateValue = paramLong1;
/*  64 */     this.timeNanos = paramLong2;
/*  65 */     this.timeZoneOffsetSeconds = paramInt;
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
/*     */   public static ValueTimestampTimeZone fromDateValueAndNanos(long paramLong1, long paramLong2, int paramInt) {
/*  79 */     return (ValueTimestampTimeZone)Value.cache(new ValueTimestampTimeZone(paramLong1, paramLong2, paramInt));
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
/*     */   public static ValueTimestampTimeZone parse(String paramString, CastDataProvider paramCastDataProvider) {
/*     */     try {
/*  96 */       return (ValueTimestampTimeZone)DateTimeUtils.parseTimestamp(paramString, paramCastDataProvider, true);
/*  97 */     } catch (Exception exception) {
/*  98 */       throw DbException.get(22007, exception, new String[] { "TIMESTAMP WITH TIME ZONE", paramString });
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
/* 109 */     return this.dateValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getTimeNanos() {
/* 118 */     return this.timeNanos;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getTimeZoneOffsetSeconds() {
/* 127 */     return this.timeZoneOffsetSeconds;
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/* 132 */     return TypeInfo.TYPE_TIMESTAMP_TZ;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getValueType() {
/* 137 */     return 21;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMemory() {
/* 143 */     return 40;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getString() {
/* 148 */     return toString(new StringBuilder(35), false).toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getISOString() {
/* 157 */     return toString(new StringBuilder(35), true).toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 162 */     return toString(paramStringBuilder.append("TIMESTAMP WITH TIME ZONE '"), false).append('\'');
/*     */   }
/*     */   
/*     */   private StringBuilder toString(StringBuilder paramStringBuilder, boolean paramBoolean) {
/* 166 */     DateTimeUtils.appendDate(paramStringBuilder, this.dateValue).append(paramBoolean ? 84 : 32);
/* 167 */     DateTimeUtils.appendTime(paramStringBuilder, this.timeNanos);
/* 168 */     return DateTimeUtils.appendTimeZone(paramStringBuilder, this.timeZoneOffsetSeconds);
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTypeSafe(Value paramValue, CompareMode paramCompareMode, CastDataProvider paramCastDataProvider) {
/* 173 */     ValueTimestampTimeZone valueTimestampTimeZone = (ValueTimestampTimeZone)paramValue;
/*     */ 
/*     */     
/* 176 */     long l1 = this.dateValue;
/* 177 */     long l2 = this.timeNanos - this.timeZoneOffsetSeconds * 1000000000L;
/* 178 */     if (l2 < 0L) {
/* 179 */       l2 += 86400000000000L;
/* 180 */       l1 = DateTimeUtils.decrementDateValue(l1);
/* 181 */     } else if (l2 >= 86400000000000L) {
/* 182 */       l2 -= 86400000000000L;
/* 183 */       l1 = DateTimeUtils.incrementDateValue(l1);
/*     */     } 
/* 185 */     long l3 = valueTimestampTimeZone.dateValue;
/* 186 */     long l4 = valueTimestampTimeZone.timeNanos - valueTimestampTimeZone.timeZoneOffsetSeconds * 1000000000L;
/* 187 */     if (l4 < 0L) {
/* 188 */       l4 += 86400000000000L;
/* 189 */       l3 = DateTimeUtils.decrementDateValue(l3);
/* 190 */     } else if (l4 >= 86400000000000L) {
/* 191 */       l4 -= 86400000000000L;
/* 192 */       l3 = DateTimeUtils.incrementDateValue(l3);
/*     */     } 
/* 194 */     int i = Long.compare(l1, l3);
/* 195 */     if (i != 0) {
/* 196 */       return i;
/*     */     }
/* 198 */     return Long.compare(l2, l4);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object paramObject) {
/* 203 */     if (this == paramObject)
/* 204 */       return true; 
/* 205 */     if (!(paramObject instanceof ValueTimestampTimeZone)) {
/* 206 */       return false;
/*     */     }
/* 208 */     ValueTimestampTimeZone valueTimestampTimeZone = (ValueTimestampTimeZone)paramObject;
/* 209 */     return (this.dateValue == valueTimestampTimeZone.dateValue && this.timeNanos == valueTimestampTimeZone.timeNanos && this.timeZoneOffsetSeconds == valueTimestampTimeZone.timeZoneOffsetSeconds);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 215 */     return (int)(this.dateValue ^ this.dateValue >>> 32L ^ this.timeNanos ^ this.timeNanos >>> 32L ^ this.timeZoneOffsetSeconds);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ValueTimestampTimeZone.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */