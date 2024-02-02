package org.apache.http.pool;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.Future;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

abstract class RouteSpecificPool<T, C, E extends PoolEntry<T, C>> {
   private final T route;
   private final Set<E> leased;
   private final LinkedList<E> available;
   private final LinkedList<Future<E>> pending;

   RouteSpecificPool(T route) {
      this.route = route;
      this.leased = new HashSet();
      this.available = new LinkedList();
      this.pending = new LinkedList();
   }

   protected abstract E createEntry(C var1);

   public final T getRoute() {
      return this.route;
   }

   public int getLeasedCount() {
      return this.leased.size();
   }

   public int getPendingCount() {
      return this.pending.size();
   }

   public int getAvailableCount() {
      return this.available.size();
   }

   public int getAllocatedCount() {
      return this.available.size() + this.leased.size();
   }

   public E getFree(Object state) {
      if (!this.available.isEmpty()) {
         Iterator it;
         PoolEntry entry;
         if (state != null) {
            it = this.available.iterator();

            while(it.hasNext()) {
               entry = (PoolEntry)it.next();
               if (state.equals(entry.getState())) {
                  it.remove();
                  this.leased.add(entry);
                  return entry;
               }
            }
         }

         it = this.available.iterator();

         while(it.hasNext()) {
            entry = (PoolEntry)it.next();
            if (entry.getState() == null) {
               it.remove();
               this.leased.add(entry);
               return entry;
            }
         }
      }

      return null;
   }

   public E getLastUsed() {
      return this.available.isEmpty() ? null : (PoolEntry)this.available.getLast();
   }

   public boolean remove(E entry) {
      Args.notNull(entry, "Pool entry");
      return this.available.remove(entry) || this.leased.remove(entry);
   }

   public void free(E entry, boolean reusable) {
      Args.notNull(entry, "Pool entry");
      boolean found = this.leased.remove(entry);
      Asserts.check(found, "Entry %s has not been leased from this pool", (Object)entry);
      if (reusable) {
         this.available.addFirst(entry);
      }

   }

   public E add(C conn) {
      E entry = this.createEntry(conn);
      this.leased.add(entry);
      return entry;
   }

   public void queue(Future<E> future) {
      if (future != null) {
         this.pending.add(future);
      }
   }

   public Future<E> nextPending() {
      return (Future)this.pending.poll();
   }

   public void unqueue(Future<E> future) {
      if (future != null) {
         this.pending.remove(future);
      }
   }

   public void shutdown() {
      Iterator i$ = this.pending.iterator();

      while(i$.hasNext()) {
         Future<E> future = (Future)i$.next();
         future.cancel(true);
      }

      this.pending.clear();
      i$ = this.available.iterator();

      PoolEntry entry;
      while(i$.hasNext()) {
         entry = (PoolEntry)i$.next();
         entry.close();
      }

      this.available.clear();
      i$ = this.leased.iterator();

      while(i$.hasNext()) {
         entry = (PoolEntry)i$.next();
         entry.close();
      }

      this.leased.clear();
   }

   public String toString() {
      StringBuilder buffer = new StringBuilder();
      buffer.append("[route: ");
      buffer.append(this.route);
      buffer.append("][leased: ");
      buffer.append(this.leased.size());
      buffer.append("][available: ");
      buffer.append(this.available.size());
      buffer.append("][pending: ");
      buffer.append(this.pending.size());
      buffer.append("]");
      return buffer.toString();
   }
}
