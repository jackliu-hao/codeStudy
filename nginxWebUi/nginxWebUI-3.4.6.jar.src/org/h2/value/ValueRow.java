/*     */ package org.h2.value;
/*     */ 
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.result.SimpleResult;
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
/*     */ public final class ValueRow
/*     */   extends ValueCollectionBase
/*     */ {
/*  22 */   public static final ValueRow EMPTY = get(Value.EMPTY_VALUES);
/*     */   
/*     */   private TypeInfo type;
/*     */   
/*     */   private ValueRow(TypeInfo paramTypeInfo, Value[] paramArrayOfValue) {
/*  27 */     super(paramArrayOfValue);
/*  28 */     int i = paramArrayOfValue.length;
/*  29 */     if (i > 16384) {
/*  30 */       throw DbException.get(54011, "16384");
/*     */     }
/*  32 */     if (paramTypeInfo != null) {
/*  33 */       if (paramTypeInfo.getValueType() != 41 || ((ExtTypeInfoRow)paramTypeInfo.getExtTypeInfo()).getFields().size() != i) {
/*  34 */         throw DbException.getInternalError();
/*     */       }
/*  36 */       this.type = paramTypeInfo;
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
/*     */   public static ValueRow get(Value[] paramArrayOfValue) {
/*  48 */     return new ValueRow(null, paramArrayOfValue);
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
/*     */   public static ValueRow get(ExtTypeInfoRow paramExtTypeInfoRow, Value[] paramArrayOfValue) {
/*  60 */     return new ValueRow(new TypeInfo(41, -1L, -1, paramExtTypeInfoRow), paramArrayOfValue);
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
/*     */   public static ValueRow get(TypeInfo paramTypeInfo, Value[] paramArrayOfValue) {
/*  72 */     return new ValueRow(paramTypeInfo, paramArrayOfValue);
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/*  77 */     TypeInfo typeInfo = this.type;
/*  78 */     if (typeInfo == null) {
/*  79 */       this.type = typeInfo = TypeInfo.getTypeInfo(41, 0L, 0, new ExtTypeInfoRow((Typed[])this.values));
/*     */     }
/*  81 */     return typeInfo;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getValueType() {
/*  86 */     return 41;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getString() {
/*  91 */     StringBuilder stringBuilder = new StringBuilder("ROW (");
/*  92 */     for (byte b = 0; b < this.values.length; b++) {
/*  93 */       if (b > 0) {
/*  94 */         stringBuilder.append(", ");
/*     */       }
/*  96 */       stringBuilder.append(this.values[b].getString());
/*     */     } 
/*  98 */     return stringBuilder.append(')').toString();
/*     */   }
/*     */   
/*     */   public SimpleResult getResult() {
/* 102 */     SimpleResult simpleResult = new SimpleResult(); byte b; int i;
/* 103 */     for (b = 0, i = this.values.length; b < i; ) {
/* 104 */       Value value = this.values[b++];
/* 105 */       simpleResult.addColumn("C" + b, value.getType());
/*     */     } 
/* 107 */     simpleResult.addRow(this.values);
/* 108 */     return simpleResult;
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTypeSafe(Value paramValue, CompareMode paramCompareMode, CastDataProvider paramCastDataProvider) {
/* 113 */     ValueRow valueRow = (ValueRow)paramValue;
/* 114 */     if (this.values == valueRow.values) {
/* 115 */       return 0;
/*     */     }
/* 117 */     int i = this.values.length;
/* 118 */     if (i != valueRow.values.length) {
/* 119 */       throw DbException.get(21002);
/*     */     }
/* 121 */     for (byte b = 0; b < i; b++) {
/* 122 */       Value value1 = this.values[b];
/* 123 */       Value value2 = valueRow.values[b];
/* 124 */       int j = value1.compareTo(value2, paramCastDataProvider, paramCompareMode);
/* 125 */       if (j != 0) {
/* 126 */         return j;
/*     */       }
/*     */     } 
/* 129 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 134 */     paramStringBuilder.append("ROW (");
/* 135 */     int i = this.values.length;
/* 136 */     for (byte b = 0; b < i; b++) {
/* 137 */       if (b > 0) {
/* 138 */         paramStringBuilder.append(", ");
/*     */       }
/* 140 */       this.values[b].getSQL(paramStringBuilder, paramInt);
/*     */     } 
/* 142 */     return paramStringBuilder.append(')');
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object paramObject) {
/* 147 */     if (!(paramObject instanceof ValueRow)) {
/* 148 */       return false;
/*     */     }
/* 150 */     ValueRow valueRow = (ValueRow)paramObject;
/* 151 */     if (this.values == valueRow.values) {
/* 152 */       return true;
/*     */     }
/* 154 */     int i = this.values.length;
/* 155 */     if (i != valueRow.values.length) {
/* 156 */       return false;
/*     */     }
/* 158 */     for (byte b = 0; b < i; b++) {
/* 159 */       if (!this.values[b].equals(valueRow.values[b])) {
/* 160 */         return false;
/*     */       }
/*     */     } 
/* 163 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ValueRow.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */