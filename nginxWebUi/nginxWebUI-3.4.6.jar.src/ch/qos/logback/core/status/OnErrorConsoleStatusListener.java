/*    */ package ch.qos.logback.core.status;
/*    */ 
/*    */ import java.io.PrintStream;
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
/*    */ public class OnErrorConsoleStatusListener
/*    */   extends OnPrintStreamStatusListenerBase
/*    */ {
/*    */   protected PrintStream getPrintStream() {
/* 28 */     return System.err;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\status\OnErrorConsoleStatusListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */