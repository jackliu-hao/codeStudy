/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.util.AbstractMap;
/*     */ import java.util.AbstractSet;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Set;
/*     */ import java.util.SortedMap;
/*     */ import java.util.TreeMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class SmallSortedMap<K extends Comparable<K>, V>
/*     */   extends AbstractMap<K, V>
/*     */ {
/*     */   private final int maxArraySize;
/*     */   private List<Entry> entryList;
/*     */   private Map<K, V> overflowEntries;
/*     */   private boolean isImmutable;
/*     */   private volatile EntrySet lazyEntrySet;
/*     */   private Map<K, V> overflowEntriesDescending;
/*     */   private volatile DescendingEntrySet lazyDescendingEntrySet;
/*     */   
/*     */   static <FieldDescriptorType extends FieldSet.FieldDescriptorLite<FieldDescriptorType>> SmallSortedMap<FieldDescriptorType, Object> newFieldMap(int arraySize) {
/*  95 */     return new SmallSortedMap<FieldDescriptorType, Object>(arraySize)
/*     */       {
/*     */         public void makeImmutable()
/*     */         {
/*  99 */           if (!isImmutable()) {
/* 100 */             for (int i = 0; i < getNumArrayEntries(); i++) {
/* 101 */               Map.Entry<FieldDescriptorType, Object> entry = getArrayEntryAt(i);
/* 102 */               if (((FieldSet.FieldDescriptorLite)entry.getKey()).isRepeated()) {
/* 103 */                 List<?> value = (List)entry.getValue();
/* 104 */                 entry.setValue(Collections.unmodifiableList(value));
/*     */               } 
/*     */             } 
/* 107 */             for (Map.Entry<FieldDescriptorType, Object> entry : getOverflowEntries()) {
/* 108 */               if (((FieldSet.FieldDescriptorLite)entry.getKey()).isRepeated()) {
/* 109 */                 List<?> value = (List)entry.getValue();
/* 110 */                 entry.setValue(Collections.unmodifiableList(value));
/*     */               } 
/*     */             } 
/*     */           } 
/* 114 */           super.makeImmutable();
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
/*     */   static <K extends Comparable<K>, V> SmallSortedMap<K, V> newInstanceForTest(int arraySize) {
/* 126 */     return new SmallSortedMap<>(arraySize);
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
/*     */   private SmallSortedMap(int arraySize) {
/* 147 */     this.maxArraySize = arraySize;
/* 148 */     this.entryList = Collections.emptyList();
/* 149 */     this.overflowEntries = Collections.emptyMap();
/* 150 */     this.overflowEntriesDescending = Collections.emptyMap();
/*     */   }
/*     */ 
/*     */   
/*     */   public void makeImmutable() {
/* 155 */     if (!this.isImmutable) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 160 */       this
/*     */ 
/*     */         
/* 163 */         .overflowEntries = this.overflowEntries.isEmpty() ? Collections.<K, V>emptyMap() : Collections.<K, V>unmodifiableMap(this.overflowEntries);
/* 164 */       this
/*     */ 
/*     */         
/* 167 */         .overflowEntriesDescending = this.overflowEntriesDescending.isEmpty() ? Collections.<K, V>emptyMap() : Collections.<K, V>unmodifiableMap(this.overflowEntriesDescending);
/* 168 */       this.isImmutable = true;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isImmutable() {
/* 174 */     return this.isImmutable;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getNumArrayEntries() {
/* 179 */     return this.entryList.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public Map.Entry<K, V> getArrayEntryAt(int index) {
/* 184 */     return this.entryList.get(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getNumOverflowEntries() {
/* 189 */     return this.overflowEntries.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterable<Map.Entry<K, V>> getOverflowEntries() {
/* 194 */     return this.overflowEntries.isEmpty() ? 
/* 195 */       EmptySet.<Map.Entry<K, V>>iterable() : this.overflowEntries
/* 196 */       .entrySet();
/*     */   }
/*     */   
/*     */   Iterable<Map.Entry<K, V>> getOverflowEntriesDescending() {
/* 200 */     return this.overflowEntriesDescending.isEmpty() ? 
/* 201 */       EmptySet.<Map.Entry<K, V>>iterable() : this.overflowEntriesDescending
/* 202 */       .entrySet();
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/* 207 */     return this.entryList.size() + this.overflowEntries.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean containsKey(Object o) {
/* 218 */     Comparable comparable = (Comparable)o;
/* 219 */     return (binarySearchInArray((K)comparable) >= 0 || this.overflowEntries.containsKey(comparable));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public V get(Object o) {
/* 230 */     Comparable comparable = (Comparable)o;
/* 231 */     int index = binarySearchInArray((K)comparable);
/* 232 */     if (index >= 0) {
/* 233 */       return ((Entry)this.entryList.get(index)).getValue();
/*     */     }
/* 235 */     return this.overflowEntries.get(comparable);
/*     */   }
/*     */ 
/*     */   
/*     */   public V put(K key, V value) {
/* 240 */     checkMutable();
/* 241 */     int index = binarySearchInArray(key);
/* 242 */     if (index >= 0)
/*     */     {
/* 244 */       return ((Entry)this.entryList.get(index)).setValue(value);
/*     */     }
/* 246 */     ensureEntryArrayMutable();
/* 247 */     int insertionPoint = -(index + 1);
/* 248 */     if (insertionPoint >= this.maxArraySize)
/*     */     {
/* 250 */       return getOverflowEntriesMutable().put(key, value);
/*     */     }
/*     */     
/* 253 */     if (this.entryList.size() == this.maxArraySize) {
/*     */       
/* 255 */       Entry lastEntryInArray = this.entryList.remove(this.maxArraySize - 1);
/* 256 */       getOverflowEntriesMutable().put(lastEntryInArray.getKey(), lastEntryInArray.getValue());
/*     */     } 
/* 258 */     this.entryList.add(insertionPoint, new Entry(key, value));
/* 259 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/* 264 */     checkMutable();
/* 265 */     if (!this.entryList.isEmpty()) {
/* 266 */       this.entryList.clear();
/*     */     }
/* 268 */     if (!this.overflowEntries.isEmpty()) {
/* 269 */       this.overflowEntries.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public V remove(Object o) {
/* 280 */     checkMutable();
/*     */     
/* 282 */     Comparable comparable = (Comparable)o;
/* 283 */     int index = binarySearchInArray((K)comparable);
/* 284 */     if (index >= 0) {
/* 285 */       return removeArrayEntryAt(index);
/*     */     }
/*     */ 
/*     */     
/* 289 */     if (this.overflowEntries.isEmpty()) {
/* 290 */       return null;
/*     */     }
/* 292 */     return this.overflowEntries.remove(comparable);
/*     */   }
/*     */ 
/*     */   
/*     */   private V removeArrayEntryAt(int index) {
/* 297 */     checkMutable();
/* 298 */     V removed = ((Entry)this.entryList.remove(index)).getValue();
/* 299 */     if (!this.overflowEntries.isEmpty()) {
/*     */ 
/*     */       
/* 302 */       Iterator<Map.Entry<K, V>> iterator = getOverflowEntriesMutable().entrySet().iterator();
/* 303 */       this.entryList.add(new Entry(iterator.next()));
/* 304 */       iterator.remove();
/*     */     } 
/* 306 */     return removed;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int binarySearchInArray(K key) {
/* 315 */     int left = 0;
/* 316 */     int right = this.entryList.size() - 1;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 321 */     if (right >= 0) {
/* 322 */       int cmp = key.compareTo(((Entry)this.entryList.get(right)).getKey());
/* 323 */       if (cmp > 0)
/* 324 */         return -(right + 2); 
/* 325 */       if (cmp == 0) {
/* 326 */         return right;
/*     */       }
/*     */     } 
/*     */     
/* 330 */     while (left <= right) {
/* 331 */       int mid = (left + right) / 2;
/* 332 */       int cmp = key.compareTo(((Entry)this.entryList.get(mid)).getKey());
/* 333 */       if (cmp < 0) {
/* 334 */         right = mid - 1; continue;
/* 335 */       }  if (cmp > 0) {
/* 336 */         left = mid + 1; continue;
/*     */       } 
/* 338 */       return mid;
/*     */     } 
/*     */     
/* 341 */     return -(left + 1);
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
/*     */   public Set<Map.Entry<K, V>> entrySet() {
/* 353 */     if (this.lazyEntrySet == null) {
/* 354 */       this.lazyEntrySet = new EntrySet();
/*     */     }
/* 356 */     return this.lazyEntrySet;
/*     */   }
/*     */   
/*     */   Set<Map.Entry<K, V>> descendingEntrySet() {
/* 360 */     if (this.lazyDescendingEntrySet == null) {
/* 361 */       this.lazyDescendingEntrySet = new DescendingEntrySet();
/*     */     }
/* 363 */     return this.lazyDescendingEntrySet;
/*     */   }
/*     */ 
/*     */   
/*     */   private void checkMutable() {
/* 368 */     if (this.isImmutable) {
/* 369 */       throw new UnsupportedOperationException();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private SortedMap<K, V> getOverflowEntriesMutable() {
/* 379 */     checkMutable();
/* 380 */     if (this.overflowEntries.isEmpty() && !(this.overflowEntries instanceof TreeMap)) {
/* 381 */       this.overflowEntries = new TreeMap<>();
/* 382 */       this.overflowEntriesDescending = ((TreeMap<K, V>)this.overflowEntries).descendingMap();
/*     */     } 
/* 384 */     return (SortedMap<K, V>)this.overflowEntries;
/*     */   }
/*     */ 
/*     */   
/*     */   private void ensureEntryArrayMutable() {
/* 389 */     checkMutable();
/* 390 */     if (this.entryList.isEmpty() && !(this.entryList instanceof ArrayList)) {
/* 391 */       this.entryList = new ArrayList<>(this.maxArraySize);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private class Entry
/*     */     implements Map.Entry<K, V>, Comparable<Entry>
/*     */   {
/*     */     private final K key;
/*     */     
/*     */     private V value;
/*     */ 
/*     */     
/*     */     Entry(Map.Entry<K, V> copy) {
/* 405 */       this(copy.getKey(), copy.getValue());
/*     */     }
/*     */     
/*     */     Entry(K key, V value) {
/* 409 */       this.key = key;
/* 410 */       this.value = value;
/*     */     }
/*     */ 
/*     */     
/*     */     public K getKey() {
/* 415 */       return this.key;
/*     */     }
/*     */ 
/*     */     
/*     */     public V getValue() {
/* 420 */       return this.value;
/*     */     }
/*     */ 
/*     */     
/*     */     public int compareTo(Entry other) {
/* 425 */       return getKey().compareTo(other.getKey());
/*     */     }
/*     */ 
/*     */     
/*     */     public V setValue(V newValue) {
/* 430 */       SmallSortedMap.this.checkMutable();
/* 431 */       V oldValue = this.value;
/* 432 */       this.value = newValue;
/* 433 */       return oldValue;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object o) {
/* 438 */       if (o == this) {
/* 439 */         return true;
/*     */       }
/* 441 */       if (!(o instanceof Map.Entry)) {
/* 442 */         return false;
/*     */       }
/*     */       
/* 445 */       Map.Entry<?, ?> other = (Map.Entry<?, ?>)o;
/* 446 */       return (equals(this.key, other.getKey()) && equals(this.value, other.getValue()));
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 451 */       return ((this.key == null) ? 0 : this.key.hashCode()) ^ ((this.value == null) ? 0 : this.value.hashCode());
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 456 */       return (new StringBuilder()).append(this.key).append("=").append(this.value).toString();
/*     */     }
/*     */ 
/*     */     
/*     */     private boolean equals(Object o1, Object o2) {
/* 461 */       return (o1 == null) ? ((o2 == null)) : o1.equals(o2);
/*     */     }
/*     */   }
/*     */   
/*     */   private class EntrySet
/*     */     extends AbstractSet<Map.Entry<K, V>> {
/*     */     private EntrySet() {}
/*     */     
/*     */     public Iterator<Map.Entry<K, V>> iterator() {
/* 470 */       return new SmallSortedMap.EntryIterator();
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() {
/* 475 */       return SmallSortedMap.this.size();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean contains(Object o) {
/* 486 */       Map.Entry<K, V> entry = (Map.Entry<K, V>)o;
/* 487 */       V existing = (V)SmallSortedMap.this.get(entry.getKey());
/* 488 */       V value = entry.getValue();
/* 489 */       return (existing == value || (existing != null && existing.equals(value)));
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean add(Map.Entry<K, V> entry) {
/* 494 */       if (!contains(entry)) {
/* 495 */         SmallSortedMap.this.put((Comparable)entry.getKey(), entry.getValue());
/* 496 */         return true;
/*     */       } 
/* 498 */       return false;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean remove(Object o) {
/* 509 */       Map.Entry<K, V> entry = (Map.Entry<K, V>)o;
/* 510 */       if (contains(entry)) {
/* 511 */         SmallSortedMap.this.remove(entry.getKey());
/* 512 */         return true;
/*     */       } 
/* 514 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     public void clear() {
/* 519 */       SmallSortedMap.this.clear();
/*     */     } }
/*     */   
/*     */   private class DescendingEntrySet extends EntrySet {
/*     */     private DescendingEntrySet() {}
/*     */     
/*     */     public Iterator<Map.Entry<K, V>> iterator() {
/* 526 */       return new SmallSortedMap.DescendingEntryIterator();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private class EntryIterator
/*     */     implements Iterator<Map.Entry<K, V>>
/*     */   {
/* 536 */     private int pos = -1;
/*     */     
/*     */     private boolean nextCalledBeforeRemove;
/*     */     private Iterator<Map.Entry<K, V>> lazyOverflowIterator;
/*     */     
/*     */     public boolean hasNext() {
/* 542 */       return (this.pos + 1 < SmallSortedMap.access$600(SmallSortedMap.this).size() || (
/* 543 */         !SmallSortedMap.this.overflowEntries.isEmpty() && getOverflowIterator().hasNext()));
/*     */     }
/*     */ 
/*     */     
/*     */     public Map.Entry<K, V> next() {
/* 548 */       this.nextCalledBeforeRemove = true;
/*     */ 
/*     */       
/* 551 */       if (++this.pos < SmallSortedMap.access$600(SmallSortedMap.this).size()) {
/* 552 */         return SmallSortedMap.access$600(SmallSortedMap.this).get(this.pos);
/*     */       }
/* 554 */       return getOverflowIterator().next();
/*     */     }
/*     */ 
/*     */     
/*     */     public void remove() {
/* 559 */       if (!this.nextCalledBeforeRemove) {
/* 560 */         throw new IllegalStateException("remove() was called before next()");
/*     */       }
/* 562 */       this.nextCalledBeforeRemove = false;
/* 563 */       SmallSortedMap.this.checkMutable();
/*     */       
/* 565 */       if (this.pos < SmallSortedMap.access$600(SmallSortedMap.this).size()) {
/* 566 */         SmallSortedMap.this.removeArrayEntryAt(this.pos--);
/*     */       } else {
/* 568 */         getOverflowIterator().remove();
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private Iterator<Map.Entry<K, V>> getOverflowIterator() {
/* 578 */       if (this.lazyOverflowIterator == null) {
/* 579 */         this.lazyOverflowIterator = SmallSortedMap.this.overflowEntries.entrySet().iterator();
/*     */       }
/* 581 */       return this.lazyOverflowIterator;
/*     */     }
/*     */ 
/*     */     
/*     */     private EntryIterator() {}
/*     */   }
/*     */   
/*     */   private class DescendingEntryIterator
/*     */     implements Iterator<Map.Entry<K, V>>
/*     */   {
/* 591 */     private int pos = SmallSortedMap.access$600(SmallSortedMap.this).size();
/*     */     
/*     */     private Iterator<Map.Entry<K, V>> lazyOverflowIterator;
/*     */     
/*     */     public boolean hasNext() {
/* 596 */       return ((this.pos > 0 && this.pos <= SmallSortedMap.access$600(SmallSortedMap.this).size()) || getOverflowIterator().hasNext());
/*     */     }
/*     */ 
/*     */     
/*     */     public Map.Entry<K, V> next() {
/* 601 */       if (getOverflowIterator().hasNext()) {
/* 602 */         return getOverflowIterator().next();
/*     */       }
/* 604 */       return SmallSortedMap.access$600(SmallSortedMap.this).get(--this.pos);
/*     */     }
/*     */ 
/*     */     
/*     */     public void remove() {
/* 609 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private Iterator<Map.Entry<K, V>> getOverflowIterator() {
/* 618 */       if (this.lazyOverflowIterator == null) {
/* 619 */         this.lazyOverflowIterator = SmallSortedMap.this.overflowEntriesDescending.entrySet().iterator();
/*     */       }
/* 621 */       return this.lazyOverflowIterator;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private DescendingEntryIterator() {}
/*     */   }
/*     */ 
/*     */   
/*     */   private static class EmptySet
/*     */   {
/* 632 */     private static final Iterator<Object> ITERATOR = new Iterator()
/*     */       {
/*     */         public boolean hasNext()
/*     */         {
/* 636 */           return false;
/*     */         }
/*     */ 
/*     */         
/*     */         public Object next() {
/* 641 */           throw new NoSuchElementException();
/*     */         }
/*     */ 
/*     */         
/*     */         public void remove() {
/* 646 */           throw new UnsupportedOperationException();
/*     */         }
/*     */       };
/*     */     
/* 650 */     private static final Iterable<Object> ITERABLE = new Iterable()
/*     */       {
/*     */         public Iterator<Object> iterator()
/*     */         {
/* 654 */           return SmallSortedMap.EmptySet.ITERATOR;
/*     */         }
/*     */       };
/*     */ 
/*     */     
/*     */     static <T> Iterable<T> iterable() {
/* 660 */       return (Iterable)ITERABLE;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 666 */     if (this == o) {
/* 667 */       return true;
/*     */     }
/*     */     
/* 670 */     if (!(o instanceof SmallSortedMap)) {
/* 671 */       return super.equals(o);
/*     */     }
/*     */     
/* 674 */     SmallSortedMap<?, ?> other = (SmallSortedMap<?, ?>)o;
/* 675 */     int size = size();
/* 676 */     if (size != other.size()) {
/* 677 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 681 */     int numArrayEntries = getNumArrayEntries();
/* 682 */     if (numArrayEntries != other.getNumArrayEntries()) {
/* 683 */       return entrySet().equals(other.entrySet());
/*     */     }
/*     */     
/* 686 */     for (int i = 0; i < numArrayEntries; i++) {
/* 687 */       if (!getArrayEntryAt(i).equals(other.getArrayEntryAt(i))) {
/* 688 */         return false;
/*     */       }
/*     */     } 
/*     */     
/* 692 */     if (numArrayEntries != size) {
/* 693 */       return this.overflowEntries.equals(other.overflowEntries);
/*     */     }
/*     */     
/* 696 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 701 */     int h = 0;
/* 702 */     int listSize = getNumArrayEntries();
/* 703 */     for (int i = 0; i < listSize; i++) {
/* 704 */       h += ((Entry)this.entryList.get(i)).hashCode();
/*     */     }
/*     */     
/* 707 */     if (getNumOverflowEntries() > 0) {
/* 708 */       h += this.overflowEntries.hashCode();
/*     */     }
/* 710 */     return h;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\SmallSortedMap.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */