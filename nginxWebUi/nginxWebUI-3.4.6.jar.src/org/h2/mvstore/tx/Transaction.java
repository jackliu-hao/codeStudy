/*     */ package org.h2.mvstore.tx;
/*     */ 
/*     */ import java.util.BitSet;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ import org.h2.engine.IsolationLevel;
/*     */ import org.h2.mvstore.DataUtils;
/*     */ import org.h2.mvstore.MVMap;
/*     */ import org.h2.mvstore.MVStore;
/*     */ import org.h2.mvstore.RootReference;
/*     */ import org.h2.mvstore.type.DataType;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Transaction
/*     */ {
/*     */   public static final int STATUS_CLOSED = 0;
/*     */   public static final int STATUS_OPEN = 1;
/*     */   public static final int STATUS_PREPARED = 2;
/*     */   public static final int STATUS_COMMITTED = 3;
/*     */   private static final int STATUS_ROLLING_BACK = 4;
/*     */   private static final int STATUS_ROLLED_BACK = 5;
/*  70 */   private static final String[] STATUS_NAMES = new String[] { "CLOSED", "OPEN", "PREPARED", "COMMITTED", "ROLLING_BACK", "ROLLED_BACK" };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final int LOG_ID_BITS = 40;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int LOG_ID_BITS1 = 41;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final long LOG_ID_LIMIT = 1099511627776L;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final long LOG_ID_MASK = 2199023255551L;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int STATUS_BITS = 4;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int STATUS_MASK = 15;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final TransactionStore store;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final TransactionStore.RollbackListener listener;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final int transactionId;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final long sequenceNum;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final AtomicLong statusAndLogId;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private MVStore.TxCounter txCounter;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String name;
/*     */ 
/*     */ 
/*     */   
/*     */   boolean wasStored;
/*     */ 
/*     */ 
/*     */   
/*     */   int timeoutMillis;
/*     */ 
/*     */ 
/*     */   
/*     */   private final int ownerId;
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile Transaction blockingTransaction;
/*     */ 
/*     */ 
/*     */   
/*     */   private String blockingMapName;
/*     */ 
/*     */ 
/*     */   
/*     */   private Object blockingKey;
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile boolean notificationRequested;
/*     */ 
/*     */ 
/*     */   
/*     */   private RootReference<Long, Record<?, ?>>[] undoLogRootReferences;
/*     */ 
/*     */ 
/*     */   
/* 171 */   private final Map<Integer, TransactionMap<?, ?>> transactionMaps = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final IsolationLevel isolationLevel;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Transaction(TransactionStore paramTransactionStore, int paramInt1, long paramLong1, int paramInt2, String paramString, long paramLong2, int paramInt3, int paramInt4, IsolationLevel paramIsolationLevel, TransactionStore.RollbackListener paramRollbackListener) {
/* 182 */     this.store = paramTransactionStore;
/* 183 */     this.transactionId = paramInt1;
/* 184 */     this.sequenceNum = paramLong1;
/* 185 */     this.statusAndLogId = new AtomicLong(composeState(paramInt2, paramLong2, false));
/* 186 */     this.name = paramString;
/* 187 */     setTimeoutMillis(paramInt3);
/* 188 */     this.ownerId = paramInt4;
/* 189 */     this.isolationLevel = paramIsolationLevel;
/* 190 */     this.listener = paramRollbackListener;
/*     */   }
/*     */   
/*     */   public int getId() {
/* 194 */     return this.transactionId;
/*     */   }
/*     */   
/*     */   public long getSequenceNum() {
/* 198 */     return this.sequenceNum;
/*     */   }
/*     */   
/*     */   public int getStatus() {
/* 202 */     return getStatus(this.statusAndLogId.get());
/*     */   }
/*     */   
/*     */   RootReference<Long, Record<?, ?>>[] getUndoLogRootReferences() {
/* 206 */     return this.undoLogRootReferences;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private long setStatus(int paramInt) {
/*     */     while (true) {
/*     */       boolean bool;
/* 216 */       long l1 = this.statusAndLogId.get();
/* 217 */       long l2 = getLogId(l1);
/* 218 */       int i = getStatus(l1);
/*     */       
/* 220 */       switch (paramInt) {
/*     */         case 4:
/* 222 */           bool = (i == 1) ? true : false;
/*     */           break;
/*     */         case 2:
/* 225 */           bool = (i == 1) ? true : false;
/*     */           break;
/*     */         case 3:
/* 228 */           bool = (i == 1 || i == 2 || i == 3) ? true : false;
/*     */           break;
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         case 5:
/* 235 */           bool = (i == 1 || i == 2 || i == 4) ? true : false;
/*     */           break;
/*     */ 
/*     */         
/*     */         case 0:
/* 240 */           bool = (i == 3 || i == 5) ? true : false;
/*     */           break;
/*     */ 
/*     */         
/*     */         default:
/* 245 */           bool = false;
/*     */           break;
/*     */       } 
/* 248 */       if (!bool)
/* 249 */         throw DataUtils.newMVStoreException(103, "Transaction was illegally transitioned from {0} to {1}", new Object[] {
/*     */ 
/*     */               
/* 252 */               getStatusName(i), getStatusName(paramInt)
/*     */             }); 
/* 254 */       long l3 = composeState(paramInt, l2, hasRollback(l1));
/* 255 */       if (this.statusAndLogId.compareAndSet(l1, l3)) {
/* 256 */         return l1;
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasChanges() {
/* 267 */     return hasChanges(this.statusAndLogId.get());
/*     */   }
/*     */   
/*     */   public void setName(String paramString) {
/* 271 */     checkNotClosed();
/* 272 */     this.name = paramString;
/* 273 */     this.store.storeTransaction(this);
/*     */   }
/*     */   
/*     */   public String getName() {
/* 277 */     return this.name;
/*     */   }
/*     */   
/*     */   public int getBlockerId() {
/* 281 */     Transaction transaction = this.blockingTransaction;
/* 282 */     return (transaction == null) ? 0 : transaction.ownerId;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long setSavepoint() {
/* 291 */     return getLogId();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasStatementDependencies() {
/* 300 */     return !this.transactionMaps.isEmpty();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IsolationLevel getIsolationLevel() {
/* 309 */     return this.isolationLevel;
/*     */   }
/*     */   
/*     */   boolean isReadCommitted() {
/* 313 */     return (this.isolationLevel == IsolationLevel.READ_COMMITTED);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean allowNonRepeatableRead() {
/* 321 */     return this.isolationLevel.allowNonRepeatableRead();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void markStatementStart(HashSet<MVMap<Object, VersionedValue<Object>>> paramHashSet) {
/* 332 */     markStatementEnd();
/* 333 */     if (this.txCounter == null) {
/* 334 */       this.txCounter = this.store.store.registerVersionUsage();
/*     */     }
/*     */     
/* 337 */     if (paramHashSet != null && !paramHashSet.isEmpty()) {
/*     */       BitSet bitSet;
/*     */ 
/*     */ 
/*     */       
/*     */       do {
/* 343 */         bitSet = this.store.committingTransactions.get();
/* 344 */         for (MVMap<?, VersionedValue<?>> mVMap : paramHashSet) {
/* 345 */           TransactionMap<?, ?> transactionMap = openMapX(mVMap);
/* 346 */           transactionMap.setStatementSnapshot(new Snapshot<>(mVMap.flushAndGetRoot(), bitSet));
/*     */         } 
/* 348 */         if (!isReadCommitted())
/* 349 */           continue;  this.undoLogRootReferences = this.store.collectUndoLogRootReferences();
/*     */       }
/* 351 */       while (bitSet != this.store.committingTransactions.get());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 357 */       for (MVMap<?, VersionedValue<?>> mVMap : paramHashSet) {
/* 358 */         TransactionMap<?, ?> transactionMap = openMapX(mVMap);
/* 359 */         transactionMap.promoteSnapshot();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void markStatementEnd() {
/* 368 */     if (allowNonRepeatableRead()) {
/* 369 */       releaseSnapshot();
/*     */     }
/* 371 */     for (TransactionMap<?, ?> transactionMap : this.transactionMaps.values()) {
/* 372 */       transactionMap.setStatementSnapshot(null);
/*     */     }
/*     */   }
/*     */   
/*     */   private void markTransactionEnd() {
/* 377 */     if (!allowNonRepeatableRead()) {
/* 378 */       releaseSnapshot();
/*     */     }
/*     */   }
/*     */   
/*     */   private void releaseSnapshot() {
/* 383 */     this.transactionMaps.clear();
/* 384 */     this.undoLogRootReferences = null;
/* 385 */     MVStore.TxCounter txCounter = this.txCounter;
/* 386 */     if (txCounter != null) {
/* 387 */       this.txCounter = null;
/* 388 */       this.store.store.deregisterVersionUsage(txCounter);
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
/*     */   long log(Record<?, ?> paramRecord) {
/* 400 */     long l1 = this.statusAndLogId.getAndIncrement();
/* 401 */     long l2 = getLogId(l1);
/* 402 */     if (l2 >= 1099511627776L)
/* 403 */       throw DataUtils.newMVStoreException(104, "Transaction {0} has too many changes", new Object[] {
/*     */ 
/*     */             
/* 406 */             Integer.valueOf(this.transactionId)
/*     */           }); 
/* 408 */     int i = getStatus(l1);
/* 409 */     checkOpen(i);
/* 410 */     return this.store.addUndoLogRecord(this.transactionId, l2, paramRecord);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void logUndo() {
/* 418 */     long l1 = this.statusAndLogId.decrementAndGet();
/* 419 */     long l2 = getLogId(l1);
/* 420 */     if (l2 >= 1099511627776L)
/* 421 */       throw DataUtils.newMVStoreException(100, "Transaction {0} has internal error", new Object[] {
/*     */ 
/*     */             
/* 424 */             Integer.valueOf(this.transactionId)
/*     */           }); 
/* 426 */     int i = getStatus(l1);
/* 427 */     checkOpen(i);
/* 428 */     this.store.removeUndoLogRecord(this.transactionId);
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
/*     */   public <K, V> TransactionMap<K, V> openMap(String paramString) {
/* 440 */     return openMap(paramString, null, null);
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
/*     */   public <K, V> TransactionMap<K, V> openMap(String paramString, DataType<K> paramDataType, DataType<V> paramDataType1) {
/* 456 */     MVMap<K, VersionedValue<V>> mVMap = this.store.openVersionedMap(paramString, paramDataType, paramDataType1);
/* 457 */     return openMapX(mVMap);
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
/*     */   public <K, V> TransactionMap<K, V> openMapX(MVMap<K, VersionedValue<V>> paramMVMap) {
/* 470 */     checkNotClosed();
/* 471 */     int i = paramMVMap.getId();
/* 472 */     TransactionMap<K, V> transactionMap = (TransactionMap)this.transactionMaps.get(Integer.valueOf(i));
/* 473 */     if (transactionMap == null) {
/* 474 */       transactionMap = new TransactionMap<>(this, paramMVMap);
/* 475 */       this.transactionMaps.put(Integer.valueOf(i), transactionMap);
/*     */     } 
/* 477 */     return transactionMap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void prepare() {
/* 485 */     setStatus(2);
/* 486 */     this.store.storeTransaction(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void commit() {
/* 493 */     assert ((VersionedBitSet)this.store.openTransactions.get()).get(this.transactionId);
/* 494 */     markTransactionEnd();
/* 495 */     Throwable throwable = null;
/* 496 */     boolean bool = false;
/* 497 */     int i = 1;
/*     */     try {
/* 499 */       long l = setStatus(3);
/* 500 */       bool = hasChanges(l);
/* 501 */       i = getStatus(l);
/* 502 */       if (bool) {
/* 503 */         this.store.commit(this, (i == 3));
/*     */       }
/* 505 */     } catch (Throwable throwable1) {
/* 506 */       throwable = throwable1;
/* 507 */       throw throwable1;
/*     */     } finally {
/* 509 */       if (isActive(i)) {
/*     */         try {
/* 511 */           this.store.endTransaction(this, bool);
/* 512 */         } catch (Throwable throwable1) {
/* 513 */           if (throwable == null) {
/* 514 */             throw throwable1;
/*     */           }
/* 516 */           throwable.addSuppressed(throwable1);
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
/*     */   public void rollbackToSavepoint(long paramLong) {
/*     */     boolean bool;
/* 530 */     long l1 = setStatus(4);
/* 531 */     long l2 = getLogId(l1);
/*     */     
/*     */     try {
/* 534 */       this.store.rollbackTo(this, l2, paramLong);
/*     */     } finally {
/* 536 */       notifyAllWaitingTransactions();
/* 537 */       long l3 = composeState(4, l2, hasRollback(l1));
/* 538 */       long l4 = composeState(1, paramLong, true);
/*     */       do {
/* 540 */         bool = this.statusAndLogId.compareAndSet(l3, l4);
/* 541 */       } while (!bool && this.statusAndLogId.get() == l3);
/*     */     } 
/*     */     
/* 544 */     if (!bool) {
/* 545 */       throw DataUtils.newMVStoreException(103, "Transaction {0} concurrently modified while rollback to savepoint was in progress", new Object[] {
/*     */ 
/*     */             
/* 548 */             Integer.valueOf(this.transactionId)
/*     */           });
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void rollback() {
/* 556 */     markTransactionEnd();
/* 557 */     Throwable throwable = null;
/* 558 */     int i = 1;
/*     */     try {
/* 560 */       long l1 = setStatus(5);
/* 561 */       i = getStatus(l1);
/* 562 */       long l2 = getLogId(l1);
/* 563 */       if (l2 > 0L) {
/* 564 */         this.store.rollbackTo(this, l2, 0L);
/*     */       }
/* 566 */     } catch (Throwable throwable1) {
/* 567 */       i = getStatus();
/* 568 */       if (isActive(i)) {
/* 569 */         throwable = throwable1;
/* 570 */         throw throwable1;
/*     */       } 
/*     */     } finally {
/*     */       try {
/* 574 */         if (isActive(i)) {
/* 575 */           this.store.endTransaction(this, true);
/*     */         }
/* 577 */       } catch (Throwable throwable1) {
/* 578 */         if (throwable == null) {
/* 579 */           throw throwable1;
/*     */         }
/* 581 */         throwable.addSuppressed(throwable1);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean isActive(int paramInt) {
/* 588 */     return (paramInt != 0 && paramInt != 3 && paramInt != 5);
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
/*     */   public Iterator<TransactionStore.Change> getChanges(long paramLong) {
/* 603 */     return this.store.getChanges(this, getLogId(), paramLong);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTimeoutMillis(int paramInt) {
/* 612 */     this.timeoutMillis = (paramInt > 0) ? paramInt : this.store.timeoutMillis;
/*     */   }
/*     */   
/*     */   private long getLogId() {
/* 616 */     return getLogId(this.statusAndLogId.get());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void checkOpen(int paramInt) {
/* 623 */     if (paramInt != 1) {
/* 624 */       throw DataUtils.newMVStoreException(103, "Transaction {0} has status {1}, not OPEN", new Object[] {
/*     */             
/* 626 */             Integer.valueOf(this.transactionId), getStatusName(paramInt)
/*     */           });
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void checkNotClosed() {
/* 634 */     if (getStatus() == 0) {
/* 635 */       throw DataUtils.newMVStoreException(4, "Transaction {0} is closed", new Object[] {
/* 636 */             Integer.valueOf(this.transactionId)
/*     */           });
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   void closeIt() {
/* 644 */     this.transactionMaps.clear();
/* 645 */     long l = setStatus(0);
/* 646 */     this.store.store.deregisterVersionUsage(this.txCounter);
/* 647 */     if (hasChanges(l) || hasRollback(l)) {
/* 648 */       notifyAllWaitingTransactions();
/*     */     }
/*     */   }
/*     */   
/*     */   private void notifyAllWaitingTransactions() {
/* 653 */     if (this.notificationRequested) {
/* 654 */       synchronized (this) {
/* 655 */         notifyAll();
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
/*     */   public boolean waitFor(Transaction paramTransaction, String paramString, Object paramObject) {
/* 670 */     this.blockingTransaction = paramTransaction;
/* 671 */     this.blockingMapName = paramString;
/* 672 */     this.blockingKey = paramObject;
/* 673 */     if (isDeadlocked(paramTransaction)) {
/* 674 */       tryThrowDeadLockException(false);
/*     */     }
/* 676 */     boolean bool = paramTransaction.waitForThisToEnd(this.timeoutMillis, this);
/* 677 */     this.blockingMapName = null;
/* 678 */     this.blockingKey = null;
/* 679 */     this.blockingTransaction = null;
/* 680 */     return bool;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isDeadlocked(Transaction paramTransaction) {
/* 686 */     Transaction transaction1 = paramTransaction;
/* 687 */     int i = this.store.getMaxTransactionId();
/* 688 */     Transaction transaction2 = paramTransaction; Transaction transaction3;
/* 689 */     for (; (transaction3 = transaction2.blockingTransaction) != null && transaction2.getStatus() == 1 && i > 0; 
/* 690 */       transaction2 = transaction3, i--) {
/*     */       
/* 692 */       if (transaction3.sequenceNum > transaction1.sequenceNum) {
/* 693 */         transaction1 = transaction3;
/*     */       }
/*     */       
/* 696 */       if (transaction3 == this) {
/* 697 */         if (transaction1 == this) {
/* 698 */           return true;
/*     */         }
/* 700 */         Transaction transaction = transaction1.blockingTransaction;
/* 701 */         if (transaction != null) {
/* 702 */           transaction1.setStatus(4);
/* 703 */           transaction.notifyAllWaitingTransactions();
/* 704 */           return false;
/*     */         } 
/*     */       } 
/*     */     } 
/* 708 */     return false;
/*     */   }
/*     */   
/*     */   private void tryThrowDeadLockException(boolean paramBoolean) {
/* 712 */     BitSet bitSet = new BitSet();
/*     */     
/* 714 */     StringBuilder stringBuilder = new StringBuilder(String.format("Transaction %d has been chosen as a deadlock victim. Details:%n", new Object[] { Integer.valueOf(this.transactionId) }));
/* 715 */     Transaction transaction1 = this; Transaction transaction2;
/* 716 */     for (; !bitSet.get(transaction1.transactionId) && (transaction2 = transaction1.blockingTransaction) != null; transaction1 = transaction2) {
/* 717 */       bitSet.set(transaction1.transactionId);
/* 718 */       stringBuilder.append(String.format("Transaction %d attempts to update map <%s> entry with key <%s> modified by transaction %s%n", new Object[] {
/*     */               
/* 720 */               Integer.valueOf(transaction1.transactionId), transaction1.blockingMapName, transaction1.blockingKey, transaction1.blockingTransaction }));
/* 721 */       if (transaction2 == this) {
/* 722 */         paramBoolean = true;
/*     */       }
/*     */     } 
/* 725 */     if (paramBoolean) {
/* 726 */       throw DataUtils.newMVStoreException(105, "{0}", new Object[] { stringBuilder.toString() });
/*     */     }
/*     */   }
/*     */   
/*     */   private synchronized boolean waitForThisToEnd(int paramInt, Transaction paramTransaction) {
/* 731 */     long l1 = System.currentTimeMillis() + paramInt;
/* 732 */     this.notificationRequested = true;
/*     */     long l2;
/*     */     int i;
/* 735 */     while ((i = getStatus(l2 = this.statusAndLogId.get())) != 0 && i != 5 && 
/* 736 */       !hasRollback(l2)) {
/* 737 */       if (paramTransaction.getStatus() != 1) {
/* 738 */         paramTransaction.tryThrowDeadLockException(true);
/*     */       }
/* 740 */       long l = l1 - System.currentTimeMillis();
/* 741 */       if (l <= 0L) {
/* 742 */         return false;
/*     */       }
/*     */       try {
/* 745 */         wait(l);
/* 746 */       } catch (InterruptedException interruptedException) {
/* 747 */         return false;
/*     */       } 
/*     */     } 
/* 750 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <K, V> void removeMap(TransactionMap<K, V> paramTransactionMap) {
/* 761 */     this.store.removeMap(paramTransactionMap);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 766 */     return this.transactionId + "(" + this.sequenceNum + ") " + stateToString();
/*     */   }
/*     */   
/*     */   private String stateToString() {
/* 770 */     return stateToString(this.statusAndLogId.get());
/*     */   }
/*     */   
/*     */   private static String stateToString(long paramLong) {
/* 774 */     return getStatusName(getStatus(paramLong)) + (hasRollback(paramLong) ? "<" : "") + " " + getLogId(paramLong);
/*     */   }
/*     */ 
/*     */   
/*     */   private static int getStatus(long paramLong) {
/* 779 */     return (int)(paramLong >>> 41L) & 0xF;
/*     */   }
/*     */   
/*     */   private static long getLogId(long paramLong) {
/* 783 */     return paramLong & 0x1FFFFFFFFFFL;
/*     */   }
/*     */   
/*     */   private static boolean hasRollback(long paramLong) {
/* 787 */     return ((paramLong & 0x200000000000L) != 0L);
/*     */   }
/*     */   
/*     */   private static boolean hasChanges(long paramLong) {
/* 791 */     return (getLogId(paramLong) != 0L);
/*     */   }
/*     */   
/*     */   private static long composeState(int paramInt, long paramLong, boolean paramBoolean) {
/* 795 */     assert paramLong < 1099511627776L : paramLong;
/* 796 */     assert (paramInt & 0xFFFFFFF0) == 0 : paramInt;
/*     */     
/* 798 */     if (paramBoolean) {
/* 799 */       paramInt |= 0x10;
/*     */     }
/* 801 */     return paramInt << 41L | paramLong;
/*     */   }
/*     */   
/*     */   private static String getStatusName(int paramInt) {
/* 805 */     return (paramInt >= 0 && paramInt < STATUS_NAMES.length) ? STATUS_NAMES[paramInt] : ("UNKNOWN_STATUS_" + paramInt);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\tx\Transaction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */