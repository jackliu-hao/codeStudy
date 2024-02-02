/*    */ package org.h2.value;
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
/*    */ public class VersionedValue<T>
/*    */ {
/*    */   public boolean isCommitted() {
/* 19 */     return true;
/*    */   }
/*    */   
/*    */   public long getOperationId() {
/* 23 */     return 0L;
/*    */   }
/*    */ 
/*    */   
/*    */   public T getCurrentValue() {
/* 28 */     return (T)this;
/*    */   }
/*    */ 
/*    */   
/*    */   public T getCommittedValue() {
/* 33 */     return (T)this;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\VersionedValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */