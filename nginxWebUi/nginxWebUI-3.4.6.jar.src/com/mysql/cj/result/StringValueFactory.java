/*     */ package com.mysql.cj.result;
/*     */ 
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.conf.PropertySet;
/*     */ import com.mysql.cj.protocol.InternalDate;
/*     */ import com.mysql.cj.protocol.InternalTime;
/*     */ import com.mysql.cj.protocol.InternalTimestamp;
/*     */ import com.mysql.cj.util.DataTypeUtil;
/*     */ import com.mysql.cj.util.StringUtils;
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
/*     */ public class StringValueFactory
/*     */   implements ValueFactory<String>
/*     */ {
/*  48 */   protected PropertySet pset = null;
/*     */   
/*     */   public StringValueFactory(PropertySet pset) {
/*  51 */     this.pset = pset;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setPropertySet(PropertySet pset) {
/*  56 */     this.pset = pset;
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
/*     */   public String createFromDate(InternalDate idate) {
/*  68 */     return String.format("%04d-%02d-%02d", new Object[] { Integer.valueOf(idate.getYear()), Integer.valueOf(idate.getMonth()), Integer.valueOf(idate.getDay()) });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String createFromTime(InternalTime it) {
/*  79 */     return it.toString();
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
/*     */   public String createFromTimestamp(InternalTimestamp its) {
/*  91 */     return String.format("%s %s", new Object[] { createFromDate((InternalDate)its), 
/*  92 */           createFromTime(new InternalTime(its.getHours(), its.getMinutes(), its.getSeconds(), its.getNanos(), its.getScale())) });
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
/*     */   public String createFromDatetime(InternalTimestamp its) {
/* 104 */     return String.format("%s %s", new Object[] { createFromDate((InternalDate)its), 
/* 105 */           createFromTime(new InternalTime(its.getHours(), its.getMinutes(), its.getSeconds(), its.getNanos(), its.getScale())) });
/*     */   }
/*     */   
/*     */   public String createFromLong(long l) {
/* 109 */     return String.valueOf(l);
/*     */   }
/*     */   
/*     */   public String createFromBigInteger(BigInteger i) {
/* 113 */     return i.toString();
/*     */   }
/*     */   
/*     */   public String createFromDouble(double d) {
/* 117 */     return String.valueOf(d);
/*     */   }
/*     */   
/*     */   public String createFromBigDecimal(BigDecimal d) {
/* 121 */     return d.toString();
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
/*     */   public String createFromBytes(byte[] bytes, int offset, int length, Field f) {
/* 139 */     return StringUtils.toString(bytes, offset, length, 
/* 140 */         (f.getCollationIndex() == 63) ? (String)this.pset.getStringProperty(PropertyKey.characterEncoding).getValue() : f
/* 141 */         .getEncoding());
/*     */   }
/*     */ 
/*     */   
/*     */   public String createFromBit(byte[] bytes, int offset, int length) {
/* 146 */     return createFromLong(DataTypeUtil.bitToLong(bytes, offset, length));
/*     */   }
/*     */ 
/*     */   
/*     */   public String createFromYear(long l) {
/* 151 */     if (((Boolean)this.pset.getBooleanProperty(PropertyKey.yearIsDateType).getValue()).booleanValue()) {
/* 152 */       if (l < 100L) {
/* 153 */         if (l <= 69L) {
/* 154 */           l += 100L;
/*     */         }
/* 156 */         l += 1900L;
/*     */       } 
/* 158 */       return createFromDate(new InternalDate((int)l, 1, 1));
/*     */     } 
/* 160 */     return createFromLong(l);
/*     */   }
/*     */   
/*     */   public String createFromNull() {
/* 164 */     return null;
/*     */   }
/*     */   
/*     */   public String getTargetTypeName() {
/* 168 */     return String.class.getName();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\result\StringValueFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */