/*     */ package ch.qos.logback.core.rolling;
/*     */ 
/*     */ import ch.qos.logback.core.rolling.helper.ArchiveRemover;
/*     */ import ch.qos.logback.core.rolling.helper.CompressionMode;
/*     */ import ch.qos.logback.core.rolling.helper.Compressor;
/*     */ import ch.qos.logback.core.rolling.helper.FileFilterUtil;
/*     */ import ch.qos.logback.core.rolling.helper.FileNamePattern;
/*     */ import ch.qos.logback.core.rolling.helper.RenameUtil;
/*     */ import ch.qos.logback.core.util.FileSize;
/*     */ import java.io.File;
/*     */ import java.util.Date;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
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
/*     */ public class TimeBasedRollingPolicy<E>
/*     */   extends RollingPolicyBase
/*     */   implements TriggeringPolicy<E>
/*     */ {
/*     */   static final String FNP_NOT_SET = "The FileNamePattern option must be set before using TimeBasedRollingPolicy. ";
/*     */   FileNamePattern fileNamePatternWithoutCompSuffix;
/*     */   private Compressor compressor;
/*  50 */   private RenameUtil renameUtil = new RenameUtil();
/*     */   
/*     */   Future<?> compressionFuture;
/*     */   Future<?> cleanUpFuture;
/*  54 */   private int maxHistory = 0;
/*  55 */   protected FileSize totalSizeCap = new FileSize(0L);
/*     */   
/*     */   private ArchiveRemover archiveRemover;
/*     */   
/*     */   TimeBasedFileNamingAndTriggeringPolicy<E> timeBasedFileNamingAndTriggeringPolicy;
/*     */   
/*     */   boolean cleanHistoryOnStart = false;
/*     */ 
/*     */   
/*     */   public void start() {
/*  65 */     this.renameUtil.setContext(this.context);
/*     */ 
/*     */     
/*  68 */     if (this.fileNamePatternStr != null) {
/*  69 */       this.fileNamePattern = new FileNamePattern(this.fileNamePatternStr, this.context);
/*  70 */       determineCompressionMode();
/*     */     } else {
/*  72 */       addWarn("The FileNamePattern option must be set before using TimeBasedRollingPolicy. ");
/*  73 */       addWarn("See also http://logback.qos.ch/codes.html#tbr_fnp_not_set");
/*  74 */       throw new IllegalStateException("The FileNamePattern option must be set before using TimeBasedRollingPolicy. See also http://logback.qos.ch/codes.html#tbr_fnp_not_set");
/*     */     } 
/*     */     
/*  77 */     this.compressor = new Compressor(this.compressionMode);
/*  78 */     this.compressor.setContext(this.context);
/*     */ 
/*     */     
/*  81 */     this.fileNamePatternWithoutCompSuffix = new FileNamePattern(Compressor.computeFileNameStrWithoutCompSuffix(this.fileNamePatternStr, this.compressionMode), this.context);
/*     */     
/*  83 */     addInfo("Will use the pattern " + this.fileNamePatternWithoutCompSuffix + " for the active file");
/*     */     
/*  85 */     if (this.compressionMode == CompressionMode.ZIP) {
/*  86 */       String zipEntryFileNamePatternStr = transformFileNamePattern2ZipEntry(this.fileNamePatternStr);
/*  87 */       this.zipEntryFileNamePattern = new FileNamePattern(zipEntryFileNamePatternStr, this.context);
/*     */     } 
/*     */     
/*  90 */     if (this.timeBasedFileNamingAndTriggeringPolicy == null) {
/*  91 */       this.timeBasedFileNamingAndTriggeringPolicy = new DefaultTimeBasedFileNamingAndTriggeringPolicy<E>();
/*     */     }
/*  93 */     this.timeBasedFileNamingAndTriggeringPolicy.setContext(this.context);
/*  94 */     this.timeBasedFileNamingAndTriggeringPolicy.setTimeBasedRollingPolicy(this);
/*  95 */     this.timeBasedFileNamingAndTriggeringPolicy.start();
/*     */     
/*  97 */     if (!this.timeBasedFileNamingAndTriggeringPolicy.isStarted()) {
/*  98 */       addWarn("Subcomponent did not start. TimeBasedRollingPolicy will not start.");
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */ 
/*     */     
/* 105 */     if (this.maxHistory != 0) {
/* 106 */       this.archiveRemover = this.timeBasedFileNamingAndTriggeringPolicy.getArchiveRemover();
/* 107 */       this.archiveRemover.setMaxHistory(this.maxHistory);
/* 108 */       this.archiveRemover.setTotalSizeCap(this.totalSizeCap.getSize());
/* 109 */       if (this.cleanHistoryOnStart) {
/* 110 */         addInfo("Cleaning on start up");
/* 111 */         Date now = new Date(this.timeBasedFileNamingAndTriggeringPolicy.getCurrentTime());
/* 112 */         this.cleanUpFuture = this.archiveRemover.cleanAsynchronously(now);
/*     */       } 
/* 114 */     } else if (!isUnboundedTotalSizeCap()) {
/* 115 */       addWarn("'maxHistory' is not set, ignoring 'totalSizeCap' option with value [" + this.totalSizeCap + "]");
/*     */     } 
/*     */     
/* 118 */     super.start();
/*     */   }
/*     */   
/*     */   protected boolean isUnboundedTotalSizeCap() {
/* 122 */     return (this.totalSizeCap.getSize() == 0L);
/*     */   }
/*     */ 
/*     */   
/*     */   public void stop() {
/* 127 */     if (!isStarted())
/*     */       return; 
/* 129 */     waitForAsynchronousJobToStop(this.compressionFuture, "compression");
/* 130 */     waitForAsynchronousJobToStop(this.cleanUpFuture, "clean-up");
/* 131 */     super.stop();
/*     */   }
/*     */   
/*     */   private void waitForAsynchronousJobToStop(Future<?> aFuture, String jobDescription) {
/* 135 */     if (aFuture != null) {
/*     */       try {
/* 137 */         aFuture.get(30L, TimeUnit.SECONDS);
/* 138 */       } catch (TimeoutException e) {
/* 139 */         addError("Timeout while waiting for " + jobDescription + " job to finish", e);
/* 140 */       } catch (Exception e) {
/* 141 */         addError("Unexpected exception while waiting for " + jobDescription + " job to finish", e);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private String transformFileNamePattern2ZipEntry(String fileNamePatternStr) {
/* 147 */     String slashified = FileFilterUtil.slashify(fileNamePatternStr);
/* 148 */     return FileFilterUtil.afterLastSlash(slashified);
/*     */   }
/*     */   
/*     */   public void setTimeBasedFileNamingAndTriggeringPolicy(TimeBasedFileNamingAndTriggeringPolicy<E> timeBasedTriggering) {
/* 152 */     this.timeBasedFileNamingAndTriggeringPolicy = timeBasedTriggering;
/*     */   }
/*     */   
/*     */   public TimeBasedFileNamingAndTriggeringPolicy<E> getTimeBasedFileNamingAndTriggeringPolicy() {
/* 156 */     return this.timeBasedFileNamingAndTriggeringPolicy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void rollover() throws RolloverFailure {
/* 164 */     String elapsedPeriodsFileName = this.timeBasedFileNamingAndTriggeringPolicy.getElapsedPeriodsFileName();
/*     */     
/* 166 */     String elapsedPeriodStem = FileFilterUtil.afterLastSlash(elapsedPeriodsFileName);
/*     */     
/* 168 */     if (this.compressionMode == CompressionMode.NONE) {
/* 169 */       if (getParentsRawFileProperty() != null) {
/* 170 */         this.renameUtil.rename(getParentsRawFileProperty(), elapsedPeriodsFileName);
/*     */       }
/*     */     }
/* 173 */     else if (getParentsRawFileProperty() == null) {
/* 174 */       this.compressionFuture = this.compressor.asyncCompress(elapsedPeriodsFileName, elapsedPeriodsFileName, elapsedPeriodStem);
/*     */     } else {
/* 176 */       this.compressionFuture = renameRawAndAsyncCompress(elapsedPeriodsFileName, elapsedPeriodStem);
/*     */     } 
/*     */ 
/*     */     
/* 180 */     if (this.archiveRemover != null) {
/* 181 */       Date now = new Date(this.timeBasedFileNamingAndTriggeringPolicy.getCurrentTime());
/* 182 */       this.cleanUpFuture = this.archiveRemover.cleanAsynchronously(now);
/*     */     } 
/*     */   }
/*     */   
/*     */   Future<?> renameRawAndAsyncCompress(String nameOfCompressedFile, String innerEntryName) throws RolloverFailure {
/* 187 */     String parentsRawFile = getParentsRawFileProperty();
/* 188 */     String tmpTarget = nameOfCompressedFile + System.nanoTime() + ".tmp";
/* 189 */     this.renameUtil.rename(parentsRawFile, tmpTarget);
/* 190 */     return this.compressor.asyncCompress(tmpTarget, nameOfCompressedFile, innerEntryName);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getActiveFileName() {
/* 214 */     String parentsRawFileProperty = getParentsRawFileProperty();
/* 215 */     if (parentsRawFileProperty != null) {
/* 216 */       return parentsRawFileProperty;
/*     */     }
/* 218 */     return this.timeBasedFileNamingAndTriggeringPolicy.getCurrentPeriodsFileNameWithoutCompressionSuffix();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isTriggeringEvent(File activeFile, E event) {
/* 223 */     return this.timeBasedFileNamingAndTriggeringPolicy.isTriggeringEvent(activeFile, event);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMaxHistory() {
/* 232 */     return this.maxHistory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMaxHistory(int maxHistory) {
/* 242 */     this.maxHistory = maxHistory;
/*     */   }
/*     */   
/*     */   public boolean isCleanHistoryOnStart() {
/* 246 */     return this.cleanHistoryOnStart;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCleanHistoryOnStart(boolean cleanHistoryOnStart) {
/* 255 */     this.cleanHistoryOnStart = cleanHistoryOnStart;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 260 */     return "c.q.l.core.rolling.TimeBasedRollingPolicy@" + hashCode();
/*     */   }
/*     */   
/*     */   public void setTotalSizeCap(FileSize totalSizeCap) {
/* 264 */     addInfo("setting totalSizeCap to " + totalSizeCap.toString());
/* 265 */     this.totalSizeCap = totalSizeCap;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\rolling\TimeBasedRollingPolicy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */