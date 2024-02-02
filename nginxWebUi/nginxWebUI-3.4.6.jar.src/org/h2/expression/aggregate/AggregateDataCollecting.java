/*     */ package org.h2.expression.aggregate;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.TreeSet;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.value.CompareMode;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueNull;
/*     */ import org.h2.value.ValueRow;
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
/*     */ final class AggregateDataCollecting
/*     */   extends AggregateData
/*     */   implements Iterable<Value>
/*     */ {
/*     */   private final boolean distinct;
/*     */   private final boolean orderedWithOrder;
/*     */   private final NullCollectionMode nullCollectionMode;
/*     */   Collection<Value> values;
/*     */   private Value shared;
/*     */   
/*     */   enum NullCollectionMode
/*     */   {
/*  42 */     IGNORED,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  48 */     EXCLUDED,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  55 */     USED_OR_IMPOSSIBLE;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   AggregateDataCollecting(boolean paramBoolean1, boolean paramBoolean2, NullCollectionMode paramNullCollectionMode) {
/*  80 */     this.distinct = paramBoolean1;
/*  81 */     this.orderedWithOrder = paramBoolean2;
/*  82 */     this.nullCollectionMode = paramNullCollectionMode;
/*     */   }
/*     */ 
/*     */   
/*     */   void add(SessionLocal paramSessionLocal, Value paramValue) {
/*  87 */     if (this.nullCollectionMode == NullCollectionMode.IGNORED && isNull(paramValue)) {
/*     */       return;
/*     */     }
/*  90 */     Collection<Value> collection = this.values;
/*  91 */     if (collection == null) {
/*  92 */       if (this.distinct) {
/*  93 */         Comparator<?> comparator; CompareMode compareMode = paramSessionLocal.getDatabase().getCompareMode();
/*  94 */         if (this.orderedWithOrder) {
/*  95 */           comparator = Comparator.comparing(paramValue -> ((ValueRow)paramValue).getList()[0], (Comparator<?>)compareMode);
/*     */         }
/*  97 */         collection = new TreeSet(comparator);
/*     */       } else {
/*  99 */         collection = new ArrayList<>();
/*     */       } 
/* 101 */       this.values = collection;
/*     */     } 
/* 103 */     if (this.nullCollectionMode == NullCollectionMode.EXCLUDED && isNull(paramValue)) {
/*     */       return;
/*     */     }
/* 106 */     collection.add(paramValue);
/*     */   }
/*     */   
/*     */   private boolean isNull(Value paramValue) {
/* 110 */     return ((this.orderedWithOrder ? (ValueNull)((ValueRow)paramValue).getList()[0] : (ValueNull)paramValue) == ValueNull.INSTANCE);
/*     */   }
/*     */ 
/*     */   
/*     */   Value getValue(SessionLocal paramSessionLocal) {
/* 115 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int getCount() {
/* 124 */     return (this.values != null) ? this.values.size() : 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Value[] getArray() {
/* 133 */     Collection<Value> collection = this.values;
/* 134 */     if (collection == null) {
/* 135 */       return null;
/*     */     }
/* 137 */     return collection.<Value>toArray(Value.EMPTY_VALUES);
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<Value> iterator() {
/* 142 */     return (this.values != null) ? this.values.iterator() : Collections.<Value>emptyIterator();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void setSharedArgument(Value paramValue) {
/* 151 */     if (this.shared == null) {
/* 152 */       this.shared = paramValue;
/* 153 */     } else if (!this.shared.equals(paramValue)) {
/* 154 */       throw DbException.get(90008, new String[] { "Inverse distribution function argument", this.shared
/* 155 */             .getTraceSQL() + "<>" + paramValue.getTraceSQL() });
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Value getSharedArgument() {
/* 165 */     return this.shared;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\aggregate\AggregateDataCollecting.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */