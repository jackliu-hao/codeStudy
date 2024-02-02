/*     */ package ch.qos.logback.core.spi;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
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
/*     */ public abstract class AbstractComponentTracker<C>
/*     */   implements ComponentTracker<C>
/*     */ {
/*     */   private static final boolean ACCESS_ORDERED = true;
/*     */   public static final long LINGERING_TIMEOUT = 10000L;
/*     */   public static final long WAIT_BETWEEN_SUCCESSIVE_REMOVAL_ITERATIONS = 1000L;
/*  42 */   protected int maxComponents = Integer.MAX_VALUE;
/*  43 */   protected long timeout = 1800000L;
/*     */ 
/*     */   
/*  46 */   LinkedHashMap<String, Entry<C>> liveMap = new LinkedHashMap<String, Entry<C>>(32, 0.75F, true);
/*     */ 
/*     */   
/*  49 */   LinkedHashMap<String, Entry<C>> lingerersMap = new LinkedHashMap<String, Entry<C>>(16, 0.75F, true);
/*  50 */   long lastCheck = 0L;
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
/*     */   public int getComponentCount() {
/*  77 */     return this.liveMap.size() + this.lingerersMap.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Entry<C> getFromEitherMap(String key) {
/*  87 */     Entry<C> entry = this.liveMap.get(key);
/*  88 */     if (entry != null) {
/*  89 */       return entry;
/*     */     }
/*  91 */     return this.lingerersMap.get(key);
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
/*     */   public synchronized C find(String key) {
/* 105 */     Entry<C> entry = getFromEitherMap(key);
/* 106 */     if (entry == null) {
/* 107 */       return null;
/*     */     }
/* 109 */     return entry.component;
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
/*     */   public synchronized C getOrCreate(String key, long timestamp) {
/* 122 */     Entry<C> entry = getFromEitherMap(key);
/* 123 */     if (entry == null) {
/* 124 */       C c = buildComponent(key);
/* 125 */       entry = new Entry<C>(key, c, timestamp);
/*     */       
/* 127 */       this.liveMap.put(key, entry);
/*     */     } else {
/* 129 */       entry.setTimestamp(timestamp);
/*     */     } 
/* 131 */     return entry.component;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void endOfLife(String key) {
/* 140 */     Entry<C> entry = this.liveMap.remove(key);
/* 141 */     if (entry == null)
/*     */       return; 
/* 143 */     this.lingerersMap.put(key, entry);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void removeStaleComponents(long now) {
/* 153 */     if (isTooSoonForRemovalIteration(now))
/*     */       return; 
/* 155 */     removeExcedentComponents();
/* 156 */     removeStaleComponentsFromMainMap(now);
/* 157 */     removeStaleComponentsFromLingerersMap(now);
/*     */   }
/*     */   
/*     */   private void removeExcedentComponents() {
/* 161 */     genericStaleComponentRemover(this.liveMap, 0L, this.byExcedent);
/*     */   }
/*     */   
/*     */   private void removeStaleComponentsFromMainMap(long now) {
/* 165 */     genericStaleComponentRemover(this.liveMap, now, this.byTimeout);
/*     */   }
/*     */   
/*     */   private void removeStaleComponentsFromLingerersMap(long now) {
/* 169 */     genericStaleComponentRemover(this.lingerersMap, now, this.byLingering);
/*     */   }
/*     */   
/*     */   private void genericStaleComponentRemover(LinkedHashMap<String, Entry<C>> map, long now, RemovalPredicator<C> removalPredicator) {
/* 173 */     Iterator<Map.Entry<String, Entry<C>>> iter = map.entrySet().iterator();
/* 174 */     while (iter.hasNext()) {
/* 175 */       Map.Entry<String, Entry<C>> mapEntry = iter.next();
/* 176 */       Entry<C> entry = mapEntry.getValue();
/* 177 */       if (removalPredicator.isSlatedForRemoval(entry, now)) {
/* 178 */         iter.remove();
/* 179 */         C c = entry.component;
/* 180 */         processPriorToRemoval(c);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 187 */   private RemovalPredicator<C> byExcedent = new RemovalPredicator<C>() {
/*     */       public boolean isSlatedForRemoval(AbstractComponentTracker.Entry<C> entry, long timestamp) {
/* 189 */         return (AbstractComponentTracker.this.liveMap.size() > AbstractComponentTracker.this.maxComponents);
/*     */       }
/*     */     };
/*     */   
/* 193 */   private RemovalPredicator<C> byTimeout = new RemovalPredicator<C>() {
/*     */       public boolean isSlatedForRemoval(AbstractComponentTracker.Entry<C> entry, long timestamp) {
/* 195 */         return AbstractComponentTracker.this.isEntryStale(entry, timestamp);
/*     */       }
/*     */     };
/* 198 */   private RemovalPredicator<C> byLingering = new RemovalPredicator<C>() {
/*     */       public boolean isSlatedForRemoval(AbstractComponentTracker.Entry<C> entry, long timestamp) {
/* 200 */         return AbstractComponentTracker.this.isEntryDoneLingering(entry, timestamp);
/*     */       }
/*     */     };
/*     */   
/*     */   private boolean isTooSoonForRemovalIteration(long now) {
/* 205 */     if (this.lastCheck + 1000L > now) {
/* 206 */       return true;
/*     */     }
/* 208 */     this.lastCheck = now;
/* 209 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isEntryStale(Entry<C> entry, long now) {
/* 215 */     C c = entry.component;
/* 216 */     if (isComponentStale(c)) {
/* 217 */       return true;
/*     */     }
/* 219 */     return (entry.timestamp + this.timeout < now);
/*     */   }
/*     */   
/*     */   private boolean isEntryDoneLingering(Entry<C> entry, long now) {
/* 223 */     return (entry.timestamp + 10000L < now);
/*     */   }
/*     */   
/*     */   public Set<String> allKeys() {
/* 227 */     HashSet<String> allKeys = new HashSet<String>(this.liveMap.keySet());
/* 228 */     allKeys.addAll(this.lingerersMap.keySet());
/* 229 */     return allKeys;
/*     */   }
/*     */   
/*     */   public Collection<C> allComponents() {
/* 233 */     List<C> allComponents = new ArrayList<C>();
/* 234 */     for (Entry<C> e : this.liveMap.values())
/* 235 */       allComponents.add(e.component); 
/* 236 */     for (Entry<C> e : this.lingerersMap.values()) {
/* 237 */       allComponents.add(e.component);
/*     */     }
/* 239 */     return allComponents;
/*     */   }
/*     */   
/*     */   public long getTimeout() {
/* 243 */     return this.timeout;
/*     */   }
/*     */   
/*     */   public void setTimeout(long timeout) {
/* 247 */     this.timeout = timeout;
/*     */   }
/*     */   
/*     */   public int getMaxComponents() {
/* 251 */     return this.maxComponents;
/*     */   }
/*     */   
/*     */   public void setMaxComponents(int maxComponents) {
/* 255 */     this.maxComponents = maxComponents;
/*     */   }
/*     */   
/*     */   protected abstract void processPriorToRemoval(C paramC);
/*     */   
/*     */   protected abstract C buildComponent(String paramString);
/*     */   
/*     */   protected abstract boolean isComponentStale(C paramC);
/*     */   
/*     */   private static interface RemovalPredicator<C> {
/*     */     boolean isSlatedForRemoval(AbstractComponentTracker.Entry<C> param1Entry, long param1Long); }
/*     */   
/*     */   private static class Entry<C> { String key;
/*     */     
/*     */     Entry(String k, C c, long timestamp) {
/* 270 */       this.key = k;
/* 271 */       this.component = c;
/* 272 */       this.timestamp = timestamp;
/*     */     }
/*     */     C component; long timestamp;
/*     */     public void setTimestamp(long timestamp) {
/* 276 */       this.timestamp = timestamp;
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 281 */       return this.key.hashCode();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object obj) {
/* 286 */       if (this == obj)
/* 287 */         return true; 
/* 288 */       if (obj == null)
/* 289 */         return false; 
/* 290 */       if (getClass() != obj.getClass()) {
/* 291 */         return false;
/*     */       }
/* 293 */       Entry<C> other = (Entry<C>)obj;
/* 294 */       if (this.key == null) {
/* 295 */         if (other.key != null)
/* 296 */           return false; 
/* 297 */       } else if (!this.key.equals(other.key)) {
/* 298 */         return false;
/* 299 */       }  if (this.component == null) {
/* 300 */         if (other.component != null)
/* 301 */           return false; 
/* 302 */       } else if (!this.component.equals(other.component)) {
/* 303 */         return false;
/* 304 */       }  return true;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 309 */       return "(" + this.key + ", " + this.component + ")";
/*     */     } }
/*     */ 
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\spi\AbstractComponentTracker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */