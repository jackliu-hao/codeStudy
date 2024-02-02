/*     */ package com.mysql.cj.result;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.conf.PropertySet;
/*     */ import com.mysql.cj.exceptions.DataConversionException;
/*     */ import com.mysql.cj.exceptions.DataReadException;
/*     */ import com.mysql.cj.protocol.InternalDate;
/*     */ import com.mysql.cj.protocol.InternalTime;
/*     */ import com.mysql.cj.protocol.InternalTimestamp;
/*     */ import com.mysql.cj.protocol.a.MysqlTextValueDecoder;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import java.time.LocalDateTime;
/*     */ import java.time.OffsetDateTime;
/*     */ import java.time.format.DateTimeParseException;
/*     */ import java.util.TimeZone;
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
/*     */ public class OffsetDateTimeValueFactory
/*     */   extends AbstractDateTimeValueFactory<OffsetDateTime>
/*     */ {
/*     */   private TimeZone defaultTimeZone;
/*     */   private TimeZone connectionTimeZone;
/*     */   
/*     */   public OffsetDateTimeValueFactory(PropertySet pset, TimeZone defaultTimeZone, TimeZone connectionTimeZone) {
/*  57 */     super(pset);
/*  58 */     this.defaultTimeZone = defaultTimeZone;
/*  59 */     this.connectionTimeZone = connectionTimeZone;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OffsetDateTime localCreateFromDate(InternalDate idate) {
/*  69 */     if (idate.getYear() == 0 && idate.getMonth() == 0 && idate.getDay() == 0) {
/*  70 */       throw new DataReadException(Messages.getString("ResultSet.InvalidZeroDate"));
/*     */     }
/*  72 */     return LocalDateTime.of(idate.getYear(), idate.getMonth(), idate.getDay(), 0, 0, 0, 0).atZone(this.defaultTimeZone.toZoneId()).toOffsetDateTime();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OffsetDateTime localCreateFromTime(InternalTime it) {
/*  82 */     if (it.getHours() < 0 || it.getHours() >= 24) {
/*  83 */       throw new DataReadException(Messages.getString("ResultSet.InvalidTimeValue", new Object[] { it.toString() }));
/*     */     }
/*  85 */     return LocalDateTime.of(1970, 1, 1, it.getHours(), it.getMinutes(), it.getSeconds(), it.getNanos()).atZone(this.defaultTimeZone.toZoneId())
/*  86 */       .toOffsetDateTime();
/*     */   }
/*     */ 
/*     */   
/*     */   public OffsetDateTime localCreateFromTimestamp(InternalTimestamp its) {
/*  91 */     if (its.getYear() == 0 && its.getMonth() == 0 && its.getDay() == 0) {
/*  92 */       throw new DataReadException(Messages.getString("ResultSet.InvalidZeroDate"));
/*     */     }
/*  94 */     return LocalDateTime.of(its.getYear(), its.getMonth(), its.getDay(), its.getHours(), its.getMinutes(), its.getSeconds(), its.getNanos())
/*  95 */       .atZone((((Boolean)this.pset.getBooleanProperty(PropertyKey.preserveInstants).getValue()).booleanValue() ? this.connectionTimeZone : this.defaultTimeZone).toZoneId())
/*  96 */       .toOffsetDateTime();
/*     */   }
/*     */ 
/*     */   
/*     */   public OffsetDateTime localCreateFromDatetime(InternalTimestamp its) {
/* 101 */     if (its.getYear() == 0 && its.getMonth() == 0 && its.getDay() == 0) {
/* 102 */       throw new DataReadException(Messages.getString("ResultSet.InvalidZeroDate"));
/*     */     }
/* 104 */     return LocalDateTime.of(its.getYear(), its.getMonth(), its.getDay(), its.getHours(), its.getMinutes(), its.getSeconds(), its.getNanos())
/* 105 */       .atZone((((Boolean)this.pset.getBooleanProperty(PropertyKey.preserveInstants).getValue()).booleanValue() ? this.connectionTimeZone : this.defaultTimeZone).toZoneId())
/* 106 */       .toOffsetDateTime();
/*     */   }
/*     */ 
/*     */   
/*     */   public OffsetDateTime createFromBytes(byte[] bytes, int offset, int length, Field f) {
/* 111 */     if (length == 0 && ((Boolean)this.pset.getBooleanProperty(PropertyKey.emptyStringsConvertToZero).getValue()).booleanValue()) {
/* 112 */       return createFromLong(0L);
/*     */     }
/*     */     
/* 115 */     String s = StringUtils.toString(bytes, offset, length, f.getEncoding());
/* 116 */     byte[] newBytes = s.getBytes();
/*     */     
/* 118 */     if (MysqlTextValueDecoder.isDate(s)) {
/* 119 */       return createFromDate(MysqlTextValueDecoder.getDate(newBytes, 0, newBytes.length));
/*     */     }
/* 121 */     if (MysqlTextValueDecoder.isTime(s)) {
/* 122 */       return createFromTime(MysqlTextValueDecoder.getTime(newBytes, 0, newBytes.length, f.getDecimals()));
/*     */     }
/* 124 */     if (MysqlTextValueDecoder.isTimestamp(s)) {
/* 125 */       return createFromTimestamp(MysqlTextValueDecoder.getTimestamp(newBytes, 0, newBytes.length, f.getDecimals()));
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/* 130 */       return OffsetDateTime.parse(s.replace(" ", "T"));
/* 131 */     } catch (DateTimeParseException e) {
/* 132 */       throw new DataConversionException(Messages.getString("ResultSet.UnableToConvertString", new Object[] { s, getTargetTypeName() }));
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String getTargetTypeName() {
/* 138 */     return OffsetDateTime.class.getName();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\result\OffsetDateTimeValueFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */