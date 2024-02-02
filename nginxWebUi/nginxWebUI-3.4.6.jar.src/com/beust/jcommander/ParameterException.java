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
/*    */ 
/*    */ public class ParameterException
/*    */   extends RuntimeException
/*    */ {
/*    */   public ParameterException(Throwable t) {
/* 31 */     super(t);
/*    */   }
/*    */   
/*    */   public ParameterException(String string) {
/* 35 */     super(string);
/*    */   }
/*    */   
/*    */   public ParameterException(String string, Throwable t) {
/* 39 */     super(string, t);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\beust\jcommander\ParameterException.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */