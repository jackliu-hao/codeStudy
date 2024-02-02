/*    */ package com.fasterxml.jackson.annotation;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum OptBoolean
/*    */ {
/* 23 */   TRUE,
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 29 */   FALSE,
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 37 */   DEFAULT;
/*    */   
/*    */   public Boolean asBoolean() {
/* 40 */     if (this == DEFAULT) return null; 
/* 41 */     return (this == TRUE) ? Boolean.TRUE : Boolean.FALSE;
/*    */   }
/*    */   
/*    */   public boolean asPrimitive() {
/* 45 */     return (this == TRUE);
/*    */   }
/*    */   
/*    */   public static OptBoolean fromBoolean(Boolean b) {
/* 49 */     if (b == null) {
/* 50 */       return DEFAULT;
/*    */     }
/* 52 */     return b.booleanValue() ? TRUE : FALSE;
/*    */   }
/*    */   
/*    */   public static boolean equals(Boolean b1, Boolean b2) {
/* 56 */     if (b1 == null) {
/* 57 */       return (b2 == null);
/*    */     }
/* 59 */     return b1.equals(b2);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\fasterxml\jackson\annotation\OptBoolean.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */