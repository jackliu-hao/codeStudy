/*     */ package com.mysql.cj.result;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.WarningListener;
/*     */ import com.mysql.cj.conf.PropertySet;
/*     */ import com.mysql.cj.exceptions.DataReadException;
/*     */ import com.mysql.cj.protocol.InternalDate;
/*     */ import com.mysql.cj.protocol.InternalTime;
/*     */ import com.mysql.cj.protocol.InternalTimestamp;
/*     */ import java.sql.Time;
/*     */ import java.util.Calendar;
/*     */ import java.util.Locale;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SqlTimeValueFactory
/*     */   extends AbstractDateTimeValueFactory<Time>
/*     */ {
/*     */   private WarningListener warningListener;
/*     */   private Calendar cal;
/*     */   
/*     */   public SqlTimeValueFactory(PropertySet pset, Calendar calendar, TimeZone tz) {
/*  57 */     super(pset);
/*  58 */     if (calendar != null) {
/*  59 */       this.cal = (Calendar)calendar.clone();
/*     */     } else {
/*     */       
/*  62 */       this.cal = Calendar.getInstance(tz, Locale.US);
/*  63 */       this.cal.setLenient(false);
/*     */     } 
/*     */   }
/*     */   
/*     */   public SqlTimeValueFactory(PropertySet pset, Calendar calendar, TimeZone tz, WarningListener warningListener) {
/*  68 */     this(pset, calendar, tz);
/*  69 */     this.warningListener = warningListener;
/*     */   }
/*     */ 
/*     */   
/*     */   Time localCreateFromDate(InternalDate idate) {
/*  74 */     synchronized (this.cal) {
/*     */       
/*  76 */       this.cal.clear();
/*  77 */       return new Time(this.cal.getTimeInMillis());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Time localCreateFromTime(InternalTime it) {
/*  86 */     if (it.getHours() < 0 || it.getHours() >= 24) {
/*  87 */       throw new DataReadException(Messages.getString("ResultSet.InvalidTimeValue", new Object[] { it.toString() }));
/*     */     }
/*     */     
/*  90 */     synchronized (this.cal) {
/*     */ 
/*     */       
/*  93 */       this.cal.set(1970, 0, 1, it.getHours(), it.getMinutes(), it.getSeconds());
/*  94 */       this.cal.set(14, 0);
/*  95 */       long ms = (it.getNanos() / 1000000) + this.cal.getTimeInMillis();
/*  96 */       return new Time(ms);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Time localCreateFromDatetime(InternalTimestamp its) {
/* 105 */     if (this.warningListener != null)
/*     */     {
/* 107 */       this.warningListener.warningEncountered(Messages.getString("ResultSet.PrecisionLostWarning", new Object[] { "java.sql.Time" }));
/*     */     }
/*     */ 
/*     */     
/* 111 */     return createFromTime(new InternalTime(its.getHours(), its.getMinutes(), its.getSeconds(), its.getNanos(), its.getScale()));
/*     */   }
/*     */ 
/*     */   
/*     */   public Time localCreateFromTimestamp(InternalTimestamp its) {
/* 116 */     if (this.warningListener != null)
/*     */     {
/* 118 */       this.warningListener.warningEncountered(Messages.getString("ResultSet.PrecisionLostWarning", new Object[] { "java.sql.Time" }));
/*     */     }
/*     */ 
/*     */     
/* 122 */     return createFromTime(new InternalTime(its.getHours(), its.getMinutes(), its.getSeconds(), its.getNanos(), its.getScale()));
/*     */   }
/*     */   
/*     */   public String getTargetTypeName() {
/* 126 */     return Time.class.getName();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\result\SqlTimeValueFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */