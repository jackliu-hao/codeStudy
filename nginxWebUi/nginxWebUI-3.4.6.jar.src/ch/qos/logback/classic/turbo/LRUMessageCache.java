/*    */ package ch.qos.logback.classic.turbo;
/*    */ 
/*    */ import java.util.LinkedHashMap;
/*    */ import java.util.Map;
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
/*    */ class LRUMessageCache
/*    */   extends LinkedHashMap<String, Integer>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   final int cacheSize;
/*    */   
/*    */   LRUMessageCache(int cacheSize) {
/* 29 */     super((int)(cacheSize * 1.3333334F), 0.75F, true);
/* 30 */     if (cacheSize < 1) {
/* 31 */       throw new IllegalArgumentException("Cache size cannot be smaller than 1");
/*    */     }
/* 33 */     this.cacheSize = cacheSize;
/*    */   }
/*    */   
/*    */   int getMessageCountAndThenIncrement(String msg) {
/*    */     Integer i;
/* 38 */     if (msg == null) {
/* 39 */       return 0;
/*    */     }
/*    */ 
/*    */ 
/*    */     
/* 44 */     synchronized (this) {
/* 45 */       i = (Integer)get(msg);
/* 46 */       if (i == null) {
/* 47 */         i = Integer.valueOf(0);
/*    */       } else {
/* 49 */         i = Integer.valueOf(i.intValue() + 1);
/*    */       } 
/* 51 */       put((K)msg, (V)i);
/*    */     } 
/* 53 */     return i.intValue();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected boolean removeEldestEntry(Map.Entry eldest) {
/* 59 */     return (size() > this.cacheSize);
/*    */   }
/*    */ 
/*    */   
/*    */   public synchronized void clear() {
/* 64 */     super.clear();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\turbo\LRUMessageCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */