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
/*    */ public class TranNeverImp
/*    */   implements TranNode
/*    */ {
/*    */   public void apply(RunnableEx runnable) throws Throwable {
/* 22 */     if (TranManager.current() != null)
/*    */     {
/* 24 */       throw new RuntimeException("Never support transactions");
/*    */     }
/* 26 */     runnable.run();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\data\tranImp\TranNeverImp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */