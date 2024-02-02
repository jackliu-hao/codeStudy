/*    */ package org.h2.value;
/*    */ 
/*    */ import org.h2.engine.CastDataProvider;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.util.DateTimeUtils;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class ValueDate
/*    */   extends Value
/*    */ {
/*    */   public static final int PRECISION = 10;
/*    */   private final long dateValue;
/*    */   
/*    */   private ValueDate(long paramLong) {
/* 27 */     if (paramLong < -511999999967L || paramLong > 512000000415L) {
/* 28 */       throw new IllegalArgumentException("dateValue out of range " + paramLong);
/*    */     }
/* 30 */     this.dateValue = paramLong;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static ValueDate fromDateValue(long paramLong) {
/* 40 */     return (ValueDate)Value.cache(new ValueDate(paramLong));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static ValueDate parse(String paramString) {
/*    */     try {
/* 51 */       return fromDateValue(DateTimeUtils.parseDateValue(paramString, 0, paramString.length()));
/* 52 */     } catch (Exception exception) {
/* 53 */       throw DbException.get(22007, exception, new String[] { "DATE", paramString });
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public long getDateValue() {
/* 59 */     return this.dateValue;
/*    */   }
/*    */ 
/*    */   
/*    */   public TypeInfo getType() {
/* 64 */     return TypeInfo.TYPE_DATE;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getValueType() {
/* 69 */     return 17;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getString() {
/* 74 */     return DateTimeUtils.appendDate(new StringBuilder(10), this.dateValue).toString();
/*    */   }
/*    */ 
/*    */   
/*    */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 79 */     return DateTimeUtils.appendDate(paramStringBuilder.append("DATE '"), this.dateValue).append('\'');
/*    */   }
/*    */ 
/*    */   
/*    */   public int compareTypeSafe(Value paramValue, CompareMode paramCompareMode, CastDataProvider paramCastDataProvider) {
/* 84 */     return Long.compare(this.dateValue, ((ValueDate)paramValue).dateValue);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object paramObject) {
/* 89 */     return (this == paramObject || (paramObject instanceof ValueDate && this.dateValue == ((ValueDate)paramObject).dateValue));
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 94 */     return (int)(this.dateValue ^ this.dateValue >>> 32L);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ValueDate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */