package org.h2.mvstore;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import org.h2.compress.Compressor;
import org.h2.util.Utils;

public abstract class Page<K, V> implements Cloneable {
   public final MVMap<K, V> map;
   private volatile long pos;
   public int pageNo;
   private int cachedCompare;
   private int memory;
   private int diskSpaceUsed;
   private K[] keys;
   private static final AtomicLongFieldUpdater<Page> posUpdater = AtomicLongFieldUpdater.newUpdater(Page.class, "pos");
   static final int PAGE_MEMORY_CHILD = 24;
   private static final int PAGE_MEMORY = 81;
   static final int PAGE_NODE_MEMORY = 121;
   static final int PAGE_LEAF_MEMORY = 113;
   private static final int IN_MEMORY = Integer.MIN_VALUE;
   private static final PageReference[] SINGLE_EMPTY;

   Page(MVMap<K, V> var1) {
      this.pageNo = -1;
      this.map = var1;
   }

   Page(MVMap<K, V> var1, Page<K, V> var2) {
      this(var1, var2.keys);
      this.memory = var2.memory;
   }

   Page(MVMap<K, V> var1, K[] var2) {
      this.pageNo = -1;
      this.map = var1;
      this.keys = var2;
   }

   static <K, V> Page<K, V> createEmptyLeaf(MVMap<K, V> var0) {
      return createLeaf(var0, var0.getKeyType().createStorage(0), var0.getValueType().createStorage(0), 113);
   }

   static <K, V> Page<K, V> createEmptyNode(MVMap<K, V> var0) {
      return createNode(var0, var0.getKeyType().createStorage(0), SINGLE_EMPTY, 0L, 153);
   }

   public static <K, V> Page<K, V> createNode(MVMap<K, V> var0, K[] var1, PageReference<K, V>[] var2, long var3, int var5) {
      assert var1 != null;

      NonLeaf var6 = new NonLeaf(var0, var1, var2, var3);
      var6.initMemoryAccount(var5);
      return var6;
   }

   static <K, V> Page<K, V> createLeaf(MVMap<K, V> var0, K[] var1, V[] var2, int var3) {
      assert var1 != null;

      Leaf var4 = new Leaf(var0, var1, var2);
      var4.initMemoryAccount(var3);
      return var4;
   }

   private void initMemoryAccount(int var1) {
      if (!this.map.isPersistent()) {
         this.memory = Integer.MIN_VALUE;
      } else if (var1 == 0) {
         this.recalculateMemory();
      } else {
         this.addMemory(var1);

         assert var1 == this.getMemory();
      }

   }

   static <K, V> V get(Page<K, V> var0, K var1) {
      while(true) {
         int var2 = var0.binarySearch(var1);
         if (var0.isLeaf()) {
            return var2 >= 0 ? var0.getValue(var2) : null;
         }

         if (var2++ < 0) {
            var2 = -var2;
         }

         var0 = var0.getChildPage(var2);
      }
   }

   static <K, V> Page<K, V> read(ByteBuffer var0, long var1, MVMap<K, V> var3) {
      boolean var4 = (DataUtils.getPageType(var1) & 1) == 0;
      Object var5 = var4 ? new Leaf(var3) : new NonLeaf(var3);
      ((Page)var5).pos = var1;
      ((Page)var5).read(var0);
      return (Page)var5;
   }

   public final int getMapId() {
      return this.map.getId();
   }

   abstract Page<K, V> copy(MVMap<K, V> var1, boolean var2);

   public K getKey(int var1) {
      return this.keys[var1];
   }

   public abstract Page<K, V> getChildPage(int var1);

   public abstract long getChildPagePos(int var1);

   public abstract V getValue(int var1);

   public final int getKeyCount() {
      return this.keys.length;
   }

   public final boolean isLeaf() {
      return this.getNodeType() == 0;
   }

   public abstract int getNodeType();

   public final long getPos() {
      return this.pos;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      this.dump(var1);
      return var1.toString();
   }

   protected void dump(StringBuilder var1) {
      var1.append("id: ").append(System.identityHashCode(this)).append('\n');
      var1.append("pos: ").append(Long.toHexString(this.pos)).append('\n');
      if (this.isSaved()) {
         int var2 = DataUtils.getPageChunkId(this.pos);
         var1.append("chunk: ").append(Long.toHexString((long)var2)).append('\n');
      }

   }

   public final Page<K, V> copy() {
      Page var1 = this.clone();
      var1.pos = 0L;
      return var1;
   }

   protected final Page<K, V> clone() {
      try {
         Page var1 = (Page)super.clone();
         return var1;
      } catch (CloneNotSupportedException var3) {
         throw new RuntimeException(var3);
      }
   }

   int binarySearch(K var1) {
      int var2 = this.map.getKeyType().binarySearch(var1, this.keys, this.getKeyCount(), this.cachedCompare);
      this.cachedCompare = var2 < 0 ? ~var2 : var2 + 1;
      return var2;
   }

   abstract Page<K, V> split(int var1);

   final K[] splitKeys(int var1, int var2) {
      assert var1 + var2 <= this.getKeyCount();

      Object[] var3 = this.createKeyStorage(var1);
      Object[] var4 = this.createKeyStorage(var2);
      System.arraycopy(this.keys, 0, var3, 0, var1);
      System.arraycopy(this.keys, this.getKeyCount() - var2, var4, 0, var2);
      this.keys = var3;
      return var4;
   }

   abstract void expand(int var1, K[] var2, V[] var3);

   final void expandKeys(int var1, K[] var2) {
      int var3 = this.getKeyCount();
      Object[] var4 = this.createKeyStorage(var3 + var1);
      System.arraycopy(this.keys, 0, var4, 0, var3);
      System.arraycopy(var2, 0, var4, var3, var1);
      this.keys = var4;
   }

   public abstract long getTotalCount();

   abstract long getCounts(int var1);

   public abstract void setChild(int var1, Page<K, V> var2);

   public final void setKey(int var1, K var2) {
      this.keys = (Object[])this.keys.clone();
      if (this.isPersistent()) {
         Object var3 = this.keys[var1];
         if (!this.map.isMemoryEstimationAllowed() || var3 == null) {
            int var4 = this.map.evaluateMemoryForKey(var2);
            if (var3 != null) {
               var4 -= this.map.evaluateMemoryForKey(var3);
            }

            this.addMemory(var4);
         }
      }

      this.keys[var1] = var2;
   }

   public abstract V setValue(int var1, V var2);

   public abstract void insertLeaf(int var1, K var2, V var3);

   public abstract void insertNode(int var1, K var2, Page<K, V> var3);

   final void insertKey(int var1, K var2) {
      int var3 = this.getKeyCount();

      assert var1 <= var3 : var1 + " > " + var3;

      Object[] var4 = this.createKeyStorage(var3 + 1);
      DataUtils.copyWithGap(this.keys, var4, var3, var1);
      this.keys = var4;
      this.keys[var1] = var2;
      if (this.isPersistent()) {
         this.addMemory(8 + this.map.evaluateMemoryForKey(var2));
      }

   }

   public void remove(int var1) {
      int var2 = this.getKeyCount();
      if (var1 == var2) {
         --var1;
      }

      if (this.isPersistent() && !this.map.isMemoryEstimationAllowed()) {
         Object var3 = this.getKey(var1);
         this.addMemory(-8 - this.map.evaluateMemoryForKey(var3));
      }

      Object[] var4 = this.createKeyStorage(var2 - 1);
      DataUtils.copyExcept(this.keys, var4, var2, var1);
      this.keys = var4;
   }

   private void read(ByteBuffer var1) {
      int var2 = DataUtils.getPageChunkId(this.pos);
      int var3 = DataUtils.getPageOffset(this.pos);
      int var4 = var1.position();
      int var5 = var1.getInt();
      int var6 = var1.remaining() + 4;
      if (var5 <= var6 && var5 >= 4) {
         short var7 = var1.getShort();
         int var8 = DataUtils.getCheckValue(var2) ^ DataUtils.getCheckValue(var3) ^ DataUtils.getCheckValue(var5);
         if (var7 != (short)var8) {
            throw DataUtils.newMVStoreException(6, "File corrupted in chunk {0}, expected check value {1}, got {2}", var2, var8, var7);
         } else {
            this.pageNo = DataUtils.readVarInt(var1);
            if (this.pageNo < 0) {
               throw DataUtils.newMVStoreException(6, "File corrupted in chunk {0}, got negative page No {1}", var2, this.pageNo);
            } else {
               int var9 = DataUtils.readVarInt(var1);
               if (var9 != this.map.getId()) {
                  throw DataUtils.newMVStoreException(6, "File corrupted in chunk {0}, expected map id {1}, got {2}", var2, this.map.getId(), var9);
               } else {
                  int var10 = DataUtils.readVarInt(var1);
                  this.keys = this.createKeyStorage(var10);
                  byte var11 = var1.get();
                  if (this.isLeaf() != ((var11 & 1) == 0)) {
                     throw DataUtils.newMVStoreException(6, "File corrupted in chunk {0}, expected node type {1}, got {2}", var2, this.isLeaf() ? "0" : "1", Integer.valueOf(var11));
                  } else {
                     var1.limit(var4 + var5);
                     if (!this.isLeaf()) {
                        this.readPayLoad(var1);
                     }

                     boolean var12 = (var11 & 2) != 0;
                     if (var12) {
                        Compressor var13;
                        if ((var11 & 6) == 6) {
                           var13 = this.map.getStore().getCompressorHigh();
                        } else {
                           var13 = this.map.getStore().getCompressorFast();
                        }

                        int var14 = DataUtils.readVarInt(var1);
                        int var15 = var1.remaining();
                        int var17 = 0;
                        byte[] var16;
                        if (var1.hasArray()) {
                           var16 = var1.array();
                           var17 = var1.arrayOffset() + var1.position();
                        } else {
                           var16 = Utils.newBytes(var15);
                           var1.get(var16);
                        }

                        int var18 = var15 + var14;
                        var1 = ByteBuffer.allocate(var18);
                        var13.expand(var16, var17, var15, var1.array(), var1.arrayOffset(), var18);
                     }

                     this.map.getKeyType().read(var1, this.keys, var10);
                     if (this.isLeaf()) {
                        this.readPayLoad(var1);
                     }

                     this.diskSpaceUsed = var5;
                     this.recalculateMemory();
                  }
               }
            }
         }
      } else {
         throw DataUtils.newMVStoreException(6, "File corrupted in chunk {0}, expected page length 4..{1}, got {2}", var2, var6, var5);
      }
   }

   protected abstract void readPayLoad(ByteBuffer var1);

   public final boolean isSaved() {
      return DataUtils.isPageSaved(this.pos);
   }

   public final boolean isRemoved() {
      return DataUtils.isPageRemoved(this.pos);
   }

   private boolean markAsRemoved() {
      assert this.getTotalCount() > 0L : this;

      do {
         long var1 = this.pos;
         if (DataUtils.isPageSaved(var1)) {
            return false;
         }

         assert !DataUtils.isPageRemoved(var1);
      } while(!posUpdater.compareAndSet(this, 0L, 1L));

      return true;
   }

   protected final int write(Chunk var1, WriteBuffer var2, List<Long> var3) {
      this.pageNo = var3.size();
      int var4 = this.getKeyCount();
      int var5 = var2.position();
      var2.putInt(0).putShort((short)0).putVarInt(this.pageNo).putVarInt(this.map.getId()).putVarInt(var4);
      int var6 = var2.position();
      int var7 = this.isLeaf() ? 0 : 1;
      var2.put((byte)var7);
      int var8 = var2.position();
      this.writeChildren(var2, true);
      int var9 = var2.position();
      this.map.getKeyType().write(var2, this.keys, var4);
      this.writeValues(var2);
      MVStore var10 = this.map.getStore();
      int var11 = var2.position() - var9;
      int var12;
      int var20;
      if (var11 > 16) {
         var12 = var10.getCompressionLevel();
         if (var12 > 0) {
            Compressor var13;
            byte var14;
            if (var12 == 1) {
               var13 = var10.getCompressorFast();
               var14 = 2;
            } else {
               var13 = var10.getCompressorHigh();
               var14 = 6;
            }

            byte[] var15 = new byte[var11 * 2];
            ByteBuffer var16 = var2.getBuffer();
            int var17 = 0;
            byte[] var18;
            if (var16.hasArray()) {
               var18 = var16.array();
               var17 = var16.arrayOffset() + var9;
            } else {
               var18 = Utils.newBytes(var11);
               var2.position(var9).get(var18);
            }

            int var19 = var13.compress(var18, var17, var11, var15, 0);
            var20 = DataUtils.getVarIntLen(var11 - var19);
            if (var19 + var20 < var11) {
               var2.position(var6).put((byte)(var7 | var14));
               var2.position(var9).putVarInt(var11 - var19).put(var15, 0, var19);
            }
         }
      }

      var12 = var2.position() - var5;
      long var22 = DataUtils.getTocElement(this.getMapId(), var5, var2.position() - var5, var7);
      var3.add(var22);
      int var23 = var1.id;
      int var24 = DataUtils.getCheckValue(var23) ^ DataUtils.getCheckValue(var5) ^ DataUtils.getCheckValue(var12);
      var2.putInt(var5, var12).putShort(var5 + 4, (short)var24);
      if (this.isSaved()) {
         throw DataUtils.newMVStoreException(3, "Page already stored");
      } else {
         long var25 = DataUtils.getPagePos(var23, var22);

         boolean var26;
         for(var26 = this.isRemoved(); !posUpdater.compareAndSet(this, var26 ? 1L : 0L, var25); var26 = this.isRemoved()) {
         }

         var10.cachePage(this);
         if (var7 == 1) {
            var10.cachePage(this);
         }

         var20 = DataUtils.getPageMaxLength(this.pos);
         boolean var21 = this.map.isSingleWriter();
         var1.accountForWrittenPage(var20, var21);
         if (var26) {
            var10.accountForRemovedPage(var25, var1.version + 1L, var21, this.pageNo);
         }

         this.diskSpaceUsed = var20 != 2097152 ? var20 : var12;
         return var8;
      }
   }

   protected abstract void writeValues(WriteBuffer var1);

   protected abstract void writeChildren(WriteBuffer var1, boolean var2);

   abstract void writeUnsavedRecursive(Chunk var1, WriteBuffer var2, List<Long> var3);

   abstract void releaseSavedPages();

   public abstract int getRawChildPageCount();

   protected final boolean isPersistent() {
      return this.memory != Integer.MIN_VALUE;
   }

   public final int getMemory() {
      return this.isPersistent() ? this.memory : 0;
   }

   public long getDiskSpaceUsed() {
      long var1 = 0L;
      if (this.isPersistent()) {
         var1 += (long)this.diskSpaceUsed;
         if (!this.isLeaf()) {
            for(int var3 = 0; var3 < this.getRawChildPageCount(); ++var3) {
               long var4 = this.getChildPagePos(var3);
               if (var4 != 0L) {
                  var1 += this.getChildPage(var3).getDiskSpaceUsed();
               }
            }
         }
      }

      return var1;
   }

   final void addMemory(int var1) {
      this.memory += var1;

      assert this.memory >= 0;

   }

   final void recalculateMemory() {
      assert this.isPersistent();

      this.memory = this.calculateMemory();
   }

   protected int calculateMemory() {
      return this.map.evaluateMemoryForKeys(this.keys, this.getKeyCount());
   }

   public boolean isComplete() {
      return true;
   }

   public void setComplete() {
   }

   public final int removePage(long var1) {
      if (this.isPersistent() && this.getTotalCount() > 0L) {
         MVStore var3 = this.map.store;
         if (this.markAsRemoved()) {
            return -this.memory;
         }

         long var4 = this.pos;
         var3.accountForRemovedPage(var4, var1, this.map.isSingleWriter(), this.pageNo);
      }

      return 0;
   }

   public abstract CursorPos<K, V> getPrependCursorPos(CursorPos<K, V> var1);

   public abstract CursorPos<K, V> getAppendCursorPos(CursorPos<K, V> var1);

   public abstract int removeAllRecursive(long var1);

   public final K[] createKeyStorage(int var1) {
      return this.map.getKeyType().createStorage(var1);
   }

   final V[] createValueStorage(int var1) {
      return this.map.getValueType().createStorage(var1);
   }

   public static <K, V> PageReference<K, V>[] createRefStorage(int var0) {
      return new PageReference[var0];
   }

   static {
      SINGLE_EMPTY = new PageReference[]{Page.PageReference.EMPTY};
   }

   private static class Leaf<K, V> extends Page<K, V> {
      private V[] values;

      Leaf(MVMap<K, V> var1) {
         super(var1);
      }

      private Leaf(MVMap<K, V> var1, Leaf<K, V> var2) {
         super(var1, (Page)var2);
         this.values = var2.values;
      }

      Leaf(MVMap<K, V> var1, K[] var2, V[] var3) {
         super(var1, var2);
         this.values = var3;
      }

      public int getNodeType() {
         return 0;
      }

      public Page<K, V> copy(MVMap<K, V> var1, boolean var2) {
         return new Leaf(var1, this);
      }

      public Page<K, V> getChildPage(int var1) {
         throw new UnsupportedOperationException();
      }

      public long getChildPagePos(int var1) {
         throw new UnsupportedOperationException();
      }

      public V getValue(int var1) {
         return this.values == null ? null : this.values[var1];
      }

      public Page<K, V> split(int var1) {
         assert !this.isSaved();

         int var2 = this.getKeyCount() - var1;
         Object[] var3 = this.splitKeys(var1, var2);
         Object[] var4 = this.createValueStorage(var2);
         if (this.values != null) {
            Object[] var5 = this.createValueStorage(var1);
            System.arraycopy(this.values, 0, var5, 0, var1);
            System.arraycopy(this.values, var1, var4, 0, var2);
            this.values = var5;
         }

         Page var6 = createLeaf(this.map, var3, var4, 0);
         if (this.isPersistent()) {
            this.recalculateMemory();
         }

         return var6;
      }

      public void expand(int var1, K[] var2, V[] var3) {
         int var4 = this.getKeyCount();
         this.expandKeys(var1, var2);
         if (this.values != null) {
            Object[] var5 = this.createValueStorage(var4 + var1);
            System.arraycopy(this.values, 0, var5, 0, var4);
            System.arraycopy(var3, 0, var5, var4, var1);
            this.values = var5;
         }

         if (this.isPersistent()) {
            this.recalculateMemory();
         }

      }

      public long getTotalCount() {
         return (long)this.getKeyCount();
      }

      long getCounts(int var1) {
         throw new UnsupportedOperationException();
      }

      public void setChild(int var1, Page<K, V> var2) {
         throw new UnsupportedOperationException();
      }

      public V setValue(int var1, V var2) {
         this.values = (Object[])this.values.clone();
         Object var3 = this.setValueInternal(var1, var2);
         if (this.isPersistent() && !this.map.isMemoryEstimationAllowed()) {
            this.addMemory(this.map.evaluateMemoryForValue(var2) - this.map.evaluateMemoryForValue(var3));
         }

         return var3;
      }

      private V setValueInternal(int var1, V var2) {
         Object var3 = this.values[var1];
         this.values[var1] = var2;
         return var3;
      }

      public void insertLeaf(int var1, K var2, V var3) {
         int var4 = this.getKeyCount();
         this.insertKey(var1, var2);
         if (this.values != null) {
            Object[] var5 = this.createValueStorage(var4 + 1);
            DataUtils.copyWithGap(this.values, var5, var4, var1);
            this.values = var5;
            this.setValueInternal(var1, var3);
            if (this.isPersistent()) {
               this.addMemory(8 + this.map.evaluateMemoryForValue(var3));
            }
         }

      }

      public void insertNode(int var1, K var2, Page<K, V> var3) {
         throw new UnsupportedOperationException();
      }

      public void remove(int var1) {
         int var2 = this.getKeyCount();
         super.remove(var1);
         if (this.values != null) {
            if (this.isPersistent()) {
               if (this.map.isMemoryEstimationAllowed()) {
                  this.addMemory(-this.getMemory() / var2);
               } else {
                  Object var3 = this.getValue(var1);
                  this.addMemory(-8 - this.map.evaluateMemoryForValue(var3));
               }
            }

            Object[] var4 = this.createValueStorage(var2 - 1);
            DataUtils.copyExcept(this.values, var4, var2, var1);
            this.values = var4;
         }

      }

      public int removeAllRecursive(long var1) {
         return this.removePage(var1);
      }

      public CursorPos<K, V> getPrependCursorPos(CursorPos<K, V> var1) {
         return new CursorPos(this, -1, var1);
      }

      public CursorPos<K, V> getAppendCursorPos(CursorPos<K, V> var1) {
         int var2 = this.getKeyCount();
         return new CursorPos(this, ~var2, var1);
      }

      protected void readPayLoad(ByteBuffer var1) {
         int var2 = this.getKeyCount();
         this.values = this.createValueStorage(var2);
         this.map.getValueType().read(var1, this.values, this.getKeyCount());
      }

      protected void writeValues(WriteBuffer var1) {
         this.map.getValueType().write(var1, this.values, this.getKeyCount());
      }

      protected void writeChildren(WriteBuffer var1, boolean var2) {
      }

      void writeUnsavedRecursive(Chunk var1, WriteBuffer var2, List<Long> var3) {
         if (!this.isSaved()) {
            this.write(var1, var2, var3);
         }

      }

      void releaseSavedPages() {
      }

      public int getRawChildPageCount() {
         return 0;
      }

      protected int calculateMemory() {
         return super.calculateMemory() + 113 + (this.values == null ? 0 : this.map.evaluateMemoryForValues(this.values, this.getKeyCount()));
      }

      public void dump(StringBuilder var1) {
         super.dump(var1);
         int var2 = this.getKeyCount();

         for(int var3 = 0; var3 < var2; ++var3) {
            if (var3 > 0) {
               var1.append(" ");
            }

            var1.append(this.getKey(var3));
            if (this.values != null) {
               var1.append(':');
               var1.append(this.getValue(var3));
            }
         }

      }
   }

   private static class IncompleteNonLeaf<K, V> extends NonLeaf<K, V> {
      private boolean complete;

      IncompleteNonLeaf(MVMap<K, V> var1, NonLeaf<K, V> var2) {
         super(var1, var2, constructEmptyPageRefs(var2.getRawChildPageCount()), var2.getTotalCount());
      }

      private static <K, V> PageReference<K, V>[] constructEmptyPageRefs(int var0) {
         PageReference[] var1 = createRefStorage(var0);
         Arrays.fill(var1, Page.PageReference.empty());
         return var1;
      }

      void writeUnsavedRecursive(Chunk var1, WriteBuffer var2, List<Long> var3) {
         if (this.complete) {
            super.writeUnsavedRecursive(var1, var2, var3);
         } else if (!this.isSaved()) {
            this.writeChildrenRecursive(var1, var2, var3);
         }

      }

      public boolean isComplete() {
         return this.complete;
      }

      public void setComplete() {
         this.recalculateTotalCount();
         this.complete = true;
      }

      public void dump(StringBuilder var1) {
         super.dump(var1);
         var1.append(", complete:").append(this.complete);
      }
   }

   private static class NonLeaf<K, V> extends Page<K, V> {
      private PageReference<K, V>[] children;
      private long totalCount;

      NonLeaf(MVMap<K, V> var1) {
         super(var1);
      }

      NonLeaf(MVMap<K, V> var1, NonLeaf<K, V> var2, PageReference<K, V>[] var3, long var4) {
         super(var1, (Page)var2);
         this.children = var3;
         this.totalCount = var4;
      }

      NonLeaf(MVMap<K, V> var1, K[] var2, PageReference<K, V>[] var3, long var4) {
         super(var1, var2);
         this.children = var3;
         this.totalCount = var4;
      }

      public int getNodeType() {
         return 1;
      }

      public Page<K, V> copy(MVMap<K, V> var1, boolean var2) {
         return (Page)(var2 ? new IncompleteNonLeaf(var1, this) : new NonLeaf(var1, this, this.children, this.totalCount));
      }

      public Page<K, V> getChildPage(int var1) {
         PageReference var2 = this.children[var1];
         Page var3 = var2.getPage();
         if (var3 == null) {
            var3 = this.map.readPage(var2.getPos());

            assert var2.getPos() == var3.getPos();

            assert var2.count == var3.getTotalCount();
         }

         return var3;
      }

      public long getChildPagePos(int var1) {
         return this.children[var1].getPos();
      }

      public V getValue(int var1) {
         throw new UnsupportedOperationException();
      }

      public Page<K, V> split(int var1) {
         assert !this.isSaved();

         int var2 = this.getKeyCount() - var1;
         Object[] var3 = this.splitKeys(var1, var2 - 1);
         PageReference[] var4 = createRefStorage(var1 + 1);
         PageReference[] var5 = createRefStorage(var2);
         System.arraycopy(this.children, 0, var4, 0, var1 + 1);
         System.arraycopy(this.children, var1 + 1, var5, 0, var2);
         this.children = var4;
         long var6 = 0L;
         PageReference[] var8 = var4;
         int var9 = var4.length;

         int var10;
         PageReference var11;
         for(var10 = 0; var10 < var9; ++var10) {
            var11 = var8[var10];
            var6 += var11.count;
         }

         this.totalCount = var6;
         var6 = 0L;
         var8 = var5;
         var9 = var5.length;

         for(var10 = 0; var10 < var9; ++var10) {
            var11 = var8[var10];
            var6 += var11.count;
         }

         Page var12 = createNode(this.map, var3, var5, var6, 0);
         if (this.isPersistent()) {
            this.recalculateMemory();
         }

         return var12;
      }

      public void expand(int var1, Object[] var2, Object[] var3) {
         throw new UnsupportedOperationException();
      }

      public long getTotalCount() {
         assert !this.isComplete() || this.totalCount == this.calculateTotalCount() : "Total count: " + this.totalCount + " != " + this.calculateTotalCount();

         return this.totalCount;
      }

      private long calculateTotalCount() {
         long var1 = 0L;
         int var3 = this.getKeyCount();

         for(int var4 = 0; var4 <= var3; ++var4) {
            var1 += this.children[var4].count;
         }

         return var1;
      }

      void recalculateTotalCount() {
         this.totalCount = this.calculateTotalCount();
      }

      long getCounts(int var1) {
         return this.children[var1].count;
      }

      public void setChild(int var1, Page<K, V> var2) {
         assert var2 != null;

         PageReference var3 = this.children[var1];
         if (var2 != var3.getPage() || var2.getPos() != var3.getPos()) {
            this.totalCount += var2.getTotalCount() - var3.count;
            this.children = (PageReference[])this.children.clone();
            this.children[var1] = new PageReference(var2);
         }

      }

      public V setValue(int var1, V var2) {
         throw new UnsupportedOperationException();
      }

      public void insertLeaf(int var1, K var2, V var3) {
         throw new UnsupportedOperationException();
      }

      public void insertNode(int var1, K var2, Page<K, V> var3) {
         int var4 = this.getRawChildPageCount();
         this.insertKey(var1, var2);
         PageReference[] var5 = createRefStorage(var4 + 1);
         DataUtils.copyWithGap(this.children, var5, var4, var1);
         this.children = var5;
         this.children[var1] = new PageReference(var3);
         this.totalCount += var3.getTotalCount();
         if (this.isPersistent()) {
            this.addMemory(32);
         }

      }

      public void remove(int var1) {
         int var2 = this.getRawChildPageCount();
         super.remove(var1);
         if (this.isPersistent()) {
            if (this.map.isMemoryEstimationAllowed()) {
               this.addMemory(-this.getMemory() / var2);
            } else {
               this.addMemory(-32);
            }
         }

         this.totalCount -= this.children[var1].count;
         PageReference[] var3 = createRefStorage(var2 - 1);
         DataUtils.copyExcept(this.children, var3, var2, var1);
         this.children = var3;
      }

      public int removeAllRecursive(long var1) {
         int var3 = this.removePage(var1);
         if (this.isPersistent()) {
            int var4 = 0;

            for(int var5 = this.map.getChildPageCount(this); var4 < var5; ++var4) {
               PageReference var6 = this.children[var4];
               Page var7 = var6.getPage();
               if (var7 != null) {
                  var3 += var7.removeAllRecursive(var1);
               } else {
                  long var8 = var6.getPos();

                  assert DataUtils.isPageSaved(var8);

                  if (DataUtils.isLeafPosition(var8)) {
                     this.map.store.accountForRemovedPage(var8, var1, this.map.isSingleWriter(), -1);
                  } else {
                     var3 += this.map.readPage(var8).removeAllRecursive(var1);
                  }
               }
            }
         }

         return var3;
      }

      public CursorPos<K, V> getPrependCursorPos(CursorPos<K, V> var1) {
         Page var2 = this.getChildPage(0);
         return var2.getPrependCursorPos(new CursorPos(this, 0, var1));
      }

      public CursorPos<K, V> getAppendCursorPos(CursorPos<K, V> var1) {
         int var2 = this.getKeyCount();
         Page var3 = this.getChildPage(var2);
         return var3.getAppendCursorPos(new CursorPos(this, var2, var1));
      }

      protected void readPayLoad(ByteBuffer var1) {
         int var2 = this.getKeyCount();
         this.children = createRefStorage(var2 + 1);
         long[] var3 = new long[var2 + 1];

         for(int var4 = 0; var4 <= var2; ++var4) {
            var3[var4] = var1.getLong();
         }

         long var11 = 0L;
         int var6 = 0;

         while(true) {
            if (var6 > var2) {
               this.totalCount = var11;
               return;
            }

            long var7 = DataUtils.readVarLong(var1);
            long var9 = var3[var6];
            if (!$assertionsDisabled) {
               if (var9 == 0L) {
                  if (var7 != 0L) {
                     break;
                  }
               } else if (var7 < 0L) {
                  break;
               }
            }

            var11 += var7;
            this.children[var6] = var9 == 0L ? Page.PageReference.empty() : new PageReference(var9, var7);
            ++var6;
         }

         throw new AssertionError();
      }

      protected void writeValues(WriteBuffer var1) {
      }

      protected void writeChildren(WriteBuffer var1, boolean var2) {
         int var3 = this.getKeyCount();

         int var4;
         for(var4 = 0; var4 <= var3; ++var4) {
            var1.putLong(this.children[var4].getPos());
         }

         if (var2) {
            for(var4 = 0; var4 <= var3; ++var4) {
               var1.putVarLong(this.children[var4].count);
            }
         }

      }

      void writeUnsavedRecursive(Chunk var1, WriteBuffer var2, List<Long> var3) {
         if (!this.isSaved()) {
            int var4 = this.write(var1, var2, var3);
            this.writeChildrenRecursive(var1, var2, var3);
            int var5 = var2.position();
            var2.position(var4);
            this.writeChildren(var2, false);
            var2.position(var5);
         }

      }

      void writeChildrenRecursive(Chunk var1, WriteBuffer var2, List<Long> var3) {
         int var4 = this.getRawChildPageCount();

         for(int var5 = 0; var5 < var4; ++var5) {
            PageReference var6 = this.children[var5];
            Page var7 = var6.getPage();
            if (var7 != null) {
               var7.writeUnsavedRecursive(var1, var2, var3);
               var6.resetPos();
            }
         }

      }

      void releaseSavedPages() {
         int var1 = this.getRawChildPageCount();

         for(int var2 = 0; var2 < var1; ++var2) {
            this.children[var2].clearPageReference();
         }

      }

      public int getRawChildPageCount() {
         return this.getKeyCount() + 1;
      }

      protected int calculateMemory() {
         return super.calculateMemory() + 121 + this.getRawChildPageCount() * 32;
      }

      public void dump(StringBuilder var1) {
         super.dump(var1);
         int var2 = this.getKeyCount();

         for(int var3 = 0; var3 <= var2; ++var3) {
            if (var3 > 0) {
               var1.append(" ");
            }

            var1.append("[").append(Long.toHexString(this.children[var3].getPos())).append("]");
            if (var3 < var2) {
               var1.append(" ").append(this.getKey(var3));
            }
         }

      }
   }

   public static final class PageReference<K, V> {
      static final PageReference EMPTY = new PageReference((Page)null, 0L, 0L);
      private long pos;
      private Page<K, V> page;
      final long count;

      public static <X, Y> PageReference<X, Y> empty() {
         return EMPTY;
      }

      public PageReference(Page<K, V> var1) {
         this(var1, var1.getPos(), var1.getTotalCount());
      }

      PageReference(long var1, long var3) {
         this((Page)null, var1, var3);

         assert DataUtils.isPageSaved(var1);

      }

      private PageReference(Page<K, V> var1, long var2, long var4) {
         this.page = var1;
         this.pos = var2;
         this.count = var4;
      }

      public Page<K, V> getPage() {
         return this.page;
      }

      void clearPageReference() {
         if (this.page != null) {
            this.page.releaseSavedPages();

            assert this.page.isSaved() || !this.page.isComplete();

            if (this.page.isSaved()) {
               assert this.pos == this.page.getPos();

               assert this.count == this.page.getTotalCount() : this.count + " != " + this.page.getTotalCount();

               this.page = null;
            }
         }

      }

      long getPos() {
         return this.pos;
      }

      void resetPos() {
         Page var1 = this.page;
         if (var1 != null && var1.isSaved()) {
            this.pos = var1.getPos();

            assert this.count == var1.getTotalCount();
         }

      }

      public String toString() {
         StringBuilder var10000;
         String var10001;
         label38: {
            var10000 = (new StringBuilder()).append("Cnt:").append(this.count).append(", pos:").append(this.pos == 0L ? "0" : DataUtils.getPageChunkId(this.pos) + (this.page == null ? "" : "/" + this.page.pageNo) + "-" + DataUtils.getPageOffset(this.pos) + ":" + DataUtils.getPageMaxLength(this.pos));
            if (this.page == null) {
               if (DataUtils.getPageType(this.pos) == 0) {
                  break label38;
               }
            } else if (this.page.isLeaf()) {
               break label38;
            }

            var10001 = " node";
            return var10000.append(var10001).append(", page:{").append(this.page).append("}").toString();
         }

         var10001 = " leaf";
         return var10000.append(var10001).append(", page:{").append(this.page).append("}").toString();
      }
   }
}
