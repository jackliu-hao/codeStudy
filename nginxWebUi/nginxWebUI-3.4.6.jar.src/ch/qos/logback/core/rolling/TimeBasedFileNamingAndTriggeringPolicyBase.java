/*     */ package ch.qos.logback.core.rolling;
/*     */ 
/*     */ import ch.qos.logback.core.rolling.helper.ArchiveRemover;
/*     */ import ch.qos.logback.core.rolling.helper.DateTokenConverter;
/*     */ import ch.qos.logback.core.rolling.helper.RollingCalendar;
/*     */ import ch.qos.logback.core.spi.ContextAwareBase;
/*     */ import java.io.File;
/*     */ import java.util.Date;
/*     */ import java.util.Locale;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class TimeBasedFileNamingAndTriggeringPolicyBase<E>
/*     */   extends ContextAwareBase
/*     */   implements TimeBasedFileNamingAndTriggeringPolicy<E>
/*     */ {
/*  30 */   private static String COLLIDING_DATE_FORMAT_URL = "http://logback.qos.ch/codes.html#rfa_collision_in_dateFormat";
/*     */   
/*     */   protected TimeBasedRollingPolicy<E> tbrp;
/*     */   
/*  34 */   protected ArchiveRemover archiveRemover = null;
/*     */   
/*     */   protected String elapsedPeriodsFileName;
/*     */   protected RollingCalendar rc;
/*  38 */   protected long artificialCurrentTime = -1L;
/*  39 */   protected Date dateInCurrentPeriod = null;
/*     */   
/*     */   protected long nextCheck;
/*     */   protected boolean started = false;
/*     */   protected boolean errorFree = true;
/*     */   
/*     */   public boolean isStarted() {
/*  46 */     return this.started;
/*     */   }
/*     */   
/*     */   public void start() {
/*  50 */     DateTokenConverter<Object> dtc = this.tbrp.fileNamePattern.getPrimaryDateTokenConverter();
/*  51 */     if (dtc == null) {
/*  52 */       throw new IllegalStateException("FileNamePattern [" + this.tbrp.fileNamePattern.getPattern() + "] does not contain a valid DateToken");
/*     */     }
/*     */     
/*  55 */     if (dtc.getTimeZone() != null) {
/*  56 */       this.rc = new RollingCalendar(dtc.getDatePattern(), dtc.getTimeZone(), Locale.getDefault());
/*     */     } else {
/*  58 */       this.rc = new RollingCalendar(dtc.getDatePattern());
/*     */     } 
/*  60 */     addInfo("The date pattern is '" + dtc.getDatePattern() + "' from file name pattern '" + this.tbrp.fileNamePattern.getPattern() + "'.");
/*  61 */     this.rc.printPeriodicity(this);
/*     */     
/*  63 */     if (!this.rc.isCollisionFree()) {
/*  64 */       addError("The date format in FileNamePattern will result in collisions in the names of archived log files.");
/*  65 */       addError("For more information, please visit " + COLLIDING_DATE_FORMAT_URL);
/*  66 */       withErrors();
/*     */       
/*     */       return;
/*     */     } 
/*  70 */     setDateInCurrentPeriod(new Date(getCurrentTime()));
/*  71 */     if (this.tbrp.getParentsRawFileProperty() != null) {
/*  72 */       File currentFile = new File(this.tbrp.getParentsRawFileProperty());
/*  73 */       if (currentFile.exists() && currentFile.canRead()) {
/*  74 */         setDateInCurrentPeriod(new Date(currentFile.lastModified()));
/*     */       }
/*     */     } 
/*  77 */     addInfo("Setting initial period to " + this.dateInCurrentPeriod);
/*  78 */     computeNextCheck();
/*     */   }
/*     */   
/*     */   public void stop() {
/*  82 */     this.started = false;
/*     */   }
/*     */   
/*     */   protected void computeNextCheck() {
/*  86 */     this.nextCheck = this.rc.getNextTriggeringDate(this.dateInCurrentPeriod).getTime();
/*     */   }
/*     */   
/*     */   protected void setDateInCurrentPeriod(long now) {
/*  90 */     this.dateInCurrentPeriod.setTime(now);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDateInCurrentPeriod(Date _dateInCurrentPeriod) {
/*  96 */     this.dateInCurrentPeriod = _dateInCurrentPeriod;
/*     */   }
/*     */   
/*     */   public String getElapsedPeriodsFileName() {
/* 100 */     return this.elapsedPeriodsFileName;
/*     */   }
/*     */   
/*     */   public String getCurrentPeriodsFileNameWithoutCompressionSuffix() {
/* 104 */     return this.tbrp.fileNamePatternWithoutCompSuffix.convert(this.dateInCurrentPeriod);
/*     */   }
/*     */   
/*     */   public void setCurrentTime(long timeInMillis) {
/* 108 */     this.artificialCurrentTime = timeInMillis;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getCurrentTime() {
/* 113 */     if (this.artificialCurrentTime >= 0L) {
/* 114 */       return this.artificialCurrentTime;
/*     */     }
/* 116 */     return System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setTimeBasedRollingPolicy(TimeBasedRollingPolicy<E> _tbrp) {
/* 121 */     this.tbrp = _tbrp;
/*     */   }
/*     */ 
/*     */   
/*     */   public ArchiveRemover getArchiveRemover() {
/* 126 */     return this.archiveRemover;
/*     */   }
/*     */   
/*     */   protected void withErrors() {
/* 130 */     this.errorFree = false;
/*     */   }
/*     */   
/*     */   protected boolean isErrorFree() {
/* 134 */     return this.errorFree;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\rolling\TimeBasedFileNamingAndTriggeringPolicyBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */