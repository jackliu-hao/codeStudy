/*     */ package ch.qos.logback.core.rolling.helper;
/*     */ 
/*     */ import ch.qos.logback.core.pattern.Converter;
/*     */ import ch.qos.logback.core.spi.ContextAwareBase;
/*     */ import ch.qos.logback.core.util.FileSize;
/*     */ import java.io.File;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.Date;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Future;
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
/*     */ public class TimeBasedArchiveRemover
/*     */   extends ContextAwareBase
/*     */   implements ArchiveRemover
/*     */ {
/*     */   protected static final long UNINITIALIZED = -1L;
/*     */   protected static final long INACTIVITY_TOLERANCE_IN_MILLIS = 2764800000L;
/*     */   static final int MAX_VALUE_FOR_INACTIVITY_PERIODS = 336;
/*     */   final FileNamePattern fileNamePattern;
/*     */   final RollingCalendar rc;
/*  40 */   private int maxHistory = 0;
/*  41 */   private long totalSizeCap = 0L;
/*     */   final boolean parentClean;
/*  43 */   long lastHeartBeat = -1L;
/*     */ 
/*     */   
/*     */   int callCount;
/*     */ 
/*     */ 
/*     */   
/*     */   public TimeBasedArchiveRemover(FileNamePattern fileNamePattern, RollingCalendar rc) {
/*  51 */     this.callCount = 0;
/*     */     this.fileNamePattern = fileNamePattern;
/*     */     this.rc = rc;
/*  54 */     this.parentClean = computeParentCleaningFlag(fileNamePattern); } public void clean(Date now) { long nowInMillis = now.getTime();
/*     */     
/*  56 */     int periodsElapsed = computeElapsedPeriodsSinceLastClean(nowInMillis);
/*  57 */     this.lastHeartBeat = nowInMillis;
/*  58 */     if (periodsElapsed > 1) {
/*  59 */       addInfo("Multiple periods, i.e. " + periodsElapsed + " periods, seem to have elapsed. This is expected at application start.");
/*     */     }
/*  61 */     for (int i = 0; i < periodsElapsed; i++) {
/*  62 */       int offset = getPeriodOffsetForDeletionTarget() - i;
/*  63 */       Date dateOfPeriodToClean = this.rc.getEndOfNextNthPeriod(now, offset);
/*  64 */       cleanPeriod(dateOfPeriodToClean);
/*     */     }  }
/*     */ 
/*     */   
/*     */   protected File[] getFilesInPeriod(Date dateOfPeriodToClean) {
/*  69 */     String filenameToDelete = this.fileNamePattern.convert(dateOfPeriodToClean);
/*  70 */     File file2Delete = new File(filenameToDelete);
/*     */     
/*  72 */     if (fileExistsAndIsFile(file2Delete)) {
/*  73 */       return new File[] { file2Delete };
/*     */     }
/*  75 */     return new File[0];
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean fileExistsAndIsFile(File file2Delete) {
/*  80 */     return (file2Delete.exists() && file2Delete.isFile());
/*     */   }
/*     */   
/*     */   public void cleanPeriod(Date dateOfPeriodToClean) {
/*  84 */     File[] matchingFileArray = getFilesInPeriod(dateOfPeriodToClean);
/*     */     
/*  86 */     for (File f : matchingFileArray) {
/*  87 */       addInfo("deleting " + f);
/*  88 */       f.delete();
/*     */     } 
/*     */     
/*  91 */     if (this.parentClean && matchingFileArray.length > 0) {
/*  92 */       File parentDir = getParentDir(matchingFileArray[0]);
/*  93 */       removeFolderIfEmpty(parentDir);
/*     */     } 
/*     */   }
/*     */   
/*     */   void capTotalSize(Date now) {
/*  98 */     long totalSize = 0L;
/*  99 */     long totalRemoved = 0L;
/* 100 */     for (int offset = 0; offset < this.maxHistory; offset++) {
/* 101 */       Date date = this.rc.getEndOfNextNthPeriod(now, -offset);
/* 102 */       File[] matchingFileArray = getFilesInPeriod(date);
/* 103 */       descendingSortByLastModified(matchingFileArray);
/* 104 */       for (File f : matchingFileArray) {
/* 105 */         long size = f.length();
/* 106 */         if (totalSize + size > this.totalSizeCap) {
/* 107 */           addInfo("Deleting [" + f + "] of size " + new FileSize(size));
/* 108 */           totalRemoved += size;
/* 109 */           f.delete();
/*     */         } 
/* 111 */         totalSize += size;
/*     */       } 
/*     */     } 
/* 114 */     addInfo("Removed  " + new FileSize(totalRemoved) + " of files");
/*     */   }
/*     */   
/*     */   private void descendingSortByLastModified(File[] matchingFileArray) {
/* 118 */     Arrays.sort(matchingFileArray, new Comparator<File>()
/*     */         {
/*     */           public int compare(File f1, File f2) {
/* 121 */             long l1 = f1.lastModified();
/* 122 */             long l2 = f2.lastModified();
/* 123 */             if (l1 == l2) {
/* 124 */               return 0;
/*     */             }
/* 126 */             if (l2 < l1) {
/* 127 */               return -1;
/*     */             }
/* 129 */             return 1;
/*     */           }
/*     */         });
/*     */   }
/*     */   
/*     */   File getParentDir(File file) {
/* 135 */     File absolute = file.getAbsoluteFile();
/* 136 */     File parentDir = absolute.getParentFile();
/* 137 */     return parentDir;
/*     */   }
/*     */   
/*     */   int computeElapsedPeriodsSinceLastClean(long nowInMillis) {
/* 141 */     long periodsElapsed = 0L;
/* 142 */     if (this.lastHeartBeat == -1L) {
/* 143 */       addInfo("first clean up after appender initialization");
/* 144 */       periodsElapsed = this.rc.periodBarriersCrossed(nowInMillis, nowInMillis + 2764800000L);
/* 145 */       periodsElapsed = Math.min(periodsElapsed, 336L);
/*     */     } else {
/* 147 */       periodsElapsed = this.rc.periodBarriersCrossed(this.lastHeartBeat, nowInMillis);
/*     */     } 
/*     */     
/* 150 */     return (int)periodsElapsed;
/*     */   }
/*     */   
/*     */   boolean computeParentCleaningFlag(FileNamePattern fileNamePattern) {
/* 154 */     DateTokenConverter<Object> dtc = fileNamePattern.getPrimaryDateTokenConverter();
/*     */     
/* 156 */     if (dtc.getDatePattern().indexOf('/') != -1) {
/* 157 */       return true;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 162 */     Converter<Object> p = fileNamePattern.headTokenConverter;
/*     */ 
/*     */     
/* 165 */     while (p != null && 
/* 166 */       !(p instanceof DateTokenConverter))
/*     */     {
/*     */       
/* 169 */       p = p.getNext();
/*     */     }
/*     */     
/* 172 */     while (p != null) {
/* 173 */       if (p instanceof ch.qos.logback.core.pattern.LiteralConverter) {
/* 174 */         String s = p.convert(null);
/* 175 */         if (s.indexOf('/') != -1) {
/* 176 */           return true;
/*     */         }
/*     */       } 
/* 179 */       p = p.getNext();
/*     */     } 
/*     */ 
/*     */     
/* 183 */     return false;
/*     */   }
/*     */   
/*     */   void removeFolderIfEmpty(File dir) {
/* 187 */     removeFolderIfEmpty(dir, 0);
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
/*     */   private void removeFolderIfEmpty(File dir, int depth) {
/* 200 */     if (depth >= 3) {
/*     */       return;
/*     */     }
/* 203 */     if (dir.isDirectory() && FileFilterUtil.isEmptyDirectory(dir)) {
/* 204 */       addInfo("deleting folder [" + dir + "]");
/* 205 */       dir.delete();
/* 206 */       removeFolderIfEmpty(dir.getParentFile(), depth + 1);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setMaxHistory(int maxHistory) {
/* 211 */     this.maxHistory = maxHistory;
/*     */   }
/*     */   
/*     */   protected int getPeriodOffsetForDeletionTarget() {
/* 215 */     return -this.maxHistory - 1;
/*     */   }
/*     */   
/*     */   public void setTotalSizeCap(long totalSizeCap) {
/* 219 */     this.totalSizeCap = totalSizeCap;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 223 */     return "c.q.l.core.rolling.helper.TimeBasedArchiveRemover";
/*     */   }
/*     */   
/*     */   public Future<?> cleanAsynchronously(Date now) {
/* 227 */     ArhiveRemoverRunnable runnable = new ArhiveRemoverRunnable(now);
/* 228 */     ExecutorService executorService = this.context.getScheduledExecutorService();
/* 229 */     Future<?> future = executorService.submit(runnable);
/* 230 */     return future;
/*     */   }
/*     */   
/*     */   public class ArhiveRemoverRunnable implements Runnable {
/*     */     Date now;
/*     */     
/*     */     ArhiveRemoverRunnable(Date now) {
/* 237 */       this.now = now;
/*     */     }
/*     */ 
/*     */     
/*     */     public void run() {
/* 242 */       TimeBasedArchiveRemover.this.clean(this.now);
/* 243 */       if (TimeBasedArchiveRemover.this.totalSizeCap != 0L && TimeBasedArchiveRemover.this.totalSizeCap > 0L)
/* 244 */         TimeBasedArchiveRemover.this.capTotalSize(this.now); 
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\rolling\helper\TimeBasedArchiveRemover.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */