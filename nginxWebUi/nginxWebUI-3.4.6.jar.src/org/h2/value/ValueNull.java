/*     */ package org.h2.value;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
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
/*     */ public final class ValueNull
/*     */   extends Value
/*     */ {
/*  23 */   public static final ValueNull INSTANCE = new ValueNull();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final int PRECISION = 1;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final int DISPLAY_SIZE = 4;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/*  41 */     return paramStringBuilder.append("NULL");
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/*  46 */     return TypeInfo.TYPE_NULL;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getValueType() {
/*  51 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMemory() {
/*  57 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getString() {
/*  62 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public Reader getReader() {
/*  67 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public Reader getReader(long paramLong1, long paramLong2) {
/*  72 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getBytes() {
/*  77 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public InputStream getInputStream() {
/*  82 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public InputStream getInputStream(long paramLong1, long paramLong2) {
/*  87 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getBoolean() {
/*  92 */     throw DbException.getInternalError();
/*     */   }
/*     */ 
/*     */   
/*     */   public byte getByte() {
/*  97 */     throw DbException.getInternalError();
/*     */   }
/*     */ 
/*     */   
/*     */   public short getShort() {
/* 102 */     throw DbException.getInternalError();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getInt() {
/* 107 */     throw DbException.getInternalError();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLong() {
/* 112 */     throw DbException.getInternalError();
/*     */   }
/*     */ 
/*     */   
/*     */   public BigDecimal getBigDecimal() {
/* 117 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getFloat() {
/* 122 */     throw DbException.getInternalError();
/*     */   }
/*     */ 
/*     */   
/*     */   public double getDouble() {
/* 127 */     throw DbException.getInternalError();
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTypeSafe(Value paramValue, CompareMode paramCompareMode, CastDataProvider paramCastDataProvider) {
/* 132 */     throw DbException.getInternalError("compare null");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsNull() {
/* 137 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 142 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object paramObject) {
/* 147 */     return (paramObject == this);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ValueNull.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */