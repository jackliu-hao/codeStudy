/*     */ package org.apache.http.pool;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.CancellationException;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import java.util.concurrent.locks.Condition;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.concurrent.FutureCallback;
/*     */ import org.apache.http.util.Args;
/*     */ import org.apache.http.util.Asserts;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
/*     */ public abstract class AbstractConnPool<T, C, E extends PoolEntry<T, C>>
/*     */   implements ConnPool<T, E>, ConnPoolControl<T>
/*     */ {
/*     */   private final Lock lock;
/*     */   private final Condition condition;
/*     */   private final ConnFactory<T, C> connFactory;
/*     */   private final Map<T, RouteSpecificPool<T, C, E>> routeToPool;
/*     */   private final Set<E> leased;
/*     */   private final LinkedList<E> available;
/*     */   private final LinkedList<Future<E>> pending;
/*     */   private final Map<T, Integer> maxPerRoute;
/*     */   private volatile boolean isShutDown;
/*     */   private volatile int defaultMaxPerRoute;
/*     */   private volatile int maxTotal;
/*     */   private volatile int validateAfterInactivity;
/*     */   
/*     */   public AbstractConnPool(ConnFactory<T, C> connFactory, int defaultMaxPerRoute, int maxTotal) {
/*  92 */     this.connFactory = (ConnFactory<T, C>)Args.notNull(connFactory, "Connection factory");
/*  93 */     this.defaultMaxPerRoute = Args.positive(defaultMaxPerRoute, "Max per route value");
/*  94 */     this.maxTotal = Args.positive(maxTotal, "Max total value");
/*  95 */     this.lock = new ReentrantLock();
/*  96 */     this.condition = this.lock.newCondition();
/*  97 */     this.routeToPool = new HashMap<T, RouteSpecificPool<T, C, E>>();
/*  98 */     this.leased = new HashSet<E>();
/*  99 */     this.available = new LinkedList<E>();
/* 100 */     this.pending = new LinkedList<Future<E>>();
/* 101 */     this.maxPerRoute = new HashMap<T, Integer>();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void onLease(E entry) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void onRelease(E entry) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void onReuse(E entry) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean validate(E entry) {
/* 131 */     return true;
/*     */   }
/*     */   
/*     */   public boolean isShutdown() {
/* 135 */     return this.isShutDown;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void shutdown() throws IOException {
/* 142 */     if (this.isShutDown) {
/*     */       return;
/*     */     }
/* 145 */     this.isShutDown = true;
/* 146 */     this.lock.lock();
/*     */     try {
/* 148 */       for (PoolEntry poolEntry : this.available) {
/* 149 */         poolEntry.close();
/*     */       }
/* 151 */       for (PoolEntry poolEntry : this.leased) {
/* 152 */         poolEntry.close();
/*     */       }
/* 154 */       for (RouteSpecificPool<T, C, E> pool : this.routeToPool.values()) {
/* 155 */         pool.shutdown();
/*     */       }
/* 157 */       this.routeToPool.clear();
/* 158 */       this.leased.clear();
/* 159 */       this.available.clear();
/*     */     } finally {
/* 161 */       this.lock.unlock();
/*     */     } 
/*     */   }
/*     */   
/*     */   private RouteSpecificPool<T, C, E> getPool(final T route) {
/* 166 */     RouteSpecificPool<T, C, E> pool = this.routeToPool.get(route);
/* 167 */     if (pool == null) {
/* 168 */       pool = new RouteSpecificPool<T, C, E>(route)
/*     */         {
/*     */           protected E createEntry(C conn)
/*     */           {
/* 172 */             return AbstractConnPool.this.createEntry(route, conn);
/*     */           }
/*     */         };
/*     */       
/* 176 */       this.routeToPool.put(route, pool);
/*     */     } 
/* 178 */     return pool;
/*     */   }
/*     */   
/*     */   private static Exception operationAborted() {
/* 182 */     return new CancellationException("Operation aborted");
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
/*     */   public Future<E> lease(final T route, final Object state, final FutureCallback<E> callback) {
/* 195 */     Args.notNull(route, "Route");
/* 196 */     Asserts.check(!this.isShutDown, "Connection pool shut down");
/*     */     
/* 198 */     return new Future<E>()
/*     */       {
/* 200 */         private final AtomicBoolean cancelled = new AtomicBoolean(false);
/* 201 */         private final AtomicBoolean done = new AtomicBoolean(false);
/* 202 */         private final AtomicReference<E> entryRef = new AtomicReference<E>(null);
/*     */ 
/*     */         
/*     */         public boolean cancel(boolean mayInterruptIfRunning) {
/* 206 */           if (this.done.compareAndSet(false, true)) {
/* 207 */             this.cancelled.set(true);
/* 208 */             AbstractConnPool.this.lock.lock();
/*     */             try {
/* 210 */               AbstractConnPool.this.condition.signalAll();
/*     */             } finally {
/* 212 */               AbstractConnPool.this.lock.unlock();
/*     */             } 
/* 214 */             if (callback != null) {
/* 215 */               callback.cancelled();
/*     */             }
/* 217 */             return true;
/*     */           } 
/* 219 */           return false;
/*     */         }
/*     */ 
/*     */         
/*     */         public boolean isCancelled() {
/* 224 */           return this.cancelled.get();
/*     */         }
/*     */ 
/*     */         
/*     */         public boolean isDone() {
/* 229 */           return this.done.get();
/*     */         }
/*     */ 
/*     */         
/*     */         public E get() throws InterruptedException, ExecutionException {
/*     */           try {
/* 235 */             return get(0L, TimeUnit.MILLISECONDS);
/* 236 */           } catch (TimeoutException ex) {
/* 237 */             throw new ExecutionException(ex);
/*     */           } 
/*     */         }
/*     */ 
/*     */         
/*     */         public E get(long timeout, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
/*     */           while (true) {
/* 244 */             synchronized (this) {
/*     */               
/* 246 */               PoolEntry poolEntry1 = (PoolEntry)this.entryRef.get();
/* 247 */               if (poolEntry1 != null) {
/* 248 */                 return (E)poolEntry1;
/*     */               }
/* 250 */               if (this.done.get()) {
/* 251 */                 throw new ExecutionException(AbstractConnPool.operationAborted());
/*     */               }
/* 253 */               PoolEntry poolEntry2 = (PoolEntry)AbstractConnPool.this.getPoolEntryBlocking((T)route, state, timeout, timeUnit, this);
/* 254 */               if (AbstractConnPool.this.validateAfterInactivity > 0 && 
/* 255 */                 poolEntry2.getUpdated() + AbstractConnPool.this.validateAfterInactivity <= System.currentTimeMillis() && 
/* 256 */                 !AbstractConnPool.this.validate(poolEntry2)) {
/* 257 */                 poolEntry2.close();
/* 258 */                 AbstractConnPool.this.release(poolEntry2, false);
/*     */                 
/*     */                 continue;
/*     */               } 
/*     */               
/* 263 */               if (this.done.compareAndSet(false, true)) {
/* 264 */                 this.entryRef.set((E)poolEntry2);
/* 265 */                 this.done.set(true);
/* 266 */                 AbstractConnPool.this.onLease(poolEntry2);
/* 267 */                 if (callback != null) {
/* 268 */                   callback.completed(poolEntry2);
/*     */                 }
/* 270 */                 return (E)poolEntry2;
/*     */               } 
/* 272 */               AbstractConnPool.this.release(poolEntry2, true);
/* 273 */               throw new ExecutionException(AbstractConnPool.operationAborted());
/*     */             } 
/*     */           } 
/*     */         }
/*     */       };
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
/*     */   public Future<E> lease(T route, Object state) {
/* 307 */     return lease(route, state, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private E getPoolEntryBlocking(T route, Object state, long timeout, TimeUnit timeUnit, Future<E> future) throws IOException, InterruptedException, ExecutionException, TimeoutException {
/* 315 */     Date deadline = null;
/* 316 */     if (timeout > 0L) {
/* 317 */       deadline = new Date(System.currentTimeMillis() + timeUnit.toMillis(timeout));
/*     */     }
/* 319 */     this.lock.lock(); try {
/*     */       boolean success;
/* 321 */       RouteSpecificPool<T, C, E> pool = getPool(route);
/*     */       do {
/*     */         E entry;
/* 324 */         Asserts.check(!this.isShutDown, "Connection pool shut down");
/* 325 */         if (future.isCancelled()) {
/* 326 */           throw new ExecutionException(operationAborted());
/*     */         }
/*     */         while (true) {
/* 329 */           entry = pool.getFree(state);
/* 330 */           if (entry == null) {
/*     */             break;
/*     */           }
/* 333 */           if (entry.isExpired(System.currentTimeMillis())) {
/* 334 */             entry.close();
/*     */           }
/* 336 */           if (entry.isClosed()) {
/* 337 */             this.available.remove(entry);
/* 338 */             pool.free(entry, false);
/*     */             continue;
/*     */           } 
/*     */           break;
/*     */         } 
/* 343 */         if (entry != null) {
/* 344 */           this.available.remove(entry);
/* 345 */           this.leased.add(entry);
/* 346 */           onReuse(entry);
/* 347 */           return entry;
/*     */         } 
/*     */ 
/*     */         
/* 351 */         int maxPerRoute = getMax(route);
/*     */         
/* 353 */         int excess = Math.max(0, pool.getAllocatedCount() + 1 - maxPerRoute);
/* 354 */         if (excess > 0) {
/* 355 */           for (int i = 0; i < excess; i++) {
/* 356 */             E lastUsed = pool.getLastUsed();
/* 357 */             if (lastUsed == null) {
/*     */               break;
/*     */             }
/* 360 */             lastUsed.close();
/* 361 */             this.available.remove(lastUsed);
/* 362 */             pool.remove(lastUsed);
/*     */           } 
/*     */         }
/*     */         
/* 366 */         if (pool.getAllocatedCount() < maxPerRoute) {
/* 367 */           int totalUsed = this.leased.size();
/* 368 */           int freeCapacity = Math.max(this.maxTotal - totalUsed, 0);
/* 369 */           if (freeCapacity > 0) {
/* 370 */             int totalAvailable = this.available.size();
/* 371 */             if (totalAvailable > freeCapacity - 1 && 
/* 372 */               !this.available.isEmpty()) {
/* 373 */               PoolEntry poolEntry = (PoolEntry)this.available.removeLast();
/* 374 */               poolEntry.close();
/* 375 */               RouteSpecificPool<T, C, E> otherpool = getPool((T)poolEntry.getRoute());
/* 376 */               otherpool.remove((E)poolEntry);
/*     */             } 
/*     */             
/* 379 */             C conn = this.connFactory.create(route);
/* 380 */             entry = pool.add(conn);
/* 381 */             this.leased.add(entry);
/* 382 */             return entry;
/*     */           } 
/*     */         } 
/*     */         
/* 386 */         success = false;
/*     */         try {
/* 388 */           pool.queue(future);
/* 389 */           this.pending.add(future);
/* 390 */           if (deadline != null) {
/* 391 */             success = this.condition.awaitUntil(deadline);
/*     */           } else {
/* 393 */             this.condition.await();
/* 394 */             success = true;
/*     */           } 
/* 396 */           if (future.isCancelled()) {
/* 397 */             throw new ExecutionException(operationAborted());
/*     */           
/*     */           }
/*     */         
/*     */         }
/*     */         finally {
/*     */           
/* 404 */           pool.unqueue(future);
/* 405 */           this.pending.remove(future);
/*     */         }
/*     */       
/* 408 */       } while (success || deadline == null || deadline.getTime() > System.currentTimeMillis());
/*     */ 
/*     */ 
/*     */       
/* 412 */       throw new TimeoutException("Timeout waiting for connection");
/*     */     } finally {
/* 414 */       this.lock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void release(E entry, boolean reusable) {
/* 420 */     this.lock.lock();
/*     */     try {
/* 422 */       if (this.leased.remove(entry)) {
/* 423 */         RouteSpecificPool<T, C, E> pool = getPool((T)entry.getRoute());
/* 424 */         pool.free(entry, reusable);
/* 425 */         if (reusable && !this.isShutDown) {
/* 426 */           this.available.addFirst(entry);
/*     */         } else {
/* 428 */           entry.close();
/*     */         } 
/* 430 */         onRelease(entry);
/* 431 */         Future<E> future = pool.nextPending();
/* 432 */         if (future != null) {
/* 433 */           this.pending.remove(future);
/*     */         } else {
/* 435 */           future = this.pending.poll();
/*     */         } 
/* 437 */         if (future != null) {
/* 438 */           this.condition.signalAll();
/*     */         }
/*     */       } 
/*     */     } finally {
/* 442 */       this.lock.unlock();
/*     */     } 
/*     */   }
/*     */   
/*     */   private int getMax(T route) {
/* 447 */     Integer v = this.maxPerRoute.get(route);
/* 448 */     return (v != null) ? v.intValue() : this.defaultMaxPerRoute;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setMaxTotal(int max) {
/* 453 */     Args.positive(max, "Max value");
/* 454 */     this.lock.lock();
/*     */     try {
/* 456 */       this.maxTotal = max;
/*     */     } finally {
/* 458 */       this.lock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMaxTotal() {
/* 464 */     this.lock.lock();
/*     */     try {
/* 466 */       return this.maxTotal;
/*     */     } finally {
/* 468 */       this.lock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDefaultMaxPerRoute(int max) {
/* 474 */     Args.positive(max, "Max per route value");
/* 475 */     this.lock.lock();
/*     */     try {
/* 477 */       this.defaultMaxPerRoute = max;
/*     */     } finally {
/* 479 */       this.lock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int getDefaultMaxPerRoute() {
/* 485 */     this.lock.lock();
/*     */     try {
/* 487 */       return this.defaultMaxPerRoute;
/*     */     } finally {
/* 489 */       this.lock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setMaxPerRoute(T route, int max) {
/* 495 */     Args.notNull(route, "Route");
/* 496 */     this.lock.lock();
/*     */     try {
/* 498 */       if (max > -1) {
/* 499 */         this.maxPerRoute.put(route, Integer.valueOf(max));
/*     */       } else {
/* 501 */         this.maxPerRoute.remove(route);
/*     */       } 
/*     */     } finally {
/* 504 */       this.lock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMaxPerRoute(T route) {
/* 510 */     Args.notNull(route, "Route");
/* 511 */     this.lock.lock();
/*     */     try {
/* 513 */       return getMax(route);
/*     */     } finally {
/* 515 */       this.lock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public PoolStats getTotalStats() {
/* 521 */     this.lock.lock();
/*     */     try {
/* 523 */       return new PoolStats(this.leased.size(), this.pending.size(), this.available.size(), this.maxTotal);
/*     */     
/*     */     }
/*     */     finally {
/*     */ 
/*     */       
/* 529 */       this.lock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public PoolStats getStats(T route) {
/* 535 */     Args.notNull(route, "Route");
/* 536 */     this.lock.lock();
/*     */     try {
/* 538 */       RouteSpecificPool<T, C, E> pool = getPool(route);
/* 539 */       return new PoolStats(pool.getLeasedCount(), pool.getPendingCount(), pool.getAvailableCount(), getMax(route));
/*     */     
/*     */     }
/*     */     finally {
/*     */ 
/*     */       
/* 545 */       this.lock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<T> getRoutes() {
/* 556 */     this.lock.lock();
/*     */     try {
/* 558 */       return new HashSet(this.routeToPool.keySet());
/*     */     } finally {
/* 560 */       this.lock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void enumAvailable(PoolEntryCallback<T, C> callback) {
/* 570 */     this.lock.lock();
/*     */     try {
/* 572 */       Iterator<E> it = this.available.iterator();
/* 573 */       while (it.hasNext()) {
/* 574 */         PoolEntry<T, C> poolEntry = (PoolEntry)it.next();
/* 575 */         callback.process(poolEntry);
/* 576 */         if (poolEntry.isClosed()) {
/* 577 */           RouteSpecificPool<T, C, E> pool = getPool(poolEntry.getRoute());
/* 578 */           pool.remove((E)poolEntry);
/* 579 */           it.remove();
/*     */         } 
/*     */       } 
/* 582 */       purgePoolMap();
/*     */     } finally {
/* 584 */       this.lock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void enumLeased(PoolEntryCallback<T, C> callback) {
/* 594 */     this.lock.lock();
/*     */     try {
/* 596 */       Iterator<E> it = this.leased.iterator();
/* 597 */       while (it.hasNext()) {
/* 598 */         PoolEntry<T, C> poolEntry = (PoolEntry)it.next();
/* 599 */         callback.process(poolEntry);
/*     */       } 
/*     */     } finally {
/* 602 */       this.lock.unlock();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void purgePoolMap() {
/* 607 */     Iterator<Map.Entry<T, RouteSpecificPool<T, C, E>>> it = this.routeToPool.entrySet().iterator();
/* 608 */     while (it.hasNext()) {
/* 609 */       Map.Entry<T, RouteSpecificPool<T, C, E>> entry = it.next();
/* 610 */       RouteSpecificPool<T, C, E> pool = entry.getValue();
/* 611 */       if (pool.getPendingCount() + pool.getAllocatedCount() == 0) {
/* 612 */         it.remove();
/*     */       }
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
/*     */   public void closeIdle(long idletime, TimeUnit timeUnit) {
/* 625 */     Args.notNull(timeUnit, "Time unit");
/* 626 */     long time = timeUnit.toMillis(idletime);
/* 627 */     if (time < 0L) {
/* 628 */       time = 0L;
/*     */     }
/* 630 */     final long deadline = System.currentTimeMillis() - time;
/* 631 */     enumAvailable(new PoolEntryCallback<T, C>()
/*     */         {
/*     */           public void process(PoolEntry<T, C> entry)
/*     */           {
/* 635 */             if (entry.getUpdated() <= deadline) {
/* 636 */               entry.close();
/*     */             }
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void closeExpired() {
/* 647 */     final long now = System.currentTimeMillis();
/* 648 */     enumAvailable(new PoolEntryCallback<T, C>()
/*     */         {
/*     */           public void process(PoolEntry<T, C> entry)
/*     */           {
/* 652 */             if (entry.isExpired(now)) {
/* 653 */               entry.close();
/*     */             }
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getValidateAfterInactivity() {
/* 665 */     return this.validateAfterInactivity;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setValidateAfterInactivity(int ms) {
/* 673 */     this.validateAfterInactivity = ms;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 678 */     this.lock.lock();
/*     */     try {
/* 680 */       StringBuilder buffer = new StringBuilder();
/* 681 */       buffer.append("[leased: ");
/* 682 */       buffer.append(this.leased);
/* 683 */       buffer.append("][available: ");
/* 684 */       buffer.append(this.available);
/* 685 */       buffer.append("][pending: ");
/* 686 */       buffer.append(this.pending);
/* 687 */       buffer.append("]");
/* 688 */       return buffer.toString();
/*     */     } finally {
/* 690 */       this.lock.unlock();
/*     */     } 
/*     */   }
/*     */   
/*     */   protected abstract E createEntry(T paramT, C paramC);
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\pool\AbstractConnPool.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */