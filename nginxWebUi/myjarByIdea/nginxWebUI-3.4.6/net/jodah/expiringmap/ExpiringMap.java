package net.jodah.expiringmap;

import java.lang.ref.WeakReference;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.jodah.expiringmap.internal.Assert;
import net.jodah.expiringmap.internal.NamedThreadFactory;

public class ExpiringMap<K, V> implements ConcurrentMap<K, V> {
   static volatile ScheduledExecutorService EXPIRER;
   static volatile ThreadPoolExecutor LISTENER_SERVICE;
   static ThreadFactory THREAD_FACTORY;
   List<ExpirationListener<K, V>> expirationListeners;
   List<ExpirationListener<K, V>> asyncExpirationListeners;
   private AtomicLong expirationNanos;
   private int maxSize;
   private final AtomicReference<ExpirationPolicy> expirationPolicy;
   private final EntryLoader<? super K, ? extends V> entryLoader;
   private final ExpiringEntryLoader<? super K, ? extends V> expiringEntryLoader;
   private final ReadWriteLock readWriteLock;
   private final Lock readLock;
   private final Lock writeLock;
   private final EntryMap<K, V> entries;
   private final boolean variableExpiration;

   public static void setThreadFactory(ThreadFactory threadFactory) {
      THREAD_FACTORY = (ThreadFactory)Assert.notNull(threadFactory, "threadFactory");
   }

   private ExpiringMap(Builder<K, V> builder) {
      this.readWriteLock = new ReentrantReadWriteLock();
      this.readLock = this.readWriteLock.readLock();
      this.writeLock = this.readWriteLock.writeLock();
      if (EXPIRER == null) {
         Class var2 = ExpiringMap.class;
         synchronized(ExpiringMap.class) {
            if (EXPIRER == null) {
               EXPIRER = Executors.newSingleThreadScheduledExecutor((ThreadFactory)(THREAD_FACTORY == null ? new NamedThreadFactory("ExpiringMap-Expirer") : THREAD_FACTORY));
            }
         }
      }

      if (LISTENER_SERVICE == null && builder.asyncExpirationListeners != null) {
         this.initListenerService();
      }

      this.variableExpiration = builder.variableExpiration;
      this.entries = (EntryMap)(this.variableExpiration ? new EntryTreeHashMap() : new EntryLinkedHashMap());
      if (builder.expirationListeners != null) {
         this.expirationListeners = new CopyOnWriteArrayList(builder.expirationListeners);
      }

      if (builder.asyncExpirationListeners != null) {
         this.asyncExpirationListeners = new CopyOnWriteArrayList(builder.asyncExpirationListeners);
      }

      this.expirationPolicy = new AtomicReference(builder.expirationPolicy);
      this.expirationNanos = new AtomicLong(TimeUnit.NANOSECONDS.convert(builder.duration, builder.timeUnit));
      this.maxSize = builder.maxSize;
      this.entryLoader = builder.entryLoader;
      this.expiringEntryLoader = builder.expiringEntryLoader;
   }

   public static Builder<Object, Object> builder() {
      return new Builder();
   }

   public static <K, V> ExpiringMap<K, V> create() {
      return new ExpiringMap(builder());
   }

   public synchronized void addExpirationListener(ExpirationListener<K, V> listener) {
      Assert.notNull(listener, "listener");
      if (this.expirationListeners == null) {
         this.expirationListeners = new CopyOnWriteArrayList();
      }

      this.expirationListeners.add(listener);
   }

   public synchronized void addAsyncExpirationListener(ExpirationListener<K, V> listener) {
      Assert.notNull(listener, "listener");
      if (this.asyncExpirationListeners == null) {
         this.asyncExpirationListeners = new CopyOnWriteArrayList();
      }

      this.asyncExpirationListeners.add(listener);
      if (LISTENER_SERVICE == null) {
         this.initListenerService();
      }

   }

   public void clear() {
      this.writeLock.lock();

      try {
         Iterator var1 = this.entries.values().iterator();

         while(var1.hasNext()) {
            ExpiringEntry<K, V> entry = (ExpiringEntry)var1.next();
            entry.cancel();
         }

         this.entries.clear();
      } finally {
         this.writeLock.unlock();
      }
   }

   public boolean containsKey(Object key) {
      this.readLock.lock();

      boolean var2;
      try {
         var2 = this.entries.containsKey(key);
      } finally {
         this.readLock.unlock();
      }

      return var2;
   }

   public boolean containsValue(Object value) {
      this.readLock.lock();

      boolean var2;
      try {
         var2 = this.entries.containsValue(value);
      } finally {
         this.readLock.unlock();
      }

      return var2;
   }

   public Set<Map.Entry<K, V>> entrySet() {
      return new AbstractSet<Map.Entry<K, V>>() {
         public void clear() {
            ExpiringMap.this.clear();
         }

         public boolean contains(Object entry) {
            if (!(entry instanceof Map.Entry)) {
               return false;
            } else {
               Map.Entry<?, ?> e = (Map.Entry)entry;
               return ExpiringMap.this.containsKey(e.getKey());
            }
         }

         public Iterator<Map.Entry<K, V>> iterator() {
            return (Iterator)(ExpiringMap.this.entries instanceof EntryLinkedHashMap ? (EntryLinkedHashMap)ExpiringMap.this.entries.new EntryIterator() : (EntryTreeHashMap)ExpiringMap.this.entries.new EntryIterator());
         }

         public boolean remove(Object entry) {
            if (entry instanceof Map.Entry) {
               Map.Entry<?, ?> e = (Map.Entry)entry;
               return ExpiringMap.this.remove(e.getKey()) != null;
            } else {
               return false;
            }
         }

         public int size() {
            return ExpiringMap.this.size();
         }
      };
   }

   public boolean equals(Object obj) {
      this.readLock.lock();

      boolean var2;
      try {
         var2 = this.entries.equals(obj);
      } finally {
         this.readLock.unlock();
      }

      return var2;
   }

   public V get(Object key) {
      ExpiringEntry<K, V> entry = this.getEntry(key);
      if (entry == null) {
         return this.load(key);
      } else {
         if (ExpirationPolicy.ACCESSED.equals(entry.expirationPolicy.get())) {
            this.resetEntry(entry, false);
         }

         return entry.getValue();
      }
   }

   private V load(K key) {
      if (this.entryLoader == null && this.expiringEntryLoader == null) {
         return null;
      } else {
         this.writeLock.lock();

         Object var4;
         try {
            ExpiringEntry<K, V> entry = this.getEntry(key);
            Object value;
            if (entry != null) {
               value = entry.getValue();
               return value;
            }

            if (this.entryLoader != null) {
               value = this.entryLoader.load(key);
               this.put(key, value);
               var4 = value;
               return var4;
            }

            ExpiringValue<? extends V> expiringValue = this.expiringEntryLoader.load(key);
            if (expiringValue != null) {
               long duration = expiringValue.getTimeUnit() == null ? this.expirationNanos.get() : expiringValue.getDuration();
               TimeUnit timeUnit = expiringValue.getTimeUnit() == null ? TimeUnit.NANOSECONDS : expiringValue.getTimeUnit();
               this.put(key, expiringValue.getValue(), expiringValue.getExpirationPolicy() == null ? (ExpirationPolicy)this.expirationPolicy.get() : expiringValue.getExpirationPolicy(), duration, timeUnit);
               Object var7 = expiringValue.getValue();
               return var7;
            }

            this.put(key, (Object)null);
            var4 = null;
         } finally {
            this.writeLock.unlock();
         }

         return var4;
      }
   }

   public long getExpiration() {
      return TimeUnit.NANOSECONDS.toMillis(this.expirationNanos.get());
   }

   public long getExpiration(K key) {
      Assert.notNull(key, "key");
      ExpiringEntry<K, V> entry = this.getEntry(key);
      Assert.element(entry, key);
      return TimeUnit.NANOSECONDS.toMillis(entry.expirationNanos.get());
   }

   public ExpirationPolicy getExpirationPolicy(K key) {
      Assert.notNull(key, "key");
      ExpiringEntry<K, V> entry = this.getEntry(key);
      Assert.element(entry, key);
      return (ExpirationPolicy)entry.expirationPolicy.get();
   }

   public long getExpectedExpiration(K key) {
      Assert.notNull(key, "key");
      ExpiringEntry<K, V> entry = this.getEntry(key);
      Assert.element(entry, key);
      return TimeUnit.NANOSECONDS.toMillis(entry.expectedExpiration.get() - System.nanoTime());
   }

   public int getMaxSize() {
      return this.maxSize;
   }

   public int hashCode() {
      this.readLock.lock();

      int var1;
      try {
         var1 = this.entries.hashCode();
      } finally {
         this.readLock.unlock();
      }

      return var1;
   }

   public boolean isEmpty() {
      this.readLock.lock();

      boolean var1;
      try {
         var1 = this.entries.isEmpty();
      } finally {
         this.readLock.unlock();
      }

      return var1;
   }

   public Set<K> keySet() {
      return new AbstractSet<K>() {
         public void clear() {
            ExpiringMap.this.clear();
         }

         public boolean contains(Object key) {
            return ExpiringMap.this.containsKey(key);
         }

         public Iterator<K> iterator() {
            return (Iterator)(ExpiringMap.this.entries instanceof EntryLinkedHashMap ? (EntryLinkedHashMap)ExpiringMap.this.entries.new KeyIterator() : (EntryTreeHashMap)ExpiringMap.this.entries.new KeyIterator());
         }

         public boolean remove(Object value) {
            return ExpiringMap.this.remove(value) != null;
         }

         public int size() {
            return ExpiringMap.this.size();
         }
      };
   }

   public V put(K key, V value) {
      Assert.notNull(key, "key");
      return this.putInternal(key, value, (ExpirationPolicy)this.expirationPolicy.get(), this.expirationNanos.get());
   }

   public V put(K key, V value, ExpirationPolicy expirationPolicy) {
      return this.put(key, value, expirationPolicy, this.expirationNanos.get(), TimeUnit.NANOSECONDS);
   }

   public V put(K key, V value, long duration, TimeUnit timeUnit) {
      return this.put(key, value, (ExpirationPolicy)this.expirationPolicy.get(), duration, timeUnit);
   }

   public V put(K key, V value, ExpirationPolicy expirationPolicy, long duration, TimeUnit timeUnit) {
      Assert.notNull(key, "key");
      Assert.notNull(expirationPolicy, "expirationPolicy");
      Assert.notNull(timeUnit, "timeUnit");
      Assert.operation(this.variableExpiration, "Variable expiration is not enabled");
      return this.putInternal(key, value, expirationPolicy, TimeUnit.NANOSECONDS.convert(duration, timeUnit));
   }

   public void putAll(Map<? extends K, ? extends V> map) {
      Assert.notNull(map, "map");
      long expiration = this.expirationNanos.get();
      ExpirationPolicy expirationPolicy = (ExpirationPolicy)this.expirationPolicy.get();
      this.writeLock.lock();

      try {
         Iterator var5 = map.entrySet().iterator();

         while(var5.hasNext()) {
            Map.Entry<? extends K, ? extends V> entry = (Map.Entry)var5.next();
            this.putInternal(entry.getKey(), entry.getValue(), expirationPolicy, expiration);
         }
      } finally {
         this.writeLock.unlock();
      }

   }

   public V putIfAbsent(K key, V value) {
      Assert.notNull(key, "key");
      this.writeLock.lock();

      Object var3;
      try {
         if (this.entries.containsKey(key)) {
            var3 = ((ExpiringEntry)this.entries.get(key)).getValue();
            return var3;
         }

         var3 = this.putInternal(key, value, (ExpirationPolicy)this.expirationPolicy.get(), this.expirationNanos.get());
      } finally {
         this.writeLock.unlock();
      }

      return var3;
   }

   public V remove(Object key) {
      Assert.notNull(key, "key");
      this.writeLock.lock();

      Object var3;
      try {
         ExpiringEntry<K, V> entry = (ExpiringEntry)this.entries.remove(key);
         if (entry != null) {
            if (entry.cancel()) {
               this.scheduleEntry(this.entries.first());
            }

            var3 = entry.getValue();
            return var3;
         }

         var3 = null;
      } finally {
         this.writeLock.unlock();
      }

      return var3;
   }

   public boolean remove(Object key, Object value) {
      Assert.notNull(key, "key");
      this.writeLock.lock();

      boolean var4;
      try {
         ExpiringEntry<K, V> entry = (ExpiringEntry)this.entries.get(key);
         if (entry == null || !entry.getValue().equals(value)) {
            var4 = false;
            return var4;
         }

         this.entries.remove(key);
         if (entry.cancel()) {
            this.scheduleEntry(this.entries.first());
         }

         var4 = true;
      } finally {
         this.writeLock.unlock();
      }

      return var4;
   }

   public V replace(K key, V value) {
      Assert.notNull(key, "key");
      this.writeLock.lock();

      Object var3;
      try {
         if (this.entries.containsKey(key)) {
            var3 = this.putInternal(key, value, (ExpirationPolicy)this.expirationPolicy.get(), this.expirationNanos.get());
            return var3;
         }

         var3 = null;
      } finally {
         this.writeLock.unlock();
      }

      return var3;
   }

   public boolean replace(K key, V oldValue, V newValue) {
      Assert.notNull(key, "key");
      this.writeLock.lock();

      boolean var5;
      try {
         ExpiringEntry<K, V> entry = (ExpiringEntry)this.entries.get(key);
         if (entry != null && entry.getValue().equals(oldValue)) {
            this.putInternal(key, newValue, (ExpirationPolicy)this.expirationPolicy.get(), this.expirationNanos.get());
            var5 = true;
            return var5;
         }

         var5 = false;
      } finally {
         this.writeLock.unlock();
      }

      return var5;
   }

   public void removeExpirationListener(ExpirationListener<K, V> listener) {
      Assert.notNull(listener, "listener");

      for(int i = 0; i < this.expirationListeners.size(); ++i) {
         if (((ExpirationListener)this.expirationListeners.get(i)).equals(listener)) {
            this.expirationListeners.remove(i);
            return;
         }
      }

   }

   public void removeAsyncExpirationListener(ExpirationListener<K, V> listener) {
      Assert.notNull(listener, "listener");

      for(int i = 0; i < this.asyncExpirationListeners.size(); ++i) {
         if (((ExpirationListener)this.asyncExpirationListeners.get(i)).equals(listener)) {
            this.asyncExpirationListeners.remove(i);
            return;
         }
      }

   }

   public void resetExpiration(K key) {
      Assert.notNull(key, "key");
      ExpiringEntry<K, V> entry = this.getEntry(key);
      if (entry != null) {
         this.resetEntry(entry, false);
      }

   }

   public void setExpiration(K key, long duration, TimeUnit timeUnit) {
      Assert.notNull(key, "key");
      Assert.notNull(timeUnit, "timeUnit");
      Assert.operation(this.variableExpiration, "Variable expiration is not enabled");
      this.writeLock.lock();

      try {
         ExpiringEntry<K, V> entry = (ExpiringEntry)this.entries.get(key);
         if (entry != null) {
            entry.expirationNanos.set(TimeUnit.NANOSECONDS.convert(duration, timeUnit));
            this.resetEntry(entry, true);
         }
      } finally {
         this.writeLock.unlock();
      }

   }

   public void setExpiration(long duration, TimeUnit timeUnit) {
      Assert.notNull(timeUnit, "timeUnit");
      Assert.operation(this.variableExpiration, "Variable expiration is not enabled");
      this.expirationNanos.set(TimeUnit.NANOSECONDS.convert(duration, timeUnit));
   }

   public void setExpirationPolicy(ExpirationPolicy expirationPolicy) {
      Assert.notNull(expirationPolicy, "expirationPolicy");
      this.expirationPolicy.set(expirationPolicy);
   }

   public void setExpirationPolicy(K key, ExpirationPolicy expirationPolicy) {
      Assert.notNull(key, "key");
      Assert.notNull(expirationPolicy, "expirationPolicy");
      Assert.operation(this.variableExpiration, "Variable expiration is not enabled");
      ExpiringEntry<K, V> entry = this.getEntry(key);
      if (entry != null) {
         entry.expirationPolicy.set(expirationPolicy);
      }

   }

   public void setMaxSize(int maxSize) {
      Assert.operation(maxSize > 0, "maxSize");
      this.maxSize = maxSize;
   }

   public int size() {
      this.readLock.lock();

      int var1;
      try {
         var1 = this.entries.size();
      } finally {
         this.readLock.unlock();
      }

      return var1;
   }

   public String toString() {
      this.readLock.lock();

      String var1;
      try {
         var1 = this.entries.toString();
      } finally {
         this.readLock.unlock();
      }

      return var1;
   }

   public Collection<V> values() {
      return new AbstractCollection<V>() {
         public void clear() {
            ExpiringMap.this.clear();
         }

         public boolean contains(Object value) {
            return ExpiringMap.this.containsValue(value);
         }

         public Iterator<V> iterator() {
            return (Iterator)(ExpiringMap.this.entries instanceof EntryLinkedHashMap ? (EntryLinkedHashMap)ExpiringMap.this.entries.new ValueIterator() : (EntryTreeHashMap)ExpiringMap.this.entries.new ValueIterator());
         }

         public int size() {
            return ExpiringMap.this.size();
         }
      };
   }

   void notifyListeners(final ExpiringEntry<K, V> entry) {
      Iterator var2;
      final ExpirationListener listener;
      if (this.asyncExpirationListeners != null) {
         var2 = this.asyncExpirationListeners.iterator();

         while(var2.hasNext()) {
            listener = (ExpirationListener)var2.next();
            LISTENER_SERVICE.execute(new Runnable() {
               public void run() {
                  try {
                     listener.expired(entry.key, entry.getValue());
                  } catch (Exception var2) {
                  }

               }
            });
         }
      }

      if (this.expirationListeners != null) {
         var2 = this.expirationListeners.iterator();

         while(var2.hasNext()) {
            listener = (ExpirationListener)var2.next();

            try {
               listener.expired(entry.key, entry.getValue());
            } catch (Exception var5) {
            }
         }
      }

   }

   ExpiringEntry<K, V> getEntry(Object key) {
      this.readLock.lock();

      ExpiringEntry var2;
      try {
         var2 = (ExpiringEntry)this.entries.get(key);
      } finally {
         this.readLock.unlock();
      }

      return var2;
   }

   V putInternal(K key, V value, ExpirationPolicy expirationPolicy, long expirationNanos) {
      this.writeLock.lock();

      try {
         ExpiringEntry<K, V> entry = (ExpiringEntry)this.entries.get(key);
         V oldValue = null;
         Object var8;
         if (entry == null) {
            entry = new ExpiringEntry(key, value, this.variableExpiration ? new AtomicReference(expirationPolicy) : this.expirationPolicy, this.variableExpiration ? new AtomicLong(expirationNanos) : this.expirationNanos);
            if (this.entries.size() >= this.maxSize) {
               ExpiringEntry<K, V> expiredEntry = this.entries.first();
               this.entries.remove(expiredEntry.key);
               this.notifyListeners(expiredEntry);
            }

            this.entries.put(key, entry);
            if (this.entries.size() == 1 || this.entries.first().equals(entry)) {
               this.scheduleEntry(entry);
            }
         } else {
            oldValue = entry.getValue();
            if (!ExpirationPolicy.ACCESSED.equals(expirationPolicy) && (oldValue == null && value == null || oldValue != null && oldValue.equals(value))) {
               var8 = value;
               return var8;
            }

            entry.setValue(value);
            this.resetEntry(entry, false);
         }

         var8 = oldValue;
         return var8;
      } finally {
         this.writeLock.unlock();
      }
   }

   void resetEntry(ExpiringEntry<K, V> entry, boolean scheduleFirstEntry) {
      this.writeLock.lock();

      try {
         boolean scheduled = entry.cancel();
         this.entries.reorder(entry);
         if (scheduled || scheduleFirstEntry) {
            this.scheduleEntry(this.entries.first());
         }
      } finally {
         this.writeLock.unlock();
      }

   }

   void scheduleEntry(ExpiringEntry<K, V> entry) {
      if (entry != null && !entry.scheduled) {
         Runnable runnable = null;
         synchronized(entry) {
            if (!entry.scheduled) {
               final WeakReference<ExpiringEntry<K, V>> entryReference = new WeakReference(entry);
               runnable = new Runnable() {
                  public void run() {
                     ExpiringEntry<K, V> entry = (ExpiringEntry)entryReference.get();
                     ExpiringMap.this.writeLock.lock();

                     try {
                        if (entry != null && entry.scheduled) {
                           ExpiringMap.this.entries.remove(entry.key);
                           ExpiringMap.this.notifyListeners(entry);
                        }

                        try {
                           Iterator<ExpiringEntry<K, V>> iterator = ExpiringMap.this.entries.valuesIterator();
                           boolean schedulePending = true;

                           while(iterator.hasNext() && schedulePending) {
                              ExpiringEntry<K, V> nextEntry = (ExpiringEntry)iterator.next();
                              if (nextEntry.expectedExpiration.get() <= System.nanoTime()) {
                                 iterator.remove();
                                 ExpiringMap.this.notifyListeners(nextEntry);
                              } else {
                                 ExpiringMap.this.scheduleEntry(nextEntry);
                                 schedulePending = false;
                              }
                           }
                        } catch (NoSuchElementException var8) {
                        }
                     } finally {
                        ExpiringMap.this.writeLock.unlock();
                     }

                  }
               };
               Future<?> entryFuture = EXPIRER.schedule(runnable, entry.expectedExpiration.get() - System.nanoTime(), TimeUnit.NANOSECONDS);
               entry.schedule(entryFuture);
            }
         }
      }
   }

   private static <K, V> Map.Entry<K, V> mapEntryFor(final ExpiringEntry<K, V> entry) {
      return new Map.Entry<K, V>() {
         public K getKey() {
            return entry.key;
         }

         public V getValue() {
            return entry.value;
         }

         public V setValue(V value) {
            throw new UnsupportedOperationException();
         }
      };
   }

   private void initListenerService() {
      Class var1 = ExpiringMap.class;
      synchronized(ExpiringMap.class) {
         if (LISTENER_SERVICE == null) {
            LISTENER_SERVICE = (ThreadPoolExecutor)Executors.newCachedThreadPool((ThreadFactory)(THREAD_FACTORY == null ? new NamedThreadFactory("ExpiringMap-Listener-%s") : THREAD_FACTORY));
         }

      }
   }

   // $FF: synthetic method
   ExpiringMap(Builder x0, Object x1) {
      this(x0);
   }

   static class ExpiringEntry<K, V> implements Comparable<ExpiringEntry<K, V>> {
      final AtomicLong expirationNanos;
      final AtomicLong expectedExpiration;
      final AtomicReference<ExpirationPolicy> expirationPolicy;
      final K key;
      volatile Future<?> entryFuture;
      V value;
      volatile boolean scheduled;

      ExpiringEntry(K key, V value, AtomicReference<ExpirationPolicy> expirationPolicy, AtomicLong expirationNanos) {
         this.key = key;
         this.value = value;
         this.expirationPolicy = expirationPolicy;
         this.expirationNanos = expirationNanos;
         this.expectedExpiration = new AtomicLong();
         this.resetExpiration();
      }

      public int compareTo(ExpiringEntry<K, V> other) {
         if (this.key.equals(other.key)) {
            return 0;
         } else {
            return this.expectedExpiration.get() < other.expectedExpiration.get() ? -1 : 1;
         }
      }

      public int hashCode() {
         int prime = true;
         int result = 1;
         result = 31 * result + (this.key == null ? 0 : this.key.hashCode());
         result = 31 * result + (this.value == null ? 0 : this.value.hashCode());
         return result;
      }

      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (obj == null) {
            return false;
         } else if (this.getClass() != obj.getClass()) {
            return false;
         } else {
            ExpiringEntry<?, ?> other = (ExpiringEntry)obj;
            if (!this.key.equals(other.key)) {
               return false;
            } else {
               if (this.value == null) {
                  if (other.value != null) {
                     return false;
                  }
               } else if (!this.value.equals(other.value)) {
                  return false;
               }

               return true;
            }
         }
      }

      public String toString() {
         return this.value.toString();
      }

      synchronized boolean cancel() {
         boolean result = this.scheduled;
         if (this.entryFuture != null) {
            this.entryFuture.cancel(false);
         }

         this.entryFuture = null;
         this.scheduled = false;
         return result;
      }

      synchronized V getValue() {
         return this.value;
      }

      void resetExpiration() {
         this.expectedExpiration.set(this.expirationNanos.get() + System.nanoTime());
      }

      synchronized void schedule(Future<?> entryFuture) {
         this.entryFuture = entryFuture;
         this.scheduled = true;
      }

      synchronized void setValue(V value) {
         this.value = value;
      }
   }

   private static class EntryTreeHashMap<K, V> extends HashMap<K, ExpiringEntry<K, V>> implements EntryMap<K, V> {
      private static final long serialVersionUID = 1L;
      SortedSet<ExpiringEntry<K, V>> sortedSet;

      private EntryTreeHashMap() {
         this.sortedSet = new ConcurrentSkipListSet();
      }

      public void clear() {
         super.clear();
         this.sortedSet.clear();
      }

      public boolean containsValue(Object value) {
         Iterator var2 = this.values().iterator();

         Object v;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            ExpiringEntry<K, V> entry = (ExpiringEntry)var2.next();
            v = entry.value;
         } while(v != value && (value == null || !value.equals(v)));

         return true;
      }

      public ExpiringEntry<K, V> first() {
         return this.sortedSet.isEmpty() ? null : (ExpiringEntry)this.sortedSet.first();
      }

      public ExpiringEntry<K, V> put(K key, ExpiringEntry<K, V> value) {
         this.sortedSet.add(value);
         return (ExpiringEntry)super.put(key, value);
      }

      public ExpiringEntry<K, V> remove(Object key) {
         ExpiringEntry<K, V> entry = (ExpiringEntry)super.remove(key);
         if (entry != null) {
            this.sortedSet.remove(entry);
         }

         return entry;
      }

      public void reorder(ExpiringEntry<K, V> value) {
         this.sortedSet.remove(value);
         value.resetExpiration();
         this.sortedSet.add(value);
      }

      public Iterator<ExpiringEntry<K, V>> valuesIterator() {
         return new ExpiringEntryIterator();
      }

      // $FF: synthetic method
      EntryTreeHashMap(Object x0) {
         this();
      }

      final class EntryIterator extends EntryTreeHashMap<K, V>.AbstractHashIterator implements Iterator<Map.Entry<K, V>> {
         EntryIterator() {
            super();
         }

         public final Map.Entry<K, V> next() {
            return ExpiringMap.mapEntryFor(this.getNext());
         }
      }

      final class ValueIterator extends EntryTreeHashMap<K, V>.AbstractHashIterator implements Iterator<V> {
         ValueIterator() {
            super();
         }

         public final V next() {
            return this.getNext().value;
         }
      }

      final class KeyIterator extends EntryTreeHashMap<K, V>.AbstractHashIterator implements Iterator<K> {
         KeyIterator() {
            super();
         }

         public final K next() {
            return this.getNext().key;
         }
      }

      final class ExpiringEntryIterator extends EntryTreeHashMap<K, V>.AbstractHashIterator implements Iterator<ExpiringEntry<K, V>> {
         ExpiringEntryIterator() {
            super();
         }

         public final ExpiringEntry<K, V> next() {
            return this.getNext();
         }
      }

      abstract class AbstractHashIterator {
         private final Iterator<ExpiringEntry<K, V>> iterator;
         protected ExpiringEntry<K, V> next;

         AbstractHashIterator() {
            this.iterator = EntryTreeHashMap.this.sortedSet.iterator();
         }

         public boolean hasNext() {
            return this.iterator.hasNext();
         }

         public ExpiringEntry<K, V> getNext() {
            this.next = (ExpiringEntry)this.iterator.next();
            return this.next;
         }

         public void remove() {
            ExpiringMap.EntryTreeHashMap.super.remove(this.next.key);
            this.iterator.remove();
         }
      }
   }

   private static class EntryLinkedHashMap<K, V> extends LinkedHashMap<K, ExpiringEntry<K, V>> implements EntryMap<K, V> {
      private static final long serialVersionUID = 1L;

      private EntryLinkedHashMap() {
      }

      public boolean containsValue(Object value) {
         Iterator var2 = this.values().iterator();

         Object v;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            ExpiringEntry<K, V> entry = (ExpiringEntry)var2.next();
            v = entry.value;
         } while(v != value && (value == null || !value.equals(v)));

         return true;
      }

      public ExpiringEntry<K, V> first() {
         return this.isEmpty() ? null : (ExpiringEntry)this.values().iterator().next();
      }

      public void reorder(ExpiringEntry<K, V> value) {
         this.remove(value.key);
         value.resetExpiration();
         this.put(value.key, value);
      }

      public Iterator<ExpiringEntry<K, V>> valuesIterator() {
         return this.values().iterator();
      }

      // $FF: synthetic method
      EntryLinkedHashMap(Object x0) {
         this();
      }

      public final class EntryIterator extends EntryLinkedHashMap<K, V>.AbstractHashIterator implements Iterator<Map.Entry<K, V>> {
         public EntryIterator() {
            super();
         }

         public final Map.Entry<K, V> next() {
            return ExpiringMap.mapEntryFor(this.getNext());
         }
      }

      final class ValueIterator extends EntryLinkedHashMap<K, V>.AbstractHashIterator implements Iterator<V> {
         ValueIterator() {
            super();
         }

         public final V next() {
            return this.getNext().value;
         }
      }

      final class KeyIterator extends EntryLinkedHashMap<K, V>.AbstractHashIterator implements Iterator<K> {
         KeyIterator() {
            super();
         }

         public final K next() {
            return this.getNext().key;
         }
      }

      abstract class AbstractHashIterator {
         private final Iterator<Map.Entry<K, ExpiringEntry<K, V>>> iterator = EntryLinkedHashMap.this.entrySet().iterator();
         private ExpiringEntry<K, V> next;

         public boolean hasNext() {
            return this.iterator.hasNext();
         }

         public ExpiringEntry<K, V> getNext() {
            this.next = (ExpiringEntry)((Map.Entry)this.iterator.next()).getValue();
            return this.next;
         }

         public void remove() {
            this.iterator.remove();
         }
      }
   }

   private interface EntryMap<K, V> extends Map<K, ExpiringEntry<K, V>> {
      ExpiringEntry<K, V> first();

      void reorder(ExpiringEntry<K, V> var1);

      Iterator<ExpiringEntry<K, V>> valuesIterator();
   }

   public static final class Builder<K, V> {
      private ExpirationPolicy expirationPolicy;
      private List<ExpirationListener<K, V>> expirationListeners;
      private List<ExpirationListener<K, V>> asyncExpirationListeners;
      private TimeUnit timeUnit;
      private boolean variableExpiration;
      private long duration;
      private int maxSize;
      private EntryLoader<K, V> entryLoader;
      private ExpiringEntryLoader<K, V> expiringEntryLoader;

      private Builder() {
         this.expirationPolicy = ExpirationPolicy.CREATED;
         this.timeUnit = TimeUnit.SECONDS;
         this.duration = 60L;
         this.maxSize = Integer.MAX_VALUE;
      }

      public <K1 extends K, V1 extends V> ExpiringMap<K1, V1> build() {
         return new ExpiringMap(this);
      }

      public Builder<K, V> expiration(long duration, TimeUnit timeUnit) {
         this.duration = duration;
         this.timeUnit = (TimeUnit)Assert.notNull(timeUnit, "timeUnit");
         return this;
      }

      public Builder<K, V> maxSize(int maxSize) {
         Assert.operation(maxSize > 0, "maxSize");
         this.maxSize = maxSize;
         return this;
      }

      public <K1 extends K, V1 extends V> Builder<K1, V1> entryLoader(EntryLoader<? super K1, ? super V1> loader) {
         this.assertNoLoaderSet();
         this.entryLoader = (EntryLoader)Assert.notNull(loader, "loader");
         return this;
      }

      public <K1 extends K, V1 extends V> Builder<K1, V1> expiringEntryLoader(ExpiringEntryLoader<? super K1, ? super V1> loader) {
         this.assertNoLoaderSet();
         this.expiringEntryLoader = (ExpiringEntryLoader)Assert.notNull(loader, "loader");
         this.variableExpiration();
         return this;
      }

      public <K1 extends K, V1 extends V> Builder<K1, V1> expirationListener(ExpirationListener<? super K1, ? super V1> listener) {
         Assert.notNull(listener, "listener");
         if (this.expirationListeners == null) {
            this.expirationListeners = new ArrayList();
         }

         this.expirationListeners.add(listener);
         return this;
      }

      public <K1 extends K, V1 extends V> Builder<K1, V1> expirationListeners(List<ExpirationListener<? super K1, ? super V1>> listeners) {
         Assert.notNull(listeners, "listeners");
         if (this.expirationListeners == null) {
            this.expirationListeners = new ArrayList(listeners.size());
         }

         Iterator var2 = listeners.iterator();

         while(var2.hasNext()) {
            ExpirationListener<? super K1, ? super V1> listener = (ExpirationListener)var2.next();
            this.expirationListeners.add(listener);
         }

         return this;
      }

      public <K1 extends K, V1 extends V> Builder<K1, V1> asyncExpirationListener(ExpirationListener<? super K1, ? super V1> listener) {
         Assert.notNull(listener, "listener");
         if (this.asyncExpirationListeners == null) {
            this.asyncExpirationListeners = new ArrayList();
         }

         this.asyncExpirationListeners.add(listener);
         return this;
      }

      public <K1 extends K, V1 extends V> Builder<K1, V1> asyncExpirationListeners(List<ExpirationListener<? super K1, ? super V1>> listeners) {
         Assert.notNull(listeners, "listeners");
         if (this.asyncExpirationListeners == null) {
            this.asyncExpirationListeners = new ArrayList(listeners.size());
         }

         Iterator var2 = listeners.iterator();

         while(var2.hasNext()) {
            ExpirationListener<? super K1, ? super V1> listener = (ExpirationListener)var2.next();
            this.asyncExpirationListeners.add(listener);
         }

         return this;
      }

      public Builder<K, V> expirationPolicy(ExpirationPolicy expirationPolicy) {
         this.expirationPolicy = (ExpirationPolicy)Assert.notNull(expirationPolicy, "expirationPolicy");
         return this;
      }

      public Builder<K, V> variableExpiration() {
         this.variableExpiration = true;
         return this;
      }

      private void assertNoLoaderSet() {
         Assert.state(this.entryLoader == null && this.expiringEntryLoader == null, "Either entryLoader or expiringEntryLoader may be set, not both");
      }

      // $FF: synthetic method
      Builder(Object x0) {
         this();
      }
   }
}
