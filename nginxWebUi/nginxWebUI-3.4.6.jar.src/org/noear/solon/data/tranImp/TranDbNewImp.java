/*    */ package org.noear.solon.data.tranImp;
/*    */ 
/*    */ import org.noear.solon.data.annotation.Tran;
/*    */ import org.noear.solon.data.tran.TranManager;
/*    */ import org.noear.solon.data.tran.TranNode;
/*    */ import org.noear.solon.ext.RunnableEx;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TranDbNewImp
/*    */   extends DbTran
/*    */   implements TranNode
/*    */ {
/*    */   public TranDbNewImp(Tran meta) {
/* 17 */     super(meta);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void apply(RunnableEx runnable) throws Throwable {
/* 24 */     DbTran tran = TranManager.trySuspend();
/*    */     
/*    */     try {
/* 27 */       execute(() -> runnable.run());
/*    */     
/*    */     }
/*    */     finally {
/*    */       
/* 32 */       TranManager.tryResume(tran);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\data\tranImp\TranDbNewImp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */