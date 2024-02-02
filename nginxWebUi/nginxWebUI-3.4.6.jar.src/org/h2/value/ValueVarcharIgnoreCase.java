/*    */ package org.h2.value;
/*    */ 
/*    */ import org.h2.engine.CastDataProvider;
/*    */ import org.h2.engine.SysProperties;
/*    */ import org.h2.util.StringUtils;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class ValueVarcharIgnoreCase
/*    */   extends ValueStringBase
/*    */ {
/* 17 */   private static final ValueVarcharIgnoreCase EMPTY = new ValueVarcharIgnoreCase("");
/*    */ 
/*    */   
/*    */   private int hash;
/*    */ 
/*    */ 
/*    */   
/*    */   private ValueVarcharIgnoreCase(String paramString) {
/* 25 */     super(paramString);
/*    */   }
/*    */ 
/*    */   
/*    */   public int getValueType() {
/* 30 */     return 4;
/*    */   }
/*    */ 
/*    */   
/*    */   public int compareTypeSafe(Value paramValue, CompareMode paramCompareMode, CastDataProvider paramCastDataProvider) {
/* 35 */     return paramCompareMode.compareString(this.value, ((ValueStringBase)paramValue).value, true);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object paramObject) {
/* 40 */     return (paramObject instanceof ValueVarcharIgnoreCase && this.value
/* 41 */       .equalsIgnoreCase(((ValueVarcharIgnoreCase)paramObject).value));
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 46 */     if (this.hash == 0)
/*    */     {
/* 48 */       this.hash = this.value.toUpperCase().hashCode();
/*    */     }
/* 50 */     return this.hash;
/*    */   }
/*    */ 
/*    */   
/*    */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 55 */     if ((paramInt & 0x4) == 0) {
/* 56 */       return StringUtils.quoteStringSQL(paramStringBuilder.append("CAST("), this.value).append(" AS VARCHAR_IGNORECASE(")
/* 57 */         .append(this.value.length()).append("))");
/*    */     }
/* 59 */     return StringUtils.quoteStringSQL(paramStringBuilder, this.value);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static ValueVarcharIgnoreCase get(String paramString) {
/* 70 */     int i = paramString.length();
/* 71 */     if (i == 0) {
/* 72 */       return EMPTY;
/*    */     }
/* 74 */     ValueVarcharIgnoreCase valueVarcharIgnoreCase1 = new ValueVarcharIgnoreCase(StringUtils.cache(paramString));
/* 75 */     if (i > SysProperties.OBJECT_CACHE_MAX_PER_ELEMENT_SIZE) {
/* 76 */       return valueVarcharIgnoreCase1;
/*    */     }
/* 78 */     ValueVarcharIgnoreCase valueVarcharIgnoreCase2 = (ValueVarcharIgnoreCase)Value.cache(valueVarcharIgnoreCase1);
/*    */ 
/*    */     
/* 81 */     if (valueVarcharIgnoreCase2.value.equals(paramString)) {
/* 82 */       return valueVarcharIgnoreCase2;
/*    */     }
/* 84 */     return valueVarcharIgnoreCase1;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ValueVarcharIgnoreCase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */