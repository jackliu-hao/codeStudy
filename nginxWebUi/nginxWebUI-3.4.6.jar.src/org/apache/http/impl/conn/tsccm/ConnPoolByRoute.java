/*     */ package org.apache.http.impl.conn.tsccm;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Map;
/*     */ import java.util.Queue;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.locks.Condition;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.conn.ClientConnectionOperator;
/*     */ import org.apache.http.conn.ConnectionPoolTimeoutException;
/*     */ import org.apache.http.conn.OperatedClientConnection;
/*     */ import org.apache.http.conn.params.ConnManagerParams;
/*     */ import org.apache.http.conn.params.ConnPerRoute;
/*     */ import org.apache.http.conn.routing.HttpRoute;
/*     */ import org.apache.http.params.HttpParams;
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
/*     */ @Deprecated
/*     */ public class ConnPoolByRoute
/*     */   extends AbstractConnPool
/*     */ {
/*  73 */   private final Log log = LogFactory.getLog(getClass());
/*     */ 
/*     */   
/*     */   private final Lock poolLock;
/*     */ 
/*     */   
/*     */   protected final ClientConnectionOperator operator;
/*     */ 
/*     */   
/*     */   protected final ConnPerRoute connPerRoute;
/*     */ 
/*     */   
/*     */   protected final Set<BasicPoolEntry> leasedConnections;
/*     */ 
/*     */   
/*     */   protected final Queue<BasicPoolEntry> freeConnections;
/*     */ 
/*     */   
/*     */   protected final Queue<WaitingThread> waitingThreads;
/*     */ 
/*     */   
/*     */   protected final Map<HttpRoute, RouteSpecificPool> routeToPool;
/*     */ 
/*     */   
/*     */   private final long connTTL;
/*     */ 
/*     */   
/*     */   private final TimeUnit connTTLTimeUnit;
/*     */ 
/*     */   
/*     */   protected volatile boolean shutdown;
/*     */ 
/*     */   
/*     */   protected volatile int maxTotalConnections;
/*     */ 
/*     */   
/*     */   protected volatile int numConnections;
/*     */ 
/*     */ 
/*     */   
/*     */   public ConnPoolByRoute(ClientConnectionOperator operator, ConnPerRoute connPerRoute, int maxTotalConnections) {
/* 114 */     this(operator, connPerRoute, maxTotalConnections, -1L, TimeUnit.MILLISECONDS);
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
/*     */   public ConnPoolByRoute(ClientConnectionOperator operator, ConnPerRoute connPerRoute, int maxTotalConnections, long connTTL, TimeUnit connTTLTimeUnit) {
/* 127 */     Args.notNull(operator, "Connection operator");
/* 128 */     Args.notNull(connPerRoute, "Connections per route");
/* 129 */     this.poolLock = super.poolLock;
/* 130 */     this.leasedConnections = super.leasedConnections;
/* 131 */     this.operator = operator;
/* 132 */     this.connPerRoute = connPerRoute;
/* 133 */     this.maxTotalConnections = maxTotalConnections;
/* 134 */     this.freeConnections = createFreeConnQueue();
/* 135 */     this.waitingThreads = createWaitingThreadQueue();
/* 136 */     this.routeToPool = createRouteToPoolMap();
/* 137 */     this.connTTL = connTTL;
/* 138 */     this.connTTLTimeUnit = connTTLTimeUnit;
/*     */   }
/*     */   
/*     */   protected Lock getLock() {
/* 142 */     return this.poolLock;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ConnPoolByRoute(ClientConnectionOperator operator, HttpParams params) {
/* 152 */     this(operator, ConnManagerParams.getMaxConnectionsPerRoute(params), ConnManagerParams.getMaxTotalConnections(params));
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
/*     */   protected Queue<BasicPoolEntry> createFreeConnQueue() {
/* 164 */     return new LinkedList<BasicPoolEntry>();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Queue<WaitingThread> createWaitingThreadQueue() {
/* 174 */     return new LinkedList<WaitingThread>();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Map<HttpRoute, RouteSpecificPool> createRouteToPoolMap() {
/* 184 */     return new HashMap<HttpRoute, RouteSpecificPool>();
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
/*     */   protected RouteSpecificPool newRouteSpecificPool(HttpRoute route) {
/* 197 */     return new RouteSpecificPool(route, this.connPerRoute);
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
/*     */   protected WaitingThread newWaitingThread(Condition cond, RouteSpecificPool rospl) {
/* 212 */     return new WaitingThread(cond, rospl);
/*     */   }
/*     */   
/*     */   private void closeConnection(BasicPoolEntry entry) {
/* 216 */     OperatedClientConnection conn = entry.getConnection();
/* 217 */     if (conn != null) {
/*     */       try {
/* 219 */         conn.close();
/* 220 */       } catch (IOException ex) {
/* 221 */         this.log.debug("I/O error closing connection", ex);
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
/*     */ 
/*     */ 
/*     */   
/*     */   protected RouteSpecificPool getRoutePool(HttpRoute route, boolean create) {
/* 237 */     RouteSpecificPool rospl = null;
/* 238 */     this.poolLock.lock();
/*     */     
/*     */     try {
/* 241 */       rospl = this.routeToPool.get(route);
/* 242 */       if (rospl == null && create) {
/*     */         
/* 244 */         rospl = newRouteSpecificPool(route);
/* 245 */         this.routeToPool.put(route, rospl);
/*     */       } 
/*     */     } finally {
/*     */       
/* 249 */       this.poolLock.unlock();
/*     */     } 
/*     */     
/* 252 */     return rospl;
/*     */   }
/*     */   
/*     */   public int getConnectionsInPool(HttpRoute route) {
/* 256 */     this.poolLock.lock();
/*     */     
/*     */     try {
/* 259 */       RouteSpecificPool rospl = getRoutePool(route, false);
/* 260 */       return (rospl != null) ? rospl.getEntryCount() : 0;
/*     */     } finally {
/*     */       
/* 263 */       this.poolLock.unlock();
/*     */     } 
/*     */   }
/*     */   
/*     */   public int getConnectionsInPool() {
/* 268 */     this.poolLock.lock();
/*     */     try {
/* 270 */       return this.numConnections;
/*     */     } finally {
/* 272 */       this.poolLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PoolEntryRequest requestPoolEntry(final HttpRoute route, final Object state) {
/* 281 */     final WaitingThreadAborter aborter = new WaitingThreadAborter();
/*     */     
/* 283 */     return new PoolEntryRequest()
/*     */       {
/*     */         public void abortRequest()
/*     */         {
/* 287 */           ConnPoolByRoute.this.poolLock.lock();
/*     */           try {
/* 289 */             aborter.abort();
/*     */           } finally {
/* 291 */             ConnPoolByRoute.this.poolLock.unlock();
/*     */           } 
/*     */         }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         public BasicPoolEntry getPoolEntry(long timeout, TimeUnit timeUnit) throws InterruptedException, ConnectionPoolTimeoutException {
/* 300 */           return ConnPoolByRoute.this.getEntryBlocking(route, state, timeout, timeUnit, aborter);
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
/*     */   protected BasicPoolEntry getEntryBlocking(HttpRoute route, Object state, long timeout, TimeUnit timeUnit, WaitingThreadAborter aborter) throws ConnectionPoolTimeoutException, InterruptedException {
/* 330 */     Date deadline = null;
/* 331 */     if (timeout > 0L) {
/* 332 */       deadline = new Date(System.currentTimeMillis() + timeUnit.toMillis(timeout));
/*     */     }
/*     */ 
/*     */     
/* 336 */     BasicPoolEntry entry = null;
/* 337 */     this.poolLock.lock();
/*     */     
/*     */     try {
/* 340 */       RouteSpecificPool rospl = getRoutePool(route, true);
/* 341 */       WaitingThread waitingThread = null;
/*     */       
/* 343 */       while (entry == null) {
/* 344 */         Asserts.check(!this.shutdown, "Connection pool shut down");
/*     */         
/* 346 */         if (this.log.isDebugEnabled()) {
/* 347 */           this.log.debug("[" + route + "] total kept alive: " + this.freeConnections.size() + ", total issued: " + this.leasedConnections.size() + ", total allocated: " + this.numConnections + " out of " + this.maxTotalConnections);
/*     */         }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 358 */         entry = getFreeEntry(rospl, state);
/* 359 */         if (entry != null) {
/*     */           break;
/*     */         }
/*     */         
/* 363 */         boolean hasCapacity = (rospl.getCapacity() > 0);
/*     */         
/* 365 */         if (this.log.isDebugEnabled()) {
/* 366 */           this.log.debug("Available capacity: " + rospl.getCapacity() + " out of " + rospl.getMaxEntries() + " [" + route + "][" + state + "]");
/*     */         }
/*     */ 
/*     */ 
/*     */         
/* 371 */         if (hasCapacity && this.numConnections < this.maxTotalConnections) {
/*     */           
/* 373 */           entry = createEntry(rospl, this.operator); continue;
/*     */         } 
/* 375 */         if (hasCapacity && !this.freeConnections.isEmpty()) {
/*     */           
/* 377 */           deleteLeastUsedEntry();
/*     */ 
/*     */           
/* 380 */           rospl = getRoutePool(route, true);
/* 381 */           entry = createEntry(rospl, this.operator);
/*     */           
/*     */           continue;
/*     */         } 
/* 385 */         if (this.log.isDebugEnabled()) {
/* 386 */           this.log.debug("Need to wait for connection [" + route + "][" + state + "]");
/*     */         }
/*     */ 
/*     */         
/* 390 */         if (waitingThread == null) {
/* 391 */           waitingThread = newWaitingThread(this.poolLock.newCondition(), rospl);
/*     */           
/* 393 */           aborter.setWaitingThread(waitingThread);
/*     */         } 
/*     */         
/* 396 */         boolean success = false;
/*     */         try {
/* 398 */           rospl.queueThread(waitingThread);
/* 399 */           this.waitingThreads.add(waitingThread);
/* 400 */           success = waitingThread.await(deadline);
/*     */ 
/*     */         
/*     */         }
/*     */         finally {
/*     */ 
/*     */           
/* 407 */           rospl.removeThread(waitingThread);
/* 408 */           this.waitingThreads.remove(waitingThread);
/*     */         } 
/*     */ 
/*     */         
/* 412 */         if (!success && deadline != null && deadline.getTime() <= System.currentTimeMillis())
/*     */         {
/* 414 */           throw new ConnectionPoolTimeoutException("Timeout waiting for connection from pool");
/*     */         }
/*     */       }
/*     */     
/*     */     }
/*     */     finally {
/*     */       
/* 421 */       this.poolLock.unlock();
/*     */     } 
/* 423 */     return entry;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void freeEntry(BasicPoolEntry entry, boolean reusable, long validDuration, TimeUnit timeUnit) {
/* 429 */     HttpRoute route = entry.getPlannedRoute();
/* 430 */     if (this.log.isDebugEnabled()) {
/* 431 */       this.log.debug("Releasing connection [" + route + "][" + entry.getState() + "]");
/*     */     }
/*     */ 
/*     */     
/* 435 */     this.poolLock.lock();
/*     */     try {
/* 437 */       if (this.shutdown) {
/*     */ 
/*     */         
/* 440 */         closeConnection(entry);
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 445 */       this.leasedConnections.remove(entry);
/*     */       
/* 447 */       RouteSpecificPool rospl = getRoutePool(route, true);
/*     */       
/* 449 */       if (reusable && rospl.getCapacity() >= 0) {
/* 450 */         if (this.log.isDebugEnabled()) {
/*     */           String s;
/* 452 */           if (validDuration > 0L) {
/* 453 */             s = "for " + validDuration + " " + timeUnit;
/*     */           } else {
/* 455 */             s = "indefinitely";
/*     */           } 
/* 457 */           this.log.debug("Pooling connection [" + route + "][" + entry.getState() + "]; keep alive " + s);
/*     */         } 
/*     */         
/* 460 */         rospl.freeEntry(entry);
/* 461 */         entry.updateExpiry(validDuration, timeUnit);
/* 462 */         this.freeConnections.add(entry);
/*     */       } else {
/* 464 */         closeConnection(entry);
/* 465 */         rospl.dropEntry();
/* 466 */         this.numConnections--;
/*     */       } 
/*     */       
/* 469 */       notifyWaitingThread(rospl);
/*     */     } finally {
/*     */       
/* 472 */       this.poolLock.unlock();
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
/*     */   protected BasicPoolEntry getFreeEntry(RouteSpecificPool rospl, Object state) {
/* 486 */     BasicPoolEntry entry = null;
/* 487 */     this.poolLock.lock();
/*     */     try {
/* 489 */       boolean done = false;
/* 490 */       while (!done)
/*     */       {
/* 492 */         entry = rospl.allocEntry(state);
/*     */         
/* 494 */         if (entry != null) {
/* 495 */           if (this.log.isDebugEnabled()) {
/* 496 */             this.log.debug("Getting free connection [" + rospl.getRoute() + "][" + state + "]");
/*     */           }
/*     */ 
/*     */           
/* 500 */           this.freeConnections.remove(entry);
/* 501 */           if (entry.isExpired(System.currentTimeMillis())) {
/*     */ 
/*     */             
/* 504 */             if (this.log.isDebugEnabled()) {
/* 505 */               this.log.debug("Closing expired free connection [" + rospl.getRoute() + "][" + state + "]");
/*     */             }
/*     */             
/* 508 */             closeConnection(entry);
/*     */ 
/*     */ 
/*     */             
/* 512 */             rospl.dropEntry();
/* 513 */             this.numConnections--; continue;
/*     */           } 
/* 515 */           this.leasedConnections.add(entry);
/* 516 */           done = true;
/*     */           
/*     */           continue;
/*     */         } 
/* 520 */         done = true;
/* 521 */         if (this.log.isDebugEnabled()) {
/* 522 */           this.log.debug("No free connections [" + rospl.getRoute() + "][" + state + "]");
/*     */         }
/*     */       }
/*     */     
/*     */     } finally {
/*     */       
/* 528 */       this.poolLock.unlock();
/*     */     } 
/* 530 */     return entry;
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
/*     */   protected BasicPoolEntry createEntry(RouteSpecificPool rospl, ClientConnectionOperator op) {
/* 547 */     if (this.log.isDebugEnabled()) {
/* 548 */       this.log.debug("Creating new connection [" + rospl.getRoute() + "]");
/*     */     }
/*     */ 
/*     */     
/* 552 */     BasicPoolEntry entry = new BasicPoolEntry(op, rospl.getRoute(), this.connTTL, this.connTTLTimeUnit);
/*     */     
/* 554 */     this.poolLock.lock();
/*     */     try {
/* 556 */       rospl.createdEntry(entry);
/* 557 */       this.numConnections++;
/* 558 */       this.leasedConnections.add(entry);
/*     */     } finally {
/* 560 */       this.poolLock.unlock();
/*     */     } 
/*     */     
/* 563 */     return entry;
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
/*     */   protected void deleteEntry(BasicPoolEntry entry) {
/* 580 */     HttpRoute route = entry.getPlannedRoute();
/*     */     
/* 582 */     if (this.log.isDebugEnabled()) {
/* 583 */       this.log.debug("Deleting connection [" + route + "][" + entry.getState() + "]");
/*     */     }
/*     */ 
/*     */     
/* 587 */     this.poolLock.lock();
/*     */     
/*     */     try {
/* 590 */       closeConnection(entry);
/*     */       
/* 592 */       RouteSpecificPool rospl = getRoutePool(route, true);
/* 593 */       rospl.deleteEntry(entry);
/* 594 */       this.numConnections--;
/* 595 */       if (rospl.isUnused()) {
/* 596 */         this.routeToPool.remove(route);
/*     */       }
/*     */     } finally {
/*     */       
/* 600 */       this.poolLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void deleteLeastUsedEntry() {
/* 610 */     this.poolLock.lock();
/*     */     
/*     */     try {
/* 613 */       BasicPoolEntry entry = this.freeConnections.remove();
/*     */       
/* 615 */       if (entry != null) {
/* 616 */         deleteEntry(entry);
/* 617 */       } else if (this.log.isDebugEnabled()) {
/* 618 */         this.log.debug("No free connection to delete");
/*     */       } 
/*     */     } finally {
/*     */       
/* 622 */       this.poolLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void handleLostEntry(HttpRoute route) {
/* 629 */     this.poolLock.lock();
/*     */     
/*     */     try {
/* 632 */       RouteSpecificPool rospl = getRoutePool(route, true);
/* 633 */       rospl.dropEntry();
/* 634 */       if (rospl.isUnused()) {
/* 635 */         this.routeToPool.remove(route);
/*     */       }
/*     */       
/* 638 */       this.numConnections--;
/* 639 */       notifyWaitingThread(rospl);
/*     */     } finally {
/*     */       
/* 642 */       this.poolLock.unlock();
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
/*     */ 
/*     */ 
/*     */   
/*     */   protected void notifyWaitingThread(RouteSpecificPool rospl) {
/* 661 */     WaitingThread waitingThread = null;
/*     */     
/* 663 */     this.poolLock.lock();
/*     */     
/*     */     try {
/* 666 */       if (rospl != null && rospl.hasThread()) {
/* 667 */         if (this.log.isDebugEnabled()) {
/* 668 */           this.log.debug("Notifying thread waiting on pool [" + rospl.getRoute() + "]");
/*     */         }
/*     */         
/* 671 */         waitingThread = rospl.nextThread();
/* 672 */       } else if (!this.waitingThreads.isEmpty()) {
/* 673 */         if (this.log.isDebugEnabled()) {
/* 674 */           this.log.debug("Notifying thread waiting on any pool");
/*     */         }
/* 676 */         waitingThread = this.waitingThreads.remove();
/* 677 */       } else if (this.log.isDebugEnabled()) {
/* 678 */         this.log.debug("Notifying no-one, there are no waiting threads");
/*     */       } 
/*     */       
/* 681 */       if (waitingThread != null) {
/* 682 */         waitingThread.wakeup();
/*     */       }
/*     */     } finally {
/*     */       
/* 686 */       this.poolLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void deleteClosedConnections() {
/* 693 */     this.poolLock.lock();
/*     */     try {
/* 695 */       Iterator<BasicPoolEntry> iter = this.freeConnections.iterator();
/* 696 */       while (iter.hasNext()) {
/* 697 */         BasicPoolEntry entry = iter.next();
/* 698 */         if (!entry.getConnection().isOpen()) {
/* 699 */           iter.remove();
/* 700 */           deleteEntry(entry);
/*     */         } 
/*     */       } 
/*     */     } finally {
/* 704 */       this.poolLock.unlock();
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
/*     */   public void closeIdleConnections(long idletime, TimeUnit timeUnit) {
/* 717 */     Args.notNull(timeUnit, "Time unit");
/* 718 */     long t = (idletime > 0L) ? idletime : 0L;
/* 719 */     if (this.log.isDebugEnabled()) {
/* 720 */       this.log.debug("Closing connections idle longer than " + t + " " + timeUnit);
/*     */     }
/*     */     
/* 723 */     long deadline = System.currentTimeMillis() - timeUnit.toMillis(t);
/* 724 */     this.poolLock.lock();
/*     */     try {
/* 726 */       Iterator<BasicPoolEntry> iter = this.freeConnections.iterator();
/* 727 */       while (iter.hasNext()) {
/* 728 */         BasicPoolEntry entry = iter.next();
/* 729 */         if (entry.getUpdated() <= deadline) {
/* 730 */           if (this.log.isDebugEnabled()) {
/* 731 */             this.log.debug("Closing connection last used @ " + new Date(entry.getUpdated()));
/*     */           }
/* 733 */           iter.remove();
/* 734 */           deleteEntry(entry);
/*     */         } 
/*     */       } 
/*     */     } finally {
/* 738 */       this.poolLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void closeExpiredConnections() {
/* 744 */     this.log.debug("Closing expired connections");
/* 745 */     long now = System.currentTimeMillis();
/*     */     
/* 747 */     this.poolLock.lock();
/*     */     try {
/* 749 */       Iterator<BasicPoolEntry> iter = this.freeConnections.iterator();
/* 750 */       while (iter.hasNext()) {
/* 751 */         BasicPoolEntry entry = iter.next();
/* 752 */         if (entry.isExpired(now)) {
/* 753 */           if (this.log.isDebugEnabled()) {
/* 754 */             this.log.debug("Closing connection expired @ " + new Date(entry.getExpiry()));
/*     */           }
/* 756 */           iter.remove();
/* 757 */           deleteEntry(entry);
/*     */         } 
/*     */       } 
/*     */     } finally {
/* 761 */       this.poolLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void shutdown() {
/* 767 */     this.poolLock.lock();
/*     */     try {
/* 769 */       if (this.shutdown) {
/*     */         return;
/*     */       }
/* 772 */       this.shutdown = true;
/*     */ 
/*     */       
/* 775 */       Iterator<BasicPoolEntry> iter1 = this.leasedConnections.iterator();
/* 776 */       while (iter1.hasNext()) {
/* 777 */         BasicPoolEntry entry = iter1.next();
/* 778 */         iter1.remove();
/* 779 */         closeConnection(entry);
/*     */       } 
/*     */ 
/*     */       
/* 783 */       Iterator<BasicPoolEntry> iter2 = this.freeConnections.iterator();
/* 784 */       while (iter2.hasNext()) {
/* 785 */         BasicPoolEntry entry = iter2.next();
/* 786 */         iter2.remove();
/*     */         
/* 788 */         if (this.log.isDebugEnabled()) {
/* 789 */           this.log.debug("Closing connection [" + entry.getPlannedRoute() + "][" + entry.getState() + "]");
/*     */         }
/*     */         
/* 792 */         closeConnection(entry);
/*     */       } 
/*     */ 
/*     */       
/* 796 */       Iterator<WaitingThread> iwth = this.waitingThreads.iterator();
/* 797 */       while (iwth.hasNext()) {
/* 798 */         WaitingThread waiter = iwth.next();
/* 799 */         iwth.remove();
/* 800 */         waiter.wakeup();
/*     */       } 
/*     */       
/* 803 */       this.routeToPool.clear();
/*     */     } finally {
/*     */       
/* 806 */       this.poolLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMaxTotalConnections(int max) {
/* 814 */     this.poolLock.lock();
/*     */     try {
/* 816 */       this.maxTotalConnections = max;
/*     */     } finally {
/* 818 */       this.poolLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMaxTotalConnections() {
/* 827 */     return this.maxTotalConnections;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\tsccm\ConnPoolByRoute.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */