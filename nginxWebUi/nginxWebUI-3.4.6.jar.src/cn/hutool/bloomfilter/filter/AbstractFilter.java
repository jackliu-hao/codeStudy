/*    */ package cn.hutool.bloomfilter.filter;
/*    */ 
/*    */ import cn.hutool.bloomfilter.BloomFilter;
/*    */ import cn.hutool.bloomfilter.bitMap.BitMap;
/*    */ import cn.hutool.bloomfilter.bitMap.IntMap;
/*    */ import cn.hutool.bloomfilter.bitMap.LongMap;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class AbstractFilter
/*    */   implements BloomFilter
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 17 */   protected static int DEFAULT_MACHINE_NUM = 32;
/*    */   
/* 19 */   private BitMap bm = null;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected long size;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public AbstractFilter(long maxValue, int machineNum) {
/* 30 */     init(maxValue, machineNum);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public AbstractFilter(long maxValue) {
/* 39 */     this(maxValue, DEFAULT_MACHINE_NUM);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void init(long maxValue, int machineNum) {
/* 49 */     this.size = maxValue;
/* 50 */     switch (machineNum) {
/*    */       case 32:
/* 52 */         this.bm = (BitMap)new IntMap((int)(this.size / machineNum));
/*    */         return;
/*    */       case 64:
/* 55 */         this.bm = (BitMap)new LongMap((int)(this.size / machineNum));
/*    */         return;
/*    */     } 
/* 58 */     throw new RuntimeException("Error Machine number!");
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean contains(String str) {
/* 64 */     return this.bm.contains(Math.abs(hash(str)));
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean add(String str) {
/* 69 */     long hash = Math.abs(hash(str));
/* 70 */     if (this.bm.contains(hash)) {
/* 71 */       return false;
/*    */     }
/*    */     
/* 74 */     this.bm.add(hash);
/* 75 */     return true;
/*    */   }
/*    */   
/*    */   public abstract long hash(String paramString);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\bloomfilter\filter\AbstractFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */