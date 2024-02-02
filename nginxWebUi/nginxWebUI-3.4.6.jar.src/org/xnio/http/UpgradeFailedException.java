/*    */ package org.xnio.http;
/*    */ 
/*    */ import java.io.IOException;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UpgradeFailedException
/*    */   extends IOException
/*    */ {
/*    */   private static final long serialVersionUID = 3835377492285694932L;
/*    */   
/*    */   public UpgradeFailedException() {}
/*    */   
/*    */   public UpgradeFailedException(String msg) {
/* 46 */     super(msg);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public UpgradeFailedException(Throwable cause) {
/* 57 */     super(cause);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public UpgradeFailedException(String msg, Throwable cause) {
/* 67 */     super(msg, cause);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\http\UpgradeFailedException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */