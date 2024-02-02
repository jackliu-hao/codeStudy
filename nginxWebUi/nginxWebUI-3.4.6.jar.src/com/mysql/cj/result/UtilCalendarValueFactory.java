/*     */ package com.mysql.cj.result;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.conf.PropertySet;
/*     */ import com.mysql.cj.exceptions.DataReadException;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.WrongArgumentException;
/*     */ import com.mysql.cj.protocol.InternalDate;
/*     */ import com.mysql.cj.protocol.InternalTime;
/*     */ import com.mysql.cj.protocol.InternalTimestamp;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UtilCalendarValueFactory
/*     */   extends AbstractDateTimeValueFactory<Calendar>
/*     */ {
/*     */   private TimeZone defaultTimeZone;
/*     */   private TimeZone connectionTimeZone;
/*     */   
/*     */   public UtilCalendarValueFactory(PropertySet pset, TimeZone defaultTimeZone, TimeZone connectionTimeZone) {
/*  62 */     super(pset);
/*  63 */     this.defaultTimeZone = defaultTimeZone;
/*  64 */     this.connectionTimeZone = connectionTimeZone;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Calendar localCreateFromDate(InternalDate idate) {
/*  74 */     if (idate.getYear() == 0 && idate.getMonth() == 0 && idate.getDay() == 0) {
/*  75 */       throw new DataReadException(Messages.getString("ResultSet.InvalidZeroDate"));
/*     */     }
/*     */     
/*     */     try {
/*  79 */       Calendar c = Calendar.getInstance(this.defaultTimeZone, Locale.US);
/*  80 */       c.set(idate.getYear(), idate.getMonth() - 1, idate.getDay(), 0, 0, 0);
/*  81 */       c.set(14, 0);
/*  82 */       c.setLenient(false);
/*  83 */       return c;
/*  84 */     } catch (IllegalArgumentException e) {
/*  85 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, e.getMessage(), e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Calendar localCreateFromTime(InternalTime it) {
/*  96 */     if (it.getHours() < 0 || it.getHours() >= 24) {
/*  97 */       throw new DataReadException(Messages.getString("ResultSet.InvalidTimeValue", new Object[] { it.toString() }));
/*     */     }
/*     */     
/*     */     try {
/* 101 */       Calendar c = Calendar.getInstance(this.defaultTimeZone, Locale.US);
/* 102 */       c.set(1970, 0, 1, it.getHours(), it.getMinutes(), it.getSeconds());
/* 103 */       c.set(14, it.getNanos() / 1000000);
/* 104 */       c.setLenient(false);
/* 105 */       return c;
/* 106 */     } catch (IllegalArgumentException e) {
/* 107 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, e.getMessage(), e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Calendar localCreateFromTimestamp(InternalTimestamp its) {
/* 113 */     if (its.getYear() == 0 && its.getMonth() == 0 && its.getDay() == 0) {
/* 114 */       throw new DataReadException(Messages.getString("ResultSet.InvalidZeroDate"));
/*     */     }
/*     */     
/*     */     try {
/* 118 */       Calendar c = Calendar.getInstance(
/* 119 */           ((Boolean)this.pset.getBooleanProperty(PropertyKey.preserveInstants).getValue()).booleanValue() ? this.connectionTimeZone : this.defaultTimeZone, Locale.US);
/* 120 */       c.set(its.getYear(), its.getMonth() - 1, its.getDay(), its.getHours(), its.getMinutes(), its.getSeconds());
/* 121 */       c.set(14, its.getNanos() / 1000000);
/* 122 */       c.setLenient(false);
/* 123 */       return c;
/* 124 */     } catch (IllegalArgumentException e) {
/* 125 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, e.getMessage(), e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Calendar localCreateFromDatetime(InternalTimestamp its) {
/* 131 */     if (its.getYear() == 0 && its.getMonth() == 0 && its.getDay() == 0) {
/* 132 */       throw new DataReadException(Messages.getString("ResultSet.InvalidZeroDate"));
/*     */     }
/*     */     
/*     */     try {
/* 136 */       Calendar c = Calendar.getInstance(
/* 137 */           ((Boolean)this.pset.getBooleanProperty(PropertyKey.preserveInstants).getValue()).booleanValue() ? this.connectionTimeZone : this.defaultTimeZone, Locale.US);
/* 138 */       c.set(its.getYear(), its.getMonth() - 1, its.getDay(), its.getHours(), its.getMinutes(), its.getSeconds());
/* 139 */       c.set(14, its.getNanos() / 1000000);
/* 140 */       c.setLenient(false);
/* 141 */       return c;
/* 142 */     } catch (IllegalArgumentException e) {
/* 143 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, e.getMessage(), e);
/*     */     } 
/*     */   }
/*     */   
/*     */   public String getTargetTypeName() {
/* 148 */     return Calendar.class.getName();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\result\UtilCalendarValueFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */