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
/*    */ public final class ValueChar
/*    */   extends ValueStringBase
/*    */ {
/*    */   private ValueChar(String paramString) {
/* 18 */     super(paramString);
/*    */   }
/*    */ 
/*    */   
/*    */   public int getValueType() {
/* 23 */     return 1;
/*    */   }
/*    */ 
/*    */   
/*    */   public int compareTypeSafe(Value paramValue, CompareMode paramCompareMode, CastDataProvider paramCastDataProvider) {
/* 28 */     return paramCompareMode.compareString(convertToChar().getString(), paramValue.convertToChar().getString(), false);
/*    */   }
/*    */ 
/*    */   
/*    */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 33 */     if ((paramInt & 0x4) == 0) {
/* 34 */       int i = this.value.length();
/* 35 */       return StringUtils.quoteStringSQL(paramStringBuilder.append("CAST("), this.value).append(" AS CHAR(")
/* 36 */         .append((i > 0) ? i : 1).append("))");
/*    */     } 
/* 38 */     return StringUtils.quoteStringSQL(paramStringBuilder, this.value);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static ValueChar get(String paramString) {
/* 48 */     ValueChar valueChar = new ValueChar(StringUtils.cache(paramString));
/* 49 */     if (paramString.length() > SysProperties.OBJECT_CACHE_MAX_PER_ELEMENT_SIZE) {
/* 50 */       return valueChar;
/*    */     }
/* 52 */     return (ValueChar)Value.cache(valueChar);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ValueChar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */