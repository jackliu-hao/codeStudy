/*    */ package cn.hutool.cache.impl;
/*    */ 
/*    */ import cn.hutool.cache.Cache;
/*    */ import cn.hutool.cache.CacheListener;
/*    */ import cn.hutool.core.lang.Opt;
/*    */ import cn.hutool.core.lang.mutable.Mutable;
/*    */ import cn.hutool.core.map.WeakConcurrentMap;
/*    */ import java.lang.ref.Reference;
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
/*    */ public class WeakCache<K, V>
/*    */   extends TimedCache<K, V>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public WeakCache(long timeout) {
/* 30 */     super(timeout, (Map<Mutable<K>, CacheObj<K, V>>)new WeakConcurrentMap());
/*    */   }
/*    */ 
/*    */   
/*    */   public WeakCache<K, V> setListener(CacheListener<K, V> listener) {
/* 35 */     super.setListener(listener);
/*    */     
/* 37 */     WeakConcurrentMap<Mutable<K>, CacheObj<K, V>> map = (WeakConcurrentMap<Mutable<K>, CacheObj<K, V>>)this.cacheMap;
/*    */     
/* 39 */     map.setPurgeListener((key, value) -> listener.onRemove(Opt.ofNullable(key).map(Reference::get).map(Mutable::get).get(), value.getValue()));
/*    */     
/* 41 */     return this;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cache\impl\WeakCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */