/*     */ package com.mysql.cj.result;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.WarningListener;
/*     */ import com.mysql.cj.conf.PropertySet;
/*     */ import com.mysql.cj.exceptions.DataReadException;
/*     */ import com.mysql.cj.protocol.InternalDate;
/*     */ import com.mysql.cj.protocol.InternalTime;
/*     */ import com.mysql.cj.protocol.InternalTimestamp;
/*     */ import java.sql.Date;
/*     */ import java.time.LocalDate;
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
/*     */ public class SqlDateValueFactory
/*     */   extends AbstractDateTimeValueFactory<Date>
/*     */ {
/*     */   private WarningListener warningListener;
/*     */   private Calendar cal;
/*     */   
/*     */   public SqlDateValueFactory(PropertySet pset, Calendar calendar, TimeZone tz) {
/*  57 */     super(pset);
/*  58 */     if (calendar != null) {
/*  59 */       this.cal = (Calendar)calendar.clone();
/*     */     } else {
/*     */       
/*  62 */       this.cal = Calendar.getInstance(tz, Locale.US);
/*  63 */       this.cal.set(14, 0);
/*  64 */       this.cal.setLenient(false);
/*     */     } 
/*     */   }
/*     */   
/*     */   public SqlDateValueFactory(PropertySet pset, Calendar calendar, TimeZone tz, WarningListener warningListener) {
/*  69 */     this(pset, calendar, tz);
/*  70 */     this.warningListener = warningListener;
/*     */   }
/*     */ 
/*     */   
/*     */   public Date localCreateFromDate(InternalDate idate) {
/*  75 */     synchronized (this.cal) {
/*     */       
/*  77 */       if (idate.isZero()) {
/*  78 */         throw new DataReadException(Messages.getString("ResultSet.InvalidZeroDate"));
/*     */       }
/*     */       
/*  81 */       this.cal.clear();
/*  82 */       this.cal.set(idate.getYear(), idate.getMonth() - 1, idate.getDay());
/*  83 */       long ms = this.cal.getTimeInMillis();
/*  84 */       return new Date(ms);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Date localCreateFromTime(InternalTime it) {
/*  93 */     if (this.warningListener != null)
/*     */     {
/*  95 */       this.warningListener.warningEncountered(Messages.getString("ResultSet.ImplicitDatePartWarning", new Object[] { "java.sql.Date" }));
/*     */     }
/*     */     
/*  98 */     return Date.valueOf(LocalDate.of(1970, 1, 1));
/*     */   }
/*     */ 
/*     */   
/*     */   public Date localCreateFromTimestamp(InternalTimestamp its) {
/* 103 */     if (this.warningListener != null)
/*     */     {
/* 105 */       this.warningListener.warningEncountered(Messages.getString("ResultSet.PrecisionLostWarning", new Object[] { "java.sql.Date" }));
/*     */     }
/*     */ 
/*     */     
/* 109 */     return createFromDate((InternalDate)its);
/*     */   }
/*     */ 
/*     */   
/*     */   public Date localCreateFromDatetime(InternalTimestamp its) {
/* 114 */     if (this.warningListener != null)
/*     */     {
/* 116 */       this.warningListener.warningEncountered(Messages.getString("ResultSet.PrecisionLostWarning", new Object[] { "java.sql.Date" }));
/*     */     }
/*     */ 
/*     */     
/* 120 */     return createFromDate((InternalDate)its);
/*     */   }
/*     */   
/*     */   public String getTargetTypeName() {
/* 124 */     return Date.class.getName();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\result\SqlDateValueFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */