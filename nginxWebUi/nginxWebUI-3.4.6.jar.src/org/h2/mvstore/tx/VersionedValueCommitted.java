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
/*    */ 
/*    */ 
/*    */ 
/*    */ class VersionedValueCommitted<T>
/*    */   extends VersionedValue<T>
/*    */ {
/*    */   public final T value;
/*    */   
/*    */   VersionedValueCommitted(T paramT) {
/* 22 */     this.value = paramT;
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
/*    */   static <X> VersionedValue<X> getInstance(X paramX) {
/* 35 */     assert paramX != null;
/* 36 */     return (paramX instanceof VersionedValue) ? (VersionedValue<X>)paramX : new VersionedValueCommitted<>(paramX);
/*    */   }
/*    */ 
/*    */   
/*    */   public T getCurrentValue() {
/* 41 */     return this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public T getCommittedValue() {
/* 46 */     return this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 51 */     return String.valueOf(this.value);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\tx\VersionedValueCommitted.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */