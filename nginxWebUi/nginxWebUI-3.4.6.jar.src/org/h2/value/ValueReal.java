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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ValueReal
/*     */   extends Value
/*     */ {
/*     */   static final int PRECISION = 24;
/*     */   static final int DECIMAL_PRECISION = 7;
/*     */   static final int DISPLAY_SIZE = 15;
/*     */   public static final int ZERO_BITS = 0;
/*  43 */   public static final ValueReal ZERO = new ValueReal(0.0F);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  48 */   public static final ValueReal ONE = new ValueReal(1.0F);
/*     */   
/*  50 */   private static final ValueReal NAN = new ValueReal(Float.NaN);
/*     */   
/*     */   private final float value;
/*     */   
/*     */   private ValueReal(float paramFloat) {
/*  55 */     this.value = paramFloat;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value add(Value paramValue) {
/*  60 */     return get(this.value + ((ValueReal)paramValue).value);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value subtract(Value paramValue) {
/*  65 */     return get(this.value - ((ValueReal)paramValue).value);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value negate() {
/*  70 */     return get(-this.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value multiply(Value paramValue) {
/*  75 */     return get(this.value * ((ValueReal)paramValue).value);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value divide(Value paramValue, TypeInfo paramTypeInfo) {
/*  80 */     ValueReal valueReal = (ValueReal)paramValue;
/*  81 */     if (valueReal.value == 0.0D) {
/*  82 */       throw DbException.get(22012, getTraceSQL());
/*     */     }
/*  84 */     return get(this.value / valueReal.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value modulus(Value paramValue) {
/*  89 */     ValueReal valueReal = (ValueReal)paramValue;
/*  90 */     if (valueReal.value == 0.0F) {
/*  91 */       throw DbException.get(22012, getTraceSQL());
/*     */     }
/*  93 */     return get(this.value % valueReal.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/*  98 */     if ((paramInt & 0x4) == 0) {
/*  99 */       return getSQL(paramStringBuilder.append("CAST(")).append(" AS REAL)");
/*     */     }
/* 101 */     return getSQL(paramStringBuilder);
/*     */   }
/*     */   
/*     */   private StringBuilder getSQL(StringBuilder paramStringBuilder) {
/* 105 */     if (this.value == Float.POSITIVE_INFINITY)
/* 106 */       return paramStringBuilder.append("'Infinity'"); 
/* 107 */     if (this.value == Float.NEGATIVE_INFINITY)
/* 108 */       return paramStringBuilder.append("'-Infinity'"); 
/* 109 */     if (Float.isNaN(this.value)) {
/* 110 */       return paramStringBuilder.append("'NaN'");
/*     */     }
/* 112 */     return paramStringBuilder.append(this.value);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/* 118 */     return TypeInfo.TYPE_REAL;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getValueType() {
/* 123 */     return 14;
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTypeSafe(Value paramValue, CompareMode paramCompareMode, CastDataProvider paramCastDataProvider) {
/* 128 */     return Float.compare(this.value, ((ValueReal)paramValue).value);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSignum() {
/* 133 */     return (this.value == 0.0F || Float.isNaN(this.value)) ? 0 : ((this.value < 0.0F) ? -1 : 1);
/*     */   }
/*     */ 
/*     */   
/*     */   public BigDecimal getBigDecimal() {
/* 138 */     if (Float.isFinite(this.value))
/*     */     {
/* 140 */       return new BigDecimal(Float.toString(this.value));
/*     */     }
/*     */     
/* 143 */     throw DbException.get(22018, Float.toString(this.value));
/*     */   }
/*     */ 
/*     */   
/*     */   public float getFloat() {
/* 148 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getDouble() {
/* 153 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getString() {
/* 158 */     return Float.toString(this.value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 167 */     return Float.floatToRawIntBits(this.value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueReal get(float paramFloat) {
/* 177 */     if (paramFloat == 1.0F)
/* 178 */       return ONE; 
/* 179 */     if (paramFloat == 0.0F)
/*     */     {
/* 181 */       return ZERO; } 
/* 182 */     if (Float.isNaN(paramFloat)) {
/* 183 */       return NAN;
/*     */     }
/* 185 */     return (ValueReal)Value.cache(new ValueReal(paramFloat));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object paramObject) {
/* 190 */     if (!(paramObject instanceof ValueReal)) {
/* 191 */       return false;
/*     */     }
/* 193 */     return (compareTypeSafe((ValueReal)paramObject, null, null) == 0);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ValueReal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */