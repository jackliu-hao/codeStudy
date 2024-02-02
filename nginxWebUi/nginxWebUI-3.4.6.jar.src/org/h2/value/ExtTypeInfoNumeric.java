/*    */ package org.h2.value;
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
/*    */ public final class ExtTypeInfoNumeric
/*    */   extends ExtTypeInfo
/*    */ {
/* 16 */   public static final ExtTypeInfoNumeric DECIMAL = new ExtTypeInfoNumeric();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 23 */     return paramStringBuilder.append("DECIMAL");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ExtTypeInfoNumeric.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */