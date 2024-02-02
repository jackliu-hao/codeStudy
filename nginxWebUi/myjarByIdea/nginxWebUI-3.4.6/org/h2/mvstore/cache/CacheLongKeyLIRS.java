package org.h2.mvstore.cache;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.h2.mvstore.DataUtils;

public class CacheLongKeyLIRS<V> {
   private long maxMemory;
   private final Segment<V>[] segments;
   private final int segmentCount;
   private final int segmentShift;
   private final int segmentMask;
   private final int stackMoveDistance;
   private final int nonResidentQueueSize;
   private final int nonResidentQueueSizeHigh;

   public CacheLongKeyLIRS(Config var1) {
      this.setMaxMemory(var1.maxMemory);
      var1.getClass();
      this.nonResidentQueueSize = 3;
      var1.getClass();
      this.nonResidentQueueSizeHigh = 12;
      DataUtils.checkArgument(Integer.bitCount(var1.segmentCount) == 1, "The segment count must be a power of 2, is {0}", var1.segmentCount);
      this.segmentCount = var1.segmentCount;
      this.segmentMask = this.segmentCount - 1;
      this.stackMoveDistance = var1.stackMoveDistance;
      this.segments = new Segment[this.segmentCount];
      this.clear();
      this.segmentShift = 32 - Integer.bitCount(this.segmentMask);
   }

   public void clear() {
      long var1 = this.getMaxItemSize();

      for(int var3 = 0; var3 < this.segmentCount; ++var3) {
         this.segments[var3] = new Segment(var1, this.stackMoveDistance, 8, this.nonResidentQueueSize, this.nonResidentQueueSizeHigh);
      }

   }

   public long getMaxItemSize() {
      return Math.max(1L, this.maxMemory / (long)this.segmentCount);
   }

   private Entry<V> find(long var1) {
      int var3 = getHash(var1);
      return this.getSegment(var3).find(var1, var3);
   }

   public boolean containsKey(long var1) {
      Entry var3 = this.find(var1);
      return var3 != null && var3.value != null;
   }

   public V peek(long var1) {
      Entry var3 = this.find(var1);
      return var3 == null ? null : var3.getValue();
   }

   public V put(long var1, V var3) {
      return this.put(var1, var3, this.sizeOf(var3));
   }

   public V put(long var1, V var3, int var4) {
      if (var3 == null) {
         throw DataUtils.newIllegalArgumentException("The value may not be null");
      } else {
         int var5 = getHash(var1);
         int var6 = this.getSegmentIndex(var5);
         Segment var7 = this.segments[var6];
         synchronized(var7) {
            var7 = this.resizeIfNeeded(var7, var6);
            return var7.put(var1, var5, var3, var4);
         }
      }
   }

   private Segment<V> resizeIfNeeded(Segment<V> var1, int var2) {
      int var3 = var1.getNewMapLen();
      if (var3 == 0) {
         return var1;
      } else {
         Segment var4 = this.segments[var2];
         if (var1 == var4) {
            var1 = new Segment(var1, var3);
            this.segments[var2] = var1;
         }

         return var1;
      }
   }

   protected int sizeOf(V var1) {
      return 1;
   }

   public V remove(long var1) {
      int var3 = getHash(var1);
      int var4 = this.getSegmentIndex(var3);
      Segment var5 = this.segments[var4];
      synchronized(var5) {
         var5 = this.resizeIfNeeded(var5, var4);
         return var5.remove(var1, var3);
      }
   }

   public int getMemory(long var1) {
      Entry var3 = this.find(var1);
      return var3 == null ? 0 : var3.getMemory();
   }

   public V get(long var1) {
      int var3 = getHash(var1);
      Segment var4 = this.getSegment(var3);
      Entry var5 = var4.find(var1, var3);
      return var4.get(var5);
   }

   private Segment<V> getSegment(int var1) {
      return this.segments[this.getSegmentIndex(var1)];
   }

   private int getSegmentIndex(int var1) {
      return var1 >>> this.segmentShift & this.segmentMask;
   }

   static int getHash(long var0) {
      int var2 = (int)(var0 >>> 32 ^ var0);
      var2 = (var2 >>> 16 ^ var2) * 73244475;
      var2 = (var2 >>> 16 ^ var2) * 73244475;
      var2 ^= var2 >>> 16;
      return var2;
   }

   public long getUsedMemory() {
      long var1 = 0L;
      Segment[] var3 = this.segments;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Segment var6 = var3[var5];
         var1 += var6.usedMemory;
      }

      return var1;
   }

   public void setMaxMemory(long var1) {
      DataUtils.checkArgument(var1 > 0L, "Max memory must be larger than 0, is {0}", var1);
      this.maxMemory = var1;
      if (this.segments != null) {
         long var3 = 1L + var1 / (long)this.segments.length;
         Segment[] var5 = this.segments;
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Segment var8 = var5[var7];
            var8.setMaxMemory(var3);
         }
      }

   }

   public long getMaxMemory() {
      return this.maxMemory;
   }

   public synchronized Set<Map.Entry<Long, V>> entrySet() {
      return this.getMap().entrySet();
   }

   public Set<Long> keySet() {
      HashSet var1 = new HashSet();
      Segment[] var2 = this.segments;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Segment var5 = var2[var4];
         var1.addAll(var5.keySet());
      }

      return var1;
   }

   public int sizeNonResident() {
      int var1 = 0;
      Segment[] var2 = this.segments;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Segment var5 = var2[var4];
         var1 += var5.queue2Size;
      }

      return var1;
   }

   public int sizeMapArray() {
      int var1 = 0;
      Segment[] var2 = this.segments;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Segment var5 = var2[var4];
         var1 += var5.entries.length;
      }

      return var1;
   }

   public int sizeHot() {
      int var1 = 0;
      Segment[] var2 = this.segments;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Segment var5 = var2[var4];
         var1 += var5.mapSize - var5.queueSize - var5.queue2Size;
      }

      return var1;
   }

   public long getHits() {
      long var1 = 0L;
      Segment[] var3 = this.segments;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Segment var6 = var3[var5];
         var1 += var6.hits;
      }

      return var1;
   }

   public long getMisses() {
      int var1 = 0;
      Segment[] var2 = this.segments;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Segment var5 = var2[var4];
         var1 = (int)((long)var1 + var5.misses);
      }

      return (long)var1;
   }

   public int size() {
      int var1 = 0;
      Segment[] var2 = this.segments;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Segment var5 = var2[var4];
         var1 += var5.mapSize - var5.queue2Size;
      }

      return var1;
   }

   public List<Long> keys(boolean var1, boolean var2) {
      ArrayList var3 = new ArrayList();
      Segment[] var4 = this.segments;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Segment var7 = var4[var6];
         var3.addAll(var7.keys(var1, var2));
      }

      return var3;
   }

   public List<V> values() {
      ArrayList var1 = new ArrayList();
      Iterator var2 = this.keySet().iterator();

      while(var2.hasNext()) {
         long var3 = (Long)var2.next();
         Object var5 = this.peek(var3);
         if (var5 != null) {
            var1.add(var5);
         }
      }

      return var1;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public boolean containsValue(V var1) {
      return this.getMap().containsValue(var1);
   }

   public Map<Long, V> getMap() {
      HashMap var1 = new HashMap();
      Iterator var2 = this.keySet().iterator();

      while(var2.hasNext()) {
         long var3 = (Long)var2.next();
         Object var5 = this.peek(var3);
         if (var5 != null) {
            var1.put(var3, var5);
         }
      }

      return var1;
   }

   public void putAll(Map<Long, ? extends V> var1) {
      Iterator var2 = var1.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         this.put((Long)var3.getKey(), var3.getValue());
      }

   }

   public void trimNonResidentQueue() {
      Segment[] var1 = this.segments;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Segment var4 = var1[var3];
         synchronized(var4) {
            var4.trimNonResidentQueue();
         }
      }

   }

   public static class Config {
      public long maxMemory = 1L;
      public int segmentCount = 16;
      public int stackMoveDistance = 32;
      public final int nonResidentQueueSize = 3;
      public final int nonResidentQueueSizeHigh = 12;
   }

   static class Entry<V> {
      final long key;
      V value;
      WeakReference<V> reference;
      final int memory;
      int topMove;
      Entry<V> stackNext;
      Entry<V> stackPrev;
      Entry<V> queueNext;
      Entry<V> queuePrev;
      Entry<V> mapNext;

      Entry() {
         this(0L, (Object)null, 0);
      }

      Entry(long var1, V var3, int var4) {
         this.key = var1;
         this.memory = var4;
         this.value = var3;
      }

      Entry(Entry<V> var1) {
         this(var1.key, var1.value, var1.memory);
         this.reference = var1.reference;
         this.topMove = var1.topMove;
      }

      boolean isHot() {
         return this.queueNext == null;
      }

      V getValue() {
         return this.value == null ? this.reference.get() : this.value;
      }

      int getMemory() {
         return this.value == null ? 0 : this.memory;
      }
   }

   private static class Segment<V> {
      int mapSize;
      int queueSize;
      int queue2Size;
      long hits;
      long misses;
      final Entry<V>[] entries;
      long usedMemory;
      private final int stackMoveDistance;
      private long maxMemory;
      private final int mask;
      private final int nonResidentQueueSize;
      private final int nonResidentQueueSizeHigh;
      private final Entry<V> stack;
      private int stackSize;
      private final Entry<V> queue;
      private final Entry<V> queue2;
      private int stackMoveCounter;

      Segment(long var1, int var3, int var4, int var5, int var6) {
         this.setMaxMemory(var1);
         this.stackMoveDistance = var3;
         this.nonResidentQueueSize = var5;
         this.nonResidentQueueSizeHigh = var6;
         this.mask = var4 - 1;
         this.stack = new Entry();
         this.stack.stackPrev = this.stack.stackNext = this.stack;
         this.queue = new Entry();
         this.queue.queuePrev = this.queue.queueNext = this.queue;
         this.queue2 = new Entry();
         this.queue2.queuePrev = this.queue2.queueNext = this.queue2;
         Entry[] var7 = new Entry[var4];
         this.entries = var7;
      }

      Segment(Segment<V> var1, int var2) {
         this(var1.maxMemory, var1.stackMoveDistance, var2, var1.nonResidentQueueSize, var1.nonResidentQueueSizeHigh);
         this.hits = var1.hits;
         this.misses = var1.misses;

         Entry var3;
         Entry var4;
         for(var3 = var1.stack.stackPrev; var3 != var1.stack; var3 = var3.stackPrev) {
            var4 = new Entry(var3);
            this.addToMap(var4);
            this.addToStack(var4);
         }

         for(var3 = var1.queue.queuePrev; var3 != var1.queue; var3 = var3.queuePrev) {
            var4 = this.find(var3.key, CacheLongKeyLIRS.getHash(var3.key));
            if (var4 == null) {
               var4 = new Entry(var3);
               this.addToMap(var4);
            }

            this.addToQueue(this.queue, var4);
         }

         for(var3 = var1.queue2.queuePrev; var3 != var1.queue2; var3 = var3.queuePrev) {
            var4 = this.find(var3.key, CacheLongKeyLIRS.getHash(var3.key));
            if (var4 == null) {
               var4 = new Entry(var3);
               this.addToMap(var4);
            }

            this.addToQueue(this.queue2, var4);
         }

      }

      int getNewMapLen() {
         int var1 = this.mask + 1;
         if (var1 * 3 < this.mapSize * 4 && var1 < 268435456) {
            return var1 * 2;
         } else {
            return var1 > 32 && var1 / 8 > this.mapSize ? var1 / 2 : 0;
         }
      }

      private void addToMap(Entry<V> var1) {
         int var2 = CacheLongKeyLIRS.getHash(var1.key) & this.mask;
         var1.mapNext = this.entries[var2];
         this.entries[var2] = var1;
         this.usedMemory += (long)var1.getMemory();
         ++this.mapSize;
      }

      synchronized V get(Entry<V> var1) {
         Object var2 = var1 == null ? null : var1.getValue();
         if (var2 == null) {
            ++this.misses;
         } else {
            this.access(var1);
            ++this.hits;
         }

         return var2;
      }

      private void access(Entry<V> var1) {
         if (var1.isHot()) {
            if (var1 != this.stack.stackNext && var1.stackNext != null && this.stackMoveCounter - var1.topMove > this.stackMoveDistance) {
               boolean var2 = var1 == this.stack.stackPrev;
               this.removeFromStack(var1);
               if (var2) {
                  this.pruneStack();
               }

               this.addToStack(var1);
            }
         } else {
            Object var3 = var1.getValue();
            if (var3 != null) {
               this.removeFromQueue(var1);
               if (var1.reference != null) {
                  var1.value = var3;
                  var1.reference = null;
                  this.usedMemory += (long)var1.memory;
               }

               if (var1.stackNext != null) {
                  this.removeFromStack(var1);
                  this.convertOldestHotToCold();
               } else {
                  this.addToQueue(this.queue, var1);
               }

               this.addToStack(var1);
               this.pruneStack();
            }
         }

      }

      synchronized V put(long var1, int var3, V var4, int var5) {
         Entry var6 = this.find(var1, var3);
         boolean var7 = var6 != null;
         Object var8 = null;
         if (var7) {
            var8 = var6.getValue();
            this.remove(var1, var3);
         }

         if ((long)var5 > this.maxMemory) {
            return var8;
         } else {
            var6 = new Entry(var1, var4, var5);
            int var9 = var3 & this.mask;
            var6.mapNext = this.entries[var9];
            this.entries[var9] = var6;
            this.usedMemory += (long)var5;
            if (this.usedMemory > this.maxMemory) {
               this.evict();
               if (this.stackSize > 0) {
                  this.addToQueue(this.queue, var6);
               }
            }

            ++this.mapSize;
            this.addToStack(var6);
            if (var7) {
               this.access(var6);
            }

            return var8;
         }
      }

      synchronized V remove(long var1, int var3) {
         int var4 = var3 & this.mask;
         Entry var5 = this.entries[var4];
         if (var5 == null) {
            return null;
         } else {
            if (var5.key == var1) {
               this.entries[var4] = var5.mapNext;
            } else {
               Entry var6;
               do {
                  var6 = var5;
                  var5 = var5.mapNext;
                  if (var5 == null) {
                     return null;
                  }
               } while(var5.key != var1);

               var6.mapNext = var5.mapNext;
            }

            Object var7 = var5.getValue();
            --this.mapSize;
            this.usedMemory -= (long)var5.getMemory();
            if (var5.stackNext != null) {
               this.removeFromStack(var5);
            }

            if (var5.isHot()) {
               var5 = this.queue.queueNext;
               if (var5 != this.queue) {
                  this.removeFromQueue(var5);
                  if (var5.stackNext == null) {
                     this.addToStackBottom(var5);
                  }
               }

               this.pruneStack();
            } else {
               this.removeFromQueue(var5);
            }

            return var7;
         }
      }

      private void evict() {
         do {
            this.evictBlock();
         } while(this.usedMemory > this.maxMemory);

      }

      private void evictBlock() {
         while(this.queueSize <= this.mapSize - this.queue2Size >>> 5 && this.stackSize > 0) {
            this.convertOldestHotToCold();
         }

         while(this.usedMemory > this.maxMemory && this.queueSize > 0) {
            Entry var1 = this.queue.queuePrev;
            this.usedMemory -= (long)var1.memory;
            this.removeFromQueue(var1);
            var1.reference = new WeakReference(var1.value);
            var1.value = null;
            this.addToQueue(this.queue2, var1);
            this.trimNonResidentQueue();
         }

      }

      void trimNonResidentQueue() {
         int var1 = this.mapSize - this.queue2Size;
         int var2 = this.nonResidentQueueSizeHigh * var1;
         int var3 = this.nonResidentQueueSize * var1;

         while(this.queue2Size > var3) {
            Entry var4 = this.queue2.queuePrev;
            if (this.queue2Size <= var2) {
               WeakReference var5 = var4.reference;
               if (var5 != null && var5.get() != null) {
                  break;
               }
            }

            int var6 = CacheLongKeyLIRS.getHash(var4.key);
            this.remove(var4.key, var6);
         }

      }

      private void convertOldestHotToCold() {
         Entry var1 = this.stack.stackPrev;
         if (var1 == this.stack) {
            throw new IllegalStateException();
         } else {
            this.removeFromStack(var1);
            this.addToQueue(this.queue, var1);
            this.pruneStack();
         }
      }

      private void pruneStack() {
         while(true) {
            Entry var1 = this.stack.stackPrev;
            if (var1.isHot()) {
               return;
            }

            this.removeFromStack(var1);
         }
      }

      Entry<V> find(long var1, int var3) {
         int var4 = var3 & this.mask;

         Entry var5;
         for(var5 = this.entries[var4]; var5 != null && var5.key != var1; var5 = var5.mapNext) {
         }

         return var5;
      }

      private void addToStack(Entry<V> var1) {
         var1.stackPrev = this.stack;
         var1.stackNext = this.stack.stackNext;
         var1.stackNext.stackPrev = var1;
         this.stack.stackNext = var1;
         ++this.stackSize;
         var1.topMove = this.stackMoveCounter++;
      }

      private void addToStackBottom(Entry<V> var1) {
         var1.stackNext = this.stack;
         var1.stackPrev = this.stack.stackPrev;
         var1.stackPrev.stackNext = var1;
         this.stack.stackPrev = var1;
         ++this.stackSize;
      }

      private void removeFromStack(Entry<V> var1) {
         var1.stackPrev.stackNext = var1.stackNext;
         var1.stackNext.stackPrev = var1.stackPrev;
         var1.stackPrev = var1.stackNext = null;
         --this.stackSize;
      }

      private void addToQueue(Entry<V> var1, Entry<V> var2) {
         var2.queuePrev = var1;
         var2.queueNext = var1.queueNext;
         var2.queueNext.queuePrev = var2;
         var1.queueNext = var2;
         if (var2.value != null) {
            ++this.queueSize;
         } else {
            ++this.queue2Size;
         }

      }

      private void removeFromQueue(Entry<V> var1) {
         var1.queuePrev.queueNext = var1.queueNext;
         var1.queueNext.queuePrev = var1.queuePrev;
         var1.queuePrev = var1.queueNext = null;
         if (var1.value != null) {
            --this.queueSize;
         } else {
            --this.queue2Size;
         }

      }

      synchronized List<Long> keys(boolean var1, boolean var2) {
         ArrayList var3 = new ArrayList();
         Entry var4;
         if (var1) {
            var4 = var2 ? this.queue2 : this.queue;

            for(Entry var5 = var4.queueNext; var5 != var4; var5 = var5.queueNext) {
               var3.add(var5.key);
            }
         } else {
            for(var4 = this.stack.stackNext; var4 != this.stack; var4 = var4.stackNext) {
               var3.add(var4.key);
            }
         }

         return var3;
      }

      synchronized Set<Long> keySet() {
         HashSet var1 = new HashSet();

         Entry var2;
         for(var2 = this.stack.stackNext; var2 != this.stack; var2 = var2.stackNext) {
            var1.add(var2.key);
         }

         for(var2 = this.queue.queueNext; var2 != this.queue; var2 = var2.queueNext) {
            var1.add(var2.key);
         }

         return var1;
      }

      void setMaxMemory(long var1) {
         this.maxMemory = var1;
      }
   }
}
