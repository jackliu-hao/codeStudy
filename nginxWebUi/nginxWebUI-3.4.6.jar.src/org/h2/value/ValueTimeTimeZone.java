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
/*     */ public final class ValueTimeTimeZone
/*     */   extends Value
/*     */ {
/*     */   public static final int DEFAULT_PRECISION = 14;
/*     */   public static final int MAXIMUM_PRECISION = 24;
/*     */   private final long nanos;
/*     */   private final int timeZoneOffsetSeconds;
/*     */   
/*     */   private ValueTimeTimeZone(long paramLong, int paramInt) {
/*  46 */     this.nanos = paramLong;
/*  47 */     this.timeZoneOffsetSeconds = paramInt;
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
/*     */   public static ValueTimeTimeZone fromNanos(long paramLong, int paramInt) {
/*  60 */     if (paramLong < 0L || paramLong >= 86400000000000L) {
/*  61 */       throw DbException.get(22007, new String[] { "TIME WITH TIME ZONE", 
/*  62 */             DateTimeUtils.appendTime(new StringBuilder(), paramLong).toString() });
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  69 */     if (paramInt < -64800 || paramInt > 64800) {
/*  70 */       throw new IllegalArgumentException("timeZoneOffsetSeconds " + paramInt);
/*     */     }
/*  72 */     return (ValueTimeTimeZone)Value.cache(new ValueTimeTimeZone(paramLong, paramInt));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueTimeTimeZone parse(String paramString) {
/*     */     try {
/*  84 */       return DateTimeUtils.parseTimeWithTimeZone(paramString, null);
/*  85 */     } catch (Exception exception) {
/*  86 */       throw DbException.get(22007, exception, new String[] { "TIME WITH TIME ZONE", paramString });
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getNanos() {
/*  94 */     return this.nanos;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getTimeZoneOffsetSeconds() {
/* 103 */     return this.timeZoneOffsetSeconds;
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/* 108 */     return TypeInfo.TYPE_TIME_TZ;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getValueType() {
/* 113 */     return 19;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMemory() {
/* 118 */     return 32;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getString() {
/* 123 */     return toString(new StringBuilder(24)).toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 128 */     return toString(paramStringBuilder.append("TIME WITH TIME ZONE '")).append('\'');
/*     */   }
/*     */   
/*     */   private StringBuilder toString(StringBuilder paramStringBuilder) {
/* 132 */     return DateTimeUtils.appendTimeZone(DateTimeUtils.appendTime(paramStringBuilder, this.nanos), this.timeZoneOffsetSeconds);
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTypeSafe(Value paramValue, CompareMode paramCompareMode, CastDataProvider paramCastDataProvider) {
/* 137 */     ValueTimeTimeZone valueTimeTimeZone = (ValueTimeTimeZone)paramValue;
/* 138 */     return Long.compare(this.nanos - this.timeZoneOffsetSeconds * 1000000000L, valueTimeTimeZone.nanos - valueTimeTimeZone.timeZoneOffsetSeconds * 1000000000L);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object paramObject) {
/* 144 */     if (this == paramObject)
/* 145 */       return true; 
/* 146 */     if (!(paramObject instanceof ValueTimeTimeZone)) {
/* 147 */       return false;
/*     */     }
/* 149 */     ValueTimeTimeZone valueTimeTimeZone = (ValueTimeTimeZone)paramObject;
/* 150 */     return (this.nanos == valueTimeTimeZone.nanos && this.timeZoneOffsetSeconds == valueTimeTimeZone.timeZoneOffsetSeconds);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 155 */     return (int)(this.nanos ^ this.nanos >>> 32L ^ this.timeZoneOffsetSeconds);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ValueTimeTimeZone.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */