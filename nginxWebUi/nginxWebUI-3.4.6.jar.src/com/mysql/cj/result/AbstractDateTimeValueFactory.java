/*     */ package com.mysql.cj.result;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.conf.PropertyDefinitions;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.conf.PropertySet;
/*     */ import com.mysql.cj.exceptions.DataConversionException;
/*     */ import com.mysql.cj.protocol.InternalDate;
/*     */ import com.mysql.cj.protocol.InternalTime;
/*     */ import com.mysql.cj.protocol.InternalTimestamp;
/*     */ import com.mysql.cj.protocol.a.MysqlTextValueDecoder;
/*     */ import com.mysql.cj.util.StringUtils;
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
/*     */ public abstract class AbstractDateTimeValueFactory<T>
/*     */   extends DefaultValueFactory<T>
/*     */ {
/*     */   public AbstractDateTimeValueFactory(PropertySet pset) {
/*  46 */     super(pset);
/*     */   }
/*     */ 
/*     */   
/*     */   abstract T localCreateFromDate(InternalDate paramInternalDate);
/*     */   
/*     */   abstract T localCreateFromTime(InternalTime paramInternalTime);
/*     */   
/*     */   abstract T localCreateFromTimestamp(InternalTimestamp paramInternalTimestamp);
/*     */   
/*     */   abstract T localCreateFromDatetime(InternalTimestamp paramInternalTimestamp);
/*     */   
/*     */   public T createFromDate(InternalDate idate) {
/*  59 */     if (idate.isZero()) {
/*  60 */       switch ((PropertyDefinitions.ZeroDatetimeBehavior)this.pset.getEnumProperty(PropertyKey.zeroDateTimeBehavior).getValue()) {
/*     */         case CONVERT_TO_NULL:
/*  62 */           return null;
/*     */         case ROUND:
/*  64 */           return localCreateFromDate(new InternalDate(1, 1, 1));
/*     */       } 
/*     */ 
/*     */     
/*     */     }
/*  69 */     return localCreateFromDate(idate);
/*     */   }
/*     */ 
/*     */   
/*     */   public T createFromTime(InternalTime it) {
/*  74 */     return localCreateFromTime(it);
/*     */   }
/*     */ 
/*     */   
/*     */   public T createFromTimestamp(InternalTimestamp its) {
/*  79 */     if (its.isZero()) {
/*  80 */       switch ((PropertyDefinitions.ZeroDatetimeBehavior)this.pset.getEnumProperty(PropertyKey.zeroDateTimeBehavior).getValue()) {
/*     */         case CONVERT_TO_NULL:
/*  82 */           return null;
/*     */         case ROUND:
/*  84 */           return localCreateFromTimestamp(new InternalTimestamp(1, 1, 1, 0, 0, 0, 0, 0));
/*     */       } 
/*     */ 
/*     */     
/*     */     }
/*  89 */     return localCreateFromTimestamp(its);
/*     */   }
/*     */ 
/*     */   
/*     */   public T createFromDatetime(InternalTimestamp its) {
/*  94 */     if (its.isZero()) {
/*  95 */       switch ((PropertyDefinitions.ZeroDatetimeBehavior)this.pset.getEnumProperty(PropertyKey.zeroDateTimeBehavior).getValue()) {
/*     */         case CONVERT_TO_NULL:
/*  97 */           return null;
/*     */         case ROUND:
/*  99 */           return localCreateFromDatetime(new InternalTimestamp(1, 1, 1, 0, 0, 0, 0, 0));
/*     */       } 
/*     */ 
/*     */     
/*     */     }
/* 104 */     return localCreateFromDatetime(its);
/*     */   }
/*     */ 
/*     */   
/*     */   public T createFromYear(long year) {
/* 109 */     if (((Boolean)this.pset.getBooleanProperty(PropertyKey.yearIsDateType).getValue()).booleanValue()) {
/* 110 */       if (year < 100L) {
/* 111 */         if (year <= 69L) {
/* 112 */           year += 100L;
/*     */         }
/* 114 */         year += 1900L;
/*     */       } 
/* 116 */       return createFromDate(new InternalDate((int)year, 1, 1));
/*     */     } 
/* 118 */     return createFromLong(year);
/*     */   }
/*     */ 
/*     */   
/*     */   public T createFromBytes(byte[] bytes, int offset, int length, Field f) {
/* 123 */     if (length == 0 && ((Boolean)this.pset.getBooleanProperty(PropertyKey.emptyStringsConvertToZero).getValue()).booleanValue()) {
/* 124 */       return createFromLong(0L);
/*     */     }
/*     */     
/* 127 */     String s = StringUtils.toString(bytes, offset, length, f.getEncoding());
/* 128 */     byte[] newBytes = s.getBytes();
/*     */     
/* 130 */     if (MysqlTextValueDecoder.isDate(s)) {
/* 131 */       return createFromDate(MysqlTextValueDecoder.getDate(newBytes, 0, newBytes.length));
/*     */     }
/* 133 */     if (MysqlTextValueDecoder.isTime(s)) {
/* 134 */       return createFromTime(MysqlTextValueDecoder.getTime(newBytes, 0, newBytes.length, f.getDecimals()));
/*     */     }
/* 136 */     if (MysqlTextValueDecoder.isTimestamp(s)) {
/* 137 */       return createFromTimestamp(MysqlTextValueDecoder.getTimestamp(newBytes, 0, newBytes.length, f.getDecimals()));
/*     */     }
/* 139 */     throw new DataConversionException(Messages.getString("ResultSet.UnableToConvertString", new Object[] { s, getTargetTypeName() }));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\result\AbstractDateTimeValueFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */