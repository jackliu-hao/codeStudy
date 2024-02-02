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
/*     */ public final class ValueArray
/*     */   extends ValueCollectionBase
/*     */ {
/*  20 */   public static final ValueArray EMPTY = get(TypeInfo.TYPE_NULL, Value.EMPTY_VALUES, (CastDataProvider)null);
/*     */   
/*     */   private TypeInfo type;
/*     */   
/*     */   private TypeInfo componentType;
/*     */   
/*     */   private ValueArray(TypeInfo paramTypeInfo, Value[] paramArrayOfValue, CastDataProvider paramCastDataProvider) {
/*  27 */     super(paramArrayOfValue);
/*  28 */     int i = paramArrayOfValue.length;
/*  29 */     if (i > 65536) {
/*  30 */       String str = getTypeName(getValueType());
/*  31 */       throw DbException.getValueTooLongException(str, str, i);
/*     */     } 
/*  33 */     for (byte b = 0; b < i; b++) {
/*  34 */       paramArrayOfValue[b] = paramArrayOfValue[b].castTo(paramTypeInfo, paramCastDataProvider);
/*     */     }
/*  36 */     this.componentType = paramTypeInfo;
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
/*     */   public static ValueArray get(Value[] paramArrayOfValue, CastDataProvider paramCastDataProvider) {
/*  48 */     return new ValueArray(TypeInfo.getHigherType((Typed[])paramArrayOfValue), paramArrayOfValue, paramCastDataProvider);
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
/*     */   public static ValueArray get(TypeInfo paramTypeInfo, Value[] paramArrayOfValue, CastDataProvider paramCastDataProvider) {
/*  61 */     return new ValueArray(paramTypeInfo, paramArrayOfValue, paramCastDataProvider);
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/*  66 */     TypeInfo typeInfo = this.type;
/*  67 */     if (typeInfo == null) {
/*  68 */       TypeInfo typeInfo1 = getComponentType();
/*  69 */       this.type = typeInfo = TypeInfo.getTypeInfo(getValueType(), this.values.length, 0, typeInfo1);
/*     */     } 
/*  71 */     return typeInfo;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getValueType() {
/*  76 */     return 40;
/*     */   }
/*     */   
/*     */   public TypeInfo getComponentType() {
/*  80 */     return this.componentType;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getString() {
/*  85 */     StringBuilder stringBuilder = (new StringBuilder()).append('[');
/*  86 */     for (byte b = 0; b < this.values.length; b++) {
/*  87 */       if (b > 0) {
/*  88 */         stringBuilder.append(", ");
/*     */       }
/*  90 */       stringBuilder.append(this.values[b].getString());
/*     */     } 
/*  92 */     return stringBuilder.append(']').toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTypeSafe(Value paramValue, CompareMode paramCompareMode, CastDataProvider paramCastDataProvider) {
/*  97 */     ValueArray valueArray = (ValueArray)paramValue;
/*  98 */     if (this.values == valueArray.values) {
/*  99 */       return 0;
/*     */     }
/* 101 */     int i = this.values.length;
/* 102 */     int j = valueArray.values.length;
/* 103 */     int k = Math.min(i, j);
/* 104 */     for (byte b = 0; b < k; b++) {
/* 105 */       Value value1 = this.values[b];
/* 106 */       Value value2 = valueArray.values[b];
/* 107 */       int m = value1.compareTo(value2, paramCastDataProvider, paramCompareMode);
/* 108 */       if (m != 0) {
/* 109 */         return m;
/*     */       }
/*     */     } 
/* 112 */     return Integer.compare(i, j);
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 117 */     paramStringBuilder.append("ARRAY [");
/* 118 */     int i = this.values.length;
/* 119 */     for (byte b = 0; b < i; b++) {
/* 120 */       if (b > 0) {
/* 121 */         paramStringBuilder.append(", ");
/*     */       }
/* 123 */       this.values[b].getSQL(paramStringBuilder, paramInt);
/*     */     } 
/* 125 */     return paramStringBuilder.append(']');
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object paramObject) {
/* 130 */     if (!(paramObject instanceof ValueArray)) {
/* 131 */       return false;
/*     */     }
/* 133 */     ValueArray valueArray = (ValueArray)paramObject;
/* 134 */     if (this.values == valueArray.values) {
/* 135 */       return true;
/*     */     }
/* 137 */     int i = this.values.length;
/* 138 */     if (i != valueArray.values.length) {
/* 139 */       return false;
/*     */     }
/* 141 */     for (byte b = 0; b < i; b++) {
/* 142 */       if (!this.values[b].equals(valueArray.values[b])) {
/* 143 */         return false;
/*     */       }
/*     */     } 
/* 146 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ValueArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */