/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MapField<K, V>
/*     */   implements MutabilityOracle
/*     */ {
/*     */   private volatile boolean isMutable;
/*     */   private volatile StorageMode mode;
/*     */   private MutatabilityAwareMap<K, V> mapData;
/*     */   private List<Message> listData;
/*     */   private final Converter<K, V> converter;
/*     */   
/*     */   private enum StorageMode
/*     */   {
/*  81 */     MAP,
/*  82 */     LIST,
/*  83 */     BOTH;
/*     */   }
/*     */ 
/*     */   
/*     */   private static interface Converter<K, V>
/*     */   {
/*     */     Message convertKeyAndValueToMessage(K param1K, V param1V);
/*     */ 
/*     */     
/*     */     void convertMessageToKeyAndValue(Message param1Message, Map<K, V> param1Map);
/*     */ 
/*     */     
/*     */     Message getMessageDefaultInstance();
/*     */   }
/*     */   
/*     */   private static class ImmutableMessageConverter<K, V>
/*     */     implements Converter<K, V>
/*     */   {
/*     */     private final MapEntry<K, V> defaultEntry;
/*     */     
/*     */     public ImmutableMessageConverter(MapEntry<K, V> defaultEntry) {
/* 104 */       this.defaultEntry = defaultEntry;
/*     */     }
/*     */ 
/*     */     
/*     */     public Message convertKeyAndValueToMessage(K key, V value) {
/* 109 */       return this.defaultEntry.newBuilderForType().setKey(key).setValue(value).buildPartial();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void convertMessageToKeyAndValue(Message message, Map<K, V> map) {
/* 115 */       MapEntry<K, V> entry = (MapEntry<K, V>)message;
/* 116 */       map.put(entry.getKey(), entry.getValue());
/*     */     }
/*     */ 
/*     */     
/*     */     public Message getMessageDefaultInstance() {
/* 121 */       return this.defaultEntry;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private MapField(Converter<K, V> converter, StorageMode mode, Map<K, V> mapData) {
/* 129 */     this.converter = converter;
/* 130 */     this.isMutable = true;
/* 131 */     this.mode = mode;
/* 132 */     this.mapData = new MutatabilityAwareMap<>(this, mapData);
/* 133 */     this.listData = null;
/*     */   }
/*     */   
/*     */   private MapField(MapEntry<K, V> defaultEntry, StorageMode mode, Map<K, V> mapData) {
/* 137 */     this(new ImmutableMessageConverter<>(defaultEntry), mode, mapData);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static <K, V> MapField<K, V> emptyMapField(MapEntry<K, V> defaultEntry) {
/* 143 */     return new MapField<>(defaultEntry, StorageMode.MAP, Collections.emptyMap());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static <K, V> MapField<K, V> newMapField(MapEntry<K, V> defaultEntry) {
/* 149 */     return new MapField<>(defaultEntry, StorageMode.MAP, new LinkedHashMap<>());
/*     */   }
/*     */ 
/*     */   
/*     */   private Message convertKeyAndValueToMessage(K key, V value) {
/* 154 */     return this.converter.convertKeyAndValueToMessage(key, value);
/*     */   }
/*     */ 
/*     */   
/*     */   private void convertMessageToKeyAndValue(Message message, Map<K, V> map) {
/* 159 */     this.converter.convertMessageToKeyAndValue(message, map);
/*     */   }
/*     */   
/*     */   private List<Message> convertMapToList(MutatabilityAwareMap<K, V> mapData) {
/* 163 */     List<Message> listData = new ArrayList<>();
/* 164 */     for (Map.Entry<K, V> entry : mapData.entrySet()) {
/* 165 */       listData.add(convertKeyAndValueToMessage(entry.getKey(), entry.getValue()));
/*     */     }
/* 167 */     return listData;
/*     */   }
/*     */   
/*     */   private MutatabilityAwareMap<K, V> convertListToMap(List<Message> listData) {
/* 171 */     Map<K, V> mapData = new LinkedHashMap<>();
/* 172 */     for (Message item : listData) {
/* 173 */       convertMessageToKeyAndValue(item, mapData);
/*     */     }
/* 175 */     return new MutatabilityAwareMap<>(this, mapData);
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<K, V> getMap() {
/* 180 */     if (this.mode == StorageMode.LIST) {
/* 181 */       synchronized (this) {
/* 182 */         if (this.mode == StorageMode.LIST) {
/* 183 */           this.mapData = convertListToMap(this.listData);
/* 184 */           this.mode = StorageMode.BOTH;
/*     */         } 
/*     */       } 
/*     */     }
/* 188 */     return Collections.unmodifiableMap(this.mapData);
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<K, V> getMutableMap() {
/* 193 */     if (this.mode != StorageMode.MAP) {
/* 194 */       if (this.mode == StorageMode.LIST) {
/* 195 */         this.mapData = convertListToMap(this.listData);
/*     */       }
/* 197 */       this.listData = null;
/* 198 */       this.mode = StorageMode.MAP;
/*     */     } 
/* 200 */     return this.mapData;
/*     */   }
/*     */   
/*     */   public void mergeFrom(MapField<K, V> other) {
/* 204 */     getMutableMap().putAll(MapFieldLite.copy(other.getMap()));
/*     */   }
/*     */   
/*     */   public void clear() {
/* 208 */     this.mapData = new MutatabilityAwareMap<>(this, new LinkedHashMap<>());
/* 209 */     this.mode = StorageMode.MAP;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object object) {
/* 215 */     if (!(object instanceof MapField)) {
/* 216 */       return false;
/*     */     }
/* 218 */     MapField<K, V> other = (MapField<K, V>)object;
/* 219 */     return MapFieldLite.equals(getMap(), other.getMap());
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 224 */     return MapFieldLite.calculateHashCodeForMap(getMap());
/*     */   }
/*     */ 
/*     */   
/*     */   public MapField<K, V> copy() {
/* 229 */     return new MapField(this.converter, StorageMode.MAP, MapFieldLite.copy(getMap()));
/*     */   }
/*     */ 
/*     */   
/*     */   List<Message> getList() {
/* 234 */     if (this.mode == StorageMode.MAP) {
/* 235 */       synchronized (this) {
/* 236 */         if (this.mode == StorageMode.MAP) {
/* 237 */           this.listData = convertMapToList(this.mapData);
/* 238 */           this.mode = StorageMode.BOTH;
/*     */         } 
/*     */       } 
/*     */     }
/* 242 */     return Collections.unmodifiableList(this.listData);
/*     */   }
/*     */ 
/*     */   
/*     */   List<Message> getMutableList() {
/* 247 */     if (this.mode != StorageMode.LIST) {
/* 248 */       if (this.mode == StorageMode.MAP) {
/* 249 */         this.listData = convertMapToList(this.mapData);
/*     */       }
/* 251 */       this.mapData = null;
/* 252 */       this.mode = StorageMode.LIST;
/*     */     } 
/* 254 */     return this.listData;
/*     */   }
/*     */ 
/*     */   
/*     */   Message getMapEntryMessageDefaultInstance() {
/* 259 */     return this.converter.getMessageDefaultInstance();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void makeImmutable() {
/* 267 */     this.isMutable = false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isMutable() {
/* 272 */     return this.isMutable;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void ensureMutable() {
/* 280 */     if (!isMutable())
/* 281 */       throw new UnsupportedOperationException(); 
/*     */   }
/*     */   
/*     */   private static class MutatabilityAwareMap<K, V>
/*     */     implements Map<K, V>
/*     */   {
/*     */     private final MutabilityOracle mutabilityOracle;
/*     */     private final Map<K, V> delegate;
/*     */     
/*     */     MutatabilityAwareMap(MutabilityOracle mutabilityOracle, Map<K, V> delegate) {
/* 291 */       this.mutabilityOracle = mutabilityOracle;
/* 292 */       this.delegate = delegate;
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() {
/* 297 */       return this.delegate.size();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isEmpty() {
/* 302 */       return this.delegate.isEmpty();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean containsKey(Object key) {
/* 307 */       return this.delegate.containsKey(key);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean containsValue(Object value) {
/* 312 */       return this.delegate.containsValue(value);
/*     */     }
/*     */ 
/*     */     
/*     */     public V get(Object key) {
/* 317 */       return this.delegate.get(key);
/*     */     }
/*     */ 
/*     */     
/*     */     public V put(K key, V value) {
/* 322 */       this.mutabilityOracle.ensureMutable();
/* 323 */       Internal.checkNotNull(key);
/* 324 */       Internal.checkNotNull(value);
/* 325 */       return this.delegate.put(key, value);
/*     */     }
/*     */ 
/*     */     
/*     */     public V remove(Object key) {
/* 330 */       this.mutabilityOracle.ensureMutable();
/* 331 */       return this.delegate.remove(key);
/*     */     }
/*     */ 
/*     */     
/*     */     public void putAll(Map<? extends K, ? extends V> m) {
/* 336 */       this.mutabilityOracle.ensureMutable();
/* 337 */       for (K key : m.keySet()) {
/* 338 */         Internal.checkNotNull(key);
/* 339 */         Internal.checkNotNull(m.get(key));
/*     */       } 
/* 341 */       this.delegate.putAll(m);
/*     */     }
/*     */ 
/*     */     
/*     */     public void clear() {
/* 346 */       this.mutabilityOracle.ensureMutable();
/* 347 */       this.delegate.clear();
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<K> keySet() {
/* 352 */       return new MutatabilityAwareSet<>(this.mutabilityOracle, this.delegate.keySet());
/*     */     }
/*     */ 
/*     */     
/*     */     public Collection<V> values() {
/* 357 */       return new MutatabilityAwareCollection<>(this.mutabilityOracle, this.delegate.values());
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<Map.Entry<K, V>> entrySet() {
/* 362 */       return new MutatabilityAwareSet<>(this.mutabilityOracle, this.delegate.entrySet());
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object o) {
/* 367 */       return this.delegate.equals(o);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 372 */       return this.delegate.hashCode();
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 377 */       return this.delegate.toString();
/*     */     }
/*     */     
/*     */     private static class MutatabilityAwareCollection<E>
/*     */       implements Collection<E> {
/*     */       private final MutabilityOracle mutabilityOracle;
/*     */       private final Collection<E> delegate;
/*     */       
/*     */       MutatabilityAwareCollection(MutabilityOracle mutabilityOracle, Collection<E> delegate) {
/* 386 */         this.mutabilityOracle = mutabilityOracle;
/* 387 */         this.delegate = delegate;
/*     */       }
/*     */ 
/*     */       
/*     */       public int size() {
/* 392 */         return this.delegate.size();
/*     */       }
/*     */ 
/*     */       
/*     */       public boolean isEmpty() {
/* 397 */         return this.delegate.isEmpty();
/*     */       }
/*     */ 
/*     */       
/*     */       public boolean contains(Object o) {
/* 402 */         return this.delegate.contains(o);
/*     */       }
/*     */ 
/*     */       
/*     */       public Iterator<E> iterator() {
/* 407 */         return new MapField.MutatabilityAwareMap.MutatabilityAwareIterator<>(this.mutabilityOracle, this.delegate.iterator());
/*     */       }
/*     */ 
/*     */       
/*     */       public Object[] toArray() {
/* 412 */         return this.delegate.toArray();
/*     */       }
/*     */ 
/*     */       
/*     */       public <T> T[] toArray(T[] a) {
/* 417 */         return this.delegate.toArray(a);
/*     */       }
/*     */ 
/*     */ 
/*     */       
/*     */       public boolean add(E e) {
/* 423 */         throw new UnsupportedOperationException();
/*     */       }
/*     */ 
/*     */       
/*     */       public boolean remove(Object o) {
/* 428 */         this.mutabilityOracle.ensureMutable();
/* 429 */         return this.delegate.remove(o);
/*     */       }
/*     */ 
/*     */       
/*     */       public boolean containsAll(Collection<?> c) {
/* 434 */         return this.delegate.containsAll(c);
/*     */       }
/*     */ 
/*     */ 
/*     */       
/*     */       public boolean addAll(Collection<? extends E> c) {
/* 440 */         throw new UnsupportedOperationException();
/*     */       }
/*     */ 
/*     */       
/*     */       public boolean removeAll(Collection<?> c) {
/* 445 */         this.mutabilityOracle.ensureMutable();
/* 446 */         return this.delegate.removeAll(c);
/*     */       }
/*     */ 
/*     */       
/*     */       public boolean retainAll(Collection<?> c) {
/* 451 */         this.mutabilityOracle.ensureMutable();
/* 452 */         return this.delegate.retainAll(c);
/*     */       }
/*     */ 
/*     */       
/*     */       public void clear() {
/* 457 */         this.mutabilityOracle.ensureMutable();
/* 458 */         this.delegate.clear();
/*     */       }
/*     */ 
/*     */       
/*     */       public boolean equals(Object o) {
/* 463 */         return this.delegate.equals(o);
/*     */       }
/*     */ 
/*     */       
/*     */       public int hashCode() {
/* 468 */         return this.delegate.hashCode();
/*     */       }
/*     */ 
/*     */       
/*     */       public String toString() {
/* 473 */         return this.delegate.toString();
/*     */       }
/*     */     }
/*     */     
/*     */     private static class MutatabilityAwareSet<E>
/*     */       implements Set<E> {
/*     */       private final MutabilityOracle mutabilityOracle;
/*     */       private final Set<E> delegate;
/*     */       
/*     */       MutatabilityAwareSet(MutabilityOracle mutabilityOracle, Set<E> delegate) {
/* 483 */         this.mutabilityOracle = mutabilityOracle;
/* 484 */         this.delegate = delegate;
/*     */       }
/*     */ 
/*     */       
/*     */       public int size() {
/* 489 */         return this.delegate.size();
/*     */       }
/*     */ 
/*     */       
/*     */       public boolean isEmpty() {
/* 494 */         return this.delegate.isEmpty();
/*     */       }
/*     */ 
/*     */       
/*     */       public boolean contains(Object o) {
/* 499 */         return this.delegate.contains(o);
/*     */       }
/*     */ 
/*     */       
/*     */       public Iterator<E> iterator() {
/* 504 */         return new MapField.MutatabilityAwareMap.MutatabilityAwareIterator<>(this.mutabilityOracle, this.delegate.iterator());
/*     */       }
/*     */ 
/*     */       
/*     */       public Object[] toArray() {
/* 509 */         return this.delegate.toArray();
/*     */       }
/*     */ 
/*     */       
/*     */       public <T> T[] toArray(T[] a) {
/* 514 */         return this.delegate.toArray(a);
/*     */       }
/*     */ 
/*     */       
/*     */       public boolean add(E e) {
/* 519 */         this.mutabilityOracle.ensureMutable();
/* 520 */         return this.delegate.add(e);
/*     */       }
/*     */ 
/*     */       
/*     */       public boolean remove(Object o) {
/* 525 */         this.mutabilityOracle.ensureMutable();
/* 526 */         return this.delegate.remove(o);
/*     */       }
/*     */ 
/*     */       
/*     */       public boolean containsAll(Collection<?> c) {
/* 531 */         return this.delegate.containsAll(c);
/*     */       }
/*     */ 
/*     */       
/*     */       public boolean addAll(Collection<? extends E> c) {
/* 536 */         this.mutabilityOracle.ensureMutable();
/* 537 */         return this.delegate.addAll(c);
/*     */       }
/*     */ 
/*     */       
/*     */       public boolean retainAll(Collection<?> c) {
/* 542 */         this.mutabilityOracle.ensureMutable();
/* 543 */         return this.delegate.retainAll(c);
/*     */       }
/*     */ 
/*     */       
/*     */       public boolean removeAll(Collection<?> c) {
/* 548 */         this.mutabilityOracle.ensureMutable();
/* 549 */         return this.delegate.removeAll(c);
/*     */       }
/*     */ 
/*     */       
/*     */       public void clear() {
/* 554 */         this.mutabilityOracle.ensureMutable();
/* 555 */         this.delegate.clear();
/*     */       }
/*     */ 
/*     */       
/*     */       public boolean equals(Object o) {
/* 560 */         return this.delegate.equals(o);
/*     */       }
/*     */ 
/*     */       
/*     */       public int hashCode() {
/* 565 */         return this.delegate.hashCode();
/*     */       }
/*     */ 
/*     */       
/*     */       public String toString() {
/* 570 */         return this.delegate.toString();
/*     */       }
/*     */     }
/*     */     
/*     */     private static class MutatabilityAwareIterator<E>
/*     */       implements Iterator<E> {
/*     */       private final MutabilityOracle mutabilityOracle;
/*     */       private final Iterator<E> delegate;
/*     */       
/*     */       MutatabilityAwareIterator(MutabilityOracle mutabilityOracle, Iterator<E> delegate) {
/* 580 */         this.mutabilityOracle = mutabilityOracle;
/* 581 */         this.delegate = delegate;
/*     */       }
/*     */ 
/*     */       
/*     */       public boolean hasNext() {
/* 586 */         return this.delegate.hasNext();
/*     */       }
/*     */ 
/*     */       
/*     */       public E next() {
/* 591 */         return this.delegate.next();
/*     */       }
/*     */ 
/*     */       
/*     */       public void remove() {
/* 596 */         this.mutabilityOracle.ensureMutable();
/* 597 */         this.delegate.remove();
/*     */       }
/*     */ 
/*     */       
/*     */       public boolean equals(Object obj) {
/* 602 */         return this.delegate.equals(obj);
/*     */       }
/*     */ 
/*     */       
/*     */       public int hashCode() {
/* 607 */         return this.delegate.hashCode();
/*     */       }
/*     */ 
/*     */       
/*     */       public String toString() {
/* 612 */         return this.delegate.toString();
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\MapField.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */