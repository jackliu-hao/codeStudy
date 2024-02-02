/*     */ package com.mysql.cj.result;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.WarningListener;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.conf.PropertySet;
/*     */ import com.mysql.cj.exceptions.DataConversionException;
/*     */ import com.mysql.cj.exceptions.DataReadException;
/*     */ import com.mysql.cj.protocol.InternalDate;
/*     */ import com.mysql.cj.protocol.InternalTime;
/*     */ import com.mysql.cj.protocol.InternalTimestamp;
/*     */ import com.mysql.cj.protocol.a.MysqlTextValueDecoder;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import java.time.LocalTime;
/*     */ import java.time.OffsetTime;
/*     */ import java.time.ZoneOffset;
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
/*     */ public class OffsetTimeValueFactory
/*     */   extends AbstractDateTimeValueFactory<OffsetTime>
/*     */ {
/*     */   private WarningListener warningListener;
/*     */   private TimeZone tz;
/*     */   
/*     */   public OffsetTimeValueFactory(PropertySet pset, TimeZone tz) {
/*  58 */     super(pset);
/*  59 */     this.tz = tz;
/*     */   }
/*     */   
/*     */   public OffsetTimeValueFactory(PropertySet pset, TimeZone tz, WarningListener warningListener) {
/*  63 */     this(pset, tz);
/*  64 */     this.warningListener = warningListener;
/*     */   }
/*     */ 
/*     */   
/*     */   OffsetTime localCreateFromDate(InternalDate idate) {
/*  69 */     return LocalTime.of(0, 0).atOffset(ZoneOffset.ofTotalSeconds(this.tz.getRawOffset() / 1000));
/*     */   }
/*     */ 
/*     */   
/*     */   public OffsetTime localCreateFromTime(InternalTime it) {
/*  74 */     if (it.getHours() < 0 || it.getHours() >= 24) {
/*  75 */       throw new DataReadException(Messages.getString("ResultSet.InvalidTimeValue", new Object[] { it.toString() }));
/*     */     }
/*  77 */     return LocalTime.of(it.getHours(), it.getMinutes(), it.getSeconds(), it.getNanos()).atOffset(ZoneOffset.ofTotalSeconds(this.tz.getRawOffset() / 1000));
/*     */   }
/*     */ 
/*     */   
/*     */   public OffsetTime localCreateFromTimestamp(InternalTimestamp its) {
/*  82 */     if (this.warningListener != null) {
/*  83 */       this.warningListener.warningEncountered(Messages.getString("ResultSet.PrecisionLostWarning", new Object[] { getTargetTypeName() }));
/*     */     }
/*     */     
/*  86 */     return createFromTime(new InternalTime(its.getHours(), its.getMinutes(), its.getSeconds(), its.getNanos(), its.getScale()));
/*     */   }
/*     */ 
/*     */   
/*     */   public OffsetTime localCreateFromDatetime(InternalTimestamp its) {
/*  91 */     if (this.warningListener != null) {
/*  92 */       this.warningListener.warningEncountered(Messages.getString("ResultSet.PrecisionLostWarning", new Object[] { getTargetTypeName() }));
/*     */     }
/*     */     
/*  95 */     return createFromTime(new InternalTime(its.getHours(), its.getMinutes(), its.getSeconds(), its.getNanos(), its.getScale()));
/*     */   }
/*     */ 
/*     */   
/*     */   public OffsetTime createFromBytes(byte[] bytes, int offset, int length, Field f) {
/* 100 */     if (length == 0 && ((Boolean)this.pset.getBooleanProperty(PropertyKey.emptyStringsConvertToZero).getValue()).booleanValue()) {
/* 101 */       return createFromLong(0L);
/*     */     }
/*     */     
/* 104 */     String s = StringUtils.toString(bytes, offset, length, f.getEncoding());
/* 105 */     byte[] newBytes = s.getBytes();
/*     */     
/* 107 */     if (MysqlTextValueDecoder.isDate(s)) {
/* 108 */       return createFromDate(MysqlTextValueDecoder.getDate(newBytes, 0, newBytes.length));
/*     */     }
/* 110 */     if (MysqlTextValueDecoder.isTime(s)) {
/* 111 */       return createFromTime(MysqlTextValueDecoder.getTime(newBytes, 0, newBytes.length, f.getDecimals()));
/*     */     }
/* 113 */     if (MysqlTextValueDecoder.isTimestamp(s)) {
/* 114 */       return createFromTimestamp(MysqlTextValueDecoder.getTimestamp(newBytes, 0, newBytes.length, f.getDecimals()));
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/* 119 */       return OffsetTime.parse(s);
/* 120 */     } catch (DateTimeParseException e) {
/* 121 */       throw new DataConversionException(Messages.getString("ResultSet.UnableToConvertString", new Object[] { s, getTargetTypeName() }));
/*     */     } 
/*     */   }
/*     */   
/*     */   public String getTargetTypeName() {
/* 126 */     return OffsetTime.class.getName();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\result\OffsetTimeValueFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */