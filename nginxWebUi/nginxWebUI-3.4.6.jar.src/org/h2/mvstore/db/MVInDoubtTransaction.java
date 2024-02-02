/*    */ package org.h2.mvstore.db;
/*    */ 
/*    */ import org.h2.mvstore.MVStore;
/*    */ import org.h2.mvstore.tx.Transaction;
/*    */ import org.h2.store.InDoubtTransaction;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class MVInDoubtTransaction
/*    */   implements InDoubtTransaction
/*    */ {
/*    */   private final MVStore store;
/*    */   private final Transaction transaction;
/* 19 */   private int state = 0;
/*    */   
/*    */   MVInDoubtTransaction(MVStore paramMVStore, Transaction paramTransaction) {
/* 22 */     this.store = paramMVStore;
/* 23 */     this.transaction = paramTransaction;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setState(int paramInt) {
/* 28 */     if (paramInt == 1) {
/* 29 */       this.transaction.commit();
/*    */     } else {
/* 31 */       this.transaction.rollback();
/*    */     } 
/* 33 */     this.store.commit();
/* 34 */     this.state = paramInt;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getState() {
/* 39 */     return this.state;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getTransactionName() {
/* 44 */     return this.transaction.getName();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\db\MVInDoubtTransaction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */