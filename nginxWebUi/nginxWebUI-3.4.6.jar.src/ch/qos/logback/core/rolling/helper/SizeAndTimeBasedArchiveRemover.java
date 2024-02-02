/*    */ package ch.qos.logback.core.rolling.helper;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.util.Date;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SizeAndTimeBasedArchiveRemover
/*    */   extends TimeBasedArchiveRemover
/*    */ {
/*    */   public SizeAndTimeBasedArchiveRemover(FileNamePattern fileNamePattern, RollingCalendar rc) {
/* 22 */     super(fileNamePattern, rc);
/*    */   }
/*    */   
/*    */   protected File[] getFilesInPeriod(Date dateOfPeriodToClean) {
/* 26 */     File archive0 = new File(this.fileNamePattern.convertMultipleArguments(new Object[] { dateOfPeriodToClean, Integer.valueOf(0) }));
/* 27 */     File parentDir = getParentDir(archive0);
/* 28 */     String stemRegex = createStemRegex(dateOfPeriodToClean);
/* 29 */     File[] matchingFileArray = FileFilterUtil.filesInFolderMatchingStemRegex(parentDir, stemRegex);
/* 30 */     return matchingFileArray;
/*    */   }
/*    */   
/*    */   private String createStemRegex(Date dateOfPeriodToClean) {
/* 34 */     String regex = this.fileNamePattern.toRegexForFixedDate(dateOfPeriodToClean);
/* 35 */     return FileFilterUtil.afterLastSlash(regex);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\rolling\helper\SizeAndTimeBasedArchiveRemover.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */