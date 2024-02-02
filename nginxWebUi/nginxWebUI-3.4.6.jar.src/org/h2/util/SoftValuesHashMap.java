/*     */ package org.h2.util;
/*     */ 
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.lang.ref.SoftReference;
/*     */ import java.util.AbstractMap;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
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
/*     */ public class SoftValuesHashMap<K, V>
/*     */   extends AbstractMap<K, V>
/*     */ {
/*     */   private final Map<K, SoftValue<V>> map;
/*  27 */   private final ReferenceQueue<V> queue = new ReferenceQueue<>();
/*     */   
/*     */   public SoftValuesHashMap() {
/*  30 */     this.map = new HashMap<>();
/*     */   }
/*     */ 
/*     */   
/*     */   private void processQueue() {
/*     */     while (true) {
/*  36 */       Reference<? extends V> reference = this.queue.poll();
/*  37 */       if (reference == null) {
/*     */         return;
/*     */       }
/*  40 */       SoftValue softValue = (SoftValue)reference;
/*  41 */       Object object = softValue.key;
/*  42 */       this.map.remove(object);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public V get(Object paramObject) {
/*  48 */     processQueue();
/*  49 */     SoftReference<V> softReference = this.map.get(paramObject);
/*  50 */     if (softReference == null) {
/*  51 */       return null;
/*     */     }
/*  53 */     return softReference.get();
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
/*     */   public V put(K paramK, V paramV) {
/*  66 */     processQueue();
/*  67 */     SoftValue<V> softValue = this.map.put(paramK, new SoftValue<>(paramV, this.queue, paramK));
/*  68 */     return (softValue == null) ? null : softValue.get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public V remove(Object paramObject) {
/*  79 */     processQueue();
/*  80 */     SoftReference<V> softReference = this.map.remove(paramObject);
/*  81 */     return (softReference == null) ? null : softReference.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/*  86 */     processQueue();
/*  87 */     this.map.clear();
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<Map.Entry<K, V>> entrySet() {
/*  92 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   private static class SoftValue<T>
/*     */     extends SoftReference<T>
/*     */   {
/*     */     final Object key;
/*     */     
/*     */     public SoftValue(T param1T, ReferenceQueue<T> param1ReferenceQueue, Object param1Object) {
/* 102 */       super(param1T, param1ReferenceQueue);
/* 103 */       this.key = param1Object;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\SoftValuesHashMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */