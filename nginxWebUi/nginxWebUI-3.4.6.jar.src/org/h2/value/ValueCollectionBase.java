/*     */ package org.h2.value;
/*     */ 
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.message.DbException;
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
/*     */ public abstract class ValueCollectionBase
/*     */   extends Value
/*     */ {
/*     */   final Value[] values;
/*     */   private int hash;
/*     */   
/*     */   ValueCollectionBase(Value[] paramArrayOfValue) {
/*  26 */     this.values = paramArrayOfValue;
/*     */   }
/*     */   
/*     */   public Value[] getList() {
/*  30 */     return this.values;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  35 */     if (this.hash != 0) {
/*  36 */       return this.hash;
/*     */     }
/*  38 */     int i = getValueType();
/*  39 */     for (Value value : this.values) {
/*  40 */       i = i * 31 + value.hashCode();
/*     */     }
/*  42 */     this.hash = i;
/*  43 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareWithNull(Value paramValue, boolean paramBoolean, CastDataProvider paramCastDataProvider, CompareMode paramCompareMode) {
/*  48 */     if (paramValue == ValueNull.INSTANCE) {
/*  49 */       return Integer.MIN_VALUE;
/*     */     }
/*  51 */     ValueCollectionBase valueCollectionBase1 = this;
/*  52 */     int i = valueCollectionBase1.getValueType();
/*  53 */     int j = paramValue.getValueType();
/*  54 */     if (j != i) {
/*  55 */       throw paramValue.getDataConversionError(i);
/*     */     }
/*  57 */     ValueCollectionBase valueCollectionBase2 = (ValueCollectionBase)paramValue;
/*  58 */     Value[] arrayOfValue1 = valueCollectionBase1.values, arrayOfValue2 = valueCollectionBase2.values;
/*  59 */     int k = arrayOfValue1.length, m = arrayOfValue2.length;
/*  60 */     if (k != m) {
/*  61 */       if (i == 41) {
/*  62 */         throw DbException.get(21002);
/*     */       }
/*  64 */       if (paramBoolean) {
/*  65 */         return 1;
/*     */       }
/*     */     } 
/*  68 */     if (paramBoolean) {
/*  69 */       boolean bool = false;
/*  70 */       for (byte b1 = 0; b1 < k; b1++) {
/*  71 */         Value value1 = arrayOfValue1[b1];
/*  72 */         Value value2 = arrayOfValue2[b1];
/*  73 */         int i1 = value1.compareWithNull(value2, paramBoolean, paramCastDataProvider, paramCompareMode);
/*  74 */         if (i1 != 0) {
/*  75 */           if (i1 != Integer.MIN_VALUE) {
/*  76 */             return i1;
/*     */           }
/*  78 */           bool = true;
/*     */         } 
/*     */       } 
/*  81 */       return bool ? Integer.MIN_VALUE : 0;
/*     */     } 
/*  83 */     int n = Math.min(k, m);
/*  84 */     for (byte b = 0; b < n; b++) {
/*  85 */       Value value1 = arrayOfValue1[b];
/*  86 */       Value value2 = arrayOfValue2[b];
/*  87 */       int i1 = value1.compareWithNull(value2, paramBoolean, paramCastDataProvider, paramCompareMode);
/*  88 */       if (i1 != 0) {
/*  89 */         return i1;
/*     */       }
/*     */     } 
/*  92 */     return Integer.compare(k, m);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsNull() {
/*  97 */     for (Value value : this.values) {
/*  98 */       if (value.containsNull()) {
/*  99 */         return true;
/*     */       }
/*     */     } 
/* 102 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMemory() {
/* 107 */     int i = 72 + this.values.length * 8;
/* 108 */     for (Value value : this.values) {
/* 109 */       i += value.getMemory();
/*     */     }
/* 111 */     return i;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ValueCollectionBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */