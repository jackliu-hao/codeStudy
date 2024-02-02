/*    */ package org.wildfly.common.codec;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DecodeException
/*    */   extends IllegalArgumentException
/*    */ {
/*    */   private static final long serialVersionUID = 5823281980783313991L;
/*    */   
/*    */   public DecodeException() {}
/*    */   
/*    */   public DecodeException(String msg) {
/* 41 */     super(msg);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public DecodeException(Throwable cause) {
/* 52 */     super(cause);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public DecodeException(String msg, Throwable cause) {
/* 62 */     super(msg, cause);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\codec\DecodeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */