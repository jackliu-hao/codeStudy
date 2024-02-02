package org.h2.mvstore;

import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.h2.mvstore.type.DataType;
import org.h2.mvstore.type.ObjectDataType;
import org.h2.util.MemoryEstimator;

public class MVMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V> {
   public final MVStore store;
   private final AtomicReference<RootReference<K, V>> root;
   private final int id;
   private final long createVersion;
   private final DataType<K> keyType;
   private final DataType<V> valueType;
   private final int keysPerPage;
   private final boolean singleWriter;
   private final K[] keysBuffer;
   private final V[] valuesBuffer;
   private final Object lock;
   private volatile boolean notificationRequested;
   private volatile boolean closed;
   private boolean readOnly;
   private boolean isVolatile;
   private final AtomicLong avgKeySize;
   private final AtomicLong avgValSize;
   static final long INITIAL_VERSION = -1L;

   protected MVMap(Map<String, Object> var1, DataType<K> var2, DataType<V> var3) {
      this((MVStore)var1.get("store"), var2, var3, DataUtils.readHexInt(var1, "id", 0), DataUtils.readHexLong(var1, "createVersion", 0L), new AtomicReference(), ((MVStore)var1.get("store")).getKeysPerPage(), var1.containsKey("singleWriter") && (Boolean)var1.get("singleWriter"));
      this.setInitialRoot(this.createEmptyLeaf(), this.store.getCurrentVersion());
   }

   protected MVMap(MVMap<K, V> var1) {
      this(var1.store, var1.keyType, var1.valueType, var1.id, var1.createVersion, new AtomicReference(var1.root.get()), var1.keysPerPage, var1.singleWriter);
   }

   MVMap(MVStore var1, int var2, DataType<K> var3, DataType<V> var4) {
      this(var1, var3, var4, var2, 0L, new AtomicReference(), var1.getKeysPerPage(), false);
      this.setInitialRoot(this.createEmptyLeaf(), var1.getCurrentVersion());
   }

   private MVMap(MVStore var1, DataType<K> var2, DataType<V> var3, int var4, long var5, AtomicReference<RootReference<K, V>> var7, int var8, boolean var9) {
      this.lock = new Object();
      this.store = var1;
      this.id = var4;
      this.createVersion = var5;
      this.keyType = var2;
      this.valueType = var3;
      this.root = var7;
      this.keysPerPage = var8;
      this.keysBuffer = var9 ? var2.createStorage(var8) : null;
      this.valuesBuffer = var9 ? var3.createStorage(var8) : null;
      this.singleWriter = var9;
      this.avgKeySize = var2.isMemoryEstimationAllowed() ? new AtomicLong() : null;
      this.avgValSize = var3.isMemoryEstimationAllowed() ? new AtomicLong() : null;
   }

   protected MVMap<K, V> cloneIt() {
      return new MVMap(this);
   }

   static String getMapRootKey(int var0) {
      return "root." + Integer.toHexString(var0);
   }

   static String getMapKey(int var0) {
      return "map." + Integer.toHexString(var0);
   }

   public V put(K var1, V var2) {
      DataUtils.checkArgument(var2 != null, "The value may not be null");
      return this.operate(var1, var2, MVMap.DecisionMaker.PUT);
   }

   public final K firstKey() {
      return this.getFirstLast(true);
   }

   public final K lastKey() {
      return this.getFirstLast(false);
   }

   public final K getKey(long var1) {
      if (var1 >= 0L && var1 < this.sizeAsLong()) {
         Page var3 = this.getRootPage();

         long var4;
         int var6;
         for(var4 = 0L; !var3.isLeaf(); var3 = var3.getChildPage(var6)) {
            var6 = 0;

            int var7;
            for(var7 = this.getChildPageCount(var3); var6 < var7; ++var6) {
               long var8 = var3.getCounts(var6);
               if (var1 < var8 + var4) {
                  break;
               }

               var4 += var8;
            }

            if (var6 == var7) {
               return null;
            }
         }

         if (var1 >= var4 + (long)var3.getKeyCount()) {
            return null;
         } else {
            Object var10 = var3.getKey((int)(var1 - var4));
            return var10;
         }
      } else {
         return null;
      }
   }

   public final List<K> keyList() {
      return new AbstractList<K>() {
         public K get(int var1) {
            return MVMap.this.getKey((long)var1);
         }

         public int size() {
            return MVMap.this.size();
         }

         public int indexOf(Object var1) {
            return (int)MVMap.this.getKeyIndex(var1);
         }
      };
   }

   public final long getKeyIndex(K var1) {
      Page var2 = this.getRootPage();
      if (var2.getTotalCount() == 0L) {
         return -1L;
      } else {
         long var3 = 0L;

         while(true) {
            int var5 = var2.binarySearch(var1);
            if (var2.isLeaf()) {
               if (var5 < 0) {
                  var3 = -var3;
               }

               return var3 + (long)var5;
            }

            if (var5++ < 0) {
               var5 = -var5;
            }

            for(int var6 = 0; var6 < var5; ++var6) {
               var3 += var2.getCounts(var6);
            }

            var2 = var2.getChildPage(var5);
         }
      }
   }

   private K getFirstLast(boolean var1) {
      Page var2 = this.getRootPage();
      return this.getFirstLast(var2, var1);
   }

   private K getFirstLast(Page<K, V> var1, boolean var2) {
      if (var1.getTotalCount() == 0L) {
         return null;
      } else {
         while(!var1.isLeaf()) {
            var1 = var1.getChildPage(var2 ? 0 : this.getChildPageCount(var1) - 1);
         }

         return var1.getKey(var2 ? 0 : var1.getKeyCount() - 1);
      }
   }

   public final K higherKey(K var1) {
      return this.getMinMax(var1, false, true);
   }

   public final K higherKey(RootReference<K, V> var1, K var2) {
      return this.getMinMax(var1, var2, false, true);
   }

   public final K ceilingKey(K var1) {
      return this.getMinMax(var1, false, false);
   }

   public final K floorKey(K var1) {
      return this.getMinMax(var1, true, false);
   }

   public final K lowerKey(K var1) {
      return this.getMinMax(var1, true, true);
   }

   public final K lowerKey(RootReference<K, V> var1, K var2) {
      return this.getMinMax(var1, var2, true, true);
   }

   private K getMinMax(K var1, boolean var2, boolean var3) {
      return this.getMinMax(this.flushAndGetRoot(), var1, var2, var3);
   }

   private K getMinMax(RootReference<K, V> var1, K var2, boolean var3, boolean var4) {
      return this.getMinMax(var1.root, var2, var3, var4);
   }

   private K getMinMax(Page<K, V> var1, K var2, boolean var3, boolean var4) {
      int var5 = var1.binarySearch(var2);
      if (var1.isLeaf()) {
         if (var5 < 0) {
            var5 = -var5 - (var3 ? 2 : 1);
         } else if (var4) {
            var5 += var3 ? -1 : 1;
         }

         return var5 >= 0 && var5 < var1.getKeyCount() ? var1.getKey(var5) : null;
      } else {
         if (var5++ < 0) {
            var5 = -var5;
         }

         while(var5 >= 0 && var5 < this.getChildPageCount(var1)) {
            Object var6 = this.getMinMax(var1.getChildPage(var5), var2, var3, var4);
            if (var6 != null) {
               return var6;
            }

            var5 += var3 ? -1 : 1;
         }

         return null;
      }
   }

   public final V get(Object var1) {
      return this.get(this.getRootPage(), var1);
   }

   public V get(Page<K, V> var1, K var2) {
      return Page.get(var1, var2);
   }

   public final boolean containsKey(Object var1) {
      return this.get(var1) != null;
   }

   public void clear() {
      this.clearIt();
   }

   RootReference<K, V> clearIt() {
      Page var1 = this.createEmptyLeaf();
      int var2 = 0;

      while(true) {
         RootReference var3 = this.flushAndGetRoot();
         if (var3.getTotalCount() == 0L) {
            return var3;
         }

         boolean var4 = var3.isLockedByCurrentThread();
         if (!var4) {
            if (var2++ == 0) {
               this.beforeWrite();
            } else if (var2 > 3 || var3.isLocked()) {
               var3 = this.lockRoot(var3, var2);
               var4 = true;
            }
         }

         Page var5 = var3.root;
         long var6 = var3.version;

         try {
            if (!var4) {
               var3 = var3.updateRootPage(var1, (long)var2);
               if (var3 == null) {
                  continue;
               }
            }

            this.store.registerUnsavedMemory(var5.removeAllRecursive(var6));
            var5 = var1;
            RootReference var8 = var3;
            return var8;
         } finally {
            if (var4) {
               this.unlockRoot(var5);
            }

         }
      }
   }

   final void close() {
      this.closed = true;
   }

   public final boolean isClosed() {
      return this.closed;
   }

   public V remove(Object var1) {
      return this.operate(var1, (Object)null, MVMap.DecisionMaker.REMOVE);
   }

   public final V putIfAbsent(K var1, V var2) {
      return this.operate(var1, var2, MVMap.DecisionMaker.IF_ABSENT);
   }

   public boolean remove(Object var1, Object var2) {
      EqualsDecisionMaker var3 = new EqualsDecisionMaker(this.valueType, var2);
      this.operate(var1, (Object)null, var3);
      return var3.getDecision() != MVMap.Decision.ABORT;
   }

   static <X> boolean areValuesEqual(DataType<X> var0, X var1, X var2) {
      return var1 == var2 || var1 != null && var2 != null && var0.compare(var1, var2) == 0;
   }

   public final boolean replace(K var1, V var2, V var3) {
      EqualsDecisionMaker var4 = new EqualsDecisionMaker(this.valueType, var2);
      Object var5 = this.operate(var1, var3, var4);
      boolean var6 = var4.getDecision() != MVMap.Decision.ABORT;

      assert !var6 || areValuesEqual(this.valueType, var2, var5) : var2 + " != " + var5;

      return var6;
   }

   public final V replace(K var1, V var2) {
      return this.operate(var1, var2, MVMap.DecisionMaker.IF_PRESENT);
   }

   final int compare(K var1, K var2) {
      return this.keyType.compare(var1, var2);
   }

   public final DataType<K> getKeyType() {
      return this.keyType;
   }

   public final DataType<V> getValueType() {
      return this.valueType;
   }

   boolean isSingleWriter() {
      return this.singleWriter;
   }

   final Page<K, V> readPage(long var1) {
      return this.store.readPage(this, var1);
   }

   final void setRootPos(long var1, long var3) {
      Page var5 = this.readOrCreateRootPage(var1);
      if (var5.map != this) {
         assert this.id == var5.map.id;

         var5 = var5.copy(this, false);
      }

      this.setInitialRoot(var5, var3);
      this.setWriteVersion(this.store.getCurrentVersion());
   }

   private Page<K, V> readOrCreateRootPage(long var1) {
      Page var3 = var1 == 0L ? this.createEmptyLeaf() : this.readPage(var1);
      return var3;
   }

   public final Iterator<K> keyIterator(K var1) {
      return this.cursor(var1, (Object)null, false);
   }

   public final Iterator<K> keyIteratorReverse(K var1) {
      return this.cursor(var1, (Object)null, true);
   }

   final boolean rewritePage(long var1) {
      Page var3 = this.readPage(var1);
      if (var3.getKeyCount() == 0) {
         return true;
      } else {
         assert var3.isSaved();

         Object var4 = var3.getKey(0);
         if (!this.isClosed()) {
            RewriteDecisionMaker var5 = new RewriteDecisionMaker(var3.getPos());
            Object var6 = this.operate(var4, (Object)null, var5);
            boolean var7 = var5.getDecision() != MVMap.Decision.ABORT;

            assert !var7 || var6 != null;

            return var7;
         } else {
            return false;
         }
      }
   }

   public final Cursor<K, V> cursor(K var1) {
      return this.cursor(var1, (Object)null, false);
   }

   public final Cursor<K, V> cursor(K var1, K var2, boolean var3) {
      return this.cursor(this.flushAndGetRoot(), var1, var2, var3);
   }

   public Cursor<K, V> cursor(RootReference<K, V> var1, K var2, K var3, boolean var4) {
      return new Cursor(var1, var2, var3, var4);
   }

   public final Set<Map.Entry<K, V>> entrySet() {
      final RootReference var1 = this.flushAndGetRoot();
      return new AbstractSet<Map.Entry<K, V>>() {
         public Iterator<Map.Entry<K, V>> iterator() {
            final Cursor var1x = MVMap.this.cursor(var1, (Object)null, (Object)null, false);
            return new Iterator<Map.Entry<K, V>>() {
               public boolean hasNext() {
                  return var1x.hasNext();
               }

               public Map.Entry<K, V> next() {
                  Object var1xx = var1x.next();
                  return new AbstractMap.SimpleImmutableEntry(var1xx, var1x.getValue());
               }
            };
         }

         public int size() {
            return MVMap.this.size();
         }

         public boolean contains(Object var1x) {
            return MVMap.this.containsKey(var1x);
         }
      };
   }

   public Set<K> keySet() {
      final RootReference var1 = this.flushAndGetRoot();
      return new AbstractSet<K>() {
         public Iterator<K> iterator() {
            return MVMap.this.cursor(var1, (Object)null, (Object)null, false);
         }

         public int size() {
            return MVMap.this.size();
         }

         public boolean contains(Object var1x) {
            return MVMap.this.containsKey(var1x);
         }
      };
   }

   public final String getName() {
      return this.store.getMapName(this.id);
   }

   public final MVStore getStore() {
      return this.store;
   }

   protected final boolean isPersistent() {
      return this.store.getFileStore() != null && !this.isVolatile;
   }

   public final int getId() {
      return this.id;
   }

   public final Page<K, V> getRootPage() {
      return this.flushAndGetRoot().root;
   }

   public RootReference<K, V> getRoot() {
      return (RootReference)this.root.get();
   }

   public RootReference<K, V> flushAndGetRoot() {
      RootReference var1 = this.getRoot();
      return this.singleWriter && var1.getAppendCounter() > 0 ? this.flushAppendBuffer(var1, true) : var1;
   }

   final void setInitialRoot(Page<K, V> var1, long var2) {
      this.root.set(new RootReference(var1, var2));
   }

   final boolean compareAndSetRoot(RootReference<K, V> var1, RootReference<K, V> var2) {
      return this.root.compareAndSet(var1, var2);
   }

   final void rollbackTo(long var1) {
      if (var1 > this.createVersion) {
         this.rollbackRoot(var1);
      }

   }

   boolean rollbackRoot(long var1) {
      RootReference var3 = this.flushAndGetRoot();

      RootReference var4;
      while(var3.version >= var1 && (var4 = var3.previous) != null) {
         if (this.root.compareAndSet(var3, var4)) {
            var3 = var4;
            this.closed = false;
         }
      }

      this.setWriteVersion(var1);
      return var3.version < var1;
   }

   protected static <K, V> boolean updateRoot(RootReference<K, V> var0, Page<K, V> var1, int var2) {
      return var0.updateRootPage(var1, (long)var2) != null;
   }

   private void removeUnusedOldVersions(RootReference<K, V> var1) {
      var1.removeUnusedOldVersions(this.store.getOldestVersionToKeep());
   }

   public final boolean isReadOnly() {
      return this.readOnly;
   }

   public final void setVolatile(boolean var1) {
      this.isVolatile = var1;
   }

   public final boolean isVolatile() {
      return this.isVolatile;
   }

   protected final void beforeWrite() {
      assert !this.getRoot().isLockedByCurrentThread() : this.getRoot();

      if (this.closed) {
         int var1 = this.getId();
         String var2 = this.store.getMapName(var1);
         throw DataUtils.newMVStoreException(4, "Map {0}({1}) is closed. {2}", var2, var1, this.store.getPanicException());
      } else if (this.readOnly) {
         throw DataUtils.newUnsupportedOperationException("This map is read-only");
      } else {
         this.store.beforeWrite(this);
      }
   }

   public final int hashCode() {
      return this.id;
   }

   public final boolean equals(Object var1) {
      return this == var1;
   }

   public final int size() {
      long var1 = this.sizeAsLong();
      return var1 > 2147483647L ? Integer.MAX_VALUE : (int)var1;
   }

   public final long sizeAsLong() {
      return this.getRoot().getTotalCount();
   }

   public boolean isEmpty() {
      return this.sizeAsLong() == 0L;
   }

   final long getCreateVersion() {
      return this.createVersion;
   }

   public final MVMap<K, V> openVersion(long var1) {
      if (this.readOnly) {
         throw DataUtils.newUnsupportedOperationException("This map is read-only; need to call the method on the writable map");
      } else {
         DataUtils.checkArgument(var1 >= this.createVersion, "Unknown version {0}; this map was created in version is {1}", var1, this.createVersion);
         RootReference var3 = this.flushAndGetRoot();
         this.removeUnusedOldVersions(var3);

         RootReference var4;
         while((var4 = var3.previous) != null && var4.version >= var1) {
            var3 = var4;
         }

         if (var4 == null && var1 < this.store.getOldestVersionToKeep()) {
            throw DataUtils.newIllegalArgumentException("Unknown version {0}", var1);
         } else {
            MVMap var5 = this.openReadOnly(var3.root, var1);

            assert var5.getVersion() <= var1 : var5.getVersion() + " <= " + var1;

            return var5;
         }
      }
   }

   final MVMap<K, V> openReadOnly(long var1, long var3) {
      Page var5 = this.readOrCreateRootPage(var1);
      return this.openReadOnly(var5, var3);
   }

   private MVMap<K, V> openReadOnly(Page<K, V> var1, long var2) {
      MVMap var4 = this.cloneIt();
      var4.readOnly = true;
      var4.setInitialRoot(var1, var2);
      return var4;
   }

   public final long getVersion() {
      return this.getRoot().getVersion();
   }

   final boolean hasChangesSince(long var1) {
      return this.getRoot().hasChangesSince(var1, this.isPersistent());
   }

   protected int getChildPageCount(Page<K, V> var1) {
      return var1.getRawChildPageCount();
   }

   public String getType() {
      return null;
   }

   protected String asString(String var1) {
      StringBuilder var2 = new StringBuilder();
      if (var1 != null) {
         DataUtils.appendMap(var2, "name", var1);
      }

      if (this.createVersion != 0L) {
         DataUtils.appendMap(var2, "createVersion", this.createVersion);
      }

      String var3 = this.getType();
      if (var3 != null) {
         DataUtils.appendMap(var2, "type", var3);
      }

      return var2.toString();
   }

   final RootReference<K, V> setWriteVersion(long var1) {
      int var3 = 0;

      while(true) {
         RootReference var4 = this.flushAndGetRoot();
         if (var4.version >= var1) {
            return var4;
         }

         if (this.isClosed() && var4.getVersion() + 1L < this.store.getOldestVersionToKeep()) {
            this.store.deregisterMapRoot(this.id);
            return null;
         }

         RootReference var5 = null;
         ++var3;
         if (var3 > 3 || var4.isLocked()) {
            var5 = this.lockRoot(var4, var3);
            var4 = this.flushAndGetRoot();
         }

         RootReference var6;
         try {
            var4 = var4.tryUnlockAndUpdateVersion(var1, var3);
            if (var4 == null) {
               continue;
            }

            var5 = null;
            this.removeUnusedOldVersions(var4);
            var6 = var4;
         } finally {
            if (var5 != null) {
               this.unlockRoot();
            }

         }

         return var6;
      }
   }

   protected Page<K, V> createEmptyLeaf() {
      return Page.createEmptyLeaf(this);
   }

   protected Page<K, V> createEmptyNode() {
      return Page.createEmptyNode(this);
   }

   final void copyFrom(MVMap<K, V> var1) {
      MVStore.TxCounter var2 = this.store.registerVersionUsage();

      try {
         this.beforeWrite();
         this.copy(var1.getRootPage(), (Page)null, 0);
      } finally {
         this.store.deregisterVersionUsage(var2);
      }

   }

   private void copy(Page<K, V> var1, Page<K, V> var2, int var3) {
      Page var4 = var1.copy(this, true);
      if (var2 == null) {
         this.setInitialRoot(var4, -1L);
      } else {
         var2.setChild(var3, var4);
      }

      if (!var1.isLeaf()) {
         for(int var5 = 0; var5 < this.getChildPageCount(var4); ++var5) {
            if (var1.getChildPagePos(var5) != 0L) {
               this.copy(var1.getChildPage(var5), var4, var5);
            }
         }

         var4.setComplete();
      }

      this.store.registerUnsavedMemory(var4.getMemory());
      if (this.store.isSaveNeeded()) {
         this.store.commit();
      }

   }

   private RootReference<K, V> flushAppendBuffer(RootReference<K, V> var1, boolean var2) {
      boolean var3 = var1.isLockedByCurrentThread();
      boolean var4 = var3;
      int var5 = this.store.getKeysPerPage();

      try {
         IntValueHolder var6 = new IntValueHolder();
         int var7 = 0;
         int var9 = var2 ? 0 : var5 - 1;

         while(true) {
            int var8;
            while((var8 = var1.getAppendCounter()) > var9) {
               if (!var4) {
                  ++var7;
                  var1 = this.tryLock(var1, var7);
                  if (var1 == null) {
                     var1 = this.getRoot();
                     continue;
                  }

                  var4 = true;
               }

               Page var10 = var1.root;
               long var11 = var1.version;
               CursorPos var13 = var10.getAppendCursorPos((CursorPos)null);

               assert var13 != null;

               assert var13.index < 0 : var13.index;

               int var14 = -var13.index - 1;

               assert var14 == var13.page.getKeyCount() : var14 + " != " + var13.page.getKeyCount();

               Page var15 = var13.page;
               CursorPos var16 = var13;
               var13 = var13.parent;
               int var17 = 0;
               Page var18 = null;
               int var19 = var5 - var15.getKeyCount();
               Object[] var21;
               if (var19 > 0) {
                  var15 = var15.copy();
                  if (var8 <= var19) {
                     var15.expand(var8, this.keysBuffer, this.valuesBuffer);
                  } else {
                     var15.expand(var19, this.keysBuffer, this.valuesBuffer);
                     var8 -= var19;
                     if (var2) {
                        Object[] var20 = var15.createKeyStorage(var8);
                        var21 = var15.createValueStorage(var8);
                        System.arraycopy(this.keysBuffer, var19, var20, 0, var8);
                        if (this.valuesBuffer != null) {
                           System.arraycopy(this.valuesBuffer, var19, var21, 0, var8);
                        }

                        var18 = Page.createLeaf(this, var20, var21, 0);
                     } else {
                        System.arraycopy(this.keysBuffer, var19, this.keysBuffer, 0, var8);
                        if (this.valuesBuffer != null) {
                           System.arraycopy(this.valuesBuffer, var19, this.valuesBuffer, 0, var8);
                        }

                        var17 = var8;
                     }
                  }
               } else {
                  var16 = var16.parent;
                  var18 = Page.createLeaf(this, Arrays.copyOf(this.keysBuffer, var8), this.valuesBuffer == null ? null : Arrays.copyOf(this.valuesBuffer, var8), 0);
               }

               var6.value = 0;
               if (var18 != null) {
                  label334: {
                     assert var18.map == this;

                     assert var18.getKeyCount() > 0;

                     Object var26 = var18.getKey(0);

                     for(var6.value += var18.getMemory(); var13 != null; var6.value += var15.getMemory() + var18.getMemory()) {
                        Page var27 = var15;
                        var15 = var13.page;
                        var14 = var13.index;
                        var13 = var13.parent;
                        var15 = var15.copy();
                        var15.setChild(var14, var18);
                        var15.insertNode(var14, var26, var27);
                        var8 = var15.getKeyCount();
                        int var22 = var8 - (var15.isLeaf() ? 1 : 2);
                        if (var8 <= var5 && ((long)var15.getMemory() < this.store.getMaxPageSize() || var22 <= 0)) {
                           break label334;
                        }

                        var26 = var15.getKey(var22);
                        var18 = var15.split(var22);
                     }

                     if (var15.getKeyCount() == 0) {
                        var15 = var18;
                     } else {
                        var21 = var15.createKeyStorage(1);
                        var21[0] = var26;
                        Page.PageReference[] var28 = Page.createRefStorage(2);
                        var28[0] = new Page.PageReference(var15);
                        var28[1] = new Page.PageReference(var18);
                        var6.value += var15.getMemory();
                        var15 = Page.createNode(this, var21, var28, var15.getTotalCount() + var18.getTotalCount(), 0);
                     }
                  }
               }

               var15 = replacePage(var13, var15, var6);
               var1 = var1.updatePageAndLockedStatus(var15, var3 || this.isPersistent(), var17);
               if (var1 != null) {
                  var4 = var3 || this.isPersistent();
                  if (this.isPersistent() && var16 != null) {
                     this.store.registerUnsavedMemory(var6.value + var16.processRemovalInfo(var11));
                  }

                  assert var1.getAppendCounter() <= var9;

                  return var1;
               }

               var1 = this.getRoot();
            }

            return var1;
         }
      } finally {
         if (var4 && !var3) {
            var1 = this.unlockRoot();
         }

      }
   }

   private static <K, V> Page<K, V> replacePage(CursorPos<K, V> var0, Page<K, V> var1, IntValueHolder var2) {
      int var3;
      for(var3 = var1.isSaved() ? 0 : var1.getMemory(); var0 != null; var0 = var0.parent) {
         Page var4 = var0.page;
         if (var4.getKeyCount() > 0) {
            Page var5 = var1;
            var1 = var4.copy();
            var1.setChild(var0.index, var5);
            var3 += var1.getMemory();
         }
      }

      var2.value += var3;
      return var1;
   }

   public void append(K var1, V var2) {
      if (this.singleWriter) {
         this.beforeWrite();
         RootReference var3 = this.lockRoot(this.getRoot(), 1);
         int var4 = var3.getAppendCounter();

         try {
            if (var4 >= this.keysPerPage) {
               var3 = this.flushAppendBuffer(var3, false);
               var4 = var3.getAppendCounter();

               assert var4 < this.keysPerPage;
            }

            this.keysBuffer[var4] = var1;
            if (this.valuesBuffer != null) {
               this.valuesBuffer[var4] = var2;
            }

            ++var4;
         } finally {
            this.unlockRoot(var4);
         }
      } else {
         this.put(var1, var2);
      }

   }

   public void trimLast() {
      if (this.singleWriter) {
         RootReference var1 = this.getRoot();
         int var2 = var1.getAppendCounter();
         boolean var3 = var2 == 0;
         if (!var3) {
            var1 = this.lockRoot(var1, 1);

            try {
               var2 = var1.getAppendCounter();
               var3 = var2 == 0;
               if (!var3) {
                  --var2;
               }
            } finally {
               this.unlockRoot(var2);
            }
         }

         if (var3) {
            Page var4 = var1.root.getAppendCursorPos((CursorPos)null).page;

            assert var4.isLeaf();

            assert var4.getKeyCount() > 0;

            Object var5 = var4.getKey(var4.getKeyCount() - 1);
            this.remove(var5);
         }
      } else {
         this.remove(this.lastKey());
      }

   }

   public final String toString() {
      return this.asString((String)null);
   }

   public V operate(K var1, V var2, DecisionMaker<? super V> var3) {
      IntValueHolder var4 = new IntValueHolder();
      int var5 = 0;

      while(true) {
         RootReference var6 = this.flushAndGetRoot();
         boolean var7 = var6.isLockedByCurrentThread();
         if (!var7) {
            if (var5++ == 0) {
               this.beforeWrite();
            }

            if (var5 > 3 || var6.isLocked()) {
               var6 = this.lockRoot(var6, var5);
               var7 = true;
            }
         }

         Page var8 = var6.root;
         long var9 = var6.version;
         var4.value = 0;

         try {
            CursorPos var13 = CursorPos.traverseDown(var8, var1);
            if (var7 || var6 == this.getRoot()) {
               Page var14 = var13.page;
               int var15 = var13.index;
               CursorPos var11 = var13;
               var13 = var13.parent;
               Object var12 = var15 < 0 ? null : var14.getValue(var15);
               Decision var16 = var3.decide(var12, var2, var11);
               int var17;
               Object var28;
               switch (var16) {
                  case REPEAT:
                     var3.reset();
                     continue;
                  case ABORT:
                     if (!var7 && var6 != this.getRoot()) {
                        var3.reset();
                        continue;
                     }

                     var28 = var12;
                     return var28;
                  case REMOVE:
                     if (var15 < 0) {
                        if (!var7 && var6 != this.getRoot()) {
                           var3.reset();
                           continue;
                        }

                        var28 = null;
                        return var28;
                     }

                     if (var14.getTotalCount() == 1L && var13 != null) {
                        do {
                           var14 = var13.page;
                           var15 = var13.index;
                           var13 = var13.parent;
                           var17 = var14.getKeyCount();
                        } while(var17 == 0 && var13 != null);

                        if (var17 <= 1) {
                           if (var17 == 1) {
                              assert var15 <= 1;

                              var14 = var14.getChildPage(1 - var15);
                           } else {
                              var14 = Page.createEmptyLeaf(this);
                           }
                           break;
                        }
                     }

                     var14 = var14.copy();
                     var14.remove(var15);
                     break;
                  case PUT:
                     var2 = var3.selectValue(var12, var2);
                     var14 = var14.copy();
                     if (var15 < 0) {
                        var14.insertLeaf(-var15 - 1, var1, var2);

                        while((var17 = var14.getKeyCount()) > this.store.getKeysPerPage() || (long)var14.getMemory() > this.store.getMaxPageSize() && var17 > (var14.isLeaf() ? 1 : 2)) {
                           long var18 = var14.getTotalCount();
                           int var20 = var17 >> 1;
                           Object var21 = var14.getKey(var20);
                           Page var22 = var14.split(var20);
                           var4.value += var14.getMemory() + var22.getMemory();
                           if (var13 == null) {
                              Object[] var29 = var14.createKeyStorage(1);
                              var29[0] = var21;
                              Page.PageReference[] var24 = Page.createRefStorage(2);
                              var24[0] = new Page.PageReference(var14);
                              var24[1] = new Page.PageReference(var22);
                              var14 = Page.createNode(this, var29, var24, var18, 0);
                              break;
                           }

                           Page var23 = var14;
                           var14 = var13.page;
                           var15 = var13.index;
                           var13 = var13.parent;
                           var14 = var14.copy();
                           var14.setChild(var15, var22);
                           var14.insertNode(var15, var21, var23);
                        }
                     } else {
                        var14.setValue(var15, var2);
                     }
               }

               var8 = replacePage(var13, var14, var4);
               if (!var7) {
                  var6 = var6.updateRootPage(var8, (long)var5);
                  if (var6 == null) {
                     var3.reset();
                     continue;
                  }
               }

               this.store.registerUnsavedMemory(var4.value + var11.processRemovalInfo(var9));
               var28 = var12;
               return var28;
            }
         } finally {
            if (var7) {
               this.unlockRoot(var8);
            }

         }
      }
   }

   private RootReference<K, V> lockRoot(RootReference<K, V> var1, int var2) {
      while(true) {
         RootReference var3 = this.tryLock(var1, var2++);
         if (var3 != null) {
            return var3;
         }

         var1 = this.getRoot();
      }
   }

   protected RootReference<K, V> tryLock(RootReference<K, V> var1, int var2) {
      RootReference var3 = var1.tryLock(var2);
      if (var3 != null) {
         return var3;
      } else {
         assert !var1.isLockedByCurrentThread() : var1;

         RootReference var4 = var1.previous;
         int var5 = 1;
         if (var4 != null) {
            long var6 = var1.updateAttemptCounter - var4.updateAttemptCounter;

            assert var6 >= 0L : var6;

            long var8 = var1.updateCounter - var4.updateCounter;

            assert var8 >= 0L : var8;

            assert var6 >= var8 : var6 + " >= " + var8;

            var5 += (int)((var6 + 1L) / (var8 + 1L));
         }

         if (var2 > 4) {
            if (var2 <= 12) {
               Thread.yield();
            } else if (var2 <= 70 - 2 * var5) {
               try {
                  Thread.sleep((long)var5);
               } catch (InterruptedException var13) {
                  throw new RuntimeException(var13);
               }
            } else {
               synchronized(this.lock) {
                  this.notificationRequested = true;

                  try {
                     this.lock.wait(5L);
                  } catch (InterruptedException var11) {
                  }
               }
            }
         }

         return null;
      }
   }

   private RootReference<K, V> unlockRoot() {
      return this.unlockRoot((Page)null, -1);
   }

   protected RootReference<K, V> unlockRoot(Page<K, V> var1) {
      return this.unlockRoot(var1, -1);
   }

   private void unlockRoot(int var1) {
      this.unlockRoot((Page)null, var1);
   }

   private RootReference<K, V> unlockRoot(Page<K, V> var1, int var2) {
      RootReference var3;
      do {
         RootReference var4 = this.getRoot();

         assert var4.isLockedByCurrentThread();

         var3 = var4.updatePageAndLockedStatus(var1 == null ? var4.root : var1, false, var2 == -1 ? var4.getAppendCounter() : var2);
      } while(var3 == null);

      this.notifyWaiters();
      return var3;
   }

   private void notifyWaiters() {
      if (this.notificationRequested) {
         synchronized(this.lock) {
            this.notificationRequested = false;
            this.lock.notify();
         }
      }

   }

   final boolean isMemoryEstimationAllowed() {
      return this.avgKeySize != null || this.avgValSize != null;
   }

   final int evaluateMemoryForKeys(K[] var1, int var2) {
      return this.avgKeySize == null ? calculateMemory(this.keyType, var1, var2) : MemoryEstimator.estimateMemory(this.avgKeySize, this.keyType, var1, var2);
   }

   final int evaluateMemoryForValues(V[] var1, int var2) {
      return this.avgValSize == null ? calculateMemory(this.valueType, var1, var2) : MemoryEstimator.estimateMemory(this.avgValSize, this.valueType, var1, var2);
   }

   private static <T> int calculateMemory(DataType<T> var0, T[] var1, int var2) {
      int var3 = var2 * 8;

      for(int var4 = 0; var4 < var2; ++var4) {
         var3 += var0.getMemory(var1[var4]);
      }

      return var3;
   }

   final int evaluateMemoryForKey(K var1) {
      return this.avgKeySize == null ? this.keyType.getMemory(var1) : MemoryEstimator.estimateMemory(this.avgKeySize, this.keyType, var1);
   }

   final int evaluateMemoryForValue(V var1) {
      return this.avgValSize == null ? this.valueType.getMemory(var1) : MemoryEstimator.estimateMemory(this.avgValSize, this.valueType, var1);
   }

   static int samplingPct(AtomicLong var0) {
      return MemoryEstimator.samplingPct(var0);
   }

   private static final class IntValueHolder {
      int value;

      IntValueHolder() {
      }
   }

   private static final class RewriteDecisionMaker<V> extends DecisionMaker<V> {
      private final long pagePos;
      private Decision decision;

      RewriteDecisionMaker(long var1) {
         this.pagePos = var1;
      }

      public Decision decide(V var1, V var2, CursorPos<?, ?> var3) {
         assert this.decision == null;

         this.decision = MVMap.Decision.ABORT;
         if (!DataUtils.isLeafPosition(this.pagePos)) {
            while((var3 = var3.parent) != null) {
               if (var3.page.getPos() == this.pagePos) {
                  this.decision = this.decide(var1, var2);
                  break;
               }
            }
         } else if (var3.page.getPos() == this.pagePos) {
            this.decision = this.decide(var1, var2);
         }

         return this.decision;
      }

      public Decision decide(V var1, V var2) {
         this.decision = var1 == null ? MVMap.Decision.ABORT : MVMap.Decision.PUT;
         return this.decision;
      }

      public <T extends V> T selectValue(T var1, T var2) {
         return var1;
      }

      public void reset() {
         this.decision = null;
      }

      Decision getDecision() {
         return this.decision;
      }

      public String toString() {
         return "rewrite";
      }
   }

   private static final class EqualsDecisionMaker<V> extends DecisionMaker<V> {
      private final DataType<V> dataType;
      private final V expectedValue;
      private Decision decision;

      EqualsDecisionMaker(DataType<V> var1, V var2) {
         this.dataType = var1;
         this.expectedValue = var2;
      }

      public Decision decide(V var1, V var2) {
         assert this.decision == null;

         this.decision = !MVMap.areValuesEqual(this.dataType, this.expectedValue, var1) ? MVMap.Decision.ABORT : (var2 == null ? MVMap.Decision.REMOVE : MVMap.Decision.PUT);
         return this.decision;
      }

      public void reset() {
         this.decision = null;
      }

      Decision getDecision() {
         return this.decision;
      }

      public String toString() {
         return "equals_to " + this.expectedValue;
      }
   }

   public abstract static class DecisionMaker<V> {
      public static final DecisionMaker<Object> DEFAULT = new DecisionMaker<Object>() {
         public Decision decide(Object var1, Object var2) {
            return var2 == null ? MVMap.Decision.REMOVE : MVMap.Decision.PUT;
         }

         public String toString() {
            return "default";
         }
      };
      public static final DecisionMaker<Object> PUT = new DecisionMaker<Object>() {
         public Decision decide(Object var1, Object var2) {
            return MVMap.Decision.PUT;
         }

         public String toString() {
            return "put";
         }
      };
      public static final DecisionMaker<Object> REMOVE = new DecisionMaker<Object>() {
         public Decision decide(Object var1, Object var2) {
            return MVMap.Decision.REMOVE;
         }

         public String toString() {
            return "remove";
         }
      };
      static final DecisionMaker<Object> IF_ABSENT = new DecisionMaker<Object>() {
         public Decision decide(Object var1, Object var2) {
            return var1 == null ? MVMap.Decision.PUT : MVMap.Decision.ABORT;
         }

         public String toString() {
            return "if_absent";
         }
      };
      static final DecisionMaker<Object> IF_PRESENT = new DecisionMaker<Object>() {
         public Decision decide(Object var1, Object var2) {
            return var1 != null ? MVMap.Decision.PUT : MVMap.Decision.ABORT;
         }

         public String toString() {
            return "if_present";
         }
      };

      public Decision decide(V var1, V var2, CursorPos<?, ?> var3) {
         return this.decide(var1, var2);
      }

      public abstract Decision decide(V var1, V var2);

      public <T extends V> T selectValue(T var1, T var2) {
         return var2;
      }

      public void reset() {
      }
   }

   public static enum Decision {
      ABORT,
      REMOVE,
      PUT,
      REPEAT;
   }

   public static class Builder<K, V> extends BasicBuilder<MVMap<K, V>, K, V> {
      private boolean singleWriter;

      public Builder<K, V> keyType(DataType<? super K> var1) {
         this.setKeyType(var1);
         return this;
      }

      public Builder<K, V> valueType(DataType<? super V> var1) {
         this.setValueType(var1);
         return this;
      }

      public Builder<K, V> singleWriter() {
         this.singleWriter = true;
         return this;
      }

      protected MVMap<K, V> create(Map<String, Object> var1) {
         var1.put("singleWriter", this.singleWriter);
         Object var2 = var1.get("type");
         if (var2 != null && !var2.equals("rtree")) {
            throw new IllegalArgumentException("Incompatible map type");
         } else {
            return new MVMap(var1, this.getKeyType(), this.getValueType());
         }
      }
   }

   public abstract static class BasicBuilder<M extends MVMap<K, V>, K, V> implements MapBuilder<M, K, V> {
      private DataType<K> keyType;
      private DataType<V> valueType;

      protected BasicBuilder() {
      }

      public DataType<K> getKeyType() {
         return this.keyType;
      }

      public DataType<V> getValueType() {
         return this.valueType;
      }

      public void setKeyType(DataType<? super K> var1) {
         this.keyType = var1;
      }

      public void setValueType(DataType<? super V> var1) {
         this.valueType = var1;
      }

      public BasicBuilder<M, K, V> keyType(DataType<? super K> var1) {
         this.setKeyType(var1);
         return this;
      }

      public BasicBuilder<M, K, V> valueType(DataType<? super V> var1) {
         this.setValueType(var1);
         return this;
      }

      public M create(MVStore var1, Map<String, Object> var2) {
         if (this.getKeyType() == null) {
            this.setKeyType(new ObjectDataType());
         }

         if (this.getValueType() == null) {
            this.setValueType(new ObjectDataType());
         }

         DataType var3 = this.getKeyType();
         DataType var4 = this.getValueType();
         var2.put("store", var1);
         var2.put("key", var3);
         var2.put("val", var4);
         return this.create(var2);
      }

      protected abstract M create(Map<String, Object> var1);
   }

   public interface MapBuilder<M extends MVMap<K, V>, K, V> {
      M create(MVStore var1, Map<String, Object> var2);

      DataType<K> getKeyType();

      DataType<V> getValueType();

      void setKeyType(DataType<? super K> var1);

      void setValueType(DataType<? super V> var1);
   }
}
