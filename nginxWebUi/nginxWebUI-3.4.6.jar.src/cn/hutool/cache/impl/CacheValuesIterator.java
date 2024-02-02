/*    */ package cn.hutool.cache.impl;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.util.Iterator;
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
/*    */ public class CacheValuesIterator<V>
/*    */   implements Iterator<V>, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final CacheObjIterator<?, V> cacheObjIter;
/*    */   
/*    */   CacheValuesIterator(CacheObjIterator<?, V> iterator) {
/* 22 */     this.cacheObjIter = iterator;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean hasNext() {
/* 30 */     return this.cacheObjIter.hasNext();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public V next() {
/* 38 */     return (V)this.cacheObjIter.next().getValue();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void remove() {
/* 46 */     this.cacheObjIter.remove();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cache\impl\CacheValuesIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */