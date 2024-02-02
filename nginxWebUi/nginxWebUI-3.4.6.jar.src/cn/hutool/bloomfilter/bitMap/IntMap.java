/*    */ package cn.hutool.bloomfilter.bitMap;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class IntMap
/*    */   implements BitMap, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final int[] ints;
/*    */   
/*    */   public IntMap() {
/* 20 */     this.ints = new int[93750000];
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public IntMap(int size) {
/* 29 */     this.ints = new int[size];
/*    */   }
/*    */ 
/*    */   
/*    */   public void add(long i) {
/* 34 */     int r = (int)(i / 32L);
/* 35 */     int c = (int)(i & 0x1FL);
/* 36 */     this.ints[r] = this.ints[r] | 1 << c;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean contains(long i) {
/* 41 */     int r = (int)(i / 32L);
/* 42 */     int c = (int)(i & 0x1FL);
/* 43 */     return ((this.ints[r] >>> c & 0x1) == 1);
/*    */   }
/*    */ 
/*    */   
/*    */   public void remove(long i) {
/* 48 */     int r = (int)(i / 32L);
/* 49 */     int c = (int)(i & 0x1FL);
/* 50 */     this.ints[r] = this.ints[r] & (1 << c ^ 0xFFFFFFFF);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\bloomfilter\bitMap\IntMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */