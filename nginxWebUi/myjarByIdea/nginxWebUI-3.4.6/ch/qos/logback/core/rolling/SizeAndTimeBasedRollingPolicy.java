package ch.qos.logback.core.rolling;

import ch.qos.logback.core.util.FileSize;

public class SizeAndTimeBasedRollingPolicy<E> extends TimeBasedRollingPolicy<E> {
   FileSize maxFileSize;

   public void start() {
      SizeAndTimeBasedFNATP<E> sizeAndTimeBasedFNATP = new SizeAndTimeBasedFNATP(SizeAndTimeBasedFNATP.Usage.EMBEDDED);
      if (this.maxFileSize == null) {
         this.addError("maxFileSize property is mandatory.");
      } else {
         this.addInfo("Archive files will be limited to [" + this.maxFileSize + "] each.");
         sizeAndTimeBasedFNATP.setMaxFileSize(this.maxFileSize);
         this.timeBasedFileNamingAndTriggeringPolicy = sizeAndTimeBasedFNATP;
         if (!this.isUnboundedTotalSizeCap() && this.totalSizeCap.getSize() < this.maxFileSize.getSize()) {
            this.addError("totalSizeCap of [" + this.totalSizeCap + "] is smaller than maxFileSize [" + this.maxFileSize + "] which is non-sensical");
         } else {
            super.start();
         }
      }
   }

   public void setMaxFileSize(FileSize aMaxFileSize) {
      this.maxFileSize = aMaxFileSize;
   }

   public String toString() {
      return "c.q.l.core.rolling.SizeAndTimeBasedRollingPolicy@" + this.hashCode();
   }
}
