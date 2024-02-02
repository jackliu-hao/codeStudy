/*    */ package org.noear.solon.data.tran;
/*    */ 
/*    */ import org.noear.solon.data.tranImp.DbTran;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class TranManager
/*    */ {
/* 12 */   private static final ThreadLocal<DbTran> _tl_tran = new ThreadLocal<>();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void currentSet(DbTran tran) {
/* 23 */     _tl_tran.set(tran);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static DbTran current() {
/* 30 */     return _tl_tran.get();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void currentRemove() {
/* 37 */     _tl_tran.remove();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static DbTran trySuspend() {
/* 46 */     DbTran tran = current();
/*    */     
/* 48 */     if (tran != null) {
/* 49 */       currentRemove();
/*    */     }
/*    */     
/* 52 */     return tran;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void tryResume(DbTran tran) {
/* 59 */     if (tran != null)
/* 60 */       currentSet(tran); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\data\tran\TranManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */