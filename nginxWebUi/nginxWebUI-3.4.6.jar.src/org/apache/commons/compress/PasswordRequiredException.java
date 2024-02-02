/*    */ package org.apache.commons.compress;
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
/*    */ public class PasswordRequiredException
/*    */   extends IOException
/*    */ {
/*    */   private static final long serialVersionUID = 1391070005491684483L;
/*    */   
/*    */   public PasswordRequiredException(String name) {
/* 38 */     super("Cannot read encrypted content from " + name + " without a password.");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\PasswordRequiredException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */