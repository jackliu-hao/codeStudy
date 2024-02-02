/*     */ package ch.qos.logback.core.rolling;
/*     */ 
/*     */ import ch.qos.logback.core.joran.spi.NoAutoStart;
/*     */ import ch.qos.logback.core.rolling.helper.ArchiveRemover;
/*     */ import ch.qos.logback.core.rolling.helper.CompressionMode;
/*     */ import ch.qos.logback.core.rolling.helper.FileFilterUtil;
/*     */ import ch.qos.logback.core.rolling.helper.SizeAndTimeBasedArchiveRemover;
/*     */ import ch.qos.logback.core.util.DefaultInvocationGate;
/*     */ import ch.qos.logback.core.util.FileSize;
/*     */ import ch.qos.logback.core.util.InvocationGate;
/*     */ import java.io.File;
/*     */ import java.util.Date;
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
/*     */ @NoAutoStart
/*     */ public class SizeAndTimeBasedFNATP<E>
/*     */   extends TimeBasedFileNamingAndTriggeringPolicyBase<E>
/*     */ {
/*     */   enum Usage
/*     */   {
/*  34 */     EMBEDDED, DIRECT;
/*     */   }
/*     */   
/*  37 */   int currentPeriodsCounter = 0;
/*     */   
/*     */   FileSize maxFileSize;
/*     */   
/*  41 */   long nextSizeCheck = 0L;
/*  42 */   static String MISSING_INT_TOKEN = "Missing integer token, that is %i, in FileNamePattern [";
/*  43 */   static String MISSING_DATE_TOKEN = "Missing date token, that is %d, in FileNamePattern [";
/*     */   private final Usage usage;
/*     */   InvocationGate invocationGate;
/*     */   
/*     */   public SizeAndTimeBasedFNATP() {
/*  48 */     this(Usage.DIRECT);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void start() {
/*  58 */     super.start();
/*     */     
/*  60 */     if (this.usage == Usage.DIRECT) {
/*  61 */       addWarn("SizeAndTimeBasedFNATP is deprecated. Use SizeAndTimeBasedRollingPolicy instead");
/*  62 */       addWarn("For more information see http://logback.qos.ch/manual/appenders.html#SizeAndTimeBasedRollingPolicy");
/*     */     } 
/*     */     
/*  65 */     if (!isErrorFree()) {
/*     */       return;
/*     */     }
/*     */     
/*  69 */     if (this.maxFileSize == null) {
/*  70 */       addError("maxFileSize property is mandatory.");
/*  71 */       withErrors();
/*     */     } 
/*     */     
/*  74 */     if (!validateDateAndIntegerTokens()) {
/*  75 */       withErrors();
/*     */       
/*     */       return;
/*     */     } 
/*  79 */     this.archiveRemover = createArchiveRemover();
/*  80 */     this.archiveRemover.setContext(this.context);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  85 */     String regex = this.tbrp.fileNamePattern.toRegexForFixedDate(this.dateInCurrentPeriod);
/*  86 */     String stemRegex = FileFilterUtil.afterLastSlash(regex);
/*     */     
/*  88 */     computeCurrentPeriodsHighestCounterValue(stemRegex);
/*     */     
/*  90 */     if (isErrorFree()) {
/*  91 */       this.started = true;
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean validateDateAndIntegerTokens() {
/*  96 */     boolean inError = false;
/*  97 */     if (this.tbrp.fileNamePattern.getIntegerTokenConverter() == null) {
/*  98 */       inError = true;
/*  99 */       addError(MISSING_INT_TOKEN + this.tbrp.fileNamePatternStr + "]");
/* 100 */       addError("See also http://logback.qos.ch/codes.html#sat_missing_integer_token");
/*     */     } 
/* 102 */     if (this.tbrp.fileNamePattern.getPrimaryDateTokenConverter() == null) {
/* 103 */       inError = true;
/* 104 */       addError(MISSING_DATE_TOKEN + this.tbrp.fileNamePatternStr + "]");
/*     */     } 
/*     */     
/* 107 */     return !inError;
/*     */   }
/*     */   
/*     */   protected ArchiveRemover createArchiveRemover() {
/* 111 */     return (ArchiveRemover)new SizeAndTimeBasedArchiveRemover(this.tbrp.fileNamePattern, this.rc);
/*     */   }
/*     */   
/*     */   void computeCurrentPeriodsHighestCounterValue(String stemRegex) {
/* 115 */     File file = new File(getCurrentPeriodsFileNameWithoutCompressionSuffix());
/* 116 */     File parentDir = file.getParentFile();
/*     */     
/* 118 */     File[] matchingFileArray = FileFilterUtil.filesInFolderMatchingStemRegex(parentDir, stemRegex);
/*     */     
/* 120 */     if (matchingFileArray == null || matchingFileArray.length == 0) {
/* 121 */       this.currentPeriodsCounter = 0;
/*     */       return;
/*     */     } 
/* 124 */     this.currentPeriodsCounter = FileFilterUtil.findHighestCounter(matchingFileArray, stemRegex);
/*     */ 
/*     */ 
/*     */     
/* 128 */     if (this.tbrp.getParentsRawFileProperty() != null || this.tbrp.compressionMode != CompressionMode.NONE)
/*     */     {
/* 130 */       this.currentPeriodsCounter++; } 
/*     */   }
/*     */   
/*     */   public SizeAndTimeBasedFNATP(Usage usage) {
/* 134 */     this.invocationGate = (InvocationGate)new DefaultInvocationGate();
/*     */     this.usage = usage;
/*     */   }
/*     */   
/*     */   public boolean isTriggeringEvent(File activeFile, E event) {
/* 139 */     long time = getCurrentTime();
/*     */ 
/*     */     
/* 142 */     if (time >= this.nextCheck) {
/* 143 */       Date dateInElapsedPeriod = this.dateInCurrentPeriod;
/* 144 */       this.elapsedPeriodsFileName = this.tbrp.fileNamePatternWithoutCompSuffix.convertMultipleArguments(new Object[] { dateInElapsedPeriod, Integer.valueOf(this.currentPeriodsCounter) });
/* 145 */       this.currentPeriodsCounter = 0;
/* 146 */       setDateInCurrentPeriod(time);
/* 147 */       computeNextCheck();
/* 148 */       return true;
/*     */     } 
/*     */ 
/*     */     
/* 152 */     if (this.invocationGate.isTooSoon(time)) {
/* 153 */       return false;
/*     */     }
/*     */     
/* 156 */     if (activeFile == null) {
/* 157 */       addWarn("activeFile == null");
/* 158 */       return false;
/*     */     } 
/* 160 */     if (this.maxFileSize == null) {
/* 161 */       addWarn("maxFileSize = null");
/* 162 */       return false;
/*     */     } 
/* 164 */     if (activeFile.length() >= this.maxFileSize.getSize()) {
/*     */       
/* 166 */       this.elapsedPeriodsFileName = this.tbrp.fileNamePatternWithoutCompSuffix.convertMultipleArguments(new Object[] { this.dateInCurrentPeriod, Integer.valueOf(this.currentPeriodsCounter) });
/* 167 */       this.currentPeriodsCounter++;
/* 168 */       return true;
/*     */     } 
/*     */     
/* 171 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCurrentPeriodsFileNameWithoutCompressionSuffix() {
/* 176 */     return this.tbrp.fileNamePatternWithoutCompSuffix.convertMultipleArguments(new Object[] { this.dateInCurrentPeriod, Integer.valueOf(this.currentPeriodsCounter) });
/*     */   }
/*     */   
/*     */   public void setMaxFileSize(FileSize aMaxFileSize) {
/* 180 */     this.maxFileSize = aMaxFileSize;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\rolling\SizeAndTimeBasedFNATP.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */