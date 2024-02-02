/*    */ package io.undertow.util;
/*    */ 
/*    */ import java.util.concurrent.Executor;
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
/*    */ public class SameThreadExecutor
/*    */   implements Executor
/*    */ {
/* 28 */   public static final Executor INSTANCE = new SameThreadExecutor();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void execute(Runnable command) {
/* 35 */     command.run();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\SameThreadExecutor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */