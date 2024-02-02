package ch.qos.logback.classic.sift;

import ch.qos.logback.classic.ClassicConstants;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.joran.spi.DefaultClass;
import ch.qos.logback.core.sift.Discriminator;
import ch.qos.logback.core.sift.SiftingAppenderBase;
import org.slf4j.Marker;

public class SiftingAppender extends SiftingAppenderBase<ILoggingEvent> {
   protected long getTimestamp(ILoggingEvent event) {
      return event.getTimeStamp();
   }

   @DefaultClass(MDCBasedDiscriminator.class)
   public void setDiscriminator(Discriminator<ILoggingEvent> discriminator) {
      super.setDiscriminator(discriminator);
   }

   protected boolean eventMarksEndOfLife(ILoggingEvent event) {
      Marker marker = event.getMarker();
      return marker == null ? false : marker.contains(ClassicConstants.FINALIZE_SESSION_MARKER);
   }
}
