/*    */ package org.wildfly.common.archive;
/*    */ 
/*    */ 
/*    */ abstract class Index
/*    */ {
/*    */   final int tableSize;
/*    */   
/*    */   Index(int entries) {
/*  9 */     if (entries >= 1073741824) {
/* 10 */       throw new IllegalStateException("Index is too large");
/*    */     }
/* 12 */     this.tableSize = Integer.highestOneBit(entries << 2);
/*    */   }
/*    */   
/*    */   final int size() {
/* 16 */     return this.tableSize;
/*    */   }
/*    */   
/*    */   abstract long get(int paramInt);
/*    */   
/*    */   abstract void put(int paramInt, long paramLong);
/*    */   
/*    */   int getMask() {
/* 24 */     return this.tableSize - 1;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\archive\Index.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */