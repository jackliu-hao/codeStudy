/*    */ package freemarker.core;
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
/*    */ class BreakOrContinueException
/*    */   extends FlowControlException
/*    */ {
/* 26 */   static final BreakOrContinueException BREAK_INSTANCE = new BreakOrContinueException();
/* 27 */   static final BreakOrContinueException CONTINUE_INSTANCE = new BreakOrContinueException();
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\BreakOrContinueException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */