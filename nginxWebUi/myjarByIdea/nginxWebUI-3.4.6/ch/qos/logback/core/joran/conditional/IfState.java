package ch.qos.logback.core.joran.conditional;

import ch.qos.logback.core.joran.event.SaxEvent;
import java.util.List;

class IfState {
   Boolean boolResult;
   List<SaxEvent> thenSaxEventList;
   List<SaxEvent> elseSaxEventList;
   boolean active;
}
