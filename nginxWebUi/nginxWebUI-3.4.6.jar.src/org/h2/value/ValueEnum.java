/*    */ package org.h2.value;
/*    */ 
/*    */ import org.h2.util.StringUtils;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class ValueEnum
/*    */   extends ValueEnumBase
/*    */ {
/*    */   private final ExtTypeInfoEnum enumerators;
/*    */   
/*    */   ValueEnum(ExtTypeInfoEnum paramExtTypeInfoEnum, String paramString, int paramInt) {
/* 18 */     super(paramString, paramInt);
/* 19 */     this.enumerators = paramExtTypeInfoEnum;
/*    */   }
/*    */ 
/*    */   
/*    */   public TypeInfo getType() {
/* 24 */     return this.enumerators.getType();
/*    */   }
/*    */   
/*    */   public ExtTypeInfoEnum getEnumerators() {
/* 28 */     return this.enumerators;
/*    */   }
/*    */ 
/*    */   
/*    */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 33 */     if ((paramInt & 0x4) == 0) {
/* 34 */       StringUtils.quoteStringSQL(paramStringBuilder.append("CAST("), this.label).append(" AS ");
/* 35 */       return this.enumerators.getType().getSQL(paramStringBuilder, paramInt).append(')');
/*    */     } 
/* 37 */     return StringUtils.quoteStringSQL(paramStringBuilder, this.label);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ValueEnum.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */