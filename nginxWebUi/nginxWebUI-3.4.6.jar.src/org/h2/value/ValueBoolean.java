/*     */ package org.h2.value;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import org.h2.engine.CastDataProvider;
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
/*     */ public final class ValueBoolean
/*     */   extends Value
/*     */ {
/*     */   public static final int PRECISION = 1;
/*     */   public static final int DISPLAY_SIZE = 5;
/*  31 */   public static final ValueBoolean TRUE = new ValueBoolean(true);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  36 */   public static final ValueBoolean FALSE = new ValueBoolean(false);
/*     */   
/*     */   private final boolean value;
/*     */   
/*     */   private ValueBoolean(boolean paramBoolean) {
/*  41 */     this.value = paramBoolean;
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/*  46 */     return TypeInfo.TYPE_BOOLEAN;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getValueType() {
/*  51 */     return 8;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMemory() {
/*  57 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/*  62 */     return paramStringBuilder.append(getString());
/*     */   }
/*     */ 
/*     */   
/*     */   public String getString() {
/*  67 */     return this.value ? "TRUE" : "FALSE";
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getBoolean() {
/*  72 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte getByte() {
/*  77 */     return this.value ? 1 : 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public short getShort() {
/*  82 */     return this.value ? 1 : 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getInt() {
/*  87 */     return this.value ? 1 : 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLong() {
/*  92 */     return this.value ? 1L : 0L;
/*     */   }
/*     */ 
/*     */   
/*     */   public BigDecimal getBigDecimal() {
/*  97 */     return this.value ? BigDecimal.ONE : BigDecimal.ZERO;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getFloat() {
/* 102 */     return this.value ? 1.0F : 0.0F;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getDouble() {
/* 107 */     return this.value ? 1.0D : 0.0D;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value negate() {
/* 112 */     return this.value ? FALSE : TRUE;
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTypeSafe(Value paramValue, CompareMode paramCompareMode, CastDataProvider paramCastDataProvider) {
/* 117 */     return Boolean.compare(this.value, ((ValueBoolean)paramValue).value);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 122 */     return this.value ? 1 : 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueBoolean get(boolean paramBoolean) {
/* 132 */     return paramBoolean ? TRUE : FALSE;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object paramObject) {
/* 138 */     return (this == paramObject);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ValueBoolean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */