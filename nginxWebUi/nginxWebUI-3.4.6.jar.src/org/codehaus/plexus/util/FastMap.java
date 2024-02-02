/*     */ package org.codehaus.plexus.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.util.AbstractCollection;
/*     */ import java.util.AbstractSet;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
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
/*     */ public class FastMap
/*     */   implements Map, Cloneable, Serializable
/*     */ {
/*     */   private transient EntryImpl[] _entries;
/*     */   private transient int _capacity;
/*     */   private transient int _mask;
/*     */   private transient EntryImpl _poolFirst;
/*     */   private transient EntryImpl _mapFirst;
/*     */   private transient EntryImpl _mapLast;
/*     */   private transient int _size;
/*     */   private transient Values _values;
/*     */   private transient EntrySet _entrySet;
/*     */   private transient KeySet _keySet;
/*     */   
/*     */   public FastMap() {
/*  94 */     initialize(256);
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
/*     */   public FastMap(Map map) {
/* 110 */     int capacity = (map instanceof FastMap) ? ((FastMap)map).capacity() : map.size();
/*     */     
/* 112 */     initialize(capacity);
/* 113 */     putAll(map);
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
/*     */   public FastMap(int capacity) {
/* 126 */     initialize(capacity);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/* 135 */     return this._size;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int capacity() {
/* 146 */     return this._capacity;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 156 */     return (this._size == 0);
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
/* 169 */     EntryImpl entry = this._entries[keyHash(key) & this._mask];
/* 170 */     while (entry != null) {
/* 171 */       if (key.equals(entry._key)) {
/* 172 */         return true;
/*     */       }
/* 174 */       entry = entry._next;
/*     */     } 
/* 176 */     return false;
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
/* 189 */     EntryImpl entry = this._mapFirst;
/* 190 */     while (entry != null) {
/* 191 */       if (value.equals(entry._value)) {
/* 192 */         return true;
/*     */       }
/* 194 */       entry = entry._after;
/*     */     } 
/* 196 */     return false;
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
/*     */   public Object get(Object key) {
/* 208 */     EntryImpl entry = this._entries[keyHash(key) & this._mask];
/* 209 */     while (entry != null) {
/* 210 */       if (key.equals(entry._key)) {
/* 211 */         return entry._value;
/*     */       }
/* 213 */       entry = entry._next;
/*     */     } 
/* 215 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map.Entry getEntry(Object key) {
/* 225 */     EntryImpl entry = this._entries[keyHash(key) & this._mask];
/* 226 */     while (entry != null) {
/* 227 */       if (key.equals(entry._key)) {
/* 228 */         return entry;
/*     */       }
/* 230 */       entry = entry._next;
/*     */     } 
/* 232 */     return null;
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
/*     */   public Object put(Object key, Object value) {
/* 249 */     EntryImpl entry = this._entries[keyHash(key) & this._mask];
/* 250 */     while (entry != null) {
/* 251 */       if (key.equals(entry._key)) {
/* 252 */         Object prevValue = entry._value;
/* 253 */         entry._value = value;
/* 254 */         return prevValue;
/*     */       } 
/* 256 */       entry = entry._next;
/*     */     } 
/*     */     
/* 259 */     addEntry(key, value);
/* 260 */     return null;
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
/*     */   public void putAll(Map map) {
/* 272 */     for (Iterator i = map.entrySet().iterator(); i.hasNext(); ) {
/* 273 */       Map.Entry e = i.next();
/* 274 */       addEntry(e.getKey(), e.getValue());
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
/*     */   public Object remove(Object key) {
/* 289 */     EntryImpl entry = this._entries[keyHash(key) & this._mask];
/* 290 */     while (entry != null) {
/* 291 */       if (key.equals(entry._key)) {
/* 292 */         Object prevValue = entry._value;
/* 293 */         removeEntry(entry);
/* 294 */         return prevValue;
/*     */       } 
/* 296 */       entry = entry._next;
/*     */     } 
/* 298 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/* 306 */     for (EntryImpl entry = this._mapFirst; entry != null; entry = entry._after) {
/* 307 */       entry._key = null;
/* 308 */       entry._value = null;
/* 309 */       entry._before = null;
/* 310 */       entry._next = null;
/* 311 */       if (entry._previous == null) {
/* 312 */         this._entries[entry._index] = null;
/*     */       } else {
/* 314 */         entry._previous = null;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 319 */     if (this._mapLast != null) {
/* 320 */       this._mapLast._after = this._poolFirst;
/* 321 */       this._poolFirst = this._mapFirst;
/* 322 */       this._mapFirst = null;
/* 323 */       this._mapLast = null;
/* 324 */       this._size = 0;
/* 325 */       sizeChanged();
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
/*     */   public void setCapacity(int newCapacity) {
/* 339 */     if (newCapacity > this._capacity) {
/* 340 */       for (int i = this._capacity; i < newCapacity; i++) {
/* 341 */         EntryImpl entry = new EntryImpl();
/* 342 */         entry._after = this._poolFirst;
/* 343 */         this._poolFirst = entry;
/*     */       } 
/* 345 */     } else if (newCapacity < this._capacity) {
/* 346 */       int i = newCapacity;
/* 347 */       for (; i < this._capacity && this._poolFirst != null; i++) {
/*     */         
/* 349 */         EntryImpl entry = this._poolFirst;
/* 350 */         this._poolFirst = entry._after;
/* 351 */         entry._after = null;
/*     */       } 
/*     */     } 
/*     */     
/* 355 */     int tableLength = 16;
/* 356 */     while (tableLength < newCapacity) {
/* 357 */       tableLength <<= 1;
/*     */     }
/*     */     
/* 360 */     if (this._entries.length != tableLength) {
/* 361 */       this._entries = new EntryImpl[tableLength];
/* 362 */       this._mask = tableLength - 1;
/*     */ 
/*     */       
/* 365 */       EntryImpl entry = this._mapFirst;
/* 366 */       while (entry != null) {
/* 367 */         int index = keyHash(entry._key) & this._mask;
/* 368 */         entry._index = index;
/*     */ 
/*     */         
/* 371 */         entry._previous = null;
/* 372 */         EntryImpl next = this._entries[index];
/* 373 */         entry._next = next;
/* 374 */         if (next != null) {
/* 375 */           next._previous = entry;
/*     */         }
/* 377 */         this._entries[index] = entry;
/*     */         
/* 379 */         entry = entry._after;
/*     */       } 
/*     */     } 
/* 382 */     this._capacity = newCapacity;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object clone() {
/*     */     try {
/* 393 */       FastMap clone = (FastMap)super.clone();
/* 394 */       clone.initialize(this._capacity);
/* 395 */       clone.putAll(this);
/* 396 */       return clone;
/* 397 */     } catch (CloneNotSupportedException e) {
/*     */       
/* 399 */       throw new InternalError();
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
/*     */   public boolean equals(Object obj) {
/* 414 */     if (obj == this)
/* 415 */       return true; 
/* 416 */     if (obj instanceof Map) {
/* 417 */       Map that = (Map)obj;
/* 418 */       if (size() == that.size()) {
/* 419 */         EntryImpl entry = this._mapFirst;
/* 420 */         while (entry != null) {
/* 421 */           if (!that.entrySet().contains(entry)) {
/* 422 */             return false;
/*     */           }
/* 424 */           entry = entry._after;
/*     */         } 
/* 426 */         return true;
/*     */       } 
/* 428 */       return false;
/*     */     } 
/*     */     
/* 431 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 441 */     int code = 0;
/* 442 */     EntryImpl entry = this._mapFirst;
/* 443 */     while (entry != null) {
/* 444 */       code += entry.hashCode();
/* 445 */       entry = entry._after;
/*     */     } 
/* 447 */     return code;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 456 */     return entrySet().toString();
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
/*     */   public Collection values() {
/* 473 */     return this._values;
/*     */   }
/*     */   private class Values extends AbstractCollection { private Values(FastMap this$0) {
/* 476 */       FastMap.this = FastMap.this;
/*     */     } private final FastMap this$0; public Iterator iterator() {
/* 478 */       return new Iterator(this) { FastMap.EntryImpl after; FastMap.EntryImpl before;
/*     */           private final FastMap.Values this$1;
/*     */           
/*     */           public void remove() {
/* 482 */             FastMap.Values.access$800(this.this$1).removeEntry(this.before);
/*     */           }
/*     */           public boolean hasNext() {
/* 485 */             return (this.after != null);
/*     */           }
/*     */           public Object next() {
/* 488 */             this.before = this.after;
/* 489 */             this.after = this.after._after;
/* 490 */             return this.before._value;
/*     */           } }
/*     */         ;
/*     */     }
/*     */     public int size() {
/* 495 */       return FastMap.this._size;
/*     */     }
/*     */     public boolean contains(Object o) {
/* 498 */       return FastMap.this.containsValue(o);
/*     */     }
/*     */     public void clear() {
/* 501 */       FastMap.this.clear();
/*     */     } }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/* 520 */     return this._entrySet;
/*     */   } private class EntrySet extends AbstractSet { private final FastMap this$0;
/*     */     private EntrySet(FastMap this$0) {
/* 523 */       FastMap.this = FastMap.this;
/*     */     } public Iterator iterator() {
/* 525 */       return (Iterator)new Object(this);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int size() {
/* 542 */       return FastMap.this._size;
/*     */     }
/*     */     public boolean contains(Object obj) {
/* 545 */       if (obj instanceof Map.Entry) {
/* 546 */         Map.Entry entry = (Map.Entry)obj;
/* 547 */         Map.Entry mapEntry = FastMap.this.getEntry(entry.getKey());
/* 548 */         return entry.equals(mapEntry);
/*     */       } 
/* 550 */       return false;
/*     */     }
/*     */     
/*     */     public boolean remove(Object obj) {
/* 554 */       if (obj instanceof Map.Entry) {
/* 555 */         Map.Entry entry = (Map.Entry)obj;
/* 556 */         FastMap.EntryImpl mapEntry = (FastMap.EntryImpl)FastMap.this.getEntry(entry.getKey());
/* 557 */         if (mapEntry != null && entry.getValue().equals(mapEntry._value)) {
/*     */           
/* 559 */           FastMap.this.removeEntry(mapEntry);
/* 560 */           return true;
/*     */         } 
/*     */       } 
/* 563 */       return false;
/*     */     } }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set keySet() {
/* 580 */     return this._keySet;
/*     */   } private class KeySet extends AbstractSet { private final FastMap this$0;
/*     */     private KeySet(FastMap this$0) {
/* 583 */       FastMap.this = FastMap.this;
/*     */     } public Iterator iterator() {
/* 585 */       return (Iterator)new Object(this);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int size() {
/* 602 */       return FastMap.this._size;
/*     */     }
/*     */     public boolean contains(Object obj) {
/* 605 */       return FastMap.this.containsKey(obj);
/*     */     }
/*     */     public boolean remove(Object obj) {
/* 608 */       return (FastMap.this.remove(obj) != null);
/*     */     }
/*     */     public void clear() {
/* 611 */       FastMap.this.clear();
/*     */     } }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void sizeChanged() {
/* 629 */     if (size() > capacity()) {
/* 630 */       setCapacity(capacity() * 2);
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
/*     */   private static int keyHash(Object key) {
/* 644 */     int hashCode = key.hashCode();
/* 645 */     hashCode += hashCode << 9 ^ 0xFFFFFFFF;
/* 646 */     hashCode ^= hashCode >>> 14;
/* 647 */     hashCode += hashCode << 4;
/* 648 */     hashCode ^= hashCode >>> 10;
/* 649 */     return hashCode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void addEntry(Object key, Object value) {
/* 658 */     EntryImpl entry = this._poolFirst;
/* 659 */     if (entry != null) {
/* 660 */       this._poolFirst = entry._after;
/* 661 */       entry._after = null;
/*     */     } else {
/* 663 */       entry = new EntryImpl();
/*     */     } 
/*     */ 
/*     */     
/* 667 */     entry._key = key;
/* 668 */     entry._value = value;
/* 669 */     int index = keyHash(key) & this._mask;
/* 670 */     entry._index = index;
/*     */ 
/*     */     
/* 673 */     EntryImpl next = this._entries[index];
/* 674 */     entry._next = next;
/* 675 */     if (next != null) {
/* 676 */       next._previous = entry;
/*     */     }
/* 678 */     this._entries[index] = entry;
/*     */ 
/*     */     
/* 681 */     if (this._mapLast != null) {
/* 682 */       entry._before = this._mapLast;
/* 683 */       this._mapLast._after = entry;
/*     */     } else {
/* 685 */       this._mapFirst = entry;
/*     */     } 
/* 687 */     this._mapLast = entry;
/*     */ 
/*     */     
/* 690 */     this._size++;
/* 691 */     sizeChanged();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void removeEntry(EntryImpl entry) {
/* 702 */     EntryImpl previous = entry._previous;
/* 703 */     EntryImpl next = entry._next;
/* 704 */     if (previous != null) {
/* 705 */       previous._next = next;
/* 706 */       entry._previous = null;
/*     */     } else {
/* 708 */       this._entries[entry._index] = next;
/*     */     } 
/* 710 */     if (next != null) {
/* 711 */       next._previous = previous;
/* 712 */       entry._next = null;
/*     */     } 
/*     */ 
/*     */     
/* 716 */     EntryImpl before = entry._before;
/* 717 */     EntryImpl after = entry._after;
/* 718 */     if (before != null) {
/* 719 */       before._after = after;
/* 720 */       entry._before = null;
/*     */     } else {
/* 722 */       this._mapFirst = after;
/*     */     } 
/* 724 */     if (after != null) {
/* 725 */       after._before = before;
/*     */     } else {
/* 727 */       this._mapLast = before;
/*     */     } 
/*     */ 
/*     */     
/* 731 */     entry._key = null;
/* 732 */     entry._value = null;
/*     */ 
/*     */     
/* 735 */     entry._after = this._poolFirst;
/* 736 */     this._poolFirst = entry;
/*     */ 
/*     */     
/* 739 */     this._size--;
/* 740 */     sizeChanged();
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
/*     */   private void initialize(int capacity) {
/* 752 */     int tableLength = 16;
/* 753 */     while (tableLength < capacity) {
/* 754 */       tableLength <<= 1;
/*     */     }
/*     */     
/* 757 */     this._entries = new EntryImpl[tableLength];
/* 758 */     this._mask = tableLength - 1;
/* 759 */     this._capacity = capacity;
/* 760 */     this._size = 0;
/*     */     
/* 762 */     this._values = new Values();
/* 763 */     this._entrySet = new EntrySet();
/* 764 */     this._keySet = new KeySet();
/*     */     
/* 766 */     this._poolFirst = null;
/* 767 */     this._mapFirst = null;
/* 768 */     this._mapLast = null;
/*     */     
/* 770 */     for (int i = 0; i < capacity; i++) {
/* 771 */       EntryImpl entry = new EntryImpl();
/* 772 */       entry._after = this._poolFirst;
/* 773 */       this._poolFirst = entry;
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
/*     */   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
/* 787 */     int capacity = stream.readInt();
/* 788 */     initialize(capacity);
/* 789 */     int size = stream.readInt();
/* 790 */     for (int i = 0; i < size; i++) {
/* 791 */       Object key = stream.readObject();
/* 792 */       Object value = stream.readObject();
/* 793 */       addEntry(key, value);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void writeObject(ObjectOutputStream stream) throws IOException {
/* 804 */     stream.writeInt(this._capacity);
/* 805 */     stream.writeInt(this._size);
/* 806 */     int count = 0;
/* 807 */     EntryImpl entry = this._mapFirst;
/* 808 */     while (entry != null) {
/* 809 */       stream.writeObject(entry._key);
/* 810 */       stream.writeObject(entry._value);
/* 811 */       count++;
/* 812 */       entry = entry._after;
/*     */     } 
/* 814 */     if (count != this._size) {
/* 815 */       throw new IOException("FastMap Corrupted");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class EntryImpl
/*     */     implements Map.Entry
/*     */   {
/*     */     private Object _key;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private Object _value;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private int _index;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private EntryImpl _previous;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private EntryImpl _next;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private EntryImpl _before;
/*     */ 
/*     */ 
/*     */     
/*     */     private EntryImpl _after;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private EntryImpl() {}
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Object getKey() {
/* 866 */       return this._key;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Object getValue() {
/* 875 */       return this._value;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Object setValue(Object value) {
/* 885 */       Object old = this._value;
/* 886 */       this._value = value;
/* 887 */       return old;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean equals(Object that) {
/* 899 */       if (that instanceof Map.Entry) {
/* 900 */         Map.Entry entry = (Map.Entry)that;
/* 901 */         return (this._key.equals(entry.getKey()) && ((this._value != null) ? this._value.equals(entry.getValue()) : (entry.getValue() == null)));
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 906 */       return false;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 916 */       return this._key.hashCode() ^ ((this._value != null) ? this._value.hashCode() : 0);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String toString() {
/* 925 */       return this._key + "=" + this._value;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\FastMap.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */