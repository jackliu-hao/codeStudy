/*     */ package org.h2.value;
/*     */ 
/*     */ import java.math.BigDecimal;
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
/*     */ public final class ValueInteger
/*     */   extends Value
/*     */ {
/*     */   public static final int PRECISION = 32;
/*     */   public static final int DECIMAL_PRECISION = 10;
/*     */   public static final int DISPLAY_SIZE = 11;
/*     */   private static final int STATIC_SIZE = 128;
/*     */   private static final int DYNAMIC_SIZE = 256;
/*  39 */   private static final ValueInteger[] STATIC_CACHE = new ValueInteger[128];
/*  40 */   private static final ValueInteger[] DYNAMIC_CACHE = new ValueInteger[256];
/*     */   
/*     */   private final int value;
/*     */   
/*     */   static {
/*  45 */     for (byte b = 0; b < ''; b++) {
/*  46 */       STATIC_CACHE[b] = new ValueInteger(b);
/*     */     }
/*     */   }
/*     */   
/*     */   private ValueInteger(int paramInt) {
/*  51 */     this.value = paramInt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueInteger get(int paramInt) {
/*  61 */     if (paramInt >= 0 && paramInt < 128) {
/*  62 */       return STATIC_CACHE[paramInt];
/*     */     }
/*  64 */     ValueInteger valueInteger = DYNAMIC_CACHE[paramInt & 0xFF];
/*  65 */     if (valueInteger == null || valueInteger.value != paramInt) {
/*  66 */       valueInteger = new ValueInteger(paramInt);
/*  67 */       DYNAMIC_CACHE[paramInt & 0xFF] = valueInteger;
/*     */     } 
/*  69 */     return valueInteger;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value add(Value paramValue) {
/*  74 */     ValueInteger valueInteger = (ValueInteger)paramValue;
/*  75 */     return checkRange(this.value + valueInteger.value);
/*     */   }
/*     */   
/*     */   private static ValueInteger checkRange(long paramLong) {
/*  79 */     if ((int)paramLong != paramLong) {
/*  80 */       throw DbException.get(22003, Long.toString(paramLong));
/*     */     }
/*  82 */     return get((int)paramLong);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSignum() {
/*  87 */     return Integer.signum(this.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value negate() {
/*  92 */     return checkRange(-(this.value));
/*     */   }
/*     */ 
/*     */   
/*     */   public Value subtract(Value paramValue) {
/*  97 */     ValueInteger valueInteger = (ValueInteger)paramValue;
/*  98 */     return checkRange(this.value - valueInteger.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value multiply(Value paramValue) {
/* 103 */     ValueInteger valueInteger = (ValueInteger)paramValue;
/* 104 */     return checkRange(this.value * valueInteger.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value divide(Value paramValue, TypeInfo paramTypeInfo) {
/* 109 */     int i = ((ValueInteger)paramValue).value;
/* 110 */     if (i == 0) {
/* 111 */       throw DbException.get(22012, getTraceSQL());
/*     */     }
/* 113 */     int j = this.value;
/* 114 */     if (j == Integer.MIN_VALUE && i == -1) {
/* 115 */       throw DbException.get(22003, "2147483648");
/*     */     }
/* 117 */     return get(j / i);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value modulus(Value paramValue) {
/* 122 */     ValueInteger valueInteger = (ValueInteger)paramValue;
/* 123 */     if (valueInteger.value == 0) {
/* 124 */       throw DbException.get(22012, getTraceSQL());
/*     */     }
/* 126 */     return get(this.value % valueInteger.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 131 */     return paramStringBuilder.append(this.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/* 136 */     return TypeInfo.TYPE_INTEGER;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getValueType() {
/* 141 */     return 11;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getBytes() {
/* 146 */     byte[] arrayOfByte = new byte[4];
/* 147 */     Bits.writeInt(arrayOfByte, 0, getInt());
/* 148 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getInt() {
/* 153 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLong() {
/* 158 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public BigDecimal getBigDecimal() {
/* 163 */     return BigDecimal.valueOf(this.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public float getFloat() {
/* 168 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getDouble() {
/* 173 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTypeSafe(Value paramValue, CompareMode paramCompareMode, CastDataProvider paramCastDataProvider) {
/* 178 */     return Integer.compare(this.value, ((ValueInteger)paramValue).value);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getString() {
/* 183 */     return Integer.toString(this.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 188 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object paramObject) {
/* 193 */     return (paramObject instanceof ValueInteger && this.value == ((ValueInteger)paramObject).value);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ValueInteger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */