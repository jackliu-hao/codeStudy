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
/*     */ import java.time.ZonedDateTime;
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
/*     */ public class ZonedDateTimeValueFactory
/*     */   extends AbstractDateTimeValueFactory<ZonedDateTime>
/*     */ {
/*     */   private TimeZone defaultTimeZone;
/*     */   private TimeZone connectionTimeZone;
/*     */   
/*     */   public ZonedDateTimeValueFactory(PropertySet pset, TimeZone defaultTimeZone, TimeZone connectionTimeZone) {
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
/*     */   public ZonedDateTime localCreateFromDate(InternalDate idate) {
/*  69 */     if (idate.getYear() == 0 && idate.getMonth() == 0 && idate.getDay() == 0) {
/*  70 */       throw new DataReadException(Messages.getString("ResultSet.InvalidZeroDate"));
/*     */     }
/*  72 */     return LocalDateTime.of(idate.getYear(), idate.getMonth(), idate.getDay(), 0, 0, 0, 0).atZone(this.defaultTimeZone.toZoneId());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZonedDateTime localCreateFromTime(InternalTime it) {
/*  82 */     if (it.getHours() < 0 || it.getHours() >= 24) {
/*  83 */       throw new DataReadException(Messages.getString("ResultSet.InvalidTimeValue", new Object[] { it.toString() }));
/*     */     }
/*  85 */     return LocalDateTime.of(1970, 1, 1, it.getHours(), it.getMinutes(), it.getSeconds(), it.getNanos()).atZone(this.defaultTimeZone.toZoneId());
/*     */   }
/*     */ 
/*     */   
/*     */   public ZonedDateTime localCreateFromTimestamp(InternalTimestamp its) {
/*  90 */     if (its.getYear() == 0 && its.getMonth() == 0 && its.getDay() == 0) {
/*  91 */       throw new DataReadException(Messages.getString("ResultSet.InvalidZeroDate"));
/*     */     }
/*  93 */     return LocalDateTime.of(its.getYear(), its.getMonth(), its.getDay(), its.getHours(), its.getMinutes(), its.getSeconds(), its.getNanos())
/*  94 */       .atZone((((Boolean)this.pset.getBooleanProperty(PropertyKey.preserveInstants).getValue()).booleanValue() ? this.connectionTimeZone : this.defaultTimeZone).toZoneId());
/*     */   }
/*     */ 
/*     */   
/*     */   public ZonedDateTime localCreateFromDatetime(InternalTimestamp its) {
/*  99 */     if (its.getYear() == 0 && its.getMonth() == 0 && its.getDay() == 0) {
/* 100 */       throw new DataReadException(Messages.getString("ResultSet.InvalidZeroDate"));
/*     */     }
/* 102 */     return LocalDateTime.of(its.getYear(), its.getMonth(), its.getDay(), its.getHours(), its.getMinutes(), its.getSeconds(), its.getNanos())
/* 103 */       .atZone((((Boolean)this.pset.getBooleanProperty(PropertyKey.preserveInstants).getValue()).booleanValue() ? this.connectionTimeZone : this.defaultTimeZone).toZoneId());
/*     */   }
/*     */ 
/*     */   
/*     */   public ZonedDateTime createFromBytes(byte[] bytes, int offset, int length, Field f) {
/* 108 */     if (length == 0 && ((Boolean)this.pset.getBooleanProperty(PropertyKey.emptyStringsConvertToZero).getValue()).booleanValue()) {
/* 109 */       return createFromLong(0L);
/*     */     }
/*     */     
/* 112 */     String s = StringUtils.toString(bytes, offset, length, f.getEncoding());
/* 113 */     byte[] newBytes = s.getBytes();
/*     */     
/* 115 */     if (MysqlTextValueDecoder.isDate(s)) {
/* 116 */       return createFromDate(MysqlTextValueDecoder.getDate(newBytes, 0, newBytes.length));
/*     */     }
/* 118 */     if (MysqlTextValueDecoder.isTime(s)) {
/* 119 */       return createFromTime(MysqlTextValueDecoder.getTime(newBytes, 0, newBytes.length, f.getDecimals()));
/*     */     }
/* 121 */     if (MysqlTextValueDecoder.isTimestamp(s)) {
/* 122 */       return createFromTimestamp(MysqlTextValueDecoder.getTimestamp(newBytes, 0, newBytes.length, f.getDecimals()));
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/* 127 */       return ZonedDateTime.parse(s.replace(" ", "T"));
/* 128 */     } catch (DateTimeParseException e) {
/* 129 */       throw new DataConversionException(Messages.getString("ResultSet.UnableToConvertString", new Object[] { s, getTargetTypeName() }));
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String getTargetTypeName() {
/* 135 */     return ZonedDateTime.class.getName();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\result\ZonedDateTimeValueFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */