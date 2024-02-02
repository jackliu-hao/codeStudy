/*    */ package org.h2.mvstore.tx;
/*    */ 
/*    */ import org.h2.value.VersionedValue;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class VersionedValueUncommitted<T>
/*    */   extends VersionedValueCommitted<T>
/*    */ {
/*    */   private final long operationId;
/*    */   private final T committedValue;
/*    */   
/*    */   private VersionedValueUncommitted(long paramLong, T paramT1, T paramT2) {
/* 20 */     super(paramT1);
/* 21 */     assert paramLong != 0L;
/* 22 */     this.operationId = paramLong;
/* 23 */     this.committedValue = paramT2;
/*    */   }
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
/*    */   static <X> VersionedValue<X> getInstance(long paramLong, X paramX1, X paramX2) {
/* 37 */     return new VersionedValueUncommitted<>(paramLong, paramX1, paramX2);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isCommitted() {
/* 42 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public long getOperationId() {
/* 47 */     return this.operationId;
/*    */   }
/*    */ 
/*    */   
/*    */   public T getCommittedValue() {
/* 52 */     return this.committedValue;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 57 */     return super.toString() + " " + 
/* 58 */       TransactionStore.getTransactionId(this.operationId) + "/" + 
/* 59 */       TransactionStore.getLogId(this.operationId) + " " + this.committedValue;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\tx\VersionedValueUncommitted.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */