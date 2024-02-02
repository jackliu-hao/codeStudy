/*    */ package org.h2.mvstore.tx;
/*    */ 
/*    */ import java.util.BitSet;
/*    */ import org.h2.mvstore.RootReference;
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
/*    */ final class Snapshot<K, V>
/*    */ {
/*    */   final RootReference<K, V> root;
/*    */   final BitSet committingTransactions;
/*    */   
/*    */   Snapshot(RootReference<K, V> paramRootReference, BitSet paramBitSet) {
/* 28 */     this.root = paramRootReference;
/* 29 */     this.committingTransactions = paramBitSet;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 35 */     int i = 1;
/* 36 */     i = 31 * i + this.committingTransactions.hashCode();
/* 37 */     i = 31 * i + this.root.hashCode();
/* 38 */     return i;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean equals(Object paramObject) {
/* 44 */     if (this == paramObject) {
/* 45 */       return true;
/*    */     }
/* 47 */     if (!(paramObject instanceof Snapshot)) {
/* 48 */       return false;
/*    */     }
/* 50 */     Snapshot snapshot = (Snapshot)paramObject;
/* 51 */     return (this.committingTransactions == snapshot.committingTransactions && this.root == snapshot.root);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\tx\Snapshot.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */