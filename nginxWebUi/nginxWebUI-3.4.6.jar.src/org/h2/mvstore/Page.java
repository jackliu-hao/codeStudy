/*      */ package org.h2.mvstore;
/*      */ 
/*      */ import java.nio.ByteBuffer;
/*      */ import java.util.Arrays;
/*      */ import java.util.List;
/*      */ import java.util.concurrent.atomic.AtomicLongFieldUpdater;
/*      */ import org.h2.compress.Compressor;
/*      */ import org.h2.util.Utils;
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
/*      */ public abstract class Page<K, V>
/*      */   implements Cloneable
/*      */ {
/*      */   public final MVMap<K, V> map;
/*      */   private volatile long pos;
/*   63 */   public int pageNo = -1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int cachedCompare;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int memory;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int diskSpaceUsed;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private K[] keys;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   91 */   private static final AtomicLongFieldUpdater<Page> posUpdater = AtomicLongFieldUpdater.newUpdater(Page.class, "pos");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static final int PAGE_MEMORY_CHILD = 24;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final int PAGE_MEMORY = 81;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static final int PAGE_NODE_MEMORY = 121;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static final int PAGE_LEAF_MEMORY = 113;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final int IN_MEMORY = -2147483648;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  128 */   private static final PageReference[] SINGLE_EMPTY = new PageReference[] { PageReference.EMPTY };
/*      */ 
/*      */   
/*      */   Page(MVMap<K, V> paramMVMap) {
/*  132 */     this.map = paramMVMap;
/*      */   }
/*      */   
/*      */   Page(MVMap<K, V> paramMVMap, Page<K, V> paramPage) {
/*  136 */     this(paramMVMap, paramPage.keys);
/*  137 */     this.memory = paramPage.memory;
/*      */   }
/*      */   
/*      */   Page(MVMap<K, V> paramMVMap, K[] paramArrayOfK) {
/*  141 */     this.map = paramMVMap;
/*  142 */     this.keys = paramArrayOfK;
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
/*      */   static <K, V> Page<K, V> createEmptyLeaf(MVMap<K, V> paramMVMap) {
/*  155 */     return createLeaf(paramMVMap, (K[])paramMVMap.getKeyType().createStorage(0), (V[])paramMVMap
/*  156 */         .getValueType().createStorage(0), 113);
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
/*      */   static <K, V> Page<K, V> createEmptyNode(MVMap<K, V> paramMVMap) {
/*  170 */     return createNode(paramMVMap, (K[])paramMVMap.getKeyType().createStorage(0), (PageReference<K, V>[])SINGLE_EMPTY, 0L, 153);
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
/*      */   public static <K, V> Page<K, V> createNode(MVMap<K, V> paramMVMap, K[] paramArrayOfK, PageReference<K, V>[] paramArrayOfPageReference, long paramLong, int paramInt) {
/*  188 */     assert paramArrayOfK != null;
/*  189 */     NonLeaf<K, V> nonLeaf = new NonLeaf<>(paramMVMap, paramArrayOfK, paramArrayOfPageReference, paramLong);
/*  190 */     nonLeaf.initMemoryAccount(paramInt);
/*  191 */     return nonLeaf;
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
/*      */   static <K, V> Page<K, V> createLeaf(MVMap<K, V> paramMVMap, K[] paramArrayOfK, V[] paramArrayOfV, int paramInt) {
/*  207 */     assert paramArrayOfK != null;
/*  208 */     Leaf<K, V> leaf = new Leaf<>(paramMVMap, paramArrayOfK, paramArrayOfV);
/*  209 */     leaf.initMemoryAccount(paramInt);
/*  210 */     return leaf;
/*      */   }
/*      */   
/*      */   private void initMemoryAccount(int paramInt) {
/*  214 */     if (!this.map.isPersistent()) {
/*  215 */       this.memory = Integer.MIN_VALUE;
/*  216 */     } else if (paramInt == 0) {
/*  217 */       recalculateMemory();
/*      */     } else {
/*  219 */       addMemory(paramInt);
/*  220 */       assert paramInt == getMemory();
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
/*      */   
/*      */   static <K, V> V get(Page<K, V> paramPage, K paramK) {
/*      */     while (true) {
/*  237 */       int i = paramPage.binarySearch(paramK);
/*  238 */       if (paramPage.isLeaf())
/*  239 */         return (i >= 0) ? paramPage.getValue(i) : null; 
/*  240 */       if (i++ < 0) {
/*  241 */         i = -i;
/*      */       }
/*  243 */       paramPage = paramPage.getChildPage(i);
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
/*      */   
/*      */   static <K, V> Page<K, V> read(ByteBuffer paramByteBuffer, long paramLong, MVMap<K, V> paramMVMap) {
/*  259 */     boolean bool = ((DataUtils.getPageType(paramLong) & 0x1) == 0) ? true : false;
/*  260 */     Page<K, V> page = (Page<K, V>)(bool ? new Leaf<>(paramMVMap) : new NonLeaf<>(paramMVMap));
/*  261 */     page.pos = paramLong;
/*  262 */     page.read(paramByteBuffer);
/*  263 */     return page;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final int getMapId() {
/*  271 */     return this.map.getId();
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public K getKey(int paramInt) {
/*  294 */     return this.keys[paramInt];
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
/*      */   public final int getKeyCount() {
/*  327 */     return this.keys.length;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final boolean isLeaf() {
/*  336 */     return (getNodeType() == 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final long getPos() {
/*  347 */     return this.pos;
/*      */   }
/*      */ 
/*      */   
/*      */   public String toString() {
/*  352 */     StringBuilder stringBuilder = new StringBuilder();
/*  353 */     dump(stringBuilder);
/*  354 */     return stringBuilder.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void dump(StringBuilder paramStringBuilder) {
/*  363 */     paramStringBuilder.append("id: ").append(System.identityHashCode(this)).append('\n');
/*  364 */     paramStringBuilder.append("pos: ").append(Long.toHexString(this.pos)).append('\n');
/*  365 */     if (isSaved()) {
/*  366 */       int i = DataUtils.getPageChunkId(this.pos);
/*  367 */       paramStringBuilder.append("chunk: ").append(Long.toHexString(i)).append('\n');
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Page<K, V> copy() {
/*  377 */     Page<K, V> page = clone();
/*  378 */     page.pos = 0L;
/*  379 */     return page;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected final Page<K, V> clone() {
/*      */     Page<K, V> page;
/*      */     try {
/*  387 */       page = (Page)super.clone();
/*  388 */     } catch (CloneNotSupportedException cloneNotSupportedException) {
/*  389 */       throw new RuntimeException(cloneNotSupportedException);
/*      */     } 
/*  391 */     return page;
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
/*      */   int binarySearch(K paramK) {
/*  406 */     int i = this.map.getKeyType().binarySearch(paramK, this.keys, getKeyCount(), this.cachedCompare);
/*  407 */     this.cachedCompare = (i < 0) ? (i ^ 0xFFFFFFFF) : (i + 1);
/*  408 */     return i;
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
/*      */   
/*      */   final K[] splitKeys(int paramInt1, int paramInt2) {
/*  427 */     assert paramInt1 + paramInt2 <= getKeyCount();
/*  428 */     K[] arrayOfK1 = createKeyStorage(paramInt1);
/*  429 */     K[] arrayOfK2 = createKeyStorage(paramInt2);
/*  430 */     System.arraycopy(this.keys, 0, arrayOfK1, 0, paramInt1);
/*  431 */     System.arraycopy(this.keys, getKeyCount() - paramInt2, arrayOfK2, 0, paramInt2);
/*  432 */     this.keys = arrayOfK1;
/*  433 */     return arrayOfK2;
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
/*      */ 
/*      */   
/*      */   final void expandKeys(int paramInt, K[] paramArrayOfK) {
/*  453 */     int i = getKeyCount();
/*  454 */     K[] arrayOfK = createKeyStorage(i + paramInt);
/*  455 */     System.arraycopy(this.keys, 0, arrayOfK, 0, i);
/*  456 */     System.arraycopy(paramArrayOfK, 0, arrayOfK, i, paramInt);
/*  457 */     this.keys = arrayOfK;
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
/*      */   public final void setKey(int paramInt, K paramK) {
/*  490 */     this.keys = (K[])this.keys.clone();
/*  491 */     if (isPersistent()) {
/*  492 */       K k = this.keys[paramInt];
/*  493 */       if (!this.map.isMemoryEstimationAllowed() || k == null) {
/*  494 */         int i = this.map.evaluateMemoryForKey(paramK);
/*  495 */         if (k != null) {
/*  496 */           i -= this.map.evaluateMemoryForKey(k);
/*      */         }
/*  498 */         addMemory(i);
/*      */       } 
/*      */     } 
/*  501 */     this.keys[paramInt] = paramK;
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
/*      */   final void insertKey(int paramInt, K paramK) {
/*  538 */     int i = getKeyCount();
/*  539 */     assert paramInt <= i : paramInt + " > " + i;
/*  540 */     K[] arrayOfK = createKeyStorage(i + 1);
/*  541 */     DataUtils.copyWithGap(this.keys, arrayOfK, i, paramInt);
/*  542 */     this.keys = arrayOfK;
/*      */     
/*  544 */     this.keys[paramInt] = paramK;
/*      */     
/*  546 */     if (isPersistent()) {
/*  547 */       addMemory(8 + this.map.evaluateMemoryForKey(paramK));
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void remove(int paramInt) {
/*  557 */     int i = getKeyCount();
/*  558 */     if (paramInt == i) {
/*  559 */       paramInt--;
/*      */     }
/*  561 */     if (isPersistent() && 
/*  562 */       !this.map.isMemoryEstimationAllowed()) {
/*  563 */       K k = getKey(paramInt);
/*  564 */       addMemory(-8 - this.map.evaluateMemoryForKey(k));
/*      */     } 
/*      */     
/*  567 */     K[] arrayOfK = createKeyStorage(i - 1);
/*  568 */     DataUtils.copyExcept(this.keys, arrayOfK, i, paramInt);
/*  569 */     this.keys = arrayOfK;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void read(ByteBuffer paramByteBuffer) {
/*  578 */     int i = DataUtils.getPageChunkId(this.pos);
/*  579 */     int j = DataUtils.getPageOffset(this.pos);
/*      */     
/*  581 */     int k = paramByteBuffer.position();
/*  582 */     int m = paramByteBuffer.getInt();
/*  583 */     int n = paramByteBuffer.remaining() + 4;
/*  584 */     if (m > n || m < 4) {
/*  585 */       throw DataUtils.newMVStoreException(6, "File corrupted in chunk {0}, expected page length 4..{1}, got {2}", new Object[] {
/*  586 */             Integer.valueOf(i), Integer.valueOf(n), 
/*  587 */             Integer.valueOf(m)
/*      */           });
/*      */     }
/*  590 */     short s = paramByteBuffer.getShort();
/*      */ 
/*      */     
/*  593 */     int i1 = DataUtils.getCheckValue(i) ^ DataUtils.getCheckValue(j) ^ DataUtils.getCheckValue(m);
/*  594 */     if (s != (short)i1) {
/*  595 */       throw DataUtils.newMVStoreException(6, "File corrupted in chunk {0}, expected check value {1}, got {2}", new Object[] {
/*  596 */             Integer.valueOf(i), Integer.valueOf(i1), Short.valueOf(s)
/*      */           });
/*      */     }
/*  599 */     this.pageNo = DataUtils.readVarInt(paramByteBuffer);
/*  600 */     if (this.pageNo < 0) {
/*  601 */       throw DataUtils.newMVStoreException(6, "File corrupted in chunk {0}, got negative page No {1}", new Object[] {
/*  602 */             Integer.valueOf(i), Integer.valueOf(this.pageNo)
/*      */           });
/*      */     }
/*  605 */     int i2 = DataUtils.readVarInt(paramByteBuffer);
/*  606 */     if (i2 != this.map.getId()) {
/*  607 */       throw DataUtils.newMVStoreException(6, "File corrupted in chunk {0}, expected map id {1}, got {2}", new Object[] {
/*  608 */             Integer.valueOf(i), Integer.valueOf(this.map.getId()), Integer.valueOf(i2)
/*      */           });
/*      */     }
/*  611 */     int i3 = DataUtils.readVarInt(paramByteBuffer);
/*  612 */     this.keys = createKeyStorage(i3);
/*  613 */     byte b = paramByteBuffer.get();
/*  614 */     if (isLeaf() != (((b & 0x1) == 0))) {
/*  615 */       throw DataUtils.newMVStoreException(6, "File corrupted in chunk {0}, expected node type {1}, got {2}", new Object[] {
/*      */ 
/*      */             
/*  618 */             Integer.valueOf(i), isLeaf() ? "0" : "1", Integer.valueOf(b)
/*      */           });
/*      */     }
/*      */     
/*  622 */     paramByteBuffer.limit(k + m);
/*      */     
/*  624 */     if (!isLeaf()) {
/*  625 */       readPayLoad(paramByteBuffer);
/*      */     }
/*  627 */     boolean bool = ((b & 0x2) != 0) ? true : false;
/*  628 */     if (bool) {
/*      */       Compressor compressor; byte[] arrayOfByte;
/*  630 */       if ((b & 0x6) == 6) {
/*      */         
/*  632 */         compressor = this.map.getStore().getCompressorHigh();
/*      */       } else {
/*  634 */         compressor = this.map.getStore().getCompressorFast();
/*      */       } 
/*  636 */       int i4 = DataUtils.readVarInt(paramByteBuffer);
/*  637 */       int i5 = paramByteBuffer.remaining();
/*      */       
/*  639 */       int i6 = 0;
/*  640 */       if (paramByteBuffer.hasArray()) {
/*  641 */         arrayOfByte = paramByteBuffer.array();
/*  642 */         i6 = paramByteBuffer.arrayOffset() + paramByteBuffer.position();
/*      */       } else {
/*  644 */         arrayOfByte = Utils.newBytes(i5);
/*  645 */         paramByteBuffer.get(arrayOfByte);
/*      */       } 
/*  647 */       int i7 = i5 + i4;
/*  648 */       paramByteBuffer = ByteBuffer.allocate(i7);
/*  649 */       compressor.expand(arrayOfByte, i6, i5, paramByteBuffer.array(), paramByteBuffer
/*  650 */           .arrayOffset(), i7);
/*      */     } 
/*  652 */     this.map.getKeyType().read(paramByteBuffer, this.keys, i3);
/*  653 */     if (isLeaf()) {
/*  654 */       readPayLoad(paramByteBuffer);
/*      */     }
/*  656 */     this.diskSpaceUsed = m;
/*  657 */     recalculateMemory();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final boolean isSaved() {
/*  668 */     return DataUtils.isPageSaved(this.pos);
/*      */   }
/*      */   
/*      */   public final boolean isRemoved() {
/*  672 */     return DataUtils.isPageRemoved(this.pos);
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
/*      */   private boolean markAsRemoved() {
/*  685 */     assert getTotalCount() > 0L : this;
/*      */     
/*      */     while (true) {
/*  688 */       long l = this.pos;
/*  689 */       if (DataUtils.isPageSaved(l)) {
/*  690 */         return false;
/*      */       }
/*  692 */       assert !DataUtils.isPageRemoved(l);
/*  693 */       if (posUpdater.compareAndSet(this, 0L, 1L)) {
/*  694 */         return true;
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final int write(Chunk paramChunk, WriteBuffer paramWriteBuffer, List<Long> paramList) {
/*  706 */     this.pageNo = paramList.size();
/*  707 */     int i = getKeyCount();
/*  708 */     int j = paramWriteBuffer.position();
/*  709 */     paramWriteBuffer.putInt(0)
/*  710 */       .putShort((short)0)
/*  711 */       .putVarInt(this.pageNo)
/*  712 */       .putVarInt(this.map.getId())
/*  713 */       .putVarInt(i);
/*  714 */     int k = paramWriteBuffer.position();
/*  715 */     byte b = isLeaf() ? 0 : 1;
/*  716 */     paramWriteBuffer.put((byte)b);
/*  717 */     int m = paramWriteBuffer.position();
/*  718 */     writeChildren(paramWriteBuffer, true);
/*  719 */     int n = paramWriteBuffer.position();
/*  720 */     this.map.getKeyType().write(paramWriteBuffer, this.keys, i);
/*  721 */     writeValues(paramWriteBuffer);
/*  722 */     MVStore mVStore = this.map.getStore();
/*  723 */     int i1 = paramWriteBuffer.position() - n;
/*  724 */     if (i1 > 16) {
/*  725 */       int i6 = mVStore.getCompressionLevel();
/*  726 */       if (i6 > 0) {
/*      */         Compressor compressor; byte b1;
/*      */         byte[] arrayOfByte2;
/*  729 */         if (i6 == 1) {
/*  730 */           compressor = mVStore.getCompressorFast();
/*  731 */           b1 = 2;
/*      */         } else {
/*  733 */           compressor = mVStore.getCompressorHigh();
/*  734 */           b1 = 6;
/*      */         } 
/*  736 */         byte[] arrayOfByte1 = new byte[i1 * 2];
/*  737 */         ByteBuffer byteBuffer = paramWriteBuffer.getBuffer();
/*  738 */         int i7 = 0;
/*      */         
/*  740 */         if (byteBuffer.hasArray()) {
/*  741 */           arrayOfByte2 = byteBuffer.array();
/*  742 */           i7 = byteBuffer.arrayOffset() + n;
/*      */         } else {
/*  744 */           arrayOfByte2 = Utils.newBytes(i1);
/*  745 */           paramWriteBuffer.position(n).get(arrayOfByte2);
/*      */         } 
/*  747 */         int i8 = compressor.compress(arrayOfByte2, i7, i1, arrayOfByte1, 0);
/*  748 */         int i9 = DataUtils.getVarIntLen(i1 - i8);
/*  749 */         if (i8 + i9 < i1) {
/*  750 */           paramWriteBuffer.position(k)
/*  751 */             .put((byte)(b | b1));
/*  752 */           paramWriteBuffer.position(n)
/*  753 */             .putVarInt(i1 - i8)
/*  754 */             .put(arrayOfByte1, 0, i8);
/*      */         } 
/*      */       } 
/*      */     } 
/*  758 */     int i2 = paramWriteBuffer.position() - j;
/*  759 */     long l1 = DataUtils.getTocElement(getMapId(), j, paramWriteBuffer.position() - j, b);
/*  760 */     paramList.add(Long.valueOf(l1));
/*  761 */     int i3 = paramChunk.id;
/*      */ 
/*      */     
/*  764 */     int i4 = DataUtils.getCheckValue(i3) ^ DataUtils.getCheckValue(j) ^ DataUtils.getCheckValue(i2);
/*  765 */     paramWriteBuffer.putInt(j, i2)
/*  766 */       .putShort(j + 4, (short)i4);
/*  767 */     if (isSaved()) {
/*  768 */       throw DataUtils.newMVStoreException(3, "Page already stored", new Object[0]);
/*      */     }
/*      */     
/*  771 */     long l2 = DataUtils.getPagePos(i3, l1);
/*  772 */     boolean bool1 = isRemoved();
/*  773 */     while (!posUpdater.compareAndSet(this, bool1 ? 1L : 0L, l2)) {
/*  774 */       bool1 = isRemoved();
/*      */     }
/*  776 */     mVStore.cachePage(this);
/*  777 */     if (b == 1)
/*      */     {
/*      */       
/*  780 */       mVStore.cachePage(this);
/*      */     }
/*  782 */     int i5 = DataUtils.getPageMaxLength(this.pos);
/*  783 */     boolean bool2 = this.map.isSingleWriter();
/*  784 */     paramChunk.accountForWrittenPage(i5, bool2);
/*  785 */     if (bool1) {
/*  786 */       mVStore.accountForRemovedPage(l2, paramChunk.version + 1L, bool2, this.pageNo);
/*      */     }
/*  788 */     this.diskSpaceUsed = (i5 != 2097152) ? i5 : i2;
/*  789 */     return m;
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
/*      */   protected final boolean isPersistent() {
/*  824 */     return (this.memory != Integer.MIN_VALUE);
/*      */   }
/*      */   
/*      */   public final int getMemory() {
/*  828 */     if (isPersistent())
/*      */     {
/*      */       
/*  831 */       return this.memory;
/*      */     }
/*  833 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getDiskSpaceUsed() {
/*  842 */     long l = 0L;
/*  843 */     if (isPersistent()) {
/*  844 */       l += this.diskSpaceUsed;
/*  845 */       if (!isLeaf()) {
/*  846 */         for (byte b = 0; b < getRawChildPageCount(); b++) {
/*  847 */           long l1 = getChildPagePos(b);
/*  848 */           if (l1 != 0L) {
/*  849 */             l += getChildPage(b).getDiskSpaceUsed();
/*      */           }
/*      */         } 
/*      */       }
/*      */     } 
/*  854 */     return l;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final void addMemory(int paramInt) {
/*  863 */     this.memory += paramInt;
/*  864 */     assert this.memory >= 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final void recalculateMemory() {
/*  871 */     assert isPersistent();
/*  872 */     this.memory = calculateMemory();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int calculateMemory() {
/*  882 */     return this.map.evaluateMemoryForKeys(this.keys, getKeyCount());
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
/*      */   public boolean isComplete() {
/*  895 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setComplete() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final int removePage(long paramLong) {
/*  913 */     if (isPersistent() && getTotalCount() > 0L) {
/*  914 */       MVStore mVStore = this.map.store;
/*  915 */       if (!markAsRemoved()) {
/*  916 */         long l = this.pos;
/*  917 */         mVStore.accountForRemovedPage(l, paramLong, this.map.isSingleWriter(), this.pageNo);
/*      */       } else {
/*  919 */         return -this.memory;
/*      */       } 
/*      */     } 
/*  922 */     return 0;
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
/*      */   public final K[] createKeyStorage(int paramInt) {
/*  955 */     return (K[])this.map.getKeyType().createStorage(paramInt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final V[] createValueStorage(int paramInt) {
/*  965 */     return (V[])this.map.getValueType().createStorage(paramInt);
/*      */   } abstract Page<K, V> copy(MVMap<K, V> paramMVMap, boolean paramBoolean); public abstract Page<K, V> getChildPage(int paramInt); public abstract long getChildPagePos(int paramInt); public abstract V getValue(int paramInt); public abstract int getNodeType(); abstract Page<K, V> split(int paramInt); abstract void expand(int paramInt, K[] paramArrayOfK, V[] paramArrayOfV);
/*      */   public abstract long getTotalCount();
/*      */   abstract long getCounts(int paramInt);
/*      */   public abstract void setChild(int paramInt, Page<K, V> paramPage);
/*      */   public abstract V setValue(int paramInt, V paramV);
/*      */   public abstract void insertLeaf(int paramInt, K paramK, V paramV);
/*      */   public abstract void insertNode(int paramInt, K paramK, Page<K, V> paramPage);
/*      */   protected abstract void readPayLoad(ByteBuffer paramByteBuffer);
/*      */   protected abstract void writeValues(WriteBuffer paramWriteBuffer);
/*      */   protected abstract void writeChildren(WriteBuffer paramWriteBuffer, boolean paramBoolean);
/*      */   abstract void writeUnsavedRecursive(Chunk paramChunk, WriteBuffer paramWriteBuffer, List<Long> paramList);
/*      */   public static <K, V> PageReference<K, V>[] createRefStorage(int paramInt) {
/*  978 */     return (PageReference<K, V>[])new PageReference[paramInt];
/*      */   }
/*      */   abstract void releaseSavedPages();
/*      */   
/*      */   public abstract int getRawChildPageCount();
/*      */   
/*      */   public abstract CursorPos<K, V> getPrependCursorPos(CursorPos<K, V> paramCursorPos);
/*      */   
/*      */   public abstract CursorPos<K, V> getAppendCursorPos(CursorPos<K, V> paramCursorPos);
/*      */   
/*      */   public abstract int removeAllRecursive(long paramLong);
/*      */   
/*  990 */   public static final class PageReference<K, V> { static final PageReference EMPTY = new PageReference(null, 0L, 0L);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private long pos;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private Page<K, V> page;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     final long count;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static <X, Y> PageReference<X, Y> empty() {
/* 1016 */       return EMPTY;
/*      */     }
/*      */     
/*      */     public PageReference(Page<K, V> param1Page) {
/* 1020 */       this(param1Page, param1Page.getPos(), param1Page.getTotalCount());
/*      */     }
/*      */     
/*      */     PageReference(long param1Long1, long param1Long2) {
/* 1024 */       this(null, param1Long1, param1Long2);
/* 1025 */       assert DataUtils.isPageSaved(param1Long1);
/*      */     }
/*      */     
/*      */     private PageReference(Page<K, V> param1Page, long param1Long1, long param1Long2) {
/* 1029 */       this.page = param1Page;
/* 1030 */       this.pos = param1Long1;
/* 1031 */       this.count = param1Long2;
/*      */     }
/*      */     
/*      */     public Page<K, V> getPage() {
/* 1035 */       return this.page;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     void clearPageReference() {
/* 1044 */       if (this.page != null) {
/* 1045 */         this.page.releaseSavedPages();
/* 1046 */         assert this.page.isSaved() || !this.page.isComplete();
/* 1047 */         if (this.page.isSaved()) {
/* 1048 */           assert this.pos == this.page.getPos();
/* 1049 */           assert this.count == this.page.getTotalCount() : this.count + " != " + this.page.getTotalCount();
/* 1050 */           this.page = null;
/*      */         } 
/*      */       } 
/*      */     }
/*      */     
/*      */     long getPos() {
/* 1056 */       return this.pos;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     void resetPos() {
/* 1063 */       Page<K, V> page = this.page;
/* 1064 */       if (page != null && page.isSaved()) {
/* 1065 */         this.pos = page.getPos();
/* 1066 */         assert this.count == page.getTotalCount();
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public String toString() {
/* 1072 */       return "Cnt:" + this.count + ", pos:" + ((this.pos == 0L) ? "0" : (DataUtils.getPageChunkId(this.pos) + ((this.page == null) ? "" : ("/" + this.page.pageNo)) + "-" + 
/*      */         
/* 1074 */         DataUtils.getPageOffset(this.pos) + ":" + DataUtils.getPageMaxLength(this.pos))) + (((this.page == null) ? (
/* 1075 */         DataUtils.getPageType(this.pos) == 0) : this.page.isLeaf()) ? " leaf" : " node") + ", page:{" + this.page + "}";
/*      */     } }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static class NonLeaf<K, V>
/*      */     extends Page<K, V>
/*      */   {
/*      */     private Page.PageReference<K, V>[] children;
/*      */ 
/*      */     
/*      */     private long totalCount;
/*      */ 
/*      */ 
/*      */     
/*      */     NonLeaf(MVMap<K, V> param1MVMap) {
/* 1093 */       super(param1MVMap);
/*      */     }
/*      */     
/*      */     NonLeaf(MVMap<K, V> param1MVMap, NonLeaf<K, V> param1NonLeaf, Page.PageReference<K, V>[] param1ArrayOfPageReference, long param1Long) {
/* 1097 */       super(param1MVMap, param1NonLeaf);
/* 1098 */       this.children = param1ArrayOfPageReference;
/* 1099 */       this.totalCount = param1Long;
/*      */     }
/*      */     
/*      */     NonLeaf(MVMap<K, V> param1MVMap, K[] param1ArrayOfK, Page.PageReference<K, V>[] param1ArrayOfPageReference, long param1Long) {
/* 1103 */       super(param1MVMap, param1ArrayOfK);
/* 1104 */       this.children = param1ArrayOfPageReference;
/* 1105 */       this.totalCount = param1Long;
/*      */     }
/*      */ 
/*      */     
/*      */     public int getNodeType() {
/* 1110 */       return 1;
/*      */     }
/*      */ 
/*      */     
/*      */     public Page<K, V> copy(MVMap<K, V> param1MVMap, boolean param1Boolean) {
/* 1115 */       return param1Boolean ? new Page.IncompleteNonLeaf<>(param1MVMap, this) : new NonLeaf(param1MVMap, this, this.children, this.totalCount);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Page<K, V> getChildPage(int param1Int) {
/* 1122 */       Page.PageReference<K, V> pageReference = this.children[param1Int];
/* 1123 */       Page<K, V> page = pageReference.getPage();
/* 1124 */       if (page == null) {
/* 1125 */         page = this.map.readPage(pageReference.getPos());
/* 1126 */         assert pageReference.getPos() == page.getPos();
/* 1127 */         assert pageReference.count == page.getTotalCount();
/*      */       } 
/* 1129 */       return page;
/*      */     }
/*      */ 
/*      */     
/*      */     public long getChildPagePos(int param1Int) {
/* 1134 */       return this.children[param1Int].getPos();
/*      */     }
/*      */ 
/*      */     
/*      */     public V getValue(int param1Int) {
/* 1139 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     
/*      */     public Page<K, V> split(int param1Int) {
/* 1144 */       assert !isSaved();
/* 1145 */       int i = getKeyCount() - param1Int;
/* 1146 */       K[] arrayOfK = splitKeys(param1Int, i - 1);
/* 1147 */       Page.PageReference[] arrayOfPageReference1 = (Page.PageReference[])createRefStorage(param1Int + 1);
/* 1148 */       Page.PageReference[] arrayOfPageReference2 = (Page.PageReference[])createRefStorage(i);
/* 1149 */       System.arraycopy(this.children, 0, arrayOfPageReference1, 0, param1Int + 1);
/* 1150 */       System.arraycopy(this.children, param1Int + 1, arrayOfPageReference2, 0, i);
/* 1151 */       this.children = (Page.PageReference<K, V>[])arrayOfPageReference1;
/*      */       
/* 1153 */       long l = 0L;
/* 1154 */       for (Page.PageReference pageReference : arrayOfPageReference1) {
/* 1155 */         l += pageReference.count;
/*      */       }
/* 1157 */       this.totalCount = l;
/* 1158 */       l = 0L;
/* 1159 */       for (Page.PageReference pageReference : arrayOfPageReference2) {
/* 1160 */         l += pageReference.count;
/*      */       }
/* 1162 */       Page<K, V> page = createNode(this.map, arrayOfK, (Page.PageReference<K, V>[])arrayOfPageReference2, l, 0);
/* 1163 */       if (isPersistent()) {
/* 1164 */         recalculateMemory();
/*      */       }
/* 1166 */       return page;
/*      */     }
/*      */ 
/*      */     
/*      */     public void expand(int param1Int, Object[] param1ArrayOfObject1, Object[] param1ArrayOfObject2) {
/* 1171 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     
/*      */     public long getTotalCount() {
/* 1176 */       assert !isComplete() || this.totalCount == calculateTotalCount() : "Total count: " + this.totalCount + " != " + 
/* 1177 */         calculateTotalCount();
/* 1178 */       return this.totalCount;
/*      */     }
/*      */     
/*      */     private long calculateTotalCount() {
/* 1182 */       long l = 0L;
/* 1183 */       int i = getKeyCount();
/* 1184 */       for (byte b = 0; b <= i; b++) {
/* 1185 */         l += (this.children[b]).count;
/*      */       }
/* 1187 */       return l;
/*      */     }
/*      */     
/*      */     void recalculateTotalCount() {
/* 1191 */       this.totalCount = calculateTotalCount();
/*      */     }
/*      */ 
/*      */     
/*      */     long getCounts(int param1Int) {
/* 1196 */       return (this.children[param1Int]).count;
/*      */     }
/*      */ 
/*      */     
/*      */     public void setChild(int param1Int, Page<K, V> param1Page) {
/* 1201 */       assert param1Page != null;
/* 1202 */       Page.PageReference<K, V> pageReference = this.children[param1Int];
/* 1203 */       if (param1Page != pageReference.getPage() || param1Page.getPos() != pageReference.getPos()) {
/* 1204 */         this.totalCount += param1Page.getTotalCount() - pageReference.count;
/* 1205 */         this.children = (Page.PageReference<K, V>[])this.children.clone();
/* 1206 */         this.children[param1Int] = new Page.PageReference<>(param1Page);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public V setValue(int param1Int, V param1V) {
/* 1212 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     
/*      */     public void insertLeaf(int param1Int, K param1K, V param1V) {
/* 1217 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     
/*      */     public void insertNode(int param1Int, K param1K, Page<K, V> param1Page) {
/* 1222 */       int i = getRawChildPageCount();
/* 1223 */       insertKey(param1Int, param1K);
/*      */       
/* 1225 */       Page.PageReference[] arrayOfPageReference = (Page.PageReference[])createRefStorage(i + 1);
/* 1226 */       DataUtils.copyWithGap(this.children, arrayOfPageReference, i, param1Int);
/* 1227 */       this.children = (Page.PageReference<K, V>[])arrayOfPageReference;
/* 1228 */       this.children[param1Int] = new Page.PageReference<>(param1Page);
/*      */       
/* 1230 */       this.totalCount += param1Page.getTotalCount();
/* 1231 */       if (isPersistent()) {
/* 1232 */         addMemory(32);
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     public void remove(int param1Int) {
/* 1238 */       int i = getRawChildPageCount();
/* 1239 */       super.remove(param1Int);
/* 1240 */       if (isPersistent()) {
/* 1241 */         if (this.map.isMemoryEstimationAllowed()) {
/* 1242 */           addMemory(-getMemory() / i);
/*      */         } else {
/* 1244 */           addMemory(-32);
/*      */         } 
/*      */       }
/* 1247 */       this.totalCount -= (this.children[param1Int]).count;
/* 1248 */       Page.PageReference[] arrayOfPageReference = (Page.PageReference[])createRefStorage(i - 1);
/* 1249 */       DataUtils.copyExcept(this.children, arrayOfPageReference, i, param1Int);
/* 1250 */       this.children = (Page.PageReference<K, V>[])arrayOfPageReference;
/*      */     }
/*      */ 
/*      */     
/*      */     public int removeAllRecursive(long param1Long) {
/* 1255 */       int i = removePage(param1Long);
/* 1256 */       if (isPersistent()) {
/* 1257 */         byte b; int j; for (b = 0, j = this.map.getChildPageCount(this); b < j; b++) {
/* 1258 */           Page.PageReference<K, V> pageReference = this.children[b];
/* 1259 */           Page<K, V> page = pageReference.getPage();
/* 1260 */           if (page != null) {
/* 1261 */             i += page.removeAllRecursive(param1Long);
/*      */           } else {
/* 1263 */             long l = pageReference.getPos();
/* 1264 */             assert DataUtils.isPageSaved(l);
/* 1265 */             if (DataUtils.isLeafPosition(l)) {
/* 1266 */               this.map.store.accountForRemovedPage(l, param1Long, this.map.isSingleWriter(), -1);
/*      */             } else {
/* 1268 */               i += this.map.readPage(l).removeAllRecursive(param1Long);
/*      */             } 
/*      */           } 
/*      */         } 
/*      */       } 
/* 1273 */       return i;
/*      */     }
/*      */ 
/*      */     
/*      */     public CursorPos<K, V> getPrependCursorPos(CursorPos<K, V> param1CursorPos) {
/* 1278 */       Page<K, V> page = getChildPage(0);
/* 1279 */       return page.getPrependCursorPos(new CursorPos<>(this, 0, param1CursorPos));
/*      */     }
/*      */ 
/*      */     
/*      */     public CursorPos<K, V> getAppendCursorPos(CursorPos<K, V> param1CursorPos) {
/* 1284 */       int i = getKeyCount();
/* 1285 */       Page<K, V> page = getChildPage(i);
/* 1286 */       return page.getAppendCursorPos(new CursorPos<>(this, i, param1CursorPos));
/*      */     }
/*      */ 
/*      */     
/*      */     protected void readPayLoad(ByteBuffer param1ByteBuffer) {
/* 1291 */       int i = getKeyCount();
/* 1292 */       this.children = createRefStorage(i + 1);
/* 1293 */       long[] arrayOfLong = new long[i + 1];
/* 1294 */       for (byte b1 = 0; b1 <= i; b1++) {
/* 1295 */         arrayOfLong[b1] = param1ByteBuffer.getLong();
/*      */       }
/* 1297 */       long l = 0L;
/* 1298 */       for (byte b2 = 0; b2 <= i; ) {
/* 1299 */         long l1 = DataUtils.readVarLong(param1ByteBuffer);
/* 1300 */         long l2 = arrayOfLong[b2]; assert false;
/* 1301 */         throw new AssertionError();
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1307 */       this.totalCount = l;
/*      */     }
/*      */ 
/*      */     
/*      */     protected void writeValues(WriteBuffer param1WriteBuffer) {}
/*      */ 
/*      */     
/*      */     protected void writeChildren(WriteBuffer param1WriteBuffer, boolean param1Boolean) {
/* 1315 */       int i = getKeyCount(); byte b;
/* 1316 */       for (b = 0; b <= i; b++) {
/* 1317 */         param1WriteBuffer.putLong(this.children[b].getPos());
/*      */       }
/* 1319 */       if (param1Boolean) {
/* 1320 */         for (b = 0; b <= i; b++) {
/* 1321 */           param1WriteBuffer.putVarLong((this.children[b]).count);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     void writeUnsavedRecursive(Chunk param1Chunk, WriteBuffer param1WriteBuffer, List<Long> param1List) {
/* 1328 */       if (!isSaved()) {
/* 1329 */         int i = write(param1Chunk, param1WriteBuffer, param1List);
/* 1330 */         writeChildrenRecursive(param1Chunk, param1WriteBuffer, param1List);
/* 1331 */         int j = param1WriteBuffer.position();
/* 1332 */         param1WriteBuffer.position(i);
/* 1333 */         writeChildren(param1WriteBuffer, false);
/* 1334 */         param1WriteBuffer.position(j);
/*      */       } 
/*      */     }
/*      */     
/*      */     void writeChildrenRecursive(Chunk param1Chunk, WriteBuffer param1WriteBuffer, List<Long> param1List) {
/* 1339 */       int i = getRawChildPageCount();
/* 1340 */       for (byte b = 0; b < i; b++) {
/* 1341 */         Page.PageReference<K, V> pageReference = this.children[b];
/* 1342 */         Page<K, V> page = pageReference.getPage();
/* 1343 */         if (page != null) {
/* 1344 */           page.writeUnsavedRecursive(param1Chunk, param1WriteBuffer, param1List);
/* 1345 */           pageReference.resetPos();
/*      */         } 
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     void releaseSavedPages() {
/* 1352 */       int i = getRawChildPageCount();
/* 1353 */       for (byte b = 0; b < i; b++) {
/* 1354 */         this.children[b].clearPageReference();
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     public int getRawChildPageCount() {
/* 1360 */       return getKeyCount() + 1;
/*      */     }
/*      */ 
/*      */     
/*      */     protected int calculateMemory() {
/* 1365 */       return super.calculateMemory() + 121 + 
/* 1366 */         getRawChildPageCount() * 32;
/*      */     }
/*      */ 
/*      */     
/*      */     public void dump(StringBuilder param1StringBuilder) {
/* 1371 */       super.dump(param1StringBuilder);
/* 1372 */       int i = getKeyCount();
/* 1373 */       for (byte b = 0; b <= i; b++) {
/* 1374 */         if (b > 0) {
/* 1375 */           param1StringBuilder.append(" ");
/*      */         }
/* 1377 */         param1StringBuilder.append("[").append(Long.toHexString(this.children[b].getPos())).append("]");
/* 1378 */         if (b < i) {
/* 1379 */           param1StringBuilder.append(" ").append(getKey(b));
/*      */         }
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   private static class IncompleteNonLeaf<K, V>
/*      */     extends NonLeaf<K, V>
/*      */   {
/*      */     private boolean complete;
/*      */     
/*      */     IncompleteNonLeaf(MVMap<K, V> param1MVMap, Page.NonLeaf<K, V> param1NonLeaf) {
/* 1391 */       super(param1MVMap, param1NonLeaf, constructEmptyPageRefs(param1NonLeaf.getRawChildPageCount()), param1NonLeaf.getTotalCount());
/*      */     }
/*      */ 
/*      */     
/*      */     private static <K, V> Page.PageReference<K, V>[] constructEmptyPageRefs(int param1Int) {
/* 1396 */       Page.PageReference[] arrayOfPageReference = (Page.PageReference[])createRefStorage(param1Int);
/* 1397 */       Arrays.fill((Object[])arrayOfPageReference, Page.PageReference.empty());
/* 1398 */       return (Page.PageReference<K, V>[])arrayOfPageReference;
/*      */     }
/*      */ 
/*      */     
/*      */     void writeUnsavedRecursive(Chunk param1Chunk, WriteBuffer param1WriteBuffer, List<Long> param1List) {
/* 1403 */       if (this.complete) {
/* 1404 */         super.writeUnsavedRecursive(param1Chunk, param1WriteBuffer, param1List);
/* 1405 */       } else if (!isSaved()) {
/* 1406 */         writeChildrenRecursive(param1Chunk, param1WriteBuffer, param1List);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean isComplete() {
/* 1412 */       return this.complete;
/*      */     }
/*      */ 
/*      */     
/*      */     public void setComplete() {
/* 1417 */       recalculateTotalCount();
/* 1418 */       this.complete = true;
/*      */     }
/*      */ 
/*      */     
/*      */     public void dump(StringBuilder param1StringBuilder) {
/* 1423 */       super.dump(param1StringBuilder);
/* 1424 */       param1StringBuilder.append(", complete:").append(this.complete);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static class Leaf<K, V>
/*      */     extends Page<K, V>
/*      */   {
/*      */     private V[] values;
/*      */ 
/*      */ 
/*      */     
/*      */     Leaf(MVMap<K, V> param1MVMap) {
/* 1438 */       super(param1MVMap);
/*      */     }
/*      */     
/*      */     private Leaf(MVMap<K, V> param1MVMap, Leaf<K, V> param1Leaf) {
/* 1442 */       super(param1MVMap, param1Leaf);
/* 1443 */       this.values = param1Leaf.values;
/*      */     }
/*      */     
/*      */     Leaf(MVMap<K, V> param1MVMap, K[] param1ArrayOfK, V[] param1ArrayOfV) {
/* 1447 */       super(param1MVMap, param1ArrayOfK);
/* 1448 */       this.values = param1ArrayOfV;
/*      */     }
/*      */ 
/*      */     
/*      */     public int getNodeType() {
/* 1453 */       return 0;
/*      */     }
/*      */ 
/*      */     
/*      */     public Page<K, V> copy(MVMap<K, V> param1MVMap, boolean param1Boolean) {
/* 1458 */       return new Leaf(param1MVMap, this);
/*      */     }
/*      */ 
/*      */     
/*      */     public Page<K, V> getChildPage(int param1Int) {
/* 1463 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     
/*      */     public long getChildPagePos(int param1Int) {
/* 1468 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     
/*      */     public V getValue(int param1Int) {
/* 1473 */       return (this.values == null) ? null : this.values[param1Int];
/*      */     }
/*      */ 
/*      */     
/*      */     public Page<K, V> split(int param1Int) {
/* 1478 */       assert !isSaved();
/* 1479 */       int i = getKeyCount() - param1Int;
/* 1480 */       K[] arrayOfK = splitKeys(param1Int, i);
/* 1481 */       V[] arrayOfV = createValueStorage(i);
/* 1482 */       if (this.values != null) {
/* 1483 */         V[] arrayOfV1 = createValueStorage(param1Int);
/* 1484 */         System.arraycopy(this.values, 0, arrayOfV1, 0, param1Int);
/* 1485 */         System.arraycopy(this.values, param1Int, arrayOfV, 0, i);
/* 1486 */         this.values = arrayOfV1;
/*      */       } 
/* 1488 */       Page<K, V> page = createLeaf(this.map, arrayOfK, arrayOfV, 0);
/* 1489 */       if (isPersistent()) {
/* 1490 */         recalculateMemory();
/*      */       }
/* 1492 */       return page;
/*      */     }
/*      */ 
/*      */     
/*      */     public void expand(int param1Int, K[] param1ArrayOfK, V[] param1ArrayOfV) {
/* 1497 */       int i = getKeyCount();
/* 1498 */       expandKeys(param1Int, param1ArrayOfK);
/* 1499 */       if (this.values != null) {
/* 1500 */         V[] arrayOfV = createValueStorage(i + param1Int);
/* 1501 */         System.arraycopy(this.values, 0, arrayOfV, 0, i);
/* 1502 */         System.arraycopy(param1ArrayOfV, 0, arrayOfV, i, param1Int);
/* 1503 */         this.values = arrayOfV;
/*      */       } 
/* 1505 */       if (isPersistent()) {
/* 1506 */         recalculateMemory();
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     public long getTotalCount() {
/* 1512 */       return getKeyCount();
/*      */     }
/*      */ 
/*      */     
/*      */     long getCounts(int param1Int) {
/* 1517 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     
/*      */     public void setChild(int param1Int, Page<K, V> param1Page) {
/* 1522 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     
/*      */     public V setValue(int param1Int, V param1V) {
/* 1527 */       this.values = (V[])this.values.clone();
/* 1528 */       V v = setValueInternal(param1Int, param1V);
/* 1529 */       if (isPersistent() && 
/* 1530 */         !this.map.isMemoryEstimationAllowed()) {
/* 1531 */         addMemory(this.map.evaluateMemoryForValue(param1V) - this.map
/* 1532 */             .evaluateMemoryForValue(v));
/*      */       }
/*      */       
/* 1535 */       return v;
/*      */     }
/*      */     
/*      */     private V setValueInternal(int param1Int, V param1V) {
/* 1539 */       V v = this.values[param1Int];
/* 1540 */       this.values[param1Int] = param1V;
/* 1541 */       return v;
/*      */     }
/*      */ 
/*      */     
/*      */     public void insertLeaf(int param1Int, K param1K, V param1V) {
/* 1546 */       int i = getKeyCount();
/* 1547 */       insertKey(param1Int, param1K);
/*      */       
/* 1549 */       if (this.values != null) {
/* 1550 */         V[] arrayOfV = createValueStorage(i + 1);
/* 1551 */         DataUtils.copyWithGap(this.values, arrayOfV, i, param1Int);
/* 1552 */         this.values = arrayOfV;
/* 1553 */         setValueInternal(param1Int, param1V);
/* 1554 */         if (isPersistent()) {
/* 1555 */           addMemory(8 + this.map.evaluateMemoryForValue(param1V));
/*      */         }
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void insertNode(int param1Int, K param1K, Page<K, V> param1Page) {
/* 1562 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     
/*      */     public void remove(int param1Int) {
/* 1567 */       int i = getKeyCount();
/* 1568 */       super.remove(param1Int);
/* 1569 */       if (this.values != null) {
/* 1570 */         if (isPersistent()) {
/* 1571 */           if (this.map.isMemoryEstimationAllowed()) {
/* 1572 */             addMemory(-getMemory() / i);
/*      */           } else {
/* 1574 */             V v = getValue(param1Int);
/* 1575 */             addMemory(-8 - this.map.evaluateMemoryForValue(v));
/*      */           } 
/*      */         }
/* 1578 */         V[] arrayOfV = createValueStorage(i - 1);
/* 1579 */         DataUtils.copyExcept(this.values, arrayOfV, i, param1Int);
/* 1580 */         this.values = arrayOfV;
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public int removeAllRecursive(long param1Long) {
/* 1586 */       return removePage(param1Long);
/*      */     }
/*      */ 
/*      */     
/*      */     public CursorPos<K, V> getPrependCursorPos(CursorPos<K, V> param1CursorPos) {
/* 1591 */       return new CursorPos<>(this, -1, param1CursorPos);
/*      */     }
/*      */ 
/*      */     
/*      */     public CursorPos<K, V> getAppendCursorPos(CursorPos<K, V> param1CursorPos) {
/* 1596 */       int i = getKeyCount();
/* 1597 */       return new CursorPos<>(this, i ^ 0xFFFFFFFF, param1CursorPos);
/*      */     }
/*      */ 
/*      */     
/*      */     protected void readPayLoad(ByteBuffer param1ByteBuffer) {
/* 1602 */       int i = getKeyCount();
/* 1603 */       this.values = createValueStorage(i);
/* 1604 */       this.map.getValueType().read(param1ByteBuffer, this.values, getKeyCount());
/*      */     }
/*      */ 
/*      */     
/*      */     protected void writeValues(WriteBuffer param1WriteBuffer) {
/* 1609 */       this.map.getValueType().write(param1WriteBuffer, this.values, getKeyCount());
/*      */     }
/*      */ 
/*      */     
/*      */     protected void writeChildren(WriteBuffer param1WriteBuffer, boolean param1Boolean) {}
/*      */ 
/*      */     
/*      */     void writeUnsavedRecursive(Chunk param1Chunk, WriteBuffer param1WriteBuffer, List<Long> param1List) {
/* 1617 */       if (!isSaved()) {
/* 1618 */         write(param1Chunk, param1WriteBuffer, param1List);
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     void releaseSavedPages() {}
/*      */ 
/*      */     
/*      */     public int getRawChildPageCount() {
/* 1627 */       return 0;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected int calculateMemory() {
/* 1633 */       return super.calculateMemory() + 113 + ((this.values == null) ? 0 : this.map
/* 1634 */         .evaluateMemoryForValues(this.values, getKeyCount()));
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
/*      */     public void dump(StringBuilder param1StringBuilder) {
/* 1648 */       super.dump(param1StringBuilder);
/* 1649 */       int i = getKeyCount();
/* 1650 */       for (byte b = 0; b < i; b++) {
/* 1651 */         if (b > 0) {
/* 1652 */           param1StringBuilder.append(" ");
/*      */         }
/* 1654 */         param1StringBuilder.append(getKey(b));
/* 1655 */         if (this.values != null) {
/* 1656 */           param1StringBuilder.append(':');
/* 1657 */           param1StringBuilder.append(getValue(b));
/*      */         } 
/*      */       } 
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\Page.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */