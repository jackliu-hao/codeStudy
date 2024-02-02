/*    */ package org.h2.store;
/*    */ 
/*    */ import org.h2.message.DbException;
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
/*    */ public interface InDoubtTransaction
/*    */ {
/*    */   public static final int IN_DOUBT = 0;
/*    */   public static final int COMMIT = 1;
/*    */   public static final int ROLLBACK = 2;
/*    */   
/*    */   void setState(int paramInt);
/*    */   
/*    */   int getState();
/*    */   
/*    */   default String getStateDescription() {
/* 52 */     int i = getState();
/* 53 */     switch (i) {
/*    */       case 0:
/* 55 */         return "IN_DOUBT";
/*    */       case 1:
/* 57 */         return "COMMIT";
/*    */       case 2:
/* 59 */         return "ROLLBACK";
/*    */     } 
/* 61 */     throw DbException.getInternalError("state=" + i);
/*    */   }
/*    */   
/*    */   String getTransactionName();
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\InDoubtTransaction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */