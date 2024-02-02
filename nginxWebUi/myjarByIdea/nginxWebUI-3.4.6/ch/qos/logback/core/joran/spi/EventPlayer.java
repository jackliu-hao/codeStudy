package ch.qos.logback.core.joran.spi;

import ch.qos.logback.core.joran.event.BodyEvent;
import ch.qos.logback.core.joran.event.EndEvent;
import ch.qos.logback.core.joran.event.SaxEvent;
import ch.qos.logback.core.joran.event.StartEvent;
import java.util.ArrayList;
import java.util.List;

public class EventPlayer {
   final Interpreter interpreter;
   List<SaxEvent> eventList;
   int currentIndex;

   public EventPlayer(Interpreter interpreter) {
      this.interpreter = interpreter;
   }

   public List<SaxEvent> getCopyOfPlayerEventList() {
      return new ArrayList(this.eventList);
   }

   public void play(List<SaxEvent> aSaxEventList) {
      this.eventList = aSaxEventList;

      for(this.currentIndex = 0; this.currentIndex < this.eventList.size(); ++this.currentIndex) {
         SaxEvent se = (SaxEvent)this.eventList.get(this.currentIndex);
         if (se instanceof StartEvent) {
            this.interpreter.startElement((StartEvent)se);
            this.interpreter.getInterpretationContext().fireInPlay(se);
         }

         if (se instanceof BodyEvent) {
            this.interpreter.getInterpretationContext().fireInPlay(se);
            this.interpreter.characters((BodyEvent)se);
         }

         if (se instanceof EndEvent) {
            this.interpreter.getInterpretationContext().fireInPlay(se);
            this.interpreter.endElement((EndEvent)se);
         }
      }

   }

   public void addEventsDynamically(List<SaxEvent> eventList, int offset) {
      this.eventList.addAll(this.currentIndex + offset, eventList);
   }
}
