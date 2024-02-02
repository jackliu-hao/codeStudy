/*     */ package org.antlr.v4.runtime.misc;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.LinkedList;
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
/*     */ public class FlexibleHashMap<K, V>
/*     */   implements Map<K, V>
/*     */ {
/*     */   public static final int INITAL_CAPACITY = 16;
/*     */   public static final int INITAL_BUCKET_CAPACITY = 8;
/*     */   public static final double LOAD_FACTOR = 0.75D;
/*     */   protected final AbstractEqualityComparator<? super K> comparator;
/*     */   protected LinkedList<Entry<K, V>>[] buckets;
/*     */   
/*     */   public static class Entry<K, V>
/*     */   {
/*     */     public final K key;
/*     */     public V value;
/*     */     
/*     */     public Entry(K key, V value) {
/*  52 */       this.key = key; this.value = value;
/*     */     }
/*     */     
/*     */     public String toString() {
/*  56 */       return this.key.toString() + ":" + this.value.toString();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  66 */   protected int n = 0;
/*     */   
/*  68 */   protected int threshold = 12;
/*     */   
/*  70 */   protected int currentPrime = 1;
/*  71 */   protected int initialBucketCapacity = 8;
/*     */   
/*     */   public FlexibleHashMap() {
/*  74 */     this(null, 16, 8);
/*     */   }
/*     */   
/*     */   public FlexibleHashMap(AbstractEqualityComparator<? super K> comparator) {
/*  78 */     this(comparator, 16, 8);
/*     */   }
/*     */   
/*     */   public FlexibleHashMap(AbstractEqualityComparator<? super K> comparator, int initialCapacity, int initialBucketCapacity) {
/*  82 */     if (comparator == null) {
/*  83 */       comparator = ObjectEqualityComparator.INSTANCE;
/*     */     }
/*     */     
/*  86 */     this.comparator = comparator;
/*  87 */     this.buckets = createEntryListArray(initialBucketCapacity);
/*  88 */     this.initialBucketCapacity = initialBucketCapacity;
/*     */   }
/*     */ 
/*     */   
/*     */   private static <K, V> LinkedList<Entry<K, V>>[] createEntryListArray(int length) {
/*  93 */     return (LinkedList<Entry<K, V>>[])new LinkedList[length];
/*     */   }
/*     */ 
/*     */   
/*     */   protected int getBucket(K key) {
/*  98 */     int hash = this.comparator.hashCode(key);
/*  99 */     int b = hash & this.buckets.length - 1;
/* 100 */     return b;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public V get(Object key) {
/* 106 */     K typedKey = (K)key;
/* 107 */     if (key == null) return null; 
/* 108 */     int b = getBucket(typedKey);
/* 109 */     LinkedList<Entry<K, V>> bucket = this.buckets[b];
/* 110 */     if (bucket == null) return null; 
/* 111 */     for (Entry<K, V> e : bucket) {
/* 112 */       if (this.comparator.equals(e.key, typedKey)) {
/* 113 */         return e.value;
/*     */       }
/*     */     } 
/* 116 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public V put(K key, V value) {
/* 121 */     if (key == null) return null; 
/* 122 */     if (this.n > this.threshold) expand(); 
/* 123 */     int b = getBucket(key);
/* 124 */     LinkedList<Entry<K, V>> bucket = this.buckets[b];
/* 125 */     if (bucket == null) {
/* 126 */       bucket = this.buckets[b] = new LinkedList<Entry<K, V>>();
/*     */     }
/* 128 */     for (Entry<K, V> e : bucket) {
/* 129 */       if (this.comparator.equals(e.key, key)) {
/* 130 */         V prev = e.value;
/* 131 */         e.value = value;
/* 132 */         this.n++;
/* 133 */         return prev;
/*     */       } 
/*     */     } 
/*     */     
/* 137 */     bucket.add(new Entry<K, V>(key, value));
/* 138 */     this.n++;
/* 139 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public V remove(Object key) {
/* 144 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public void putAll(Map<? extends K, ? extends V> m) {
/* 149 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<K> keySet() {
/* 154 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public Collection<V> values() {
/* 159 */     List<V> a = new ArrayList<V>(size());
/* 160 */     for (LinkedList<Entry<K, V>> bucket : this.buckets) {
/* 161 */       if (bucket != null)
/* 162 */         for (Entry<K, V> e : bucket) {
/* 163 */           a.add(e.value);
/*     */         } 
/*     */     } 
/* 166 */     return a;
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<Map.Entry<K, V>> entrySet() {
/* 171 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsKey(Object key) {
/* 176 */     return (get(key) != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsValue(Object value) {
/* 181 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 186 */     int hash = MurmurHash.initialize();
/* 187 */     for (LinkedList<Entry<K, V>> bucket : this.buckets) {
/* 188 */       if (bucket != null) {
/* 189 */         for (Entry<K, V> e : bucket) {
/* 190 */           if (e == null)
/* 191 */             break;  hash = MurmurHash.update(hash, this.comparator.hashCode(e.key));
/*     */         } 
/*     */       }
/*     */     } 
/* 195 */     hash = MurmurHash.finish(hash, size());
/* 196 */     return hash;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 201 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */   protected void expand() {
/* 205 */     LinkedList<Entry<K, V>>[] old = this.buckets;
/* 206 */     this.currentPrime += 4;
/* 207 */     int newCapacity = this.buckets.length * 2;
/* 208 */     LinkedList[] arrayOfLinkedList = (LinkedList[])createEntryListArray(newCapacity);
/* 209 */     this.buckets = (LinkedList<Entry<K, V>>[])arrayOfLinkedList;
/* 210 */     this.threshold = (int)(newCapacity * 0.75D);
/*     */ 
/*     */     
/* 213 */     int oldSize = size();
/* 214 */     for (LinkedList<Entry<K, V>> bucket : old) {
/* 215 */       if (bucket != null)
/* 216 */         for (Entry<K, V> e : bucket) {
/* 217 */           if (e == null)
/* 218 */             break;  put(e.key, e.value);
/*     */         }  
/*     */     } 
/* 221 */     this.n = oldSize;
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/* 226 */     return this.n;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 231 */     return (this.n == 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/* 236 */     this.buckets = createEntryListArray(16);
/* 237 */     this.n = 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 242 */     if (size() == 0) return "{}";
/*     */     
/* 244 */     StringBuilder buf = new StringBuilder();
/* 245 */     buf.append('{');
/* 246 */     boolean first = true;
/* 247 */     for (LinkedList<Entry<K, V>> bucket : this.buckets) {
/* 248 */       if (bucket != null)
/* 249 */         for (Entry<K, V> e : bucket) {
/* 250 */           if (e == null)
/* 251 */             break;  if (first) { first = false; }
/* 252 */           else { buf.append(", "); }
/* 253 */            buf.append(e.toString());
/*     */         }  
/*     */     } 
/* 256 */     buf.append('}');
/* 257 */     return buf.toString();
/*     */   }
/*     */   
/*     */   public String toTableString() {
/* 261 */     StringBuilder buf = new StringBuilder();
/* 262 */     for (LinkedList<Entry<K, V>> bucket : this.buckets) {
/* 263 */       if (bucket == null) {
/* 264 */         buf.append("null\n");
/*     */       } else {
/*     */         
/* 267 */         buf.append('[');
/* 268 */         boolean first = true;
/* 269 */         for (Entry<K, V> e : bucket) {
/* 270 */           if (first) { first = false; }
/* 271 */           else { buf.append(" "); }
/* 272 */            if (e == null) { buf.append("_"); continue; }
/* 273 */            buf.append(e.toString());
/*     */         } 
/* 275 */         buf.append("]\n");
/*     */       } 
/* 277 */     }  return buf.toString();
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/* 281 */     FlexibleHashMap<String, Integer> map = new FlexibleHashMap<String, Integer>();
/* 282 */     map.put("hi", Integer.valueOf(1));
/* 283 */     map.put("mom", Integer.valueOf(2));
/* 284 */     map.put("foo", Integer.valueOf(3));
/* 285 */     map.put("ach", Integer.valueOf(4));
/* 286 */     map.put("cbba", Integer.valueOf(5));
/* 287 */     map.put("d", Integer.valueOf(6));
/* 288 */     map.put("edf", Integer.valueOf(7));
/* 289 */     map.put("mom", Integer.valueOf(8));
/* 290 */     map.put("hi", Integer.valueOf(9));
/* 291 */     System.out.println(map);
/* 292 */     System.out.println(map.toTableString());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\misc\FlexibleHashMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */