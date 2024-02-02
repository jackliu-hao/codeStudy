/*     */ package org.apache.commons.logging.impl;
/*     */ 
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashSet;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class WeakHashtable
/*     */   extends Hashtable
/*     */ {
/*     */   private static final long serialVersionUID = -1546036869799732453L;
/*     */   private static final int MAX_CHANGES_BEFORE_PURGE = 100;
/*     */   private static final int PARTIAL_PURGE_COUNT = 10;
/* 129 */   private final ReferenceQueue queue = new ReferenceQueue();
/*     */   
/* 131 */   private int changeCount = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean containsKey(Object key) {
/* 144 */     Referenced referenced = new Referenced(key);
/* 145 */     return super.containsKey(referenced);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Enumeration elements() {
/* 152 */     purge();
/* 153 */     return super.elements();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set entrySet() {
/* 160 */     purge();
/* 161 */     Set referencedEntries = super.entrySet();
/* 162 */     Set unreferencedEntries = new HashSet();
/* 163 */     for (Iterator it = referencedEntries.iterator(); it.hasNext(); ) {
/* 164 */       Map.Entry entry = it.next();
/* 165 */       Referenced referencedKey = (Referenced)entry.getKey();
/* 166 */       Object key = referencedKey.getValue();
/* 167 */       Object value = entry.getValue();
/* 168 */       if (key != null) {
/* 169 */         Entry dereferencedEntry = new Entry(key, value);
/* 170 */         unreferencedEntries.add(dereferencedEntry);
/*     */       } 
/*     */     } 
/* 173 */     return unreferencedEntries;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object get(Object key) {
/* 181 */     Referenced referenceKey = new Referenced(key);
/* 182 */     return super.get(referenceKey);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Enumeration keys() {
/* 189 */     purge();
/* 190 */     Enumeration enumer = super.keys();
/* 191 */     return new Enumeration(this, enumer) { private final Enumeration val$enumer;
/*     */         public boolean hasMoreElements() {
/* 193 */           return this.val$enumer.hasMoreElements();
/*     */         } private final WeakHashtable this$0;
/*     */         public Object nextElement() {
/* 196 */           WeakHashtable.Referenced nextReference = this.val$enumer.nextElement();
/* 197 */           return nextReference.getValue();
/*     */         } }
/*     */       ;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set keySet() {
/* 206 */     purge();
/* 207 */     Set referencedKeys = super.keySet();
/* 208 */     Set unreferencedKeys = new HashSet();
/* 209 */     for (Iterator it = referencedKeys.iterator(); it.hasNext(); ) {
/* 210 */       Referenced referenceKey = (Referenced)it.next();
/* 211 */       Object keyValue = referenceKey.getValue();
/* 212 */       if (keyValue != null) {
/* 213 */         unreferencedKeys.add(keyValue);
/*     */       }
/*     */     } 
/* 216 */     return unreferencedKeys;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized Object put(Object key, Object value) {
/* 224 */     if (key == null) {
/* 225 */       throw new NullPointerException("Null keys are not allowed");
/*     */     }
/* 227 */     if (value == null) {
/* 228 */       throw new NullPointerException("Null values are not allowed");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 233 */     if (this.changeCount++ > 100) {
/* 234 */       purge();
/* 235 */       this.changeCount = 0;
/*     */     
/*     */     }
/* 238 */     else if (this.changeCount % 10 == 0) {
/* 239 */       purgeOne();
/*     */     } 
/*     */     
/* 242 */     Referenced keyRef = new Referenced(key, this.queue);
/* 243 */     return super.put(keyRef, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void putAll(Map t) {
/* 250 */     if (t != null) {
/* 251 */       Set entrySet = t.entrySet();
/* 252 */       for (Iterator it = entrySet.iterator(); it.hasNext(); ) {
/* 253 */         Map.Entry entry = it.next();
/* 254 */         put(entry.getKey(), entry.getValue());
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Collection values() {
/* 263 */     purge();
/* 264 */     return super.values();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized Object remove(Object key) {
/* 273 */     if (this.changeCount++ > 100) {
/* 274 */       purge();
/* 275 */       this.changeCount = 0;
/*     */     
/*     */     }
/* 278 */     else if (this.changeCount % 10 == 0) {
/* 279 */       purgeOne();
/*     */     } 
/* 281 */     return super.remove(new Referenced(key));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 288 */     purge();
/* 289 */     return super.isEmpty();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/* 296 */     purge();
/* 297 */     return super.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 304 */     purge();
/* 305 */     return super.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void rehash() {
/* 313 */     purge();
/* 314 */     super.rehash();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void purge() {
/* 322 */     List toRemove = new ArrayList();
/* 323 */     synchronized (this.queue) {
/*     */       WeakKey key;
/* 325 */       while ((key = (WeakKey)this.queue.poll()) != null) {
/* 326 */         toRemove.add(key.getReferenced());
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 333 */     int size = toRemove.size();
/* 334 */     for (int i = 0; i < size; i++) {
/* 335 */       super.remove(toRemove.get(i));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void purgeOne() {
/* 344 */     synchronized (this.queue) {
/* 345 */       WeakKey key = (WeakKey)this.queue.poll();
/* 346 */       if (key != null) {
/* 347 */         super.remove(key.getReferenced());
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private static final class Entry
/*     */     implements Map.Entry
/*     */   {
/*     */     private final Object key;
/*     */     private final Object value;
/*     */     
/*     */     private Entry(Object key, Object value) {
/* 359 */       this.key = key;
/* 360 */       this.value = value;
/*     */     }
/*     */     
/*     */     public boolean equals(Object o) {
/* 364 */       boolean result = false;
/* 365 */       if (o != null && o instanceof Map.Entry) {
/* 366 */         Map.Entry entry = (Map.Entry)o;
/* 367 */         result = (((getKey() == null) ? (entry.getKey() == null) : getKey().equals(entry.getKey())) && ((getValue() == null) ? (entry.getValue() == null) : getValue().equals(entry.getValue())));
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 374 */       return result;
/*     */     }
/*     */     
/*     */     public int hashCode() {
/* 378 */       return ((getKey() == null) ? 0 : getKey().hashCode()) ^ ((getValue() == null) ? 0 : getValue().hashCode());
/*     */     }
/*     */ 
/*     */     
/*     */     public Object setValue(Object value) {
/* 383 */       throw new UnsupportedOperationException("Entry.setValue is not supported.");
/*     */     }
/*     */     
/*     */     public Object getValue() {
/* 387 */       return this.value;
/*     */     }
/*     */     
/*     */     public Object getKey() {
/* 391 */       return this.key;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class Referenced
/*     */   {
/*     */     private final WeakReference reference;
/*     */ 
/*     */     
/*     */     private final int hashCode;
/*     */ 
/*     */     
/*     */     private Referenced(Object referant) {
/* 406 */       this.reference = new WeakReference(referant);
/*     */ 
/*     */       
/* 409 */       this.hashCode = referant.hashCode();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private Referenced(Object key, ReferenceQueue queue) {
/* 417 */       this.reference = new WeakHashtable.WeakKey(key, queue, this);
/*     */ 
/*     */       
/* 420 */       this.hashCode = key.hashCode();
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 425 */       return this.hashCode;
/*     */     }
/*     */     
/*     */     private Object getValue() {
/* 429 */       return this.reference.get();
/*     */     }
/*     */     
/*     */     public boolean equals(Object o) {
/* 433 */       boolean result = false;
/* 434 */       if (o instanceof Referenced) {
/* 435 */         Referenced otherKey = (Referenced)o;
/* 436 */         Object thisKeyValue = getValue();
/* 437 */         Object otherKeyValue = otherKey.getValue();
/* 438 */         if (thisKeyValue == null) {
/* 439 */           result = (otherKeyValue == null);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 447 */           result = (result && hashCode() == otherKey.hashCode());
/*     */ 
/*     */         
/*     */         }
/*     */         else {
/*     */ 
/*     */ 
/*     */           
/* 455 */           result = thisKeyValue.equals(otherKeyValue);
/*     */         } 
/*     */       } 
/* 458 */       return result;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class WeakKey
/*     */     extends WeakReference
/*     */   {
/*     */     private final WeakHashtable.Referenced referenced;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private WeakKey(Object key, ReferenceQueue queue, WeakHashtable.Referenced referenced) {
/* 474 */       super((T)key, queue);
/* 475 */       this.referenced = referenced;
/*     */     }
/*     */     
/*     */     private WeakHashtable.Referenced getReferenced() {
/* 479 */       return this.referenced;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\logging\impl\WeakHashtable.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */