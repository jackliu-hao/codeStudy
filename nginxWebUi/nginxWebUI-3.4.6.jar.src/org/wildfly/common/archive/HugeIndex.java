/*    */ package org.wildfly.common.archive;
/*    */ 
/*    */ import java.util.Arrays;
/*    */ 
/*    */ 
/*    */ final class HugeIndex
/*    */   extends Index
/*    */ {
/*    */   private final long[] table;
/*    */   
/*    */   HugeIndex(int entries) {
/* 12 */     super(entries);
/* 13 */     long[] array = new long[size()];
/* 14 */     Arrays.fill(array, -1L);
/* 15 */     this.table = array;
/*    */   }
/*    */   
/*    */   long get(int index) {
/* 19 */     return this.table[index];
/*    */   }
/*    */   
/*    */   void put(int index, long offset) {
/* 23 */     long[] table = this.table;
/* 24 */     long val = table[index];
/* 25 */     while (val != -1L) {
/* 26 */       index = index + 1 & getMask();
/* 27 */       val = table[index];
/*    */     } 
/* 29 */     table[index] = offset;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\archive\HugeIndex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */