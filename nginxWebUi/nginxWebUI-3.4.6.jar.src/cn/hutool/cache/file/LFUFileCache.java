/*    */ package cn.hutool.cache.file;
/*    */ 
/*    */ import cn.hutool.cache.Cache;
/*    */ import cn.hutool.cache.impl.LFUCache;
/*    */ import java.io.File;
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
/*    */ public class LFUFileCache
/*    */   extends AbstractFileCache
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public LFUFileCache(int capacity) {
/* 23 */     this(capacity, capacity / 2, 0L);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public LFUFileCache(int capacity, int maxFileSize) {
/* 33 */     this(capacity, maxFileSize, 0L);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public LFUFileCache(int capacity, int maxFileSize, long timeout) {
/* 43 */     super(capacity, maxFileSize, timeout);
/*    */   }
/*    */ 
/*    */   
/*    */   protected Cache<File, byte[]> initCache() {
/* 48 */     return (Cache<File, byte[]>)new LFUCache<File, byte[]>(this.capacity, this.timeout)
/*    */       {
/*    */         private static final long serialVersionUID = 1L;
/*    */         
/*    */         public boolean isFull() {
/* 53 */           return (LFUFileCache.this.usedSize > this.capacity);
/*    */         }
/*    */ 
/*    */         
/*    */         protected void onRemove(File key, byte[] cachedObject) {
/* 58 */           LFUFileCache.this.usedSize -= cachedObject.length;
/*    */         }
/*    */       };
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cache\file\LFUFileCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */