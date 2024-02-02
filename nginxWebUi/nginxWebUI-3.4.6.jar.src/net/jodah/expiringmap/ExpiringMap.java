/*      */ package net.jodah.expiringmap;
/*      */ 
/*      */ import java.lang.ref.WeakReference;
/*      */ import java.util.AbstractCollection;
/*      */ import java.util.AbstractSet;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.NoSuchElementException;
/*      */ import java.util.Set;
/*      */ import java.util.SortedSet;
/*      */ import java.util.concurrent.ConcurrentMap;
/*      */ import java.util.concurrent.ConcurrentSkipListSet;
/*      */ import java.util.concurrent.CopyOnWriteArrayList;
/*      */ import java.util.concurrent.Executors;
/*      */ import java.util.concurrent.Future;
/*      */ import java.util.concurrent.ScheduledExecutorService;
/*      */ import java.util.concurrent.ThreadFactory;
/*      */ import java.util.concurrent.ThreadPoolExecutor;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import java.util.concurrent.atomic.AtomicLong;
/*      */ import java.util.concurrent.atomic.AtomicReference;
/*      */ import java.util.concurrent.locks.Lock;
/*      */ import java.util.concurrent.locks.ReadWriteLock;
/*      */ import java.util.concurrent.locks.ReentrantReadWriteLock;
/*      */ import net.jodah.expiringmap.internal.Assert;
/*      */ import net.jodah.expiringmap.internal.NamedThreadFactory;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ExpiringMap<K, V>
/*      */   implements ConcurrentMap<K, V>
/*      */ {
/*      */   static volatile ScheduledExecutorService EXPIRER;
/*      */   static volatile ThreadPoolExecutor LISTENER_SERVICE;
/*      */   static ThreadFactory THREAD_FACTORY;
/*      */   List<ExpirationListener<K, V>> expirationListeners;
/*      */   List<ExpirationListener<K, V>> asyncExpirationListeners;
/*      */   private AtomicLong expirationNanos;
/*      */   private int maxSize;
/*      */   private final AtomicReference<ExpirationPolicy> expirationPolicy;
/*      */   private final EntryLoader<? super K, ? extends V> entryLoader;
/*      */   private final ExpiringEntryLoader<? super K, ? extends V> expiringEntryLoader;
/*   88 */   private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
/*   89 */   private final Lock readLock = this.readWriteLock.readLock();
/*   90 */   private final Lock writeLock = this.readWriteLock.writeLock();
/*      */ 
/*      */ 
/*      */   
/*      */   private final EntryMap<K, V> entries;
/*      */ 
/*      */ 
/*      */   
/*      */   private final boolean variableExpiration;
/*      */ 
/*      */ 
/*      */   
/*      */   public static void setThreadFactory(ThreadFactory threadFactory) {
/*  103 */     THREAD_FACTORY = (ThreadFactory)Assert.notNull(threadFactory, "threadFactory");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ExpiringMap(Builder<K, V> builder) {
/*  112 */     if (EXPIRER == null) {
/*  113 */       synchronized (ExpiringMap.class) {
/*  114 */         if (EXPIRER == null) {
/*  115 */           EXPIRER = Executors.newSingleThreadScheduledExecutor((THREAD_FACTORY == null) ? (ThreadFactory)new NamedThreadFactory("ExpiringMap-Expirer") : THREAD_FACTORY);
/*      */         }
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*  121 */     if (LISTENER_SERVICE == null && builder.asyncExpirationListeners != null) {
/*  122 */       initListenerService();
/*      */     }
/*  124 */     this.variableExpiration = builder.variableExpiration;
/*  125 */     this.entries = this.variableExpiration ? new EntryTreeHashMap<K, V>() : new EntryLinkedHashMap<K, V>();
/*  126 */     if (builder.expirationListeners != null)
/*  127 */       this.expirationListeners = new CopyOnWriteArrayList<ExpirationListener<K, V>>(builder.expirationListeners); 
/*  128 */     if (builder.asyncExpirationListeners != null)
/*  129 */       this.asyncExpirationListeners = new CopyOnWriteArrayList<ExpirationListener<K, V>>(builder.asyncExpirationListeners); 
/*  130 */     this.expirationPolicy = new AtomicReference<ExpirationPolicy>(builder.expirationPolicy);
/*  131 */     this.expirationNanos = new AtomicLong(TimeUnit.NANOSECONDS.convert(builder.duration, builder.timeUnit));
/*  132 */     this.maxSize = builder.maxSize;
/*  133 */     this.entryLoader = builder.entryLoader;
/*  134 */     this.expiringEntryLoader = builder.expiringEntryLoader;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class Builder<K, V>
/*      */   {
/*  142 */     private ExpirationPolicy expirationPolicy = ExpirationPolicy.CREATED;
/*      */     private List<ExpirationListener<K, V>> expirationListeners;
/*      */     private List<ExpirationListener<K, V>> asyncExpirationListeners;
/*  145 */     private TimeUnit timeUnit = TimeUnit.SECONDS;
/*      */     private boolean variableExpiration;
/*  147 */     private long duration = 60L;
/*  148 */     private int maxSize = Integer.MAX_VALUE;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private EntryLoader<K, V> entryLoader;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private ExpiringEntryLoader<K, V> expiringEntryLoader;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public <K1 extends K, V1 extends V> ExpiringMap<K1, V1> build() {
/*  166 */       return new ExpiringMap<K1, V1>(this);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder<K, V> expiration(long duration, TimeUnit timeUnit) {
/*  177 */       this.duration = duration;
/*  178 */       this.timeUnit = (TimeUnit)Assert.notNull(timeUnit, "timeUnit");
/*  179 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder<K, V> maxSize(int maxSize) {
/*  189 */       Assert.operation((maxSize > 0), "maxSize");
/*  190 */       this.maxSize = maxSize;
/*  191 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public <K1 extends K, V1 extends V> Builder<K1, V1> entryLoader(EntryLoader<? super K1, ? super V1> loader) {
/*  204 */       assertNoLoaderSet();
/*  205 */       this.entryLoader = (EntryLoader<K, V>)Assert.notNull(loader, "loader");
/*  206 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public <K1 extends K, V1 extends V> Builder<K1, V1> expiringEntryLoader(ExpiringEntryLoader<? super K1, ? super V1> loader) {
/*  220 */       assertNoLoaderSet();
/*  221 */       this.expiringEntryLoader = (ExpiringEntryLoader<K, V>)Assert.notNull(loader, "loader");
/*  222 */       variableExpiration();
/*  223 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public <K1 extends K, V1 extends V> Builder<K1, V1> expirationListener(ExpirationListener<? super K1, ? super V1> listener) {
/*  236 */       Assert.notNull(listener, "listener");
/*  237 */       if (this.expirationListeners == null)
/*  238 */         this.expirationListeners = new ArrayList<ExpirationListener<K, V>>(); 
/*  239 */       this.expirationListeners.add(listener);
/*  240 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public <K1 extends K, V1 extends V> Builder<K1, V1> expirationListeners(List<ExpirationListener<? super K1, ? super V1>> listeners) {
/*  253 */       Assert.notNull(listeners, "listeners");
/*  254 */       if (this.expirationListeners == null)
/*  255 */         this.expirationListeners = new ArrayList<ExpirationListener<K, V>>(listeners.size()); 
/*  256 */       for (ExpirationListener<? super K1, ? super V1> listener : listeners)
/*  257 */         this.expirationListeners.add(listener); 
/*  258 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public <K1 extends K, V1 extends V> Builder<K1, V1> asyncExpirationListener(ExpirationListener<? super K1, ? super V1> listener) {
/*  271 */       Assert.notNull(listener, "listener");
/*  272 */       if (this.asyncExpirationListeners == null)
/*  273 */         this.asyncExpirationListeners = new ArrayList<ExpirationListener<K, V>>(); 
/*  274 */       this.asyncExpirationListeners.add(listener);
/*  275 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public <K1 extends K, V1 extends V> Builder<K1, V1> asyncExpirationListeners(List<ExpirationListener<? super K1, ? super V1>> listeners) {
/*  288 */       Assert.notNull(listeners, "listeners");
/*  289 */       if (this.asyncExpirationListeners == null)
/*  290 */         this.asyncExpirationListeners = new ArrayList<ExpirationListener<K, V>>(listeners.size()); 
/*  291 */       for (ExpirationListener<? super K1, ? super V1> listener : listeners)
/*  292 */         this.asyncExpirationListeners.add(listener); 
/*  293 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder<K, V> expirationPolicy(ExpirationPolicy expirationPolicy) {
/*  303 */       this.expirationPolicy = (ExpirationPolicy)Assert.notNull(expirationPolicy, "expirationPolicy");
/*  304 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder<K, V> variableExpiration() {
/*  311 */       this.variableExpiration = true;
/*  312 */       return this;
/*      */     }
/*      */     
/*      */     private void assertNoLoaderSet() {
/*  316 */       Assert.state((this.entryLoader == null && this.expiringEntryLoader == null), "Either entryLoader or expiringEntryLoader may be set, not both", new Object[0]);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private Builder() {}
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static class EntryLinkedHashMap<K, V>
/*      */     extends LinkedHashMap<K, ExpiringEntry<K, V>>
/*      */     implements EntryMap<K, V>
/*      */   {
/*      */     private static final long serialVersionUID = 1L;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private EntryLinkedHashMap() {}
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean containsValue(Object value) {
/*  344 */       for (ExpiringMap.ExpiringEntry<K, V> entry : values()) {
/*  345 */         V v = entry.value;
/*  346 */         if (v == value || (value != null && value.equals(v)))
/*  347 */           return true; 
/*      */       } 
/*  349 */       return false;
/*      */     }
/*      */ 
/*      */     
/*      */     public ExpiringMap.ExpiringEntry<K, V> first() {
/*  354 */       return isEmpty() ? null : values().iterator().next();
/*      */     }
/*      */ 
/*      */     
/*      */     public void reorder(ExpiringMap.ExpiringEntry<K, V> value) {
/*  359 */       remove(value.key);
/*  360 */       value.resetExpiration();
/*  361 */       put(value.key, value);
/*      */     }
/*      */ 
/*      */     
/*      */     public Iterator<ExpiringMap.ExpiringEntry<K, V>> valuesIterator() {
/*  366 */       return values().iterator();
/*      */     }
/*      */     
/*      */     abstract class AbstractHashIterator {
/*  370 */       private final Iterator<Map.Entry<K, ExpiringMap.ExpiringEntry<K, V>>> iterator = ExpiringMap.EntryLinkedHashMap.this.entrySet().iterator();
/*      */       private ExpiringMap.ExpiringEntry<K, V> next;
/*      */       
/*      */       public boolean hasNext() {
/*  374 */         return this.iterator.hasNext();
/*      */       }
/*      */       
/*      */       public ExpiringMap.ExpiringEntry<K, V> getNext() {
/*  378 */         this.next = (ExpiringMap.ExpiringEntry<K, V>)((Map.Entry)this.iterator.next()).getValue();
/*  379 */         return this.next;
/*      */       }
/*      */       
/*      */       public void remove() {
/*  383 */         this.iterator.remove();
/*      */       }
/*      */     }
/*      */     
/*      */     final class KeyIterator extends AbstractHashIterator implements Iterator<K> {
/*      */       public final K next() {
/*  389 */         return (getNext()).key;
/*      */       }
/*      */     }
/*      */     
/*      */     final class ValueIterator extends AbstractHashIterator implements Iterator<V> {
/*      */       public final V next() {
/*  395 */         return (getNext()).value;
/*      */       }
/*      */     }
/*      */     
/*      */     public final class EntryIterator extends AbstractHashIterator implements Iterator<Map.Entry<K, V>> {
/*      */       public final Map.Entry<K, V> next() {
/*  401 */         return ExpiringMap.mapEntryFor(getNext());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private static class EntryTreeHashMap<K, V>
/*      */     extends HashMap<K, ExpiringEntry<K, V>> implements EntryMap<K, V> {
/*      */     private static final long serialVersionUID = 1L;
/*  409 */     SortedSet<ExpiringMap.ExpiringEntry<K, V>> sortedSet = new ConcurrentSkipListSet<ExpiringMap.ExpiringEntry<K, V>>();
/*      */ 
/*      */     
/*      */     public void clear() {
/*  413 */       super.clear();
/*  414 */       this.sortedSet.clear();
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean containsValue(Object value) {
/*  419 */       for (ExpiringMap.ExpiringEntry<K, V> entry : values()) {
/*  420 */         V v = entry.value;
/*  421 */         if (v == value || (value != null && value.equals(v)))
/*  422 */           return true; 
/*      */       } 
/*  424 */       return false;
/*      */     }
/*      */ 
/*      */     
/*      */     public ExpiringMap.ExpiringEntry<K, V> first() {
/*  429 */       return this.sortedSet.isEmpty() ? null : this.sortedSet.first();
/*      */     }
/*      */ 
/*      */     
/*      */     public ExpiringMap.ExpiringEntry<K, V> put(K key, ExpiringMap.ExpiringEntry<K, V> value) {
/*  434 */       this.sortedSet.add(value);
/*  435 */       return super.put(key, value);
/*      */     }
/*      */ 
/*      */     
/*      */     public ExpiringMap.ExpiringEntry<K, V> remove(Object key) {
/*  440 */       ExpiringMap.ExpiringEntry<K, V> entry = super.remove(key);
/*  441 */       if (entry != null)
/*  442 */         this.sortedSet.remove(entry); 
/*  443 */       return entry;
/*      */     }
/*      */ 
/*      */     
/*      */     public void reorder(ExpiringMap.ExpiringEntry<K, V> value) {
/*  448 */       this.sortedSet.remove(value);
/*  449 */       value.resetExpiration();
/*  450 */       this.sortedSet.add(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public Iterator<ExpiringMap.ExpiringEntry<K, V>> valuesIterator() {
/*  455 */       return new ExpiringEntryIterator();
/*      */     }
/*      */     private EntryTreeHashMap() {}
/*      */     
/*  459 */     abstract class AbstractHashIterator { private final Iterator<ExpiringMap.ExpiringEntry<K, V>> iterator = ExpiringMap.EntryTreeHashMap.this.sortedSet.iterator();
/*      */       protected ExpiringMap.ExpiringEntry<K, V> next;
/*      */       
/*      */       public boolean hasNext() {
/*  463 */         return this.iterator.hasNext();
/*      */       }
/*      */       
/*      */       public ExpiringMap.ExpiringEntry<K, V> getNext() {
/*  467 */         this.next = this.iterator.next();
/*  468 */         return this.next;
/*      */       }
/*      */       
/*      */       public void remove() {
/*  472 */         ExpiringMap.EntryTreeHashMap.this.remove(this.next.key);
/*  473 */         this.iterator.remove();
/*      */       } }
/*      */ 
/*      */     
/*      */     final class ExpiringEntryIterator extends AbstractHashIterator implements Iterator<ExpiringMap.ExpiringEntry<K, V>> {
/*      */       public final ExpiringMap.ExpiringEntry<K, V> next() {
/*  479 */         return getNext();
/*      */       }
/*      */     }
/*      */     
/*      */     final class KeyIterator extends AbstractHashIterator implements Iterator<K> {
/*      */       public final K next() {
/*  485 */         return (getNext()).key;
/*      */       }
/*      */     }
/*      */     
/*      */     final class ValueIterator extends AbstractHashIterator implements Iterator<V> {
/*      */       public final V next() {
/*  491 */         return (getNext()).value;
/*      */       }
/*      */     }
/*      */     
/*      */     final class EntryIterator extends AbstractHashIterator implements Iterator<Map.Entry<K, V>> {
/*      */       public final Map.Entry<K, V> next() {
/*  497 */         return ExpiringMap.mapEntryFor(getNext());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static class ExpiringEntry<K, V>
/*      */     implements Comparable<ExpiringEntry<K, V>>
/*      */   {
/*      */     final AtomicLong expirationNanos;
/*      */ 
/*      */     
/*      */     final AtomicLong expectedExpiration;
/*      */ 
/*      */     
/*      */     final AtomicReference<ExpirationPolicy> expirationPolicy;
/*      */     
/*      */     final K key;
/*      */     
/*      */     volatile Future<?> entryFuture;
/*      */     
/*      */     V value;
/*      */     
/*      */     volatile boolean scheduled;
/*      */ 
/*      */     
/*      */     ExpiringEntry(K key, V value, AtomicReference<ExpirationPolicy> expirationPolicy, AtomicLong expirationNanos) {
/*  525 */       this.key = key;
/*  526 */       this.value = value;
/*  527 */       this.expirationPolicy = expirationPolicy;
/*  528 */       this.expirationNanos = expirationNanos;
/*  529 */       this.expectedExpiration = new AtomicLong();
/*  530 */       resetExpiration();
/*      */     }
/*      */ 
/*      */     
/*      */     public int compareTo(ExpiringEntry<K, V> other) {
/*  535 */       if (this.key.equals(other.key))
/*  536 */         return 0; 
/*  537 */       return (this.expectedExpiration.get() < other.expectedExpiration.get()) ? -1 : 1;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/*  542 */       int prime = 31;
/*  543 */       int result = 1;
/*  544 */       result = 31 * result + ((this.key == null) ? 0 : this.key.hashCode());
/*  545 */       result = 31 * result + ((this.value == null) ? 0 : this.value.hashCode());
/*  546 */       return result;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/*  551 */       if (this == obj)
/*  552 */         return true; 
/*  553 */       if (obj == null)
/*  554 */         return false; 
/*  555 */       if (getClass() != obj.getClass())
/*  556 */         return false; 
/*  557 */       ExpiringEntry<?, ?> other = (ExpiringEntry<?, ?>)obj;
/*  558 */       if (!this.key.equals(other.key))
/*  559 */         return false; 
/*  560 */       if (this.value == null) {
/*  561 */         if (other.value != null)
/*  562 */           return false; 
/*  563 */       } else if (!this.value.equals(other.value)) {
/*  564 */         return false;
/*  565 */       }  return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public String toString() {
/*  570 */       return this.value.toString();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     synchronized boolean cancel() {
/*  579 */       boolean result = this.scheduled;
/*  580 */       if (this.entryFuture != null) {
/*  581 */         this.entryFuture.cancel(false);
/*      */       }
/*  583 */       this.entryFuture = null;
/*  584 */       this.scheduled = false;
/*  585 */       return result;
/*      */     }
/*      */ 
/*      */     
/*      */     synchronized V getValue() {
/*  590 */       return this.value;
/*      */     }
/*      */ 
/*      */     
/*      */     void resetExpiration() {
/*  595 */       this.expectedExpiration.set(this.expirationNanos.get() + System.nanoTime());
/*      */     }
/*      */ 
/*      */     
/*      */     synchronized void schedule(Future<?> entryFuture) {
/*  600 */       this.entryFuture = entryFuture;
/*  601 */       this.scheduled = true;
/*      */     }
/*      */ 
/*      */     
/*      */     synchronized void setValue(V value) {
/*  606 */       this.value = value;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Builder<Object, Object> builder() {
/*  616 */     return new Builder<Object, Object>();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <K, V> ExpiringMap<K, V> create() {
/*  624 */     return new ExpiringMap<K, V>((Builder)builder());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void addExpirationListener(ExpirationListener<K, V> listener) {
/*  634 */     Assert.notNull(listener, "listener");
/*  635 */     if (this.expirationListeners == null)
/*  636 */       this.expirationListeners = new CopyOnWriteArrayList<ExpirationListener<K, V>>(); 
/*  637 */     this.expirationListeners.add(listener);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void addAsyncExpirationListener(ExpirationListener<K, V> listener) {
/*  647 */     Assert.notNull(listener, "listener");
/*  648 */     if (this.asyncExpirationListeners == null)
/*  649 */       this.asyncExpirationListeners = new CopyOnWriteArrayList<ExpirationListener<K, V>>(); 
/*  650 */     this.asyncExpirationListeners.add(listener);
/*      */     
/*  652 */     if (LISTENER_SERVICE == null) {
/*  653 */       initListenerService();
/*      */     }
/*      */   }
/*      */   
/*      */   public void clear() {
/*  658 */     this.writeLock.lock();
/*      */     try {
/*  660 */       for (ExpiringEntry<K, V> entry : this.entries.values())
/*  661 */         entry.cancel(); 
/*  662 */       this.entries.clear();
/*      */     } finally {
/*  664 */       this.writeLock.unlock();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean containsKey(Object key) {
/*  670 */     this.readLock.lock();
/*      */     try {
/*  672 */       return this.entries.containsKey(key);
/*      */     } finally {
/*  674 */       this.readLock.unlock();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean containsValue(Object value) {
/*  680 */     this.readLock.lock();
/*      */     try {
/*  682 */       return this.entries.containsValue(value);
/*      */     } finally {
/*  684 */       this.readLock.unlock();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public Set<Map.Entry<K, V>> entrySet() {
/*  690 */     return new AbstractSet<Map.Entry<K, V>>()
/*      */       {
/*      */         public void clear() {
/*  693 */           ExpiringMap.this.clear();
/*      */         }
/*      */ 
/*      */         
/*      */         public boolean contains(Object entry) {
/*  698 */           if (!(entry instanceof Map.Entry))
/*  699 */             return false; 
/*  700 */           Map.Entry<?, ?> e = (Map.Entry<?, ?>)entry;
/*  701 */           return ExpiringMap.this.containsKey(e.getKey());
/*      */         }
/*      */ 
/*      */         
/*      */         public Iterator<Map.Entry<K, V>> iterator() {
/*  706 */           ((ExpiringMap.EntryLinkedHashMap)ExpiringMap.this.entries).getClass();
/*  707 */           ((ExpiringMap.EntryTreeHashMap)ExpiringMap.this.entries).getClass(); return (ExpiringMap.this.entries instanceof ExpiringMap.EntryLinkedHashMap) ? new ExpiringMap.EntryLinkedHashMap.EntryIterator((ExpiringMap.EntryLinkedHashMap)ExpiringMap.this.entries) : new ExpiringMap.EntryTreeHashMap.EntryIterator((ExpiringMap.EntryTreeHashMap)ExpiringMap.this.entries);
/*      */         }
/*      */ 
/*      */         
/*      */         public boolean remove(Object entry) {
/*  712 */           if (entry instanceof Map.Entry) {
/*  713 */             Map.Entry<?, ?> e = (Map.Entry<?, ?>)entry;
/*  714 */             return (ExpiringMap.this.remove(e.getKey()) != null);
/*      */           } 
/*  716 */           return false;
/*      */         }
/*      */ 
/*      */         
/*      */         public int size() {
/*  721 */           return ExpiringMap.this.size();
/*      */         }
/*      */       };
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean equals(Object obj) {
/*  728 */     this.readLock.lock();
/*      */     try {
/*  730 */       return this.entries.equals(obj);
/*      */     } finally {
/*  732 */       this.readLock.unlock();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public V get(Object key) {
/*  739 */     ExpiringEntry<K, V> entry = getEntry(key);
/*      */     
/*  741 */     if (entry == null)
/*  742 */       return load((K)key); 
/*  743 */     if (ExpirationPolicy.ACCESSED.equals(entry.expirationPolicy.get())) {
/*  744 */       resetEntry(entry, false);
/*      */     }
/*  746 */     return entry.getValue();
/*      */   }
/*      */   
/*      */   private V load(K key) {
/*  750 */     if (this.entryLoader == null && this.expiringEntryLoader == null) {
/*  751 */       return null;
/*      */     }
/*  753 */     this.writeLock.lock();
/*      */     
/*      */     try {
/*  756 */       ExpiringEntry<K, V> entry = getEntry(key);
/*  757 */       if (entry != null) {
/*  758 */         return entry.getValue();
/*      */       }
/*  760 */       if (this.entryLoader != null) {
/*  761 */         V value = this.entryLoader.load(key);
/*  762 */         put(key, value);
/*  763 */         return value;
/*      */       } 
/*  765 */       ExpiringValue<? extends V> expiringValue = this.expiringEntryLoader.load(key);
/*  766 */       if (expiringValue == null) {
/*  767 */         put(key, null);
/*  768 */         return null;
/*      */       } 
/*  770 */       long duration = (expiringValue.getTimeUnit() == null) ? this.expirationNanos.get() : expiringValue.getDuration();
/*  771 */       TimeUnit timeUnit = (expiringValue.getTimeUnit() == null) ? TimeUnit.NANOSECONDS : expiringValue.getTimeUnit();
/*  772 */       put(key, expiringValue.getValue(), (expiringValue.getExpirationPolicy() == null) ? this.expirationPolicy.get() : expiringValue
/*  773 */           .getExpirationPolicy(), duration, timeUnit);
/*  774 */       return expiringValue.getValue();
/*      */     }
/*      */     finally {
/*      */       
/*  778 */       this.writeLock.unlock();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getExpiration() {
/*  788 */     return TimeUnit.NANOSECONDS.toMillis(this.expirationNanos.get());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getExpiration(K key) {
/*  800 */     Assert.notNull(key, "key");
/*  801 */     ExpiringEntry<K, V> entry = getEntry(key);
/*  802 */     Assert.element(entry, key);
/*  803 */     return TimeUnit.NANOSECONDS.toMillis(entry.expirationNanos.get());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExpirationPolicy getExpirationPolicy(K key) {
/*  815 */     Assert.notNull(key, "key");
/*  816 */     ExpiringEntry<K, V> entry = getEntry(key);
/*  817 */     Assert.element(entry, key);
/*  818 */     return entry.expirationPolicy.get();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getExpectedExpiration(K key) {
/*  831 */     Assert.notNull(key, "key");
/*  832 */     ExpiringEntry<K, V> entry = getEntry(key);
/*  833 */     Assert.element(entry, key);
/*  834 */     return TimeUnit.NANOSECONDS.toMillis(entry.expectedExpiration.get() - System.nanoTime());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxSize() {
/*  844 */     return this.maxSize;
/*      */   }
/*      */ 
/*      */   
/*      */   public int hashCode() {
/*  849 */     this.readLock.lock();
/*      */     try {
/*  851 */       return this.entries.hashCode();
/*      */     } finally {
/*  853 */       this.readLock.unlock();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isEmpty() {
/*  859 */     this.readLock.lock();
/*      */     try {
/*  861 */       return this.entries.isEmpty();
/*      */     } finally {
/*  863 */       this.readLock.unlock();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public Set<K> keySet() {
/*  869 */     return new AbstractSet<K>()
/*      */       {
/*      */         public void clear() {
/*  872 */           ExpiringMap.this.clear();
/*      */         }
/*      */ 
/*      */         
/*      */         public boolean contains(Object key) {
/*  877 */           return ExpiringMap.this.containsKey(key);
/*      */         }
/*      */ 
/*      */         
/*      */         public Iterator<K> iterator() {
/*  882 */           ((ExpiringMap.EntryLinkedHashMap)ExpiringMap.this.entries).getClass();
/*  883 */           ((ExpiringMap.EntryTreeHashMap)ExpiringMap.this.entries).getClass(); return (ExpiringMap.this.entries instanceof ExpiringMap.EntryLinkedHashMap) ? new ExpiringMap.EntryLinkedHashMap.KeyIterator((ExpiringMap.EntryLinkedHashMap)ExpiringMap.this.entries) : new ExpiringMap.EntryTreeHashMap.KeyIterator((ExpiringMap.EntryTreeHashMap)ExpiringMap.this.entries);
/*      */         }
/*      */ 
/*      */         
/*      */         public boolean remove(Object value) {
/*  888 */           return (ExpiringMap.this.remove(value) != null);
/*      */         }
/*      */ 
/*      */         
/*      */         public int size() {
/*  893 */           return ExpiringMap.this.size();
/*      */         }
/*      */       };
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public V put(K key, V value) {
/*  909 */     Assert.notNull(key, "key");
/*  910 */     return putInternal(key, value, this.expirationPolicy.get(), this.expirationNanos.get());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public V put(K key, V value, ExpirationPolicy expirationPolicy) {
/*  917 */     return put(key, value, expirationPolicy, this.expirationNanos.get(), TimeUnit.NANOSECONDS);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public V put(K key, V value, long duration, TimeUnit timeUnit) {
/*  924 */     return put(key, value, this.expirationPolicy.get(), duration, timeUnit);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public V put(K key, V value, ExpirationPolicy expirationPolicy, long duration, TimeUnit timeUnit) {
/*  940 */     Assert.notNull(key, "key");
/*  941 */     Assert.notNull(expirationPolicy, "expirationPolicy");
/*  942 */     Assert.notNull(timeUnit, "timeUnit");
/*  943 */     Assert.operation(this.variableExpiration, "Variable expiration is not enabled");
/*  944 */     return putInternal(key, value, expirationPolicy, TimeUnit.NANOSECONDS.convert(duration, timeUnit));
/*      */   }
/*      */ 
/*      */   
/*      */   public void putAll(Map<? extends K, ? extends V> map) {
/*  949 */     Assert.notNull(map, "map");
/*  950 */     long expiration = this.expirationNanos.get();
/*  951 */     ExpirationPolicy expirationPolicy = this.expirationPolicy.get();
/*  952 */     this.writeLock.lock();
/*      */     try {
/*  954 */       for (Map.Entry<? extends K, ? extends V> entry : map.entrySet())
/*  955 */         putInternal(entry.getKey(), entry.getValue(), expirationPolicy, expiration); 
/*      */     } finally {
/*  957 */       this.writeLock.unlock();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public V putIfAbsent(K key, V value) {
/*  963 */     Assert.notNull(key, "key");
/*  964 */     this.writeLock.lock();
/*      */     try {
/*  966 */       if (!this.entries.containsKey(key)) {
/*  967 */         return putInternal(key, value, this.expirationPolicy.get(), this.expirationNanos.get());
/*      */       }
/*  969 */       return (V)this.entries.get(key).getValue();
/*      */     } finally {
/*  971 */       this.writeLock.unlock();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public V remove(Object key) {
/*  977 */     Assert.notNull(key, "key");
/*  978 */     this.writeLock.lock();
/*      */     try {
/*  980 */       ExpiringEntry<K, V> entry = this.entries.remove(key);
/*  981 */       if (entry == null)
/*  982 */         return null; 
/*  983 */       if (entry.cancel())
/*  984 */         scheduleEntry(this.entries.first()); 
/*  985 */       return entry.getValue();
/*      */     } finally {
/*  987 */       this.writeLock.unlock();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean remove(Object key, Object value) {
/*  993 */     Assert.notNull(key, "key");
/*  994 */     this.writeLock.lock();
/*      */     try {
/*  996 */       ExpiringEntry<K, V> entry = this.entries.get(key);
/*  997 */       if (entry != null && entry.getValue().equals(value)) {
/*  998 */         this.entries.remove(key);
/*  999 */         if (entry.cancel())
/* 1000 */           scheduleEntry(this.entries.first()); 
/* 1001 */         return true;
/*      */       } 
/* 1003 */       return false;
/*      */     } finally {
/* 1005 */       this.writeLock.unlock();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public V replace(K key, V value) {
/* 1011 */     Assert.notNull(key, "key");
/* 1012 */     this.writeLock.lock();
/*      */     try {
/* 1014 */       if (this.entries.containsKey(key)) {
/* 1015 */         return putInternal(key, value, this.expirationPolicy.get(), this.expirationNanos.get());
/*      */       }
/* 1017 */       return null;
/*      */     } finally {
/* 1019 */       this.writeLock.unlock();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean replace(K key, V oldValue, V newValue) {
/* 1025 */     Assert.notNull(key, "key");
/* 1026 */     this.writeLock.lock();
/*      */     try {
/* 1028 */       ExpiringEntry<K, V> entry = this.entries.get(key);
/* 1029 */       if (entry != null && entry.getValue().equals(oldValue)) {
/* 1030 */         putInternal(key, newValue, this.expirationPolicy.get(), this.expirationNanos.get());
/* 1031 */         return true;
/*      */       } 
/* 1033 */       return false;
/*      */     } finally {
/* 1035 */       this.writeLock.unlock();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeExpirationListener(ExpirationListener<K, V> listener) {
/* 1046 */     Assert.notNull(listener, "listener");
/* 1047 */     for (int i = 0; i < this.expirationListeners.size(); i++) {
/* 1048 */       if (((ExpirationListener)this.expirationListeners.get(i)).equals(listener)) {
/* 1049 */         this.expirationListeners.remove(i);
/*      */         return;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeAsyncExpirationListener(ExpirationListener<K, V> listener) {
/* 1062 */     Assert.notNull(listener, "listener");
/* 1063 */     for (int i = 0; i < this.asyncExpirationListeners.size(); i++) {
/* 1064 */       if (((ExpirationListener)this.asyncExpirationListeners.get(i)).equals(listener)) {
/* 1065 */         this.asyncExpirationListeners.remove(i);
/*      */         return;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void resetExpiration(K key) {
/* 1078 */     Assert.notNull(key, "key");
/* 1079 */     ExpiringEntry<K, V> entry = getEntry(key);
/* 1080 */     if (entry != null) {
/* 1081 */       resetEntry(entry, false);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setExpiration(K key, long duration, TimeUnit timeUnit) {
/* 1095 */     Assert.notNull(key, "key");
/* 1096 */     Assert.notNull(timeUnit, "timeUnit");
/* 1097 */     Assert.operation(this.variableExpiration, "Variable expiration is not enabled");
/* 1098 */     this.writeLock.lock();
/*      */     try {
/* 1100 */       ExpiringEntry<K, V> entry = this.entries.get(key);
/* 1101 */       if (entry != null) {
/* 1102 */         entry.expirationNanos.set(TimeUnit.NANOSECONDS.convert(duration, timeUnit));
/* 1103 */         resetEntry(entry, true);
/*      */       } 
/*      */     } finally {
/* 1106 */       this.writeLock.unlock();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setExpiration(long duration, TimeUnit timeUnit) {
/* 1119 */     Assert.notNull(timeUnit, "timeUnit");
/* 1120 */     Assert.operation(this.variableExpiration, "Variable expiration is not enabled");
/* 1121 */     this.expirationNanos.set(TimeUnit.NANOSECONDS.convert(duration, timeUnit));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setExpirationPolicy(ExpirationPolicy expirationPolicy) {
/* 1131 */     Assert.notNull(expirationPolicy, "expirationPolicy");
/* 1132 */     this.expirationPolicy.set(expirationPolicy);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setExpirationPolicy(K key, ExpirationPolicy expirationPolicy) {
/* 1144 */     Assert.notNull(key, "key");
/* 1145 */     Assert.notNull(expirationPolicy, "expirationPolicy");
/* 1146 */     Assert.operation(this.variableExpiration, "Variable expiration is not enabled");
/* 1147 */     ExpiringEntry<K, V> entry = getEntry(key);
/* 1148 */     if (entry != null) {
/* 1149 */       entry.expirationPolicy.set(expirationPolicy);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setMaxSize(int maxSize) {
/* 1159 */     Assert.operation((maxSize > 0), "maxSize");
/* 1160 */     this.maxSize = maxSize;
/*      */   }
/*      */ 
/*      */   
/*      */   public int size() {
/* 1165 */     this.readLock.lock();
/*      */     try {
/* 1167 */       return this.entries.size();
/*      */     } finally {
/* 1169 */       this.readLock.unlock();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public String toString() {
/* 1175 */     this.readLock.lock();
/*      */     try {
/* 1177 */       return this.entries.toString();
/*      */     } finally {
/* 1179 */       this.readLock.unlock();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public Collection<V> values() {
/* 1185 */     return new AbstractCollection<V>()
/*      */       {
/*      */         public void clear() {
/* 1188 */           ExpiringMap.this.clear();
/*      */         }
/*      */ 
/*      */         
/*      */         public boolean contains(Object value) {
/* 1193 */           return ExpiringMap.this.containsValue(value);
/*      */         }
/*      */ 
/*      */         
/*      */         public Iterator<V> iterator() {
/* 1198 */           ((ExpiringMap.EntryLinkedHashMap)ExpiringMap.this.entries).getClass();
/* 1199 */           ((ExpiringMap.EntryTreeHashMap)ExpiringMap.this.entries).getClass(); return (ExpiringMap.this.entries instanceof ExpiringMap.EntryLinkedHashMap) ? new ExpiringMap.EntryLinkedHashMap.ValueIterator((ExpiringMap.EntryLinkedHashMap)ExpiringMap.this.entries) : new ExpiringMap.EntryTreeHashMap.ValueIterator((ExpiringMap.EntryTreeHashMap)ExpiringMap.this.entries);
/*      */         }
/*      */ 
/*      */         
/*      */         public int size() {
/* 1204 */           return ExpiringMap.this.size();
/*      */         }
/*      */       };
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void notifyListeners(final ExpiringEntry<K, V> entry) {
/* 1215 */     if (this.asyncExpirationListeners != null) {
/* 1216 */       for (ExpirationListener<K, V> listener : this.asyncExpirationListeners) {
/* 1217 */         LISTENER_SERVICE.execute(new Runnable() {
/*      */               public void run() {
/*      */                 try {
/* 1220 */                   listener.expired(entry.key, entry.getValue());
/* 1221 */                 } catch (Exception exception) {}
/*      */               }
/*      */             });
/*      */       } 
/*      */     }
/*      */     
/* 1227 */     if (this.expirationListeners != null) {
/* 1228 */       for (ExpirationListener<K, V> listener : this.expirationListeners) {
/*      */         try {
/* 1230 */           listener.expired(entry.key, entry.getValue());
/* 1231 */         } catch (Exception exception) {}
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   ExpiringEntry<K, V> getEntry(Object key) {
/* 1240 */     this.readLock.lock();
/*      */     try {
/* 1242 */       return this.entries.get(key);
/*      */     } finally {
/* 1244 */       this.readLock.unlock();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   V putInternal(K key, V value, ExpirationPolicy expirationPolicy, long expirationNanos) {
/* 1253 */     this.writeLock.lock();
/*      */     try {
/* 1255 */       ExpiringEntry<K, V> entry = this.entries.get(key);
/* 1256 */       V oldValue = null;
/*      */       
/* 1258 */       if (entry == null) {
/* 1259 */         entry = new ExpiringEntry<K, V>(key, value, this.variableExpiration ? new AtomicReference<ExpirationPolicy>(expirationPolicy) : this.expirationPolicy, this.variableExpiration ? new AtomicLong(expirationNanos) : this.expirationNanos);
/*      */ 
/*      */         
/* 1262 */         if (this.entries.size() >= this.maxSize) {
/* 1263 */           ExpiringEntry<K, V> expiredEntry = this.entries.first();
/* 1264 */           this.entries.remove(expiredEntry.key);
/* 1265 */           notifyListeners(expiredEntry);
/*      */         } 
/* 1267 */         this.entries.put(key, entry);
/* 1268 */         if (this.entries.size() == 1 || this.entries.first().equals(entry))
/* 1269 */           scheduleEntry(entry); 
/*      */       } else {
/* 1271 */         oldValue = entry.getValue();
/* 1272 */         if (!ExpirationPolicy.ACCESSED.equals(expirationPolicy) && ((oldValue == null && value == null) || (oldValue != null && oldValue
/* 1273 */           .equals(value)))) {
/* 1274 */           return value;
/*      */         }
/* 1276 */         entry.setValue(value);
/* 1277 */         resetEntry(entry, false);
/*      */       } 
/*      */       
/* 1280 */       return oldValue;
/*      */     } finally {
/* 1282 */       this.writeLock.unlock();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void resetEntry(ExpiringEntry<K, V> entry, boolean scheduleFirstEntry) {
/* 1295 */     this.writeLock.lock();
/*      */     try {
/* 1297 */       boolean scheduled = entry.cancel();
/* 1298 */       this.entries.reorder(entry);
/*      */       
/* 1300 */       if (scheduled || scheduleFirstEntry)
/* 1301 */         scheduleEntry(this.entries.first()); 
/*      */     } finally {
/* 1303 */       this.writeLock.unlock();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void scheduleEntry(ExpiringEntry<K, V> entry) {
/* 1314 */     if (entry == null || entry.scheduled) {
/*      */       return;
/*      */     }
/* 1317 */     Runnable runnable = null;
/* 1318 */     synchronized (entry) {
/* 1319 */       if (entry.scheduled) {
/*      */         return;
/*      */       }
/* 1322 */       final WeakReference<ExpiringEntry<K, V>> entryReference = new WeakReference<ExpiringEntry<K, V>>(entry);
/* 1323 */       runnable = new Runnable()
/*      */         {
/*      */           public void run() {
/* 1326 */             ExpiringMap.ExpiringEntry<K, V> entry = entryReference.get();
/*      */             
/* 1328 */             ExpiringMap.this.writeLock.lock();
/*      */             try {
/* 1330 */               if (entry != null && entry.scheduled) {
/* 1331 */                 ExpiringMap.this.entries.remove(entry.key);
/* 1332 */                 ExpiringMap.this.notifyListeners(entry);
/*      */               } 
/*      */ 
/*      */               
/*      */               try {
/* 1337 */                 Iterator<ExpiringMap.ExpiringEntry<K, V>> iterator = ExpiringMap.this.entries.valuesIterator();
/* 1338 */                 boolean schedulePending = true;
/*      */                 
/* 1340 */                 while (iterator.hasNext() && schedulePending) {
/* 1341 */                   ExpiringMap.ExpiringEntry<K, V> nextEntry = iterator.next();
/* 1342 */                   if (nextEntry.expectedExpiration.get() <= System.nanoTime()) {
/* 1343 */                     iterator.remove();
/* 1344 */                     ExpiringMap.this.notifyListeners(nextEntry); continue;
/*      */                   } 
/* 1346 */                   ExpiringMap.this.scheduleEntry(nextEntry);
/* 1347 */                   schedulePending = false;
/*      */                 }
/*      */               
/* 1350 */               } catch (NoSuchElementException noSuchElementException) {}
/*      */             } finally {
/*      */               
/* 1353 */               ExpiringMap.this.writeLock.unlock();
/*      */             } 
/*      */           }
/*      */         };
/*      */       
/* 1358 */       Future<?> entryFuture = EXPIRER.schedule(runnable, entry.expectedExpiration.get() - System.nanoTime(), TimeUnit.NANOSECONDS);
/*      */       
/* 1360 */       entry.schedule(entryFuture);
/*      */     } 
/*      */   }
/*      */   
/*      */   private static <K, V> Map.Entry<K, V> mapEntryFor(final ExpiringEntry<K, V> entry) {
/* 1365 */     return new Map.Entry<K, V>()
/*      */       {
/*      */         public K getKey() {
/* 1368 */           return entry.key;
/*      */         }
/*      */ 
/*      */         
/*      */         public V getValue() {
/* 1373 */           return entry.value;
/*      */         }
/*      */ 
/*      */         
/*      */         public V setValue(V value) {
/* 1378 */           throw new UnsupportedOperationException();
/*      */         }
/*      */       };
/*      */   }
/*      */   
/*      */   private void initListenerService() {
/* 1384 */     synchronized (ExpiringMap.class) {
/* 1385 */       if (LISTENER_SERVICE == null)
/* 1386 */         LISTENER_SERVICE = (ThreadPoolExecutor)Executors.newCachedThreadPool((THREAD_FACTORY == null) ? (ThreadFactory)new NamedThreadFactory("ExpiringMap-Listener-%s") : THREAD_FACTORY); 
/*      */     } 
/*      */   }
/*      */   
/*      */   private static interface EntryMap<K, V> extends Map<K, ExpiringEntry<K, V>> {
/*      */     ExpiringMap.ExpiringEntry<K, V> first();
/*      */     
/*      */     void reorder(ExpiringMap.ExpiringEntry<K, V> param1ExpiringEntry);
/*      */     
/*      */     Iterator<ExpiringMap.ExpiringEntry<K, V>> valuesIterator();
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\net\jodah\expiringmap\ExpiringMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */