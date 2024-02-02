/*    */ package cn.hutool.bloomfilter;
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
/*    */ public class BloomFilterUtil
/*    */ {
/*    */   public static BitSetBloomFilter createBitSet(int c, int n, int k) {
/* 20 */     return new BitSetBloomFilter(c, n, k);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static BitMapBloomFilter createBitMap(int m) {
/* 30 */     return new BitMapBloomFilter(m);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\bloomfilter\BloomFilterUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */