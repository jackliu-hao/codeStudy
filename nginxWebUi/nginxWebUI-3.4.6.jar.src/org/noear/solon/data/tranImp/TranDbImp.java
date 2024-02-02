/*    */ package org.noear.solon.data.tranImp;
/*    */ 
/*    */ import org.noear.solon.data.annotation.Tran;
/*    */ import org.noear.solon.data.tran.TranNode;
/*    */ import org.noear.solon.ext.RunnableEx;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TranDbImp
/*    */   extends DbTran
/*    */   implements TranNode
/*    */ {
/*    */   public TranDbImp(Tran meta) {
/* 15 */     super(meta);
/*    */   }
/*    */ 
/*    */   
/*    */   public void apply(RunnableEx runnable) throws Throwable {
/* 20 */     execute(() -> runnable.run());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\data\tranImp\TranDbImp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */