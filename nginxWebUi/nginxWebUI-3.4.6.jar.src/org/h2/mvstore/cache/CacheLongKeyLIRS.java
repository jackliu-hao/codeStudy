/*      */ package org.h2.mvstore.cache;
/*      */ 
/*      */ import java.lang.ref.WeakReference;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import org.h2.mvstore.DataUtils;
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
/*      */ 
/*      */ 
/*      */ public class CacheLongKeyLIRS<V>
/*      */ {
/*      */   private long maxMemory;
/*      */   private final Segment<V>[] segments;
/*      */   private final int segmentCount;
/*      */   private final int segmentShift;
/*      */   private final int segmentMask;
/*      */   private final int stackMoveDistance;
/*      */   private final int nonResidentQueueSize;
/*      */   private final int nonResidentQueueSizeHigh;
/*      */   
/*      */   public CacheLongKeyLIRS(Config paramConfig) {
/*   69 */     setMaxMemory(paramConfig.maxMemory);
/*   70 */     paramConfig.getClass(); this.nonResidentQueueSize = 3;
/*   71 */     paramConfig.getClass(); this.nonResidentQueueSizeHigh = 12;
/*   72 */     DataUtils.checkArgument(
/*   73 */         (Integer.bitCount(paramConfig.segmentCount) == 1), "The segment count must be a power of 2, is {0}", new Object[] {
/*   74 */           Integer.valueOf(paramConfig.segmentCount) });
/*   75 */     this.segmentCount = paramConfig.segmentCount;
/*   76 */     this.segmentMask = this.segmentCount - 1;
/*   77 */     this.stackMoveDistance = paramConfig.stackMoveDistance;
/*   78 */     this.segments = (Segment<V>[])new Segment[this.segmentCount];
/*   79 */     clear();
/*      */     
/*   81 */     this.segmentShift = 32 - Integer.bitCount(this.segmentMask);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void clear() {
/*   88 */     long l = getMaxItemSize();
/*   89 */     for (byte b = 0; b < this.segmentCount; b++) {
/*   90 */       this.segments[b] = new Segment<>(l, this.stackMoveDistance, 8, this.nonResidentQueueSize, this.nonResidentQueueSizeHigh);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getMaxItemSize() {
/*  100 */     return Math.max(1L, this.maxMemory / this.segmentCount);
/*      */   }
/*      */   
/*      */   private Entry<V> find(long paramLong) {
/*  104 */     int i = getHash(paramLong);
/*  105 */     return getSegment(i).find(paramLong, i);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean containsKey(long paramLong) {
/*  116 */     Entry<V> entry = find(paramLong);
/*  117 */     return (entry != null && entry.value != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public V peek(long paramLong) {
/*  128 */     Entry<V> entry = find(paramLong);
/*  129 */     return (entry == null) ? null : entry.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public V put(long paramLong, V paramV) {
/*  140 */     return put(paramLong, paramV, sizeOf(paramV));
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
/*      */   public V put(long paramLong, V paramV, int paramInt) {
/*  154 */     if (paramV == null) {
/*  155 */       throw DataUtils.newIllegalArgumentException("The value may not be null", new Object[0]);
/*      */     }
/*      */     
/*  158 */     int i = getHash(paramLong);
/*  159 */     int j = getSegmentIndex(i);
/*  160 */     Segment<V> segment = this.segments[j];
/*      */ 
/*      */ 
/*      */     
/*  164 */     synchronized (segment) {
/*  165 */       segment = resizeIfNeeded(segment, j);
/*  166 */       return segment.put(paramLong, i, paramV, paramInt);
/*      */     } 
/*      */   }
/*      */   
/*      */   private Segment<V> resizeIfNeeded(Segment<V> paramSegment, int paramInt) {
/*  171 */     int i = paramSegment.getNewMapLen();
/*  172 */     if (i == 0) {
/*  173 */       return paramSegment;
/*      */     }
/*      */ 
/*      */     
/*  177 */     Segment<V> segment = this.segments[paramInt];
/*  178 */     if (paramSegment == segment) {
/*      */       
/*  180 */       paramSegment = new Segment<>(paramSegment, i);
/*  181 */       this.segments[paramInt] = paramSegment;
/*      */     } 
/*  183 */     return paramSegment;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int sizeOf(V paramV) {
/*  194 */     return 1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public V remove(long paramLong) {
/*  205 */     int i = getHash(paramLong);
/*  206 */     int j = getSegmentIndex(i);
/*  207 */     Segment<V> segment = this.segments[j];
/*      */ 
/*      */ 
/*      */     
/*  211 */     synchronized (segment) {
/*  212 */       segment = resizeIfNeeded(segment, j);
/*  213 */       return segment.remove(paramLong, i);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMemory(long paramLong) {
/*  224 */     Entry<V> entry = find(paramLong);
/*  225 */     return (entry == null) ? 0 : entry.getMemory();
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
/*      */   public V get(long paramLong) {
/*  237 */     int i = getHash(paramLong);
/*  238 */     Segment<V> segment = getSegment(i);
/*  239 */     Entry<V> entry = segment.find(paramLong, i);
/*  240 */     return segment.get(entry);
/*      */   }
/*      */   
/*      */   private Segment<V> getSegment(int paramInt) {
/*  244 */     return this.segments[getSegmentIndex(paramInt)];
/*      */   }
/*      */   
/*      */   private int getSegmentIndex(int paramInt) {
/*  248 */     return paramInt >>> this.segmentShift & this.segmentMask;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static int getHash(long paramLong) {
/*  259 */     int i = (int)(paramLong >>> 32L ^ paramLong);
/*      */ 
/*      */     
/*  262 */     i = (i >>> 16 ^ i) * 73244475;
/*  263 */     i = (i >>> 16 ^ i) * 73244475;
/*  264 */     i = i >>> 16 ^ i;
/*  265 */     return i;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getUsedMemory() {
/*  274 */     long l = 0L;
/*  275 */     for (Segment<V> segment : this.segments) {
/*  276 */       l += segment.usedMemory;
/*      */     }
/*  278 */     return l;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setMaxMemory(long paramLong) {
/*  289 */     DataUtils.checkArgument((paramLong > 0L), "Max memory must be larger than 0, is {0}", new Object[] {
/*      */           
/*  291 */           Long.valueOf(paramLong) });
/*  292 */     this.maxMemory = paramLong;
/*  293 */     if (this.segments != null) {
/*  294 */       long l = 1L + paramLong / this.segments.length;
/*  295 */       for (Segment<V> segment : this.segments) {
/*  296 */         segment.setMaxMemory(l);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getMaxMemory() {
/*  307 */     return this.maxMemory;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Set<Map.Entry<Long, V>> entrySet() {
/*  316 */     return getMap().entrySet();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Set<Long> keySet() {
/*  325 */     HashSet<Long> hashSet = new HashSet();
/*  326 */     for (Segment<V> segment : this.segments) {
/*  327 */       hashSet.addAll(segment.keySet());
/*      */     }
/*  329 */     return hashSet;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int sizeNonResident() {
/*  338 */     int i = 0;
/*  339 */     for (Segment<V> segment : this.segments) {
/*  340 */       i += segment.queue2Size;
/*      */     }
/*  342 */     return i;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int sizeMapArray() {
/*  351 */     int i = 0;
/*  352 */     for (Segment<V> segment : this.segments) {
/*  353 */       i += segment.entries.length;
/*      */     }
/*  355 */     return i;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int sizeHot() {
/*  364 */     int i = 0;
/*  365 */     for (Segment<V> segment : this.segments) {
/*  366 */       i += segment.mapSize - segment.queueSize - segment.queue2Size;
/*      */     }
/*  368 */     return i;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getHits() {
/*  377 */     long l = 0L;
/*  378 */     for (Segment<V> segment : this.segments) {
/*  379 */       l += segment.hits;
/*      */     }
/*  381 */     return l;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getMisses() {
/*  390 */     int i = 0;
/*  391 */     for (Segment<V> segment : this.segments) {
/*  392 */       i = (int)(i + segment.misses);
/*      */     }
/*  394 */     return i;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int size() {
/*  403 */     int i = 0;
/*  404 */     for (Segment<V> segment : this.segments) {
/*  405 */       i += segment.mapSize - segment.queue2Size;
/*      */     }
/*  407 */     return i;
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
/*      */   public List<Long> keys(boolean paramBoolean1, boolean paramBoolean2) {
/*  419 */     ArrayList<Long> arrayList = new ArrayList();
/*  420 */     for (Segment<V> segment : this.segments) {
/*  421 */       arrayList.addAll(segment.keys(paramBoolean1, paramBoolean2));
/*      */     }
/*  423 */     return arrayList;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<V> values() {
/*  432 */     ArrayList<V> arrayList = new ArrayList();
/*  433 */     for (Iterator<Long> iterator = keySet().iterator(); iterator.hasNext(); ) { long l = ((Long)iterator.next()).longValue();
/*  434 */       V v = peek(l);
/*  435 */       if (v != null) {
/*  436 */         arrayList.add(v);
/*      */       } }
/*      */     
/*  439 */     return arrayList;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isEmpty() {
/*  448 */     return (size() == 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean containsValue(V paramV) {
/*  458 */     return getMap().containsValue(paramV);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Map<Long, V> getMap() {
/*  467 */     HashMap<Object, Object> hashMap = new HashMap<>();
/*  468 */     for (Iterator<Long> iterator = keySet().iterator(); iterator.hasNext(); ) { long l = ((Long)iterator.next()).longValue();
/*  469 */       V v = peek(l);
/*  470 */       if (v != null) {
/*  471 */         hashMap.put(Long.valueOf(l), v);
/*      */       } }
/*      */     
/*  474 */     return (Map)hashMap;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void putAll(Map<Long, ? extends V> paramMap) {
/*  483 */     for (Map.Entry<Long, ? extends V> entry : paramMap.entrySet())
/*      */     {
/*  485 */       put(((Long)entry.getKey()).longValue(), (V)entry.getValue());
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void trimNonResidentQueue() {
/*  493 */     for (Segment<V> segment : this.segments) {
/*  494 */       synchronized (segment) {
/*  495 */         segment.trimNonResidentQueue();
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
/*      */   private static class Segment<V>
/*      */   {
/*      */     int mapSize;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     int queueSize;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     int queue2Size;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     long hits;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     long misses;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     final CacheLongKeyLIRS.Entry<V>[] entries;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     long usedMemory;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private final int stackMoveDistance;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private long maxMemory;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private final int mask;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private final int nonResidentQueueSize;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private final int nonResidentQueueSizeHigh;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private final CacheLongKeyLIRS.Entry<V> stack;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private int stackSize;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private final CacheLongKeyLIRS.Entry<V> queue;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private final CacheLongKeyLIRS.Entry<V> queue2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private int stackMoveCounter;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     Segment(long param1Long, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
/*  616 */       setMaxMemory(param1Long);
/*  617 */       this.stackMoveDistance = param1Int1;
/*  618 */       this.nonResidentQueueSize = param1Int3;
/*  619 */       this.nonResidentQueueSizeHigh = param1Int4;
/*      */ 
/*      */       
/*  622 */       this.mask = param1Int2 - 1;
/*      */ 
/*      */       
/*  625 */       this.stack = new CacheLongKeyLIRS.Entry<>();
/*  626 */       this.stack.stackPrev = this.stack.stackNext = this.stack;
/*  627 */       this.queue = new CacheLongKeyLIRS.Entry<>();
/*  628 */       this.queue.queuePrev = this.queue.queueNext = this.queue;
/*  629 */       this.queue2 = new CacheLongKeyLIRS.Entry<>();
/*  630 */       this.queue2.queuePrev = this.queue2.queueNext = this.queue2;
/*      */ 
/*      */       
/*  633 */       CacheLongKeyLIRS.Entry[] arrayOfEntry = new CacheLongKeyLIRS.Entry[param1Int2];
/*  634 */       this.entries = (CacheLongKeyLIRS.Entry<V>[])arrayOfEntry;
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
/*      */     Segment(Segment<V> param1Segment, int param1Int) {
/*  646 */       this(param1Segment.maxMemory, param1Segment.stackMoveDistance, param1Int, param1Segment.nonResidentQueueSize, param1Segment.nonResidentQueueSizeHigh);
/*      */       
/*  648 */       this.hits = param1Segment.hits;
/*  649 */       this.misses = param1Segment.misses;
/*  650 */       CacheLongKeyLIRS.Entry<V> entry = param1Segment.stack.stackPrev;
/*  651 */       while (entry != param1Segment.stack) {
/*  652 */         CacheLongKeyLIRS.Entry<V> entry1 = new CacheLongKeyLIRS.Entry<>(entry);
/*  653 */         addToMap(entry1);
/*  654 */         addToStack(entry1);
/*  655 */         entry = entry.stackPrev;
/*      */       } 
/*  657 */       entry = param1Segment.queue.queuePrev;
/*  658 */       while (entry != param1Segment.queue) {
/*  659 */         CacheLongKeyLIRS.Entry<V> entry1 = find(entry.key, CacheLongKeyLIRS.getHash(entry.key));
/*  660 */         if (entry1 == null) {
/*  661 */           entry1 = new CacheLongKeyLIRS.Entry<>(entry);
/*  662 */           addToMap(entry1);
/*      */         } 
/*  664 */         addToQueue(this.queue, entry1);
/*  665 */         entry = entry.queuePrev;
/*      */       } 
/*  667 */       entry = param1Segment.queue2.queuePrev;
/*  668 */       while (entry != param1Segment.queue2) {
/*  669 */         CacheLongKeyLIRS.Entry<V> entry1 = find(entry.key, CacheLongKeyLIRS.getHash(entry.key));
/*  670 */         if (entry1 == null) {
/*  671 */           entry1 = new CacheLongKeyLIRS.Entry<>(entry);
/*  672 */           addToMap(entry1);
/*      */         } 
/*  674 */         addToQueue(this.queue2, entry1);
/*  675 */         entry = entry.queuePrev;
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     int getNewMapLen() {
/*  686 */       int i = this.mask + 1;
/*  687 */       if (i * 3 < this.mapSize * 4 && i < 268435456)
/*      */       {
/*  689 */         return i * 2; } 
/*  690 */       if (i > 32 && i / 8 > this.mapSize)
/*      */       {
/*  692 */         return i / 2;
/*      */       }
/*  694 */       return 0;
/*      */     }
/*      */     
/*      */     private void addToMap(CacheLongKeyLIRS.Entry<V> param1Entry) {
/*  698 */       int i = CacheLongKeyLIRS.getHash(param1Entry.key) & this.mask;
/*  699 */       param1Entry.mapNext = this.entries[i];
/*  700 */       this.entries[i] = param1Entry;
/*  701 */       this.usedMemory += param1Entry.getMemory();
/*  702 */       this.mapSize++;
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
/*      */     synchronized V get(CacheLongKeyLIRS.Entry<V> param1Entry) {
/*  714 */       V v = (param1Entry == null) ? null : param1Entry.getValue();
/*  715 */       if (v == null) {
/*      */ 
/*      */         
/*  718 */         this.misses++;
/*      */       } else {
/*  720 */         access(param1Entry);
/*  721 */         this.hits++;
/*      */       } 
/*  723 */       return v;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private void access(CacheLongKeyLIRS.Entry<V> param1Entry) {
/*  733 */       if (param1Entry.isHot()) {
/*  734 */         if (param1Entry != this.stack.stackNext && param1Entry.stackNext != null && 
/*  735 */           this.stackMoveCounter - param1Entry.topMove > this.stackMoveDistance) {
/*      */ 
/*      */           
/*  738 */           boolean bool = (param1Entry == this.stack.stackPrev) ? true : false;
/*  739 */           removeFromStack(param1Entry);
/*  740 */           if (bool)
/*      */           {
/*      */             
/*  743 */             pruneStack();
/*      */           }
/*  745 */           addToStack(param1Entry);
/*      */         } 
/*      */       } else {
/*      */         
/*  749 */         V v = param1Entry.getValue();
/*  750 */         if (v != null) {
/*  751 */           removeFromQueue(param1Entry);
/*  752 */           if (param1Entry.reference != null) {
/*  753 */             param1Entry.value = v;
/*  754 */             param1Entry.reference = null;
/*  755 */             this.usedMemory += param1Entry.memory;
/*      */           } 
/*  757 */           if (param1Entry.stackNext != null) {
/*      */ 
/*      */             
/*  760 */             removeFromStack(param1Entry);
/*      */ 
/*      */ 
/*      */             
/*  764 */             convertOldestHotToCold();
/*      */           }
/*      */           else {
/*      */             
/*  768 */             addToQueue(this.queue, param1Entry);
/*      */           } 
/*      */           
/*  771 */           addToStack(param1Entry);
/*      */ 
/*      */           
/*  774 */           pruneStack();
/*      */         } 
/*      */       } 
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
/*      */     
/*      */     synchronized V put(long param1Long, int param1Int1, V param1V, int param1Int2) {
/*  791 */       CacheLongKeyLIRS.Entry<V> entry = find(param1Long, param1Int1);
/*  792 */       boolean bool = (entry != null) ? true : false;
/*  793 */       V v = null;
/*  794 */       if (bool) {
/*  795 */         v = entry.getValue();
/*  796 */         remove(param1Long, param1Int1);
/*      */       } 
/*  798 */       if (param1Int2 > this.maxMemory)
/*      */       {
/*  800 */         return v;
/*      */       }
/*  802 */       entry = new CacheLongKeyLIRS.Entry<>(param1Long, param1V, param1Int2);
/*  803 */       int i = param1Int1 & this.mask;
/*  804 */       entry.mapNext = this.entries[i];
/*  805 */       this.entries[i] = entry;
/*  806 */       this.usedMemory += param1Int2;
/*  807 */       if (this.usedMemory > this.maxMemory) {
/*      */         
/*  809 */         evict();
/*      */ 
/*      */         
/*  812 */         if (this.stackSize > 0)
/*      */         {
/*  814 */           addToQueue(this.queue, entry);
/*      */         }
/*      */       } 
/*  817 */       this.mapSize++;
/*      */       
/*  819 */       addToStack(entry);
/*  820 */       if (bool)
/*      */       {
/*  822 */         access(entry);
/*      */       }
/*  824 */       return v;
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
/*      */     synchronized V remove(long param1Long, int param1Int) {
/*      */       // Byte code:
/*      */       //   0: iload_3
/*      */       //   1: aload_0
/*      */       //   2: getfield mask : I
/*      */       //   5: iand
/*      */       //   6: istore #4
/*      */       //   8: aload_0
/*      */       //   9: getfield entries : [Lorg/h2/mvstore/cache/CacheLongKeyLIRS$Entry;
/*      */       //   12: iload #4
/*      */       //   14: aaload
/*      */       //   15: astore #5
/*      */       //   17: aload #5
/*      */       //   19: ifnonnull -> 24
/*      */       //   22: aconst_null
/*      */       //   23: areturn
/*      */       //   24: aload #5
/*      */       //   26: getfield key : J
/*      */       //   29: lload_1
/*      */       //   30: lcmp
/*      */       //   31: ifne -> 49
/*      */       //   34: aload_0
/*      */       //   35: getfield entries : [Lorg/h2/mvstore/cache/CacheLongKeyLIRS$Entry;
/*      */       //   38: iload #4
/*      */       //   40: aload #5
/*      */       //   42: getfield mapNext : Lorg/h2/mvstore/cache/CacheLongKeyLIRS$Entry;
/*      */       //   45: aastore
/*      */       //   46: goto -> 87
/*      */       //   49: aload #5
/*      */       //   51: astore #6
/*      */       //   53: aload #5
/*      */       //   55: getfield mapNext : Lorg/h2/mvstore/cache/CacheLongKeyLIRS$Entry;
/*      */       //   58: astore #5
/*      */       //   60: aload #5
/*      */       //   62: ifnonnull -> 67
/*      */       //   65: aconst_null
/*      */       //   66: areturn
/*      */       //   67: aload #5
/*      */       //   69: getfield key : J
/*      */       //   72: lload_1
/*      */       //   73: lcmp
/*      */       //   74: ifne -> 49
/*      */       //   77: aload #6
/*      */       //   79: aload #5
/*      */       //   81: getfield mapNext : Lorg/h2/mvstore/cache/CacheLongKeyLIRS$Entry;
/*      */       //   84: putfield mapNext : Lorg/h2/mvstore/cache/CacheLongKeyLIRS$Entry;
/*      */       //   87: aload #5
/*      */       //   89: invokevirtual getValue : ()Ljava/lang/Object;
/*      */       //   92: astore #6
/*      */       //   94: aload_0
/*      */       //   95: dup
/*      */       //   96: getfield mapSize : I
/*      */       //   99: iconst_1
/*      */       //   100: isub
/*      */       //   101: putfield mapSize : I
/*      */       //   104: aload_0
/*      */       //   105: dup
/*      */       //   106: getfield usedMemory : J
/*      */       //   109: aload #5
/*      */       //   111: invokevirtual getMemory : ()I
/*      */       //   114: i2l
/*      */       //   115: lsub
/*      */       //   116: putfield usedMemory : J
/*      */       //   119: aload #5
/*      */       //   121: getfield stackNext : Lorg/h2/mvstore/cache/CacheLongKeyLIRS$Entry;
/*      */       //   124: ifnull -> 133
/*      */       //   127: aload_0
/*      */       //   128: aload #5
/*      */       //   130: invokespecial removeFromStack : (Lorg/h2/mvstore/cache/CacheLongKeyLIRS$Entry;)V
/*      */       //   133: aload #5
/*      */       //   135: invokevirtual isHot : ()Z
/*      */       //   138: ifeq -> 186
/*      */       //   141: aload_0
/*      */       //   142: getfield queue : Lorg/h2/mvstore/cache/CacheLongKeyLIRS$Entry;
/*      */       //   145: getfield queueNext : Lorg/h2/mvstore/cache/CacheLongKeyLIRS$Entry;
/*      */       //   148: astore #5
/*      */       //   150: aload #5
/*      */       //   152: aload_0
/*      */       //   153: getfield queue : Lorg/h2/mvstore/cache/CacheLongKeyLIRS$Entry;
/*      */       //   156: if_acmpeq -> 179
/*      */       //   159: aload_0
/*      */       //   160: aload #5
/*      */       //   162: invokespecial removeFromQueue : (Lorg/h2/mvstore/cache/CacheLongKeyLIRS$Entry;)V
/*      */       //   165: aload #5
/*      */       //   167: getfield stackNext : Lorg/h2/mvstore/cache/CacheLongKeyLIRS$Entry;
/*      */       //   170: ifnonnull -> 179
/*      */       //   173: aload_0
/*      */       //   174: aload #5
/*      */       //   176: invokespecial addToStackBottom : (Lorg/h2/mvstore/cache/CacheLongKeyLIRS$Entry;)V
/*      */       //   179: aload_0
/*      */       //   180: invokespecial pruneStack : ()V
/*      */       //   183: goto -> 192
/*      */       //   186: aload_0
/*      */       //   187: aload #5
/*      */       //   189: invokespecial removeFromQueue : (Lorg/h2/mvstore/cache/CacheLongKeyLIRS$Entry;)V
/*      */       //   192: aload #6
/*      */       //   194: areturn
/*      */       // Line number table:
/*      */       //   Java source line number -> byte code offset
/*      */       //   #836	-> 0
/*      */       //   #837	-> 8
/*      */       //   #838	-> 17
/*      */       //   #839	-> 22
/*      */       //   #841	-> 24
/*      */       //   #842	-> 34
/*      */       //   #846	-> 49
/*      */       //   #847	-> 53
/*      */       //   #848	-> 60
/*      */       //   #849	-> 65
/*      */       //   #851	-> 67
/*      */       //   #852	-> 77
/*      */       //   #854	-> 87
/*      */       //   #855	-> 94
/*      */       //   #856	-> 104
/*      */       //   #857	-> 119
/*      */       //   #858	-> 127
/*      */       //   #860	-> 133
/*      */       //   #863	-> 141
/*      */       //   #864	-> 150
/*      */       //   #865	-> 159
/*      */       //   #866	-> 165
/*      */       //   #867	-> 173
/*      */       //   #870	-> 179
/*      */       //   #872	-> 186
/*      */       //   #874	-> 192
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
/*      */     private void evict() {
/*      */       do {
/*  884 */         evictBlock();
/*  885 */       } while (this.usedMemory > this.maxMemory);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private void evictBlock() {
/*  892 */       while (this.queueSize <= this.mapSize - this.queue2Size >>> 5 && this.stackSize > 0) {
/*  893 */         convertOldestHotToCold();
/*      */       }
/*      */       
/*  896 */       while (this.usedMemory > this.maxMemory && this.queueSize > 0) {
/*  897 */         CacheLongKeyLIRS.Entry<V> entry = this.queue.queuePrev;
/*  898 */         this.usedMemory -= entry.memory;
/*  899 */         removeFromQueue(entry);
/*  900 */         entry.reference = new WeakReference<>(entry.value);
/*  901 */         entry.value = null;
/*  902 */         addToQueue(this.queue2, entry);
/*      */         
/*  904 */         trimNonResidentQueue();
/*      */       } 
/*      */     }
/*      */     
/*      */     void trimNonResidentQueue() {
/*  909 */       int i = this.mapSize - this.queue2Size;
/*  910 */       int j = this.nonResidentQueueSizeHigh * i;
/*  911 */       int k = this.nonResidentQueueSize * i;
/*  912 */       while (this.queue2Size > k) {
/*  913 */         CacheLongKeyLIRS.Entry<V> entry = this.queue2.queuePrev;
/*  914 */         if (this.queue2Size <= j) {
/*  915 */           WeakReference<V> weakReference = entry.reference;
/*  916 */           if (weakReference != null && weakReference.get() != null) {
/*      */             break;
/*      */           }
/*      */         } 
/*  920 */         int m = CacheLongKeyLIRS.getHash(entry.key);
/*  921 */         remove(entry.key, m);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     private void convertOldestHotToCold() {
/*  927 */       CacheLongKeyLIRS.Entry<V> entry = this.stack.stackPrev;
/*  928 */       if (entry == this.stack)
/*      */       {
/*      */         
/*  931 */         throw new IllegalStateException();
/*      */       }
/*      */ 
/*      */       
/*  935 */       removeFromStack(entry);
/*      */       
/*  937 */       addToQueue(this.queue, entry);
/*  938 */       pruneStack();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private void pruneStack() {
/*      */       while (true) {
/*  946 */         CacheLongKeyLIRS.Entry<V> entry = this.stack.stackPrev;
/*      */ 
/*      */ 
/*      */         
/*  950 */         if (entry.isHot()) {
/*      */           break;
/*      */         }
/*      */         
/*  954 */         removeFromStack(entry);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     CacheLongKeyLIRS.Entry<V> find(long param1Long, int param1Int) {
/*  966 */       int i = param1Int & this.mask;
/*  967 */       CacheLongKeyLIRS.Entry<V> entry = this.entries[i];
/*  968 */       while (entry != null && entry.key != param1Long) {
/*  969 */         entry = entry.mapNext;
/*      */       }
/*  971 */       return entry;
/*      */     }
/*      */     
/*      */     private void addToStack(CacheLongKeyLIRS.Entry<V> param1Entry) {
/*  975 */       param1Entry.stackPrev = this.stack;
/*  976 */       param1Entry.stackNext = this.stack.stackNext;
/*  977 */       param1Entry.stackNext.stackPrev = param1Entry;
/*  978 */       this.stack.stackNext = param1Entry;
/*  979 */       this.stackSize++;
/*  980 */       param1Entry.topMove = this.stackMoveCounter++;
/*      */     }
/*      */     
/*      */     private void addToStackBottom(CacheLongKeyLIRS.Entry<V> param1Entry) {
/*  984 */       param1Entry.stackNext = this.stack;
/*  985 */       param1Entry.stackPrev = this.stack.stackPrev;
/*  986 */       param1Entry.stackPrev.stackNext = param1Entry;
/*  987 */       this.stack.stackPrev = param1Entry;
/*  988 */       this.stackSize++;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private void removeFromStack(CacheLongKeyLIRS.Entry<V> param1Entry) {
/*  997 */       param1Entry.stackPrev.stackNext = param1Entry.stackNext;
/*  998 */       param1Entry.stackNext.stackPrev = param1Entry.stackPrev;
/*  999 */       param1Entry.stackPrev = param1Entry.stackNext = null;
/* 1000 */       this.stackSize--;
/*      */     }
/*      */     
/*      */     private void addToQueue(CacheLongKeyLIRS.Entry<V> param1Entry1, CacheLongKeyLIRS.Entry<V> param1Entry2) {
/* 1004 */       param1Entry2.queuePrev = param1Entry1;
/* 1005 */       param1Entry2.queueNext = param1Entry1.queueNext;
/* 1006 */       param1Entry2.queueNext.queuePrev = param1Entry2;
/* 1007 */       param1Entry1.queueNext = param1Entry2;
/* 1008 */       if (param1Entry2.value != null) {
/* 1009 */         this.queueSize++;
/*      */       } else {
/* 1011 */         this.queue2Size++;
/*      */       } 
/*      */     }
/*      */     
/*      */     private void removeFromQueue(CacheLongKeyLIRS.Entry<V> param1Entry) {
/* 1016 */       param1Entry.queuePrev.queueNext = param1Entry.queueNext;
/* 1017 */       param1Entry.queueNext.queuePrev = param1Entry.queuePrev;
/* 1018 */       param1Entry.queuePrev = param1Entry.queueNext = null;
/* 1019 */       if (param1Entry.value != null) {
/* 1020 */         this.queueSize--;
/*      */       } else {
/* 1022 */         this.queue2Size--;
/*      */       } 
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
/*      */     synchronized List<Long> keys(boolean param1Boolean1, boolean param1Boolean2) {
/* 1035 */       ArrayList<Long> arrayList = new ArrayList();
/* 1036 */       if (param1Boolean1) {
/* 1037 */         CacheLongKeyLIRS.Entry<V> entry1 = param1Boolean2 ? this.queue2 : this.queue;
/* 1038 */         for (CacheLongKeyLIRS.Entry<V> entry2 = entry1.queueNext; entry2 != entry1; 
/* 1039 */           entry2 = entry2.queueNext) {
/* 1040 */           arrayList.add(Long.valueOf(entry2.key));
/*      */         }
/*      */       } else {
/* 1043 */         for (CacheLongKeyLIRS.Entry<V> entry = this.stack.stackNext; entry != this.stack; 
/* 1044 */           entry = entry.stackNext) {
/* 1045 */           arrayList.add(Long.valueOf(entry.key));
/*      */         }
/*      */       } 
/* 1048 */       return arrayList;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     synchronized Set<Long> keySet() {
/* 1057 */       HashSet<Long> hashSet = new HashSet(); CacheLongKeyLIRS.Entry<V> entry;
/* 1058 */       for (entry = this.stack.stackNext; entry != this.stack; entry = entry.stackNext) {
/* 1059 */         hashSet.add(Long.valueOf(entry.key));
/*      */       }
/* 1061 */       for (entry = this.queue.queueNext; entry != this.queue; entry = entry.queueNext) {
/* 1062 */         hashSet.add(Long.valueOf(entry.key));
/*      */       }
/* 1064 */       return hashSet;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     void setMaxMemory(long param1Long) {
/* 1075 */       this.maxMemory = param1Long;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class Entry<V>
/*      */   {
/*      */     final long key;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     V value;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     WeakReference<V> reference;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     final int memory;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     int topMove;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     Entry<V> stackNext;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     Entry<V> stackPrev;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     Entry<V> queueNext;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     Entry<V> queuePrev;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     Entry<V> mapNext;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     Entry() {
/* 1144 */       this(0L, null, 0);
/*      */     }
/*      */     
/*      */     Entry(long param1Long, V param1V, int param1Int) {
/* 1148 */       this.key = param1Long;
/* 1149 */       this.memory = param1Int;
/* 1150 */       this.value = param1V;
/*      */     }
/*      */     
/*      */     Entry(Entry<V> param1Entry) {
/* 1154 */       this(param1Entry.key, param1Entry.value, param1Entry.memory);
/* 1155 */       this.reference = param1Entry.reference;
/* 1156 */       this.topMove = param1Entry.topMove;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     boolean isHot() {
/* 1165 */       return (this.queueNext == null);
/*      */     }
/*      */     
/*      */     V getValue() {
/* 1169 */       return (this.value == null) ? this.reference.get() : this.value;
/*      */     }
/*      */     
/*      */     int getMemory() {
/* 1173 */       return (this.value == null) ? 0 : this.memory;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class Config
/*      */   {
/* 1185 */     public long maxMemory = 1L;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1190 */     public int segmentCount = 16;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1196 */     public int stackMoveDistance = 32;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1202 */     public final int nonResidentQueueSize = 3;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1208 */     public final int nonResidentQueueSizeHigh = 12;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\cache\CacheLongKeyLIRS.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */