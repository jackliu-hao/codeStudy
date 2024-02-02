package ch.qos.logback.core.spi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractComponentTracker<C> implements ComponentTracker<C> {
   private static final boolean ACCESS_ORDERED = true;
   public static final long LINGERING_TIMEOUT = 10000L;
   public static final long WAIT_BETWEEN_SUCCESSIVE_REMOVAL_ITERATIONS = 1000L;
   protected int maxComponents = Integer.MAX_VALUE;
   protected long timeout = 1800000L;
   LinkedHashMap<String, Entry<C>> liveMap = new LinkedHashMap(32, 0.75F, true);
   LinkedHashMap<String, Entry<C>> lingerersMap = new LinkedHashMap(16, 0.75F, true);
   long lastCheck = 0L;
   private RemovalPredicator<C> byExcedent = new RemovalPredicator<C>() {
      public boolean isSlatedForRemoval(Entry<C> entry, long timestamp) {
         return AbstractComponentTracker.this.liveMap.size() > AbstractComponentTracker.this.maxComponents;
      }
   };
   private RemovalPredicator<C> byTimeout = new RemovalPredicator<C>() {
      public boolean isSlatedForRemoval(Entry<C> entry, long timestamp) {
         return AbstractComponentTracker.this.isEntryStale(entry, timestamp);
      }
   };
   private RemovalPredicator<C> byLingering = new RemovalPredicator<C>() {
      public boolean isSlatedForRemoval(Entry<C> entry, long timestamp) {
         return AbstractComponentTracker.this.isEntryDoneLingering(entry, timestamp);
      }
   };

   protected abstract void processPriorToRemoval(C var1);

   protected abstract C buildComponent(String var1);

   protected abstract boolean isComponentStale(C var1);

   public int getComponentCount() {
      return this.liveMap.size() + this.lingerersMap.size();
   }

   private Entry<C> getFromEitherMap(String key) {
      Entry<C> entry = (Entry)this.liveMap.get(key);
      return entry != null ? entry : (Entry)this.lingerersMap.get(key);
   }

   public synchronized C find(String key) {
      Entry<C> entry = this.getFromEitherMap(key);
      return entry == null ? null : entry.component;
   }

   public synchronized C getOrCreate(String key, long timestamp) {
      Entry<C> entry = this.getFromEitherMap(key);
      if (entry == null) {
         C c = this.buildComponent(key);
         entry = new Entry(key, c, timestamp);
         this.liveMap.put(key, entry);
      } else {
         entry.setTimestamp(timestamp);
      }

      return entry.component;
   }

   public void endOfLife(String key) {
      Entry<C> entry = (Entry)this.liveMap.remove(key);
      if (entry != null) {
         this.lingerersMap.put(key, entry);
      }
   }

   public synchronized void removeStaleComponents(long now) {
      if (!this.isTooSoonForRemovalIteration(now)) {
         this.removeExcedentComponents();
         this.removeStaleComponentsFromMainMap(now);
         this.removeStaleComponentsFromLingerersMap(now);
      }
   }

   private void removeExcedentComponents() {
      this.genericStaleComponentRemover(this.liveMap, 0L, this.byExcedent);
   }

   private void removeStaleComponentsFromMainMap(long now) {
      this.genericStaleComponentRemover(this.liveMap, now, this.byTimeout);
   }

   private void removeStaleComponentsFromLingerersMap(long now) {
      this.genericStaleComponentRemover(this.lingerersMap, now, this.byLingering);
   }

   private void genericStaleComponentRemover(LinkedHashMap<String, Entry<C>> map, long now, RemovalPredicator<C> removalPredicator) {
      Iterator<Map.Entry<String, Entry<C>>> iter = map.entrySet().iterator();

      while(iter.hasNext()) {
         Map.Entry<String, Entry<C>> mapEntry = (Map.Entry)iter.next();
         Entry<C> entry = (Entry)mapEntry.getValue();
         if (!removalPredicator.isSlatedForRemoval(entry, now)) {
            break;
         }

         iter.remove();
         C c = entry.component;
         this.processPriorToRemoval(c);
      }

   }

   private boolean isTooSoonForRemovalIteration(long now) {
      if (this.lastCheck + 1000L > now) {
         return true;
      } else {
         this.lastCheck = now;
         return false;
      }
   }

   private boolean isEntryStale(Entry<C> entry, long now) {
      C c = entry.component;
      if (this.isComponentStale(c)) {
         return true;
      } else {
         return entry.timestamp + this.timeout < now;
      }
   }

   private boolean isEntryDoneLingering(Entry<C> entry, long now) {
      return entry.timestamp + 10000L < now;
   }

   public Set<String> allKeys() {
      HashSet<String> allKeys = new HashSet(this.liveMap.keySet());
      allKeys.addAll(this.lingerersMap.keySet());
      return allKeys;
   }

   public Collection<C> allComponents() {
      List<C> allComponents = new ArrayList();
      Iterator var2 = this.liveMap.values().iterator();

      Entry e;
      while(var2.hasNext()) {
         e = (Entry)var2.next();
         allComponents.add(e.component);
      }

      var2 = this.lingerersMap.values().iterator();

      while(var2.hasNext()) {
         e = (Entry)var2.next();
         allComponents.add(e.component);
      }

      return allComponents;
   }

   public long getTimeout() {
      return this.timeout;
   }

   public void setTimeout(long timeout) {
      this.timeout = timeout;
   }

   public int getMaxComponents() {
      return this.maxComponents;
   }

   public void setMaxComponents(int maxComponents) {
      this.maxComponents = maxComponents;
   }

   private static class Entry<C> {
      String key;
      C component;
      long timestamp;

      Entry(String k, C c, long timestamp) {
         this.key = k;
         this.component = c;
         this.timestamp = timestamp;
      }

      public void setTimestamp(long timestamp) {
         this.timestamp = timestamp;
      }

      public int hashCode() {
         return this.key.hashCode();
      }

      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (obj == null) {
            return false;
         } else if (this.getClass() != obj.getClass()) {
            return false;
         } else {
            Entry<C> other = (Entry)obj;
            if (this.key == null) {
               if (other.key != null) {
                  return false;
               }
            } else if (!this.key.equals(other.key)) {
               return false;
            }

            if (this.component == null) {
               if (other.component != null) {
                  return false;
               }
            } else if (!this.component.equals(other.component)) {
               return false;
            }

            return true;
         }
      }

      public String toString() {
         return "(" + this.key + ", " + this.component + ")";
      }
   }

   private interface RemovalPredicator<C> {
      boolean isSlatedForRemoval(Entry<C> var1, long var2);
   }
}
