/*     */ package org.h2.value;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.util.StringUtils;
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
/*     */ public class ValueEnumBase
/*     */   extends Value
/*     */ {
/*     */   final String label;
/*     */   private final int ordinal;
/*     */   
/*     */   protected ValueEnumBase(String paramString, int paramInt) {
/*  25 */     this.label = paramString;
/*  26 */     this.ordinal = paramInt;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value add(Value paramValue) {
/*  31 */     ValueInteger valueInteger = paramValue.convertToInt(null);
/*  32 */     return convertToInt(null).add(valueInteger);
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTypeSafe(Value paramValue, CompareMode paramCompareMode, CastDataProvider paramCastDataProvider) {
/*  37 */     return Integer.compare(getInt(), paramValue.getInt());
/*     */   }
/*     */ 
/*     */   
/*     */   public Value divide(Value paramValue, TypeInfo paramTypeInfo) {
/*  42 */     ValueInteger valueInteger = paramValue.convertToInt(null);
/*  43 */     return convertToInt(null).divide(valueInteger, paramTypeInfo);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object paramObject) {
/*  48 */     return (paramObject instanceof ValueEnumBase && 
/*  49 */       getInt() == ((ValueEnumBase)paramObject).getInt());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueEnumBase get(String paramString, int paramInt) {
/*  60 */     return new ValueEnumBase(paramString, paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getInt() {
/*  65 */     return this.ordinal;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLong() {
/*  70 */     return this.ordinal;
/*     */   }
/*     */ 
/*     */   
/*     */   public BigDecimal getBigDecimal() {
/*  75 */     return BigDecimal.valueOf(this.ordinal);
/*     */   }
/*     */ 
/*     */   
/*     */   public float getFloat() {
/*  80 */     return this.ordinal;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getDouble() {
/*  85 */     return this.ordinal;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSignum() {
/*  90 */     return Integer.signum(this.ordinal);
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/*  95 */     return StringUtils.quoteStringSQL(paramStringBuilder, this.label);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getString() {
/* 100 */     return this.label;
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/* 105 */     return TypeInfo.TYPE_ENUM_UNDEFINED;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getValueType() {
/* 110 */     return 36;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMemory() {
/* 115 */     return 120;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 120 */     int i = 31;
/* 121 */     i += getString().hashCode();
/* 122 */     i += getInt();
/* 123 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value modulus(Value paramValue) {
/* 128 */     ValueInteger valueInteger = paramValue.convertToInt(null);
/* 129 */     return convertToInt(null).modulus(valueInteger);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value multiply(Value paramValue) {
/* 134 */     ValueInteger valueInteger = paramValue.convertToInt(null);
/* 135 */     return convertToInt(null).multiply(valueInteger);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value subtract(Value paramValue) {
/* 140 */     ValueInteger valueInteger = paramValue.convertToInt(null);
/* 141 */     return convertToInt(null).subtract(valueInteger);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ValueEnumBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */