/*     */ package org.h2.result;
/*     */ 
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueBigint;
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
/*     */ public class DefaultRow
/*     */   extends Row
/*     */ {
/*     */   public static final int MEMORY_CALCULATE = -1;
/*     */   protected final Value[] data;
/*     */   private int memory;
/*     */   
/*     */   DefaultRow(int paramInt) {
/*  30 */     this.data = new Value[paramInt];
/*  31 */     this.memory = -1;
/*     */   }
/*     */   
/*     */   public DefaultRow(Value[] paramArrayOfValue) {
/*  35 */     this.data = paramArrayOfValue;
/*  36 */     this.memory = -1;
/*     */   }
/*     */   
/*     */   public DefaultRow(Value[] paramArrayOfValue, int paramInt) {
/*  40 */     this.data = paramArrayOfValue;
/*  41 */     this.memory = paramInt;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value getValue(int paramInt) {
/*  46 */     return (paramInt == -1) ? (Value)ValueBigint.get(this.key) : this.data[paramInt];
/*     */   }
/*     */ 
/*     */   
/*     */   public void setValue(int paramInt, Value paramValue) {
/*  51 */     if (paramInt == -1) {
/*  52 */       this.key = paramValue.getLong();
/*     */     } else {
/*  54 */       this.data[paramInt] = paramValue;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int getColumnCount() {
/*  60 */     return this.data.length;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMemory() {
/*  65 */     if (this.memory != -1) {
/*  66 */       return this.memory;
/*     */     }
/*  68 */     return this.memory = calculateMemory();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  73 */     StringBuilder stringBuilder = (new StringBuilder("( /* key:")).append(this.key).append(" */ "); byte b; int i;
/*  74 */     for (b = 0, i = this.data.length; b < i; b++) {
/*  75 */       if (b > 0) {
/*  76 */         stringBuilder.append(", ");
/*     */       }
/*  78 */       Value value = this.data[b];
/*  79 */       stringBuilder.append((value == null) ? "null" : value.getTraceSQL());
/*     */     } 
/*  81 */     return stringBuilder.append(')').toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int calculateMemory() {
/*  90 */     int i = 64 + this.data.length * 8;
/*  91 */     for (Value value : this.data) {
/*  92 */       if (value != null) {
/*  93 */         i += value.getMemory();
/*     */       }
/*     */     } 
/*  96 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value[] getValueList() {
/* 101 */     return this.data;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasSharedData(Row paramRow) {
/* 106 */     return (paramRow instanceof DefaultRow && this.data == ((DefaultRow)paramRow).data);
/*     */   }
/*     */ 
/*     */   
/*     */   public void copyFrom(SearchRow paramSearchRow) {
/* 111 */     setKey(paramSearchRow.getKey());
/* 112 */     for (byte b = 0; b < getColumnCount(); b++)
/* 113 */       setValue(b, paramSearchRow.getValue(b)); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\result\DefaultRow.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */