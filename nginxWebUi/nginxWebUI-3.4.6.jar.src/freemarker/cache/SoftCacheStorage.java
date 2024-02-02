/*     */ package freemarker.cache;
/*     */ 
/*     */ import freemarker.template.utility.UndeclaredThrowableException;
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.lang.ref.SoftReference;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
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
/*     */ public class SoftCacheStorage
/*     */   implements ConcurrentCacheStorage, CacheStorageWithGetSize
/*     */ {
/*  42 */   private static final Method atomicRemove = getAtomicRemoveMethod();
/*     */   
/*  44 */   private final ReferenceQueue queue = new ReferenceQueue();
/*     */   
/*     */   private final Map map;
/*     */   
/*     */   private final boolean concurrent;
/*     */ 
/*     */   
/*     */   public SoftCacheStorage() {
/*  52 */     this(new ConcurrentHashMap<>());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isConcurrent() {
/*  60 */     return this.concurrent;
/*     */   }
/*     */   
/*     */   public SoftCacheStorage(Map backingMap) {
/*  64 */     this.map = backingMap;
/*  65 */     this.concurrent = this.map instanceof java.util.concurrent.ConcurrentMap;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object get(Object key) {
/*  70 */     processQueue();
/*  71 */     Reference ref = (Reference)this.map.get(key);
/*  72 */     return (ref == null) ? null : ref.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public void put(Object key, Object value) {
/*  77 */     processQueue();
/*  78 */     this.map.put(key, new SoftValueReference(key, value, this.queue));
/*     */   }
/*     */ 
/*     */   
/*     */   public void remove(Object key) {
/*  83 */     processQueue();
/*  84 */     this.map.remove(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/*  89 */     this.map.clear();
/*  90 */     processQueue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSize() {
/* 100 */     processQueue();
/* 101 */     return this.map.size();
/*     */   }
/*     */   
/*     */   private void processQueue() {
/*     */     while (true) {
/* 106 */       SoftValueReference ref = (SoftValueReference)this.queue.poll();
/* 107 */       if (ref == null) {
/*     */         return;
/*     */       }
/* 110 */       Object key = ref.getKey();
/* 111 */       if (this.concurrent) {
/*     */         try {
/* 113 */           atomicRemove.invoke(this.map, new Object[] { key, ref });
/* 114 */         } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
/* 115 */           throw new UndeclaredThrowableException(e);
/*     */         }  continue;
/* 117 */       }  if (this.map.get(key) == ref)
/* 118 */         this.map.remove(key); 
/*     */     } 
/*     */   }
/*     */   
/*     */   private static final class SoftValueReference
/*     */     extends SoftReference {
/*     */     private final Object key;
/*     */     
/*     */     SoftValueReference(Object key, Object value, ReferenceQueue<? super T> queue) {
/* 127 */       super((T)value, queue);
/* 128 */       this.key = key;
/*     */     }
/*     */     
/*     */     Object getKey() {
/* 132 */       return this.key;
/*     */     }
/*     */   }
/*     */   
/*     */   private static Method getAtomicRemoveMethod() {
/*     */     try {
/* 138 */       return Class.forName("java.util.concurrent.ConcurrentMap").getMethod("remove", new Class[] { Object.class, Object.class });
/* 139 */     } catch (ClassNotFoundException e) {
/* 140 */       return null;
/* 141 */     } catch (NoSuchMethodException e) {
/* 142 */       throw new UndeclaredThrowableException(e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\cache\SoftCacheStorage.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */