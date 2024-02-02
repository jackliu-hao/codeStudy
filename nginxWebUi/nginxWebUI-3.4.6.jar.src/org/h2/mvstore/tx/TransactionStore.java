/*     */ package org.h2.mvstore.tx;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.BitSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import java.util.concurrent.atomic.AtomicReferenceArray;
/*     */ import org.h2.engine.IsolationLevel;
/*     */ import org.h2.mvstore.Cursor;
/*     */ import org.h2.mvstore.DataUtils;
/*     */ import org.h2.mvstore.MVMap;
/*     */ import org.h2.mvstore.MVStore;
/*     */ import org.h2.mvstore.RootReference;
/*     */ import org.h2.mvstore.rtree.MVRTreeMap;
/*     */ import org.h2.mvstore.rtree.SpatialDataType;
/*     */ import org.h2.mvstore.type.DataType;
/*     */ import org.h2.mvstore.type.LongDataType;
/*     */ import org.h2.mvstore.type.MetaType;
/*     */ import org.h2.mvstore.type.ObjectDataType;
/*     */ import org.h2.mvstore.type.StringDataType;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.value.VersionedValue;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TransactionStore
/*     */ {
/*     */   final MVStore store;
/*     */   final int timeoutMillis;
/*     */   private final MVMap<Integer, Object[]> preparedTransactions;
/*     */   private final MVMap<String, DataType<?>> typeRegistry;
/*  65 */   final MVMap<Long, Record<?, ?>>[] undoLogs = (MVMap<Long, Record<?, ?>>[])new MVMap[65535];
/*     */ 
/*     */ 
/*     */   
/*     */   private final MVMap.Builder<Long, Record<?, ?>> undoLogBuilder;
/*     */ 
/*     */ 
/*     */   
/*     */   private final DataType<?> dataType;
/*     */ 
/*     */   
/*  76 */   final AtomicReference<VersionedBitSet> openTransactions = new AtomicReference<>(new VersionedBitSet());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  85 */   final AtomicReference<BitSet> committingTransactions = new AtomicReference<>(new BitSet());
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean init;
/*     */ 
/*     */ 
/*     */   
/*  93 */   private int maxTransactionId = 65535;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 100 */   private final AtomicReferenceArray<Transaction> transactions = new AtomicReferenceArray<>(65536);
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String TYPE_REGISTRY_NAME = "_";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String UNDO_LOG_NAME_PREFIX = "undoLog";
/*     */ 
/*     */ 
/*     */   
/*     */   private static final char UNDO_LOG_COMMITTED = '-';
/*     */ 
/*     */   
/*     */   private static final char UNDO_LOG_OPEN = '.';
/*     */ 
/*     */   
/*     */   private static final int MAX_OPEN_TRANSACTIONS = 65535;
/*     */ 
/*     */   
/*     */   private static final int LOG_ID_BITS = 40;
/*     */ 
/*     */   
/*     */   private static final long LOG_ID_MASK = 1099511627775L;
/*     */ 
/*     */ 
/*     */   
/*     */   private static String getUndoLogName(int paramInt) {
/* 129 */     return (paramInt > 0) ? ("undoLog." + paramInt) : "undoLog.";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TransactionStore(MVStore paramMVStore) {
/* 139 */     this(paramMVStore, (DataType<?>)new ObjectDataType());
/*     */   }
/*     */   
/*     */   public TransactionStore(MVStore paramMVStore, DataType<?> paramDataType) {
/* 143 */     this(paramMVStore, new MetaType(null, paramMVStore.backgroundExceptionHandler), paramDataType, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TransactionStore(MVStore paramMVStore, MetaType<?> paramMetaType, DataType<?> paramDataType, int paramInt) {
/* 154 */     this.store = paramMVStore;
/* 155 */     this.dataType = paramDataType;
/* 156 */     this.timeoutMillis = paramInt;
/* 157 */     this.typeRegistry = openTypeRegistry(paramMVStore, paramMetaType);
/* 158 */     this.preparedTransactions = paramMVStore.openMap("openTransactions", (MVMap.MapBuilder)new MVMap.Builder());
/* 159 */     this.undoLogBuilder = createUndoLogBuilder();
/*     */   }
/*     */ 
/*     */   
/*     */   MVMap.Builder<Long, Record<?, ?>> createUndoLogBuilder() {
/* 164 */     return (new MVMap.Builder())
/* 165 */       .singleWriter()
/* 166 */       .keyType((DataType)LongDataType.INSTANCE)
/* 167 */       .valueType((DataType)new Record.Type<>(this));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static MVMap<String, DataType<?>> openTypeRegistry(MVStore paramMVStore, MetaType<?> paramMetaType) {
/* 174 */     MVMap.Builder builder = (new MVMap.Builder()).keyType((DataType)StringDataType.INSTANCE).valueType((DataType)paramMetaType);
/* 175 */     return paramMVStore.openMap("_", (MVMap.MapBuilder)builder);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void init() {
/* 183 */     init(ROLLBACK_LISTENER_NONE);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void init(RollbackListener paramRollbackListener) {
/* 194 */     if (!this.init) {
/* 195 */       for (String str : this.store.getMapNames()) {
/* 196 */         if (str.startsWith("undoLog")) {
/*     */ 
/*     */           
/* 199 */           if (str.length() > "undoLog".length()) {
/*     */ 
/*     */             
/* 202 */             boolean bool = (str.charAt("undoLog".length()) == '-') ? true : false;
/* 203 */             if (this.store.hasData(str)) {
/* 204 */               int i = StringUtils.parseUInt31(str, "undoLog".length() + 1, str
/* 205 */                   .length());
/* 206 */               VersionedBitSet versionedBitSet = this.openTransactions.get();
/* 207 */               if (!versionedBitSet.get(i)) {
/* 208 */                 byte b; String str1; Object[] arrayOfObject = (Object[])this.preparedTransactions.get(Integer.valueOf(i));
/*     */ 
/*     */                 
/* 211 */                 if (arrayOfObject == null) {
/* 212 */                   b = 1;
/* 213 */                   str1 = null;
/*     */                 } else {
/* 215 */                   b = ((Integer)arrayOfObject[0]).intValue();
/* 216 */                   str1 = (String)arrayOfObject[1];
/*     */                 } 
/* 218 */                 MVMap<Long, Record<?, ?>> mVMap = this.store.openMap(str, (MVMap.MapBuilder)this.undoLogBuilder);
/* 219 */                 this.undoLogs[i] = mVMap;
/* 220 */                 Long long_ = (Long)mVMap.lastKey();
/* 221 */                 assert long_ != null;
/* 222 */                 assert getTransactionId(long_.longValue()) == i;
/* 223 */                 long l = getLogId(long_.longValue()) + 1L;
/* 224 */                 if (bool) {
/*     */                   
/* 226 */                   this.store.renameMap(mVMap, getUndoLogName(i));
/* 227 */                   markUndoLogAsCommitted(i);
/*     */                 } else {
/* 229 */                   bool = (l > 1099511627775L) ? true : false;
/*     */                 } 
/* 231 */                 if (bool) {
/* 232 */                   b = 3;
/* 233 */                   long_ = (Long)mVMap.lowerKey(long_);
/* 234 */                   assert long_ == null || getTransactionId(long_.longValue()) == i;
/* 235 */                   l = (long_ == null) ? 0L : (getLogId(long_.longValue()) + 1L);
/*     */                 } 
/* 237 */                 registerTransaction(i, b, str1, l, this.timeoutMillis, 0, IsolationLevel.READ_COMMITTED, paramRollbackListener);
/*     */                 
/*     */                 continue;
/*     */               } 
/*     */             } 
/*     */           } 
/*     */           
/* 244 */           if (!this.store.isReadOnly()) {
/* 245 */             this.store.removeMap(str);
/*     */           }
/*     */         } 
/*     */       } 
/* 249 */       this.init = true;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void markUndoLogAsCommitted(int paramInt) {
/* 254 */     addUndoLogRecord(paramInt, 1099511627775L, Record.COMMIT_MARKER);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void endLeftoverTransactions() {
/* 262 */     List<Transaction> list = getOpenTransactions();
/* 263 */     for (Transaction transaction : list) {
/* 264 */       int i = transaction.getStatus();
/* 265 */       if (i == 3) {
/* 266 */         transaction.commit(); continue;
/* 267 */       }  if (i != 2) {
/* 268 */         transaction.rollback();
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   int getMaxTransactionId() {
/* 274 */     return this.maxTransactionId;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMaxTransactionId(int paramInt) {
/* 285 */     DataUtils.checkArgument((paramInt <= 65535), "Concurrent transactions limit is too high: {0}", new Object[] {
/* 286 */           Integer.valueOf(paramInt) });
/* 287 */     this.maxTransactionId = paramInt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasMap(String paramString) {
/* 297 */     return this.store.hasMap(paramString);
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
/*     */   static long getOperationId(int paramInt, long paramLong) {
/* 311 */     DataUtils.checkArgument((paramInt >= 0 && paramInt < 16777216), "Transaction id out of range: {0}", new Object[] {
/* 312 */           Integer.valueOf(paramInt) });
/* 313 */     DataUtils.checkArgument((paramLong >= 0L && paramLong <= 1099511627775L), "Transaction log id out of range: {0}", new Object[] {
/* 314 */           Long.valueOf(paramLong) });
/* 315 */     return paramInt << 40L | paramLong;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static int getTransactionId(long paramLong) {
/* 325 */     return (int)(paramLong >>> 40L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static long getLogId(long paramLong) {
/* 335 */     return paramLong & 0xFFFFFFFFFFL;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Transaction> getOpenTransactions() {
/* 344 */     if (!this.init) {
/* 345 */       init();
/*     */     }
/* 347 */     ArrayList<Transaction> arrayList = new ArrayList();
/* 348 */     int i = 0;
/* 349 */     BitSet bitSet = this.openTransactions.get();
/* 350 */     while ((i = bitSet.nextSetBit(i + 1)) > 0) {
/* 351 */       Transaction transaction = getTransaction(i);
/* 352 */       if (transaction != null && 
/* 353 */         transaction.getStatus() != 0) {
/* 354 */         arrayList.add(transaction);
/*     */       }
/*     */     } 
/*     */     
/* 358 */     return arrayList;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void close() {
/* 365 */     this.store.commit();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Transaction begin() {
/* 374 */     return begin(ROLLBACK_LISTENER_NONE, this.timeoutMillis, 0, IsolationLevel.READ_COMMITTED);
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
/*     */   public Transaction begin(RollbackListener paramRollbackListener, int paramInt1, int paramInt2, IsolationLevel paramIsolationLevel) {
/* 387 */     return registerTransaction(0, 1, null, 0L, paramInt1, paramInt2, paramIsolationLevel, paramRollbackListener);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Transaction registerTransaction(int paramInt1, int paramInt2, String paramString, long paramLong, int paramInt3, int paramInt4, IsolationLevel paramIsolationLevel, RollbackListener paramRollbackListener) {
/*     */     int i;
/*     */     long l;
/*     */     boolean bool;
/*     */     do {
/* 399 */       VersionedBitSet versionedBitSet1 = this.openTransactions.get();
/* 400 */       if (paramInt1 == 0) {
/* 401 */         i = versionedBitSet1.nextClearBit(1);
/*     */       } else {
/* 403 */         i = paramInt1;
/* 404 */         assert !versionedBitSet1.get(i);
/*     */       } 
/* 406 */       if (i > this.maxTransactionId)
/* 407 */         throw DataUtils.newMVStoreException(102, "There are {0} open transactions", new Object[] {
/*     */ 
/*     */               
/* 410 */               Integer.valueOf(i - 1)
/*     */             }); 
/* 412 */       VersionedBitSet versionedBitSet2 = versionedBitSet1.clone();
/* 413 */       versionedBitSet2.set(i);
/* 414 */       l = versionedBitSet2.getVersion() + 1L;
/* 415 */       versionedBitSet2.setVersion(l);
/* 416 */       bool = this.openTransactions.compareAndSet(versionedBitSet1, versionedBitSet2);
/* 417 */     } while (!bool);
/*     */     
/* 419 */     Transaction transaction = new Transaction(this, i, l, paramInt2, paramString, paramLong, paramInt3, paramInt4, paramIsolationLevel, paramRollbackListener);
/*     */ 
/*     */     
/* 422 */     assert this.transactions.get(i) == null;
/* 423 */     this.transactions.set(i, transaction);
/*     */     
/* 425 */     if (this.undoLogs[i] == null) {
/* 426 */       String str = getUndoLogName(i);
/* 427 */       MVMap<Long, Record<?, ?>> mVMap = this.store.openMap(str, (MVMap.MapBuilder)this.undoLogBuilder);
/* 428 */       this.undoLogs[i] = mVMap;
/*     */     } 
/* 430 */     return transaction;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void storeTransaction(Transaction paramTransaction) {
/* 439 */     if (paramTransaction.getStatus() == 2 || paramTransaction
/* 440 */       .getName() != null) {
/* 441 */       Object[] arrayOfObject = { Integer.valueOf(paramTransaction.getStatus()), paramTransaction.getName() };
/* 442 */       this.preparedTransactions.put(Integer.valueOf(paramTransaction.getId()), arrayOfObject);
/* 443 */       paramTransaction.wasStored = true;
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
/*     */   long addUndoLogRecord(int paramInt, long paramLong, Record<?, ?> paramRecord) {
/* 456 */     MVMap<Long, Record<?, ?>> mVMap = this.undoLogs[paramInt];
/* 457 */     long l = getOperationId(paramInt, paramLong);
/* 458 */     if (paramLong == 0L && !mVMap.isEmpty())
/* 459 */       throw DataUtils.newMVStoreException(102, "An old transaction with the same id is still open: {0}", new Object[] {
/*     */ 
/*     */ 
/*     */             
/* 463 */             Integer.valueOf(paramInt)
/*     */           }); 
/* 465 */     mVMap.append(Long.valueOf(l), paramRecord);
/* 466 */     return l;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void removeUndoLogRecord(int paramInt) {
/* 474 */     this.undoLogs[paramInt].trimLast();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void removeMap(TransactionMap<?, ?> paramTransactionMap) {
/* 483 */     this.store.removeMap(paramTransactionMap.map);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void commit(Transaction paramTransaction, boolean paramBoolean) {
/* 493 */     if (!this.store.isClosed()) {
/* 494 */       Cursor cursor; int i = paramTransaction.transactionId;
/*     */ 
/*     */ 
/*     */       
/* 498 */       MVMap<Long, Record<?, ?>> mVMap = this.undoLogs[i];
/*     */       
/* 500 */       if (paramBoolean) {
/* 501 */         removeUndoLogRecord(i);
/* 502 */         cursor = mVMap.cursor(null);
/*     */       } else {
/* 504 */         cursor = mVMap.cursor(null);
/* 505 */         markUndoLogAsCommitted(i);
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 510 */       flipCommittingTransactionsBit(i, true);
/*     */       
/* 512 */       CommitDecisionMaker commitDecisionMaker = new CommitDecisionMaker();
/*     */       try {
/* 514 */         while (cursor.hasNext()) {
/* 515 */           Long long_ = (Long)cursor.next();
/* 516 */           Record record = (Record)cursor.getValue();
/* 517 */           int j = record.mapId;
/* 518 */           MVMap<?, VersionedValue<?>> mVMap1 = openMap(j);
/* 519 */           if (mVMap1 != null && !mVMap1.isClosed()) {
/* 520 */             K k = record.key;
/* 521 */             commitDecisionMaker.setUndoKey(long_.longValue());
/*     */ 
/*     */             
/* 524 */             mVMap1.operate(k, null, commitDecisionMaker);
/*     */           } 
/*     */         } 
/*     */       } finally {
/*     */         try {
/* 529 */           mVMap.clear();
/*     */         } finally {
/* 531 */           flipCommittingTransactionsBit(i, false);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void flipCommittingTransactionsBit(int paramInt, boolean paramBoolean) {
/*     */     boolean bool;
/*     */     do {
/* 540 */       BitSet bitSet1 = this.committingTransactions.get();
/* 541 */       assert bitSet1.get(paramInt) != paramBoolean : paramBoolean ? "Double commit" : "Mysterious bit's disappearance";
/* 542 */       BitSet bitSet2 = (BitSet)bitSet1.clone();
/* 543 */       bitSet2.set(paramInt, paramBoolean);
/* 544 */       bool = this.committingTransactions.compareAndSet(bitSet1, bitSet2);
/* 545 */     } while (!bool);
/*     */   }
/*     */   
/*     */   <K, V> MVMap<K, VersionedValue<V>> openVersionedMap(String paramString, DataType<K> paramDataType, DataType<V> paramDataType1) {
/* 549 */     VersionedValueType versionedValueType = (paramDataType1 == null) ? null : new VersionedValueType<>(paramDataType1);
/* 550 */     return openMap(paramString, paramDataType, (DataType<VersionedValue<V>>)versionedValueType);
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
/*     */   public <K, V> MVMap<K, V> openMap(String paramString, DataType<K> paramDataType, DataType<V> paramDataType1) {
/* 564 */     return this.store.openMap(paramString, (MVMap.MapBuilder)(new TxMapBuilder<>(this.typeRegistry, this.dataType))
/* 565 */         .keyType(paramDataType).valueType(paramDataType1));
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
/*     */   <K, V> MVMap<K, VersionedValue<V>> openMap(int paramInt) {
/* 578 */     MVMap<K, VersionedValue<V>> mVMap = this.store.getMap(paramInt);
/* 579 */     if (mVMap == null) {
/* 580 */       String str = this.store.getMapName(paramInt);
/* 581 */       if (str == null)
/*     */       {
/* 583 */         return null;
/*     */       }
/* 585 */       TxMapBuilder<Object, Object> txMapBuilder = new TxMapBuilder<>(this.typeRegistry, this.dataType);
/* 586 */       mVMap = this.store.openMap(paramInt, (MVMap.MapBuilder)txMapBuilder);
/*     */     } 
/* 588 */     return mVMap;
/*     */   }
/*     */   
/*     */   <K, V> MVMap<K, VersionedValue<V>> getMap(int paramInt) {
/* 592 */     MVMap<?, VersionedValue<?>> mVMap = this.store.getMap(paramInt);
/* 593 */     if (mVMap == null && !this.init) {
/* 594 */       mVMap = openMap(paramInt);
/*     */     }
/* 596 */     assert mVMap != null : "map with id " + paramInt + " is missing" + (this.init ? "" : " during initialization");
/*     */     
/* 598 */     return (MVMap)mVMap;
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
/*     */   void endTransaction(Transaction paramTransaction, boolean paramBoolean) {
/*     */     boolean bool;
/* 612 */     paramTransaction.closeIt();
/* 613 */     int i = paramTransaction.transactionId;
/* 614 */     this.transactions.set(i, null);
/*     */ 
/*     */     
/*     */     do {
/* 618 */       VersionedBitSet versionedBitSet1 = this.openTransactions.get();
/* 619 */       assert versionedBitSet1.get(i);
/* 620 */       VersionedBitSet versionedBitSet2 = versionedBitSet1.clone();
/* 621 */       versionedBitSet2.clear(i);
/* 622 */       bool = this.openTransactions.compareAndSet(versionedBitSet1, versionedBitSet2);
/* 623 */     } while (!bool);
/*     */     
/* 625 */     if (paramBoolean) {
/* 626 */       boolean bool1 = paramTransaction.wasStored;
/* 627 */       if (bool1 && !this.preparedTransactions.isClosed()) {
/* 628 */         this.preparedTransactions.remove(Integer.valueOf(i));
/*     */       }
/*     */       
/* 631 */       if (this.store.getFileStore() != null) {
/* 632 */         if (bool1 || this.store.getAutoCommitDelay() == 0) {
/* 633 */           this.store.commit();
/*     */         }
/* 635 */         else if (isUndoEmpty()) {
/*     */ 
/*     */ 
/*     */           
/* 639 */           int j = this.store.getUnsavedMemory();
/* 640 */           int k = this.store.getAutoCommitMemory();
/*     */           
/* 642 */           if (j * 4 > k * 3) {
/* 643 */             this.store.tryCommit();
/*     */           }
/*     */         } 
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
/*     */   RootReference<Long, Record<?, ?>>[] collectUndoLogRootReferences() {
/* 658 */     BitSet bitSet = this.openTransactions.get();
/*     */     
/* 660 */     RootReference[] arrayOfRootReference = new RootReference[bitSet.length()];
/* 661 */     for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i + 1)) {
/* 662 */       MVMap<Long, Record<?, ?>> mVMap = this.undoLogs[i];
/* 663 */       if (mVMap != null) {
/* 664 */         RootReference rootReference = mVMap.getRoot();
/* 665 */         if (rootReference.needFlush())
/*     */         {
/*     */           
/* 668 */           return null;
/*     */         }
/* 670 */         arrayOfRootReference[i] = rootReference;
/*     */       } 
/*     */     } 
/* 673 */     return (RootReference<Long, Record<?, ?>>[])arrayOfRootReference;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static long calculateUndoLogsTotalSize(RootReference<Long, Record<?, ?>>[] paramArrayOfRootReference) {
/* 683 */     long l = 0L;
/* 684 */     for (RootReference<Long, Record<?, ?>> rootReference : paramArrayOfRootReference) {
/* 685 */       if (rootReference != null) {
/* 686 */         l += rootReference.getTotalCount();
/*     */       }
/*     */     } 
/* 689 */     return l;
/*     */   }
/*     */   
/*     */   private boolean isUndoEmpty() {
/* 693 */     BitSet bitSet = this.openTransactions.get();
/* 694 */     for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i + 1)) {
/* 695 */       MVMap<Long, Record<?, ?>> mVMap = this.undoLogs[i];
/* 696 */       if (mVMap != null && !mVMap.isEmpty()) {
/* 697 */         return false;
/*     */       }
/*     */     } 
/* 700 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Transaction getTransaction(int paramInt) {
/* 710 */     return this.transactions.get(paramInt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void rollbackTo(Transaction paramTransaction, long paramLong1, long paramLong2) {
/* 721 */     int i = paramTransaction.getId();
/* 722 */     MVMap<Long, Record<?, ?>> mVMap = this.undoLogs[i];
/* 723 */     RollbackDecisionMaker rollbackDecisionMaker = new RollbackDecisionMaker(this, i, paramLong2, paramTransaction.listener); long l;
/* 724 */     for (l = paramLong1 - 1L; l >= paramLong2; l--) {
/* 725 */       Long long_ = Long.valueOf(getOperationId(i, l));
/* 726 */       mVMap.operate(long_, null, rollbackDecisionMaker);
/* 727 */       rollbackDecisionMaker.reset();
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
/*     */   Iterator<Change> getChanges(final Transaction t, final long maxLogId, final long toLogId) {
/* 743 */     final MVMap<Long, Record<?, ?>> undoLog = this.undoLogs[t.getId()];
/* 744 */     return new Iterator<Change>()
/*     */       {
/* 746 */         private long logId = maxLogId - 1L;
/*     */         private TransactionStore.Change current;
/*     */         
/*     */         private void fetchNext() {
/* 750 */           int i = t.getId();
/* 751 */           while (this.logId >= toLogId) {
/* 752 */             Long long_ = Long.valueOf(TransactionStore.getOperationId(i, this.logId));
/* 753 */             Record record = (Record)undoLog.get(long_);
/* 754 */             this.logId--;
/* 755 */             if (record == null) {
/*     */               
/* 757 */               long_ = (Long)undoLog.floorKey(long_);
/* 758 */               if (long_ == null || TransactionStore.getTransactionId(long_.longValue()) != i) {
/*     */                 break;
/*     */               }
/* 761 */               this.logId = TransactionStore.getLogId(long_.longValue());
/*     */               continue;
/*     */             } 
/* 764 */             int j = record.mapId;
/* 765 */             MVMap<?, VersionedValue<?>> mVMap = TransactionStore.this.openMap(j);
/* 766 */             if (mVMap != null) {
/* 767 */               VersionedValue versionedValue = record.oldValue;
/* 768 */               this
/* 769 */                 .current = new TransactionStore.Change(mVMap.getName(), record.key, (versionedValue == null) ? null : versionedValue.getCurrentValue());
/*     */               return;
/*     */             } 
/*     */           } 
/* 773 */           this.current = null;
/*     */         }
/*     */ 
/*     */         
/*     */         public boolean hasNext() {
/* 778 */           if (this.current == null) {
/* 779 */             fetchNext();
/*     */           }
/* 781 */           return (this.current != null);
/*     */         }
/*     */ 
/*     */         
/*     */         public TransactionStore.Change next() {
/* 786 */           if (!hasNext()) {
/* 787 */             throw DataUtils.newUnsupportedOperationException("no data");
/*     */           }
/* 789 */           TransactionStore.Change change = this.current;
/* 790 */           this.current = null;
/* 791 */           return change;
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Change
/*     */   {
/*     */     public final String mapName;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public final Object key;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public final Object value;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Change(String param1String, Object param1Object1, Object param1Object2) {
/* 818 */       this.mapName = param1String;
/* 819 */       this.key = param1Object1;
/* 820 */       this.value = param1Object2;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static final RollbackListener ROLLBACK_LISTENER_NONE = (paramMVMap, paramObject, paramVersionedValue1, paramVersionedValue2) -> {
/*     */     
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static interface RollbackListener
/*     */   {
/*     */     void onRollback(MVMap<Object, VersionedValue<Object>> param1MVMap, Object param1Object, VersionedValue<Object> param1VersionedValue1, VersionedValue<Object> param1VersionedValue2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class TxMapBuilder<K, V>
/*     */     extends MVMap.Builder<K, V>
/*     */   {
/*     */     private final MVMap<String, DataType<?>> typeRegistry;
/*     */ 
/*     */ 
/*     */     
/*     */     private final DataType defaultDataType;
/*     */ 
/*     */ 
/*     */     
/*     */     TxMapBuilder(MVMap<String, DataType<?>> param1MVMap, DataType<?> param1DataType) {
/* 853 */       this.typeRegistry = param1MVMap;
/* 854 */       this.defaultDataType = param1DataType;
/*     */     }
/*     */     
/*     */     private void registerDataType(DataType<?> param1DataType) {
/* 858 */       String str = getDataTypeRegistrationKey(param1DataType);
/* 859 */       DataType dataType = (DataType)this.typeRegistry.putIfAbsent(str, param1DataType);
/* 860 */       if (dataType != null);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     static String getDataTypeRegistrationKey(DataType<?> param1DataType) {
/* 866 */       return Integer.toHexString(Objects.hashCode(param1DataType));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public MVMap<K, V> create(MVStore param1MVStore, Map<String, Object> param1Map) {
/* 872 */       DataType<?> dataType1 = getKeyType();
/* 873 */       if (dataType1 == null) {
/* 874 */         String str = (String)param1Map.remove("key");
/* 875 */         if (str != null) {
/* 876 */           dataType1 = (DataType)this.typeRegistry.get(str);
/* 877 */           if (dataType1 == null) {
/* 878 */             throw DataUtils.newMVStoreException(106, "Data type with hash {0} can not be found", new Object[] { str });
/*     */           }
/*     */           
/* 881 */           setKeyType(dataType1);
/*     */         } 
/*     */       } else {
/* 884 */         registerDataType(dataType1);
/*     */       } 
/*     */       
/* 887 */       DataType<?> dataType2 = getValueType();
/* 888 */       if (dataType2 == null) {
/* 889 */         String str = (String)param1Map.remove("val");
/* 890 */         if (str != null) {
/* 891 */           dataType2 = (DataType)this.typeRegistry.get(str);
/* 892 */           if (dataType2 == null) {
/* 893 */             throw DataUtils.newMVStoreException(106, "Data type with hash {0} can not be found", new Object[] { str });
/*     */           }
/*     */           
/* 896 */           setValueType(dataType2);
/*     */         } 
/*     */       } else {
/* 899 */         registerDataType(dataType2);
/*     */       } 
/*     */       
/* 902 */       if (getKeyType() == null) {
/* 903 */         setKeyType(this.defaultDataType);
/* 904 */         registerDataType(getKeyType());
/*     */       } 
/* 906 */       if (getValueType() == null) {
/* 907 */         setValueType((DataType)new VersionedValueType<>(this.defaultDataType));
/* 908 */         registerDataType(getValueType());
/*     */       } 
/*     */       
/* 911 */       param1Map.put("store", param1MVStore);
/* 912 */       param1Map.put("key", getKeyType());
/* 913 */       param1Map.put("val", getValueType());
/* 914 */       return create(param1Map);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected MVMap<K, V> create(Map<String, Object> param1Map) {
/* 920 */       if ("rtree".equals(param1Map.get("type"))) {
/* 921 */         return (MVMap<K, V>)new MVRTreeMap(param1Map, (SpatialDataType)getKeyType(), 
/* 922 */             getValueType());
/*     */       }
/*     */       
/* 925 */       return new TMVMap<>(param1Map, getKeyType(), getValueType());
/*     */     }
/*     */     
/*     */     private static final class TMVMap<K, V> extends MVMap<K, V> {
/*     */       private final String type;
/*     */       
/*     */       TMVMap(Map<String, Object> param2Map, DataType<K> param2DataType, DataType<V> param2DataType1) {
/* 932 */         super(param2Map, param2DataType, param2DataType1);
/* 933 */         this.type = (String)param2Map.get("type");
/*     */       }
/*     */       
/*     */       private TMVMap(MVMap<K, V> param2MVMap) {
/* 937 */         super(param2MVMap);
/* 938 */         this.type = param2MVMap.getType();
/*     */       }
/*     */ 
/*     */       
/*     */       protected MVMap<K, V> cloneIt() {
/* 943 */         return new TMVMap(this);
/*     */       }
/*     */ 
/*     */       
/*     */       public String getType() {
/* 948 */         return this.type;
/*     */       }
/*     */ 
/*     */       
/*     */       protected String asString(String param2String) {
/* 953 */         StringBuilder stringBuilder = new StringBuilder();
/* 954 */         stringBuilder.append(super.asString(param2String));
/* 955 */         DataUtils.appendMap(stringBuilder, "key", TransactionStore.TxMapBuilder.getDataTypeRegistrationKey(getKeyType()));
/* 956 */         DataUtils.appendMap(stringBuilder, "val", TransactionStore.TxMapBuilder.getDataTypeRegistrationKey(getValueType()));
/* 957 */         return stringBuilder.toString();
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\tx\TransactionStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */