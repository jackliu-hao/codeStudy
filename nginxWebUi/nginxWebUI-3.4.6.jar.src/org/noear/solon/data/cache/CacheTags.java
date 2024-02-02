/*     */ package org.noear.solon.data.cache;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CacheTags
/*     */ {
/*     */   private CacheService _cache;
/*     */   
/*     */   public CacheTags(CacheService caching) {
/*  16 */     this._cache = caching;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void add(String tag, String targetCacheKey) {
/*  26 */     String tagKey = _tagKey(tag);
/*     */     
/*  28 */     List<String> cacheKeyList = _get(tagKey);
/*  29 */     if (cacheKeyList.contains(targetCacheKey)) {
/*     */       return;
/*     */     }
/*  32 */     cacheKeyList.add(targetCacheKey);
/*     */     
/*  34 */     _set(tagKey, cacheKeyList);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CacheTags remove(String tag) {
/*  43 */     String tagKey = _tagKey(tag);
/*     */     
/*  45 */     List<String> cacheKeyList = _get(tagKey);
/*     */     
/*  47 */     for (String cacheKey : cacheKeyList) {
/*  48 */       this._cache.remove(cacheKey);
/*     */     }
/*  50 */     this._cache.remove(tagKey);
/*     */     
/*  52 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void update(String tag, Object newValue, int seconds) {
/*  63 */     String tagKey = _tagKey(tag);
/*     */     
/*  65 */     List<String> cacheKeyList = _get(tagKey);
/*     */     
/*  67 */     for (String cacheKey : cacheKeyList) {
/*  68 */       Object temp = this._cache.get(cacheKey);
/*  69 */       if (temp != null) {
/*     */ 
/*     */         
/*  72 */         if (newValue == null) {
/*     */           
/*  74 */           this._cache.remove(cacheKey);
/*     */           continue;
/*     */         } 
/*  77 */         if (newValue.getClass() == temp.getClass()) {
/*  78 */           this._cache.store(cacheKey, newValue, seconds);
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected List<String> _get(String tagKey) {
/*  91 */     Object temp = this._cache.get(tagKey);
/*     */     
/*  93 */     if (temp == null) {
/*  94 */       return new ArrayList<>();
/*     */     }
/*  96 */     return (List<String>)temp;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void _set(String tagKey, List<String> value) {
/* 106 */     this._cache.store(tagKey, value, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String _tagKey(String tag) {
/* 113 */     return ("@" + tag).toUpperCase();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\data\cache\CacheTags.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */