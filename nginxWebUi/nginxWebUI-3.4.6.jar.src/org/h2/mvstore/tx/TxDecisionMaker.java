/*     */ package org.h2.mvstore.tx;
/*     */ 
/*     */ import java.util.BitSet;
/*     */ import java.util.function.Function;
/*     */ import org.h2.mvstore.DataUtils;
/*     */ import org.h2.mvstore.MVMap;
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
/*     */ class TxDecisionMaker<K, V>
/*     */   extends MVMap.DecisionMaker<VersionedValue<V>>
/*     */ {
/*     */   private final int mapId;
/*     */   protected K key;
/*     */   private V value;
/*     */   private final Transaction transaction;
/*     */   private long undoKey;
/*     */   private long lastOperationId;
/*     */   private Transaction blockingTransaction;
/*     */   private MVMap.Decision decision;
/*     */   private V lastValue;
/*     */   
/*     */   TxDecisionMaker(int paramInt, Transaction paramTransaction) {
/*  58 */     this.mapId = paramInt;
/*  59 */     this.transaction = paramTransaction;
/*     */   }
/*     */   
/*     */   void initialize(K paramK, V paramV) {
/*  63 */     this.key = paramK;
/*  64 */     this.value = paramV;
/*  65 */     this.decision = null;
/*  66 */     reset();
/*     */   }
/*     */ 
/*     */   
/*     */   public MVMap.Decision decide(VersionedValue<V> paramVersionedValue1, VersionedValue<V> paramVersionedValue2) {
/*  71 */     assert this.decision == null;
/*     */     
/*     */     long l;
/*     */     int i;
/*  75 */     if (paramVersionedValue1 == null || (
/*     */       
/*  77 */       l = paramVersionedValue1.getOperationId()) == 0L || 
/*     */       
/*  79 */       isThisTransaction(i = TransactionStore.getTransactionId(l))) {
/*  80 */       logAndDecideToPut(paramVersionedValue1, (paramVersionedValue1 == null) ? null : (V)paramVersionedValue1.getCommittedValue());
/*  81 */     } else if (isCommitted(i)) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  86 */       Object object = paramVersionedValue1.getCurrentValue();
/*  87 */       logAndDecideToPut((object == null) ? null : VersionedValueCommitted.<V>getInstance((V)object), (V)object);
/*     */     }
/*  89 */     else if (getBlockingTransaction() != null) {
/*     */ 
/*     */ 
/*     */       
/*  93 */       this.lastValue = (V)paramVersionedValue1.getCurrentValue();
/*  94 */       this.decision = MVMap.Decision.ABORT;
/*  95 */     } else if (isRepeatedOperation(l)) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 103 */       Object object = paramVersionedValue1.getCommittedValue();
/* 104 */       logAndDecideToPut((object == null) ? null : VersionedValueCommitted.<V>getInstance((V)object), (V)object);
/*     */     
/*     */     }
/*     */     else {
/*     */ 
/*     */       
/* 110 */       this.decision = MVMap.Decision.REPEAT;
/*     */     } 
/* 112 */     return this.decision;
/*     */   }
/*     */ 
/*     */   
/*     */   public final void reset() {
/* 117 */     if (this.decision != MVMap.Decision.REPEAT) {
/* 118 */       this.lastOperationId = 0L;
/* 119 */       if (this.decision == MVMap.Decision.PUT)
/*     */       {
/*     */         
/* 122 */         this.transaction.logUndo();
/*     */       }
/*     */     } 
/* 125 */     this.blockingTransaction = null;
/* 126 */     this.decision = null;
/* 127 */     this.lastValue = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T extends VersionedValue<V>> T selectValue(T paramT1, T paramT2) {
/* 134 */     return (T)VersionedValueUncommitted.getInstance(this.undoKey, getNewValue((VersionedValue<V>)paramT1), this.lastValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   V getNewValue(VersionedValue<V> paramVersionedValue) {
/* 145 */     return this.value;
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
/*     */   MVMap.Decision logAndDecideToPut(VersionedValue<V> paramVersionedValue, V paramV) {
/* 158 */     this.undoKey = this.transaction.log(new Record<>(this.mapId, this.key, paramVersionedValue));
/* 159 */     this.lastValue = paramV;
/* 160 */     return setDecision(MVMap.Decision.PUT);
/*     */   }
/*     */   
/*     */   final MVMap.Decision decideToAbort(V paramV) {
/* 164 */     this.lastValue = paramV;
/* 165 */     return setDecision(MVMap.Decision.ABORT);
/*     */   }
/*     */   
/*     */   final boolean allowNonRepeatableRead() {
/* 169 */     return this.transaction.allowNonRepeatableRead();
/*     */   }
/*     */   
/*     */   final MVMap.Decision getDecision() {
/* 173 */     return this.decision;
/*     */   }
/*     */   
/*     */   final Transaction getBlockingTransaction() {
/* 177 */     return this.blockingTransaction;
/*     */   }
/*     */   
/*     */   final V getLastValue() {
/* 181 */     return this.lastValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final boolean isThisTransaction(int paramInt) {
/* 192 */     return (paramInt == this.transaction.transactionId);
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
/*     */   final boolean isCommitted(int paramInt) {
/*     */     Transaction transaction;
/*     */     boolean bool;
/* 206 */     TransactionStore transactionStore = this.transaction.store;
/*     */     do {
/* 208 */       transaction = transactionStore.getTransaction(paramInt);
/* 209 */       bool = ((BitSet)transactionStore.committingTransactions.get()).get(paramInt);
/* 210 */     } while (transaction != transactionStore.getTransaction(paramInt));
/*     */     
/* 212 */     if (!bool) {
/* 213 */       this.blockingTransaction = transaction;
/*     */     }
/* 215 */     return bool;
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
/*     */   final boolean isRepeatedOperation(long paramLong) {
/* 229 */     if (paramLong == this.lastOperationId) {
/* 230 */       return true;
/*     */     }
/* 232 */     this.lastOperationId = paramLong;
/* 233 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final MVMap.Decision setDecision(MVMap.Decision paramDecision) {
/* 243 */     return this.decision = paramDecision;
/*     */   }
/*     */ 
/*     */   
/*     */   public final String toString() {
/* 248 */     return "txdm " + this.transaction.transactionId;
/*     */   }
/*     */   
/*     */   public static final class PutIfAbsentDecisionMaker<K, V>
/*     */     extends TxDecisionMaker<K, V>
/*     */   {
/*     */     private final Function<K, V> oldValueSupplier;
/*     */     
/*     */     PutIfAbsentDecisionMaker(int param1Int, Transaction param1Transaction, Function<K, V> param1Function) {
/* 257 */       super(param1Int, param1Transaction);
/* 258 */       this.oldValueSupplier = param1Function;
/*     */     }
/*     */ 
/*     */     
/*     */     public MVMap.Decision decide(VersionedValue<V> param1VersionedValue1, VersionedValue<V> param1VersionedValue2) {
/* 263 */       assert getDecision() == null;
/*     */ 
/*     */       
/* 266 */       if (param1VersionedValue1 == null) {
/* 267 */         V v = getValueInSnapshot();
/* 268 */         if (v != null)
/*     */         {
/*     */           
/* 271 */           return decideToAbort(v);
/*     */         }
/* 273 */         return logAndDecideToPut(null, null);
/*     */       } 
/* 275 */       long l = param1VersionedValue1.getOperationId(); int i;
/* 276 */       if (l == 0L || 
/*     */         
/* 278 */         isThisTransaction(i = TransactionStore.getTransactionId(l))) {
/* 279 */         if (param1VersionedValue1.getCurrentValue() != null) {
/* 280 */           return decideToAbort((V)param1VersionedValue1.getCurrentValue());
/*     */         }
/* 282 */         if (l == 0L) {
/* 283 */           V v = getValueInSnapshot();
/* 284 */           if (v != null) {
/* 285 */             return decideToAbort(v);
/*     */           }
/*     */         } 
/* 288 */         return logAndDecideToPut(param1VersionedValue1, (V)param1VersionedValue1.getCommittedValue());
/* 289 */       }  if (isCommitted(i)) {
/*     */ 
/*     */         
/* 292 */         if (param1VersionedValue1.getCurrentValue() != null) {
/* 293 */           return decideToAbort((V)param1VersionedValue1.getCurrentValue());
/*     */         }
/*     */ 
/*     */ 
/*     */         
/* 298 */         V v = getValueInSnapshot();
/* 299 */         if (v != null) {
/* 300 */           return decideToAbort(v);
/*     */         }
/* 302 */         return logAndDecideToPut(null, null);
/* 303 */       }  if (getBlockingTransaction() != null)
/*     */       {
/*     */ 
/*     */ 
/*     */         
/* 308 */         return decideToAbort((V)param1VersionedValue1.getCurrentValue()); } 
/* 309 */       if (isRepeatedOperation(l)) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 318 */         Object object = param1VersionedValue1.getCommittedValue();
/* 319 */         if (object != null) {
/* 320 */           return decideToAbort((V)object);
/*     */         }
/* 322 */         return logAndDecideToPut(null, null);
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 327 */       return setDecision(MVMap.Decision.REPEAT);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private V getValueInSnapshot() {
/* 333 */       return allowNonRepeatableRead() ? null : this.oldValueSupplier.apply(this.key);
/*     */     }
/*     */   }
/*     */   
/*     */   public static class LockDecisionMaker<K, V>
/*     */     extends TxDecisionMaker<K, V>
/*     */   {
/*     */     LockDecisionMaker(int param1Int, Transaction param1Transaction) {
/* 341 */       super(param1Int, param1Transaction);
/*     */     }
/*     */ 
/*     */     
/*     */     public MVMap.Decision decide(VersionedValue<V> param1VersionedValue1, VersionedValue<V> param1VersionedValue2) {
/* 346 */       MVMap.Decision decision = super.decide(param1VersionedValue1, param1VersionedValue2);
/* 347 */       if (param1VersionedValue1 == null) {
/* 348 */         assert decision == MVMap.Decision.PUT;
/* 349 */         decision = setDecision(MVMap.Decision.REMOVE);
/*     */       } 
/* 351 */       return decision;
/*     */     }
/*     */ 
/*     */     
/*     */     V getNewValue(VersionedValue<V> param1VersionedValue) {
/* 356 */       return (param1VersionedValue == null) ? null : (V)param1VersionedValue.getCurrentValue();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static final class RepeatableReadLockDecisionMaker<K, V>
/*     */     extends LockDecisionMaker<K, V>
/*     */   {
/*     */     private final DataType<VersionedValue<V>> valueType;
/*     */     private final Function<K, V> snapshotValueSupplier;
/*     */     
/*     */     RepeatableReadLockDecisionMaker(int param1Int, Transaction param1Transaction, DataType<VersionedValue<V>> param1DataType, Function<K, V> param1Function) {
/* 368 */       super(param1Int, param1Transaction);
/* 369 */       this.valueType = param1DataType;
/* 370 */       this.snapshotValueSupplier = param1Function;
/*     */     }
/*     */ 
/*     */     
/*     */     MVMap.Decision logAndDecideToPut(VersionedValue<V> param1VersionedValue, V param1V) {
/* 375 */       V v = this.snapshotValueSupplier.apply(this.key);
/* 376 */       if (v != null && (param1VersionedValue == null || this.valueType
/* 377 */         .compare(VersionedValueCommitted.getInstance(v), param1VersionedValue) != 0)) {
/* 378 */         throw DataUtils.newMVStoreException(105, "", new Object[0]);
/*     */       }
/* 380 */       return super.logAndDecideToPut(param1VersionedValue, param1V);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\tx\TxDecisionMaker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */