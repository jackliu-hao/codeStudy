/*     */ package org.apache.http.pool;
/*     */ 
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.Future;
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
/*     */ abstract class RouteSpecificPool<T, C, E extends PoolEntry<T, C>>
/*     */ {
/*     */   private final T route;
/*     */   private final Set<E> leased;
/*     */   private final LinkedList<E> available;
/*     */   private final LinkedList<Future<E>> pending;
/*     */   
/*     */   RouteSpecificPool(T route) {
/*  47 */     this.route = route;
/*  48 */     this.leased = new HashSet<E>();
/*  49 */     this.available = new LinkedList<E>();
/*  50 */     this.pending = new LinkedList<Future<E>>();
/*     */   }
/*     */   
/*     */   protected abstract E createEntry(C paramC);
/*     */   
/*     */   public final T getRoute() {
/*  56 */     return this.route;
/*     */   }
/*     */   
/*     */   public int getLeasedCount() {
/*  60 */     return this.leased.size();
/*     */   }
/*     */   
/*     */   public int getPendingCount() {
/*  64 */     return this.pending.size();
/*     */   }
/*     */   
/*     */   public int getAvailableCount() {
/*  68 */     return this.available.size();
/*     */   }
/*     */   
/*     */   public int getAllocatedCount() {
/*  72 */     return this.available.size() + this.leased.size();
/*     */   }
/*     */   
/*     */   public E getFree(Object state) {
/*  76 */     if (!this.available.isEmpty()) {
/*  77 */       if (state != null) {
/*  78 */         Iterator<E> iterator = this.available.iterator();
/*  79 */         while (iterator.hasNext()) {
/*  80 */           PoolEntry poolEntry = (PoolEntry)iterator.next();
/*  81 */           if (state.equals(poolEntry.getState())) {
/*  82 */             iterator.remove();
/*  83 */             this.leased.add((E)poolEntry);
/*  84 */             return (E)poolEntry;
/*     */           } 
/*     */         } 
/*     */       } 
/*  88 */       Iterator<E> it = this.available.iterator();
/*  89 */       while (it.hasNext()) {
/*  90 */         PoolEntry poolEntry = (PoolEntry)it.next();
/*  91 */         if (poolEntry.getState() == null) {
/*  92 */           it.remove();
/*  93 */           this.leased.add((E)poolEntry);
/*  94 */           return (E)poolEntry;
/*     */         } 
/*     */       } 
/*     */     } 
/*  98 */     return null;
/*     */   }
/*     */   
/*     */   public E getLastUsed() {
/* 102 */     return this.available.isEmpty() ? null : this.available.getLast();
/*     */   }
/*     */   
/*     */   public boolean remove(E entry) {
/* 106 */     Args.notNull(entry, "Pool entry");
/* 107 */     if (!this.available.remove(entry) && 
/* 108 */       !this.leased.remove(entry)) {
/* 109 */       return false;
/*     */     }
/*     */     
/* 112 */     return true;
/*     */   }
/*     */   
/*     */   public void free(E entry, boolean reusable) {
/* 116 */     Args.notNull(entry, "Pool entry");
/* 117 */     boolean found = this.leased.remove(entry);
/* 118 */     Asserts.check(found, "Entry %s has not been leased from this pool", entry);
/* 119 */     if (reusable) {
/* 120 */       this.available.addFirst(entry);
/*     */     }
/*     */   }
/*     */   
/*     */   public E add(C conn) {
/* 125 */     E entry = createEntry(conn);
/* 126 */     this.leased.add(entry);
/* 127 */     return entry;
/*     */   }
/*     */   
/*     */   public void queue(Future<E> future) {
/* 131 */     if (future == null) {
/*     */       return;
/*     */     }
/* 134 */     this.pending.add(future);
/*     */   }
/*     */   
/*     */   public Future<E> nextPending() {
/* 138 */     return this.pending.poll();
/*     */   }
/*     */   
/*     */   public void unqueue(Future<E> future) {
/* 142 */     if (future == null) {
/*     */       return;
/*     */     }
/*     */     
/* 146 */     this.pending.remove(future);
/*     */   }
/*     */   
/*     */   public void shutdown() {
/* 150 */     for (Future<E> future : this.pending) {
/* 151 */       future.cancel(true);
/*     */     }
/* 153 */     this.pending.clear();
/* 154 */     for (PoolEntry poolEntry : this.available) {
/* 155 */       poolEntry.close();
/*     */     }
/* 157 */     this.available.clear();
/* 158 */     for (PoolEntry poolEntry : this.leased) {
/* 159 */       poolEntry.close();
/*     */     }
/* 161 */     this.leased.clear();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 166 */     StringBuilder buffer = new StringBuilder();
/* 167 */     buffer.append("[route: ");
/* 168 */     buffer.append(this.route);
/* 169 */     buffer.append("][leased: ");
/* 170 */     buffer.append(this.leased.size());
/* 171 */     buffer.append("][available: ");
/* 172 */     buffer.append(this.available.size());
/* 173 */     buffer.append("][pending: ");
/* 174 */     buffer.append(this.pending.size());
/* 175 */     buffer.append("]");
/* 176 */     return buffer.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\pool\RouteSpecificPool.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */