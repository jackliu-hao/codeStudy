/*     */ package org.h2.value;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.util.Bits;
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
/*     */ public final class ValueBigint
/*     */   extends Value
/*     */ {
/*  24 */   public static final ValueBigint MIN = get(Long.MIN_VALUE);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  29 */   public static final ValueBigint MAX = get(Long.MAX_VALUE);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  34 */   public static final BigInteger MAX_BI = BigInteger.valueOf(Long.MAX_VALUE);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final int PRECISION = 64;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int DECIMAL_PRECISION = 19;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int DISPLAY_SIZE = 20;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int STATIC_SIZE = 100;
/*     */ 
/*     */ 
/*     */   
/*  58 */   private static final ValueBigint[] STATIC_CACHE = new ValueBigint[100]; static {
/*  59 */     for (byte b = 0; b < 100; b++)
/*  60 */       STATIC_CACHE[b] = new ValueBigint(b); 
/*     */   }
/*     */   private final long value;
/*     */   
/*     */   private ValueBigint(long paramLong) {
/*  65 */     this.value = paramLong;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value add(Value paramValue) {
/*  70 */     long l1 = this.value;
/*  71 */     long l2 = ((ValueBigint)paramValue).value;
/*  72 */     long l3 = l1 + l2;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  77 */     if (((l1 ^ l3) & (l2 ^ l3)) < 0L) {
/*  78 */       throw getOverflow();
/*     */     }
/*  80 */     return get(l3);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSignum() {
/*  85 */     return Long.signum(this.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value negate() {
/*  90 */     if (this.value == Long.MIN_VALUE) {
/*  91 */       throw getOverflow();
/*     */     }
/*  93 */     return get(-this.value);
/*     */   }
/*     */   
/*     */   private DbException getOverflow() {
/*  97 */     return DbException.get(22003, 
/*  98 */         Long.toString(this.value));
/*     */   }
/*     */ 
/*     */   
/*     */   public Value subtract(Value paramValue) {
/* 103 */     long l1 = this.value;
/* 104 */     long l2 = ((ValueBigint)paramValue).value;
/* 105 */     long l3 = l1 - l2;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 110 */     if (((l1 ^ l2) & (l1 ^ l3)) < 0L) {
/* 111 */       throw getOverflow();
/*     */     }
/* 113 */     return get(l3);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value multiply(Value paramValue) {
/* 118 */     long l1 = this.value;
/* 119 */     long l2 = ((ValueBigint)paramValue).value;
/* 120 */     long l3 = l1 * l2;
/*     */     
/* 122 */     if ((Math.abs(l1) | Math.abs(l2)) >>> 31L != 0L && l2 != 0L && (l3 / l2 != l1 || (l1 == Long.MIN_VALUE && l2 == -1L)))
/*     */     {
/*     */ 
/*     */ 
/*     */       
/* 127 */       throw getOverflow();
/*     */     }
/* 129 */     return get(l3);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value divide(Value paramValue, TypeInfo paramTypeInfo) {
/* 134 */     long l1 = ((ValueBigint)paramValue).value;
/* 135 */     if (l1 == 0L) {
/* 136 */       throw DbException.get(22012, getTraceSQL());
/*     */     }
/* 138 */     long l2 = this.value;
/* 139 */     if (l2 == Long.MIN_VALUE && l1 == -1L) {
/* 140 */       throw getOverflow();
/*     */     }
/* 142 */     return get(l2 / l1);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value modulus(Value paramValue) {
/* 147 */     ValueBigint valueBigint = (ValueBigint)paramValue;
/* 148 */     if (valueBigint.value == 0L) {
/* 149 */       throw DbException.get(22012, getTraceSQL());
/*     */     }
/* 151 */     return get(this.value % valueBigint.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 156 */     if ((paramInt & 0x4) == 0 && this.value == (int)this.value) {
/* 157 */       return paramStringBuilder.append("CAST(").append(this.value).append(" AS BIGINT)");
/*     */     }
/* 159 */     return paramStringBuilder.append(this.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/* 164 */     return TypeInfo.TYPE_BIGINT;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getValueType() {
/* 169 */     return 12;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getBytes() {
/* 174 */     byte[] arrayOfByte = new byte[8];
/* 175 */     Bits.writeLong(arrayOfByte, 0, getLong());
/* 176 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLong() {
/* 181 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public BigDecimal getBigDecimal() {
/* 186 */     return BigDecimal.valueOf(this.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public float getFloat() {
/* 191 */     return (float)this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getDouble() {
/* 196 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTypeSafe(Value paramValue, CompareMode paramCompareMode, CastDataProvider paramCastDataProvider) {
/* 201 */     return Long.compare(this.value, ((ValueBigint)paramValue).value);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getString() {
/* 206 */     return Long.toString(this.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 211 */     return (int)(this.value ^ this.value >> 32L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueBigint get(long paramLong) {
/* 221 */     if (paramLong >= 0L && paramLong < 100L) {
/* 222 */       return STATIC_CACHE[(int)paramLong];
/*     */     }
/* 224 */     return (ValueBigint)Value.cache(new ValueBigint(paramLong));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object paramObject) {
/* 229 */     return (paramObject instanceof ValueBigint && this.value == ((ValueBigint)paramObject).value);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ValueBigint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */