/*     */ package freemarker.ext.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.util.AbstractCollection;
/*     */ import java.util.AbstractMap;
/*     */ import java.util.AbstractSet;
/*     */ import java.util.Collection;
/*     */ import java.util.ConcurrentModificationException;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.NoSuchElementException;
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
/*     */ @Deprecated
/*     */ public class IdentityHashMap
/*     */   extends AbstractMap
/*     */   implements Map, Cloneable, Serializable
/*     */ {
/*     */   public static final long serialVersionUID = 362498820763181265L;
/*     */   private transient Entry[] table;
/*     */   private transient int count;
/*     */   private int threshold;
/*     */   private float loadFactor;
/*  72 */   private transient int modCount = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private transient Set keySet;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private transient Set entrySet;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private transient Collection values;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int KEYS = 0;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int VALUES = 1;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int ENTRIES = 2;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IdentityHashMap(int initialCapacity) {
/* 106 */     this(initialCapacity, 0.75F);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IdentityHashMap() {
/* 114 */     this(11, 0.75F);
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
/*     */   public IdentityHashMap(Map t) {
/* 126 */     this(Math.max(2 * t.size(), 11), 0.75F);
/* 127 */     putAll(t);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/* 137 */     return this.count;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 147 */     return (this.count == 0);
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
/*     */   public boolean containsValue(Object value) {
/* 160 */     Entry[] tab = this.table;
/*     */     
/* 162 */     if (value == null)
/* 163 */     { for (int i = tab.length; i-- > 0;) {
/* 164 */         for (Entry e = tab[i]; e != null; e = e.next)
/* 165 */         { if (e.value == null)
/* 166 */             return true;  } 
/*     */       }  }
/* 168 */     else { for (int i = tab.length; i-- > 0;) {
/* 169 */         for (Entry e = tab[i]; e != null; e = e.next) {
/* 170 */           if (value.equals(e.value))
/* 171 */             return true; 
/*     */         } 
/*     */       }  }
/* 174 */      return false;
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
/*     */   public boolean containsKey(Object key) {
/* 187 */     Entry[] tab = this.table;
/* 188 */     if (key != null)
/* 189 */     { int hash = System.identityHashCode(key);
/* 190 */       int index = (hash & Integer.MAX_VALUE) % tab.length;
/* 191 */       for (Entry e = tab[index]; e != null; e = e.next) {
/* 192 */         if (e.hash == hash && key == e.key)
/* 193 */           return true; 
/*     */       }  }
/* 195 */     else { for (Entry e = tab[0]; e != null; e = e.next) {
/* 196 */         if (e.key == null)
/* 197 */           return true; 
/*     */       }  }
/*     */     
/* 200 */     return false;
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
/*     */   public Object get(Object key) {
/* 216 */     Entry[] tab = this.table;
/*     */     
/* 218 */     if (key != null)
/* 219 */     { int hash = System.identityHashCode(key);
/* 220 */       int index = (hash & Integer.MAX_VALUE) % tab.length;
/* 221 */       for (Entry e = tab[index]; e != null; e = e.next) {
/* 222 */         if (e.hash == hash && key == e.key)
/* 223 */           return e.value; 
/*     */       }  }
/* 225 */     else { for (Entry e = tab[0]; e != null; e = e.next) {
/* 226 */         if (e.key == null)
/* 227 */           return e.value; 
/*     */       }  }
/*     */     
/* 230 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void rehash() {
/* 239 */     int oldCapacity = this.table.length;
/* 240 */     Entry[] oldMap = this.table;
/*     */     
/* 242 */     int newCapacity = oldCapacity * 2 + 1;
/* 243 */     Entry[] newMap = new Entry[newCapacity];
/*     */     
/* 245 */     this.modCount++;
/* 246 */     this.threshold = (int)(newCapacity * this.loadFactor);
/* 247 */     this.table = newMap;
/*     */     
/* 249 */     for (int i = oldCapacity; i-- > 0;) {
/* 250 */       for (Entry old = oldMap[i]; old != null; ) {
/* 251 */         Entry e = old;
/* 252 */         old = old.next;
/*     */         
/* 254 */         int index = (e.hash & Integer.MAX_VALUE) % newCapacity;
/* 255 */         e.next = newMap[index];
/* 256 */         newMap[index] = e;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object put(Object key, Object value) {
/* 276 */     Entry[] tab = this.table;
/* 277 */     int hash = 0;
/* 278 */     int index = 0;
/*     */     
/* 280 */     if (key != null) {
/* 281 */       hash = System.identityHashCode(key);
/* 282 */       index = (hash & Integer.MAX_VALUE) % tab.length;
/* 283 */       for (Entry entry = tab[index]; entry != null; entry = entry.next) {
/* 284 */         if (entry.hash == hash && key == entry.key) {
/* 285 */           Object old = entry.value;
/* 286 */           entry.value = value;
/* 287 */           return old;
/*     */         } 
/*     */       } 
/*     */     } else {
/* 291 */       for (Entry entry = tab[0]; entry != null; entry = entry.next) {
/* 292 */         if (entry.key == null) {
/* 293 */           Object old = entry.value;
/* 294 */           entry.value = value;
/* 295 */           return old;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 300 */     this.modCount++;
/* 301 */     if (this.count >= this.threshold) {
/*     */       
/* 303 */       rehash();
/*     */       
/* 305 */       tab = this.table;
/* 306 */       index = (hash & Integer.MAX_VALUE) % tab.length;
/*     */     } 
/*     */ 
/*     */     
/* 310 */     Entry e = new Entry(hash, key, value, tab[index]);
/* 311 */     tab[index] = e;
/* 312 */     this.count++;
/* 313 */     return null;
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
/*     */   public Object remove(Object key) {
/* 327 */     Entry[] tab = this.table;
/*     */     
/* 329 */     if (key != null) {
/* 330 */       int hash = System.identityHashCode(key);
/* 331 */       int index = (hash & Integer.MAX_VALUE) % tab.length;
/*     */       
/* 333 */       Entry e = tab[index], prev = null;
/* 334 */       for (; e != null; 
/* 335 */         prev = e, e = e.next) {
/* 336 */         if (e.hash == hash && key == e.key) {
/* 337 */           this.modCount++;
/* 338 */           if (prev != null) {
/* 339 */             prev.next = e.next;
/*     */           } else {
/* 341 */             tab[index] = e.next;
/*     */           } 
/* 343 */           this.count--;
/* 344 */           Object oldValue = e.value;
/* 345 */           e.value = null;
/* 346 */           return oldValue;
/*     */         } 
/*     */       } 
/*     */     } else {
/* 350 */       Entry e = tab[0], prev = null;
/* 351 */       for (; e != null; 
/* 352 */         prev = e, e = e.next) {
/* 353 */         if (e.key == null) {
/* 354 */           this.modCount++;
/* 355 */           if (prev != null) {
/* 356 */             prev.next = e.next;
/*     */           } else {
/* 358 */             tab[0] = e.next;
/*     */           } 
/* 360 */           this.count--;
/* 361 */           Object oldValue = e.value;
/* 362 */           e.value = null;
/* 363 */           return oldValue;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 368 */     return null;
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
/*     */   public void putAll(Map t) {
/* 381 */     Iterator<Map.Entry> i = t.entrySet().iterator();
/* 382 */     while (i.hasNext()) {
/* 383 */       Map.Entry e = i.next();
/* 384 */       put(e.getKey(), e.getValue());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/* 393 */     Entry[] tab = this.table;
/* 394 */     this.modCount++;
/* 395 */     for (int index = tab.length; --index >= 0;)
/* 396 */       tab[index] = null; 
/* 397 */     this.count = 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object clone() {
/*     */     try {
/* 409 */       IdentityHashMap t = (IdentityHashMap)super.clone();
/* 410 */       t.table = new Entry[this.table.length];
/* 411 */       for (int i = this.table.length; i-- > 0;) {
/* 412 */         t.table[i] = (this.table[i] != null) ? (Entry)this.table[i]
/* 413 */           .clone() : null;
/*     */       }
/* 415 */       t.keySet = null;
/* 416 */       t.entrySet = null;
/* 417 */       t.values = null;
/* 418 */       t.modCount = 0;
/* 419 */       return t;
/* 420 */     } catch (CloneNotSupportedException e) {
/*     */       
/* 422 */       throw new InternalError();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public IdentityHashMap(int initialCapacity, float loadFactor) {
/* 428 */     this.keySet = null;
/* 429 */     this.entrySet = null;
/* 430 */     this.values = null;
/*     */     if (initialCapacity < 0) {
/*     */       throw new IllegalArgumentException("Illegal Initial Capacity: " + initialCapacity);
/*     */     }
/*     */     if (loadFactor <= 0.0F || Float.isNaN(loadFactor)) {
/*     */       throw new IllegalArgumentException("Illegal Load factor: " + loadFactor);
/*     */     }
/*     */     if (initialCapacity == 0)
/*     */       initialCapacity = 1; 
/*     */     this.loadFactor = loadFactor;
/*     */     this.table = new Entry[initialCapacity];
/*     */     this.threshold = (int)(initialCapacity * loadFactor);
/*     */   }
/*     */   
/*     */   public Set keySet() {
/* 445 */     if (this.keySet == null) {
/* 446 */       this.keySet = new AbstractSet()
/*     */         {
/*     */           public Iterator iterator()
/*     */           {
/* 450 */             return IdentityHashMap.this.getHashIterator(0);
/*     */           }
/*     */           
/*     */           public int size() {
/* 454 */             return IdentityHashMap.this.count;
/*     */           }
/*     */           
/*     */           public boolean contains(Object o) {
/* 458 */             return IdentityHashMap.this.containsKey(o);
/*     */           }
/*     */           
/*     */           public boolean remove(Object o) {
/* 462 */             int oldSize = IdentityHashMap.this.count;
/* 463 */             IdentityHashMap.this.remove(o);
/* 464 */             return (IdentityHashMap.this.count != oldSize);
/*     */           }
/*     */           
/*     */           public void clear() {
/* 468 */             IdentityHashMap.this.clear();
/*     */           }
/*     */         };
/*     */     }
/* 472 */     return this.keySet;
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
/*     */   public Collection values() {
/* 488 */     if (this.values == null) {
/* 489 */       this.values = new AbstractCollection()
/*     */         {
/*     */           public Iterator iterator()
/*     */           {
/* 493 */             return IdentityHashMap.this.getHashIterator(1);
/*     */           }
/*     */           
/*     */           public int size() {
/* 497 */             return IdentityHashMap.this.count;
/*     */           }
/*     */           
/*     */           public boolean contains(Object o) {
/* 501 */             return IdentityHashMap.this.containsValue(o);
/*     */           }
/*     */           
/*     */           public void clear() {
/* 505 */             IdentityHashMap.this.clear();
/*     */           }
/*     */         };
/*     */     }
/* 509 */     return this.values;
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
/*     */   public Set entrySet() {
/* 527 */     if (this.entrySet == null) {
/* 528 */       this.entrySet = new AbstractSet()
/*     */         {
/*     */           public Iterator iterator()
/*     */           {
/* 532 */             return IdentityHashMap.this.getHashIterator(2);
/*     */           }
/*     */ 
/*     */           
/*     */           public boolean contains(Object o) {
/* 537 */             if (!(o instanceof Map.Entry))
/* 538 */               return false; 
/* 539 */             Map.Entry entry = (Map.Entry)o;
/* 540 */             Object key = entry.getKey();
/* 541 */             IdentityHashMap.Entry[] tab = IdentityHashMap.this.table;
/* 542 */             int hash = (key == null) ? 0 : System.identityHashCode(key);
/* 543 */             int index = (hash & Integer.MAX_VALUE) % tab.length;
/*     */             
/* 545 */             for (IdentityHashMap.Entry e = tab[index]; e != null; e = e.next) {
/* 546 */               if (e.hash == hash && e.equals(entry))
/* 547 */                 return true; 
/* 548 */             }  return false;
/*     */           }
/*     */ 
/*     */           
/*     */           public boolean remove(Object o) {
/* 553 */             if (!(o instanceof Map.Entry))
/* 554 */               return false; 
/* 555 */             Map.Entry entry = (Map.Entry)o;
/* 556 */             Object key = entry.getKey();
/* 557 */             IdentityHashMap.Entry[] tab = IdentityHashMap.this.table;
/* 558 */             int hash = (key == null) ? 0 : System.identityHashCode(key);
/* 559 */             int index = (hash & Integer.MAX_VALUE) % tab.length;
/*     */             
/* 561 */             IdentityHashMap.Entry e = tab[index], prev = null;
/* 562 */             for (; e != null; 
/* 563 */               prev = e, e = e.next) {
/* 564 */               if (e.hash == hash && e.equals(entry)) {
/* 565 */                 IdentityHashMap.this.modCount++;
/* 566 */                 if (prev != null) {
/* 567 */                   prev.next = e.next;
/*     */                 } else {
/* 569 */                   tab[index] = e.next;
/*     */                 } 
/* 571 */                 IdentityHashMap.this.count--;
/* 572 */                 e.value = null;
/* 573 */                 return true;
/*     */               } 
/*     */             } 
/* 576 */             return false;
/*     */           }
/*     */ 
/*     */           
/*     */           public int size() {
/* 581 */             return IdentityHashMap.this.count;
/*     */           }
/*     */ 
/*     */           
/*     */           public void clear() {
/* 586 */             IdentityHashMap.this.clear();
/*     */           }
/*     */         };
/*     */     }
/*     */     
/* 591 */     return this.entrySet;
/*     */   }
/*     */   
/*     */   private Iterator getHashIterator(int type) {
/* 595 */     if (this.count == 0) {
/* 596 */       return emptyHashIterator;
/*     */     }
/* 598 */     return new HashIterator(type);
/*     */   }
/*     */ 
/*     */   
/*     */   private static class Entry
/*     */     implements Map.Entry
/*     */   {
/*     */     int hash;
/*     */     
/*     */     Object key;
/*     */     Object value;
/*     */     Entry next;
/*     */     
/*     */     Entry(int hash, Object key, Object value, Entry next) {
/* 612 */       this.hash = hash;
/* 613 */       this.key = key;
/* 614 */       this.value = value;
/* 615 */       this.next = next;
/*     */     }
/*     */ 
/*     */     
/*     */     protected Object clone() {
/* 620 */       return new Entry(this.hash, this.key, this.value, (this.next == null) ? null : (Entry)this.next
/*     */ 
/*     */ 
/*     */           
/* 624 */           .clone());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Object getKey() {
/* 631 */       return this.key;
/*     */     }
/*     */ 
/*     */     
/*     */     public Object getValue() {
/* 636 */       return this.value;
/*     */     }
/*     */ 
/*     */     
/*     */     public Object setValue(Object value) {
/* 641 */       Object oldValue = this.value;
/* 642 */       this.value = value;
/* 643 */       return oldValue;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object o) {
/* 648 */       if (!(o instanceof Map.Entry))
/* 649 */         return false; 
/* 650 */       Map.Entry e = (Map.Entry)o;
/*     */       
/* 652 */       return (this.key == e.getKey() && ((this.value == null) ? (e
/*     */         
/* 654 */         .getValue() == null) : this.value
/* 655 */         .equals(e.getValue())));
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 660 */       return this.hash ^ ((this.value == null) ? 0 : this.value.hashCode());
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 665 */       return this.key + "=" + this.value;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 674 */   private static EmptyHashIterator emptyHashIterator = new EmptyHashIterator();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class EmptyHashIterator
/*     */     implements Iterator
/*     */   {
/*     */     public boolean hasNext() {
/* 685 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     public Object next() {
/* 690 */       throw new NoSuchElementException();
/*     */     }
/*     */ 
/*     */     
/*     */     public void remove() {
/* 695 */       throw new IllegalStateException();
/*     */     }
/*     */   }
/*     */   
/*     */   private class HashIterator
/*     */     implements Iterator {
/* 701 */     IdentityHashMap.Entry[] table = IdentityHashMap.this.table;
/* 702 */     int index = this.table.length;
/* 703 */     IdentityHashMap.Entry entry = null;
/* 704 */     IdentityHashMap.Entry lastReturned = null;
/*     */ 
/*     */ 
/*     */     
/*     */     int type;
/*     */ 
/*     */ 
/*     */     
/* 712 */     private int expectedModCount = IdentityHashMap.this.modCount;
/*     */     
/*     */     HashIterator(int type) {
/* 715 */       this.type = type;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean hasNext() {
/* 720 */       IdentityHashMap.Entry e = this.entry;
/* 721 */       int i = this.index;
/* 722 */       IdentityHashMap.Entry[] t = this.table;
/*     */       
/* 724 */       while (e == null && i > 0)
/* 725 */         e = t[--i]; 
/* 726 */       this.entry = e;
/* 727 */       this.index = i;
/* 728 */       return (e != null);
/*     */     }
/*     */ 
/*     */     
/*     */     public Object next() {
/* 733 */       if (IdentityHashMap.this.modCount != this.expectedModCount) {
/* 734 */         throw new ConcurrentModificationException();
/*     */       }
/* 736 */       IdentityHashMap.Entry et = this.entry;
/* 737 */       int i = this.index;
/* 738 */       IdentityHashMap.Entry[] t = this.table;
/*     */ 
/*     */       
/* 741 */       while (et == null && i > 0) {
/* 742 */         et = t[--i];
/*     */       }
/* 744 */       this.entry = et;
/* 745 */       this.index = i;
/* 746 */       if (et != null) {
/* 747 */         IdentityHashMap.Entry e = this.lastReturned = this.entry;
/* 748 */         this.entry = e.next;
/* 749 */         return (this.type == 0) ? e.key : ((this.type == 1) ? e.value : e);
/*     */       } 
/* 751 */       throw new NoSuchElementException();
/*     */     }
/*     */ 
/*     */     
/*     */     public void remove() {
/* 756 */       if (this.lastReturned == null)
/* 757 */         throw new IllegalStateException(); 
/* 758 */       if (IdentityHashMap.this.modCount != this.expectedModCount) {
/* 759 */         throw new ConcurrentModificationException();
/*     */       }
/* 761 */       IdentityHashMap.Entry[] tab = IdentityHashMap.this.table;
/* 762 */       int index = (this.lastReturned.hash & Integer.MAX_VALUE) % tab.length;
/*     */       
/* 764 */       IdentityHashMap.Entry e = tab[index], prev = null;
/* 765 */       for (; e != null; 
/* 766 */         prev = e, e = e.next) {
/* 767 */         if (e == this.lastReturned) {
/* 768 */           IdentityHashMap.this.modCount++;
/* 769 */           this.expectedModCount++;
/* 770 */           if (prev == null) {
/* 771 */             tab[index] = e.next;
/*     */           } else {
/* 773 */             prev.next = e.next;
/* 774 */           }  IdentityHashMap.this.count--;
/* 775 */           this.lastReturned = null;
/*     */           return;
/*     */         } 
/*     */       } 
/* 779 */       throw new ConcurrentModificationException();
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
/*     */   private void writeObject(ObjectOutputStream s) throws IOException {
/* 797 */     s.defaultWriteObject();
/*     */ 
/*     */     
/* 800 */     s.writeInt(this.table.length);
/*     */ 
/*     */     
/* 803 */     s.writeInt(this.count);
/*     */ 
/*     */     
/* 806 */     for (int index = this.table.length - 1; index >= 0; index--) {
/* 807 */       Entry entry = this.table[index];
/*     */       
/* 809 */       while (entry != null) {
/* 810 */         s.writeObject(entry.key);
/* 811 */         s.writeObject(entry.value);
/* 812 */         entry = entry.next;
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
/*     */   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
/* 824 */     s.defaultReadObject();
/*     */ 
/*     */     
/* 827 */     int numBuckets = s.readInt();
/* 828 */     this.table = new Entry[numBuckets];
/*     */ 
/*     */     
/* 831 */     int size = s.readInt();
/*     */ 
/*     */     
/* 834 */     for (int i = 0; i < size; i++) {
/* 835 */       Object key = s.readObject();
/* 836 */       Object value = s.readObject();
/* 837 */       put(key, value);
/*     */     } 
/*     */   }
/*     */   
/*     */   int capacity() {
/* 842 */     return this.table.length;
/*     */   }
/*     */   
/*     */   float loadFactor() {
/* 846 */     return this.loadFactor;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ex\\util\IdentityHashMap.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */