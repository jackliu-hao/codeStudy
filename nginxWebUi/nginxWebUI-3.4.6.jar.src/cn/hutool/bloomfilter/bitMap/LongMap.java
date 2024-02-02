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
/*    */ public class LongMap
/*    */   implements BitMap, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final long[] longs;
/*    */   
/*    */   public LongMap() {
/* 20 */     this.longs = new long[93750000];
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public LongMap(int size) {
/* 29 */     this.longs = new long[size];
/*    */   }
/*    */ 
/*    */   
/*    */   public void add(long i) {
/* 34 */     int r = (int)(i / 64L);
/* 35 */     long c = i & 0x3FL;
/* 36 */     this.longs[r] = this.longs[r] | 1L << (int)c;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean contains(long i) {
/* 41 */     int r = (int)(i / 64L);
/* 42 */     long c = i & 0x3FL;
/* 43 */     return ((this.longs[r] >>> (int)c & 0x1L) == 1L);
/*    */   }
/*    */ 
/*    */   
/*    */   public void remove(long i) {
/* 48 */     int r = (int)(i / 64L);
/* 49 */     long c = i & 0x3FL;
/* 50 */     this.longs[r] = this.longs[r] & (1L << (int)c ^ 0xFFFFFFFFFFFFFFFFL);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\bloomfilter\bitMap\LongMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */