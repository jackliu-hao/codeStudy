package ch.qos.logback.core.rolling;

import ch.qos.logback.core.util.DefaultInvocationGate;
import ch.qos.logback.core.util.FileSize;
import ch.qos.logback.core.util.InvocationGate;
import java.io.File;

public class SizeBasedTriggeringPolicy<E> extends TriggeringPolicyBase<E> {
   public static final String SEE_SIZE_FORMAT = "http://logback.qos.ch/codes.html#sbtp_size_format";
   public static final long DEFAULT_MAX_FILE_SIZE = 10485760L;
   FileSize maxFileSize = new FileSize(10485760L);
   InvocationGate invocationGate = new DefaultInvocationGate();

   public boolean isTriggeringEvent(File activeFile, E event) {
      long now = System.currentTimeMillis();
      if (this.invocationGate.isTooSoon(now)) {
         return false;
      } else {
         return activeFile.length() >= this.maxFileSize.getSize();
      }
   }

   public void setMaxFileSize(FileSize aMaxFileSize) {
      this.maxFileSize = aMaxFileSize;
   }
}
