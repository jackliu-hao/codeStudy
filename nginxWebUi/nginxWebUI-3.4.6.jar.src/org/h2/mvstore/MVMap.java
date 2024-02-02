/*      */ package org.h2.mvstore;
/*      */ 
/*      */ import java.util.AbstractList;
/*      */ import java.util.AbstractMap;
/*      */ import java.util.AbstractSet;
/*      */ import java.util.Arrays;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.ConcurrentMap;
/*      */ import java.util.concurrent.atomic.AtomicLong;
/*      */ import java.util.concurrent.atomic.AtomicReference;
/*      */ import org.h2.mvstore.type.DataType;
/*      */ import org.h2.mvstore.type.ObjectDataType;
/*      */ import org.h2.util.MemoryEstimator;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class MVMap<K, V>
/*      */   extends AbstractMap<K, V>
/*      */   implements ConcurrentMap<K, V>
/*      */ {
/*      */   public final MVStore store;
/*      */   private final AtomicReference<RootReference<K, V>> root;
/*      */   private final int id;
/*      */   private final long createVersion;
/*      */   private final DataType<K> keyType;
/*      */   private final DataType<V> valueType;
/*      */   private final int keysPerPage;
/*      */   private final boolean singleWriter;
/*      */   private final K[] keysBuffer;
/*      */   private final V[] valuesBuffer;
/*   56 */   private final Object lock = new Object();
/*      */ 
/*      */   
/*      */   private volatile boolean notificationRequested;
/*      */ 
/*      */   
/*      */   private volatile boolean closed;
/*      */ 
/*      */   
/*      */   private boolean readOnly;
/*      */   
/*      */   private boolean isVolatile;
/*      */   
/*      */   private final AtomicLong avgKeySize;
/*      */   
/*      */   private final AtomicLong avgValSize;
/*      */   
/*      */   static final long INITIAL_VERSION = -1L;
/*      */ 
/*      */   
/*      */   protected MVMap(Map<String, Object> paramMap, DataType<K> paramDataType, DataType<V> paramDataType1) {
/*   77 */     this((MVStore)paramMap.get("store"), paramDataType, paramDataType1, 
/*   78 */         DataUtils.readHexInt(paramMap, "id", 0), 
/*   79 */         DataUtils.readHexLong(paramMap, "createVersion", 0L), new AtomicReference<>(), ((MVStore)paramMap
/*      */         
/*   81 */         .get("store")).getKeysPerPage(), (paramMap
/*   82 */         .containsKey("singleWriter") && ((Boolean)paramMap.get("singleWriter")).booleanValue()));
/*      */     
/*   84 */     setInitialRoot(createEmptyLeaf(), this.store.getCurrentVersion());
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected MVMap(MVMap<K, V> paramMVMap) {
/*   90 */     this(paramMVMap.store, paramMVMap.keyType, paramMVMap.valueType, paramMVMap.id, paramMVMap.createVersion, new AtomicReference<>(paramMVMap.root
/*   91 */           .get()), paramMVMap.keysPerPage, paramMVMap.singleWriter);
/*      */   }
/*      */ 
/*      */   
/*      */   MVMap(MVStore paramMVStore, int paramInt, DataType<K> paramDataType, DataType<V> paramDataType1) {
/*   96 */     this(paramMVStore, paramDataType, paramDataType1, paramInt, 0L, new AtomicReference<>(), paramMVStore.getKeysPerPage(), false);
/*   97 */     setInitialRoot(createEmptyLeaf(), paramMVStore.getCurrentVersion());
/*      */   }
/*      */ 
/*      */   
/*      */   private MVMap(MVStore paramMVStore, DataType<K> paramDataType, DataType<V> paramDataType1, int paramInt1, long paramLong, AtomicReference<RootReference<K, V>> paramAtomicReference, int paramInt2, boolean paramBoolean) {
/*  102 */     this.store = paramMVStore;
/*  103 */     this.id = paramInt1;
/*  104 */     this.createVersion = paramLong;
/*  105 */     this.keyType = paramDataType;
/*  106 */     this.valueType = paramDataType1;
/*  107 */     this.root = paramAtomicReference;
/*  108 */     this.keysPerPage = paramInt2;
/*  109 */     this.keysBuffer = paramBoolean ? (K[])paramDataType.createStorage(paramInt2) : null;
/*  110 */     this.valuesBuffer = paramBoolean ? (V[])paramDataType1.createStorage(paramInt2) : null;
/*  111 */     this.singleWriter = paramBoolean;
/*  112 */     this.avgKeySize = paramDataType.isMemoryEstimationAllowed() ? new AtomicLong() : null;
/*  113 */     this.avgValSize = paramDataType1.isMemoryEstimationAllowed() ? new AtomicLong() : null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected MVMap<K, V> cloneIt() {
/*  123 */     return new MVMap(this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static String getMapRootKey(int paramInt) {
/*  133 */     return "root." + Integer.toHexString(paramInt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static String getMapKey(int paramInt) {
/*  143 */     return "map." + Integer.toHexString(paramInt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public V put(K paramK, V paramV) {
/*  155 */     DataUtils.checkArgument((paramV != null), "The value may not be null", new Object[0]);
/*  156 */     return operate(paramK, paramV, (DecisionMaker)DecisionMaker.PUT);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final K firstKey() {
/*  165 */     return getFirstLast(true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final K lastKey() {
/*  174 */     return getFirstLast(false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final K getKey(long paramLong) {
/*  186 */     if (paramLong < 0L || paramLong >= sizeAsLong()) {
/*  187 */       return null;
/*      */     }
/*  189 */     Page<K, V> page = getRootPage();
/*  190 */     long l = 0L;
/*      */     while (true) {
/*  192 */       if (page.isLeaf()) {
/*  193 */         if (paramLong >= l + page.getKeyCount()) {
/*  194 */           return null;
/*      */         }
/*  196 */         return page.getKey((int)(paramLong - l));
/*      */       } 
/*      */       
/*  199 */       byte b = 0; int i = getChildPageCount(page);
/*  200 */       for (; b < i; b++) {
/*  201 */         long l1 = page.getCounts(b);
/*  202 */         if (paramLong < l1 + l) {
/*      */           break;
/*      */         }
/*  205 */         l += l1;
/*      */       } 
/*  207 */       if (b == i) {
/*  208 */         return null;
/*      */       }
/*  210 */       page = page.getChildPage(b);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final List<K> keyList() {
/*  223 */     return new AbstractList<K>()
/*      */       {
/*      */         public K get(int param1Int)
/*      */         {
/*  227 */           return (K)MVMap.this.getKey(param1Int);
/*      */         }
/*      */ 
/*      */         
/*      */         public int size() {
/*  232 */           return MVMap.this.size();
/*      */         }
/*      */ 
/*      */ 
/*      */         
/*      */         public int indexOf(Object param1Object) {
/*  238 */           return (int)MVMap.this.getKeyIndex(param1Object);
/*      */         }
/*      */       };
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final long getKeyIndex(K paramK) {
/*  257 */     Page<K, V> page = getRootPage();
/*  258 */     if (page.getTotalCount() == 0L) {
/*  259 */       return -1L;
/*      */     }
/*  261 */     long l = 0L;
/*      */     while (true) {
/*  263 */       int i = page.binarySearch(paramK);
/*  264 */       if (page.isLeaf()) {
/*  265 */         if (i < 0) {
/*  266 */           l = -l;
/*      */         }
/*  268 */         return l + i;
/*      */       } 
/*  270 */       if (i++ < 0) {
/*  271 */         i = -i;
/*      */       }
/*  273 */       for (byte b = 0; b < i; b++) {
/*  274 */         l += page.getCounts(b);
/*      */       }
/*  276 */       page = page.getChildPage(i);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private K getFirstLast(boolean paramBoolean) {
/*  287 */     Page<K, V> page = getRootPage();
/*  288 */     return getFirstLast(page, paramBoolean);
/*      */   }
/*      */   
/*      */   private K getFirstLast(Page<K, V> paramPage, boolean paramBoolean) {
/*  292 */     if (paramPage.getTotalCount() == 0L) {
/*  293 */       return null;
/*      */     }
/*      */     while (true) {
/*  296 */       if (paramPage.isLeaf()) {
/*  297 */         return paramPage.getKey(paramBoolean ? 0 : (paramPage.getKeyCount() - 1));
/*      */       }
/*  299 */       paramPage = paramPage.getChildPage(paramBoolean ? 0 : (getChildPageCount(paramPage) - 1));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final K higherKey(K paramK) {
/*  311 */     return getMinMax(paramK, false, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final K higherKey(RootReference<K, V> paramRootReference, K paramK) {
/*  323 */     return getMinMax(paramRootReference, paramK, false, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final K ceilingKey(K paramK) {
/*  333 */     return getMinMax(paramK, false, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final K floorKey(K paramK) {
/*  343 */     return getMinMax(paramK, true, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final K lowerKey(K paramK) {
/*  354 */     return getMinMax(paramK, true, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final K lowerKey(RootReference<K, V> paramRootReference, K paramK) {
/*  366 */     return getMinMax(paramRootReference, paramK, true, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private K getMinMax(K paramK, boolean paramBoolean1, boolean paramBoolean2) {
/*  378 */     return getMinMax(flushAndGetRoot(), paramK, paramBoolean1, paramBoolean2);
/*      */   }
/*      */   
/*      */   private K getMinMax(RootReference<K, V> paramRootReference, K paramK, boolean paramBoolean1, boolean paramBoolean2) {
/*  382 */     return getMinMax(paramRootReference.root, paramK, paramBoolean1, paramBoolean2);
/*      */   }
/*      */   
/*      */   private K getMinMax(Page<K, V> paramPage, K paramK, boolean paramBoolean1, boolean paramBoolean2) {
/*  386 */     int i = paramPage.binarySearch(paramK);
/*  387 */     if (paramPage.isLeaf()) {
/*  388 */       if (i < 0) {
/*  389 */         i = -i - (paramBoolean1 ? 2 : 1);
/*  390 */       } else if (paramBoolean2) {
/*  391 */         i += paramBoolean1 ? -1 : 1;
/*      */       } 
/*  393 */       if (i < 0 || i >= paramPage.getKeyCount()) {
/*  394 */         return null;
/*      */       }
/*  396 */       return paramPage.getKey(i);
/*      */     } 
/*  398 */     if (i++ < 0) {
/*  399 */       i = -i;
/*      */     }
/*      */     while (true) {
/*  402 */       if (i < 0 || i >= getChildPageCount(paramPage)) {
/*  403 */         return null;
/*      */       }
/*  405 */       K k = getMinMax(paramPage.getChildPage(i), paramK, paramBoolean1, paramBoolean2);
/*  406 */       if (k != null) {
/*  407 */         return k;
/*      */       }
/*  409 */       i += paramBoolean1 ? -1 : 1;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final V get(Object paramObject) {
/*  424 */     return get(getRootPage(), (K)paramObject);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public V get(Page<K, V> paramPage, K paramK) {
/*  436 */     return Page.get(paramPage, paramK);
/*      */   }
/*      */ 
/*      */   
/*      */   public final boolean containsKey(Object paramObject) {
/*  441 */     return (get(paramObject) != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void clear() {
/*  449 */     clearIt();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   RootReference<K, V> clearIt() {
/*  458 */     Page<K, V> page = createEmptyLeaf();
/*  459 */     byte b = 0;
/*      */     while (true) {
/*  461 */       RootReference<K, V> rootReference = flushAndGetRoot();
/*  462 */       if (rootReference.getTotalCount() == 0L) {
/*  463 */         return rootReference;
/*      */       }
/*  465 */       boolean bool = rootReference.isLockedByCurrentThread();
/*  466 */       if (!bool) {
/*  467 */         if (b++ == 0) {
/*  468 */           beforeWrite();
/*  469 */         } else if (b > 3 || rootReference.isLocked()) {
/*  470 */           rootReference = lockRoot(rootReference, b);
/*  471 */           bool = true;
/*      */         } 
/*      */       }
/*  474 */       Page<K, V> page1 = rootReference.root;
/*  475 */       long l = rootReference.version;
/*      */       
/*  477 */       try { if (!bool)
/*  478 */         { rootReference = rootReference.updateRootPage(page, b);
/*  479 */           if (rootReference == null)
/*      */           
/*      */           { 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  487 */             if (bool)
/*  488 */               unlockRoot(page1);  continue; }  }  this.store.registerUnsavedMemory(page1.removeAllRecursive(l)); page1 = page; return rootReference; } finally { if (bool) unlockRoot(page1);
/*      */          }
/*      */     
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final void close() {
/*  499 */     this.closed = true;
/*      */   }
/*      */   
/*      */   public final boolean isClosed() {
/*  503 */     return this.closed;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public V remove(Object paramObject) {
/*  516 */     return operate((K)paramObject, null, (DecisionMaker)DecisionMaker.REMOVE);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final V putIfAbsent(K paramK, V paramV) {
/*  528 */     return operate(paramK, paramV, (DecisionMaker)DecisionMaker.IF_ABSENT);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean remove(Object paramObject1, Object paramObject2) {
/*  541 */     EqualsDecisionMaker<V> equalsDecisionMaker = new EqualsDecisionMaker<>(this.valueType, (V)paramObject2);
/*  542 */     operate((K)paramObject1, null, equalsDecisionMaker);
/*  543 */     return (equalsDecisionMaker.getDecision() != Decision.ABORT);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static <X> boolean areValuesEqual(DataType<X> paramDataType, X paramX1, X paramX2) {
/*  557 */     return (paramX1 == paramX2 || (paramX1 != null && paramX2 != null && paramDataType
/*  558 */       .compare(paramX1, paramX2) == 0));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final boolean replace(K paramK, V paramV1, V paramV2) {
/*  571 */     EqualsDecisionMaker<V> equalsDecisionMaker = new EqualsDecisionMaker<>(this.valueType, paramV1);
/*  572 */     V v = operate(paramK, paramV2, equalsDecisionMaker);
/*  573 */     boolean bool = (equalsDecisionMaker.getDecision() != Decision.ABORT) ? true : false;
/*  574 */     assert !bool || areValuesEqual(this.valueType, paramV1, v) : (new StringBuilder()).append(paramV1).append(" != ").append(v).toString();
/*  575 */     return bool;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final V replace(K paramK, V paramV) {
/*  587 */     return operate(paramK, paramV, (DecisionMaker)DecisionMaker.IF_PRESENT);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final int compare(K paramK1, K paramK2) {
/*  599 */     return this.keyType.compare(paramK1, paramK2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final DataType<K> getKeyType() {
/*  608 */     return this.keyType;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final DataType<V> getValueType() {
/*  617 */     return this.valueType;
/*      */   }
/*      */   
/*      */   boolean isSingleWriter() {
/*  621 */     return this.singleWriter;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final Page<K, V> readPage(long paramLong) {
/*  631 */     return this.store.readPage(this, paramLong);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final void setRootPos(long paramLong1, long paramLong2) {
/*  641 */     Page<K, V> page = readOrCreateRootPage(paramLong1);
/*  642 */     if (page.map != this) {
/*      */ 
/*      */ 
/*      */       
/*  646 */       assert this.id == page.map.id;
/*      */ 
/*      */       
/*  649 */       page = page.copy(this, false);
/*      */     } 
/*  651 */     setInitialRoot(page, paramLong2);
/*  652 */     setWriteVersion(this.store.getCurrentVersion());
/*      */   }
/*      */   
/*      */   private Page<K, V> readOrCreateRootPage(long paramLong) {
/*  656 */     return (paramLong == 0L) ? createEmptyLeaf() : readPage(paramLong);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Iterator<K> keyIterator(K paramK) {
/*  667 */     return cursor(paramK, null, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Iterator<K> keyIteratorReverse(K paramK) {
/*  677 */     return cursor(paramK, null, true);
/*      */   }
/*      */   
/*      */   final boolean rewritePage(long paramLong) {
/*  681 */     Page<K, V> page = readPage(paramLong);
/*  682 */     if (page.getKeyCount() == 0) {
/*  683 */       return true;
/*      */     }
/*  685 */     assert page.isSaved();
/*  686 */     K k = page.getKey(0);
/*  687 */     if (!isClosed()) {
/*  688 */       RewriteDecisionMaker<? super V> rewriteDecisionMaker = new RewriteDecisionMaker(page.getPos());
/*  689 */       V v = operate(k, null, rewriteDecisionMaker);
/*  690 */       boolean bool = (rewriteDecisionMaker.getDecision() != Decision.ABORT) ? true : false;
/*  691 */       assert !bool || v != null;
/*  692 */       return bool;
/*      */     } 
/*  694 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Cursor<K, V> cursor(K paramK) {
/*  704 */     return cursor(paramK, null, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Cursor<K, V> cursor(K paramK1, K paramK2, boolean paramBoolean) {
/*  716 */     return cursor(flushAndGetRoot(), paramK1, paramK2, paramBoolean);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Cursor<K, V> cursor(RootReference<K, V> paramRootReference, K paramK1, K paramK2, boolean paramBoolean) {
/*  729 */     return new Cursor<>(paramRootReference, paramK1, paramK2, paramBoolean);
/*      */   }
/*      */ 
/*      */   
/*      */   public final Set<Map.Entry<K, V>> entrySet() {
/*  734 */     final RootReference<K, V> rootReference = flushAndGetRoot();
/*  735 */     return new AbstractSet<Map.Entry<K, V>>()
/*      */       {
/*      */         public Iterator<Map.Entry<K, V>> iterator()
/*      */         {
/*  739 */           final Cursor cursor = MVMap.this.cursor(rootReference, null, null, false);
/*  740 */           return new Iterator<Map.Entry<K, V>>()
/*      */             {
/*      */               public boolean hasNext()
/*      */               {
/*  744 */                 return cursor.hasNext();
/*      */               }
/*      */ 
/*      */               
/*      */               public Map.Entry<K, V> next() {
/*  749 */                 Object object = cursor.next();
/*  750 */                 return new AbstractMap.SimpleImmutableEntry<>((K)object, (V)cursor.getValue());
/*      */               }
/*      */             };
/*      */         }
/*      */ 
/*      */ 
/*      */         
/*      */         public int size() {
/*  758 */           return MVMap.this.size();
/*      */         }
/*      */ 
/*      */         
/*      */         public boolean contains(Object param1Object) {
/*  763 */           return MVMap.this.containsKey(param1Object);
/*      */         }
/*      */       };
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Set<K> keySet() {
/*  772 */     final RootReference<K, V> rootReference = flushAndGetRoot();
/*  773 */     return new AbstractSet<K>()
/*      */       {
/*      */         public Iterator<K> iterator()
/*      */         {
/*  777 */           return MVMap.this.cursor(rootReference, null, null, false);
/*      */         }
/*      */ 
/*      */         
/*      */         public int size() {
/*  782 */           return MVMap.this.size();
/*      */         }
/*      */ 
/*      */         
/*      */         public boolean contains(Object param1Object) {
/*  787 */           return MVMap.this.containsKey(param1Object);
/*      */         }
/*      */       };
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final String getName() {
/*  799 */     return this.store.getMapName(this.id);
/*      */   }
/*      */   
/*      */   public final MVStore getStore() {
/*  803 */     return this.store;
/*      */   }
/*      */   
/*      */   protected final boolean isPersistent() {
/*  807 */     return (this.store.getFileStore() != null && !this.isVolatile);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final int getId() {
/*  817 */     return this.id;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Page<K, V> getRootPage() {
/*  826 */     return (flushAndGetRoot()).root;
/*      */   }
/*      */   
/*      */   public RootReference<K, V> getRoot() {
/*  830 */     return this.root.get();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public RootReference<K, V> flushAndGetRoot() {
/*  839 */     RootReference<K, V> rootReference = getRoot();
/*  840 */     if (this.singleWriter && rootReference.getAppendCounter() > 0) {
/*  841 */       return flushAppendBuffer(rootReference, true);
/*      */     }
/*  843 */     return rootReference;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final void setInitialRoot(Page<K, V> paramPage, long paramLong) {
/*  853 */     this.root.set(new RootReference<>(paramPage, paramLong));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final boolean compareAndSetRoot(RootReference<K, V> paramRootReference1, RootReference<K, V> paramRootReference2) {
/*  865 */     return this.root.compareAndSet(paramRootReference1, paramRootReference2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final void rollbackTo(long paramLong) {
/*  875 */     if (paramLong > this.createVersion) {
/*  876 */       rollbackRoot(paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   boolean rollbackRoot(long paramLong) {
/*  887 */     RootReference<K, V> rootReference1 = flushAndGetRoot();
/*      */     RootReference<K, V> rootReference2;
/*  889 */     while (rootReference1.version >= paramLong && (rootReference2 = rootReference1.previous) != null) {
/*  890 */       if (this.root.compareAndSet(rootReference1, rootReference2)) {
/*  891 */         rootReference1 = rootReference2;
/*  892 */         this.closed = false;
/*      */       } 
/*      */     } 
/*  895 */     setWriteVersion(paramLong);
/*  896 */     return (rootReference1.version < paramLong);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static <K, V> boolean updateRoot(RootReference<K, V> paramRootReference, Page<K, V> paramPage, int paramInt) {
/*  912 */     return (paramRootReference.updateRootPage(paramPage, paramInt) != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void removeUnusedOldVersions(RootReference<K, V> paramRootReference) {
/*  920 */     paramRootReference.removeUnusedOldVersions(this.store.getOldestVersionToKeep());
/*      */   }
/*      */   
/*      */   public final boolean isReadOnly() {
/*  924 */     return this.readOnly;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void setVolatile(boolean paramBoolean) {
/*  933 */     this.isVolatile = paramBoolean;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final boolean isVolatile() {
/*  944 */     return this.isVolatile;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final void beforeWrite() {
/*  956 */     assert !getRoot().isLockedByCurrentThread() : getRoot();
/*  957 */     if (this.closed) {
/*  958 */       int i = getId();
/*  959 */       String str = this.store.getMapName(i);
/*  960 */       throw DataUtils.newMVStoreException(4, "Map {0}({1}) is closed. {2}", new Object[] { str, 
/*  961 */             Integer.valueOf(i), this.store.getPanicException() });
/*      */     } 
/*  963 */     if (this.readOnly) {
/*  964 */       throw DataUtils.newUnsupportedOperationException("This map is read-only");
/*      */     }
/*      */     
/*  967 */     this.store.beforeWrite(this);
/*      */   }
/*      */ 
/*      */   
/*      */   public final int hashCode() {
/*  972 */     return this.id;
/*      */   }
/*      */ 
/*      */   
/*      */   public final boolean equals(Object paramObject) {
/*  977 */     return (this == paramObject);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final int size() {
/*  989 */     long l = sizeAsLong();
/*  990 */     return (l > 2147483647L) ? Integer.MAX_VALUE : (int)l;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final long sizeAsLong() {
/*  999 */     return getRoot().getTotalCount();
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isEmpty() {
/* 1004 */     return (sizeAsLong() == 0L);
/*      */   }
/*      */   
/*      */   final long getCreateVersion() {
/* 1008 */     return this.createVersion;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final MVMap<K, V> openVersion(long paramLong) {
/* 1021 */     if (this.readOnly) {
/* 1022 */       throw DataUtils.newUnsupportedOperationException("This map is read-only; need to call the method on the writable map");
/*      */     }
/*      */ 
/*      */     
/* 1026 */     DataUtils.checkArgument((paramLong >= this.createVersion), "Unknown version {0}; this map was created in version is {1}", new Object[] {
/*      */           
/* 1028 */           Long.valueOf(paramLong), Long.valueOf(this.createVersion) });
/* 1029 */     RootReference<K, V> rootReference1 = flushAndGetRoot();
/* 1030 */     removeUnusedOldVersions(rootReference1);
/*      */     RootReference<K, V> rootReference2;
/* 1032 */     while ((rootReference2 = rootReference1.previous) != null && rootReference2.version >= paramLong) {
/* 1033 */       rootReference1 = rootReference2;
/*      */     }
/* 1035 */     if (rootReference2 == null && paramLong < this.store.getOldestVersionToKeep()) {
/* 1036 */       throw DataUtils.newIllegalArgumentException("Unknown version {0}", new Object[] { Long.valueOf(paramLong) });
/*      */     }
/* 1038 */     MVMap<K, V> mVMap = openReadOnly(rootReference1.root, paramLong);
/* 1039 */     assert mVMap.getVersion() <= paramLong : mVMap.getVersion() + " <= " + paramLong;
/* 1040 */     return mVMap;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final MVMap<K, V> openReadOnly(long paramLong1, long paramLong2) {
/* 1051 */     Page<K, V> page = readOrCreateRootPage(paramLong1);
/* 1052 */     return openReadOnly(page, paramLong2);
/*      */   }
/*      */   
/*      */   private MVMap<K, V> openReadOnly(Page<K, V> paramPage, long paramLong) {
/* 1056 */     MVMap<K, V> mVMap = cloneIt();
/* 1057 */     mVMap.readOnly = true;
/* 1058 */     mVMap.setInitialRoot(paramPage, paramLong);
/* 1059 */     return mVMap;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final long getVersion() {
/* 1069 */     return getRoot().getVersion();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final boolean hasChangesSince(long paramLong) {
/* 1079 */     return getRoot().hasChangesSince(paramLong, isPersistent());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int getChildPageCount(Page<K, V> paramPage) {
/* 1091 */     return paramPage.getRawChildPageCount();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getType() {
/* 1100 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected String asString(String paramString) {
/* 1110 */     StringBuilder stringBuilder = new StringBuilder();
/* 1111 */     if (paramString != null) {
/* 1112 */       DataUtils.appendMap(stringBuilder, "name", paramString);
/*      */     }
/* 1114 */     if (this.createVersion != 0L) {
/* 1115 */       DataUtils.appendMap(stringBuilder, "createVersion", this.createVersion);
/*      */     }
/* 1117 */     String str = getType();
/* 1118 */     if (str != null) {
/* 1119 */       DataUtils.appendMap(stringBuilder, "type", str);
/*      */     }
/* 1121 */     return stringBuilder.toString();
/*      */   }
/*      */   
/*      */   final RootReference<K, V> setWriteVersion(long paramLong) {
/* 1125 */     byte b = 0;
/*      */     while (true) {
/* 1127 */       RootReference<K, V> rootReference1 = flushAndGetRoot();
/* 1128 */       if (rootReference1.version >= paramLong)
/* 1129 */         return rootReference1; 
/* 1130 */       if (isClosed())
/*      */       {
/*      */         
/* 1133 */         if (rootReference1.getVersion() + 1L < this.store.getOldestVersionToKeep()) {
/* 1134 */           this.store.deregisterMapRoot(this.id);
/* 1135 */           return null;
/*      */         } 
/*      */       }
/*      */       
/* 1139 */       RootReference<K, V> rootReference2 = null;
/* 1140 */       if (++b > 3 || rootReference1.isLocked()) {
/* 1141 */         rootReference2 = lockRoot(rootReference1, b);
/* 1142 */         rootReference1 = flushAndGetRoot();
/*      */       } 
/*      */       
/*      */       try {
/* 1146 */         rootReference1 = rootReference1.tryUnlockAndUpdateVersion(paramLong, b);
/* 1147 */         if (rootReference1 != null) {
/* 1148 */           rootReference2 = null;
/* 1149 */           removeUnusedOldVersions(rootReference1);
/* 1150 */           return rootReference1;
/*      */         } 
/*      */       } finally {
/* 1153 */         if (rootReference2 != null) {
/* 1154 */           unlockRoot();
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Page<K, V> createEmptyLeaf() {
/* 1166 */     return Page.createEmptyLeaf(this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Page<K, V> createEmptyNode() {
/* 1175 */     return Page.createEmptyNode(this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final void copyFrom(MVMap<K, V> paramMVMap) {
/* 1184 */     MVStore.TxCounter txCounter = this.store.registerVersionUsage();
/*      */     try {
/* 1186 */       beforeWrite();
/* 1187 */       copy(paramMVMap.getRootPage(), null, 0);
/*      */     } finally {
/* 1189 */       this.store.deregisterVersionUsage(txCounter);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void copy(Page<K, V> paramPage1, Page<K, V> paramPage2, int paramInt) {
/* 1194 */     Page<K, V> page = paramPage1.copy(this, true);
/* 1195 */     if (paramPage2 == null) {
/* 1196 */       setInitialRoot(page, -1L);
/*      */     } else {
/* 1198 */       paramPage2.setChild(paramInt, page);
/*      */     } 
/* 1200 */     if (!paramPage1.isLeaf()) {
/* 1201 */       for (byte b = 0; b < getChildPageCount(page); b++) {
/* 1202 */         if (paramPage1.getChildPagePos(b) != 0L)
/*      */         {
/*      */ 
/*      */           
/* 1206 */           copy(paramPage1.getChildPage(b), page, b);
/*      */         }
/*      */       } 
/* 1209 */       page.setComplete();
/*      */     } 
/* 1211 */     this.store.registerUnsavedMemory(page.getMemory());
/* 1212 */     if (this.store.isSaveNeeded()) {
/* 1213 */       this.store.commit();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private RootReference<K, V> flushAppendBuffer(RootReference<K, V> paramRootReference, boolean paramBoolean) {
/* 1226 */     boolean bool1 = paramRootReference.isLockedByCurrentThread();
/* 1227 */     boolean bool2 = bool1;
/* 1228 */     int i = this.store.getKeysPerPage();
/*      */     try {
/* 1230 */       IntValueHolder intValueHolder = new IntValueHolder();
/* 1231 */       byte b1 = 0;
/*      */       
/* 1233 */       byte b2 = paramBoolean ? 0 : (i - 1); int j;
/* 1234 */       while ((j = paramRootReference.getAppendCounter()) > b2) {
/* 1235 */         if (!bool2) {
/*      */ 
/*      */           
/* 1238 */           paramRootReference = tryLock(paramRootReference, ++b1);
/* 1239 */           if (paramRootReference == null) {
/* 1240 */             paramRootReference = getRoot();
/*      */             continue;
/*      */           } 
/* 1243 */           bool2 = true;
/*      */         } 
/*      */         
/* 1246 */         Page<K, V> page1 = paramRootReference.root;
/* 1247 */         long l = paramRootReference.version;
/* 1248 */         CursorPos<K, V> cursorPos1 = page1.getAppendCursorPos(null);
/* 1249 */         assert cursorPos1 != null;
/* 1250 */         assert cursorPos1.index < 0 : cursorPos1.index;
/* 1251 */         int k = -cursorPos1.index - 1;
/* 1252 */         assert k == cursorPos1.page.getKeyCount() : k + " != " + cursorPos1.page.getKeyCount();
/* 1253 */         Page<K, V> page2 = cursorPos1.page;
/* 1254 */         CursorPos<K, V> cursorPos2 = cursorPos1;
/* 1255 */         cursorPos1 = cursorPos1.parent;
/*      */         
/* 1257 */         int m = 0;
/* 1258 */         Page<K, V> page3 = null;
/* 1259 */         int n = i - page2.getKeyCount();
/* 1260 */         if (n > 0) {
/* 1261 */           page2 = page2.copy();
/* 1262 */           if (j <= n) {
/* 1263 */             page2.expand(j, this.keysBuffer, this.valuesBuffer);
/*      */           } else {
/* 1265 */             page2.expand(n, this.keysBuffer, this.valuesBuffer);
/* 1266 */             j -= n;
/* 1267 */             if (paramBoolean) {
/* 1268 */               K[] arrayOfK = page2.createKeyStorage(j);
/* 1269 */               V[] arrayOfV = page2.createValueStorage(j);
/* 1270 */               System.arraycopy(this.keysBuffer, n, arrayOfK, 0, j);
/* 1271 */               if (this.valuesBuffer != null) {
/* 1272 */                 System.arraycopy(this.valuesBuffer, n, arrayOfV, 0, j);
/*      */               }
/* 1274 */               page3 = Page.createLeaf(this, arrayOfK, arrayOfV, 0);
/*      */             } else {
/* 1276 */               System.arraycopy(this.keysBuffer, n, this.keysBuffer, 0, j);
/* 1277 */               if (this.valuesBuffer != null) {
/* 1278 */                 System.arraycopy(this.valuesBuffer, n, this.valuesBuffer, 0, j);
/*      */               }
/* 1280 */               m = j;
/*      */             } 
/*      */           } 
/*      */         } else {
/* 1284 */           cursorPos2 = cursorPos2.parent;
/* 1285 */           page3 = Page.createLeaf(this, 
/* 1286 */               Arrays.copyOf(this.keysBuffer, j), (this.valuesBuffer == null) ? null : 
/* 1287 */               Arrays.<V>copyOf(this.valuesBuffer, j), 0);
/*      */         } 
/*      */ 
/*      */         
/* 1291 */         intValueHolder.value = 0;
/* 1292 */         if (page3 != null) {
/* 1293 */           assert page3.map == this;
/* 1294 */           assert page3.getKeyCount() > 0;
/* 1295 */           K k1 = page3.getKey(0);
/* 1296 */           intValueHolder.value += page3.getMemory();
/*      */           while (true) {
/* 1298 */             if (cursorPos1 == null) {
/* 1299 */               if (page2.getKeyCount() == 0) {
/* 1300 */                 page2 = page3; break;
/*      */               } 
/* 1302 */               K[] arrayOfK = page2.createKeyStorage(1);
/* 1303 */               arrayOfK[0] = k1;
/* 1304 */               Page.PageReference[] arrayOfPageReference = (Page.PageReference[])Page.createRefStorage(2);
/* 1305 */               arrayOfPageReference[0] = new Page.PageReference<>(page2);
/* 1306 */               arrayOfPageReference[1] = new Page.PageReference<>(page3);
/* 1307 */               intValueHolder.value += page2.getMemory();
/* 1308 */               page2 = Page.createNode(this, arrayOfK, (Page.PageReference<K, V>[])arrayOfPageReference, page2.getTotalCount() + page3.getTotalCount(), 0);
/*      */               
/*      */               break;
/*      */             } 
/* 1312 */             Page<K, V> page = page2;
/* 1313 */             page2 = cursorPos1.page;
/* 1314 */             k = cursorPos1.index;
/* 1315 */             cursorPos1 = cursorPos1.parent;
/* 1316 */             page2 = page2.copy();
/* 1317 */             page2.setChild(k, page3);
/* 1318 */             page2.insertNode(k, k1, page);
/* 1319 */             j = page2.getKeyCount();
/* 1320 */             int i1 = j - (page2.isLeaf() ? 1 : 2);
/* 1321 */             if (j <= i && (page2
/* 1322 */               .getMemory() < this.store.getMaxPageSize() || i1 <= 0)) {
/*      */               break;
/*      */             }
/* 1325 */             k1 = page2.getKey(i1);
/* 1326 */             page3 = page2.split(i1);
/* 1327 */             intValueHolder.value += page2.getMemory() + page3.getMemory();
/*      */           } 
/*      */         } 
/* 1330 */         page2 = replacePage(cursorPos1, page2, intValueHolder);
/* 1331 */         paramRootReference = paramRootReference.updatePageAndLockedStatus(page2, (bool1 || isPersistent()), m);
/*      */         
/* 1333 */         if (paramRootReference != null) {
/*      */           
/* 1335 */           bool2 = (bool1 || isPersistent());
/* 1336 */           if (isPersistent() && cursorPos2 != null) {
/* 1337 */             this.store.registerUnsavedMemory(intValueHolder.value + cursorPos2.processRemovalInfo(l));
/*      */           }
/* 1339 */           assert paramRootReference.getAppendCounter() <= b2;
/*      */           break;
/*      */         } 
/* 1342 */         paramRootReference = getRoot();
/*      */       } 
/*      */     } finally {
/* 1345 */       if (bool2 && !bool1) {
/* 1346 */         paramRootReference = unlockRoot();
/*      */       }
/*      */     } 
/* 1349 */     return paramRootReference;
/*      */   }
/*      */ 
/*      */   
/*      */   private static <K, V> Page<K, V> replacePage(CursorPos<K, V> paramCursorPos, Page<K, V> paramPage, IntValueHolder paramIntValueHolder) {
/* 1354 */     int i = paramPage.isSaved() ? 0 : paramPage.getMemory();
/* 1355 */     while (paramCursorPos != null) {
/* 1356 */       Page<K, V> page = paramCursorPos.page;
/*      */ 
/*      */       
/* 1359 */       if (page.getKeyCount() > 0) {
/* 1360 */         Page<K, V> page1 = paramPage;
/* 1361 */         paramPage = page.copy();
/* 1362 */         paramPage.setChild(paramCursorPos.index, page1);
/* 1363 */         i += paramPage.getMemory();
/*      */       } 
/* 1365 */       paramCursorPos = paramCursorPos.parent;
/*      */     } 
/* 1367 */     paramIntValueHolder.value += i;
/* 1368 */     return paramPage;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void append(K paramK, V paramV) {
/* 1380 */     if (this.singleWriter) {
/* 1381 */       beforeWrite();
/* 1382 */       RootReference<K, V> rootReference = lockRoot(getRoot(), 1);
/* 1383 */       int i = rootReference.getAppendCounter();
/*      */       try {
/* 1385 */         if (i >= this.keysPerPage) {
/* 1386 */           rootReference = flushAppendBuffer(rootReference, false);
/* 1387 */           i = rootReference.getAppendCounter();
/* 1388 */           assert i < this.keysPerPage;
/*      */         } 
/* 1390 */         this.keysBuffer[i] = paramK;
/* 1391 */         if (this.valuesBuffer != null) {
/* 1392 */           this.valuesBuffer[i] = paramV;
/*      */         }
/* 1394 */         i++;
/*      */       } finally {
/* 1396 */         unlockRoot(i);
/*      */       } 
/*      */     } else {
/* 1399 */       put(paramK, paramV);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void trimLast() {
/* 1409 */     if (this.singleWriter) {
/* 1410 */       RootReference<K, V> rootReference = getRoot();
/* 1411 */       int i = rootReference.getAppendCounter();
/* 1412 */       boolean bool = (i == 0) ? true : false;
/* 1413 */       if (!bool) {
/* 1414 */         rootReference = lockRoot(rootReference, 1);
/*      */         try {
/* 1416 */           i = rootReference.getAppendCounter();
/* 1417 */           bool = (i == 0) ? true : false;
/* 1418 */           if (!bool) {
/* 1419 */             i--;
/*      */           }
/*      */         } finally {
/* 1422 */           unlockRoot(i);
/*      */         } 
/*      */       } 
/* 1425 */       if (bool) {
/* 1426 */         Page<K, V> page = (rootReference.root.getAppendCursorPos(null)).page;
/* 1427 */         assert page.isLeaf();
/* 1428 */         assert page.getKeyCount() > 0;
/* 1429 */         K k = page.getKey(page.getKeyCount() - 1);
/* 1430 */         remove(k);
/*      */       } 
/*      */     } else {
/* 1433 */       remove(lastKey());
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public final String toString() {
/* 1439 */     return asString(null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static abstract class BasicBuilder<M extends MVMap<K, V>, K, V>
/*      */     implements MapBuilder<M, K, V>
/*      */   {
/*      */     private DataType<K> keyType;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private DataType<V> valueType;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public DataType<K> getKeyType() {
/* 1490 */       return this.keyType;
/*      */     }
/*      */ 
/*      */     
/*      */     public DataType<V> getValueType() {
/* 1495 */       return this.valueType;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void setKeyType(DataType<? super K> param1DataType) {
/* 1501 */       this.keyType = (DataType)param1DataType;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void setValueType(DataType<? super V> param1DataType) {
/* 1507 */       this.valueType = (DataType)param1DataType;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public BasicBuilder<M, K, V> keyType(DataType<? super K> param1DataType) {
/* 1517 */       setKeyType(param1DataType);
/* 1518 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public BasicBuilder<M, K, V> valueType(DataType<? super V> param1DataType) {
/* 1528 */       setValueType(param1DataType);
/* 1529 */       return this;
/*      */     }
/*      */ 
/*      */     
/*      */     public M create(MVStore param1MVStore, Map<String, Object> param1Map) {
/* 1534 */       if (getKeyType() == null) {
/* 1535 */         setKeyType((DataType<? super K>)new ObjectDataType());
/*      */       }
/* 1537 */       if (getValueType() == null) {
/* 1538 */         setValueType((DataType<? super V>)new ObjectDataType());
/*      */       }
/* 1540 */       DataType<K> dataType = getKeyType();
/* 1541 */       DataType<V> dataType1 = getValueType();
/* 1542 */       param1Map.put("store", param1MVStore);
/* 1543 */       param1Map.put("key", dataType);
/* 1544 */       param1Map.put("val", dataType1);
/* 1545 */       return create(param1Map);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected abstract M create(Map<String, Object> param1Map);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class Builder<K, V>
/*      */     extends BasicBuilder<MVMap<K, V>, K, V>
/*      */   {
/*      */     private boolean singleWriter;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder<K, V> keyType(DataType<? super K> param1DataType) {
/* 1570 */       setKeyType(param1DataType);
/* 1571 */       return this;
/*      */     }
/*      */ 
/*      */     
/*      */     public Builder<K, V> valueType(DataType<? super V> param1DataType) {
/* 1576 */       setValueType(param1DataType);
/* 1577 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder<K, V> singleWriter() {
/* 1587 */       this.singleWriter = true;
/* 1588 */       return this;
/*      */     }
/*      */ 
/*      */     
/*      */     protected MVMap<K, V> create(Map<String, Object> param1Map) {
/* 1593 */       param1Map.put("singleWriter", Boolean.valueOf(this.singleWriter));
/* 1594 */       Object object = param1Map.get("type");
/* 1595 */       if (object == null || object.equals("rtree")) {
/* 1596 */         return new MVMap<>(param1Map, getKeyType(), getValueType());
/*      */       }
/* 1598 */       throw new IllegalArgumentException("Incompatible map type");
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public enum Decision
/*      */   {
/* 1605 */     ABORT, REMOVE, PUT, REPEAT;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static abstract class DecisionMaker<V>
/*      */   {
/* 1622 */     public static final DecisionMaker<Object> DEFAULT = new DecisionMaker<Object>()
/*      */       {
/*      */         public MVMap.Decision decide(Object param2Object1, Object param2Object2) {
/* 1625 */           return (param2Object2 == null) ? MVMap.Decision.REMOVE : MVMap.Decision.PUT;
/*      */         }
/*      */ 
/*      */         
/*      */         public String toString() {
/* 1630 */           return "default";
/*      */         }
/*      */       };
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1637 */     public static final DecisionMaker<Object> PUT = new DecisionMaker<Object>()
/*      */       {
/*      */         public MVMap.Decision decide(Object param2Object1, Object param2Object2) {
/* 1640 */           return MVMap.Decision.PUT;
/*      */         }
/*      */ 
/*      */         
/*      */         public String toString() {
/* 1645 */           return "put";
/*      */         }
/*      */       };
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1652 */     public static final DecisionMaker<Object> REMOVE = new DecisionMaker<Object>()
/*      */       {
/*      */         public MVMap.Decision decide(Object param2Object1, Object param2Object2) {
/* 1655 */           return MVMap.Decision.REMOVE;
/*      */         }
/*      */ 
/*      */         
/*      */         public String toString() {
/* 1660 */           return "remove";
/*      */         }
/*      */       };
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1667 */     static final DecisionMaker<Object> IF_ABSENT = new DecisionMaker<Object>()
/*      */       {
/*      */         public MVMap.Decision decide(Object param2Object1, Object param2Object2) {
/* 1670 */           return (param2Object1 == null) ? MVMap.Decision.PUT : MVMap.Decision.ABORT;
/*      */         }
/*      */ 
/*      */         
/*      */         public String toString() {
/* 1675 */           return "if_absent";
/*      */         }
/*      */       };
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1682 */     static final DecisionMaker<Object> IF_PRESENT = new DecisionMaker<Object>()
/*      */       {
/*      */         public MVMap.Decision decide(Object param2Object1, Object param2Object2) {
/* 1685 */           return (param2Object1 != null) ? MVMap.Decision.PUT : MVMap.Decision.ABORT;
/*      */         }
/*      */ 
/*      */         
/*      */         public String toString() {
/* 1690 */           return "if_present";
/*      */         }
/*      */       };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public MVMap.Decision decide(V param1V1, V param1V2, CursorPos<?, ?> param1CursorPos) {
/* 1703 */       return decide(param1V1, param1V2);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public abstract MVMap.Decision decide(V param1V1, V param1V2);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public <T extends V> T selectValue(T param1T1, T param1T2) {
/* 1728 */       return param1T2;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void reset() {}
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public V operate(K paramK, V paramV, DecisionMaker<? super V> paramDecisionMaker) {
/* 1748 */     IntValueHolder intValueHolder = new IntValueHolder();
/* 1749 */     byte b = 0;
/*      */     while (true) {
/* 1751 */       RootReference<K, V> rootReference = flushAndGetRoot();
/* 1752 */       boolean bool = rootReference.isLockedByCurrentThread();
/* 1753 */       if (!bool) {
/* 1754 */         if (b++ == 0) {
/* 1755 */           beforeWrite();
/*      */         }
/* 1757 */         if (b > 3 || rootReference.isLocked()) {
/* 1758 */           rootReference = lockRoot(rootReference, b);
/* 1759 */           bool = true;
/*      */         } 
/*      */       } 
/* 1762 */       Page<K, V> page = rootReference.root;
/* 1763 */       long l = rootReference.version;
/*      */ 
/*      */       
/* 1766 */       intValueHolder.value = 0;
/*      */       
/* 1768 */       try { CursorPos<K, V> cursorPos2 = CursorPos.traverseDown(page, paramK);
/* 1769 */         if (!bool && rootReference != getRoot())
/*      */         
/*      */         { 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1876 */           if (bool)
/* 1877 */             unlockRoot(page);  continue; }  Page<K, V> page1 = cursorPos2.page; int i = cursorPos2.index; CursorPos<K, V> cursorPos1 = cursorPos2; cursorPos2 = cursorPos2.parent; V v1 = (i < 0) ? null : page1.getValue(i); Decision decision = paramDecisionMaker.decide(v1, paramV, cursorPos1); switch (decision) { case REPEAT: paramDecisionMaker.reset(); if (bool) unlockRoot(page);  continue;case ABORT: if (!bool && rootReference != getRoot()) { paramDecisionMaker.reset(); if (bool) unlockRoot(page);  continue; }  v2 = v1; return v2;case REMOVE: if (i < 0) { if (!bool && rootReference != getRoot()) { paramDecisionMaker.reset(); if (bool) unlockRoot(page);  continue; }  v2 = null; return v2; }  if (page1.getTotalCount() == 1L && cursorPos2 != null) { int j; do { page1 = cursorPos2.page; i = cursorPos2.index; cursorPos2 = cursorPos2.parent; j = page1.getKeyCount(); } while (j == 0 && cursorPos2 != null); if (j <= 1) { if (j == 1) { assert i <= 1; page1 = page1.getChildPage(1 - i); break; }  page1 = Page.createEmptyLeaf(this); break; }  }  page1 = page1.copy(); page1.remove(i); break;case PUT: paramV = paramDecisionMaker.selectValue(v1, paramV); page1 = page1.copy(); if (i < 0) { page1.insertLeaf(-i - 1, paramK, paramV); int j; while ((j = page1.getKeyCount()) > this.store.getKeysPerPage() || (page1.getMemory() > this.store.getMaxPageSize() && j > (page1.isLeaf() ? 1 : 2))) { long l1 = page1.getTotalCount(); int k = j >> 1; K k1 = page1.getKey(k); Page<K, V> page2 = page1.split(k); intValueHolder.value += page1.getMemory() + page2.getMemory(); if (cursorPos2 == null) { K[] arrayOfK = page1.createKeyStorage(1); arrayOfK[0] = k1; Page.PageReference[] arrayOfPageReference = (Page.PageReference[])Page.createRefStorage(2); arrayOfPageReference[0] = new Page.PageReference<>(page1); arrayOfPageReference[1] = new Page.PageReference<>(page2); page1 = Page.createNode(this, arrayOfK, (Page.PageReference<K, V>[])arrayOfPageReference, l1, 0); break; }  Page<K, V> page3 = page1; page1 = cursorPos2.page; i = cursorPos2.index; cursorPos2 = cursorPos2.parent; page1 = page1.copy(); page1.setChild(i, page2); page1.insertNode(i, k1, page3); }  break; }  page1.setValue(i, paramV); break; }  page = replacePage(cursorPos2, page1, intValueHolder); if (!bool) { rootReference = rootReference.updateRootPage(page, b); if (rootReference == null) { paramDecisionMaker.reset(); if (bool) unlockRoot(page);  continue; }  }  this.store.registerUnsavedMemory(intValueHolder.value + cursorPos1.processRemovalInfo(l)); V v2 = v1; return v2; } finally { if (bool) unlockRoot(page);
/*      */          }
/*      */     
/*      */     } 
/*      */   }
/*      */   
/*      */   private RootReference<K, V> lockRoot(RootReference<K, V> paramRootReference, int paramInt) {
/*      */     while (true) {
/* 1885 */       RootReference<K, V> rootReference = tryLock(paramRootReference, paramInt++);
/* 1886 */       if (rootReference != null) {
/* 1887 */         return rootReference;
/*      */       }
/* 1889 */       paramRootReference = getRoot();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected RootReference<K, V> tryLock(RootReference<K, V> paramRootReference, int paramInt) {
/* 1901 */     RootReference<K, V> rootReference1 = paramRootReference.tryLock(paramInt);
/* 1902 */     if (rootReference1 != null) {
/* 1903 */       return rootReference1;
/*      */     }
/* 1905 */     assert !paramRootReference.isLockedByCurrentThread() : paramRootReference;
/* 1906 */     RootReference<K, V> rootReference2 = paramRootReference.previous;
/* 1907 */     int i = 1;
/* 1908 */     if (rootReference2 != null) {
/* 1909 */       long l1 = paramRootReference.updateAttemptCounter - rootReference2.updateAttemptCounter;
/*      */       
/* 1911 */       assert l1 >= 0L : l1;
/* 1912 */       long l2 = paramRootReference.updateCounter - rootReference2.updateCounter;
/* 1913 */       assert l2 >= 0L : l2;
/* 1914 */       assert l1 >= l2 : l1 + " >= " + l2;
/* 1915 */       i += (int)((l1 + 1L) / (l2 + 1L));
/*      */     } 
/*      */     
/* 1918 */     if (paramInt > 4) {
/* 1919 */       if (paramInt <= 12) {
/* 1920 */         Thread.yield();
/* 1921 */       } else if (paramInt <= 70 - 2 * i) {
/*      */         try {
/* 1923 */           Thread.sleep(i);
/* 1924 */         } catch (InterruptedException interruptedException) {
/* 1925 */           throw new RuntimeException(interruptedException);
/*      */         } 
/*      */       } else {
/* 1928 */         synchronized (this.lock) {
/* 1929 */           this.notificationRequested = true;
/*      */           try {
/* 1931 */             this.lock.wait(5L);
/* 1932 */           } catch (InterruptedException interruptedException) {}
/*      */         } 
/*      */       } 
/*      */     }
/*      */     
/* 1937 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private RootReference<K, V> unlockRoot() {
/* 1946 */     return unlockRoot(null, -1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected RootReference<K, V> unlockRoot(Page<K, V> paramPage) {
/* 1956 */     return unlockRoot(paramPage, -1);
/*      */   }
/*      */   
/*      */   private void unlockRoot(int paramInt) {
/* 1960 */     unlockRoot(null, paramInt);
/*      */   }
/*      */   
/*      */   private RootReference<K, V> unlockRoot(Page<K, V> paramPage, int paramInt) {
/*      */     RootReference<K, V> rootReference;
/*      */     do {
/* 1966 */       RootReference<K, V> rootReference1 = getRoot();
/* 1967 */       assert rootReference1.isLockedByCurrentThread();
/* 1968 */       rootReference = rootReference1.updatePageAndLockedStatus((paramPage == null) ? rootReference1.root : paramPage, false, (paramInt == -1) ? rootReference1
/*      */ 
/*      */           
/* 1971 */           .getAppendCounter() : paramInt);
/*      */     }
/* 1973 */     while (rootReference == null);
/*      */     
/* 1975 */     notifyWaiters();
/* 1976 */     return rootReference;
/*      */   }
/*      */   
/*      */   private void notifyWaiters() {
/* 1980 */     if (this.notificationRequested) {
/* 1981 */       synchronized (this.lock) {
/* 1982 */         this.notificationRequested = false;
/* 1983 */         this.lock.notify();
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   final boolean isMemoryEstimationAllowed() {
/* 1989 */     return (this.avgKeySize != null || this.avgValSize != null);
/*      */   }
/*      */   
/*      */   final int evaluateMemoryForKeys(K[] paramArrayOfK, int paramInt) {
/* 1993 */     if (this.avgKeySize == null) {
/* 1994 */       return calculateMemory(this.keyType, paramArrayOfK, paramInt);
/*      */     }
/* 1996 */     return MemoryEstimator.estimateMemory(this.avgKeySize, this.keyType, (Object[])paramArrayOfK, paramInt);
/*      */   }
/*      */   
/*      */   final int evaluateMemoryForValues(V[] paramArrayOfV, int paramInt) {
/* 2000 */     if (this.avgValSize == null) {
/* 2001 */       return calculateMemory(this.valueType, paramArrayOfV, paramInt);
/*      */     }
/* 2003 */     return MemoryEstimator.estimateMemory(this.avgValSize, this.valueType, (Object[])paramArrayOfV, paramInt);
/*      */   }
/*      */   
/*      */   private static <T> int calculateMemory(DataType<T> paramDataType, T[] paramArrayOfT, int paramInt) {
/* 2007 */     int i = paramInt * 8;
/* 2008 */     for (byte b = 0; b < paramInt; b++) {
/* 2009 */       i += paramDataType.getMemory(paramArrayOfT[b]);
/*      */     }
/* 2011 */     return i;
/*      */   }
/*      */   
/*      */   final int evaluateMemoryForKey(K paramK) {
/* 2015 */     if (this.avgKeySize == null) {
/* 2016 */       return this.keyType.getMemory(paramK);
/*      */     }
/* 2018 */     return MemoryEstimator.estimateMemory(this.avgKeySize, this.keyType, paramK);
/*      */   }
/*      */   
/*      */   final int evaluateMemoryForValue(V paramV) {
/* 2022 */     if (this.avgValSize == null) {
/* 2023 */       return this.valueType.getMemory(paramV);
/*      */     }
/* 2025 */     return MemoryEstimator.estimateMemory(this.avgValSize, this.valueType, paramV);
/*      */   }
/*      */   
/*      */   static int samplingPct(AtomicLong paramAtomicLong) {
/* 2029 */     return MemoryEstimator.samplingPct(paramAtomicLong);
/*      */   }
/*      */   
/*      */   private static final class EqualsDecisionMaker<V> extends DecisionMaker<V> {
/*      */     private final DataType<V> dataType;
/*      */     private final V expectedValue;
/*      */     private MVMap.Decision decision;
/*      */     
/*      */     EqualsDecisionMaker(DataType<V> param1DataType, V param1V) {
/* 2038 */       this.dataType = param1DataType;
/* 2039 */       this.expectedValue = param1V;
/*      */     }
/*      */ 
/*      */     
/*      */     public MVMap.Decision decide(V param1V1, V param1V2) {
/* 2044 */       assert this.decision == null;
/* 2045 */       this.decision = !MVMap.areValuesEqual(this.dataType, this.expectedValue, param1V1) ? MVMap.Decision.ABORT : ((param1V2 == null) ? MVMap.Decision.REMOVE : MVMap.Decision.PUT);
/*      */       
/* 2047 */       return this.decision;
/*      */     }
/*      */ 
/*      */     
/*      */     public void reset() {
/* 2052 */       this.decision = null;
/*      */     }
/*      */     
/*      */     MVMap.Decision getDecision() {
/* 2056 */       return this.decision;
/*      */     }
/*      */ 
/*      */     
/*      */     public String toString() {
/* 2061 */       return "equals_to " + this.expectedValue;
/*      */     }
/*      */   }
/*      */   
/*      */   private static final class RewriteDecisionMaker<V> extends DecisionMaker<V> {
/*      */     private final long pagePos;
/*      */     private MVMap.Decision decision;
/*      */     
/*      */     RewriteDecisionMaker(long param1Long) {
/* 2070 */       this.pagePos = param1Long;
/*      */     }
/*      */ 
/*      */     
/*      */     public MVMap.Decision decide(V param1V1, V param1V2, CursorPos<?, ?> param1CursorPos) {
/* 2075 */       assert this.decision == null;
/* 2076 */       this.decision = MVMap.Decision.ABORT;
/* 2077 */       if (!DataUtils.isLeafPosition(this.pagePos)) {
/* 2078 */         while ((param1CursorPos = param1CursorPos.parent) != null) {
/* 2079 */           if (param1CursorPos.page.getPos() == this.pagePos) {
/* 2080 */             this.decision = decide(param1V1, param1V2);
/*      */             break;
/*      */           } 
/*      */         } 
/* 2084 */       } else if (param1CursorPos.page.getPos() == this.pagePos) {
/* 2085 */         this.decision = decide(param1V1, param1V2);
/*      */       } 
/* 2087 */       return this.decision;
/*      */     }
/*      */ 
/*      */     
/*      */     public MVMap.Decision decide(V param1V1, V param1V2) {
/* 2092 */       this.decision = (param1V1 == null) ? MVMap.Decision.ABORT : MVMap.Decision.PUT;
/* 2093 */       return this.decision;
/*      */     }
/*      */ 
/*      */     
/*      */     public <T extends V> T selectValue(T param1T1, T param1T2) {
/* 2098 */       return param1T1;
/*      */     }
/*      */ 
/*      */     
/*      */     public void reset() {
/* 2103 */       this.decision = null;
/*      */     }
/*      */     
/*      */     MVMap.Decision getDecision() {
/* 2107 */       return this.decision;
/*      */     }
/*      */ 
/*      */     
/*      */     public String toString() {
/* 2112 */       return "rewrite";
/*      */     }
/*      */   }
/*      */   
/*      */   private static final class IntValueHolder {
/*      */     int value;
/*      */   }
/*      */   
/*      */   public static interface MapBuilder<M extends MVMap<K, V>, K, V> {
/*      */     M create(MVStore param1MVStore, Map<String, Object> param1Map);
/*      */     
/*      */     DataType<K> getKeyType();
/*      */     
/*      */     DataType<V> getValueType();
/*      */     
/*      */     void setKeyType(DataType<? super K> param1DataType);
/*      */     
/*      */     void setValueType(DataType<? super V> param1DataType);
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\MVMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */