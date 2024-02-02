/*     */ package org.h2.value;
/*     */ 
/*     */ import java.math.BigDecimal;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ValueSmallint
/*     */   extends Value
/*     */ {
/*     */   static final int PRECISION = 16;
/*     */   public static final int DECIMAL_PRECISION = 5;
/*     */   static final int DISPLAY_SIZE = 6;
/*     */   private final short value;
/*     */   
/*     */   private ValueSmallint(short paramShort) {
/*  38 */     this.value = paramShort;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value add(Value paramValue) {
/*  43 */     ValueSmallint valueSmallint = (ValueSmallint)paramValue;
/*  44 */     return checkRange(this.value + valueSmallint.value);
/*     */   }
/*     */   
/*     */   private static ValueSmallint checkRange(int paramInt) {
/*  48 */     if ((short)paramInt != paramInt) {
/*  49 */       throw DbException.get(22003, 
/*  50 */           Integer.toString(paramInt));
/*     */     }
/*  52 */     return get((short)paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSignum() {
/*  57 */     return Integer.signum(this.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value negate() {
/*  62 */     return checkRange(-this.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value subtract(Value paramValue) {
/*  67 */     ValueSmallint valueSmallint = (ValueSmallint)paramValue;
/*  68 */     return checkRange(this.value - valueSmallint.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value multiply(Value paramValue) {
/*  73 */     ValueSmallint valueSmallint = (ValueSmallint)paramValue;
/*  74 */     return checkRange(this.value * valueSmallint.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value divide(Value paramValue, TypeInfo paramTypeInfo) {
/*  79 */     ValueSmallint valueSmallint = (ValueSmallint)paramValue;
/*  80 */     if (valueSmallint.value == 0) {
/*  81 */       throw DbException.get(22012, getTraceSQL());
/*     */     }
/*  83 */     return checkRange(this.value / valueSmallint.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value modulus(Value paramValue) {
/*  88 */     ValueSmallint valueSmallint = (ValueSmallint)paramValue;
/*  89 */     if (valueSmallint.value == 0) {
/*  90 */       throw DbException.get(22012, getTraceSQL());
/*     */     }
/*  92 */     return get((short)(this.value % valueSmallint.value));
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/*  97 */     if ((paramInt & 0x4) == 0) {
/*  98 */       return paramStringBuilder.append("CAST(").append(this.value).append(" AS SMALLINT)");
/*     */     }
/* 100 */     return paramStringBuilder.append(this.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/* 105 */     return TypeInfo.TYPE_SMALLINT;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getValueType() {
/* 110 */     return 10;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getBytes() {
/* 115 */     short s = this.value;
/* 116 */     return new byte[] { (byte)(s >> 8), (byte)s };
/*     */   }
/*     */ 
/*     */   
/*     */   public short getShort() {
/* 121 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getInt() {
/* 126 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLong() {
/* 131 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public BigDecimal getBigDecimal() {
/* 136 */     return BigDecimal.valueOf(this.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public float getFloat() {
/* 141 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getDouble() {
/* 146 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTypeSafe(Value paramValue, CompareMode paramCompareMode, CastDataProvider paramCastDataProvider) {
/* 151 */     return Integer.compare(this.value, ((ValueSmallint)paramValue).value);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getString() {
/* 156 */     return Integer.toString(this.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 161 */     return this.value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueSmallint get(short paramShort) {
/* 171 */     return (ValueSmallint)Value.cache(new ValueSmallint(paramShort));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object paramObject) {
/* 176 */     return (paramObject instanceof ValueSmallint && this.value == ((ValueSmallint)paramObject).value);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ValueSmallint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */