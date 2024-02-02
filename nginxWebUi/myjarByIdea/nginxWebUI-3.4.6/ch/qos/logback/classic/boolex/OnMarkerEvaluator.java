package ch.qos.logback.classic.boolex;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluatorBase;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Marker;

public class OnMarkerEvaluator extends EventEvaluatorBase<ILoggingEvent> {
   List<String> markerList = new ArrayList();

   public void addMarker(String markerStr) {
      this.markerList.add(markerStr);
   }

   public boolean evaluate(ILoggingEvent event) throws NullPointerException, EvaluationException {
      Marker eventsMarker = event.getMarker();
      if (eventsMarker == null) {
         return false;
      } else {
         Iterator var3 = this.markerList.iterator();

         String markerStr;
         do {
            if (!var3.hasNext()) {
               return false;
            }

            markerStr = (String)var3.next();
         } while(!eventsMarker.contains(markerStr));

         return true;
      }
   }
}
