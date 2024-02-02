package ch.qos.logback.core.rolling;

import ch.qos.logback.core.rolling.helper.ArchiveRemover;
import ch.qos.logback.core.rolling.helper.CompressionMode;
import ch.qos.logback.core.rolling.helper.Compressor;
import ch.qos.logback.core.rolling.helper.FileFilterUtil;
import ch.qos.logback.core.rolling.helper.FileNamePattern;
import ch.qos.logback.core.rolling.helper.RenameUtil;
import ch.qos.logback.core.util.FileSize;
import java.io.File;
import java.util.Date;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TimeBasedRollingPolicy<E> extends RollingPolicyBase implements TriggeringPolicy<E> {
   static final String FNP_NOT_SET = "The FileNamePattern option must be set before using TimeBasedRollingPolicy. ";
   FileNamePattern fileNamePatternWithoutCompSuffix;
   private Compressor compressor;
   private RenameUtil renameUtil = new RenameUtil();
   Future<?> compressionFuture;
   Future<?> cleanUpFuture;
   private int maxHistory = 0;
   protected FileSize totalSizeCap = new FileSize(0L);
   private ArchiveRemover archiveRemover;
   TimeBasedFileNamingAndTriggeringPolicy<E> timeBasedFileNamingAndTriggeringPolicy;
   boolean cleanHistoryOnStart = false;

   public void start() {
      this.renameUtil.setContext(this.context);
      if (this.fileNamePatternStr != null) {
         this.fileNamePattern = new FileNamePattern(this.fileNamePatternStr, this.context);
         this.determineCompressionMode();
         this.compressor = new Compressor(this.compressionMode);
         this.compressor.setContext(this.context);
         this.fileNamePatternWithoutCompSuffix = new FileNamePattern(Compressor.computeFileNameStrWithoutCompSuffix(this.fileNamePatternStr, this.compressionMode), this.context);
         this.addInfo("Will use the pattern " + this.fileNamePatternWithoutCompSuffix + " for the active file");
         if (this.compressionMode == CompressionMode.ZIP) {
            String zipEntryFileNamePatternStr = this.transformFileNamePattern2ZipEntry(this.fileNamePatternStr);
            this.zipEntryFileNamePattern = new FileNamePattern(zipEntryFileNamePatternStr, this.context);
         }

         if (this.timeBasedFileNamingAndTriggeringPolicy == null) {
            this.timeBasedFileNamingAndTriggeringPolicy = new DefaultTimeBasedFileNamingAndTriggeringPolicy();
         }

         this.timeBasedFileNamingAndTriggeringPolicy.setContext(this.context);
         this.timeBasedFileNamingAndTriggeringPolicy.setTimeBasedRollingPolicy(this);
         this.timeBasedFileNamingAndTriggeringPolicy.start();
         if (!this.timeBasedFileNamingAndTriggeringPolicy.isStarted()) {
            this.addWarn("Subcomponent did not start. TimeBasedRollingPolicy will not start.");
         } else {
            if (this.maxHistory != 0) {
               this.archiveRemover = this.timeBasedFileNamingAndTriggeringPolicy.getArchiveRemover();
               this.archiveRemover.setMaxHistory(this.maxHistory);
               this.archiveRemover.setTotalSizeCap(this.totalSizeCap.getSize());
               if (this.cleanHistoryOnStart) {
                  this.addInfo("Cleaning on start up");
                  Date now = new Date(this.timeBasedFileNamingAndTriggeringPolicy.getCurrentTime());
                  this.cleanUpFuture = this.archiveRemover.cleanAsynchronously(now);
               }
            } else if (!this.isUnboundedTotalSizeCap()) {
               this.addWarn("'maxHistory' is not set, ignoring 'totalSizeCap' option with value [" + this.totalSizeCap + "]");
            }

            super.start();
         }
      } else {
         this.addWarn("The FileNamePattern option must be set before using TimeBasedRollingPolicy. ");
         this.addWarn("See also http://logback.qos.ch/codes.html#tbr_fnp_not_set");
         throw new IllegalStateException("The FileNamePattern option must be set before using TimeBasedRollingPolicy. See also http://logback.qos.ch/codes.html#tbr_fnp_not_set");
      }
   }

   protected boolean isUnboundedTotalSizeCap() {
      return this.totalSizeCap.getSize() == 0L;
   }

   public void stop() {
      if (this.isStarted()) {
         this.waitForAsynchronousJobToStop(this.compressionFuture, "compression");
         this.waitForAsynchronousJobToStop(this.cleanUpFuture, "clean-up");
         super.stop();
      }
   }

   private void waitForAsynchronousJobToStop(Future<?> aFuture, String jobDescription) {
      if (aFuture != null) {
         try {
            aFuture.get(30L, TimeUnit.SECONDS);
         } catch (TimeoutException var4) {
            this.addError("Timeout while waiting for " + jobDescription + " job to finish", var4);
         } catch (Exception var5) {
            this.addError("Unexpected exception while waiting for " + jobDescription + " job to finish", var5);
         }
      }

   }

   private String transformFileNamePattern2ZipEntry(String fileNamePatternStr) {
      String slashified = FileFilterUtil.slashify(fileNamePatternStr);
      return FileFilterUtil.afterLastSlash(slashified);
   }

   public void setTimeBasedFileNamingAndTriggeringPolicy(TimeBasedFileNamingAndTriggeringPolicy<E> timeBasedTriggering) {
      this.timeBasedFileNamingAndTriggeringPolicy = timeBasedTriggering;
   }

   public TimeBasedFileNamingAndTriggeringPolicy<E> getTimeBasedFileNamingAndTriggeringPolicy() {
      return this.timeBasedFileNamingAndTriggeringPolicy;
   }

   public void rollover() throws RolloverFailure {
      String elapsedPeriodsFileName = this.timeBasedFileNamingAndTriggeringPolicy.getElapsedPeriodsFileName();
      String elapsedPeriodStem = FileFilterUtil.afterLastSlash(elapsedPeriodsFileName);
      if (this.compressionMode == CompressionMode.NONE) {
         if (this.getParentsRawFileProperty() != null) {
            this.renameUtil.rename(this.getParentsRawFileProperty(), elapsedPeriodsFileName);
         }
      } else if (this.getParentsRawFileProperty() == null) {
         this.compressionFuture = this.compressor.asyncCompress(elapsedPeriodsFileName, elapsedPeriodsFileName, elapsedPeriodStem);
      } else {
         this.compressionFuture = this.renameRawAndAsyncCompress(elapsedPeriodsFileName, elapsedPeriodStem);
      }

      if (this.archiveRemover != null) {
         Date now = new Date(this.timeBasedFileNamingAndTriggeringPolicy.getCurrentTime());
         this.cleanUpFuture = this.archiveRemover.cleanAsynchronously(now);
      }

   }

   Future<?> renameRawAndAsyncCompress(String nameOfCompressedFile, String innerEntryName) throws RolloverFailure {
      String parentsRawFile = this.getParentsRawFileProperty();
      String tmpTarget = nameOfCompressedFile + System.nanoTime() + ".tmp";
      this.renameUtil.rename(parentsRawFile, tmpTarget);
      return this.compressor.asyncCompress(tmpTarget, nameOfCompressedFile, innerEntryName);
   }

   public String getActiveFileName() {
      String parentsRawFileProperty = this.getParentsRawFileProperty();
      return parentsRawFileProperty != null ? parentsRawFileProperty : this.timeBasedFileNamingAndTriggeringPolicy.getCurrentPeriodsFileNameWithoutCompressionSuffix();
   }

   public boolean isTriggeringEvent(File activeFile, E event) {
      return this.timeBasedFileNamingAndTriggeringPolicy.isTriggeringEvent(activeFile, event);
   }

   public int getMaxHistory() {
      return this.maxHistory;
   }

   public void setMaxHistory(int maxHistory) {
      this.maxHistory = maxHistory;
   }

   public boolean isCleanHistoryOnStart() {
      return this.cleanHistoryOnStart;
   }

   public void setCleanHistoryOnStart(boolean cleanHistoryOnStart) {
      this.cleanHistoryOnStart = cleanHistoryOnStart;
   }

   public String toString() {
      return "c.q.l.core.rolling.TimeBasedRollingPolicy@" + this.hashCode();
   }

   public void setTotalSizeCap(FileSize totalSizeCap) {
      this.addInfo("setting totalSizeCap to " + totalSizeCap.toString());
      this.totalSizeCap = totalSizeCap;
   }
}
