package ch.qos.logback.core.rolling;

import ch.qos.logback.core.rolling.helper.ArchiveRemover;
import ch.qos.logback.core.rolling.helper.DateTokenConverter;
import ch.qos.logback.core.rolling.helper.RollingCalendar;
import ch.qos.logback.core.spi.ContextAwareBase;
import java.io.File;
import java.util.Date;
import java.util.Locale;

public abstract class TimeBasedFileNamingAndTriggeringPolicyBase<E> extends ContextAwareBase implements TimeBasedFileNamingAndTriggeringPolicy<E> {
   private static String COLLIDING_DATE_FORMAT_URL = "http://logback.qos.ch/codes.html#rfa_collision_in_dateFormat";
   protected TimeBasedRollingPolicy<E> tbrp;
   protected ArchiveRemover archiveRemover = null;
   protected String elapsedPeriodsFileName;
   protected RollingCalendar rc;
   protected long artificialCurrentTime = -1L;
   protected Date dateInCurrentPeriod = null;
   protected long nextCheck;
   protected boolean started = false;
   protected boolean errorFree = true;

   public boolean isStarted() {
      return this.started;
   }

   public void start() {
      DateTokenConverter<Object> dtc = this.tbrp.fileNamePattern.getPrimaryDateTokenConverter();
      if (dtc == null) {
         throw new IllegalStateException("FileNamePattern [" + this.tbrp.fileNamePattern.getPattern() + "] does not contain a valid DateToken");
      } else {
         if (dtc.getTimeZone() != null) {
            this.rc = new RollingCalendar(dtc.getDatePattern(), dtc.getTimeZone(), Locale.getDefault());
         } else {
            this.rc = new RollingCalendar(dtc.getDatePattern());
         }

         this.addInfo("The date pattern is '" + dtc.getDatePattern() + "' from file name pattern '" + this.tbrp.fileNamePattern.getPattern() + "'.");
         this.rc.printPeriodicity(this);
         if (!this.rc.isCollisionFree()) {
            this.addError("The date format in FileNamePattern will result in collisions in the names of archived log files.");
            this.addError("For more information, please visit " + COLLIDING_DATE_FORMAT_URL);
            this.withErrors();
         } else {
            this.setDateInCurrentPeriod(new Date(this.getCurrentTime()));
            if (this.tbrp.getParentsRawFileProperty() != null) {
               File currentFile = new File(this.tbrp.getParentsRawFileProperty());
               if (currentFile.exists() && currentFile.canRead()) {
                  this.setDateInCurrentPeriod(new Date(currentFile.lastModified()));
               }
            }

            this.addInfo("Setting initial period to " + this.dateInCurrentPeriod);
            this.computeNextCheck();
         }
      }
   }

   public void stop() {
      this.started = false;
   }

   protected void computeNextCheck() {
      this.nextCheck = this.rc.getNextTriggeringDate(this.dateInCurrentPeriod).getTime();
   }

   protected void setDateInCurrentPeriod(long now) {
      this.dateInCurrentPeriod.setTime(now);
   }

   public void setDateInCurrentPeriod(Date _dateInCurrentPeriod) {
      this.dateInCurrentPeriod = _dateInCurrentPeriod;
   }

   public String getElapsedPeriodsFileName() {
      return this.elapsedPeriodsFileName;
   }

   public String getCurrentPeriodsFileNameWithoutCompressionSuffix() {
      return this.tbrp.fileNamePatternWithoutCompSuffix.convert(this.dateInCurrentPeriod);
   }

   public void setCurrentTime(long timeInMillis) {
      this.artificialCurrentTime = timeInMillis;
   }

   public long getCurrentTime() {
      return this.artificialCurrentTime >= 0L ? this.artificialCurrentTime : System.currentTimeMillis();
   }

   public void setTimeBasedRollingPolicy(TimeBasedRollingPolicy<E> _tbrp) {
      this.tbrp = _tbrp;
   }

   public ArchiveRemover getArchiveRemover() {
      return this.archiveRemover;
   }

   protected void withErrors() {
      this.errorFree = false;
   }

   protected boolean isErrorFree() {
      return this.errorFree;
   }
}
