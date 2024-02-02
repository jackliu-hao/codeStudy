/*    */ package com.mysql.cj.exceptions;
/*    */ 
/*    */ import com.mysql.cj.Messages;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AssertionFailedException
/*    */   extends CJException
/*    */ {
/*    */   private static final long serialVersionUID = 5832552608575043403L;
/*    */   
/*    */   public static AssertionFailedException shouldNotHappen(Exception ex) throws AssertionFailedException {
/* 51 */     throw new AssertionFailedException(ex);
/*    */   }
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
/*    */   public static AssertionFailedException shouldNotHappen(String assertion) throws AssertionFailedException {
/* 73 */     return new AssertionFailedException(assertion);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public AssertionFailedException(Exception ex) {
/* 84 */     super(Messages.getString("AssertionFailedException.0") + ex.toString() + Messages.getString("AssertionFailedException.1"), ex);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public AssertionFailedException(String assertion) {
/* 94 */     super(Messages.getString("AssertionFailedException.2", new Object[] { assertion }));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\exceptions\AssertionFailedException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */