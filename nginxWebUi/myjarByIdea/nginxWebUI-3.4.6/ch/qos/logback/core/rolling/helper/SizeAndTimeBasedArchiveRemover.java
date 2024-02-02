package ch.qos.logback.core.rolling.helper;

import java.io.File;
import java.util.Date;

public class SizeAndTimeBasedArchiveRemover extends TimeBasedArchiveRemover {
   public SizeAndTimeBasedArchiveRemover(FileNamePattern fileNamePattern, RollingCalendar rc) {
      super(fileNamePattern, rc);
   }

   protected File[] getFilesInPeriod(Date dateOfPeriodToClean) {
      File archive0 = new File(this.fileNamePattern.convertMultipleArguments(dateOfPeriodToClean, 0));
      File parentDir = this.getParentDir(archive0);
      String stemRegex = this.createStemRegex(dateOfPeriodToClean);
      File[] matchingFileArray = FileFilterUtil.filesInFolderMatchingStemRegex(parentDir, stemRegex);
      return matchingFileArray;
   }

   private String createStemRegex(Date dateOfPeriodToClean) {
      String regex = this.fileNamePattern.toRegexForFixedDate(dateOfPeriodToClean);
      return FileFilterUtil.afterLastSlash(regex);
   }
}
