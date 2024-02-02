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
/*     */ import java.sql.Timestamp;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SqlTimestampValueFactory
/*     */   extends AbstractDateTimeValueFactory<Timestamp>
/*     */ {
/*     */   private Calendar cal;
/*     */   private TimeZone defaultTimeZone;
/*     */   private TimeZone connectionTimeZone;
/*     */   
/*     */   public SqlTimestampValueFactory(PropertySet pset, Calendar calendar, TimeZone defaultTimeZone, TimeZone connectionTimeZone) {
/*  69 */     super(pset);
/*  70 */     this.defaultTimeZone = defaultTimeZone;
/*  71 */     this.connectionTimeZone = connectionTimeZone;
/*  72 */     this.cal = (calendar != null) ? (Calendar)calendar.clone() : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Timestamp localCreateFromDate(InternalDate idate) {
/*  82 */     if (idate.getYear() == 0 && idate.getMonth() == 0 && idate.getDay() == 0) {
/*  83 */       throw new DataReadException(Messages.getString("ResultSet.InvalidZeroDate"));
/*     */     }
/*     */     
/*  86 */     synchronized (this.defaultTimeZone) {
/*     */       Calendar c;
/*     */       
/*  89 */       if (this.cal != null) {
/*  90 */         c = this.cal;
/*     */       } else {
/*     */         
/*  93 */         c = Calendar.getInstance(this.defaultTimeZone, Locale.US);
/*  94 */         c.setLenient(false);
/*     */       } 
/*     */       
/*     */       try {
/*  98 */         c.clear();
/*  99 */         c.set(idate.getYear(), idate.getMonth() - 1, idate.getDay(), 0, 0, 0);
/* 100 */         return new Timestamp(c.getTimeInMillis());
/* 101 */       } catch (IllegalArgumentException e) {
/* 102 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, e.getMessage(), e);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Timestamp localCreateFromTime(InternalTime it) {
/* 114 */     if (it.getHours() < 0 || it.getHours() >= 24) {
/* 115 */       throw new DataReadException(Messages.getString("ResultSet.InvalidTimeValue", new Object[] { it.toString() }));
/*     */     }
/*     */     
/* 118 */     synchronized (this.defaultTimeZone) {
/*     */       Calendar c;
/*     */       
/* 121 */       if (this.cal != null) {
/* 122 */         c = this.cal;
/*     */       } else {
/*     */         
/* 125 */         c = Calendar.getInstance(this.defaultTimeZone, Locale.US);
/* 126 */         c.setLenient(false);
/*     */       } 
/*     */       
/*     */       try {
/* 130 */         c.set(1970, 0, 1, it.getHours(), it.getMinutes(), it.getSeconds());
/* 131 */         Timestamp ts = new Timestamp(c.getTimeInMillis());
/* 132 */         ts.setNanos(it.getNanos());
/* 133 */         return ts;
/* 134 */       } catch (IllegalArgumentException e) {
/* 135 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, e.getMessage(), e);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Timestamp localCreateFromTimestamp(InternalTimestamp its) {
/* 142 */     if (its.getYear() == 0 && its.getMonth() == 0 && its.getDay() == 0) {
/* 143 */       throw new DataReadException(Messages.getString("ResultSet.InvalidZeroDate"));
/*     */     }
/*     */     
/* 146 */     synchronized (this.defaultTimeZone) {
/*     */       Calendar c;
/*     */       
/* 149 */       if (this.cal != null) {
/* 150 */         c = this.cal;
/*     */       } else {
/*     */         
/* 153 */         c = Calendar.getInstance(((Boolean)this.pset.getBooleanProperty(PropertyKey.preserveInstants).getValue()).booleanValue() ? this.connectionTimeZone : this.defaultTimeZone, Locale.US);
/*     */         
/* 155 */         c.setLenient(false);
/*     */       } 
/*     */ 
/*     */       
/*     */       try {
/* 160 */         c.set(its.getYear(), its.getMonth() - 1, its.getDay(), its.getHours(), its.getMinutes(), its.getSeconds());
/* 161 */         Timestamp ts = new Timestamp(c.getTimeInMillis());
/* 162 */         ts.setNanos(its.getNanos());
/* 163 */         return ts;
/* 164 */       } catch (IllegalArgumentException e) {
/* 165 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, e.getMessage(), e);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Timestamp localCreateFromDatetime(InternalTimestamp its) {
/* 172 */     if (its.getYear() == 0 && its.getMonth() == 0 && its.getDay() == 0) {
/* 173 */       throw new DataReadException(Messages.getString("ResultSet.InvalidZeroDate"));
/*     */     }
/*     */     
/* 176 */     synchronized (this.defaultTimeZone) {
/*     */       Calendar c;
/*     */       
/* 179 */       if (this.cal != null) {
/* 180 */         c = this.cal;
/*     */       } else {
/*     */         
/* 183 */         c = Calendar.getInstance(((Boolean)this.pset.getBooleanProperty(PropertyKey.preserveInstants).getValue()).booleanValue() ? this.connectionTimeZone : this.defaultTimeZone, Locale.US);
/*     */         
/* 185 */         c.setLenient(false);
/*     */       } 
/*     */ 
/*     */       
/*     */       try {
/* 190 */         c.set(its.getYear(), its.getMonth() - 1, its.getDay(), its.getHours(), its.getMinutes(), its.getSeconds());
/* 191 */         Timestamp ts = new Timestamp(c.getTimeInMillis());
/* 192 */         ts.setNanos(its.getNanos());
/* 193 */         return ts;
/* 194 */       } catch (IllegalArgumentException e) {
/* 195 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, e.getMessage(), e);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public String getTargetTypeName() {
/* 201 */     return Timestamp.class.getName();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\result\SqlTimestampValueFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */