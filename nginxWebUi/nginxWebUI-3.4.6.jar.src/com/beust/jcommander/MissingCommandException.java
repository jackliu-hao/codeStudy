/*    */ package com.beust.jcommander;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MissingCommandException
/*    */   extends ParameterException
/*    */ {
/*    */   public MissingCommandException(String string) {
/* 30 */     super(string);
/*    */   }
/*    */   
/*    */   public MissingCommandException(Throwable t) {
/* 34 */     super(t);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\beust\jcommander\MissingCommandException.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */