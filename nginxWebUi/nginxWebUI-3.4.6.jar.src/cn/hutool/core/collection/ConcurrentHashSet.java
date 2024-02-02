/*     */ package cn.hutool.core.collection;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.AbstractSet;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConcurrentHashSet<E>
/*     */   extends AbstractSet<E>
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 7997886765361607470L;
/*  20 */   private static final Boolean PRESENT = Boolean.valueOf(true);
/*     */ 
/*     */ 
/*     */   
/*     */   private final ConcurrentHashMap<E, Boolean> map;
/*     */ 
/*     */ 
/*     */   
/*     */   public ConcurrentHashSet() {
/*  29 */     this.map = new ConcurrentHashMap<>();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConcurrentHashSet(int initialCapacity) {
/*  39 */     this.map = new ConcurrentHashMap<>(initialCapacity);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConcurrentHashSet(int initialCapacity, float loadFactor) {
/*  49 */     this.map = new ConcurrentHashMap<>(initialCapacity, loadFactor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConcurrentHashSet(int initialCapacity, float loadFactor, int concurrencyLevel) {
/*  60 */     this.map = new ConcurrentHashMap<>(initialCapacity, loadFactor, concurrencyLevel);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConcurrentHashSet(Iterable<E> iter) {
/*  68 */     if (iter instanceof Collection) {
/*  69 */       Collection<E> collection = (Collection<E>)iter;
/*  70 */       this.map = new ConcurrentHashMap<>((int)(collection.size() / 0.75F));
/*  71 */       addAll(collection);
/*     */     } else {
/*  73 */       this.map = new ConcurrentHashMap<>();
/*  74 */       for (E e : iter) {
/*  75 */         add(e);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterator<E> iterator() {
/*  83 */     return this.map.keySet().iterator();
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/*  88 */     return this.map.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/*  93 */     return this.map.isEmpty();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean contains(Object o) {
/*  99 */     return this.map.containsKey(o);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean add(E e) {
/* 104 */     return (this.map.put(e, PRESENT) == null);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean remove(Object o) {
/* 109 */     return PRESENT.equals(this.map.remove(o));
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/* 114 */     this.map.clear();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\collection\ConcurrentHashSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */