/*     */ package com.mysql.cj.result;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.conf.PropertySet;
/*     */ import com.mysql.cj.exceptions.DataConversionException;
/*     */ import com.mysql.cj.protocol.InternalDate;
/*     */ import com.mysql.cj.protocol.InternalTime;
/*     */ import com.mysql.cj.protocol.InternalTimestamp;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
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
/*     */ public abstract class DefaultValueFactory<T>
/*     */   implements ValueFactory<T>
/*     */ {
/*     */   protected boolean jdbcCompliantTruncationForReads = true;
/*     */   protected PropertySet pset;
/*     */   
/*     */   public DefaultValueFactory(PropertySet pset) {
/*  62 */     this.pset = null;
/*     */     this.pset = pset;
/*     */     this.jdbcCompliantTruncationForReads = ((Boolean)this.pset.getBooleanProperty(PropertyKey.jdbcCompliantTruncation).getInitialValue()).booleanValue();
/*     */   } public void setPropertySet(PropertySet pset) {
/*  66 */     this.pset = pset;
/*     */   }
/*     */   
/*     */   protected T unsupported(String sourceType) {
/*  70 */     throw new DataConversionException(Messages.getString("ResultSet.UnsupportedConversion", new Object[] { sourceType, getTargetTypeName() }));
/*     */   }
/*     */   
/*     */   public T createFromDate(InternalDate idate) {
/*  74 */     return unsupported("DATE");
/*     */   }
/*     */   
/*     */   public T createFromTime(InternalTime it) {
/*  78 */     return unsupported("TIME");
/*     */   }
/*     */   
/*     */   public T createFromTimestamp(InternalTimestamp its) {
/*  82 */     return unsupported("TIMESTAMP");
/*     */   }
/*     */ 
/*     */   
/*     */   public T createFromDatetime(InternalTimestamp its) {
/*  87 */     return unsupported("DATETIME");
/*     */   }
/*     */   
/*     */   public T createFromLong(long l) {
/*  91 */     return unsupported("LONG");
/*     */   }
/*     */   
/*     */   public T createFromBigInteger(BigInteger i) {
/*  95 */     return unsupported("BIGINT");
/*     */   }
/*     */   
/*     */   public T createFromDouble(double d) {
/*  99 */     return unsupported("DOUBLE");
/*     */   }
/*     */   
/*     */   public T createFromBigDecimal(BigDecimal d) {
/* 103 */     return unsupported("DECIMAL");
/*     */   }
/*     */   
/*     */   public T createFromBit(byte[] bytes, int offset, int length) {
/* 107 */     return unsupported("BIT");
/*     */   }
/*     */ 
/*     */   
/*     */   public T createFromYear(long l) {
/* 112 */     return unsupported("YEAR");
/*     */   }
/*     */   
/*     */   public T createFromNull() {
/* 116 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\result\DefaultValueFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */