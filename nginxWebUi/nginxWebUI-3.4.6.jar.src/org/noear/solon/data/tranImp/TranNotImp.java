/*    */ package org.noear.solon.data.tranImp;
/*    */ 
/*    */ import org.noear.solon.data.tran.TranManager;
/*    */ import org.noear.solon.data.tran.TranNode;
/*    */ import org.noear.solon.ext.RunnableEx;
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
/*    */ public class TranNotImp
/*    */   implements TranNode
/*    */ {
/*    */   public void apply(RunnableEx runnable) throws Throwable {
/* 22 */     DbTran tran = TranManager.trySuspend();
/*    */     
/*    */     try {
/* 25 */       runnable.run();
/*    */     } finally {
/* 27 */       TranManager.tryResume(tran);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\data\tranImp\TranNotImp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */