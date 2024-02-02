/*     */ package org.h2.value;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import java.nio.charset.StandardCharsets;
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
/*     */ abstract class ValueStringBase
/*     */   extends Value
/*     */ {
/*     */   String value;
/*     */   private TypeInfo type;
/*     */   
/*     */   ValueStringBase(String paramString) {
/*  29 */     int i = paramString.length();
/*  30 */     if (i > 1048576) {
/*  31 */       throw DbException.getValueTooLongException(getTypeName(getValueType()), paramString, i);
/*     */     }
/*  33 */     this.value = paramString;
/*     */   }
/*     */ 
/*     */   
/*     */   public final TypeInfo getType() {
/*  38 */     TypeInfo typeInfo = this.type;
/*  39 */     if (typeInfo == null) {
/*  40 */       int i = this.value.length();
/*  41 */       this.type = typeInfo = new TypeInfo(getValueType(), i, 0, null);
/*     */     } 
/*  43 */     return typeInfo;
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTypeSafe(Value paramValue, CompareMode paramCompareMode, CastDataProvider paramCastDataProvider) {
/*  48 */     return paramCompareMode.compareString(this.value, ((ValueStringBase)paramValue).value, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  55 */     return getClass().hashCode() ^ this.value.hashCode();
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
/*     */   public final String getString() {
/*  84 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public final byte[] getBytes() {
/*  89 */     return this.value.getBytes(StandardCharsets.UTF_8);
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean getBoolean() {
/*  94 */     String str = this.value.trim();
/*  95 */     if (str.equalsIgnoreCase("true") || str.equalsIgnoreCase("t") || str.equalsIgnoreCase("yes") || str
/*  96 */       .equalsIgnoreCase("y"))
/*  97 */       return true; 
/*  98 */     if (str.equalsIgnoreCase("false") || str.equalsIgnoreCase("f") || str.equalsIgnoreCase("no") || str
/*  99 */       .equalsIgnoreCase("n")) {
/* 100 */       return false;
/*     */     }
/*     */     
/*     */     try {
/* 104 */       return ((new BigDecimal(str)).signum() != 0);
/* 105 */     } catch (NumberFormatException numberFormatException) {
/* 106 */       throw getDataConversionError(8);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public final byte getByte() {
/*     */     try {
/* 113 */       return Byte.parseByte(this.value.trim());
/* 114 */     } catch (NumberFormatException numberFormatException) {
/* 115 */       throw DbException.get(22018, numberFormatException, new String[] { this.value });
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public final short getShort() {
/*     */     try {
/* 122 */       return Short.parseShort(this.value.trim());
/* 123 */     } catch (NumberFormatException numberFormatException) {
/* 124 */       throw DbException.get(22018, numberFormatException, new String[] { this.value });
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public final int getInt() {
/*     */     try {
/* 131 */       return Integer.parseInt(this.value.trim());
/* 132 */     } catch (NumberFormatException numberFormatException) {
/* 133 */       throw DbException.get(22018, numberFormatException, new String[] { this.value });
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public final long getLong() {
/*     */     try {
/* 140 */       return Long.parseLong(this.value.trim());
/* 141 */     } catch (NumberFormatException numberFormatException) {
/* 142 */       throw DbException.get(22018, numberFormatException, new String[] { this.value });
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public final BigDecimal getBigDecimal() {
/*     */     try {
/* 149 */       return new BigDecimal(this.value.trim());
/* 150 */     } catch (NumberFormatException numberFormatException) {
/* 151 */       throw DbException.get(22018, numberFormatException, new String[] { this.value });
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public final float getFloat() {
/*     */     try {
/* 158 */       return Float.parseFloat(this.value.trim());
/* 159 */     } catch (NumberFormatException numberFormatException) {
/* 160 */       throw DbException.get(22018, numberFormatException, new String[] { this.value });
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public final double getDouble() {
/*     */     try {
/* 167 */       return Double.parseDouble(this.value.trim());
/* 168 */     } catch (NumberFormatException numberFormatException) {
/* 169 */       throw DbException.get(22018, numberFormatException, new String[] { this.value });
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final int getMemory() {
/* 180 */     return this.value.length() * 2 + 94;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object paramObject) {
/* 185 */     return (paramObject != null && getClass() == paramObject.getClass() && this.value.equals(((ValueStringBase)paramObject).value));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ValueStringBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */