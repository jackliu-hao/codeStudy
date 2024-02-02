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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class ValueVarchar
/*    */   extends ValueStringBase
/*    */ {
/* 21 */   public static final ValueVarchar EMPTY = new ValueVarchar("");
/*    */   
/*    */   private ValueVarchar(String paramString) {
/* 24 */     super(paramString);
/*    */   }
/*    */ 
/*    */   
/*    */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 29 */     return StringUtils.quoteStringSQL(paramStringBuilder, this.value);
/*    */   }
/*    */ 
/*    */   
/*    */   public int getValueType() {
/* 34 */     return 2;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Value get(String paramString) {
/* 44 */     return get(paramString, null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Value get(String paramString, CastDataProvider paramCastDataProvider) {
/* 55 */     if (paramString.isEmpty()) {
/* 56 */       return (paramCastDataProvider != null && (paramCastDataProvider.getMode()).treatEmptyStringsAsNull) ? ValueNull.INSTANCE : EMPTY;
/*    */     }
/* 58 */     ValueVarchar valueVarchar = new ValueVarchar(StringUtils.cache(paramString));
/* 59 */     if (paramString.length() > SysProperties.OBJECT_CACHE_MAX_PER_ELEMENT_SIZE) {
/* 60 */       return valueVarchar;
/*    */     }
/* 62 */     return Value.cache(valueVarchar);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ValueVarchar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */