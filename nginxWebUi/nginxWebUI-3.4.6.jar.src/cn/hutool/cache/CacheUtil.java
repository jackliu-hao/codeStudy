/*     */ package cn.hutool.cache;
/*     */ 
/*     */ import cn.hutool.cache.impl.FIFOCache;
/*     */ import cn.hutool.cache.impl.LFUCache;
/*     */ import cn.hutool.cache.impl.LRUCache;
/*     */ import cn.hutool.cache.impl.NoCache;
/*     */ import cn.hutool.cache.impl.TimedCache;
/*     */ import cn.hutool.cache.impl.WeakCache;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CacheUtil
/*     */ {
/*     */   public static <K, V> FIFOCache<K, V> newFIFOCache(int capacity, long timeout) {
/*  27 */     return new FIFOCache(capacity, timeout);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <K, V> FIFOCache<K, V> newFIFOCache(int capacity) {
/*  39 */     return new FIFOCache(capacity);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <K, V> LFUCache<K, V> newLFUCache(int capacity, long timeout) {
/*  52 */     return new LFUCache(capacity, timeout);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <K, V> LFUCache<K, V> newLFUCache(int capacity) {
/*  64 */     return new LFUCache(capacity);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <K, V> LRUCache<K, V> newLRUCache(int capacity, long timeout) {
/*  78 */     return new LRUCache(capacity, timeout);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <K, V> LRUCache<K, V> newLRUCache(int capacity) {
/*  90 */     return new LRUCache(capacity);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <K, V> TimedCache<K, V> newTimedCache(long timeout) {
/* 102 */     return new TimedCache(timeout);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <K, V> WeakCache<K, V> newWeakCache(long timeout) {
/* 115 */     return new WeakCache(timeout);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <K, V> NoCache<K, V> newNoCache() {
/* 126 */     return new NoCache();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cache\CacheUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */