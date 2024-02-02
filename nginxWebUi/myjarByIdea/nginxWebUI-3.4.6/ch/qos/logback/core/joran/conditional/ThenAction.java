package ch.qos.logback.core.joran.conditional;

import ch.qos.logback.core.joran.event.SaxEvent;
import java.util.List;

public class ThenAction extends ThenOrElseActionBase {
   void registerEventList(IfAction ifAction, List<SaxEvent> eventList) {
      ifAction.setThenSaxEventList(eventList);
   }
}
