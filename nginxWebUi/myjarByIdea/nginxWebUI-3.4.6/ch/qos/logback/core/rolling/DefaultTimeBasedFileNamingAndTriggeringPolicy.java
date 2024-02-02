package ch.qos.logback.core.rolling;

import ch.qos.logback.core.joran.spi.NoAutoStart;
import ch.qos.logback.core.rolling.helper.TimeBasedArchiveRemover;
import java.io.File;
import java.util.Date;

@NoAutoStart
public class DefaultTimeBasedFileNamingAndTriggeringPolicy<E> extends TimeBasedFileNamingAndTriggeringPolicyBase<E> {
   public void start() {
      super.start();
      if (super.isErrorFree()) {
         if (this.tbrp.fileNamePattern.hasIntegerTokenCOnverter()) {
            this.addError("Filename pattern [" + this.tbrp.fileNamePattern + "] contains an integer token converter, i.e. %i, INCOMPATIBLE with this configuration. Remove it.");
         } else {
            this.archiveRemover = new TimeBasedArchiveRemover(this.tbrp.fileNamePattern, this.rc);
            this.archiveRemover.setContext(this.context);
            this.started = true;
         }
      }
   }

   public boolean isTriggeringEvent(File activeFile, E event) {
      long time = this.getCurrentTime();
      if (time >= this.nextCheck) {
         Date dateOfElapsedPeriod = this.dateInCurrentPeriod;
         this.addInfo("Elapsed period: " + dateOfElapsedPeriod);
         this.elapsedPeriodsFileName = this.tbrp.fileNamePatternWithoutCompSuffix.convert(dateOfElapsedPeriod);
         this.setDateInCurrentPeriod(time);
         this.computeNextCheck();
         return true;
      } else {
         return false;
      }
   }

   public String toString() {
      return "c.q.l.core.rolling.DefaultTimeBasedFileNamingAndTriggeringPolicy";
   }
}
