/*     */ package org.h2.result;
/*     */ 
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.value.CompareMode;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueNull;
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
/*     */ public abstract class SearchRow
/*     */   extends Value
/*     */ {
/*     */   public static final int ROWID_INDEX = -1;
/*  29 */   public static long MATCH_ALL_ROW_KEY = -9223372036854775807L;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int MEMORY_CALCULATE = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected long key;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract int getColumnCount();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isNull(int paramInt) {
/*  54 */     return (getValue(paramInt) == ValueNull.INSTANCE);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract Value getValue(int paramInt);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void setValue(int paramInt, Value paramValue);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setKey(long paramLong) {
/*  79 */     this.key = paramLong;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getKey() {
/*  88 */     return this.key;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract int getMemory();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void copyFrom(SearchRow paramSearchRow);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/* 107 */     return TypeInfo.TYPE_ROW_EMPTY;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getValueType() {
/* 112 */     return 41;
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 117 */     paramStringBuilder.append("ROW ("); byte b; int i;
/* 118 */     for (b = 0, i = getColumnCount(); b < i; b++) {
/* 119 */       if (b != 0) {
/* 120 */         paramStringBuilder.append(", ");
/*     */       }
/* 122 */       getValue(b).getSQL(paramStringBuilder, paramInt);
/*     */     } 
/* 124 */     return paramStringBuilder.append(')');
/*     */   }
/*     */ 
/*     */   
/*     */   public String getString() {
/* 129 */     return getTraceSQL();
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 134 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object paramObject) {
/* 139 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTypeSafe(Value paramValue, CompareMode paramCompareMode, CastDataProvider paramCastDataProvider) {
/* 144 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\result\SearchRow.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */