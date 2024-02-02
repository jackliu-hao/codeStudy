/*    */ package org.wildfly.common.cpu;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class CacheLevelInfo
/*    */ {
/*    */   private final int cacheLevel;
/*    */   private final CacheType cacheType;
/*    */   private final int cacheLevelSizeKB;
/*    */   private final int cacheLineSize;
/*    */   
/*    */   CacheLevelInfo(int cacheLevel, CacheType cacheType, int cacheLevelSizeKB, int cacheLineSize) {
/* 31 */     this.cacheLevel = cacheLevel;
/* 32 */     this.cacheType = cacheType;
/* 33 */     this.cacheLevelSizeKB = cacheLevelSizeKB;
/* 34 */     this.cacheLineSize = cacheLineSize;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getCacheLevel() {
/* 44 */     return this.cacheLevel;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CacheType getCacheType() {
/* 53 */     return this.cacheType;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getCacheLevelSizeKB() {
/* 62 */     return this.cacheLevelSizeKB;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getCacheLineSize() {
/* 71 */     return this.cacheLineSize;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\cpu\CacheLevelInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */