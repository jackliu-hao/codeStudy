/*    */ package org.wildfly.common.archive;
/*    */ 
/*    */ import java.util.Arrays;
/*    */ 
/*    */ 
/*    */ final class TinyIndex
/*    */   extends Index
/*    */ {
/*    */   private final short[] table;
/*    */   
/*    */   TinyIndex(int entries) {
/* 12 */     super(entries);
/* 13 */     short[] array = new short[size()];
/* 14 */     Arrays.fill(array, (short)-1);
/* 15 */     this.table = array;
/*    */   }
/*    */   
/*    */   long get(int index) {
/* 19 */     int val = this.table[index];
/* 20 */     return (val == -1) ? -1L : (val & 0xFFFF);
/*    */   }
/*    */   
/*    */   void put(int index, long offset) {
/* 24 */     short[] table = this.table;
/* 25 */     int val = table[index];
/* 26 */     while (val != -1L) {
/* 27 */       index = index + 1 & getMask();
/* 28 */       val = table[index];
/*    */     } 
/* 30 */     table[index] = (short)(int)offset;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\archive\TinyIndex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */