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
/*     */ public final class ValueTime
/*     */   extends Value
/*     */ {
/*     */   public static final int DEFAULT_PRECISION = 8;
/*     */   public static final int MAXIMUM_PRECISION = 18;
/*     */   public static final int DEFAULT_SCALE = 0;
/*     */   public static final int MAXIMUM_SCALE = 9;
/*     */   private final long nanos;
/*     */   
/*     */   private ValueTime(long paramLong) {
/*  49 */     this.nanos = paramLong;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueTime fromNanos(long paramLong) {
/*  59 */     if (paramLong < 0L || paramLong >= 86400000000000L) {
/*  60 */       throw DbException.get(22007, new String[] { "TIME", 
/*  61 */             DateTimeUtils.appendTime(new StringBuilder(), paramLong).toString() });
/*     */     }
/*  63 */     return (ValueTime)Value.cache(new ValueTime(paramLong));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueTime parse(String paramString) {
/*     */     try {
/*  74 */       return fromNanos(DateTimeUtils.parseTimeNanos(paramString, 0, paramString.length()));
/*  75 */     } catch (Exception exception) {
/*  76 */       throw DbException.get(22007, exception, new String[] { "TIME", paramString });
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getNanos() {
/*  85 */     return this.nanos;
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/*  90 */     return TypeInfo.TYPE_TIME;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getValueType() {
/*  95 */     return 18;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getString() {
/* 100 */     return DateTimeUtils.appendTime(new StringBuilder(18), this.nanos).toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 105 */     return DateTimeUtils.appendTime(paramStringBuilder.append("TIME '"), this.nanos).append('\'');
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTypeSafe(Value paramValue, CompareMode paramCompareMode, CastDataProvider paramCastDataProvider) {
/* 110 */     return Long.compare(this.nanos, ((ValueTime)paramValue).nanos);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object paramObject) {
/* 115 */     return (this == paramObject || (paramObject instanceof ValueTime && this.nanos == ((ValueTime)paramObject).nanos));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 120 */     return (int)(this.nanos ^ this.nanos >>> 32L);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value add(Value paramValue) {
/* 125 */     ValueTime valueTime = (ValueTime)paramValue;
/* 126 */     return fromNanos(this.nanos + valueTime.getNanos());
/*     */   }
/*     */ 
/*     */   
/*     */   public Value subtract(Value paramValue) {
/* 131 */     ValueTime valueTime = (ValueTime)paramValue;
/* 132 */     return fromNanos(this.nanos - valueTime.getNanos());
/*     */   }
/*     */ 
/*     */   
/*     */   public Value multiply(Value paramValue) {
/* 137 */     return fromNanos((long)(this.nanos * paramValue.getDouble()));
/*     */   }
/*     */ 
/*     */   
/*     */   public Value divide(Value paramValue, TypeInfo paramTypeInfo) {
/* 142 */     return fromNanos((long)(this.nanos / paramValue.getDouble()));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ValueTime.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */