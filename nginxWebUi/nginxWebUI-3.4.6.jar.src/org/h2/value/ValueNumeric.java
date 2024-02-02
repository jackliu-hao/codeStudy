/*     */ package org.h2.value;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.math.RoundingMode;
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
/*     */ public final class ValueNumeric
/*     */   extends ValueBigDecimalBase
/*     */ {
/*  24 */   public static final ValueNumeric ZERO = new ValueNumeric(BigDecimal.ZERO);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  29 */   public static final ValueNumeric ONE = new ValueNumeric(BigDecimal.ONE);
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int DEFAULT_SCALE = 0;
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int MAXIMUM_SCALE = 100000;
/*     */ 
/*     */ 
/*     */   
/*     */   private ValueNumeric(BigDecimal paramBigDecimal) {
/*  42 */     super(paramBigDecimal);
/*  43 */     if (paramBigDecimal == null) {
/*  44 */       throw new IllegalArgumentException("null");
/*     */     }
/*  46 */     int i = paramBigDecimal.scale();
/*  47 */     if (i < 0 || i > 100000) {
/*  48 */       throw DbException.get(90151, new String[] { Integer.toString(i), "0", "100000" });
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public String getString() {
/*  54 */     return this.value.toPlainString();
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/*  59 */     String str = getString();
/*  60 */     if ((paramInt & 0x4) == 0 && str.indexOf('.') < 0 && this.value.compareTo(MAX_LONG_DECIMAL) <= 0 && this.value
/*  61 */       .compareTo(MIN_LONG_DECIMAL) >= 0) {
/*  62 */       return paramStringBuilder.append("CAST(").append(this.value).append(" AS NUMERIC(").append(this.value.precision()).append("))");
/*     */     }
/*  64 */     return paramStringBuilder.append(str);
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/*  69 */     TypeInfo typeInfo = this.type;
/*  70 */     if (typeInfo == null) {
/*  71 */       this.type = typeInfo = new TypeInfo(13, this.value.precision(), this.value.scale(), null);
/*     */     }
/*  73 */     return typeInfo;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getValueType() {
/*  78 */     return 13;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value add(Value paramValue) {
/*  83 */     return get(this.value.add(((ValueNumeric)paramValue).value));
/*     */   }
/*     */ 
/*     */   
/*     */   public Value subtract(Value paramValue) {
/*  88 */     return get(this.value.subtract(((ValueNumeric)paramValue).value));
/*     */   }
/*     */ 
/*     */   
/*     */   public Value negate() {
/*  93 */     return get(this.value.negate());
/*     */   }
/*     */ 
/*     */   
/*     */   public Value multiply(Value paramValue) {
/*  98 */     return get(this.value.multiply(((ValueNumeric)paramValue).value));
/*     */   }
/*     */ 
/*     */   
/*     */   public Value divide(Value paramValue, TypeInfo paramTypeInfo) {
/* 103 */     BigDecimal bigDecimal = ((ValueNumeric)paramValue).value;
/* 104 */     if (bigDecimal.signum() == 0) {
/* 105 */       throw DbException.get(22012, getTraceSQL());
/*     */     }
/* 107 */     return get(this.value.divide(bigDecimal, paramTypeInfo.getScale(), RoundingMode.HALF_DOWN));
/*     */   }
/*     */ 
/*     */   
/*     */   public Value modulus(Value paramValue) {
/* 112 */     ValueNumeric valueNumeric = (ValueNumeric)paramValue;
/* 113 */     if (valueNumeric.value.signum() == 0) {
/* 114 */       throw DbException.get(22012, getTraceSQL());
/*     */     }
/* 116 */     return get(this.value.remainder(valueNumeric.value));
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTypeSafe(Value paramValue, CompareMode paramCompareMode, CastDataProvider paramCastDataProvider) {
/* 121 */     return this.value.compareTo(((ValueNumeric)paramValue).value);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSignum() {
/* 126 */     return this.value.signum();
/*     */   }
/*     */ 
/*     */   
/*     */   public BigDecimal getBigDecimal() {
/* 131 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getFloat() {
/* 136 */     return this.value.floatValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public double getDouble() {
/* 141 */     return this.value.doubleValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 146 */     return getClass().hashCode() * 31 + this.value.hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object paramObject) {
/* 151 */     return (paramObject instanceof ValueNumeric && this.value.equals(((ValueNumeric)paramObject).value));
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMemory() {
/* 156 */     return this.value.precision() + 120;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueNumeric get(BigDecimal paramBigDecimal) {
/* 166 */     if (BigDecimal.ZERO.equals(paramBigDecimal))
/* 167 */       return ZERO; 
/* 168 */     if (BigDecimal.ONE.equals(paramBigDecimal)) {
/* 169 */       return ONE;
/*     */     }
/* 171 */     return (ValueNumeric)Value.cache(new ValueNumeric(paramBigDecimal));
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
/*     */   public static ValueNumeric getAnyScale(BigDecimal paramBigDecimal) {
/* 183 */     if (paramBigDecimal.scale() < 0) {
/* 184 */       paramBigDecimal = paramBigDecimal.setScale(0, RoundingMode.UNNECESSARY);
/*     */     }
/* 186 */     return get(paramBigDecimal);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueNumeric get(BigInteger paramBigInteger) {
/* 196 */     if (paramBigInteger.signum() == 0)
/* 197 */       return ZERO; 
/* 198 */     if (BigInteger.ONE.equals(paramBigInteger)) {
/* 199 */       return ONE;
/*     */     }
/* 201 */     return (ValueNumeric)Value.cache(new ValueNumeric(new BigDecimal(paramBigInteger)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BigDecimal setScale(BigDecimal paramBigDecimal, int paramInt) {
/* 212 */     if (paramInt < 0 || paramInt > 100000) {
/* 213 */       throw DbException.getInvalidValueException("scale", Integer.valueOf(paramInt));
/*     */     }
/* 215 */     return paramBigDecimal.setScale(paramInt, RoundingMode.HALF_UP);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ValueNumeric.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */