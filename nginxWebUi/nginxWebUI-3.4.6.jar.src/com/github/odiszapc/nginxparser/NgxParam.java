/*    */ package com.github.odiszapc.nginxparser;
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
/*    */ public class NgxParam
/*    */   extends NgxAbstractEntry
/*    */ {
/*    */   public NgxParam() {
/* 24 */     super(new String[0]);
/*    */   }
/*    */   public String toString() {
/* 27 */     String s = super.toString();
/* 28 */     if (s.isEmpty()) {
/* 29 */       return s;
/*    */     }
/* 31 */     return s + ";";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\odiszapc\nginxparser\NgxParam.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */