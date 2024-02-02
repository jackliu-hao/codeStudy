/*      */ package org.h2.mvstore.tx;
/*      */ 
/*      */ import java.util.AbstractMap;
/*      */ import java.util.AbstractSet;
/*      */ import java.util.BitSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.Map;
/*      */ import java.util.NoSuchElementException;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.atomic.AtomicReference;
/*      */ import java.util.function.BiFunction;
/*      */ import org.h2.engine.IsolationLevel;
/*      */ import org.h2.mvstore.Cursor;
/*      */ import org.h2.mvstore.DataUtils;
/*      */ import org.h2.mvstore.MVMap;
/*      */ import org.h2.mvstore.MVStoreException;
/*      */ import org.h2.mvstore.RootReference;
/*      */ import org.h2.mvstore.type.DataType;
/*      */ import org.h2.value.VersionedValue;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class TransactionMap<K, V>
/*      */   extends AbstractMap<K, V>
/*      */ {
/*      */   public final MVMap<K, VersionedValue<V>> map;
/*      */   private final Transaction transaction;
/*      */   private Snapshot<K, VersionedValue<V>> snapshot;
/*      */   private Snapshot<K, VersionedValue<V>> statementSnapshot;
/*      */   private boolean hasChanges;
/*      */   private final TxDecisionMaker<K, V> txDecisionMaker;
/*      */   private final TxDecisionMaker<K, V> ifAbsentDecisionMaker;
/*      */   private final TxDecisionMaker<K, V> lockDecisionMaker;
/*      */   
/*      */   TransactionMap(Transaction paramTransaction, MVMap<K, VersionedValue<V>> paramMVMap) {
/*   78 */     this.transaction = paramTransaction;
/*   79 */     this.map = paramMVMap;
/*   80 */     this.txDecisionMaker = new TxDecisionMaker<>(paramMVMap.getId(), paramTransaction);
/*   81 */     this.ifAbsentDecisionMaker = new TxDecisionMaker.PutIfAbsentDecisionMaker<>(paramMVMap.getId(), paramTransaction, this::getFromSnapshot);
/*      */     
/*   83 */     this
/*      */ 
/*      */       
/*   86 */       .lockDecisionMaker = paramTransaction.allowNonRepeatableRead() ? new TxDecisionMaker.LockDecisionMaker<>(paramMVMap.getId(), paramTransaction) : new TxDecisionMaker.RepeatableReadLockDecisionMaker<>(paramMVMap.getId(), paramTransaction, paramMVMap.getValueType(), this::getFromSnapshot);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TransactionMap<K, V> getInstance(Transaction paramTransaction) {
/*   97 */     return paramTransaction.openMapX(this.map);
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
/*      */   public int size() {
/*  109 */     long l = sizeAsLong();
/*  110 */     return (l > 2147483647L) ? Integer.MAX_VALUE : (int)l;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long sizeAsLongMax() {
/*  120 */     return this.map.sizeAsLong();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long sizeAsLong() {
/*      */     Snapshot<K, VersionedValue<V>> snapshot;
/*      */     RootReference[] arrayOfRootReference;
/*  129 */     IsolationLevel isolationLevel = this.transaction.getIsolationLevel();
/*  130 */     if (!isolationLevel.allowNonRepeatableRead() && this.hasChanges) {
/*  131 */       return sizeAsLongRepeatableReadWithChanges();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     do {
/*  139 */       snapshot = getSnapshot();
/*  140 */       arrayOfRootReference = (RootReference[])getTransaction().getUndoLogRootReferences();
/*  141 */     } while (!snapshot.equals(getSnapshot()));
/*      */     
/*  143 */     RootReference<K, VersionedValue<V>> rootReference = snapshot.root;
/*  144 */     long l1 = rootReference.getTotalCount();
/*      */     
/*  146 */     long l2 = (arrayOfRootReference == null) ? l1 : TransactionStore.calculateUndoLogsTotalSize((RootReference<Long, Record<?, ?>>[])arrayOfRootReference);
/*      */     
/*  148 */     if (l2 == 0L) {
/*  149 */       return l1;
/*      */     }
/*  151 */     return adjustSize((RootReference<Long, Record<?, ?>>[])arrayOfRootReference, rootReference, (isolationLevel == IsolationLevel.READ_UNCOMMITTED) ? null : snapshot.committingTransactions, l1, l2);
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
/*      */   private long adjustSize(RootReference<Long, Record<?, ?>>[] paramArrayOfRootReference, RootReference<K, VersionedValue<V>> paramRootReference, BitSet paramBitSet, long paramLong1, long paramLong2) {
/*  163 */     if (2L * paramLong2 > paramLong1) {
/*      */       
/*  165 */       Cursor cursor = this.map.cursor(paramRootReference, null, null, false);
/*  166 */       while (cursor.hasNext()) {
/*  167 */         cursor.next();
/*  168 */         VersionedValue<?> versionedValue = (VersionedValue)cursor.getValue();
/*  169 */         assert versionedValue != null;
/*  170 */         long l = versionedValue.getOperationId();
/*  171 */         if (l != 0L && 
/*  172 */           isIrrelevant(l, versionedValue, paramBitSet)) {
/*  173 */           paramLong1--;
/*      */         }
/*      */       } 
/*      */     } else {
/*  177 */       assert paramArrayOfRootReference != null;
/*      */ 
/*      */       
/*  180 */       for (RootReference<Long, Record<?, ?>> rootReference : paramArrayOfRootReference) {
/*  181 */         if (rootReference != null) {
/*  182 */           Cursor cursor = rootReference.root.map.cursor(rootReference, null, null, false);
/*      */           
/*  184 */           while (cursor.hasNext()) {
/*  185 */             cursor.next();
/*  186 */             Record record = (Record)cursor.getValue();
/*  187 */             if (record.mapId == this.map.getId()) {
/*      */               
/*  189 */               VersionedValue<?> versionedValue = (VersionedValue)this.map.get(paramRootReference.root, record.key);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/*  196 */               if (versionedValue != null) {
/*      */ 
/*      */                 
/*  199 */                 long l = ((Long)cursor.getKey()).longValue();
/*  200 */                 assert l != 0L;
/*  201 */                 if (versionedValue.getOperationId() == l && 
/*  202 */                   isIrrelevant(l, versionedValue, paramBitSet)) {
/*  203 */                   paramLong1--;
/*      */                 }
/*      */               } 
/*      */             } 
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/*  211 */     return paramLong1;
/*      */   }
/*      */   
/*      */   private boolean isIrrelevant(long paramLong, VersionedValue<?> paramVersionedValue, BitSet paramBitSet) {
/*      */     Object object;
/*  216 */     if (paramBitSet == null) {
/*  217 */       object = paramVersionedValue.getCurrentValue();
/*      */     } else {
/*  219 */       int i = TransactionStore.getTransactionId(paramLong);
/*      */       
/*  221 */       object = (i == this.transaction.transactionId || paramBitSet.get(i)) ? paramVersionedValue.getCurrentValue() : paramVersionedValue.getCommittedValue();
/*      */     } 
/*  223 */     return (object == null);
/*      */   }
/*      */   
/*      */   private long sizeAsLongRepeatableReadWithChanges() {
/*  227 */     long l = 0L;
/*  228 */     RepeatableIterator<Object, Object, Object> repeatableIterator = new RepeatableIterator<>(this, null, null, false, false);
/*  229 */     while (repeatableIterator.fetchNext() != null) {
/*  230 */       l++;
/*      */     }
/*  232 */     return l;
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
/*      */   public V remove(Object paramObject) {
/*  248 */     return set((K)paramObject, (V)null);
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
/*      */   public V put(K paramK, V paramV) {
/*  264 */     DataUtils.checkArgument((paramV != null), "The value may not be null", new Object[0]);
/*  265 */     return set(paramK, paramV);
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
/*      */   public V putIfAbsent(K paramK, V paramV) {
/*  279 */     DataUtils.checkArgument((paramV != null), "The value may not be null", new Object[0]);
/*  280 */     this.ifAbsentDecisionMaker.initialize(paramK, paramV);
/*  281 */     V v = set(paramK, this.ifAbsentDecisionMaker);
/*  282 */     if (this.ifAbsentDecisionMaker.getDecision() == MVMap.Decision.ABORT) {
/*  283 */       v = this.ifAbsentDecisionMaker.getLastValue();
/*      */     }
/*  285 */     return v;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void append(K paramK, V paramV) {
/*  295 */     this.map.append(paramK, VersionedValueUncommitted.getInstance(this.transaction
/*  296 */           .log(new Record<>(this.map.getId(), paramK, null)), paramV, null));
/*  297 */     this.hasChanges = true;
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
/*      */   public V lock(K paramK) {
/*  311 */     this.lockDecisionMaker.initialize(paramK, null);
/*  312 */     return set(paramK, this.lockDecisionMaker);
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
/*      */   public V putCommitted(K paramK, V paramV) {
/*  324 */     DataUtils.checkArgument((paramV != null), "The value may not be null", new Object[0]);
/*  325 */     VersionedValue<V> versionedValue = VersionedValueCommitted.getInstance(paramV);
/*  326 */     VersionedValue versionedValue1 = (VersionedValue)this.map.put(paramK, versionedValue);
/*  327 */     return (V)((versionedValue1 == null) ? null : versionedValue1.getCurrentValue());
/*      */   }
/*      */ 
/*      */   
/*      */   private V set(K paramK, V paramV) {
/*  332 */     this.txDecisionMaker.initialize(paramK, paramV);
/*  333 */     return set(paramK, this.txDecisionMaker);
/*      */   }
/*      */   
/*      */   private V set(Object paramObject, TxDecisionMaker<K, V> paramTxDecisionMaker) {
/*      */     Transaction transaction;
/*      */     VersionedValue versionedValue;
/*  339 */     String str = null;
/*      */     do {
/*  341 */       assert this.transaction.getBlockerId() == 0;
/*      */       
/*  343 */       Object object = paramObject;
/*      */ 
/*      */       
/*  346 */       versionedValue = (VersionedValue)this.map.operate(object, null, paramTxDecisionMaker);
/*      */       
/*  348 */       MVMap.Decision decision = paramTxDecisionMaker.getDecision();
/*  349 */       assert decision != null;
/*  350 */       assert decision != MVMap.Decision.REPEAT;
/*  351 */       transaction = paramTxDecisionMaker.getBlockingTransaction();
/*  352 */       if (decision != MVMap.Decision.ABORT || transaction == null) {
/*  353 */         this.hasChanges |= (decision != MVMap.Decision.ABORT) ? 1 : 0;
/*  354 */         return (V)((versionedValue == null) ? null : versionedValue.getCurrentValue());
/*      */       } 
/*      */       
/*  357 */       paramTxDecisionMaker.reset();
/*  358 */       if (str != null)
/*  359 */         continue;  str = this.map.getName();
/*      */     }
/*  361 */     while (this.transaction.waitFor(transaction, str, paramObject));
/*      */     
/*  363 */     throw DataUtils.newMVStoreException(101, "Map entry <{0}> with key <{1}> and value {2} is locked by tx {3} and can not be updated by tx {4} within allocated time interval {5} ms.", new Object[] { str, paramObject, versionedValue, 
/*      */ 
/*      */           
/*  366 */           Integer.valueOf(transaction.transactionId), Integer.valueOf(this.transaction.transactionId), 
/*  367 */           Integer.valueOf(this.transaction.timeoutMillis) });
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
/*      */   public boolean tryRemove(K paramK) {
/*  380 */     return trySet(paramK, null);
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
/*      */   public boolean tryPut(K paramK, V paramV) {
/*  394 */     DataUtils.checkArgument((paramV != null), "The value may not be null", new Object[0]);
/*  395 */     return trySet(paramK, paramV);
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
/*      */   public boolean trySet(K paramK, V paramV) {
/*      */     try {
/*  413 */       set(paramK, paramV);
/*  414 */       return true;
/*  415 */     } catch (MVStoreException mVStoreException) {
/*  416 */       return false;
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
/*      */   public V get(Object paramObject) {
/*  430 */     return getImmediate((K)paramObject);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public V getFromSnapshot(K paramK) {
/*      */     VersionedValue versionedValue;
/*  441 */     switch (this.transaction.isolationLevel) {
/*      */       case READ_UNCOMMITTED:
/*  443 */         snapshot = getStatementSnapshot();
/*  444 */         versionedValue = (VersionedValue)this.map.get(snapshot.root.root, paramK);
/*  445 */         if (versionedValue != null) {
/*  446 */           return (V)versionedValue.getCurrentValue();
/*      */         }
/*  448 */         return null;
/*      */       
/*      */       case REPEATABLE_READ:
/*      */       case SNAPSHOT:
/*      */       case SERIALIZABLE:
/*  453 */         if (this.transaction.hasChanges()) {
/*  454 */           snapshot = getStatementSnapshot();
/*  455 */           versionedValue = (VersionedValue)this.map.get(snapshot.root.root, paramK);
/*  456 */           if (versionedValue != null) {
/*  457 */             long l = versionedValue.getOperationId();
/*  458 */             if (l != 0L && this.transaction.transactionId == TransactionStore.getTransactionId(l)) {
/*  459 */               return (V)versionedValue.getCurrentValue();
/*      */             }
/*      */           } 
/*      */         } 
/*      */         break;
/*      */     } 
/*      */     
/*  466 */     Snapshot<K, VersionedValue<V>> snapshot = getSnapshot();
/*  467 */     return getFromSnapshot(snapshot.root, snapshot.committingTransactions, paramK);
/*      */   }
/*      */ 
/*      */   
/*      */   private V getFromSnapshot(RootReference<K, VersionedValue<V>> paramRootReference, BitSet paramBitSet, K paramK) {
/*  472 */     VersionedValue versionedValue = (VersionedValue)this.map.get(paramRootReference.root, paramK);
/*  473 */     if (versionedValue == null)
/*      */     {
/*  475 */       return null;
/*      */     }
/*  477 */     long l = versionedValue.getOperationId();
/*  478 */     if (l != 0L) {
/*  479 */       int i = TransactionStore.getTransactionId(l);
/*  480 */       if (i != this.transaction.transactionId && !paramBitSet.get(i))
/*      */       {
/*  482 */         return (V)versionedValue.getCommittedValue();
/*      */       }
/*      */     } 
/*      */     
/*  486 */     return (V)versionedValue.getCurrentValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public V getImmediate(K paramK) {
/*  497 */     return useSnapshot((paramRootReference, paramBitSet) -> getFromSnapshot(paramRootReference, paramBitSet, (K)paramObject));
/*      */   }
/*      */ 
/*      */   
/*      */   Snapshot<K, VersionedValue<V>> getSnapshot() {
/*  502 */     return (this.snapshot == null) ? createSnapshot() : this.snapshot;
/*      */   }
/*      */   
/*      */   Snapshot<K, VersionedValue<V>> getStatementSnapshot() {
/*  506 */     return (this.statementSnapshot == null) ? createSnapshot() : this.statementSnapshot;
/*      */   }
/*      */   
/*      */   void setStatementSnapshot(Snapshot<K, VersionedValue<V>> paramSnapshot) {
/*  510 */     this.statementSnapshot = paramSnapshot;
/*      */   }
/*      */   
/*      */   void promoteSnapshot() {
/*  514 */     if (this.snapshot == null) {
/*  515 */       this.snapshot = this.statementSnapshot;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Snapshot<K, VersionedValue<V>> createSnapshot() {
/*  525 */     return useSnapshot(Snapshot::new);
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
/*      */   <R> R useSnapshot(BiFunction<RootReference<K, VersionedValue<V>>, BitSet, R> paramBiFunction) {
/*  544 */     AtomicReference<BitSet> atomicReference = this.transaction.store.committingTransactions;
/*  545 */     BitSet bitSet = atomicReference.get();
/*      */     while (true) {
/*  547 */       BitSet bitSet1 = bitSet;
/*  548 */       RootReference<K, VersionedValue<V>> rootReference = this.map.getRoot();
/*  549 */       bitSet = atomicReference.get();
/*  550 */       if (bitSet == bitSet1) {
/*  551 */         return paramBiFunction.apply(rootReference, bitSet);
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
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean containsKey(Object paramObject) {
/*  566 */     return (getImmediate((K)paramObject) != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isDeletedByCurrentTransaction(K paramK) {
/*  576 */     VersionedValue versionedValue = (VersionedValue)this.map.get(paramK);
/*  577 */     if (versionedValue != null) {
/*  578 */       long l = versionedValue.getOperationId();
/*  579 */       return (l != 0L && TransactionStore.getTransactionId(l) == this.transaction.transactionId && versionedValue
/*  580 */         .getCurrentValue() == null);
/*      */     } 
/*  582 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isSameTransaction(K paramK) {
/*  593 */     VersionedValue versionedValue = (VersionedValue)this.map.get(paramK);
/*  594 */     if (versionedValue == null)
/*      */     {
/*  596 */       return false;
/*      */     }
/*  598 */     int i = TransactionStore.getTransactionId(versionedValue.getOperationId());
/*  599 */     return (i == this.transaction.transactionId);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isClosed() {
/*  608 */     return this.map.isClosed();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void clear() {
/*  617 */     this.map.clear();
/*  618 */     this.hasChanges = true;
/*      */   }
/*      */ 
/*      */   
/*      */   public Set<Map.Entry<K, V>> entrySet() {
/*  623 */     return new AbstractSet<Map.Entry<K, V>>()
/*      */       {
/*      */         public Iterator<Map.Entry<K, V>> iterator()
/*      */         {
/*  627 */           return TransactionMap.this.entryIterator(null, null);
/*      */         }
/*      */ 
/*      */         
/*      */         public int size() {
/*  632 */           return TransactionMap.this.size();
/*      */         }
/*      */ 
/*      */         
/*      */         public boolean contains(Object param1Object) {
/*  637 */           return TransactionMap.this.containsKey(param1Object);
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
/*      */   public Map.Entry<K, V> firstEntry() {
/*  649 */     return (Map.Entry<K, V>)chooseIterator(null, null, false, true).fetchNext();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public K firstKey() {
/*  658 */     return (K)chooseIterator(null, null, false, false).fetchNext();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Map.Entry<K, V> lastEntry() {
/*  667 */     return (Map.Entry<K, V>)chooseIterator(null, null, true, true).fetchNext();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public K lastKey() {
/*  676 */     return (K)chooseIterator(null, null, true, false).fetchNext();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Map.Entry<K, V> higherEntry(K paramK) {
/*  687 */     return higherLowerEntry(paramK, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public K higherKey(K paramK) {
/*  698 */     return higherLowerKey(paramK, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Map.Entry<K, V> ceilingEntry(K paramK) {
/*  709 */     return (Map.Entry<K, V>)chooseIterator(paramK, null, false, true).fetchNext();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public K ceilingKey(K paramK) {
/*  720 */     return (K)chooseIterator(paramK, null, false, false).fetchNext();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Map.Entry<K, V> floorEntry(K paramK) {
/*  731 */     return (Map.Entry<K, V>)chooseIterator(paramK, null, true, true).fetchNext();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public K floorKey(K paramK) {
/*  742 */     return (K)chooseIterator(paramK, null, true, false).fetchNext();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Map.Entry<K, V> lowerEntry(K paramK) {
/*  753 */     return higherLowerEntry(paramK, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public K lowerKey(K paramK) {
/*  764 */     return higherLowerKey(paramK, true);
/*      */   }
/*      */   
/*      */   private Map.Entry<K, V> higherLowerEntry(K paramK, boolean paramBoolean) {
/*  768 */     TMIterator<K, ?, ?> tMIterator = chooseIterator(paramK, null, paramBoolean, true);
/*  769 */     Map.Entry<K, V> entry = (Map.Entry)tMIterator.fetchNext();
/*  770 */     if (entry != null && this.map.getKeyType().compare(paramK, entry.getKey()) == 0) {
/*  771 */       entry = (Map.Entry)tMIterator.fetchNext();
/*      */     }
/*  773 */     return entry;
/*      */   }
/*      */   
/*      */   private K higherLowerKey(K paramK, boolean paramBoolean) {
/*  777 */     TMIterator<K, ?, ?> tMIterator = chooseIterator(paramK, null, paramBoolean, false);
/*  778 */     Object object = tMIterator.fetchNext();
/*  779 */     if (object != null && this.map.getKeyType().compare(paramK, object) == 0) {
/*  780 */       object = tMIterator.fetchNext();
/*      */     }
/*  782 */     return (K)object;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Iterator<K> keyIterator(K paramK) {
/*  792 */     return chooseIterator(paramK, null, false, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TMIterator<K, V, K> keyIterator(K paramK, boolean paramBoolean) {
/*  803 */     return chooseIterator(paramK, null, paramBoolean, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TMIterator<K, V, K> keyIterator(K paramK1, K paramK2) {
/*  814 */     return chooseIterator(paramK1, paramK2, false, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TMIterator<K, V, K> keyIteratorUncommitted(K paramK1, K paramK2) {
/*  825 */     return new ValidationIterator<>(this, paramK1, paramK2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TMIterator<K, V, Map.Entry<K, V>> entryIterator(K paramK1, K paramK2) {
/*  836 */     return chooseIterator(paramK1, paramK2, false, true);
/*      */   }
/*      */   
/*      */   private <X> TMIterator<K, V, X> chooseIterator(K paramK1, K paramK2, boolean paramBoolean1, boolean paramBoolean2) {
/*  840 */     switch (this.transaction.isolationLevel) {
/*      */       case READ_UNCOMMITTED:
/*  842 */         return new UncommittedIterator<>(this, paramK1, paramK2, paramBoolean1, paramBoolean2);
/*      */       case REPEATABLE_READ:
/*      */       case SNAPSHOT:
/*      */       case SERIALIZABLE:
/*  846 */         if (this.hasChanges) {
/*  847 */           return new RepeatableIterator<>(this, paramK1, paramK2, paramBoolean1, paramBoolean2);
/*      */         }
/*      */         break;
/*      */     } 
/*      */     
/*  852 */     return new CommittedIterator<>(this, paramK1, paramK2, paramBoolean1, paramBoolean2);
/*      */   }
/*      */ 
/*      */   
/*      */   public Transaction getTransaction() {
/*  857 */     return this.transaction;
/*      */   }
/*      */   
/*      */   public DataType<K> getKeyType() {
/*  861 */     return this.map.getKeyType();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static class UncommittedIterator<K, V, X>
/*      */     extends TMIterator<K, V, X>
/*      */   {
/*      */     UncommittedIterator(TransactionMap<K, V> param1TransactionMap, K param1K1, K param1K2, boolean param1Boolean1, boolean param1Boolean2) {
/*  875 */       super(param1TransactionMap, param1K1, param1K2, param1TransactionMap.createSnapshot(), param1Boolean1, param1Boolean2);
/*      */     }
/*      */ 
/*      */     
/*      */     UncommittedIterator(TransactionMap<K, V> param1TransactionMap, K param1K1, K param1K2, Snapshot<K, VersionedValue<V>> param1Snapshot, boolean param1Boolean1, boolean param1Boolean2) {
/*  880 */       super(param1TransactionMap, param1K1, param1K2, param1Snapshot, param1Boolean1, param1Boolean2);
/*      */     }
/*      */ 
/*      */     
/*      */     public final X fetchNext() {
/*  885 */       while (this.cursor.hasNext()) {
/*  886 */         Object object = this.cursor.next();
/*  887 */         VersionedValue<?> versionedValue = (VersionedValue)this.cursor.getValue();
/*  888 */         if (versionedValue != null) {
/*  889 */           Object object1 = versionedValue.getCurrentValue();
/*  890 */           if (object1 != null || shouldIgnoreRemoval(versionedValue)) {
/*  891 */             return toElement((K)object, object1);
/*      */           }
/*      */         } 
/*      */       } 
/*  895 */       return null;
/*      */     }
/*      */     
/*      */     boolean shouldIgnoreRemoval(VersionedValue<?> param1VersionedValue) {
/*  899 */       return false;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static final class ValidationIterator<K, V, X>
/*      */     extends UncommittedIterator<K, V, X>
/*      */   {
/*      */     ValidationIterator(TransactionMap<K, V> param1TransactionMap, K param1K1, K param1K2) {
/*  909 */       super(param1TransactionMap, param1K1, param1K2, param1TransactionMap.createSnapshot(), false, false);
/*      */     }
/*      */ 
/*      */     
/*      */     boolean shouldIgnoreRemoval(VersionedValue<?> param1VersionedValue) {
/*  914 */       assert param1VersionedValue.getCurrentValue() == null;
/*  915 */       long l = param1VersionedValue.getOperationId();
/*  916 */       if (l != 0L) {
/*  917 */         int i = TransactionStore.getTransactionId(l);
/*  918 */         return (this.transactionId != i && !this.committingTransactions.get(i));
/*      */       } 
/*  920 */       return false;
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
/*      */   private static final class CommittedIterator<K, V, X>
/*      */     extends TMIterator<K, V, X>
/*      */   {
/*      */     CommittedIterator(TransactionMap<K, V> param1TransactionMap, K param1K1, K param1K2, boolean param1Boolean1, boolean param1Boolean2) {
/*  935 */       super(param1TransactionMap, param1K1, param1K2, param1TransactionMap.getSnapshot(), param1Boolean1, param1Boolean2);
/*      */     }
/*      */ 
/*      */     
/*      */     public X fetchNext() {
/*  940 */       while (this.cursor.hasNext()) {
/*  941 */         Object object = this.cursor.next();
/*  942 */         VersionedValue versionedValue = (VersionedValue)this.cursor.getValue();
/*      */ 
/*      */         
/*  945 */         if (versionedValue != null) {
/*  946 */           long l = versionedValue.getOperationId();
/*  947 */           if (l != 0L) {
/*  948 */             int i = TransactionStore.getTransactionId(l);
/*  949 */             if (i != this.transactionId && !this.committingTransactions.get(i)) {
/*      */ 
/*      */               
/*  952 */               Object object2 = versionedValue.getCommittedValue();
/*  953 */               if (object2 == null) {
/*      */                 continue;
/*      */               }
/*  956 */               return toElement((K)object, object2);
/*      */             } 
/*      */           } 
/*  959 */           Object object1 = versionedValue.getCurrentValue();
/*  960 */           if (object1 != null) {
/*  961 */             return toElement((K)object, object1);
/*      */           }
/*      */         } 
/*      */       } 
/*  965 */       return null;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static final class RepeatableIterator<K, V, X>
/*      */     extends TMIterator<K, V, X>
/*      */   {
/*      */     private final DataType<K> keyType;
/*      */ 
/*      */     
/*      */     private K snapshotKey;
/*      */ 
/*      */     
/*      */     private Object snapshotValue;
/*      */ 
/*      */     
/*      */     private final Cursor<K, VersionedValue<V>> uncommittedCursor;
/*      */     
/*      */     private K uncommittedKey;
/*      */     
/*      */     private V uncommittedValue;
/*      */ 
/*      */     
/*      */     RepeatableIterator(TransactionMap<K, V> param1TransactionMap, K param1K1, K param1K2, boolean param1Boolean1, boolean param1Boolean2) {
/*  991 */       super(param1TransactionMap, param1K1, param1K2, param1TransactionMap.getSnapshot(), param1Boolean1, param1Boolean2);
/*  992 */       this.keyType = param1TransactionMap.map.getKeyType();
/*  993 */       Snapshot<K, VersionedValue<V>> snapshot = param1TransactionMap.getStatementSnapshot();
/*  994 */       this.uncommittedCursor = param1TransactionMap.map.cursor(snapshot.root, param1K1, param1K2, param1Boolean1);
/*      */     }
/*      */ 
/*      */     
/*      */     public X fetchNext() {
/*  999 */       X x = null;
/*      */       do {
/* 1001 */         if (this.snapshotKey == null) {
/* 1002 */           fetchSnapshot();
/*      */         }
/* 1004 */         if (this.uncommittedKey == null) {
/* 1005 */           fetchUncommitted();
/*      */         }
/* 1007 */         if (this.snapshotKey == null && this.uncommittedKey == null) {
/*      */           break;
/*      */         }
/*      */ 
/*      */         
/* 1012 */         boolean bool = (this.snapshotKey == null) ? true : ((this.uncommittedKey == null) ? true : this.keyType.compare(this.snapshotKey, this.uncommittedKey));
/* 1013 */         if (bool) {
/* 1014 */           x = toElement(this.snapshotKey, this.snapshotValue);
/* 1015 */           this.snapshotKey = null;
/*      */           break;
/*      */         } 
/* 1018 */         if (this.uncommittedValue != null)
/*      */         {
/* 1020 */           x = toElement(this.uncommittedKey, this.uncommittedValue);
/*      */         }
/* 1022 */         if (!bool) {
/* 1023 */           this.snapshotKey = null;
/*      */         }
/* 1025 */         this.uncommittedKey = null;
/* 1026 */       } while (x == null);
/* 1027 */       return x;
/*      */     }
/*      */     
/*      */     private void fetchSnapshot() {
/* 1031 */       while (this.cursor.hasNext()) {
/* 1032 */         Object object = this.cursor.next();
/* 1033 */         VersionedValue versionedValue = (VersionedValue)this.cursor.getValue();
/*      */ 
/*      */         
/* 1036 */         if (versionedValue != null) {
/* 1037 */           Object object1 = versionedValue.getCommittedValue();
/* 1038 */           long l = versionedValue.getOperationId();
/* 1039 */           if (l != 0L) {
/* 1040 */             int i = TransactionStore.getTransactionId(l);
/* 1041 */             if (i == this.transactionId || this.committingTransactions.get(i))
/*      */             {
/*      */               
/* 1044 */               object1 = versionedValue.getCurrentValue();
/*      */             }
/*      */           } 
/* 1047 */           if (object1 != null) {
/* 1048 */             this.snapshotKey = (K)object;
/* 1049 */             this.snapshotValue = object1;
/*      */             return;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     }
/*      */     
/*      */     private void fetchUncommitted() {
/* 1057 */       while (this.uncommittedCursor.hasNext()) {
/* 1058 */         Object object = this.uncommittedCursor.next();
/* 1059 */         VersionedValue versionedValue = (VersionedValue)this.uncommittedCursor.getValue();
/* 1060 */         if (versionedValue != null) {
/* 1061 */           long l = versionedValue.getOperationId();
/* 1062 */           if (l != 0L && this.transactionId == TransactionStore.getTransactionId(l)) {
/* 1063 */             this.uncommittedKey = (K)object;
/* 1064 */             this.uncommittedValue = (V)versionedValue.getCurrentValue();
/*      */             return;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public static abstract class TMIterator<K, V, X>
/*      */     implements Iterator<X>
/*      */   {
/*      */     final int transactionId;
/*      */     
/*      */     final BitSet committingTransactions;
/*      */     
/*      */     protected final Cursor<K, VersionedValue<V>> cursor;
/*      */     private final boolean forEntries;
/*      */     X current;
/*      */     
/*      */     TMIterator(TransactionMap<K, V> param1TransactionMap, K param1K1, K param1K2, Snapshot<K, VersionedValue<V>> param1Snapshot, boolean param1Boolean1, boolean param1Boolean2) {
/* 1085 */       Transaction transaction = param1TransactionMap.getTransaction();
/* 1086 */       this.transactionId = transaction.transactionId;
/* 1087 */       this.forEntries = param1Boolean2;
/* 1088 */       this.cursor = param1TransactionMap.map.cursor(param1Snapshot.root, param1K1, param1K2, param1Boolean1);
/* 1089 */       this.committingTransactions = param1Snapshot.committingTransactions;
/*      */     }
/*      */ 
/*      */     
/*      */     final X toElement(K param1K, Object param1Object) {
/* 1094 */       return this.forEntries ? (X)new AbstractMap.SimpleImmutableEntry<>(param1K, param1Object) : (X)param1K;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public abstract X fetchNext();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final boolean hasNext() {
/* 1109 */       return (this.current != null || (this.current = fetchNext()) != null);
/*      */     }
/*      */ 
/*      */     
/*      */     public final X next() {
/* 1114 */       X x = this.current;
/* 1115 */       if (x == null) {
/* 1116 */         if ((x = fetchNext()) == null) {
/* 1117 */           throw new NoSuchElementException();
/*      */         }
/*      */       } else {
/* 1120 */         this.current = null;
/*      */       } 
/* 1122 */       return x;
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\tx\TransactionMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */