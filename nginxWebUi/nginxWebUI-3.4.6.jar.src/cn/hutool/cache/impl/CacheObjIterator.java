/*    */ package cn.hutool.cache.impl;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.util.Iterator;
/*    */ import java.util.NoSuchElementException;
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
/*    */ public class CacheObjIterator<K, V>
/*    */   implements Iterator<CacheObj<K, V>>, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final Iterator<CacheObj<K, V>> iterator;
/*    */   private CacheObj<K, V> nextValue;
/*    */   
/*    */   CacheObjIterator(Iterator<CacheObj<K, V>> iterator) {
/* 28 */     this.iterator = iterator;
/* 29 */     nextValue();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean hasNext() {
/* 37 */     return (this.nextValue != null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CacheObj<K, V> next() {
/* 45 */     if (false == hasNext()) {
/* 46 */       throw new NoSuchElementException();
/*    */     }
/* 48 */     CacheObj<K, V> cachedObject = this.nextValue;
/* 49 */     nextValue();
/* 50 */     return cachedObject;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void remove() {
/* 58 */     throw new UnsupportedOperationException("Cache values Iterator is not support to modify.");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private void nextValue() {
/* 65 */     while (this.iterator.hasNext()) {
/* 66 */       this.nextValue = this.iterator.next();
/* 67 */       if (!this.nextValue.isExpired()) {
/*    */         return;
/*    */       }
/*    */     } 
/* 71 */     this.nextValue = null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cache\impl\CacheObjIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */