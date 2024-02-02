/*     */ package com.zaxxer.hikari.util;
/*     */ 
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ import java.util.concurrent.SynchronousQueue;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.concurrent.locks.LockSupport;
/*     */ import java.util.stream.Collectors;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
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
/*     */ public class ConcurrentBag<T extends ConcurrentBag.IConcurrentBagEntry>
/*     */   implements AutoCloseable
/*     */ {
/*  61 */   private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrentBag.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final CopyOnWriteArrayList<T> sharedList;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final boolean weakThreadLocals;
/*     */ 
/*     */ 
/*     */   
/*     */   private final ThreadLocal<List<Object>> threadList;
/*     */ 
/*     */ 
/*     */   
/*     */   private final IBagStateListener listener;
/*     */ 
/*     */ 
/*     */   
/*     */   private final AtomicInteger waiters;
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile boolean closed;
/*     */ 
/*     */ 
/*     */   
/*     */   private final SynchronousQueue<T> handoffQueue;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConcurrentBag(IBagStateListener listener) {
/*  97 */     this.listener = listener;
/*  98 */     this.weakThreadLocals = useWeakThreadLocals();
/*     */     
/* 100 */     this.handoffQueue = new SynchronousQueue<>(true);
/* 101 */     this.waiters = new AtomicInteger();
/* 102 */     this.sharedList = new CopyOnWriteArrayList<>();
/* 103 */     if (this.weakThreadLocals) {
/* 104 */       this.threadList = ThreadLocal.withInitial(() -> new ArrayList(16));
/*     */     } else {
/*     */       
/* 107 */       this.threadList = ThreadLocal.withInitial(() -> new FastList(IConcurrentBagEntry.class, 16));
/*     */     } 
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
/*     */ 
/*     */   
/*     */   public T borrow(long timeout, TimeUnit timeUnit) throws InterruptedException {
/* 123 */     List<Object> list = this.threadList.get();
/* 124 */     for (int i = list.size() - 1; i >= 0; i--) {
/* 125 */       Object entry = list.remove(i);
/*     */       
/* 127 */       IConcurrentBagEntry iConcurrentBagEntry = this.weakThreadLocals ? ((WeakReference<IConcurrentBagEntry>)entry).get() : (IConcurrentBagEntry)entry;
/* 128 */       if (iConcurrentBagEntry != null && iConcurrentBagEntry.compareAndSet(0, 1)) {
/* 129 */         return (T)iConcurrentBagEntry;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 134 */     int waiting = this.waiters.incrementAndGet();
/*     */     try {
/* 136 */       for (IConcurrentBagEntry iConcurrentBagEntry : this.sharedList) {
/* 137 */         if (iConcurrentBagEntry.compareAndSet(0, 1)) {
/*     */           
/* 139 */           if (waiting > 1) {
/* 140 */             this.listener.addBagItem(waiting - 1);
/*     */           }
/* 142 */           return (T)iConcurrentBagEntry;
/*     */         } 
/*     */       } 
/*     */       
/* 146 */       this.listener.addBagItem(waiting);
/*     */       
/* 148 */       timeout = timeUnit.toNanos(timeout);
/*     */       do {
/* 150 */         long start = ClockSource.currentTime();
/* 151 */         IConcurrentBagEntry iConcurrentBagEntry = (IConcurrentBagEntry)this.handoffQueue.poll(timeout, TimeUnit.NANOSECONDS);
/* 152 */         if (iConcurrentBagEntry == null || iConcurrentBagEntry.compareAndSet(0, 1)) {
/* 153 */           return (T)iConcurrentBagEntry;
/*     */         }
/*     */         
/* 156 */         timeout -= ClockSource.elapsedNanos(start);
/* 157 */       } while (timeout > 10000L);
/*     */       
/* 159 */       return null;
/*     */     } finally {
/*     */       
/* 162 */       this.waiters.decrementAndGet();
/*     */     } 
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
/*     */   
/*     */   public void requite(T bagEntry) {
/* 177 */     bagEntry.setState(0);
/*     */     
/* 179 */     for (int i = 0; this.waiters.get() > 0; i++) {
/* 180 */       if (bagEntry.getState() != 0 || this.handoffQueue.offer(bagEntry)) {
/*     */         return;
/*     */       }
/* 183 */       if ((i & 0xFF) == 255) {
/* 184 */         LockSupport.parkNanos(TimeUnit.MICROSECONDS.toNanos(10L));
/*     */       } else {
/*     */         
/* 187 */         Thread.yield();
/*     */       } 
/*     */     } 
/*     */     
/* 191 */     List<Object> threadLocalList = this.threadList.get();
/* 192 */     if (threadLocalList.size() < 50) {
/* 193 */       threadLocalList.add(this.weakThreadLocals ? new WeakReference<>(bagEntry) : bagEntry);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void add(T bagEntry) {
/* 204 */     if (this.closed) {
/* 205 */       LOGGER.info("ConcurrentBag has been closed, ignoring add()");
/* 206 */       throw new IllegalStateException("ConcurrentBag has been closed, ignoring add()");
/*     */     } 
/*     */     
/* 209 */     this.sharedList.add(bagEntry);
/*     */ 
/*     */     
/* 212 */     while (this.waiters.get() > 0 && bagEntry.getState() == 0 && !this.handoffQueue.offer(bagEntry)) {
/* 213 */       Thread.yield();
/*     */     }
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
/*     */   
/*     */   public boolean remove(T bagEntry) {
/* 228 */     if (!bagEntry.compareAndSet(1, -1) && !bagEntry.compareAndSet(-2, -1) && !this.closed) {
/* 229 */       LOGGER.warn("Attempt to remove an object from the bag that was not borrowed or reserved: {}", bagEntry);
/* 230 */       return false;
/*     */     } 
/*     */     
/* 233 */     boolean removed = this.sharedList.remove(bagEntry);
/* 234 */     if (!removed && !this.closed) {
/* 235 */       LOGGER.warn("Attempt to remove an object from the bag that does not exist: {}", bagEntry);
/*     */     }
/*     */     
/* 238 */     ((List)this.threadList.get()).remove(bagEntry);
/*     */     
/* 240 */     return removed;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {
/* 249 */     this.closed = true;
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
/*     */   
/*     */   public List<T> values(int state) {
/* 263 */     List<T> list = (List<T>)this.sharedList.stream().filter(e -> (e.getState() == state)).collect(Collectors.toList());
/* 264 */     Collections.reverse(list);
/* 265 */     return list;
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
/*     */   
/*     */   public List<T> values() {
/* 279 */     return (List<T>)this.sharedList.clone();
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean reserve(T bagEntry) {
/* 296 */     return bagEntry.compareAndSet(0, -2);
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
/*     */   public void unreserve(T bagEntry) {
/* 308 */     if (bagEntry.compareAndSet(-2, 0)) {
/*     */       
/* 310 */       while (this.waiters.get() > 0 && !this.handoffQueue.offer(bagEntry)) {
/* 311 */         Thread.yield();
/*     */       }
/*     */     } else {
/*     */       
/* 315 */       LOGGER.warn("Attempt to relinquish an object to the bag that was not reserved: {}", bagEntry);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getWaitingThreadCount() {
/* 327 */     return this.waiters.get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getCount(int state) {
/* 338 */     int count = 0;
/* 339 */     for (IConcurrentBagEntry e : this.sharedList) {
/* 340 */       if (e.getState() == state) {
/* 341 */         count++;
/*     */       }
/*     */     } 
/* 344 */     return count;
/*     */   }
/*     */ 
/*     */   
/*     */   public int[] getStateCounts() {
/* 349 */     int[] states = new int[6];
/* 350 */     for (IConcurrentBagEntry e : this.sharedList) {
/* 351 */       states[e.getState()] = states[e.getState()] + 1;
/*     */     }
/* 353 */     states[4] = this.sharedList.size();
/* 354 */     states[5] = this.waiters.get();
/*     */     
/* 356 */     return states;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/* 366 */     return this.sharedList.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public void dumpState() {
/* 371 */     this.sharedList.forEach(entry -> LOGGER.info(entry.toString()));
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
/*     */   private boolean useWeakThreadLocals() {
/*     */     try {
/* 384 */       if (System.getProperty("com.zaxxer.hikari.useWeakReferences") != null) {
/* 385 */         return Boolean.getBoolean("com.zaxxer.hikari.useWeakReferences");
/*     */       }
/*     */       
/* 388 */       return (getClass().getClassLoader() != ClassLoader.getSystemClassLoader());
/*     */     }
/* 390 */     catch (SecurityException se) {
/* 391 */       return true;
/*     */     } 
/*     */   }
/*     */   
/*     */   public static interface IBagStateListener {
/*     */     void addBagItem(int param1Int);
/*     */   }
/*     */   
/*     */   public static interface IConcurrentBagEntry {
/*     */     public static final int STATE_NOT_IN_USE = 0;
/*     */     public static final int STATE_IN_USE = 1;
/*     */     public static final int STATE_REMOVED = -1;
/*     */     public static final int STATE_RESERVED = -2;
/*     */     
/*     */     boolean compareAndSet(int param1Int1, int param1Int2);
/*     */     
/*     */     void setState(int param1Int);
/*     */     
/*     */     int getState();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\zaxxer\hikar\\util\ConcurrentBag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */