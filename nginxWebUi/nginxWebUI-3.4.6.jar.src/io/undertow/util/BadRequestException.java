/*    */ package io.undertow.util;
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
/*    */ public class BadRequestException
/*    */   extends Exception
/*    */ {
/*    */   public BadRequestException() {}
/*    */   
/*    */   public BadRequestException(String message) {
/* 32 */     super(message);
/*    */   }
/*    */   
/*    */   public BadRequestException(Throwable cause) {
/* 36 */     super(cause);
/*    */   }
/*    */   
/*    */   public BadRequestException(String message, Throwable cause) {
/* 40 */     super(message, cause);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\BadRequestException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */