/*     */ package freemarker.cache;
/*     */ 
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.lang.ref.SoftReference;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
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
/*     */ public class MruCacheStorage
/*     */   implements CacheStorageWithGetSize
/*     */ {
/*  58 */   private final MruEntry strongHead = new MruEntry();
/*  59 */   private final MruEntry softHead = new MruEntry(); private final Map map; private final ReferenceQueue refQueue; private final int strongSizeLimit;
/*     */   public MruCacheStorage(int strongSizeLimit, int softSizeLimit) {
/*  61 */     this.softHead.linkAfter(this.strongHead);
/*     */     
/*  63 */     this.map = new HashMap<>();
/*  64 */     this.refQueue = new ReferenceQueue();
/*     */ 
/*     */     
/*  67 */     this.strongSize = 0;
/*  68 */     this.softSize = 0;
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
/*  79 */     if (strongSizeLimit < 0) throw new IllegalArgumentException("strongSizeLimit < 0"); 
/*  80 */     if (softSizeLimit < 0) throw new IllegalArgumentException("softSizeLimit < 0"); 
/*  81 */     this.strongSizeLimit = strongSizeLimit;
/*  82 */     this.softSizeLimit = softSizeLimit;
/*     */   }
/*     */   private final int softSizeLimit; private int strongSize; private int softSize;
/*     */   
/*     */   public Object get(Object key) {
/*  87 */     removeClearedReferences();
/*  88 */     MruEntry entry = (MruEntry)this.map.get(key);
/*  89 */     if (entry == null) {
/*  90 */       return null;
/*     */     }
/*  92 */     relinkEntryAfterStrongHead(entry, null);
/*  93 */     Object value = entry.getValue();
/*  94 */     if (value instanceof MruReference)
/*     */     {
/*  96 */       return ((MruReference)value).get();
/*     */     }
/*  98 */     return value;
/*     */   }
/*     */ 
/*     */   
/*     */   public void put(Object key, Object value) {
/* 103 */     removeClearedReferences();
/* 104 */     MruEntry entry = (MruEntry)this.map.get(key);
/* 105 */     if (entry == null) {
/* 106 */       entry = new MruEntry(key, value);
/* 107 */       this.map.put(key, entry);
/* 108 */       linkAfterStrongHead(entry);
/*     */     } else {
/* 110 */       relinkEntryAfterStrongHead(entry, value);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void remove(Object key) {
/* 117 */     removeClearedReferences();
/* 118 */     removeInternal(key);
/*     */   }
/*     */   
/*     */   private void removeInternal(Object key) {
/* 122 */     MruEntry entry = (MruEntry)this.map.remove(key);
/* 123 */     if (entry != null) {
/* 124 */       unlinkEntryAndInspectIfSoft(entry);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/* 130 */     this.strongHead.makeHead();
/* 131 */     this.softHead.linkAfter(this.strongHead);
/* 132 */     this.map.clear();
/* 133 */     this.strongSize = this.softSize = 0;
/*     */     
/* 135 */     while (this.refQueue.poll() != null);
/*     */   }
/*     */   
/*     */   private void relinkEntryAfterStrongHead(MruEntry entry, Object newValue) {
/* 139 */     if (unlinkEntryAndInspectIfSoft(entry) && newValue == null) {
/*     */       
/* 141 */       MruReference mref = (MruReference)entry.getValue();
/* 142 */       Object strongValue = mref.get();
/* 143 */       if (strongValue != null) {
/* 144 */         entry.setValue(strongValue);
/* 145 */         linkAfterStrongHead(entry);
/*     */       } else {
/* 147 */         this.map.remove(mref.getKey());
/*     */       } 
/*     */     } else {
/* 150 */       if (newValue != null) {
/* 151 */         entry.setValue(newValue);
/*     */       }
/* 153 */       linkAfterStrongHead(entry);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void linkAfterStrongHead(MruEntry entry) {
/* 158 */     entry.linkAfter(this.strongHead);
/* 159 */     if (this.strongSize == this.strongSizeLimit) {
/*     */       
/* 161 */       MruEntry lruStrong = this.softHead.getPrevious();
/*     */ 
/*     */ 
/*     */       
/* 165 */       if (lruStrong != this.strongHead) {
/* 166 */         lruStrong.unlink();
/* 167 */         if (this.softSizeLimit > 0) {
/* 168 */           lruStrong.linkAfter(this.softHead);
/* 169 */           lruStrong.setValue(new MruReference(lruStrong, this.refQueue));
/* 170 */           if (this.softSize == this.softSizeLimit) {
/*     */             
/* 172 */             MruEntry lruSoft = this.strongHead.getPrevious();
/* 173 */             lruSoft.unlink();
/* 174 */             this.map.remove(lruSoft.getKey());
/*     */           } else {
/* 176 */             this.softSize++;
/*     */           } 
/*     */         } else {
/* 179 */           this.map.remove(lruStrong.getKey());
/*     */         } 
/*     */       } 
/*     */     } else {
/* 183 */       this.strongSize++;
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean unlinkEntryAndInspectIfSoft(MruEntry entry) {
/* 188 */     entry.unlink();
/* 189 */     if (entry.getValue() instanceof MruReference) {
/* 190 */       this.softSize--;
/* 191 */       return true;
/*     */     } 
/* 193 */     this.strongSize--;
/* 194 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private void removeClearedReferences() {
/*     */     while (true) {
/* 200 */       MruReference ref = (MruReference)this.refQueue.poll();
/* 201 */       if (ref == null) {
/*     */         break;
/*     */       }
/* 204 */       removeInternal(ref.getKey());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getStrongSizeLimit() {
/* 214 */     return this.strongSizeLimit;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSoftSizeLimit() {
/* 223 */     return this.softSizeLimit;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getStrongSize() {
/* 233 */     return this.strongSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSoftSize() {
/* 243 */     removeClearedReferences();
/* 244 */     return this.softSize;
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
/*     */   public int getSize() {
/* 256 */     return getSoftSize() + getStrongSize();
/*     */   }
/*     */ 
/*     */   
/*     */   private static final class MruEntry
/*     */   {
/*     */     private MruEntry prev;
/*     */     
/*     */     private MruEntry next;
/*     */     private final Object key;
/*     */     private Object value;
/*     */     
/*     */     MruEntry() {
/* 269 */       makeHead();
/* 270 */       this.key = this.value = null;
/*     */     }
/*     */     
/*     */     MruEntry(Object key, Object value) {
/* 274 */       this.key = key;
/* 275 */       this.value = value;
/*     */     }
/*     */     
/*     */     Object getKey() {
/* 279 */       return this.key;
/*     */     }
/*     */     
/*     */     Object getValue() {
/* 283 */       return this.value;
/*     */     }
/*     */     
/*     */     void setValue(Object value) {
/* 287 */       this.value = value;
/*     */     }
/*     */     
/*     */     MruEntry getPrevious() {
/* 291 */       return this.prev;
/*     */     }
/*     */     
/*     */     void linkAfter(MruEntry entry) {
/* 295 */       this.next = entry.next;
/* 296 */       entry.next = this;
/* 297 */       this.prev = entry;
/* 298 */       this.next.prev = this;
/*     */     }
/*     */     
/*     */     void unlink() {
/* 302 */       this.next.prev = this.prev;
/* 303 */       this.prev.next = this.next;
/* 304 */       this.prev = null;
/* 305 */       this.next = null;
/*     */     }
/*     */     
/*     */     void makeHead() {
/* 309 */       this.prev = this.next = this;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class MruReference extends SoftReference {
/*     */     private final Object key;
/*     */     
/*     */     MruReference(MruCacheStorage.MruEntry entry, ReferenceQueue<? super T> queue) {
/* 317 */       super((T)entry.getValue(), queue);
/* 318 */       this.key = entry.getKey();
/*     */     }
/*     */     
/*     */     Object getKey() {
/* 322 */       return this.key;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\cache\MruCacheStorage.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */