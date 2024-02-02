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
/*     */ public final class ValueTinyint
/*     */   extends Value
/*     */ {
/*     */   static final int PRECISION = 8;
/*     */   public static final int DECIMAL_PRECISION = 3;
/*     */   static final int DISPLAY_SIZE = 4;
/*     */   private final byte value;
/*     */   
/*     */   private ValueTinyint(byte paramByte) {
/*  38 */     this.value = paramByte;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value add(Value paramValue) {
/*  43 */     ValueTinyint valueTinyint = (ValueTinyint)paramValue;
/*  44 */     return checkRange(this.value + valueTinyint.value);
/*     */   }
/*     */   
/*     */   private static ValueTinyint checkRange(int paramInt) {
/*  48 */     if ((byte)paramInt != paramInt) {
/*  49 */       throw DbException.get(22003, 
/*  50 */           Integer.toString(paramInt));
/*     */     }
/*  52 */     return get((byte)paramInt);
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
/*  67 */     ValueTinyint valueTinyint = (ValueTinyint)paramValue;
/*  68 */     return checkRange(this.value - valueTinyint.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value multiply(Value paramValue) {
/*  73 */     ValueTinyint valueTinyint = (ValueTinyint)paramValue;
/*  74 */     return checkRange(this.value * valueTinyint.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value divide(Value paramValue, TypeInfo paramTypeInfo) {
/*  79 */     ValueTinyint valueTinyint = (ValueTinyint)paramValue;
/*  80 */     if (valueTinyint.value == 0) {
/*  81 */       throw DbException.get(22012, getTraceSQL());
/*     */     }
/*  83 */     return checkRange(this.value / valueTinyint.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value modulus(Value paramValue) {
/*  88 */     ValueTinyint valueTinyint = (ValueTinyint)paramValue;
/*  89 */     if (valueTinyint.value == 0) {
/*  90 */       throw DbException.get(22012, getTraceSQL());
/*     */     }
/*  92 */     return get((byte)(this.value % valueTinyint.value));
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/*  97 */     if ((paramInt & 0x4) == 0) {
/*  98 */       return paramStringBuilder.append("CAST(").append(this.value).append(" AS TINYINT)");
/*     */     }
/* 100 */     return paramStringBuilder.append(this.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/* 105 */     return TypeInfo.TYPE_TINYINT;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getValueType() {
/* 110 */     return 9;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getBytes() {
/* 115 */     return new byte[] { this.value };
/*     */   }
/*     */ 
/*     */   
/*     */   public byte getByte() {
/* 120 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public short getShort() {
/* 125 */     return (short)this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getInt() {
/* 130 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLong() {
/* 135 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public BigDecimal getBigDecimal() {
/* 140 */     return BigDecimal.valueOf(this.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public float getFloat() {
/* 145 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getDouble() {
/* 150 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTypeSafe(Value paramValue, CompareMode paramCompareMode, CastDataProvider paramCastDataProvider) {
/* 155 */     return Integer.compare(this.value, ((ValueTinyint)paramValue).value);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getString() {
/* 160 */     return Integer.toString(this.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 165 */     return this.value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueTinyint get(byte paramByte) {
/* 175 */     return (ValueTinyint)Value.cache(new ValueTinyint(paramByte));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object paramObject) {
/* 180 */     return (paramObject instanceof ValueTinyint && this.value == ((ValueTinyint)paramObject).value);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ValueTinyint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */