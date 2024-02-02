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
/*     */ public final class ValueDouble
/*     */   extends Value
/*     */ {
/*     */   static final int PRECISION = 53;
/*     */   public static final int DECIMAL_PRECISION = 17;
/*     */   public static final int DISPLAY_SIZE = 24;
/*     */   public static final long ZERO_BITS = 0L;
/*  43 */   public static final ValueDouble ZERO = new ValueDouble(0.0D);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  48 */   public static final ValueDouble ONE = new ValueDouble(1.0D);
/*     */   
/*  50 */   private static final ValueDouble NAN = new ValueDouble(Double.NaN);
/*     */   
/*     */   private final double value;
/*     */   
/*     */   private ValueDouble(double paramDouble) {
/*  55 */     this.value = paramDouble;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value add(Value paramValue) {
/*  60 */     return get(this.value + ((ValueDouble)paramValue).value);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value subtract(Value paramValue) {
/*  65 */     return get(this.value - ((ValueDouble)paramValue).value);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value negate() {
/*  70 */     return get(-this.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value multiply(Value paramValue) {
/*  75 */     return get(this.value * ((ValueDouble)paramValue).value);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value divide(Value paramValue, TypeInfo paramTypeInfo) {
/*  80 */     ValueDouble valueDouble = (ValueDouble)paramValue;
/*  81 */     if (valueDouble.value == 0.0D) {
/*  82 */       throw DbException.get(22012, getTraceSQL());
/*     */     }
/*  84 */     return get(this.value / valueDouble.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ValueDouble modulus(Value paramValue) {
/*  89 */     ValueDouble valueDouble = (ValueDouble)paramValue;
/*  90 */     if (valueDouble.value == 0.0D) {
/*  91 */       throw DbException.get(22012, getTraceSQL());
/*     */     }
/*  93 */     return get(this.value % valueDouble.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/*  98 */     if ((paramInt & 0x4) == 0) {
/*  99 */       return getSQL(paramStringBuilder.append("CAST(")).append(" AS DOUBLE PRECISION)");
/*     */     }
/* 101 */     return getSQL(paramStringBuilder);
/*     */   }
/*     */   
/*     */   private StringBuilder getSQL(StringBuilder paramStringBuilder) {
/* 105 */     if (this.value == Double.POSITIVE_INFINITY)
/* 106 */       return paramStringBuilder.append("'Infinity'"); 
/* 107 */     if (this.value == Double.NEGATIVE_INFINITY)
/* 108 */       return paramStringBuilder.append("'-Infinity'"); 
/* 109 */     if (Double.isNaN(this.value)) {
/* 110 */       return paramStringBuilder.append("'NaN'");
/*     */     }
/* 112 */     return paramStringBuilder.append(this.value);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/* 118 */     return TypeInfo.TYPE_DOUBLE;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getValueType() {
/* 123 */     return 15;
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTypeSafe(Value paramValue, CompareMode paramCompareMode, CastDataProvider paramCastDataProvider) {
/* 128 */     return Double.compare(this.value, ((ValueDouble)paramValue).value);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSignum() {
/* 133 */     return (this.value == 0.0D || Double.isNaN(this.value)) ? 0 : ((this.value < 0.0D) ? -1 : 1);
/*     */   }
/*     */ 
/*     */   
/*     */   public BigDecimal getBigDecimal() {
/* 138 */     if (Double.isFinite(this.value)) {
/* 139 */       return BigDecimal.valueOf(this.value);
/*     */     }
/*     */     
/* 142 */     throw DbException.get(22018, Double.toString(this.value));
/*     */   }
/*     */ 
/*     */   
/*     */   public float getFloat() {
/* 147 */     return (float)this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getDouble() {
/* 152 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getString() {
/* 157 */     return Double.toString(this.value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 166 */     long l = Double.doubleToRawLongBits(this.value);
/* 167 */     return (int)(l ^ l >>> 32L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueDouble get(double paramDouble) {
/* 177 */     if (paramDouble == 1.0D)
/* 178 */       return ONE; 
/* 179 */     if (paramDouble == 0.0D)
/*     */     {
/* 181 */       return ZERO; } 
/* 182 */     if (Double.isNaN(paramDouble)) {
/* 183 */       return NAN;
/*     */     }
/* 185 */     return (ValueDouble)Value.cache(new ValueDouble(paramDouble));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object paramObject) {
/* 190 */     if (!(paramObject instanceof ValueDouble)) {
/* 191 */       return false;
/*     */     }
/* 193 */     return (compareTypeSafe((ValueDouble)paramObject, null, null) == 0);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ValueDouble.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */