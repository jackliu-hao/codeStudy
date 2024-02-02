/*    */ package org.wildfly.common.archive;
/*    */ 
/*    */ import java.util.Arrays;
/*    */ 
/*    */ 
/*    */ final class LargeIndex
/*    */   extends Index
/*    */ {
/*    */   private final int[] table;
/*    */   
/*    */   LargeIndex(int entries) {
/* 12 */     super(entries);
/* 13 */     int[] array = new int[size()];
/* 14 */     Arrays.fill(array, -1);
/* 15 */     this.table = array;
/*    */   }
/*    */   
/*    */   long get(int index) {
/* 19 */     int val = this.table[index];
/* 20 */     return (val == -1) ? -1L : (val & 0xFFFFFFFF);
/*    */   }
/*    */   
/*    */   void put(int index, long offset) {
/* 24 */     int[] table = this.table;
/* 25 */     int val = table[index];
/* 26 */     while (val != -1L) {
/* 27 */       index = index + 1 & getMask();
/* 28 */       val = table[index];
/*    */     } 
/* 30 */     table[index] = Math.toIntExact(offset);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\archive\LargeIndex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */