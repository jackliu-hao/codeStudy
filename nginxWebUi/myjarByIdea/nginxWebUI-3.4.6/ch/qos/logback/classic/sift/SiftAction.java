package ch.qos.logback.classic.sift;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.event.InPlayListener;
import ch.qos.logback.core.joran.event.SaxEvent;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.xml.sax.Attributes;

public class SiftAction extends Action implements InPlayListener {
   List<SaxEvent> seList;

   public void begin(InterpretationContext ic, String name, Attributes attributes) throws ActionException {
      this.seList = new ArrayList();
      ic.addInPlayListener(this);
   }

   public void end(InterpretationContext ic, String name) throws ActionException {
      ic.removeInPlayListener(this);
      Object o = ic.peekObject();
      if (o instanceof SiftingAppender) {
         SiftingAppender sa = (SiftingAppender)o;
         Map<String, String> propertyMap = ic.getCopyOfPropertyMap();
         AppenderFactoryUsingJoran appenderFactory = new AppenderFactoryUsingJoran(this.seList, sa.getDiscriminatorKey(), propertyMap);
         sa.setAppenderFactory(appenderFactory);
      }

   }

   public void inPlay(SaxEvent event) {
      this.seList.add(event);
   }

   public List<SaxEvent> getSeList() {
      return this.seList;
   }
}
